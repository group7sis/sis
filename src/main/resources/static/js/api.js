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
    const msg = typeof data === 'object' ? JSON.stringify(data) : (data || res.statusText);
    throw new Error(msg);
  }
  return data;
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
  return days.map(d => `${d.name} ${d.startTime}-${d.endTime}`).join(', ');
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
