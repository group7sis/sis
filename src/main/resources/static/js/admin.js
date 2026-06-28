const user = requireRole('ADMIN', 'admin-login.html');
if (!user) throw new Error('no session');

document.getElementById('userLabel').textContent = user.name;
bindLogout();

const msg = document.getElementById('msg');

function statBox(label, value) {
  return `<div class="stat"><strong>${value ?? 0}</strong><span>${label}</span></div>`;
}

async function loadDashboard() {
  const d = await api('/admin/dashboard');
  document.getElementById('dashStats').innerHTML = Object.entries(d)
    .map(([k, v]) => statBox(k.replace(/([A-Z])/g, ' $1'), v)).join('');
}

async function loadStudents() {
  const name = document.getElementById('studentSearch').value.trim();
  const q = name ? '?name=' + encodeURIComponent(name) : '';
  const data = await api('/admin/students' + q);
  const s = data.stats || {};
  document.getElementById('studentStats').innerHTML =
    statBox('Total', s.totalStudents) + statBox('Active', s.totalActiveStudents) +
    statBox('Irregular', s.totalIrregularStudents) + statBox('Graduated', s.totalGraduatedStudents);
  const rows = (data.students || []).map(st => [
    st.studentNumber,
    fmtName(st.profile),
    st.studentStatus,
    st.scholasticStatus,
    `<button class="btn btn-sm" data-id="${st.id}" data-kind="student">View</button>`
  ]);
  document.getElementById('studentTable').innerHTML = tableHtml(
    ['Number', 'Name', 'Status', 'Scholastic', ''], rows);
}

async function loadFaculty() {
  const name = document.getElementById('facultySearch').value.trim();
  const q = name ? '?name=' + encodeURIComponent(name) : '';
  const data = await api('/admin/faculty' + q);
  const s = data.stats || {};
  document.getElementById('facultyStats').innerHTML =
    statBox('Total', s.totalFaculty) + statBox('Active', s.totalActiveFaculty) +
    statBox('Inactive', s.totalInactiveFaculty);
  const rows = (data.faculties || []).map(f => [
    f.facultyNumber,
    fmtName(f.profile),
    f.employmentType,
    f.status,
    `<button class="btn btn-sm" data-id="${f.id}" data-kind="faculty">View</button>`
  ]);
  document.getElementById('facultyTable').innerHTML = tableHtml(
    ['Number', 'Name', 'Type', 'Status', ''], rows);
}

async function loadCourses() {
  const data = await api('/admin/courses');
  const s = data.stats || {};
  document.getElementById('courseStats').innerHTML =
    statBox('Total', s.totalCourses) + statBox('Active', s.totalActiveCourses) + statBox('Inactive', s.totalInactiveCourses);
  const rows = (data.courses || []).map(c => [c.code, c.title, c.units, c.status]);
  document.getElementById('courseTable').innerHTML = tableHtml(['Code', 'Title', 'Units', 'Status'], rows);
}

async function loadSchedules() {
  const data = await api('/admin/schedules');
  const s = data.stats || {};
  document.getElementById('scheduleStats').innerHTML =
    statBox('Total', s.totalSchedules) + statBox('Active', s.totalActiveSchedules) +
    statBox('Weekly Hrs', s.weeklyHours);
  const rows = (data.schedules || []).map(sc => [
    sc.code, sc.courseTitle, sc.sectionName, sc.academicYear, sc.room, sc.status
  ]);
  document.getElementById('scheduleTable').innerHTML = tableHtml(
    ['Code', 'Course', 'Section', 'Year', 'Room', 'Status'], rows);
}

async function loadSections() {
  const data = await api('/admin/sections');
  const s = data.stats || {};
  document.getElementById('sectionStats').innerHTML =
    statBox('Total', s.totalSections) + statBox('Active', s.totalActiveSections);
  const rows = (data.sections || []).map(x => [x.code, x.name, x.programId, x.yearLevel, x.maxCapacity, x.status]);
  document.getElementById('sectionTable').innerHTML = tableHtml(
    ['Code', 'Name', 'Program', 'Year', 'Capacity', 'Status'], rows);
}

async function loadPrograms() {
  const data = await api('/admin/programs');
  const rows = (data || []).map(p => [p.code || p.id, p.title, p.status]);
  document.getElementById('programTable').innerHTML = tableHtml(['Code', 'Title', 'Status'], rows);
}

async function loadDepartments() {
  const data = await api('/admin/departments');
  const rows = (data || []).map(d => [d.code, d.name]);
  document.getElementById('departmentTable').innerHTML = tableHtml(['Code', 'Name'], rows);
}

async function loadTerms() {
  const data = await api('/admin/terms');
  const rows = (data || []).map(t => [t.academicYear, t.semester, t.status]);
  document.getElementById('termTable').innerHTML = tableHtml(['Academic Year', 'Semester', 'Status'], rows);
}

async function loadAccounts() {
  const data = await api('/admin/accounts');
  const rows = (data || []).map(a => [a.username]);
  document.getElementById('accountTable').innerHTML = tableHtml(['Username'], rows);
}

async function jsonAdd(path, textareaId, reloadFn) {
  try {
    hideMsg(msg);
    const body = JSON.parse(document.getElementById(textareaId).value);
    await api(path, { method: 'POST', body: JSON.stringify(body) });
    showMsg(msg, 'Saved successfully', 'ok');
    reloadFn();
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
}

function formToObj(form) {
  const fd = new FormData(form);
  const obj = {};
  fd.forEach((v, k) => {
    if (v === '') return;
    obj[k] = /^\d+$/.test(v) ? Number(v) : v;
  });
  return obj;
}

document.getElementById('studentSearchBtn').onclick = loadStudents;
document.getElementById('facultySearchBtn').onclick = loadFaculty;
document.getElementById('studentAddBtn').onclick = () => jsonAdd('/admin/students/add', 'studentJson', loadStudents);
document.getElementById('facultyAddBtn').onclick = () => jsonAdd('/admin/faculty/add', 'facultyJson', loadFaculty);
document.getElementById('courseAddBtn').onclick = () => jsonAdd('/admin/courses/add', 'courseJson', loadCourses);
document.getElementById('scheduleAddBtn').onclick = () => jsonAdd('/admin/schedules/add', 'scheduleJson', loadSchedules);
document.getElementById('programAddBtn').onclick = () => jsonAdd('/admin/programs/add', 'programJson', loadPrograms);

document.getElementById('sectionForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    await api('/admin/sections/add', { method: 'POST', body: JSON.stringify(formToObj(e.target)) });
    showMsg(msg, 'Section added', 'ok');
    e.target.reset();
    loadSections();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('deptForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    await api('/admin/departments/add', { method: 'POST', body: JSON.stringify(formToObj(e.target)) });
    showMsg(msg, 'Department added', 'ok');
    e.target.reset();
    loadDepartments();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('termForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    await api('/admin/terms/add', { method: 'POST', body: JSON.stringify(formToObj(e.target)) });
    showMsg(msg, 'Term added', 'ok');
    e.target.reset();
    loadTerms();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('accountForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    await api('/admin/accounts/add', { method: 'POST', body: JSON.stringify(formToObj(e.target)) });
    showMsg(msg, 'Admin added', 'ok');
    e.target.reset();
    loadAccounts();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.body.addEventListener('click', async e => {
  const btn = e.target.closest('button[data-kind]');
  if (!btn) return;
  const id = btn.dataset.id;
  const detail = btn.dataset.kind === 'student' ? document.getElementById('studentDetail') : document.getElementById('facultyDetail');
  const data = await api('/admin/' + (btn.dataset.kind === 'student' ? 'students' : 'faculty') + '/' + id);
  detail.textContent = JSON.stringify(data, null, 2);
  detail.classList.remove('hidden');
});

bindNav({
  dashboard: loadDashboard,
  students: loadStudents,
  faculty: loadFaculty,
  courses: loadCourses,
  schedules: loadSchedules,
  sections: loadSections,
  programs: loadPrograms,
  departments: loadDepartments,
  terms: loadTerms,
  accounts: loadAccounts
});

loadDashboard();
