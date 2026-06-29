const user = requireRole('ADMIN', 'admin-login.html');
if (!user) throw new Error('no session');

const TERM_ID = 1;
let currentTerm = null;
let managedProgram = null;
let managedCourse = null;
let currentModalForm = null;

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
  updateGlobalTermBanner();
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

function updateGlobalTermBanner() {
  const banner = document.getElementById('globalTermBanner');
  if (banner && currentTerm) {
    banner.innerHTML = `<strong>CURRENT TERM:</strong> ${currentTerm.academicYear} | Semester ${currentTerm.semester} | ${currentTerm.status}`;
  }
}

async function loadDashboard() {
  await loadCurrentTerm();
  updateGlobalTermBanner();
  const d = await api('/admin/dashboard');
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
    ['Number', 'Name', 'Year Level', 'Status', 'Scholastic', ''],
    (data.students || []).map(st => [
      st.studentNumber, fmtName(st.profile), st.yearLevel ?? '-', st.studentStatus, st.scholasticStatus,
      `<button class="btn btn-sm" data-id="${st.id}" data-kind="student">View</button>
       <button class="btn btn-sm secondary" data-show-dialog="studentDialog" data-edit-id="${st.id}">Edit</button>`
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
      `<button class="btn btn-sm" data-id="${f.id}" data-kind="faculty">View</button>
       <button class="btn btn-sm secondary" data-show-dialog="facultyDialog" data-edit-id="${f.id}">Edit</button>`
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
    ['Code', 'Title', 'Units', 'Assignments', 'Status', ''],
    (data.courses || []).map(c => [
      c.code, c.title, c.units, (c.courseAssignments || []).length, c.status,
      `<button class="btn btn-sm" data-show-dialog="courseDialog" data-edit-id="${c.id}">Manage/Edit</button>`
    ]));
}

async function loadSchedules() {
  const filters = filterValues('scheduleFilters');
  const q = buildQuery(filters);
  const data = await api('/admin/schedules' + q);
  const s = data.stats || {};
  let statsHtml = statBox('Total', s.totalSchedules) + statBox('Active', s.totalActiveSchedules);
  if (filters.day) {
    statsHtml += statBox('Daily Hrs', s.dailyHours);
  } else {
    statsHtml += statBox('Weekly Hrs', s.weeklyHours);
  }
  document.getElementById('scheduleStats').innerHTML = statsHtml;
  document.getElementById('scheduleTable').innerHTML = tableHtml(
    ['Code', 'Course', 'Section', 'Faculty', 'Year', 'Days', 'Status', ''],
    (data.schedules || []).map(sc => [
      sc.code, sc.courseTitle, sc.sectionName, sc.facultyName || '-', sc.academicYear, fmtDays(sc.days), sc.status,
      `<button class="btn btn-sm secondary" data-show-dialog="scheduleDialog" data-edit-id="${sc.id}">Edit</button>`
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
    (data.sections || []).map(x => [
      x.code, x.name, LK.programs.find(p => p.id === x.programId)?.code || x.programId, x.yearLevel, x.maxCapacity, x.status,
      `<button class="btn btn-sm secondary" data-show-dialog="sectionDialog" data-edit-id="${x.id}">Edit</button>`
    ]));
}

async function loadPrograms() {
  const q = buildQuery(filterValues('programFilters'));
  const data = await api('/admin/programs' + q);
  document.getElementById('programTable').innerHTML = tableHtml(
    ['Code', 'Title', 'Status', 'Active Curriculum', ''],
  (data || []).map(p => {
    const activeCurr = (p.curriculums || []).find(c => c.id === p.activeCurriculumId);
    const activeLabel = activeCurr ? `${activeCurr.code || activeCurr.id} (${activeCurr.effectiveYear || ''})` : (p.activeCurriculumId ?? '-');
    return [
      p.code || p.id, p.title, p.status, activeLabel,
      `<button class="btn btn-sm" data-show-dialog="programDialog" data-edit-id="${p.id}">Manage/Edit</button>`
    ];
  }));
}

async function loadDepartments() {
  const q = buildQuery(filterValues('deptFilters'));
  const data = await api('/admin/departments' + q);
  document.getElementById('departmentTable').innerHTML = tableHtml(
    ['Code', 'Name'], (data || []).map(d => [
      d.code, d.name,
      `<button class="btn btn-sm secondary" data-show-dialog="deptDialog" data-edit-id="${d.id}">Edit</button>`
    ]));
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
    ['Username', ''], (data || []).map(a => [
      a.username,
      `<button class="btn btn-sm secondary" data-show-dialog="accountDialog" data-edit-id="${a.id}">Edit</button>`
    ]));
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
  renderCurriculumTable();
  fillCurriculumSelect();
  document.getElementById('programManageDialog').showModal();
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

async function openCourseManage(courseId) {
  managedCourse = await api('/admin/courses/' + courseId);
  document.getElementById('courseManageTitle').textContent =
    (managedCourse.code || 'Course') + ' — ' + (managedCourse.title || '');
  const dept = LK.departments.find(d => d.id === managedCourse.departmentId);
  const deptName = dept ? `${dept.code} - ${dept.name}` : `Department ${managedCourse.departmentId}`;
  document.getElementById('courseManageMeta').textContent =
    `${deptName} · ${managedCourse.units} units · ${managedCourse.status}`;
  const container = document.getElementById('courseManageAssignmentRows');
  container.innerHTML = '';
  const assignments = managedCourse.courseAssignments || [];
  if (assignments.length) {
    assignments.forEach(a => container.insertAdjacentHTML('beforeend', assignmentRowHtml(a)));
  } else {
    container.insertAdjacentHTML('beforeend', assignmentRowHtml());
  }
  document.getElementById('courseManagePrereqs').innerHTML = qualifiedCourseSelectHtml(managedCourse.prerequisiteIds || []);
  document.getElementById('courseManageDialog').showModal();
}

function formToObj(form) {
  const obj = {};
  new FormData(form).forEach((v, k) => {
    if (v === '') return;
    obj[k] = /^\d+$/.test(v) ? Number(v) : v;
  });
  return obj;
}

// Open dialogs using native HTML dialog element
document.body.addEventListener('click', async e => {
  const showBtn = e.target.closest('[data-show-dialog]');
  if (showBtn) {
    const dialogId = showBtn.dataset.showDialog;
    const dialog = document.getElementById(dialogId);
    if (dialog) {
      // Close any other open dialogs
      document.querySelectorAll('dialog[open]').forEach(d => {
        if (d !== dialog) d.close();
      });
      
      // Handle edit mode if edit-id is provided
      const editId = showBtn.dataset.editId;
      if (editId) {
        await loadEntityForEdit(dialogId, editId);
      }
      
      // Open this dialog
      dialog.showModal();
      
      // Re-initialize dynamic elements
      const form = dialog.querySelector('form');
      if (form) {
        setTimeout(() => {
          if (form.querySelector('[data-repeater="assignment"]')) {
            initRepeater('courseAssignmentRows', assignmentRowHtml, 1);
          }
          if (form.querySelector('[data-repeater="day"]')) {
            initRepeater('scheduleDayRows', dayRowHtml, 1);
          }
          const facultyCourses = form.querySelector('#facultyQualifiedCourses');
          if (facultyCourses) facultyCourses.innerHTML = qualifiedCourseSelectHtml();
          const coursePrereqs = form.querySelector('#coursePrereqs');
          if (coursePrereqs) coursePrereqs.innerHTML = qualifiedCourseSelectHtml();
          if (form.id === 'programForm' || form.id === 'curriculumForm') {
            const yearCountInput = form.querySelector('[name="yearCount"]');
            const containerId = form.id === 'programForm' ? 'yearSemesterFields' : 'curriculumYearSemesterFields';
            if (yearCountInput) renderYearSemesterFields(containerId, Number(yearCountInput.value) || 4);
          }
        }, 0);
      }
    }
  }
});

async function loadEntityForEdit(dialogId, entityId) {
  // Map dialog IDs to entity types and API endpoints
  const dialogMap = {
    studentDialog: { type: 'student', endpoint: `/admin/students/${entityId}` },
    facultyDialog: { type: 'faculty', endpoint: `/admin/faculty/${entityId}` },
    courseDialog: { type: 'course', endpoint: `/admin/courses/${entityId}` },
    scheduleDialog: { type: 'schedule', endpoint: `/admin/schedules/${entityId}` },
    sectionDialog: { type: 'section', endpoint: `/admin/sections/${entityId}` },
    programDialog: { type: 'program', endpoint: `/admin/programs/${entityId}` },
    deptDialog: { type: 'department', endpoint: `/admin/departments/${entityId}` },
    accountDialog: { type: 'account', endpoint: `/admin/accounts/${entityId}` }
  };
  
  const mapping = dialogMap[dialogId];
  if (!mapping) return;
  
  try {
    const data = await api(mapping.endpoint);
    editState.type = mapping.type;
    editState.id = entityId;
    editState.data = data;
    
    const form = document.getElementById(dialogId)?.querySelector('form');
    if (!form) return;
    
    // Populate form based on entity type
    switch (mapping.type) {
      case 'student':
        populateStudentForm(form, data);
        break;
      case 'faculty':
        populateFacultyForm(form, data);
        break;
      case 'course':
        populateCourseForm(form, data);
        break;
      case 'schedule':
        populateScheduleForm(form, data);
        break;
      case 'section':
        populateSectionForm(form, data);
        break;
      case 'program':
        populateProgramForm(form, data);
        break;
      case 'department':
        populateDepartmentForm(form, data);
        break;
      case 'account':
        populateAccountForm(form, data);
        break;
    }
    
    // Update submit button text
    const submitBtn = form.querySelector('[type="submit"]');
    if (submitBtn) submitBtn.textContent = 'Update';
    
    // Show cancel edit button if exists
    const cancelBtn = form.querySelector('[id$="CancelEdit"]');
    if (cancelBtn) cancelBtn.classList.remove('hidden');
  } catch (err) {
    console.error('Failed to load entity for edit:', err);
  }
}

function populateStudentForm(form, data) {
  const p = data.profile || {};
  form.firstName.value = p.firstName || '';
  form.middleName.value = p.middleName || '';
  form.lastName.value = p.lastName || '';
  form.suffix.value = p.suffix || '';
  form.sex.value = p.sex || 'MALE';
  form.dateOfBirth.value = p.dateOfBirth || '';
  form.civilStatus.value = p.civilStatus || 'SINGLE';
  form.nationality.value = p.nationality || 'Filipino';
  form.mobileNumber.value = p.mobileNumber || '';
  
  const a = data.account || {};
  form.username.value = a.username || '';
  form.password.value = a.password || '';
  form.email.value = a.email || '';
  form.accountStatus.value = a.status || 'ACTIVE';
  
  // Show edit fields
  const editFields = document.getElementById('studentEditFields');
  if (editFields) {
    editFields.classList.remove('hidden');
    editFields.classList.add('grid-2');
  }
  form.studentStatus.value = data.studentStatus || 'ACTIVE';
  form.scholasticStatus.value = data.scholasticStatus || 'REGULAR';
}

function populateFacultyForm(form, data) {
  const p = data.profile || {};
  form.firstName.value = p.firstName || '';
  form.middleName.value = p.middleName || '';
  form.lastName.value = p.lastName || '';
  form.suffix.value = p.suffix || '';
  form.sex.value = p.sex || 'MALE';
  form.dateOfBirth.value = p.dateOfBirth || '';
  form.civilStatus.value = p.civilStatus || 'SINGLE';
  form.nationality.value = p.nationality || 'Filipino';
  form.mobileNumber.value = p.mobileNumber || '';
  
  const a = data.account || {};
  form.username.value = a.username || '';
  form.password.value = a.password || '';
  form.email.value = a.email || '';
  form.accountStatus.value = a.status || 'ACTIVE';
  
  form.departmentId.value = data.departmentId || '';
  form.employmentType.value = data.employmentType || 'FULL_TIME';
  form.position.value = data.position || '';
  form.dateHired.value = data.dateHired || '';
  form.status.value = data.status || 'ACTIVE';
  
  const facultyCourses = document.getElementById('facultyQualifiedCourses');
  if (facultyCourses && data.qualifiedCoursesIds) {
    facultyCourses.innerHTML = qualifiedCourseSelectHtml(data.qualifiedCoursesIds);
  }
}

function populateCourseForm(form, data) {
  form.title.value = data.title || '';
  form.departmentId.value = data.departmentId || '';
  form.units.value = data.units || '';
  form.lectureHours.value = data.lectureHours || '';
  form.labHours.value = data.labHours || 0;
  form.status.value = data.status || 'ACTIVE';
  form.notes.value = data.notes || '';
  
  const prereqs = document.getElementById('coursePrereqs');
  if (prereqs && data.prerequisiteIds) {
    prereqs.innerHTML = qualifiedCourseSelectHtml(data.prerequisiteIds);
  }
  
  const assignments = data.courseAssignments || [];
  const container = document.getElementById('courseAssignmentRows');
  if (container) {
    container.innerHTML = '';
    assignments.forEach(a => container.insertAdjacentHTML('beforeend', assignmentRowHtml(a)));
  }
}

function populateScheduleForm(form, data) {
  form.courseAssignmentId.value = data.courseAssignmentId || '';
  form.sectionId.value = data.sectionId || '';
  form.assignedFacultyId.value = data.assignedFacultyId || '';
  form.yearLevel.value = data.yearLevel || 1;
  form.academicYear.value = data.academicYear || '';
  form.semester.value = data.semester || '';
  form.status.value = data.status || 'ACTIVE';
  form.notes.value = data.notes || '';
  
  const days = data.days || [];
  const container = document.getElementById('scheduleDayRows');
  if (container) {
    container.innerHTML = '';
    days.forEach(d => container.insertAdjacentHTML('beforeend', dayRowHtml(d)));
  }
}

function populateSectionForm(form, data) {
  form.name.value = data.name || '';
  form.programId.value = data.programId || '';
  form.yearLevel.value = data.yearLevel || '';
  form.maxCapacity.value = data.maxCapacity || '';
  form.status.value = data.status || 'ACTIVE';
  form.notes.value = data.notes || '';
}

function populateProgramForm(form, data) {
  form.title.value = data.title || '';
  form.code.value = data.code || '';
  form.status.value = data.status || 'ACTIVE';
  form.departmentId.value = data.departmentId || '';
  
  const curriculums = data.curriculums || [];
  if (curriculums.length > 0) {
    const activeCurr = curriculums.find(c => c.id === data.activeCurriculumId) || curriculums[0];
    form.effectiveYear.value = activeCurr.effectiveYear || '';
    
    const yearCount = activeCurr.years ? activeCurr.years.length : 4;
    form.yearCount.value = yearCount;
    renderYearSemesterFields('yearSemesterFields', yearCount);
    
    if (activeCurr.years) {
      activeCurr.years.forEach(y => {
        const input = form.querySelector(`[name="yearSemester_${y.level}"]`);
        if (input) input.value = y.totalSemester || 2;
      });
    }
  }
}

function populateDepartmentForm(form, data) {
  form.name.value = data.name || '';
  form.code.value = data.code || '';
}

function populateAccountForm(form, data) {
  form.username.value = data.username || '';
  form.password.value = data.password || '';
}

// Close dialogs using native API
document.body.addEventListener('click', e => {
  const closeBtn = e.target.closest('[data-close-dialog]');
  if (closeBtn) {
    const dialogId = closeBtn.dataset.closeDialog;
    const dialog = document.getElementById(dialogId);
    if (dialog) dialog.close();
  }
});

// Show schedule filter function - filters schedules by current term's academic year/semester
function filterSchedulesByTerm() {
  const scheduleSelect = document.querySelector('[data-lookup="schedule"]');
  if (!scheduleSelect || !currentTerm) return;
  const filtered = (LK.schedules || []).filter(s =>
    s.academicYear === currentTerm.academicYear && s.semester === currentTerm.semester
  );
  fillSelect(scheduleSelect, filtered, 'id', schedLabel, scheduleSelect.value);
}

// Filter section dropdown when program changes in student form
function bindStudentSectionFilter() {
  const progSelect = document.forms.studentForm?.enrolledProgramId;
  const sectSelect = document.forms.studentForm?.sectionId;
  if (!progSelect || !sectSelect) return;
  progSelect.addEventListener('change', () => {
    const progId = Number(progSelect.value);
    if (!progId) {
      fillSelect(sectSelect, LK.sections, 'id', sectLabel, '');
      return;
    }
    const filtered = (LK.sections || []).filter(s => s.programId === progId);
    fillSelect(sectSelect, filtered, 'id', sectLabel, '');
  });
}

// Generate per-year semester fields
function renderYearSemesterFields(containerId, yearCount) {
  const container = document.getElementById(containerId);
  if (!container) return;
  container.innerHTML = '';
  for (let i = 1; i <= yearCount; i++) {
    const div = document.createElement('div');
    div.innerHTML = `<label>Year ${i} - Total Semesters</label><input name="yearSemester_${i}" type="number" value="2" min="1" required>`;
    container.appendChild(div);
  }
}

// Year count change handler for program form
document.getElementById('programForm')?.querySelector('[name="yearCount"]')?.addEventListener('change', function() {
  renderYearSemesterFields('yearSemesterFields', Number(this.value) || 1);
});

// Year count change handler for curriculum form
document.getElementById('curriculumForm')?.querySelector('[name="yearCount"]')?.addEventListener('change', function() {
  renderYearSemesterFields('curriculumYearSemesterFields', Number(this.value) || 1);
});

document.getElementById('studentForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    await loadCurrentTerm();
    const f = e.target;
    if (editState.type === 'student' && editState.id) {
      const body = {
        ...editState.data,
        studentStatus: f.studentStatus.value,
        scholasticStatus: f.scholasticStatus.value,
        account: { ...editState.data.account, ...buildAccountFromForm(f, 'STUDENT') },
        profile: buildProfileFromForm(f)
      };
      await api('/admin/students/' + editState.id, { method: 'PUT', body: JSON.stringify(body) });
      showMsg(msg, 'Student updated', 'ok');
      clearEdit('student');
      loadStudents();
      return;
    }
    const today = new Date().toISOString().slice(0, 10);
    const prog = await api('/admin/programs/' + f.enrolledProgramId.value);
    const scheduleIds = getMultiSelectValues(f.scheduleIds);
    if (!scheduleIds.length) throw new Error('Select at least one schedule');
    const body = {
      studentStatus: 'ACTIVE',
      scholasticStatus: 'REGULAR',
      yearAdmitted: currentTerm.academicYear,
      account: buildAccountFromForm(f, 'STUDENT'),
      profile: buildProfileFromForm(f),
      academicInfo: [{
        enrolledProgramId: Number(f.enrolledProgramId.value),
        programCurriculumId: prog.activeCurriculumId,
        sectionId: Number(f.sectionId.value),
        academicYear: currentTerm.academicYear,
        semester: currentTerm.semester,
        yearLevel: Number(f.yearLevel.value),
        units: 0,
        enrollmentDate: today,
        scholasticStatus: 'REGULAR',
        enrollmentStatus: 'ACTIVE',
        enrolledCourses: scheduleIds.map(scheduleId => ({ scheduleId, status: 'ONGOING' }))
      }]
    };
    if (!body.academicInfo[0].programCurriculumId) throw new Error('Program has no active curriculum');
    await api('/admin/students/add', { method: 'POST', body: JSON.stringify(body) });
    showMsg(msg, 'Student added', 'ok');
    f.reset();
    applyTermToForms();
    loadStudents();
    await loadLookups();
    fillLookupSelects();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('facultyForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    const f = e.target;
    const body = {
      account: buildAccountFromForm(f, 'FACULTY'),
      profile: buildProfileFromForm(f),
      departmentId: f.departmentId.value ? Number(f.departmentId.value) : null,
      employmentType: f.employmentType.value,
      position: f.position.value,
      dateHired: f.dateHired.value,
      qualifiedCoursesIds: getMultiSelectValues(document.getElementById('facultyQualifiedCourses')),
      status: f.status.value
    };
    if (editState.type === 'faculty' && editState.id) {
      await api('/admin/faculty/' + editState.id, { method: 'PUT', body: JSON.stringify({ ...editState.data, ...body }) });
      showMsg(msg, 'Faculty updated', 'ok');
      clearEdit('faculty');
    } else {
      await api('/admin/faculty/add', { method: 'POST', body: JSON.stringify(body) });
      showMsg(msg, 'Faculty added', 'ok');
      f.reset();
    }
    loadFaculty();
    await loadLookups();
    fillLookupSelects();
    document.getElementById('facultyQualifiedCourses').innerHTML = qualifiedCourseSelectHtml();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('courseForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    const f = e.target;
    const assignments = collectAssignments('courseAssignmentRows');
    if (!assignments.length) throw new Error('Add at least one program/curriculum assignment');
    const body = {
      title: f.title.value,
      departmentId: Number(f.departmentId.value),
      units: Number(f.units.value),
      lectureHours: Number(f.lectureHours.value),
      labHours: Number(f.labHours.value),
      status: f.status.value,
      notes: f.notes.value || null,
      prerequisiteIds: getMultiSelectValues(document.getElementById('coursePrereqs')),
      courseAssignments: assignments
    };
    await api('/admin/courses/add', { method: 'POST', body: JSON.stringify(body) });
    showMsg(msg, 'Course added', 'ok');
    f.reset();
    initRepeater('courseAssignmentRows', assignmentRowHtml, 1);
    loadCourses();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('scheduleForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    await loadCurrentTerm();
    const f = e.target;
    const days = collectDays('scheduleDayRows');
    if (!days.length) throw new Error('Add at least one schedule day');
    const body = {
      courseAssignmentId: Number(f.courseAssignmentId.value),
      sectionId: Number(f.sectionId.value),
      assignedFacultyId: Number(f.assignedFacultyId.value),
      room: f.room.value || 'TBD',
      yearLevel: Number(f.yearLevel.value),
      academicYear: currentTerm.academicYear,
      status: f.status.value,
      notes: f.notes.value || null,
      days
    };
    if (editState.type === 'schedule' && editState.id) {
      await api('/admin/schedules/' + editState.id, { method: 'PUT', body: JSON.stringify({ ...editState.data, ...body }) });
      showMsg(msg, 'Schedule updated', 'ok');
      clearEdit('schedule');
    } else {
      await api('/admin/schedules/add', { method: 'POST', body: JSON.stringify(body) });
      showMsg(msg, 'Schedule added', 'ok');
      f.reset();
      applyTermToForms();
      initRepeater('scheduleDayRows', dayRowHtml, 1);
    }
    loadSchedules();
    await loadLookups();
    fillLookupSelects();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('sectionForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    const body = formToObj(e.target);
    if (editState.type === 'section' && editState.id) {
      await api('/admin/sections/' + editState.id, { method: 'PUT', body: JSON.stringify({ ...editState.data, ...body }) });
      showMsg(msg, 'Section updated', 'ok');
      clearEdit('section');
    } else {
      await api('/admin/sections/add', { method: 'POST', body: JSON.stringify(body) });
      showMsg(msg, 'Section added', 'ok');
      e.target.reset();
    }
    loadSections();
    await loadLookups();
    fillLookupSelects();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('deptForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    const body = formToObj(e.target);
    if (editState.type === 'department' && editState.id) {
      await api('/admin/departments/' + editState.id, { method: 'PUT', body: JSON.stringify({ ...editState.data, ...body }) });
      showMsg(msg, 'Department updated', 'ok');
      clearEdit('department');
    } else {
      await api('/admin/departments/add', { method: 'POST', body: JSON.stringify(body) });
      showMsg(msg, 'Department added', 'ok');
      e.target.reset();
    }
    loadDepartments();
    await loadLookups();
    fillLookupSelects();
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
    const body = formToObj(e.target);
    if (editState.type === 'account' && editState.id) {
      await api('/admin/accounts/' + editState.id, { method: 'PUT', body: JSON.stringify(body) });
      showMsg(msg, 'Admin updated', 'ok');
      clearEdit('account');
    } else {
      await api('/admin/accounts/add', { method: 'POST', body: JSON.stringify(body) });
      showMsg(msg, 'Admin added', 'ok');
      e.target.reset();
    }
    loadAccounts();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('programForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    const f = formToObj(e.target);
    if (editState.type === 'program' && editState.id) {
      const body = {
        ...editState.data,
        title: f.title,
        code: f.code,
        status: f.status,
        departmentId: f.departmentId,
        curriculums: editState.data.curriculums
      };
      await api('/admin/programs/' + editState.id, { method: 'PUT', body: JSON.stringify(body) });
      showMsg(msg, 'Program updated', 'ok');
      clearEdit('program');
      loadPrograms();
      return;
    }
    const years = [];
    for (let i = 1; i <= f.yearCount; i++) {
      const semKey = 'yearSemester_' + i;
      years.push({ level: i, totalSemester: f[semKey] || 2 });
    }
    const body = {
      title: f.title, code: f.code, status: f.status, departmentId: f.departmentId,
      curriculums: [{ effectiveYear: f.effectiveYear, years }]
    };
    await api('/admin/programs/add', { method: 'POST', body: JSON.stringify(body) });
    showMsg(msg, 'Program added', 'ok');
    e.target.reset();
    loadPrograms();
    await loadLookups();
    fillLookupSelects();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('courseManageSave').onclick = async () => {
  if (!managedCourse) return;
  try {
    hideMsg(msg);
    const assignments = collectRepeater('courseManageAssignmentRows').map(a => {
      const row = {
        curriculumId: a.curriculumId,
        yearLevel: a.yearLevel,
        semester: a.semester
      };
      if (a.programId) row.programId = a.programId;
      if (a.id) row.id = a.id;
      return row;
    });
    if (!assignments.length) throw new Error('Add at least one assignment');
    const body = {
      id: managedCourse.id,
      code: managedCourse.code,
      title: managedCourse.title,
      departmentId: managedCourse.departmentId,
      units: managedCourse.units,
      lectureHours: managedCourse.lectureHours,
      labHours: managedCourse.labHours,
      status: managedCourse.status,
      notes: managedCourse.notes,
      prerequisiteIds: getMultiSelectValues(document.getElementById('courseManagePrereqs')),
      courseAssignments: assignments
    };
    managedCourse = await api('/admin/courses/' + managedCourse.id, { method: 'PUT', body: JSON.stringify(body) });
    showMsg(msg, 'Course assignments updated', 'ok');
    loadCourses();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

document.getElementById('courseManageClose').onclick = () => {
  document.getElementById('courseManageDialog').close();
  managedCourse = null;
};

document.getElementById('curriculumForm').onsubmit = async e => {
  e.preventDefault();
  if (!managedProgram) return;
  try {
    hideMsg(msg);
    const f = formToObj(e.target);
    const years = [];
    for (let i = 1; i <= f.yearCount; i++) {
      const semKey = 'yearSemester_' + i;
      years.push({ level: i, totalSemester: f[semKey] || 2 });
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
  document.getElementById('programManageDialog').close();
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
  students: async () => { await loadCurrentTerm(); await loadLookups(); fillLookupSelects(); filterSchedulesByTerm(); bindStudentSectionFilter(); loadStudents(); },
  faculty: async () => { await loadLookups(); fillLookupSelects(); loadFaculty(); },
  courses: async () => { await loadLookups(); fillLookupSelects(); initRepeater('courseAssignmentRows', assignmentRowHtml, 1); document.getElementById('coursePrereqs').innerHTML = qualifiedCourseSelectHtml(); loadCourses(); },
  schedules: async () => { await loadCurrentTerm(); await loadLookups(); fillLookupSelects(); initRepeater('scheduleDayRows', dayRowHtml, 1); loadSchedules(); },
  sections: async () => { await loadLookups(); fillLookupSelects(); loadSections(); },
  programs: async () => { await loadLookups(); fillLookupSelects(); loadPrograms(); },
  departments: async () => { await loadLookups(); fillLookupSelects(); loadDepartments(); },
  term: loadTermPanel,
  accounts: loadAccounts
});

initAdmin().then(() => {
  initRepeater('courseAssignmentRows', assignmentRowHtml, 1);
  initRepeater('scheduleDayRows', dayRowHtml, 1);
  // Initialize per-year semester fields for program form
  const yearCountInput = document.getElementById('programForm')?.querySelector('[name="yearCount"]');
  if (yearCountInput) renderYearSemesterFields('yearSemesterFields', Number(yearCountInput.value) || 4);
  const currYearCountInput = document.getElementById('curriculumForm')?.querySelector('[name="yearCount"]');
  if (currYearCountInput) renderYearSemesterFields('curriculumYearSemesterFields', Number(currYearCountInput.value) || 4);
  loadDashboard();
});
