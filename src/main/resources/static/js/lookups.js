const LK = {
  departments: [],
  programs: [],
  curriculums: [],
  sections: [],
  faculty: [],
  courses: [],
  courseAssignments: [],
  schedules: []
};

function selectHtml(items, valueKey, labelFn, selected, emptyLabel = 'Select...') {
  let html = `<option value="">${emptyLabel}</option>`;
  (items || []).forEach(item => {
    const val = valueKey ? item[valueKey] : item.id;
    const sel = String(val) === String(selected ?? '') ? ' selected' : '';
    html += `<option value="${val}"${sel}>${labelFn(item)}</option>`;
  });
  return html;
}

async function loadLookups() {
  const [departments, programs, sections, facultyPage, coursePage, schedulePage] = await Promise.all([
    api('/admin/departments'),
    api('/admin/programs'),
    api('/admin/sections'),
    api('/admin/faculty'),
    api('/admin/courses'),
    api('/admin/schedules')
  ]);
  LK.departments = departments || [];
  LK.programs = programs || [];
  LK.sections = sections.sections || [];
  LK.faculty = facultyPage.faculties || [];
  LK.courses = coursePage.courses || [];
  LK.schedules = schedulePage.schedules || [];
  LK.curriculums = LK.programs.flatMap(p => (p.curriculums || []).map(c => ({
    ...c, programId: p.id, programCode: p.code, programTitle: p.title
  })));
  LK.courseAssignments = LK.courses.flatMap(c => (c.courseAssignments || []).map(a => ({
    ...a, courseId: c.id, courseCode: c.code, courseTitle: c.title,
    label: `${c.code} → ${a.curriculumId ? 'Curr ' + a.curriculumId : ''} Y${a.yearLevel} S${a.semester}`
  })));
}

function deptLabel(d) { return `${d.code} - ${d.name}`; }
function progLabel(p) { return `${p.code || p.id} - ${p.title}`; }
function currLabel(c) { return `${c.programCode || ''} ${c.code || c.id} (${c.effectiveYear || ''})`.trim(); }
function sectLabel(s) { return `${s.name} (${s.code || s.id}) · Prog ${s.programId}`; }
function facultyLabel(f) { return `${f.facultyNumber || f.id} - ${fmtName(f.profile)}`; }
function courseLabel(c) { return `${c.code} - ${c.title}`; }
function assignLabel(a) { return a.label || `${a.courseCode} Y${a.yearLevel} S${a.semester} (#${a.id})`; }
function schedLabel(s) { return `${s.code} - ${s.courseTitle} / ${s.sectionName || s.sectionId}`; }

function fillSelect(el, items, valueKey, labelFn, selected) {
  if (!el) return;
  el.innerHTML = selectHtml(items, valueKey, labelFn, selected);
}

function fillLookupSelects(root = document) {
  root.querySelectorAll('[data-lookup="department"]').forEach(el =>
    fillSelect(el, LK.departments, 'id', deptLabel, el.value));
  root.querySelectorAll('[data-lookup="program"]').forEach(el =>
    fillSelect(el, LK.programs, 'id', progLabel, el.value));
  root.querySelectorAll('[data-lookup="curriculum"]').forEach(el =>
    fillSelect(el, LK.curriculums, 'id', currLabel, el.value));
  root.querySelectorAll('[data-lookup="section"]').forEach(el =>
    fillSelect(el, LK.sections, 'id', sectLabel, el.value));
  root.querySelectorAll('[data-lookup="faculty"]').forEach(el =>
    fillSelect(el, LK.faculty, 'id', facultyLabel, el.value));
  root.querySelectorAll('[data-lookup="course"]').forEach(el =>
    fillSelect(el, LK.courses, 'id', courseLabel, el.value));
  root.querySelectorAll('[data-lookup="assignment"]').forEach(el =>
    fillSelect(el, LK.courseAssignments, 'id', assignLabel, el.value));
  root.querySelectorAll('[data-lookup="schedule"]').forEach(el =>
    fillSelect(el, LK.schedules, 'id', schedLabel, el.value));
}

function assignmentRowHtml(data = {}) {
  const idField = data.id ? `<input type="hidden" name="id" value="${data.id}">` : '';
  const progOpts = selectHtml(LK.programs, 'id', progLabel, data.programId, 'Any program');
  const currOpts = selectHtml(LK.curriculums, 'id', currLabel, data.curriculumId, 'Select curriculum');
  return `<div class="repeater-row grid-4" data-repeater="assignment">
    ${idField}
    <div><label>Program</label><select name="programId">${progOpts}</select></div>
    <div><label>Curriculum</label><select name="curriculumId" required>${currOpts}</select></div>
    <div><label>Year Level</label><input name="yearLevel" type="number" value="${data.yearLevel ?? 1}" required></div>
    <div><label>Semester</label><input name="semester" type="number" value="${data.semester ?? 1}" required></div>
    <div style="grid-column:1/-1" class="row"><button type="button" class="btn btn-sm danger" data-remove-row>Remove</button></div>
  </div>`;
}

function qualifiedCourseSelectHtml(selectedIds = []) {
  const ids = new Set((selectedIds || []).map(String));
  return (LK.courses || []).map(c =>
    `<option value="${c.id}"${ids.has(String(c.id)) ? ' selected' : ''}>${courseLabel(c)}</option>`
  ).join('');
}
