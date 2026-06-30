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
      `<button class="btn btn-sm secondary" data-show-dialog="facultyDialog" data-edit-id="${f.id}">Edit</button>`
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
      `<button class="btn btn-sm secondary" data-show-dialog="courseDialog" data-edit-id="${c.id}">Edit</button>`
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
    ['Code', 'Name', 'Program', 'Year', 'Capacity', 'Status', ''],
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
      const activeLabel = activeCurr ? `${activeCurr.code}` : (p.activeCurriculumId ?? '-');
      return [
        p.code || p.id, p.title, p.status, activeLabel,
        `<button class="btn btn-sm" id="programCurricula${p.id}">Curriculums</button>
        <button class="btn btn-sm secondary" data-show-dialog="programDialog" data-edit-id="${p.id}">Edit</button>`
      ];
    }));
    
    (data || []).forEach(p => {
      document.getElementById(`programCurricula${p.id}`).onclick = () => {
        openProgramManage(p.id, p.title);
      };
    });
}

async function loadDepartments() {
  const q = buildQuery(filterValues('deptFilters'));
  const data = await api('/admin/departments' + q);
  document.getElementById('departmentTable').innerHTML = tableHtml(
    ['Code', 'Name', ''], (data || []).map(d => [
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
  document.getElementById('studentViewTitle').textContent = `${fmtName(st.profile)} (${st.studentNumber})`;
  
  let html = `<p style="margin-bottom:0.75rem">Status: ${st.studentStatus} · Scholastic: ${st.scholasticStatus} · Admitted: ${st.yearAdmitted || '-'}</p>`;
  
  const infos = st.academicInfo || [];
  if (!infos.length) {
    html += '<p>No enrollment records.</p>';
  } else {
    infos.forEach((info, idx) => {
      const enrolledCourses = info.enrolledCourses || [];
      html += `<div class="sub-panel" style="margin-bottom:0.75rem">
        <div style="display:flex;flex-wrap:wrap;gap:1rem;margin-bottom:0.5rem;font-size:0.85rem">
          <span><strong>A.Y.:</strong> ${info.academicYear}</span>
          <span><strong>Program:</strong> ${info.programTitle}</span>
          <span><strong>Year Level:</strong> ${info.yearLevel}</span>
          <span><strong>Sem.:</strong> ${info.semester}</span>
          <span><strong>Status:</strong> ${info.enrollmentStatus}</span>
          <span><strong>Section:</strong> ${info.sectionName || '-'}</span>
          <span><strong>Scholastic Status:</strong> ${info.scholasticStatus}</span>
          <span><strong>GWA:</strong> ${info.gwa == 0 ? '-' : info.gwa}</span>
        </div>
        <button class="btn btn-sm secondary" data-edit-enrollment="${idx}" data-student-id="${st.id}" style="margin-bottom:0.5rem">Edit Enrollment</button>`;
      if (enrolledCourses.length) {
        html += tableHtml(
          ['Course Code', 'Units', 'Course Title', 'Faculty', 'Grade', 'Status'],
          enrolledCourses.map(ec => [
            ec.courseCode || '-',
            ec.units || '-',
            ec.courseTitle || '-',
            ec.facultyName || '-',
            ec.grade ?? '-',
            ec.status
          ])
        );
      } else {
        html += '<p style="font-size:0.85rem;color:#666">No courses enrolled.</p>';
      }
      html += '</div>';
    });
  }
  
  html += `<button class="btn btn-sm" id="addEnrollmentBtn" data-student-id="${st.id}" style="margin-top:0.5rem">+ Add Enrollment</button>`;
  
  document.getElementById('studentViewContent').innerHTML = html;
  document.getElementById('studentViewDialog').showModal();
  
  // Bind add enrollment button
  document.getElementById('addEnrollmentBtn').onclick = () => openAddEnrollment(st.id);
}

// Add enrollment dialog
async function openAddEnrollment(studentId) {
  await loadCurrentTerm();
  const form = document.getElementById('addEnrollmentForm');
  if (!form) return;
  // Fill academic year from current term only
  form.academicYear.value = currentTerm ? currentTerm.academicYear : '';
  form.enrollmentStatus.value = 'ACTIVE';
  form.scholasticStatus.value = 'REGULAR';
  
  // Populate schedule checkboxes (filtered by selected program, year level, semester)
  const container = document.getElementById('addEnrollmentScheduleCheckboxes');
  if (container) {
    // Populate year level options from program curriculum
    const updateYearLevelOptions = () => {
      const progId = Number(form.enrolledProgramId.value);
      const yearSelect = form.yearLevel;
      yearSelect.innerHTML = '<option value="">Select</option>';
      if (!progId) return;
      const program = LK.programs.find(p => p.id === progId);
      const activeCurr = (program?.curriculums || []).find(c => c.id === program.activeCurriculumId);
      const years = activeCurr?.years || [];
      years.forEach(y => {
        yearSelect.innerHTML += `<option value="${y.level}">Year ${y.level}</option>`;
      });
    };
    
    // Populate semester options based on selected year level
    const updateSemesterOptions = () => {
      const progId = Number(form.enrolledProgramId.value);
      const yearLevel = Number(form.yearLevel.value);
      const semSelect = form.semester;
      semSelect.innerHTML = '<option value="">Select</option>';
      if (!progId || !yearLevel) return;
      const program = LK.programs.find(p => p.id === progId);
      const activeCurr = (program?.curriculums || []).find(c => c.id === program.activeCurriculumId);
      const yearData = (activeCurr?.years || []).find(y => y.level === yearLevel);
      const totalSem = yearData?.totalSemester || 2;
      for (let s = 1; s <= totalSem; s++) {
        semSelect.innerHTML += `<option value="${s}">Semester ${s}</option>`;
      }
    };
    
    const updateSchedules = () => {
      const progId = Number(form.enrolledProgramId.value);
      const yearLevel = Number(form.yearLevel.value);
      const semester = Number(form.semester.value);
      const academicYear = form.academicYear.value;
      
      let filtered = (LK.schedules || []).filter(s => {
        // Filter by academic year if provided
        if (academicYear && s.academicYear !== academicYear) return false;
        // Filter by semester if provided
        if (semester && s.semester !== semester) return false;
        return true;
      });
      
      if (progId) {
        const program = LK.programs.find(p => p.id === progId);
        const curriculumIds = new Set((program?.curriculums || []).map(c => c.id));
        filtered = filtered.filter(s => curriculumIds.has(s.curriculumId));
      }
      
      // Filter by year level from course assignment
      if (yearLevel) {
        filtered = filtered.filter(s => s.yearLevel === yearLevel);
      }
      
      if (!filtered.length) {
        container.innerHTML = '<p style="font-size:0.85rem;color:#666">No schedules available for selected criteria.</p>';
      } else {
        container.innerHTML = filtered.map(s => `
          <label><input type="checkbox" name="scheduleIds" value="${s.id}">
            ${s.courseTitle} / ${s.sectionName || s.sectionId} ${fmtDays(s.days)}
          </label>`).join('');
      }
    };
    
    updateYearLevelOptions();
    updateSchedules();
    
    form.enrolledProgramId.onchange = () => {
      updateYearLevelOptions();
      updateSemesterOptions();
      updateSchedules();
    };
    form.yearLevel.onchange = () => {
      updateSemesterOptions();
      updateSchedules();
    };
    form.semester.onchange = updateSchedules;
  }
  
  document.getElementById('addEnrollmentSave').dataset.studentId = studentId;
  
  // Also set section filter to re-run after schedule filter changes program
  const updateSectionOptions = () => {
    const progId = Number(form.enrolledProgramId.value);
    if (!progId) {
      fillSelect(form.sectionId, LK.sections, 'id', sectLabel, form.sectionId.value);
    } else {
      const filtered = (LK.sections || []).filter(s => s.programId === progId);
      fillSelect(form.sectionId, filtered, 'id', sectLabel, form.sectionId.value);
    }
  };
  // Extend the schedule program change handler to also update sections
  const origProgChange = form.enrolledProgramId.onchange;
  form.enrolledProgramId.onchange = () => {
    if (origProgChange) origProgChange();
    updateSectionOptions();
  };
  updateSectionOptions();
  
  document.getElementById('addEnrollmentDialog').showModal();
}

document.getElementById('addEnrollmentForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    const f = e.target;
    const studentId = document.getElementById('addEnrollmentSave').dataset.studentId;
    const st = await api('/admin/students/' + studentId);
    const infos = st.academicInfo || [];
    const scheduleIds = [...document.querySelectorAll('#addEnrollmentScheduleCheckboxes input[name="scheduleIds"]:checked')].map(cb => Number(cb.value));
    if (!scheduleIds.length) throw new Error('Select at least one schedule');
    
    const prog = await api('/admin/programs/' + f.enrolledProgramId.value);
    if (!prog.activeCurriculumId) throw new Error('Selected program has no active curriculum');
    
    const info = {
      enrolledProgramId: Number(f.enrolledProgramId.value),
      programCurriculumId: prog.activeCurriculumId,
      sectionId: Number(f.sectionId.value),
      academicYear: f.academicYear.value,
      semester: Number(f.semester.value),
      yearLevel: Number(f.yearLevel.value),
      units: 0,
      enrollmentDate: new Date().toISOString().slice(0, 10),
      scholasticStatus: f.scholasticStatus.value,
      enrollmentStatus: f.enrollmentStatus.value,
      enrolledCourses: scheduleIds.map(scheduleId => ({ scheduleId, status: 'ONGOING' }))
    };
    infos.push(info);
    st.academicInfo = infos;
    await api('/admin/students/' + studentId, { method: 'PUT', body: JSON.stringify(st) });
    showMsg(msg, 'Enrollment added', 'ok');
    document.getElementById('addEnrollmentDialog').close();
    showStudentDetail(studentId);
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
};

// Enrollment edit handler
document.body.addEventListener('click', async e => {
  const editBtn = e.target.closest('[data-edit-enrollment]');
  if (!editBtn) return;
  const studentId = editBtn.dataset.studentId;
  const idx = Number(editBtn.dataset.editEnrollment);
  const st = await api('/admin/students/' + studentId);
  const info = (st.academicInfo || [])[idx];
  if (!info) return;
  
  const form = document.getElementById('enrollmentEditForm');
  form.academicYear.value = info.academicYear || '';
  form.semester.value = info.semester || '';
  form.yearLevel.value = info.yearLevel || '';
  form.enrollmentStatus.value = info.enrollmentStatus || 'ACTIVE';
  form.scholasticStatus.value = info.scholasticStatus || 'REGULAR';
  form.sectionId.value = info.sectionId || '';
  form.enrolledProgramId.value = info.enrolledProgramId || '';
  
  // Bind section filter for edit enrollment form
  const editProgSelect = form.enrolledProgramId;
  const editSectSelect = form.sectionId;
  if (editProgSelect && editSectSelect) {
    editProgSelect.onchange = () => {
      const progId = Number(editProgSelect.value);
      if (!progId) {
        fillSelect(editSectSelect, LK.sections, 'id', sectLabel, '');
      } else {
        const filtered = (LK.sections || []).filter(s => s.programId === progId);
        fillSelect(editSectSelect, filtered, 'id', sectLabel, '');
      }
    };
  }
  
  // Build the schedule checkboxes based on saved enrollment info
  const buildEditEnrollmentSchedules = () => {
    const progId = Number(form.enrolledProgramId.value);
    const yearLevel = Number(form.yearLevel.value);
    const semester = Number(form.semester.value);
    const academicYear = form.academicYear.value;
    const container = document.getElementById('editEnrollmentScheduleCheckboxes');
    if (!container) return;
    
    let filtered = (LK.schedules || []).filter(s => {
      if (academicYear && s.academicYear !== academicYear) return false;
      if (semester && s.semester !== semester) return false;
      return true;
    });
    if (progId) {
      const program = LK.programs.find(p => p.id === progId);
      const curriculumIds = new Set((program?.curriculums || []).map(c => c.id));
      filtered = filtered.filter(s => curriculumIds.has(s.curriculumId));
    }
    if (yearLevel) filtered = filtered.filter(s => s.yearLevel === yearLevel);
    if (semester) filtered = filtered.filter(s => s.semester === semester);
    
    const prevSelected = new Set((info.enrolledCourses || []).map(ec => ec.scheduleId));
    
    container.innerHTML = filtered.length ? filtered.map(s => `
      <label><input type="checkbox" name="scheduleIds" value="${s.id}"${prevSelected.has(s.id) ? ' checked' : ''}>
        ${s.courseTitle} / ${s.sectionName || s.sectionId} ${fmtDays(s.days)}
      </label>`).join('') :
      '<p style="font-size:0.85rem;color:#666">No schedules available for selected criteria.</p>';
  };
  
  // Rebuild year level and semester dropdowns from program curriculum
  const rebuildYearSemesterDropdowns = () => {
    const progId = Number(form.enrolledProgramId.value);
    const yearSelect = form.yearLevel;
    const semSelect = form.semester;
    const savedYear = info.yearLevel;
    const savedSem = info.semester;
    
    yearSelect.innerHTML = '<option value="">Select</option>';
    if (progId) {
      const program = LK.programs.find(p => p.id === progId);
      const activeCurr = (program?.curriculums || []).find(c => c.id === program.activeCurriculumId);
      const years = activeCurr?.years || [];
      years.forEach(y => {
        const sel = y.level === savedYear ? ' selected' : '';
        yearSelect.innerHTML += `<option value="${y.level}"${sel}>Year ${y.level}</option>`;
      });
    }
    
    const currentYear = savedYear || Number(yearSelect.value);
    semSelect.innerHTML = '<option value="">Select</option>';
    if (progId && currentYear) {
      const program = LK.programs.find(p => p.id === progId);
      const activeCurr = (program?.curriculums || []).find(c => c.id === program.activeCurriculumId);
      const yearData = (activeCurr?.years || []).find(y => y.level === currentYear);
      const totalSem = yearData?.totalSemester || 2;
      for (let s = 1; s <= totalSem; s++) {
        const sel = s === savedSem ? ' selected' : '';
        semSelect.innerHTML += `<option value="${s}"${sel}>Semester ${s}</option>`;
      }
    }
  };
  
  rebuildYearSemesterDropdowns();
  buildEditEnrollmentSchedules();
  
  // Attach dynamic update handlers
  form.enrolledProgramId.onchange = () => {
    rebuildYearSemesterDropdowns();
    buildEditEnrollmentSchedules();
    // Also update section options
    const progId = Number(form.enrolledProgramId.value);
    if (!progId) {
      fillSelect(form.sectionId, LK.sections, 'id', sectLabel, '');
    } else {
      const filtered = (LK.sections || []).filter(s => s.programId === progId);
      fillSelect(form.sectionId, filtered, 'id', sectLabel, form.sectionId.value);
    }
  };
  form.yearLevel.onchange = () => {
    rebuildYearSemesterDropdowns();
    buildEditEnrollmentSchedules();
  };
  form.semester.onchange = buildEditEnrollmentSchedules;
  
  document.getElementById('enrollmentEditTitle').textContent = `Edit Enrollment - ${info.academicYear} Sem ${info.semester}`;
  document.getElementById('enrollmentEditDialog').showModal();
  
  // Store for save
  document.getElementById('enrollmentEditSave').dataset.studentId = studentId;
  document.getElementById('enrollmentEditSave').dataset.infoIdx = idx;
});

document.getElementById('enrollmentEditForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    const saveBtn = document.getElementById('enrollmentEditSave');
    const studentId = saveBtn.dataset.studentId;
    const idx = Number(saveBtn.dataset.infoIdx);
    const st = await api('/admin/students/' + studentId);
    const info = (st.academicInfo || [])[idx];
    if (!info) throw new Error('Enrollment not found');
    
    const f = e.target;
    info.academicYear = f.academicYear.value;
    info.semester = Number(f.semester.value);
    info.yearLevel = Number(f.yearLevel.value);
    info.enrollmentStatus = f.enrollmentStatus.value;
    info.scholasticStatus = f.scholasticStatus.value;
    info.sectionId = Number(f.sectionId.value);
    info.enrolledProgramId = Number(f.enrolledProgramId.value);
    
    // Update curriculum if program changed
    if (info.enrolledProgramId) {
      const prog = await api('/admin/programs/' + info.enrolledProgramId);
      info.programCurriculumId = prog.activeCurriculumId;
    }
    
    // Update enrolled courses from checked schedule checkboxes
    const scheduleIds = [...document.querySelectorAll('#editEnrollmentScheduleCheckboxes input[name="scheduleIds"]:checked')].map(cb => Number(cb.value));
    if (!scheduleIds.length) throw new Error('Select at least one schedule');
    
    // Keep existing enrolled courses that are still selected, add new ones
    const existingScheduleIds = new Set((info.enrolledCourses || []).map(ec => ec.scheduleId));
    const newScheduleIds = scheduleIds.filter(id => !existingScheduleIds.has(id));
    const removedScheduleIds = (info.enrolledCourses || []).filter(ec => !scheduleIds.includes(ec.scheduleId)).map(ec => ec.scheduleId);
    
    // Remove courses that were unchecked
    info.enrolledCourses = (info.enrolledCourses || []).filter(ec => scheduleIds.includes(ec.scheduleId));
    
    // Add new courses
    newScheduleIds.forEach(scheduleId => {
      info.enrolledCourses.push({ scheduleId, status: 'ONGOING' });
    });
    
    st.academicInfo[idx] = info;
    const updated = await api('/admin/students/' + studentId, { method: 'PUT', body: JSON.stringify(st) });
    
    // Delete grade records for removed schedules
    if (removedScheduleIds.length) {
      for (const removedId of removedScheduleIds) {
        try {
          await api('/admin/students/' + studentId + '/grade-records/' + removedId, { method: 'DELETE' });
        } catch (e) { /* ignore if no grade record exists */ }
      }
    }
    
    showMsg(msg, 'Enrollment updated', 'ok');
    document.getElementById('enrollmentEditDialog').close();
    showStudentDetail(studentId);
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
};

async function openProgramManage(programId, title) {
  managedProgram = await api('/admin/programs/' + programId);
  document.getElementById('programManageTitle').textContent = (title || managedProgram.title) + ' — Curriculums';
  renderCurriculumTable();
  document.getElementById('programManageDialog').showModal();
}

function renderCurriculumTable() {
  const rows = (managedProgram.curriculums || []).map(c => [
    c.code, c.effectiveYear, c.durationYear,
    managedProgram.activeCurriculumId === c.id ? 'Active' : `<button class="btn btn-sm" data-set-active="${c.id}">Set Active</button>`,
    `<button class="btn btn-sm" data-view-courses="${c.id}">View Courses</button>
     <button class="btn btn-sm secondary" data-edit-curriculum="${c.id}">Edit</button>`
  ]);
  document.getElementById('curriculumTable').innerHTML = tableHtml(
    ['Code', 'Effective Year', 'Duration', 'Status', ''], rows);
}

async function showCurriculumCourses(curriculumId) {
  if (!managedProgram) return;
  const curriculum = (managedProgram.curriculums || []).find(c => c.id == curriculumId);
  if (!curriculum) return;
  
  document.getElementById('curriculumCoursesTitle').textContent = 
    `Courses in ${curriculum.code || curriculum.id} (${curriculum.effectiveYear || ''})`;
  
  // Build semester filter controls
  const years = curriculum.years || [];
  let html = '<div class="filters"><div class="grid-3">';
  html += `<div><label>Year Level</label><select id="ccYearLevel">`;
  years.forEach(y => {
    html += `<option value="${y.level}">Year ${y.level}</option>`;
  });
  html += `</select></div>`;
  html += `<div><label>Semester</label><select id="ccSemester">`;
  // Default to 2 semesters if no years data
  const totalSem = years.length > 0 ? years[0].totalSemester : 2;
  for (let s = 1; s <= totalSem; s++) {
    html += `<option value="${s}">Semester ${s}</option>`;
  }
  html += `</select></div>`;
  html += '</div></div>';
  html += '<div id="ccResults"></div>';
  
  document.getElementById('curriculumCoursesContent').innerHTML = html;
  
  const year = document.getElementById('ccYearLevel');
  const sem = document.getElementById('ccSemester');

  year.onchange = () => loadCurrCourses(curriculumId);
  sem.onchange = () => loadCurrCourses(curriculumId);
  
  await loadCurrCourses(curriculumId);

  document.getElementById('curriculumCoursesDialog').showModal();
}

async function loadCurrCourses(curriculumId){
  const yearLevel = document.getElementById('ccYearLevel').value;
  const semester = document.getElementById('ccSemester').value;
  try {
    const list = await api(`/admin/programs/${managedProgram.id}/curriculum/${curriculumId}/courses?yearLevel=${yearLevel}&semester=${semester}`);
    document.getElementById('ccResults').innerHTML = tableHtml(
      ['Code', 'Title', 'Units', 'Prerequisites'],
      (list || []).map(c => [
        c.code,
        c.title,
        c.units,
        (c.prerequisiteIds || []).map(id => {
          const prereq = LK.courses.find(pc => pc.id === id);
          return prereq ? prereq.code : id;
        }).join(', ') || '-'
      ]));
  } catch (err) {
    document.getElementById('ccResults').innerHTML = `<p class="err">${err.message}</p>`;
  }
}

async function editCurriculum(curriculumId) {
  if (!managedProgram) return;
  const curriculum = (managedProgram.curriculums || []).find(c => c.id == curriculumId);
  if (!curriculum) return;
  
  const dialog = document.getElementById('curriculumDialog');
  const form = document.getElementById('curriculumForm');
  
  // Set edit state
  editState.type = 'curriculum';
  editState.id = curriculumId;
  editState.data = curriculum;
  
  // Populate form
  form.effectiveYear.value = curriculum.effectiveYear || '';
  form.setActive.value = 'no';
  
  const yearCount = curriculum.years ? curriculum.years.length : 4;
  form.yearCount.value = yearCount;
  renderYearSemesterFields('curriculumYearSemesterFields', yearCount);
  
  if (curriculum.years) {
    curriculum.years.forEach(y => {
      const input = form.querySelector(`[name="yearSemester_${y.level}"]`);
      if (input) input.value = y.totalSemester || 2;
    });
  }
  
  // Update buttons
  document.getElementById('curriculumSubmitBtn').textContent = 'Update Curriculum';
  
  dialog.showModal();
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
      } else if (dialogId === 'studentDialog') {
        // Ensure add-student state: show academic info, hide edit-only fields
        const academicInfoFields = document.getElementById('studentAcademicInfoFields');
        if (academicInfoFields) academicInfoFields.style.display = '';
        const editFields = document.getElementById('studentEditFields');
        if (editFields) editFields.classList.add('hidden');
        populateScheduleCheckboxes();
      }
      
      // Open this dialog
      dialog.showModal();
      
      // Re-initialize dynamic elements (only for add mode, not edit)
      const form = dialog.querySelector('form');
      if (form) {
        setTimeout(() => {
          if (!editId) {
            if (form.querySelector('[data-repeater="assignment"]')) {
              initRepeater('courseAssignmentRows', assignmentRowHtml, 1);
            }
            if (form.querySelector('[data-repeater="day"]')) {
              initRepeater('scheduleDayRows', dayRowHtml, 1);
            }
            const facultyCourses = form.querySelector('#facultyQualifiedCourses');
            if (facultyCourses) facultyCourses.innerHTML = qualifiedCourseCheckboxHtml();
            const coursePrereqs = form.querySelector('#coursePrereqs');
            if (coursePrereqs) coursePrereqs.innerHTML = qualifiedCourseCheckboxHtml();
          }
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
  
  // Hide initial academic info fields when editing
  const academicInfoFields = document.getElementById('studentAcademicInfoFields');
  if (academicInfoFields) academicInfoFields.style.display = 'none';
  
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
    facultyCourses.innerHTML = qualifiedCourseCheckboxHtml(data.qualifiedCoursesIds);
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
    prereqs.innerHTML = qualifiedCourseCheckboxHtml(data.prerequisiteIds, data.id);
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
  form.academicYear.value = data.academicYear || '';
  form.status.value = data.status || 'ACTIVE';
  form.notes.value = data.notes || '';
  
  // The days array from API uses 'timeStart'/'timeEnd', dayRowHtml expects 'startTime'/'endTime'
  const days = (data.days || []).map(d => ({
    name: d.name,
    startTime: d.timeStart || d.startTime || '',
    endTime: d.timeEnd || d.endTime || ''
  }));
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
  
  // When editing, hide the initial curriculum section; when adding, show it
  const initialCurriculumSection = form.querySelector('.section-title');
  if (initialCurriculumSection && initialCurriculumSection.textContent.includes('Initial Curriculum')) {
    if (editState.id) {
      initialCurriculumSection.style.display = 'none';
      const effectiveYearField = form.querySelector('[name="effectiveYear"]');
      const yearCountField = form.querySelector('[name="yearCount"]');
      const yearSemesterFields = document.getElementById('yearSemesterFields');
      if (effectiveYearField) effectiveYearField.closest('div').style.display = 'none';
      if (yearCountField) yearCountField.closest('div').style.display = 'none';
      if (yearSemesterFields) yearSemesterFields.style.display = 'none';
    } else {
      initialCurriculumSection.style.display = '';
      const effectiveYearField = form.querySelector('[name="effectiveYear"]');
      const yearCountField = form.querySelector('[name="yearCount"]');
      const yearSemesterFields = document.getElementById('yearSemesterFields');
      if (effectiveYearField) effectiveYearField.closest('div').style.display = '';
      if (yearCountField) yearCountField.closest('div').style.display = '';
      if (yearSemesterFields) yearSemesterFields.style.display = '';
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
  form.password.placeholder = data.password ? 'Leave blank to keep current' : '';
}

// Close dialogs using native API - also clears edit state
document.body.addEventListener('click', e => {
  const closeBtn = e.target.closest('[data-close-dialog]');
  if (closeBtn) {
    const dialogId = closeBtn.dataset.closeDialog;
    const dialog = document.getElementById(dialogId);
    if (dialog) {
      dialog.close();
      // Clear edit state for the associated form type
      const typeMap = {
        studentDialog: 'student',
        facultyDialog: 'faculty',
        courseDialog: 'course',
        scheduleDialog: 'schedule',
        sectionDialog: 'section',
        programDialog: 'program',
        deptDialog: 'department',
        accountDialog: 'account',
        curriculumDialog: 'curriculum'
      };
      const type = typeMap[dialogId];
      if (type && editState.type === type) {
        clearEdit(type);
      }
    }
  }
});

// Show schedule filter function - filters schedules by current term's academic year/semester
// Populate schedule checkboxes for student form (better multi-select UI)
function populateScheduleCheckboxes() {
  const container = document.getElementById('scheduleCheckboxes');
  if (!container) return;
  
  const updateSchedules = () => {
    const form = document.forms.studentForm;
    const progId = Number(form?.enrolledProgramId?.value);
    const yearLevel = Number(form?.yearLevel?.value);
    const semester = Number(form?.semester?.value);
    const academicYear = form?.academicYear?.value;
    
    let filtered = (LK.schedules || []).filter(s => {
      if (academicYear && s.academicYear !== academicYear) return false;
      if (semester && s.semester !== semester) return false;
      return true;
    });
    
    if (progId) {
      const program = LK.programs.find(p => p.id === progId);
      const curriculumIds = new Set((program?.curriculums || []).map(c => c.id));
      filtered = filtered.filter(s => curriculumIds.has(s.curriculumId));
    }
    if (yearLevel) filtered = filtered.filter(s => s.yearLevel === yearLevel);
    if (semester) filtered = filtered.filter(s => s.semester === semester);
    
    if (!filtered.length) {
      container.innerHTML = '<p style="font-size:0.85rem;color:#666">No schedules available for selected criteria.</p>';
    } else {
      container.innerHTML = filtered.map(s => `
        <label><input type="checkbox" name="scheduleIds" value="${s.id}">
          ${s.courseTitle} / ${s.sectionName || s.sectionId} ${fmtDays(s.days)}
        </label>`).join('');
    }
  };
  
  // Populate year level options
  const yearSelect = document.forms.studentForm?.yearLevel;
  const updateYearLevel = () => {
    const progId = Number(document.forms.studentForm?.enrolledProgramId?.value);
    if (!yearSelect || !progId) return;
    const program = LK.programs.find(p => p.id === progId);
    const activeCurr = (program?.curriculums || []).find(c => c.id === program.activeCurriculumId);
    const years = activeCurr?.years || [];
    yearSelect.innerHTML = '<option value="">Select</option>';
    years.forEach(y => {
      yearSelect.innerHTML += `<option value="${y.level}">Year ${y.level}</option>`;
    });
  };
  
  // Populate semester options
  const semSelect = document.forms.studentForm?.semester;
  const updateSemester = () => {
    const progId = Number(document.forms.studentForm?.enrolledProgramId?.value);
    const yearLevel = Number(document.forms.studentForm?.yearLevel?.value);
    if (!semSelect || !progId || !yearLevel) return;
    const program = LK.programs.find(p => p.id === progId);
    const activeCurr = (program?.curriculums || []).find(c => c.id === program.activeCurriculumId);
    const yearData = (activeCurr?.years || []).find(y => y.level === yearLevel);
    const totalSem = yearData?.totalSemester || 2;
    semSelect.innerHTML = '<option value="">Select</option>';
    for (let s = 1; s <= totalSem; s++) {
      semSelect.innerHTML += `<option value="${s}">Semester ${s}</option>`;
    }
  };
  
  // Re-initialize as dropdowns
  const form = document.forms.studentForm;
  if (form && form.yearLevel && form.semester) {
    // Add onchange handlers
    form.enrolledProgramId?.addEventListener('change', () => {
      updateYearLevel();
      updateSemester();
      updateSchedules();
    });
    form.yearLevel?.addEventListener('change', () => {
      updateSemester();
      updateSchedules();
    });
    form.semester?.addEventListener('change', updateSchedules);
  }
  
  updateYearLevel();
  updateSemester();
  updateSchedules();
}

// Get selected schedule IDs from checkboxes
function getSelectedScheduleIds() {
  return [...document.querySelectorAll('#scheduleCheckboxes input[name="scheduleIds"]:checked')].map(cb => Number(cb.value));
}

function filterSchedulesByTerm() {
  const scheduleSelect = document.querySelector('[data-lookup="schedule"]');
  if (!scheduleSelect || !currentTerm) return;
  const filtered = (LK.schedules || []).filter(s =>
    s.academicYear === currentTerm.academicYear && s.semester === currentTerm.semester
  );
  fillSelect(scheduleSelect, filtered, 'id', schedLabel, scheduleSelect.value);
}

// Filter curriculum in assignment rows based on program selection
function bindCourseProgramFilter() {
  document.body.addEventListener('change', e => {
    const row = e.target.closest('[data-repeater="assignment"]');
    if (!row) return;
    const progSelect = row.querySelector('select[name="programId"]');
    const currSelect = row.querySelector('select[name="curriculumId"]');
    if (e.target === progSelect && currSelect) {
      const progId = Number(progSelect.value);
      if (!progId) {
        fillSelect(currSelect, LK.curriculums, 'id', currLabel, '');
      } else {
        const filtered = (LK.curriculums || []).filter(c => c.programId === progId);
        fillSelect(currSelect, filtered, 'id', currLabel, '');
      }
    }
  });
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
      // For edit: keep original academicInfo, don't include hidden fields
      const body = {
        ...editState.data,
        studentStatus: f.studentStatus.value,
        scholasticStatus: f.scholasticStatus.value,
        account: { ...editState.data.account, ...buildAccountFromForm(f, 'STUDENT') },
        profile: buildProfileFromForm(f)
      };
      // Preserve original academicInfo since those fields are hidden
      body.academicInfo = editState.data.academicInfo;
      await api('/admin/students/' + editState.id, { method: 'PUT', body: JSON.stringify(body) });
      showMsg(msg, 'Student updated', 'ok');
      clearEdit('student');
      document.getElementById('studentDialog').close();
      loadStudents();
      return;
    }
    const today = new Date().toISOString().slice(0, 10);
    const prog = await api('/admin/programs/' + f.enrolledProgramId.value);
    const scheduleIds = getSelectedScheduleIds();
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
    if (!body.academicInfo[0].yearLevel) throw new Error('Select year level');
    if (!body.academicInfo[0].semester) throw new Error('Select semester');
    await api('/admin/students/add', { method: 'POST', body: JSON.stringify(body) });
    showMsg(msg, 'Student added', 'ok');
    f.reset();
    applyTermToForms();
    document.getElementById('studentDialog').close();
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
      qualifiedCoursesIds: getCheckboxValues(document.getElementById('facultyQualifiedCourses')),
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
      document.getElementById('facultyDialog').close();
    }
    loadFaculty();
    await loadLookups();
    fillLookupSelects();
    document.getElementById('facultyQualifiedCourses').innerHTML = qualifiedCourseCheckboxHtml();
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
      prerequisiteIds: getCheckboxValues(document.getElementById('coursePrereqs')),
      courseAssignments: assignments
    };
    if (editState.type === 'course' && editState.id) {
      await api('/admin/courses/' + editState.id, { method: 'PUT', body: JSON.stringify({ ...editState.data, ...body }) });
      showMsg(msg, 'Course updated', 'ok');
      clearEdit('course');
      document.getElementById('courseDialog').close();
    } else {
      await api('/admin/courses/add', { method: 'POST', body: JSON.stringify(body) });
      showMsg(msg, 'Course added', 'ok');
      f.reset();
      initRepeater('courseAssignmentRows', assignmentRowHtml, 1);
      document.getElementById('courseDialog').close();
    }
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
      academicYear: currentTerm.academicYear,
      status: f.status.value,
      notes: f.notes.value || null,
      days
    };
    if (editState.type === 'schedule' && editState.id) {
      await api('/admin/schedules/' + editState.id, { method: 'PUT', body: JSON.stringify({ ...editState.data, ...body }) });
      showMsg(msg, 'Schedule updated', 'ok');
      clearEdit('schedule');
      document.getElementById('scheduleDialog').close();
    } else {
      await api('/admin/schedules/add', { method: 'POST', body: JSON.stringify(body) });
      showMsg(msg, 'Schedule added', 'ok');
      f.reset();
      applyTermToForms();
      initRepeater('scheduleDayRows', dayRowHtml, 1);
      document.getElementById('scheduleDialog').close();
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
      document.getElementById('sectionDialog').close();
    } else {
      await api('/admin/sections/add', { method: 'POST', body: JSON.stringify(body) });
      showMsg(msg, 'Section added', 'ok');
      e.target.reset();
      document.getElementById('sectionDialog').close();
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
      document.getElementById('deptDialog').close();
    } else {
      await api('/admin/departments/add', { method: 'POST', body: JSON.stringify(body) });
      showMsg(msg, 'Department added', 'ok');
      e.target.reset();
      document.getElementById('deptDialog').close();
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
    const f = formToObj(e.target);
    // Don't send empty password on update
    const body = editState.type === 'account' && editState.id && !f.password
      ? { username: f.username }
      : f;
    if (editState.type === 'account' && editState.id) {
      await api('/admin/accounts/' + editState.id, { method: 'PUT', body: JSON.stringify(body) });
      showMsg(msg, 'Admin updated', 'ok');
      clearEdit('account');
      document.getElementById('accountDialog').close();
    } else {
      if (!f.password) throw new Error('Password is required');
      await api('/admin/accounts/add', { method: 'POST', body: JSON.stringify(body) });
      showMsg(msg, 'Admin added', 'ok');
      e.target.reset();
      document.getElementById('accountDialog').close();
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
    // For edit: keep original curriculums since initial curriculum fields are hidden
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
    document.getElementById('programDialog').close();
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
    document.getElementById('programDialog').close();
    loadPrograms();
    await loadLookups();
    fillLookupSelects();
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
      const semKey = 'yearSemester_' + i;
      years.push({ level: i, totalSemester: f[semKey] || 2 });
    }
    let curriculums = [...(managedProgram.curriculums || [])];
    
    if (editState.type === 'curriculum' && editState.id) {
      // Update existing curriculum
      const idx = curriculums.findIndex(c => c.id == editState.id);
      if (idx >= 0) {
        curriculums[idx] = { ...curriculums[idx], effectiveYear: f.effectiveYear, years };
      }
    } else {
      // Add new curriculum
      curriculums.push({ effectiveYear: f.effectiveYear, years });
    }
    
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
    
    if (editState.type !== 'curriculum' && f.setActive === 'yes') {
      const newest = managedProgram.curriculums[managedProgram.curriculums.length - 1];
      body.activeCurriculumId = newest.id;
      body.curriculums = managedProgram.curriculums;
      managedProgram = await api('/admin/programs/' + managedProgram.id, { method: 'PUT', body: JSON.stringify(body) });
    }
    
    const action = editState.type === 'curriculum' ? 'updated' : 'added';
    showMsg(msg, `Curriculum ${action}`, 'ok');
    clearEdit('curriculum');
    document.getElementById('curriculumDialog').close();
    renderCurriculumTable();
    e.target.reset();
    loadPrograms();
  } catch (err) { showMsg(msg, err.message, 'err'); }
};

// Handlers for curriculum view courses and edit buttons
document.body.addEventListener('click', async e => {
  const viewBtn = e.target.closest('[data-view-courses]');
  if (viewBtn && managedProgram) {
    showCurriculumCourses(viewBtn.dataset.viewCourses);
    return;
  }
  const editCurrBtn = e.target.closest('[data-edit-curriculum]');
  if (editCurrBtn && managedProgram) {
    editCurriculum(editCurrBtn.dataset.editCurriculum);
    return;
  }
});

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
  courses: async () => { await loadLookups(); fillLookupSelects(); initRepeater('courseAssignmentRows', assignmentRowHtml, 1); document.getElementById('coursePrereqs').innerHTML = qualifiedCourseCheckboxHtml(); loadCourses(); bindCourseProgramFilter(); },
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
