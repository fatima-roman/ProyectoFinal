-- Solo inserta si las tablas están vacías
INSERT OR IGNORE INTO MONSTER_TYPE (id, name, description, weakness, terrorLevel) VALUES
(1, 'Vampire', 'Children of the night', 'Sunlight', 5),
(2, 'Werewolf', 'Wolf shapeshifters', 'Silver', 4),
(3, 'Zombie', 'Undead students', 'Fire', 3),
(4, 'Witch', 'Magic wielders', 'Iron', 4),
(5, 'Mummy', 'Ancient wrapped beings', 'Water', 3);

INSERT OR IGNORE INTO TEACHER (id, name, surname, birthDate, email, specialty) VALUES
(1, 'Dracula', 'Von Shriek', '1350-10-31', 'dracula@monsterhigh.edu', 'Dark Arts'),
(2, 'Bloodgood', 'Headless', '1820-09-13', 'bloodgood@monsterhigh.edu', 'History of Monsters'),
(3, 'Frankenstein', 'Igor', '1800-04-01', 'frankie@monsterhigh.edu', 'Mad Science');

INSERT OR IGNORE INTO MONSTER_GROUP (id, name, tutorId) VALUES
(1, '1A', 1),
(2, '1B', 2),
(3, '2A', 3);

INSERT OR IGNORE INTO STUDENT (id, name, surname, birthDate, email, studentYear, groupName, monsterTypeId) VALUES
(1, 'Draculaura', 'Dracula', '2006-10-31', 'draculaura@monsterhigh.edu', 1, '1A', 1),
(2, 'Clawdeen', 'Wolf', '2006-03-10', 'clawdeen@monsterhigh.edu', 1, '1A', 2),
(3, 'Frankie', 'Stein', '2006-09-18', 'frankie.s@monsterhigh.edu', 1, '1B', 3),
(4, 'Lagoona', 'Blue', '2005-06-16', 'lagoona@monsterhigh.edu', 2, '2A', 4),
(5, 'Cleo', 'De Nile', '2005-11-05', 'cleo@monsterhigh.edu', 2, '2A', 5);

INSERT OR IGNORE INTO SUBJECT (id, name, course, teacherId) VALUES
(1, 'Dark Arts 101', 1, 1),
(2, 'Monster History', 1, 2),
(3, 'Mad Science', 2, 3),
(4, 'Creepateria', 2, 2);

INSERT OR IGNORE INTO ENROLLMENT (studentId, subjectId, grade1, grade2) VALUES
(1, 1, 9.5, 8.0),
(1, 2, 7.0, 8.5),
(2, 1, 6.5, 7.5),
(2, 2, 8.0, 9.0),
(3, 1, 5.0, 6.0),
(4, 3, 9.0, 9.5),
(4, 4, 8.5, 8.0),
(5, 3, 7.0, 7.5),
(5, 4, 9.0, 8.5);