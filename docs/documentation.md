# SIS Admin Design Reference

This page is for the design team. It explains what each admin screen can show and what values should appear on the screen.

## What this reference is for

Use this page to understand:
- what each admin resource is for
- what data should be shown
- what filters the UI can let users pick
- what summary numbers are available

## Resources

### Accounts
What it is:
- admin user accounts

Filters:
- none documented for design

Summary stats:
- none

---

### Terms
What it is:
- academic terms

Filters:
- none documented for design

Summary stats:
- none

---

### Programs
What it is:
- academic programs and current curriculum

Filters:
- title
- status

Summary stats:
- none

---

### Courses
What it is:
- course details and where the course belongs

Filters:
- title
- department
- program
- curriculum
- year level
- semester
- status

Summary stats:
- total courses
- total active courses
- total inactive courses

---

### Students
What it is:
- student profiles and academic status

Filters:
- name
- student status
- scholastic status
- program
- curriculum
- academic year
- year level
- section

Summary stats:
- total students
- total active students
- total irregular students
- total graduated students

---

### Faculty
What it is:
- faculty members and their assignments

Filters:
- name
- status
- department
- employment type
- qualified course
- scheduled course

Summary stats:
- total faculty
- total active faculty
- total inactive faculty
- total schedules assigned per faculty

---

### Schedules
What it is:
- course schedules and assigned faculty

Filters:
- academic year
- program
- curriculum
- year level
- semester
- section
- status
- day

Summary stats:
- total schedules
- total active schedules
- total inactive schedules
- weekly hours
- daily hours (calculated for the selected day)

---

### Sections
What it is:
- student groups by program and year level

Filters:
- name
- program
- year level
- status

Summary stats:
- total sections
- total active sections
- total inactive sections
- total enrolled students per section

---

### Departments
What it is:
- department labels

Filters:
- name

Summary stats:
- none

---

### Dashboard
What it is:
- global admin summary stats for the system

Filters:
- none (dashboard shows global totals)

Summary stats:
- total programs
- total curriculums
- total courses
- total sections
- total students
- total faculty
- total schedules
- total departments
- total admin accounts

---

### General design notes
- The summary numbers should match whatever filters the user has selected.
- For schedules, daily hours is based on the selected day filter.
- This page is a design reference, not a technical API guide.
