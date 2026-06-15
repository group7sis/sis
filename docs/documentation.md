# SIS Admin UI API Guide

This guide is for the UI team. It explains the `/admin` API resources, what each endpoint does, what filters are available, and what stats come back in the response.

## Core admin endpoints

Base path: `/admin`

Common endpoints for each resource:
- `GET /admin/{resource}` - get all items for that resource, with optional filters
- `GET /admin/{resource}/{id}` - get one item by ID
- `POST /admin/{resource}/add` - create a new item
- `PUT /admin/{resource}/{id}` - update an item by ID

### Response format for list endpoints

`GET /admin/{resource}` returns:
- a list of matching items
- a `stats` object for that same search result set

This means the stats reflect the filtered list the user sees.

## Global dashboard stats

Endpoint: `GET /admin/dashboard`

Returns a single object with global totals. Use this for dashboard summary cards.

Available dashboard stats:
- `totalPrograms`
- `totalCurriculums`
- `totalCourses`
- `totalSections`
- `totalStudents`
- `totalFaculty`
- `totalSchedules`
- `totalDepartments`
- `totalAdminAccounts`

## Resources overview

Resources available under `GET /admin/{resource}`:
- `accounts`
- `terms`
- `programs`
- `courses`
- `students`
- `faculty`
- `schedules`
- `sections`
- `departments`

There is also a debugging endpoint:
- `GET /admin/facultyentity` - returns all faculty entities for debugging only

---

## 1) Accounts

### What it does
Manages admin user accounts.

### Use in UI
- Show list of admin users
- Add new admin accounts
- Update existing admin account details

### Filtering/search
No special filters are documented for this endpoint.

### Stats
No per-resource stats are returned for accounts in the list endpoint.

---

## 2) Terms

### What it does
Manages academic terms.

### Use in UI
- Show term list
- Add or edit terms

### Filtering/search
No filters are documented for terms.

### Stats
No per-resource stats are returned for terms.

---

## 3) Programs

### What it does
Manages programs and their curriculums.

### Use in UI
- Show programs list
- Add or edit program titles and active curriculum
- Display program code, active curriculum, years, and status

### Filtering/search
- `title` - partial match on program title
- `status` - `ACTIVE` or `INACTIVE`

### Stats
No per-resource stats are returned for programs in the list endpoint.

---

## 4) Courses

### What it does
Manages course details and assignments.

### Use in UI
- Show course list
- Display department, curriculum, year level, semester, units, lecture/lab hours, and status
- Add or edit course details

### Filtering/search
- `title` - partial match on course name
- `department` - department ID
- `program` - program ID
- `curriculum` - curriculum ID
- `yearLevel` - year level number
- `semester` - semester number
- `status` - `ACTIVE` or `INACTIVE`

### Stats returned
The list response includes a `stats` object with:
- `totalCourses`
- `totalActiveCourses`
- `totalInactiveCourses`

---

## 5) Students

### What it does
Manages student profiles and academic information.

### Use in UI
- Show student list
- Display student status, scholastic status, program, year level, section, and year admitted
- Add or edit student profile and account details

### Filtering/search
- `name` - partial match on first, middle, or last name
- `studentStatus` - `ACTIVE`, `INACTIVE`, `GRADUATED`, or `DROPPED`
- `scholasticStatus` - `REGULAR` or `IRREGULAR`
- `program` - program ID
- `curriculum` - curriculum ID
- `academicYear` - year string, e.g. `2025-2026`
- `yearLevel` - year level number
- `section` - section ID

### Stats returned
The list response includes a `stats` object with:
- `totalStudents`
- `totalActiveStudents`
- `totalIrregularStudents`
- `totalGraduatedStudents`

---

## 6) Faculty

### What it does
Manages faculty members, employment type, and related assignments.

### Use in UI
- Show faculty list
- Display faculty status, department, position, employment type, qualified courses, and profile
- Add or edit faculty records

### Filtering/search
- `name` - partial match on first, middle, or last name
- `status` - `ACTIVE` or `INACTIVE`
- `department` - department ID
- `employmentType` - `FULL_TIME` or `PART_TIME`
- `qualifiedCourse` - course ID
- `scheduledCourse` - course ID (filters faculties linked to a scheduled course)

### Stats returned
The list response includes a `stats` object with:
- `totalFaculty`
- `totalActiveFaculty`
- `totalInactiveFaculty`
- `totalSchedulesAssignedPerFaculty`

---

## 7) Schedules

### What it does
Manages course schedules and assigned faculty.

### Use in UI
- Show schedule list
- Display academic year, program, curriculum, year level, semester, section, day, and status
- Add or edit schedules and faculty assignment

### Filtering/search
- `academicYear` - academic year string
- `program` - program ID
- `curriculum` - curriculum ID
- `yearLevel` - year level number
- `semester` - semester number
- `section` - section ID
- `status` - `ACTIVE` or `INACTIVE`
- `day` - day name

### Stats returned
The list response includes a `stats` object with:
- `totalSchedules`
- `totalActiveSchedules`
- `totalInactiveSchedules`
- `weeklyHours`
- `dailyHours` - hours calculated for the filtered `day` value, when `day` is provided

---

## 8) Sections

### What it does
Manages sections for programs and year levels.

### Use in UI
- Show section list
- Display section name, code, capacity, program, year level, status, and enrolled student count
- Add or edit sections

### Filtering/search
- `name` - partial match on section name
- `program` - program ID
- `yearLevel` - year level number
- `status` - `ACTIVE` or `INACTIVE`

### Stats returned
The list response includes a `stats` object with:
- `totalSections`
- `totalActiveSections`
- `totalInactiveSections`
- `totalEnrolledStudentsPerSection`

---

## 9) Departments

### What it does
Manages department labels.

### Use in UI
- Show department list
- Add or edit departments

### Filtering/search
- `name` - partial name match on department name

### Stats
No per-resource stats are returned for departments in the list endpoint.

---

## Notes for UI design

- Use the `stats` object in the list response to show summary values for the current filters.
- If users change filters, call the same `GET /admin/{resource}` endpoint again and use the updated `stats` results.
- Use `GET /admin/dashboard` for top dashboard summary cards across the whole system.
- `POST /admin/{resource}/add` is for creating items. `PUT /admin/{resource}/{id}` is for editing existing items.
- For add/update forms, most list fields are the same as the return object structures shown in the API docs.

This page is meant to help the UI team know what each resource can show and what filters and stats are available.