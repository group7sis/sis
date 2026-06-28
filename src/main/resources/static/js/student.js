const user = requireRole('STUDENT', 'portal-login.html');
if (!user) throw new Error('no session');

const id = user.id;
document.getElementById('userLabel').textContent = user.name;
bindLogout();

const msg = document.getElementById('msg');
const base = '/student/' + id;
let enrollmentLocked = false;

function scheduleRows(list) {
  return (list || []).map(s => [
    s.courseCode, s.courseTitle, s.sectionName, s.facultyName, s.units, fmtDays(s.days)
  ]);
}

function renderChecks(containerId, list, prefix, disabled) {
  const el = document.getElementById(containerId);
  if (!list || !list.length) {
    el.innerHTML = '<p style="padding:0.5rem;font-size:0.85rem;color:#666">None available</p>';
    return;
  }
  el.innerHTML = list.map(s => `
    <label><input type="checkbox" name="${prefix}" value="${s.id}"${disabled ? ' disabled' : ''}>
      ${s.courseCode} - ${s.courseTitle} (${s.sectionName}) ${fmtDays(s.days)}
    </label>`).join('');
}

function setEnrollmentUi(closed, message) {
  enrollmentLocked = closed;
  const btn = document.getElementById('enrollBtn');
  const lists = ['currentList', 'failedList', 'missedList'];
  btn.disabled = closed;
  if (closed) {
    lists.forEach(id => {
      document.querySelectorAll(`#${id} input[type="checkbox"]`).forEach(cb => { cb.disabled = true; });
    });
  }
  const em = document.getElementById('enrollMsg');
  if (message) {
    em.innerHTML = `<div class="msg ${closed ? 'info' : 'ok'}">${message}</div>`;
  } else if (closed) {
    em.innerHTML = '<div class="msg info">Enrollment is closed for this term.</div>';
  }
}

function updateEnrollButton() {
  if (enrollmentLocked) return;
  const total = checkedValues('current').length + checkedValues('failed').length + checkedValues('missed').length;
  document.getElementById('enrollBtn').disabled = total === 0;
}

async function loadDashboard() {
  const d = await api(base + '/dashboard');
  document.getElementById('dashContent').innerHTML = `
    <div class="stats">
      ${stat('GWA', d.gwa ?? '-')}
      ${stat('Program', d.programCode || '-')}
      ${stat('Section', d.sectionName || '-')}
      ${stat('Courses', d.totalEnrolledCourses ?? '-')}
      ${stat('Units', d.totalUnits ?? '-')}
      ${stat('Grades', d.gradeCompletionStatus || '-')}
    </div>
    <p style="font-size:0.9rem;margin-bottom:0.5rem">Today: ${d.currentDayOfTheWeek || ''}, ${d.currentMonth || ''} ${d.currentDay || ''}, ${d.currentYear || ''}</p>
    <div id="todayTable"></div>`;
  function stat(label, value) {
    return `<div class="stat"><strong>${value}</strong><span>${label}</span></div>`;
  }
  document.getElementById('todayTable').innerHTML = tableHtml(
    ['Course', 'Section', 'Days'],
    (d.schedulesToday || []).map(s => [s.courseTitle, s.sectionName, fmtDays(s.days)])
  );
}

async function loadSchedule() {
  const list = await api(base + '/schedule');
  document.getElementById('scheduleTable').innerHTML = tableHtml(
    ['Code', 'Title', 'Section', 'Faculty', 'Units', 'Days'], scheduleRows(list));
}

async function loadGrades() {
  const g = await api(base + '/grades');
  document.getElementById('gradesContent').innerHTML =
    `<p style="margin-bottom:0.5rem"><strong>GWA:</strong> ${g.gwa ?? '-'}</p>` +
    tableHtml(['Course', 'Faculty', 'Grade', 'Status'],
      (g.enrolledCourses || []).map(c => [c.courseTitle || c.courseId, c.facultyName, c.grade ?? '-', c.status]));
}

async function loadEnrollment() {
  const data = await api(base + '/enrollment');
  const closed = !!data.message && (
    data.message.toLowerCase().includes('closed') ||
    data.message.toLowerCase().includes('already submitted') ||
    data.message.toLowerCase().includes('unavailable') ||
    data.message.toLowerCase().includes('dropped') ||
    data.message.toLowerCase().includes('graduated')
  );
  renderChecks('currentList', data.currentSchedules, 'current', closed);
  renderChecks('failedList', data.failedCourseSchedules, 'failed', closed);
  renderChecks('missedList', data.missedCourseSchedules, 'missed', closed);
  setEnrollmentUi(closed, data.message);
  if (!closed) updateEnrollButton();
}

function checkedValues(name) {
  return [...document.querySelectorAll(`input[name="${name}"]:checked`)].map(x => Number(x.value));
}

document.getElementById('enrollmentPanel').addEventListener('change', e => {
  if (e.target.matches('input[type="checkbox"]')) updateEnrollButton();
});

document.getElementById('enrollBtn').onclick = async () => {
  if (enrollmentLocked) return;
  const total = checkedValues('current').length + checkedValues('failed').length + checkedValues('missed').length;
  if (total === 0) {
    showMsg(msg, 'Select at least one course', 'err');
    return;
  }
  try {
    hideMsg(msg);
    const body = {
      currentSchedulesIds: checkedValues('current'),
      failedCourseSchedulesIds: checkedValues('failed'),
      missedCourseSchedulesIds: checkedValues('missed')
    };
    const res = await api(base + '/enrollment', { method: 'PUT', body: JSON.stringify(body) });
    const ok = res.message && res.message.toLowerCase().includes('succ');
    showMsg(msg, res.message || 'Enrollment submitted', ok ? 'ok' : 'info');
    if (ok) {
      setEnrollmentUi(true, 'Enrollment submitted. Enrollment is now closed for this term.');
    }
    loadEnrollment();
    loadDashboard();
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
};

async function loadEnrollmentHistory() {
  const list = await api(base + '/enrollment-history');
  if (!list || !list.length) {
    document.getElementById('historyContent').innerHTML = '<p>No enrollment records.</p>';
    return;
  }
  let rows = [];
  list.forEach(info => {
    rows.push([
      info.academicYear, info.semester, info.yearLevel,
      info.enrollmentStatus, info.scholasticStatus, (info.enrolledCourses || []).length
    ]);
    (info.enrolledCourses || []).forEach(ec => {
      rows.push([
        `<span style="padding-left:1rem">${ec.courseCode || ec.courseTitle || 'Course'}</span>`,
        ec.sectionName || '-', ec.facultyName || '-', ec.grade ?? '-', ec.status, ''
      ]);
    });
  });
  document.getElementById('historyContent').innerHTML = tableHtml(
    ['Term / Course', 'Section', 'Faculty', 'Grade', 'Status', ''],
    rows
  );
}

async function loadProfile() {
  const p = await api(base + '/profile');
  const form = document.getElementById('profileForm');
  ['firstName', 'middleName', 'lastName', 'suffix', 'mobileNumber', 'enrolledProgram', 'yearLevel']
    .forEach(k => { if (form[k]) form[k].value = p[k] ?? ''; });
}

document.getElementById('profileForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    const form = e.target;
    const body = {
      firstName: form.firstName.value,
      middleName: form.middleName.value || null,
      lastName: form.lastName.value,
      suffix: form.suffix.value || null,
      mobileNumber: form.mobileNumber.value || null
    };
    await api(base + '/profile', { method: 'PUT', body: JSON.stringify(body) });
    showMsg(msg, 'Profile updated', 'ok');
    loadProfile();
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
};

bindNav({
  dashboard: loadDashboard,
  schedule: loadSchedule,
  grades: loadGrades,
  enrollment: loadEnrollment,
  history: loadEnrollmentHistory,
  profile: loadProfile
});

loadDashboard();
