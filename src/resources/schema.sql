-- ============================================================
-- Monster High Institute Manager -- Database Schema (SQLite)
-- Run this script once before launching the application.
-- All primary keys use AUTOINCREMENT so IDs are never reused.
-- ============================================================

CREATE TABLE IF NOT EXISTS MONSTER_TYPE (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT    NOT NULL UNIQUE,
    description TEXT,
    weakness    TEXT,
    terrorLevel INTEGER DEFAULT 1
);

CREATE TABLE IF NOT EXISTS TEACHER (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    name      TEXT    NOT NULL,
    surname   TEXT    NOT NULL,
    birthDate TEXT,
    email     TEXT    NOT NULL UNIQUE,
    specialty TEXT    NOT NULL
);

CREATE TABLE IF NOT EXISTS STUDENT (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    name         TEXT    NOT NULL,
    surname      TEXT    NOT NULL,
    birthDate    TEXT,
    email        TEXT    NOT NULL UNIQUE,
    studentYear  INTEGER NOT NULL CHECK(studentYear IN (1, 2)),
    groupName    TEXT    NOT NULL,
    monsterTypeId INTEGER REFERENCES MONSTER_TYPE(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS SUBJECT (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    name      TEXT    NOT NULL,
    course    INTEGER NOT NULL CHECK(course IN (1, 2)),
    teacherId INTEGER REFERENCES TEACHER(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS MONSTER_GROUP (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    name    TEXT    NOT NULL UNIQUE,
    tutorId INTEGER REFERENCES TEACHER(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS ENROLLMENT (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    studentId INTEGER NOT NULL REFERENCES STUDENT(id) ON DELETE CASCADE,
    subjectId INTEGER NOT NULL REFERENCES SUBJECT(id) ON DELETE CASCADE,
    grade1    REAL    NOT NULL DEFAULT 0.0,
    grade2    REAL    NOT NULL DEFAULT 0.0,
    UNIQUE(studentId, subjectId)
);
