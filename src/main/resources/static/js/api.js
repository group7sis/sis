const API = '';

async function api(path, opts = {}) {
  const res = await fetch(API + path, {
    headers: { 'Content-Type': 'application/json', ...(opts.headers || {}) },
    ...opts
  });
  const text = await res.text();
  let data = null;
  if (text) {
    try { data = JSON.parse(text); } catch { data = text; }
  }
  if (!res.ok) {
    throw new Error(parseApiError(data, res.statusText));
  }
  return data;
}

function parseApiError(data, fallback) {
  if (!data) return fallback || 'Request failed';
  if (typeof data === 'string') {
    if (data.includes('"message"')) {
      try { return parseApiError(JSON.parse(data)); } catch { /* fall through */ }
    }
    return data.length > 180 ? data.slice(0, 180) + '…' : data;
  }
  if (data.message) return data.message;
  if (typeof data === 'object') {
    const vals = Object.values(data).filter(Boolean);
    if (vals.length === 1) return String(vals[0]);
    if (vals.length) return vals.slice(0, 3).join(' · ');
  }
  return fallback || 'Request failed';
}

function saveSession(user) {
  sessionStorage.setItem('sisUser', JSON.stringify(user));
}

function getSession() {
  const raw = sessionStorage.getItem('sisUser');
  return raw ? JSON.parse(raw) : null;
}

function clearSession() {
  sessionStorage.removeItem('sisUser');
}

function requireRole(role, redirect) {
  const user = getSession();
  if (!user || user.role !== role) {
    location.href = redirect;
    return null;
  }
  return user;
}

function showMsg(el, text, type = 'info') {
  if (!el) return;
  el.className = 'msg ' + type;
  el.textContent = text;
  el.classList.remove('hidden');
}

function hideMsg(el) {
  if (el) el.classList.add('hidden');
}

function tableHtml(headers, rows) {
  const head = headers.map(h => `<th>${h}</th>`).join('');
  const body = rows.length
    ? rows.map(r => `<tr>${r.map(c => `<td>${c ?? ''}</td>`).join('')}</tr>`).join('')
    : `<tr><td colspan="${headers.length}">No records</td></tr>`;
  return `<div class="table-wrap"><table><thead><tr>${head}</tr></thead><tbody>${body}</tbody></table></div>`;
}

function fmtName(p) {
  if (!p) return '';
  return [p.firstName, p.middleName, p.lastName, p.suffix].filter(Boolean).join(' ');
}

function fmtDays(days) {
  if (!days || !days.length) return '-';
  return days.map(d => `${d.name} ${d.timeStart || d.startTime || ''}-${d.timeEnd || d.endTime || ''}`).join(', ');
}

function bindNav(loaders) {
  document.querySelectorAll('[data-panel]').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = btn.dataset.panel;
      document.querySelectorAll('[data-panel]').forEach(b => b.classList.toggle('active', b === btn));
      document.querySelectorAll('.panel').forEach(p => p.classList.toggle('active', p.id === 'panel-' + id));
      if (loaders[id]) loaders[id]();
    });
  });
}

function bindLogout() {
  document.querySelectorAll('.logout').forEach(el => {
    el.addEventListener('click', e => {
      e.preventDefault();
      clearSession();
      location.href = 'index.html';
    });
  });
}

function buildQuery(params) {
  const parts = [];
  Object.entries(params || {}).forEach(([k, v]) => {
    if (v !== '' && v != null) parts.push(encodeURIComponent(k) + '=' + encodeURIComponent(v));
  });
  return parts.length ? '?' + parts.join('&') : '';
}

function filterValues(formId) {
  const form = document.getElementById(formId);
  if (!form) return {};
  const o = {};
  new FormData(form).forEach((v, k) => { if (v !== '') o[k] = v; });
  return o;
}

function bindFilter(formId, loadFn) {
  const form = document.getElementById(formId);
  if (!form) return;
  form.addEventListener('submit', e => { e.preventDefault(); loadFn(); });
  form.querySelector('[data-reset]')?.addEventListener('click', () => {
    form.reset();
    loadFn();
  });
}

const DAY_NAMES = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

function dayOptions(selected) {
  return DAY_NAMES.map(d => `<option value="${d}"${d === selected ? ' selected' : ''}>${d}</option>`).join('');
}

function assignmentRowHtml(data = {}) {
  const idField = data.id ? `<input type="hidden" name="id" value="${data.id}">` : '';
  return `<div class="repeater-row grid-4" data-repeater="assignment">
    ${idField}
    <div><label>Program ID</label><input name="programId" type="number" value="${data.programId ?? ''}" placeholder="optional"></div>
    <div><label>Curriculum ID</label><input name="curriculumId" type="number" value="${data.curriculumId ?? ''}" required></div>
    <div><label>Year Level</label><input name="yearLevel" type="number" value="${data.yearLevel ?? 1}" required></div>
    <div><label>Semester</label><input name="semester" type="number" value="${data.semester ?? 1}" required></div>
    <div style="grid-column:1/-1" class="row"><button type="button" class="btn btn-sm danger" data-remove-row>Remove</button></div>
  </div>`;
}

function dayRowHtml(data = {}) {
  const start = data.startTime ? String(data.startTime).slice(0, 5) : '';
  const end = data.endTime ? String(data.endTime).slice(0, 5) : '';
  return `<div class="repeater-row grid-4" data-repeater="day">
    <div><label>Day</label><select name="name" required>${dayOptions(data.name)}</select></div>
    <div><label>Start Time</label><input name="startTime" type="time" value="${start}" required></div>
    <div><label>End Time</label><input name="endTime" type="time" value="${end}" required></div>
    <div style="align-self:end"><button type="button" class="btn btn-sm danger" data-remove-row>Remove</button></div>
  </div>`;
}

function initRepeater(containerId, rowHtml, count = 1) {
  const el = document.getElementById(containerId);
  if (!el) return;
  el.innerHTML = '';
  for (let i = 0; i < count; i++) el.insertAdjacentHTML('beforeend', rowHtml());
}

function collectRepeater(containerId) {
  const rows = [];
  document.querySelectorAll('#' + containerId + ' [data-repeater]').forEach(row => {
    const item = {};
    row.querySelectorAll('input, select').forEach(input => {
      if (!input.name) return;
      let v = input.value.trim();
      if (v === '') return;
      if (input.type === 'number' || input.name === 'id') v = Number(v);
      item[input.name] = v;
    });
    if (Object.keys(item).length) rows.push(item);
  });
  return rows;
}

function parseIdList(value) {
  return (value || '').split(',').map(s => s.trim()).filter(Boolean).map(Number);
}

document.body.addEventListener('click', e => {
  const addBtn = e.target.closest('[data-add-row]');
  if (addBtn) {
    const container = document.getElementById(addBtn.dataset.addRow);
    if (!container) return;
    const kind = addBtn.dataset.rowKind;
    container.insertAdjacentHTML('beforeend', kind === 'day' ? dayRowHtml() : assignmentRowHtml());
    return;
  }
  const removeBtn = e.target.closest('[data-remove-row]');
  if (removeBtn) {
    const row = removeBtn.closest('[data-repeater]');
    const wrap = row?.parentElement;
    if (row && wrap && wrap.querySelectorAll('[data-repeater]').length > 1) row.remove();
  }
});

function buildProfileFromForm(f) {
  return {
    firstName: f.firstName.value,
    middleName: f.middleName.value || null,
    lastName: f.lastName.value,
    suffix: f.suffix.value || null,
    sex: f.sex.value,
    dateOfBirth: f.dateOfBirth.value,
    civilStatus: f.civilStatus.value,
    nationality: f.nationality.value,
    mobileNumber: f.mobileNumber.value
  };
}

function buildAccountFromForm(f, role) {
  return {
    username: f.username.value,
    password: f.password.value,
    email: f.email.value,
    role,
    status: f.accountStatus?.value || 'ACTIVE'
  };
}

function collectAssignments(containerId) {
  return collectRepeater(containerId).map(a => {
    const item = {
      programId: a.programId || undefined,
      curriculumId: a.curriculumId,
      yearLevel: a.yearLevel,
      semester: a.semester
    };
    if (a.id) item.id = a.id;
    return item;
  });
}

function collectDays(containerId) {
  return collectRepeater(containerId).map(d => ({
    name: d.name,
    startTime: d.startTime ? String(d.startTime).slice(0, 5) : '',
    endTime: d.endTime ? String(d.endTime).slice(0, 5) : ''
  }));
}
