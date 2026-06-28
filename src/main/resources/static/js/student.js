const user = requireRole('STUDENT', 'portal-login.html');
if (!user) throw new Error('no session');

const id = user.id;
document.getElementById('userLabel').textContent = user.name;
bindLogout();

const msg = document.getElementById('msg');
const base = '/student/' + id;

function scheduleRows(list) {
  return (list || []).map(s => [
    s.courseCode, s.courseTitle, s.sectionName, s.facultyName, s.units, fmtDays(s.days)
  ]);
}

function renderChecks(containerId, list, prefix) {
  const el = document.getElementById(containerId);
  if (!list || !list.length) {
    el.innerHTML = '<p style="padding:0.5rem;font-size:0.85rem;color:#666">None available</p>';
    return;
  }
  el.innerHTML = list.map(s => `
    <label><input type="checkbox" name="${prefix}" value="${s.id}">
      ${s.courseCode} - ${s.courseTitle} (${s.sectionName}) ${fmtDays(s.days)}
    </label>`).join('');
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
    ['Course', 'Section', 'Room', 'Days'],
    (d.schedulesToday || []).map(s => [s.courseTitle, s.sectionName, s.room, fmtDays(s.days)])
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
  const em = document.getElementById('enrollMsg');
  em.innerHTML = data.message ? `<div class="msg info">${data.message}</div>` : '';
  renderChecks('currentList', data.currentSchedules, 'current');
  renderChecks('failedList', data.failedCourseSchedules, 'failed');
  renderChecks('missedList', data.missedCourseSchedules, 'missed');
}

function checkedValues(name) {
  return [...document.querySelectorAll(`input[name="${name}"]:checked`)].map(x => Number(x.value));
}

document.getElementById('enrollBtn').onclick = async () => {
  try {
    hideMsg(msg);
    const body = {
      currentSchedulesIds: checkedValues('current'),
      failedCourseSchedulesIds: checkedValues('failed'),
      missedCourseSchedulesIds: checkedValues('missed')
    };
    const res = await api(base + '/enrollment', { method: 'PUT', body: JSON.stringify(body) });
    showMsg(msg, res.message || 'Enrollment submitted', res.message && res.message.toLowerCase().includes('succ') ? 'ok' : 'info');
    loadEnrollment();
    loadDashboard();
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
};

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
  profile: loadProfile
});

loadDashboard();
