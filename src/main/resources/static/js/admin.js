const user = requireRole('ADMIN', 'admin-login.html');
if (!user) throw new Error('no session');

const TERM_ID = 1;
let currentTerm = null;
let managedProgram = null;

document.getElementById('userLabel').textContent = user.name;
bindLogout();

const msg = document.getElementById('msg');

function statBox(label, value) {
  return `<div class="stat"><strong>${value ?? 0}</strong><span>${label}</span></div>`;
}

function termBannerHtml(t) {
  if (!t) return '';
  return `<strong>Current Term:</strong> ${t.academicYear} · Semester ${t.semester} · ${t.status}`;
}

async function loadCurrentTerm() {
  currentTerm = await api('/admin/terms/' + TERM_ID);
  applyTermToForms();
  return currentTerm;
}

function applyTermToForms() {
  if (!currentTerm) return;
  const map = {
    studentForm: { yearAdmitted: currentTerm.academicYear, academicYear: currentTerm.academicYear, semester: currentTerm.semester },
    scheduleForm: { academicYear: currentTerm.academicYear, semester: currentTerm.semester }
  };
  Object.entries(map).forEach(([formId, fields]) => {
    const form = document.getElementById(formId);
    if (!form) return;
    Object.entries(fields).forEach(([k, v]) => {
      if (form[k]) form[k].value = v;
    });
  });
  const schedFilter = document.getElementById('scheduleFilters');
  if (schedFilter && schedFilter.academicYear && !schedFilter.academicYear.value) {
    schedFilter.academicYear.placeholder = currentTerm.academicYear;
  }
}

async function loadDashboard() {
  await loadCurrentTerm();
  const d = await api('/admin/dashboard');
  document.getElementById('dashTerm').innerHTML = termBannerHtml(currentTerm);
  document.getElementById('dashStats').innerHTML = Object.entries(d)
    .map(([k, v]) => statBox(k.replace(/([A-Z])/g, ' $1'), v)).join('');
}

async function loadStudents() {
  const q = buildQuery(filterValues('studentFilters'));
  const data = await api('/admin/students' + q);
  const s = data.stats || {};
  document.getElementById('studentStats').innerHTML =
    statBox('Total', s.totalStudents) + statBox('Active', s.totalActiveStudents) +
    statBox('Irregular', s.totalIrregularStudents) + statBox('Graduated', s.totalGraduatedStudents);
  document.getElementById('studentTable').innerHTML = tableHtml(
    ['Number', 'Name', 'Status', 'Scholastic', ''],
    (data.students || []).map(st => [
      st.studentNumber, fmtName(st.profile), st.studentStatus, st.scholasticStatus,
      `<button class="btn btn-sm" data-id="${st.id}" data-kind="student">View</button>`
    ]));
}

async function loadFaculty() {
  const q = buildQuery(filterValues('facultyFilters'));
  const data = await api('/admin/faculty' + q);
  const s = data.stats || {};
  document.getElementById('facultyStats').innerHTML =
    statBox('Total', s.totalFaculty) + statBox('Active', s.totalActiveFaculty) +
    statBox('Inactive', s.totalInactiveFaculty);
  document.getElementById('facultyTable').innerHTML = tableHtml(
    ['Number', 'Name', 'Type', 'Status', ''],
    (data.faculties || []).map(f => [
      f.facultyNumber, fmtName(f.profile), f.employmentType, f.status,
      `<button class="btn btn-sm" data-id="${f.id}" data-kind="faculty">View</button>`
    ]));
}

async function loadCourses() {
  const q = buildQuery(filterValues('courseFilters'));
  const data = await api('/admin/courses' + q);
  const s = data.stats || {};
  document.getElementById('courseStats').innerHTML =
    statBox('Total', s.totalCourses) + statBox('Active', s.totalActiveCourses) +
    statBox('Inactive', s.totalInactiveCourses);
  document.getElementById('courseTable').innerHTML = tableHtml(
    ['Code', 'Title', 'Units', 'Status'],
    (data.courses || []).map(c => [c.code, c.title, c.units, c.status]));
}

async function loadSchedules() {
  const q = buildQuery(filterValues('scheduleFilters'));
  const data = await api('/admin/schedules' + q);
  const s = data.stats || {};
  document.getElementById('scheduleStats').innerHTML =
    statBox('Total', s.totalSchedules) + statBox('Active', s.totalActiveSchedules) +
    statBox('Weekly Hrs', s.weeklyHours);
  document.getElementById('scheduleTable').innerHTML = tableHtml(
    ['Code', 'Course', 'Section', 'Year', 'Sem', 'Room', 'Status'],
    (data.schedules || []).map(sc => [
      sc.code, sc.courseTitle, sc.sectionName, sc.academicYear, sc.semester ?? '-', sc.room, sc.status
    ]));
}

async function loadSections() {
  const q = buildQuery(filterValues('sectionFilters'));
  const data = await api('/admin/sections' + q);
  const s = data.stats || {};
  document.getElementById('sectionStats').innerHTML =
    statBox('Total', s.totalSections) + statBox('Active', s.totalActiveSections);
  document.getElementById('sectionTable').innerHTML = tableHtml(
    ['Code', 'Name', 'Program', 'Year', 'Capacity', 'Status'],
    (data.sections || []).map(x => [x.code, x.name, x.programId, x.yearLevel, x.maxCapacity, x.status]));
}

async function loadPrograms() {
  const q = buildQuery(filterValues('programFilters'));
  const data = await api('/admin/programs' + q);
  document.getElementById('programTable').innerHTML = tableHtml(
    ['Code', 'Title', 'Status', 'Active Curriculum', ''],
    (data || []).map(p => [
      p.code || p.id, p.title, p.status, p.activeCurriculumId ?? '-',
      `<button class="btn btn-sm" data-program="${p.id}" data-title="${p.title || ''}">Manage</button>`
    ]));
}

async function loadDepartments() {
  const q = buildQuery(filterValues('deptFilters'));
  const data = await api('/admin/departments' + q);
  document.getElementById('departmentTable').innerHTML = tableHtml(
    ['Code', 'Name'], (data || []).map(d => [d.code, d.name]));
}

async function loadTermPanel() {
  const t = await loadCurrentTerm();
  const form = document.getElementById('termForm');
  form.academicYear.value = t.academicYear;
  form.semester.value = t.semester;
  form.status.value = t.status;
}

async function loadAccounts() {
  const data = await api('/admin/accounts');
  document.getElementById('accountTable').innerHTML = tableHtml(
    ['Username'], (data || []).map(a => [a.username]));
}

function enrollmentHistoryHtml(student) {
  const infos = student.academicInfo || [];
  if (!infos.length) {
    return '<div class="history-block"><div class="section-title">Enrollment History</div><p>No enrollment records.</p></div>';
  }
  let body = '';
  infos.forEach(info => {
    body += `<tr>
      <td>${info.academicYear}</td><td>${info.semester}</td><td>${info.yearLevel}</td>
      <td>${info.enrollmentStatus}</td><td>${info.scholasticStatus}</td>
      <td>${(info.enrolledCourses || []).length}</td>
    </tr>`;
    (info.enrolledCourses || []).forEach(ec => {
      body += `<tr class="sub-row">
        <td colspan="3" style="padding-left:1.5rem">${ec.courseCode || ec.courseTitle || ec.courseId || 'Course'}</td>
        <td>${ec.grade ?? '-'}</td><td>${ec.status}</td><td></td>
      </tr>`;
    });
  });
  return `<div class="history-block"><div class="section-title">Enrollment History</div>
    <div class="table-wrap"><table><thead><tr>
      <th>Year</th><th>Sem</th><th>Level</th><th>Status / Grade</th><th>Scholastic / Course Status</th><th>Courses</th>
    </tr></thead><tbody>${body}</tbody></table></div></div>`;
}

async function showStudentDetail(id) {
  const st = await api('/admin/students/' + id);
  const el = document.getElementById('studentDetail');
  el.innerHTML = `
    <h2>${fmtName(st.profile)} (${st.studentNumber})</h2>
    <p>Status: ${st.studentStatus} · Scholastic: ${st.scholasticStatus} · Admitted: ${st.yearAdmitted || '-'}</p>
    ${enrollmentHistoryHtml(st)}
    <details style="margin-top:0.5rem"><summary>Full JSON</summary><pre>${JSON.stringify(st, null, 2)}</pre></details>`;
  el.classList.remove('hidden');
}

async function openProgramManage(programId, title) {
  managedProgram = await api('/admin/programs/' + programId);
  document.getElementById('programManageTitle').textContent = (title || managedProgram.title) + ' — Curriculums';
  document.getElementById('programManage').classList.remove('hidden');
  renderCurriculumTable();
  fillCurriculumSelect();
}

function renderCurriculumTable() {
  const rows = (managedProgram.curriculums || []).map(c => [
    c.code, c.effectiveYear, c.durationYear, c.status,
    managedProgram.activeCurriculumId === c.id ? 'Active' : `<button class="btn btn-sm" data-set-active="${c.id}">Set Active</button>`
  ]);
  document.getElementById('curriculumTable').innerHTML = tableHtml(
    ['Code', 'Effective Year', 'Duration', 'Status', ''], rows);
}

function fillCurriculumSelect() {
  const sel = document.getElementById('curriculumSelect');
  sel.innerHTML = (managedProgram.curriculums || [])
    .map(c => `<option value="${c.id}">${c.code} (${c.effectiveYear})</option>`).join('');
}

async function loadCurriculumCourses() {
  if (!managedProgram) return;
  const f = filterValues('curriculumCourseFilters');
  const list = await api(`/admin/programs/${managedProgram.id}/curriculum/${f.curriculumId}/courses?yearLevel=${f.yearLevel}&semester=${f.semester}`);
  document.getElementById('curriculumCourseTable').innerHTML = tableHtml(
    ['Code', 'Title', 'Units', 'Year', 'Sem'],
    (list || []).map(c => {
      const a = (c.courseAssignments || []).find(x => x.curriculumId == f.curriculumId) || c.courseAssignments?.[0] || {};
      return [c.code, c.title, c.units, a.yearLevel, a.semester];
    }));
}

function formToObj(form) {
  const obj = {};
  new FormData(form).forEach((v, k) => {
    if (v === '') return;
    obj[k] = /^\d+$/.test(v) ? Number(v) : v;
  });
  return obj;
}

document.getElementById('studentForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    await loadCurrentTerm();
    const f = e.target;
    const today = new Date().toISOString().slice(0, 10);
    const body = {
      studentStatus: 'ACTIVE',
      scholasticStatus: 'REGULAR',
      yearAdmitted: currentTerm.academicYear,
      account: {
        username: f.username.value,
        password: f.password.value,
        email: f.email.value,
        role: 'STUDENT',
        status: 'ACTIVE'
      },
      profile: {
        firstName: f.firstName.value,
        lastName: f.lastName.value,
        civilStatus: 'SINGLE',
        nationality: 'Filipino',
        sex: 'MALE'
      },
      academicInfo: [{
        enrolledProgramId: Number(f.enrolledProgramId.value),
        programCurriculumId: null,
        sectionId: Number(f.sectionId.value),
        academicYear: currentTerm.academicYear,
        semester: currentTerm.semester,
        yearLevel: Number(f.yearLevel.value),
        units: 0,
        enrollmentDate: today,
        scholasticStatus: 'REGULAR',
        enrollmentStatus: 'ACTIVE',
        enrolledCourses: [{
          scheduleId: Number(f.scheduleId.value),
          status: 'ONGOING'
        }]
      }]
    };
    const prog = await api('/admin/programs/' + f.enrolledProgramId.value);
    body.academicInfo[0].programCurriculumId = prog.activeCurriculumId;
    if (!body.academicInfo[0].programCurriculumId) throw new Error('Program has no active curriculum');
    await api('/admin/students/add', { method: 'POST', body: JSON.stringify(body) });
    showMsg(msg, 'Student added', 'ok');
    f.reset();
    applyTermToForms();
    loadStudents();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('facultyForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    const f = e.target;
    const ids = f.qualifiedCoursesIds.value.split(',').map(s => s.trim()).filter(Boolean).map(Number);
    const body = {
      account: { username: f.username.value, password: f.password.value, email: f.email.value, role: 'FACULTY', status: 'ACTIVE' },
      profile: { firstName: f.firstName.value, lastName: f.lastName.value, civilStatus: 'SINGLE', nationality: 'Filipino', sex: 'MALE' },
      departmentId: f.departmentId.value ? Number(f.departmentId.value) : null,
      employmentType: f.employmentType.value,
      position: f.position.value,
      dateHired: f.dateHired.value,
      qualifiedCoursesIds: ids,
      status: 'ACTIVE'
    };
    await api('/admin/faculty/add', { method: 'POST', body: JSON.stringify(body) });
    showMsg(msg, 'Faculty added', 'ok');
    f.reset();
    loadFaculty();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('courseForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    const f = formToObj(e.target);
    const body = {
      title: f.title,
      departmentId: f.departmentId,
      units: f.units,
      lectureHours: f.lectureHours,
      labHours: f.labHours,
      status: f.status,
      notes: f.notes || null,
      courseAssignments: [{ programId: f.programId, curriculumId: f.curriculumId, yearLevel: f.yearLevel, semester: f.semester }]
    };
    await api('/admin/courses/add', { method: 'POST', body: JSON.stringify(body) });
    showMsg(msg, 'Course added', 'ok');
    e.target.reset();
    loadCourses();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('scheduleForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    await loadCurrentTerm();
    const f = e.target;
    const body = {
      courseAssignmentId: Number(f.courseAssignmentId.value),
      sectionId: Number(f.sectionId.value),
      assignedFacultyId: Number(f.assignedFacultyId.value),
      room: f.room.value,
      yearLevel: Number(f.yearLevel.value),
      academicYear: currentTerm.academicYear,
      status: f.status.value,
      notes: f.notes.value || null,
      days: [{ name: f.dayName.value, startTime: f.startTime.value.slice(0, 5), endTime: f.endTime.value.slice(0, 5) }]
    };
    await api('/admin/schedules/add', { method: 'POST', body: JSON.stringify(body) });
    showMsg(msg, 'Schedule added', 'ok');
    f.reset();
    applyTermToForms();
    loadSchedules();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

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
    const body = formToObj(e.target);
    await api('/admin/terms/' + TERM_ID, { method: 'PUT', body: JSON.stringify(body) });
    showMsg(msg, 'Term updated', 'ok');
    loadCurrentTerm();
    loadDashboard();
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

document.getElementById('programAddBtn').onclick = async () => {
  try {
    hideMsg(msg);
    const body = JSON.parse(document.getElementById('programJson').value);
    await api('/admin/programs/add', { method: 'POST', body: JSON.stringify(body) });
    showMsg(msg, 'Program added', 'ok');
    loadPrograms();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('curriculumForm').onsubmit = async e => {
  e.preventDefault();
  if (!managedProgram) return;
  try {
    hideMsg(msg);
    const f = formToObj(e.target);
    const years = [];
    for (let i = 1; i <= f.yearCount; i++) {
      years.push({ level: i, totalSemester: f.semestersPerYear });
    }
    const curriculums = [...(managedProgram.curriculums || [])];
    curriculums.push({ effectiveYear: f.effectiveYear, years });
    const body = {
      id: managedProgram.id,
      title: managedProgram.title,
      code: managedProgram.code,
      status: managedProgram.status,
      departmentId: managedProgram.departmentId,
      activeCurriculumId: managedProgram.activeCurriculumId,
      curriculums
    };
    managedProgram = await api('/admin/programs/' + managedProgram.id, { method: 'PUT', body: JSON.stringify(body) });
    if (f.setActive === 'yes') {
      const newest = managedProgram.curriculums[managedProgram.curriculums.length - 1];
      body.activeCurriculumId = newest.id;
      body.curriculums = managedProgram.curriculums;
      managedProgram = await api('/admin/programs/' + managedProgram.id, { method: 'PUT', body: JSON.stringify(body) });
    }
    showMsg(msg, 'Curriculum added', 'ok');
    renderCurriculumTable();
    fillCurriculumSelect();
    e.target.reset();
    loadPrograms();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('curriculumCourseFilters').onsubmit = e => {
  e.preventDefault();
  loadCurriculumCourses();
};

document.getElementById('programManageClose').onclick = () => {
  document.getElementById('programManage').classList.add('hidden');
  managedProgram = null;
};

document.body.addEventListener('click', async e => {
  const btn = e.target.closest('button[data-kind]');
  if (btn) {
    if (btn.dataset.kind === 'student') showStudentDetail(btn.dataset.id);
    else {
      const data = await api('/admin/faculty/' + btn.dataset.id);
      const el = document.getElementById('facultyDetail');
      el.textContent = JSON.stringify(data, null, 2);
      el.classList.remove('hidden');
    }
    return;
  }
  const progBtn = e.target.closest('[data-program]');
  if (progBtn) openProgramManage(progBtn.dataset.program, progBtn.dataset.title);
  const activeBtn = e.target.closest('[data-set-active]');
  if (activeBtn && managedProgram) {
    try {
      hideMsg(msg);
      const body = {
        id: managedProgram.id,
        title: managedProgram.title,
        code: managedProgram.code,
        status: managedProgram.status,
        departmentId: managedProgram.departmentId,
        activeCurriculumId: Number(activeBtn.dataset.setActive),
        curriculums: managedProgram.curriculums
      };
      managedProgram = await api('/admin/programs/' + managedProgram.id, { method: 'PUT', body: JSON.stringify(body) });
      showMsg(msg, 'Active curriculum updated', 'ok');
      renderCurriculumTable();
      loadPrograms();
    } catch (err) { showMsg(msg, err.message, 'err'); }
  }
});

['studentFilters', 'facultyFilters', 'courseFilters', 'scheduleFilters', 'sectionFilters', 'programFilters', 'deptFilters']
  .forEach(id => bindFilter(id, {
    studentFilters: loadStudents,
    facultyFilters: loadFaculty,
    courseFilters: loadCourses,
    scheduleFilters: loadSchedules,
    sectionFilters: loadSections,
    programFilters: loadPrograms,
    deptFilters: loadDepartments
  }[id]));

bindNav({
  dashboard: loadDashboard,
  students: () => { loadCurrentTerm().then(loadStudents); },
  faculty: loadFaculty,
  courses: loadCourses,
  schedules: () => { loadCurrentTerm().then(loadSchedules); },
  sections: loadSections,
  programs: loadPrograms,
  departments: loadDepartments,
  term: loadTermPanel,
  accounts: loadAccounts
});

loadDashboard();
