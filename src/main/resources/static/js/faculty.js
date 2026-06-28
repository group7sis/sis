const user = requireRole('FACULTY', 'portal-login.html');
if (!user) throw new Error('no session');

const id = user.id;
const base = '/faculty/' + id;
let activeRecordId = null;
let termClosed = false;

document.getElementById('userLabel').textContent = user.name;
bindLogout();

const msg = document.getElementById('msg');

const days = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];

async function loadCurrentTerm() {
  try {
    const term = await api('/auth/term/current');
    termClosed = term.status === 'CLOSED';
    return term;
  } catch {
    termClosed = false;
    return null;
  }
}

function applyGradeTermLock() {
  const notice = document.getElementById('gradeTermNotice');
  const controls = document.getElementById('gradeControls');
  if (termClosed) {
    notice.classList.remove('hidden');
    notice.innerHTML = '<div class="msg info">Grade encoding is unavailable because the term status is CLOSED.</div>';
    controls.classList.add('hidden');
    document.getElementById('gradeStudentTable').innerHTML = '';
    document.getElementById('gradeFormWrap').classList.add('hidden');
  } else {
    notice.classList.add('hidden');
    controls.classList.remove('hidden');
  }
}

async function loadDashboard() {
  const d = await api(base + '/dashboard');
  const s = d.stats || {};
  document.getElementById('dashContent').innerHTML = `
    <p style="margin-bottom:0.5rem">Hello, <strong>${d.facultyName || user.name}</strong></p>
    <div class="stats">
      ${box('Courses', s.totalCoursesHandled)}
      ${box('Sections', s.totalSections)}
      ${box('Students', s.totalStudents)}
      ${box('Schedules', s.totalSchedules)}
    </div>
    <p style="font-size:0.85rem;margin:0.5rem 0">${d.currentDayOfTheWeek || ''}, ${d.currentMonth || ''} ${d.currentDay || ''}</p>
    <div id="dashSched"></div>`;
  function box(label, val) {
    return `<div class="stat"><strong>${val ?? 0}</strong><span>${label}</span></div>`;
  }
  document.getElementById('dashSched').innerHTML = tableHtml(
    ['Course', 'Section', 'Days'],
    (d.schedules || []).map(x => [x.courseTitle, x.sectionName, fmtDays(x.days)])
  );
}

async function loadSchedules() {
  const data = await api(base + '/schedules');
  let html = '';
  days.forEach(day => {
    const list = data[day] || [];
    html += `<div class="section-title">${day.charAt(0).toUpperCase() + day.slice(1)}</div>`;
    html += tableHtml(['Course', 'Section', 'Time'],
      list.map(s => [s.courseTitle, s.sectionName, fmtDays(s.days)]));
  });
  document.getElementById('schedContent').innerHTML = html;
}

async function loadClassCourses() {
  const courses = await api(base + '/records/courses');
  const sel = document.getElementById('classCourseId');
  sel.innerHTML = '<option value="">Select course</option>' +
    courses.map(c => `<option value="${c.id}">${c.code} - ${c.title}</option>`).join('');
}

document.getElementById('classCourseId').onchange = async () => {
  const courseId = document.getElementById('classCourseId').value;
  const secSel = document.getElementById('classSectionId');
  secSel.innerHTML = '<option value="">Select section</option>';
  if (!courseId) return;
  const sections = await api(`${base}/records/courses/${courseId}/sections`);
  secSel.innerHTML += sections.map(s => {
    const sec = s.section || {};
    return `<option value="${sec.id}">${sec.name} (${s.totalStudents} students)</option>`;
  }).join('');
};

document.getElementById('classLoadBtn').onclick = async () => {
  const courseId = document.getElementById('classCourseId').value;
  const sectionId = document.getElementById('classSectionId').value;
  const name = document.getElementById('className').value.trim();
  if (!courseId || !sectionId) {
    showMsg(msg, 'Select course and section', 'err');
    return;
  }
  try {
    hideMsg(msg);
    const q = name ? '?name=' + encodeURIComponent(name) : '';
    const list = await api(`${base}/classes/courses/${courseId}/sections/${sectionId}/students` + q);
    document.getElementById('classTable').innerHTML = tableHtml(
      ['Student #', 'Name', 'Status'],
      (list || []).map(st => [st.studentNumber, fmtName(st.profile), st.studentStatus])
    );
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
};

async function loadGradeCourses() {
  await loadCurrentTerm();
  applyGradeTermLock();
  if (termClosed) return;
  const courses = await api(base + '/records/courses');
  const sel = document.getElementById('gradeCourse');
  sel.innerHTML = '<option value="">Select course</option>' +
    courses.map(c => `<option value="${c.id}">${c.code} - ${c.title}</option>`).join('');
}

document.getElementById('gradeCourse').onchange = async () => {
  if (termClosed) return;
  const courseId = document.getElementById('gradeCourse').value;
  const secSel = document.getElementById('gradeSection');
  secSel.innerHTML = '<option value="">Select section</option>';
  document.getElementById('gradeStudentTable').innerHTML = '';
  document.getElementById('gradeFormWrap').classList.add('hidden');
  if (!courseId) return;
  const sections = await api(`${base}/records/courses/${courseId}/sections`);
  secSel.innerHTML += sections.map(s => {
    const sec = s.section || {};
    return `<option value="${sec.id}">${sec.name} (${s.totalStudents} students)</option>`;
  }).join('');
};

document.getElementById('gradeLoadBtn').onclick = async () => {
  if (termClosed) return;
  const courseId = document.getElementById('gradeCourse').value;
  const sectionId = document.getElementById('gradeSection').value;
  if (!courseId || !sectionId) {
    showMsg(msg, 'Select course and section', 'err');
    return;
  }
  try {
    hideMsg(msg);
    const list = await api(`${base}/records/courses/${courseId}/sections/${sectionId}/students`);
    document.getElementById('gradeStudentTable').innerHTML = tableHtml(
      ['Student', 'Course', 'Final Grade', 'Finalized', ''],
      (list || []).map(r => [
        r.studentName, r.courseName, r.finalGrade ?? '-', r.finalized ? 'Yes' : 'No',
        `<button class="btn btn-sm" data-record="${r.id}">Encode</button>`
      ])
    );
    document.getElementById('gradeFormWrap').classList.add('hidden');
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
};

document.getElementById('gradeStudentTable').onclick = async e => {
  if (termClosed) return;
  const btn = e.target.closest('[data-record]');
  if (!btn) return;
  activeRecordId = btn.dataset.record;
  const page = await api(`${base}/records/encode/${activeRecordId}`);
  const r = page.studentGradeRecord || {};
  document.getElementById('gradeStudentLabel').textContent = `${r.studentName} - ${r.courseName}`;
  const form = document.getElementById('gradeForm');
  ['assignments', 'quizzes', 'activities', 'finalExam', 'calculatedGrade', 'finalGrade']
    .forEach(k => { form[k].value = r[k] ?? ''; });
  document.getElementById('gradeFormWrap').classList.remove('hidden');
};

document.getElementById('computeBtn').onclick = async () => {
  if (!activeRecordId || termClosed) return;
  try {
    hideMsg(msg);
    const form = document.getElementById('gradeForm');
    const body = {
      assignments: num(form.assignments.value),
      quizzes: num(form.quizzes.value),
      activities: num(form.activities.value),
      finalExam: num(form.finalExam.value),
      finalGrade: num(form.finalGrade.value)
    };
    await api(`${base}/records/encode/${activeRecordId}`, { method: 'PUT', body: JSON.stringify(body) });
    const computed = await api(`${base}/records/encode/${activeRecordId}/compute`);
    form.calculatedGrade.value = computed.calculatedGrade ?? '';
    form.finalGrade.value = computed.finalGrade ?? '';
    showMsg(msg, 'Grade computed', 'ok');
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
};

document.getElementById('gradeForm').onsubmit = async e => {
  e.preventDefault();
  if (!activeRecordId || termClosed) return;
  try {
    hideMsg(msg);
    const form = e.target;
    const body = {
      assignments: num(form.assignments.value),
      quizzes: num(form.quizzes.value),
      activities: num(form.activities.value),
      finalExam: num(form.finalExam.value),
      finalGrade: num(form.finalGrade.value),
      finalized: true
    };
    await api(`${base}/records/encode/${activeRecordId}`, { method: 'PUT', body: JSON.stringify(body) });
    showMsg(msg, 'Grade saved', 'ok');
    document.getElementById('gradeLoadBtn').click();
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
};

function num(v) {
  return v === '' || v == null ? null : Number(v);
}

async function loadFormula() {
  const f = await api(base + '/grade-formula');
  const form = document.getElementById('formulaForm');
  ['assignmentWeight', 'quizWeight', 'activityWeight', 'finalExamWeight']
    .forEach(k => { form[k].value = f[k] ?? ''; });
}

document.getElementById('formulaForm').onsubmit = async e => {
  e.preventDefault();
  try {
    hideMsg(msg);
    const form = e.target;
    const body = {
      assignmentWeight: Number(form.assignmentWeight.value),
      quizWeight: Number(form.quizWeight.value),
      activityWeight: Number(form.activityWeight.value),
      finalExamWeight: Number(form.finalExamWeight.value)
    };
    await api(base + '/grade-formula', { method: 'POST', body: JSON.stringify(body) });
    showMsg(msg, 'Formula saved', 'ok');
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
};

async function loadProfile() {
  const p = await api(base + '/profile');
  const form = document.getElementById('profileForm');
  ['firstName', 'middleName', 'lastName', 'suffix', 'mobileNumber', 'departmentName', 'position']
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
  } catch (err) {
    showMsg(msg, err.message, 'err');
  }
};

bindNav({
  dashboard: loadDashboard,
  schedules: loadSchedules,
  classes: loadClassCourses,
  grades: loadGradeCourses,
  formula: loadFormula,
  profile: loadProfile
});

loadDashboard();
