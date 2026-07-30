INSERT INTO patient_tbl (name, gender, birth_date, email, blood_group)
VALUES
    ('Aarav Verma', 'MALE', '1990-05-10', 'aarav.sharma@example.com', 'O_POSITIVE'),
    ('Ananya Patel', 'FEMALE', '1994-08-22', 'ananya.patel@example.com', 'A_POSITIVE'),
    ('Rohan Mehta', 'MALE', '1988-12-15', 'rohan.mehta@example.com', 'B_POSITIVE'),
    ('Priya Singh', 'FEMALE', '1992-03-05', 'priya.singh@example.com', 'AB_POSITIVE'),
    ('Karan Verma', 'MALE', '1985-07-19', 'karan.verma@example.com', 'O_NEGATIVE'),
    ('Neha Gupta', 'FEMALE', '1996-11-30', 'neha.gupta@example.com', 'A_NEGATIVE'),
    ('Vikram Iyer', 'MALE', '1989-01-25', 'vikram.iyer@example.com', 'B_NEGATIVE'),
    ('Sneha Rao', 'FEMALE', '1993-09-14', 'sneha.rao@example.com', 'AB_NEGATIVE'),
    ('Rahul Khanna', 'MALE', '1991-06-08', 'rahul.khanna@example.com', 'O_POSITIVE'),
    ('Pooja Malhotra', 'FEMALE', '1995-04-18', 'pooja.malhotra@example.com', 'A_POSITIVE');

INSERT INTO doctor (name, specialization, email)
VALUES
    ('Dr. Meera Nair', 'Cardiology', 'meera.nair@hospital.com'),
    ('Dr. Arjun Desai', 'Oncology', 'arjun.desai@hospital.com'),
    ('Dr. Kavya Reddy', 'Neurology', 'kavya.reddy@hospital.com'),
    ('Dr. Sameer Joshi', 'Orthopaedics', 'sameer.joshi@hospital.com');

-- head_doctor_id is the default join column for Department.headDoctor (@OneToOne)
INSERT INTO department (name, head_doctor_id)
VALUES
    ('Cardiology', 1),
    ('Oncology', 2),
    ('Neurology', 3);

-- join table for the Department <-> Doctor many-to-many
INSERT INTO my_dpt_doctors (dpt_id, doctor_id)
VALUES
    (1, 1), (1, 4),
    (2, 2),
    (3, 3), (3, 4);

INSERT INTO appointment (appointment_time, reason, patient_id, doctor_id)
VALUES
    ('2026-08-03 09:30:00', 'Chest pain follow-up', 1, 1),
    ('2026-08-03 11:00:00', 'Routine cardiac screening', 1, 1),
    ('2026-08-04 14:15:00', 'Chemotherapy review', 2, 2),
    ('2026-08-05 10:00:00', 'Migraine consultation', 3, 3),
    ('2026-08-05 16:45:00', 'Post-surgery checkup', 4, 4);
