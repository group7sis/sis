let editState = { type: null, id: null, data: null };

function getMultiSelectValues(sel) {
  if (!sel) return [];
  return [...sel.selectedOptions].map(o => Number(o.value));
}

function setMultiSelectValues(sel, ids) {
  if (!sel) return;
  const set = new Set((ids || []).map(String));
  [...sel.options].forEach(o => { o.selected = set.has(o.value); });
}

function getCheckboxValues(container) {
  if (!container) return [];
  return [...container.querySelectorAll('input[type="checkbox"][name]')]
    .filter(cb => cb.checked)
    .map(cb => Number(cb.value));
}

function setCheckboxValues(container, ids) {
  if (!container) return;
  const set = new Set((ids || []).map(String));
  container.querySelectorAll('input[type="checkbox"][name]').forEach(cb => {
    cb.checked = set.has(cb.value);
  });
}

function clearEdit(type) {
  if (editState.type !== type) return;
  editState = { type: null, id: null, data: null };
  const map = {
    student: ['studentForm', 'studentSubmitBtn', 'Add Student'],
    faculty: ['facultyForm', 'facultySubmitBtn', 'Add Faculty'],
    course: ['courseForm', 'courseSubmitBtn', 'Add Course'],
    schedule: ['scheduleForm', 'scheduleSubmitBtn', 'Add Schedule'],
    section: ['sectionForm', 'sectionSubmitBtn', 'Add Section'],
    program: ['programForm', 'programSubmitBtn', 'Add Program'],
    department: ['deptForm', 'deptSubmitBtn', 'Add Department'],
    account: ['accountForm', 'accountSubmitBtn', 'Add Admin'],
    curriculum: ['curriculumForm', 'curriculumSubmitBtn', 'Add Curriculum']
  };
  const [formId, btnId, label] = map[type] || [];
  const form = document.getElementById(formId);
  if (form) {
    form.reset();
    // Show initial curriculum section in program form if it was hidden
    if (type === 'program') {
      const section = form.querySelector('.section-title');
      if (section && section.textContent.includes('Initial Curriculum')) {
        section.style.display = '';
        const fields = form.querySelectorAll('.grid-2 > div');
        // Show effective year, year count, year semester fields
        const yearFields = form.querySelectorAll('[name="effectiveYear"], [name="yearCount"], #yearSemesterFields');
        yearFields.forEach(el => { if (el) el.closest?.('div') ? el.closest('div').style.display = '' : el.style.display = ''; });
      }
    }
  }
  document.getElementById(btnId).textContent = label;
  if (type === 'student') {
    document.getElementById('studentEditFields')?.classList.add('hidden');
    const academicInfoFields = document.getElementById('studentAcademicInfoFields');
    if (academicInfoFields) academicInfoFields.style.display = '';
    applyTermToForms();
  }
  if (type === 'course') {
    const container = document.getElementById('courseAssignmentRows');
    if (container) {
      container.innerHTML = '';
      initRepeater('courseAssignmentRows', assignmentRowHtml, 1);
    }
    const prereqs = document.getElementById('coursePrereqs');
    if (prereqs) {
      prereqs.innerHTML = qualifiedCourseCheckboxHtml();
      setCheckboxValues(prereqs, []);
    }
  }
  if (type === 'schedule') initRepeater('scheduleDayRows', dayRowHtml, 1);
}

async function startEdit(type, id) {
  editState = { type, id, data: null };
  hideMsg(msg);
  if (type === 'course') {
    const data = await api('/admin/courses/' + id);
    editState.data = data;
    const f = document.getElementById('courseForm');
    f.title.value = data.title || '';
    f.departmentId.value = data.departmentId || '';
    f.units.value = data.units || '';
    f.lectureHours.value = data.lectureHours || '';
    f.labHours.value = data.labHours || 0;
    f.status.value = data.status || 'ACTIVE';
    f.notes.value = data.notes || '';
    const prereqs = document.getElementById('coursePrereqs');
    if (prereqs && data.prerequisiteIds) {
      prereqs.innerHTML = qualifiedCourseCheckboxHtml(data.prerequisiteIds, data.id);
    }
    const assignments = data.courseAssignments || [];
    const container = document.getElementById('courseAssignmentRows');
    if (container) {
      container.innerHTML = '';
      assignments.forEach(a => container.insertAdjacentHTML('beforeend', assignmentRowHtml(a)));
    }
    document.getElementById('courseSubmitBtn').textContent = 'Update Course';
  }
  if (type === 'student') {
    const st = await api('/admin/students/' + id);
    editState.data = st;
    const f = document.getElementById('studentForm');
    const p = st.profile || {};
    const a = st.account || {};
    ['firstName', 'middleName', 'lastName', 'suffix', 'mobileNumber'].forEach(k => { if (f[k]) f[k].value = p[k] ?? ''; });
    f.sex.value = p.sex || 'MALE';
    f.dateOfBirth.value = p.dateOfBirth || '';
    f.civilStatus.value = p.civilStatus || 'SINGLE';
    f.nationality.value = p.nationality || 'Filipino';
    f.username.value = a.username || '';
    f.password.value = a.password || '';
    f.email.value = a.email || '';
    f.accountStatus.value = a.status || 'ACTIVE';
    f.studentStatus.value = st.studentStatus;
    f.scholasticStatus.value = st.scholasticStatus;
    document.getElementById('studentEditFields').classList.remove('hidden');
    fillLookupSelects(f);
    document.getElementById('studentSubmitBtn').textContent = 'Update Student';
    f.scrollIntoView({ behavior: 'smooth' });
  }
  if (type === 'faculty') {
    const data = await api('/admin/faculty/' + id);
    editState.data = data;
    const f = document.getElementById('facultyForm');
    const p = data.profile || {};
    const a = data.account || {};
    ['firstName', 'middleName', 'lastName', 'suffix', 'mobileNumber', 'position'].forEach(k => { if (f[k]) f[k].value = p[k] ?? ''; });
    f.sex.value = p.sex || 'MALE';
    f.dateOfBirth.value = p.dateOfBirth || '';
    f.civilStatus.value = p.civilStatus || 'SINGLE';
    f.nationality.value = p.nationality || 'Filipino';
    f.username.value = a.username || '';
    f.password.value = a.password || '';
    f.email.value = a.email || '';
    f.accountStatus.value = a.status || 'ACTIVE';
    f.departmentId.value = data.departmentId ?? '';
    f.employmentType.value = data.employmentType;
    f.dateHired.value = data.dateHired || '';
    f.status.value = data.status;
    const facultyCourses = document.getElementById('facultyQualifiedCourses');
    if (facultyCourses && data.qualifiedCoursesIds) {
      facultyCourses.innerHTML = qualifiedCourseCheckboxHtml(data.qualifiedCoursesIds);
    }
    document.getElementById('facultySubmitBtn').textContent = 'Update Faculty';
  }
  if (type === 'schedule') {
    const data = await api('/admin/schedules/' + id);
    editState.data = data;
    const f = document.getElementById('scheduleForm');
    fillLookupSelects(f);
    f.courseAssignmentId.value = data.courseAssignmentId;
    f.sectionId.value = data.sectionId;
    f.assignedFacultyId.value = data.assignedFacultyId;
    f.academicYear.value = data.academicYear;
    f.status.value = data.status;
    f.notes.value = data.notes || '';
    const container = document.getElementById('scheduleDayRows');
    container.innerHTML = '';
    const days = (data.days || []).map(d => ({
      name: d.name,
      startTime: d.timeStart || d.startTime || '',
      endTime: d.timeEnd || d.endTime || ''
    }));
    if (days.length) {
      days.forEach(d => container.insertAdjacentHTML('beforeend', dayRowHtml(d)));
    } else {
      container.insertAdjacentHTML('beforeend', dayRowHtml({ name: 'MONDAY' }));
    }
    document.getElementById('scheduleSubmitBtn').textContent = 'Update Schedule';
  }
  if (type === 'section') {
    const data = await api('/admin/sections/' + id);
    editState.data = data;
    const f = document.getElementById('sectionForm');
    fillLookupSelects(f);
    f.name.value = data.name;
    f.programId.value = data.programId;
    f.yearLevel.value = data.yearLevel;
    f.maxCapacity.value = data.maxCapacity;
    f.status.value = data.status;
    f.notes.value = data.notes || '';
    document.getElementById('sectionSubmitBtn').textContent = 'Update Section';
  }
  if (type === 'program') {
    const data = await api('/admin/programs/' + id);
    editState.data = data;
    const f = document.getElementById('programForm');
    fillLookupSelects(f);
    f.title.value = data.title;
    f.code.value = data.code;
    f.status.value = data.status;
    f.departmentId.value = data.departmentId;
    document.getElementById('programSubmitBtn').textContent = 'Update Program';
  }
  if (type === 'department') {
    const data = await api('/admin/departments/' + id);
    editState.data = data;
    const f = document.getElementById('deptForm');
    f.name.value = data.name;
    f.code.value = data.code;
    document.getElementById('deptSubmitBtn').textContent = 'Update Department';
  }
  if (type === 'account') {
    const list = await api('/admin/accounts');
    const data = list.find(a => a.id === Number(id));
    editState.data = data;
    const f = document.getElementById('accountForm');
    f.username.value = data.username;
    f.password.value = data.password || '';
    document.getElementById('accountSubmitBtn').textContent = 'Update Admin';
  }
}

['student', 'faculty', 'course', 'schedule', 'section', 'program', 'department', 'account', 'curriculum'].forEach(type => {
  document.getElementById( type + 'CloseDialog')?.addEventListener('click', () => clearEdit(type));
});

document.body.addEventListener('click', e => {
  const btn = e.target.closest('[data-edit]');
  if (btn) startEdit(btn.dataset.edit, btn.dataset.id);
});

async function initAdmin() {
  await loadLookups();
  fillLookupSelects();
  const fq = document.getElementById('facultyQualifiedCourses');
  const cp = document.getElementById('coursePrereqs');
  if (fq) fq.innerHTML = qualifiedCourseCheckboxHtml();
  if (cp) cp.innerHTML = qualifiedCourseCheckboxHtml();
}
