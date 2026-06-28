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

function clearEdit(type) {
  if (editState.type !== type) return;
  editState = { type: null, id: null, data: null };
  const map = {
    student: ['studentForm', 'studentSubmitBtn', 'studentCancelEdit', 'Add Student'],
    faculty: ['facultyForm', 'facultySubmitBtn', 'facultyCancelEdit', 'Add Faculty'],
    schedule: ['scheduleForm', 'scheduleSubmitBtn', 'scheduleCancelEdit', 'Add Schedule'],
    section: ['sectionForm', 'sectionSubmitBtn', 'sectionCancelEdit', 'Add Section'],
    program: ['programForm', 'programSubmitBtn', 'programCancelEdit', 'Add Program'],
    department: ['deptForm', 'deptSubmitBtn', 'deptCancelEdit', 'Add Department'],
    account: ['accountForm', 'accountSubmitBtn', 'accountCancelEdit', 'Add Admin']
  };
  const [formId, btnId, cancelId, label] = map[type] || [];
  document.getElementById(formId)?.reset();
  document.getElementById(btnId).textContent = label;
  document.getElementById(cancelId)?.classList.add('hidden');
  if (type === 'student') {
    document.getElementById('studentEditFields')?.classList.add('hidden');
    applyTermToForms();
  }
  if (type === 'schedule') initRepeater('scheduleDayRows', dayRowHtml, 1);
}

async function startEdit(type, id) {
  editState = { type, id, data: null };
  hideMsg(msg);
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
    document.getElementById('studentCancelEdit').classList.remove('hidden');
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
    setMultiSelectValues(document.getElementById('facultyQualifiedCourses'), data.qualifiedCoursesIds);
    document.getElementById('facultySubmitBtn').textContent = 'Update Faculty';
    document.getElementById('facultyCancelEdit').classList.remove('hidden');
  }
  if (type === 'schedule') {
    const data = await api('/admin/schedules/' + id);
    editState.data = data;
    const f = document.getElementById('scheduleForm');
    fillLookupSelects(f);
    f.courseAssignmentId.value = data.courseAssignmentId;
    f.sectionId.value = data.sectionId;
    f.assignedFacultyId.value = data.assignedFacultyId;
    f.yearLevel.value = data.yearLevel;
    f.academicYear.value = data.academicYear;
    f.semester.value = data.semester ?? '';
    f.status.value = data.status;
    f.notes.value = data.notes || '';
    const container = document.getElementById('scheduleDayRows');
    container.innerHTML = '';
    (data.days || [{ name: 'MONDAY' }]).forEach(d => container.insertAdjacentHTML('beforeend', dayRowHtml(d)));
    document.getElementById('scheduleSubmitBtn').textContent = 'Update Schedule';
    document.getElementById('scheduleCancelEdit').classList.remove('hidden');
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
    document.getElementById('sectionCancelEdit').classList.remove('hidden');
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
    document.getElementById('programCancelEdit').classList.remove('hidden');
  }
  if (type === 'department') {
    const data = await api('/admin/departments/' + id);
    editState.data = data;
    const f = document.getElementById('deptForm');
    f.name.value = data.name;
    f.code.value = data.code;
    document.getElementById('deptSubmitBtn').textContent = 'Update Department';
    document.getElementById('deptCancelEdit').classList.remove('hidden');
  }
  if (type === 'account') {
    const list = await api('/admin/accounts');
    const data = list.find(a => a.id === Number(id));
    editState.data = data;
    const f = document.getElementById('accountForm');
    f.username.value = data.username;
    f.password.value = data.password || '';
    document.getElementById('accountSubmitBtn').textContent = 'Update Admin';
    document.getElementById('accountCancelEdit').classList.remove('hidden');
  }
}

['student', 'faculty', 'schedule', 'section', 'program', 'department', 'account'].forEach(type => {
  document.getElementById(type + 'CancelEdit')?.addEventListener('click', () => clearEdit(type));
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
  if (fq) fq.innerHTML = qualifiedCourseSelectHtml();
  if (cp) cp.innerHTML = qualifiedCourseSelectHtml();
  const cmp = document.getElementById('courseManagePrereqs');
  if (cmp && cmp.tagName === 'SELECT') cmp.innerHTML = qualifiedCourseSelectHtml();
}
