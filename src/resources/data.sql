-- Solo inserta si las tablas están vacías
INSERT OR IGNORE INTO MONSTER_TYPE (name, description, weakness, terrorLevel) VALUES
('Vampire', 'Children of the night', 'Sunlight', 5),
('Werewolf', 'Wolf shapeshifters', 'Silver', 4),
('Zombie', 'Undead students', 'Fire', 3),
('Witch', 'Magic wielders', 'Iron', 4),
('Mummy', 'Ancient wrapped beings', 'Water', 3);

INSERT OR IGNORE INTO TEACHER (name, surname, birthDate, email, specialty) VALUES
('Dracula', 'Von Shriek', '1350-10-31', 'dracula@monsterhigh.edu', 'Dark Arts'),
('Bloodgood', 'Headless', '1820-09-13', 'bloodgood@monsterhigh.edu', 'History of Monsters'),
('Frankenstein', 'Igor', '1800-04-01', 'frankie@monsterhigh.edu', 'Mad Science');

INSERT OR IGNORE INTO MONSTER_GROUP (name, tutorId) VALUES
('1A', 1),
('1B', 2),
('2A', 3);

INSERT OR IGNORE INTO STUDENT (name, surname, birthDate, email, studentYear, groupName, monsterTypeId) VALUES
('Draculaura', 'Dracula', '2006-10-31', 'draculaura@monsterhigh.edu', 1, '1A', 1),
('Clawdeen', 'Wolf', '2006-03-10', 'clawdeen@monsterhigh.edu', 1, '1A', 2),
('Frankie', 'Stein', '2006-09-18', 'frankie.s@monsterhigh.edu', 1, '1B', 3),
('Lagoona', 'Blue', '2005-06-16', 'lagoona@monsterhigh.edu', 2, '2A', 4),
('Cleo', 'De Nile', '2005-11-05', 'cleo@monsterhigh.edu', 2, '2A', 5);

INSERT OR IGNORE INTO SUBJECT (name, course, teacherId) VALUES
('Dark Arts 101', 1, 1),
('Monster History', 1, 2),
('Mad Science', 2, 3),
('Creepateria', 2, 2);

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