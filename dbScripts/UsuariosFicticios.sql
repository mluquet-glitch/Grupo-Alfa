-- =============================================================================
-- DATOS FICTICIOS PARA TODAS LAS TABLAS (PostgreSQL)
-- Al menos 10 registros por tabla. Ordenado por dependencias FK.
-- Uso de IDs explícitos + reinicio de secuencias IDENTITY.
-- =============================================================================

BEGIN;

-- ---------------------------------------------------------------------------
-- 1. USUARIOS (45: 10 admin + 10 asesor + 10 apoderado + 12 estudiante + 3 extra)
-- ---------------------------------------------------------------------------
INSERT INTO usuarios (id_usuario, nro_carnet, apellido_paterno, apellido_materno, nombre, fecha_nacimiento, correo_electronico, contraseña, usuario_vigente) VALUES
 (1, 1000001, 'Mamani', 'Quispe', 'Juan Carlos', '1985-03-12', 'juan.mamani@mail.com', 'hash_pbkdf2_001', TRUE),
 (2, 1000002, 'Apaza', 'Condori', 'María Elena', '1990-07-22', 'maria.apaza@mail.com', 'hash_pbkdf2_002', TRUE),
 (3, 1000003, 'Gutiérrez', 'Rojas', 'Pedro Luis', '1982-11-05', 'pedro.gutierrez@mail.com', 'hash_pbkdf2_003', TRUE),
 (4, 1000004, 'Fernández', 'Torres', 'Ana Lucía', '1988-01-30', 'ana.fernandez@mail.com', 'hash_pbkdf2_004', TRUE),
 (5, 1000005, 'Ramos', 'Vargas', 'Diego Armando', '1992-05-17', 'diego.ramos@mail.com', 'hash_pbkdf2_005', TRUE),
 (6, 1000006, 'Salazar', 'Mendoza', 'Carla Patricia', '1987-09-09', 'carla.salazar@mail.com', 'hash_pbkdf2_006', TRUE),
 (7, 1000007, 'Torrez', 'Flores', 'Miguel Ángel', '1980-12-01', 'miguel.torrez@mail.com', 'hash_pbkdf2_007', TRUE),
 (8, 1000008, 'Vargas', 'Cruz', 'Lucía Fernanda', '1995-04-14', 'lucia.vargas@mail.com', 'hash_pbkdf2_008', TRUE),
 (9, 1000009, 'Condori', 'Huanca', 'Jorge Luis', '1983-06-25', 'jorge.condori@mail.com', 'hash_pbkdf2_009', TRUE),
 (10, 1000010, 'Quispe', 'Lima', 'Sofía Alejandra', '1991-10-11', 'sofia.quispe@mail.com', 'hash_pbkdf2_010', TRUE),
 (11, 1000011, 'Paredes', 'Siles', 'Roberto Carlos', '1979-02-18', 'roberto.paredes@mail.com', 'hash_pbkdf2_011', TRUE),
 (12, 1000012, 'Mendoza', 'Aguilar', 'Paola Andrea', '1986-08-27', 'paola.mendoza@mail.com', 'hash_pbkdf2_012', TRUE),
 (13, 1000013, 'Castro', 'Bravo', 'Fernando José', '1984-03-03', 'fernando.castro@mail.com', 'hash_pbkdf2_013', TRUE),
 (14, 1000014, 'Ríos', 'Ponce', 'Daniela', '1993-12-19', 'daniela.rios@mail.com', 'hash_pbkdf2_014', TRUE),
 (15, 1000015, 'Acuña', 'Zegarra', 'Marco Antonio', '1981-07-07', 'marco.acuna@mail.com', 'hash_pbkdf2_015', TRUE),
 (16, 1000016, 'Balboa', 'Terrazas', 'Gabriela', '1989-05-29', 'gabriela.balboa@mail.com', 'hash_pbkdf2_016', TRUE),
 (17, 1000017, 'Cordero', 'Vega', 'Andrés Felipe', '1978-09-15', 'andres.cordero@mail.com', 'hash_pbkdf2_017', TRUE),
 (18, 1000018, 'Delgado', 'Iriarte', 'Valeria', '1994-11-23', 'valeria.delgado@mail.com', 'hash_pbkdf2_018', TRUE),
 (19, 1000019, 'Espinoza', 'Nina', 'Cristian David', '1987-01-08', 'cristian.espinoza@mail.com', 'hash_pbkdf2_019', TRUE),
 (20, 1000020, 'Fuentes', 'Orellana', 'Natalia', '1990-06-16', 'natalia.fuentes@mail.com', 'hash_pbkdf2_020', TRUE),
 (21, 1000021, 'Galarza', 'Patiño', 'Hugo René', '1975-04-20', 'hugo.galarza@mail.com', 'hash_pbkdf2_021', TRUE),
 (22, 1000022, 'Herrera', 'Quiroga', 'Rosa María', '1980-10-02', 'rosa.herrera@mail.com', 'hash_pbkdf2_022', TRUE),
 (23, 1000023, 'Ibáñez', 'Rengel', 'Luis Fernando', '1977-12-12', 'luis.ibanez@mail.com', 'hash_pbkdf2_023', TRUE),
 (24, 1000024, 'Jiménez', 'Soria', 'Carmen Rosa', '1982-05-06', 'carmen.jimenez@mail.com', 'hash_pbkdf2_024', TRUE),
 (25, 1000025, 'Kuno', 'Ticona', 'Óscar', '1979-08-08', 'oscar.kuno@mail.com', 'hash_pbkdf2_025', TRUE),
 (26, 1000026, 'López', 'Ugarte', 'Mónica', '1985-02-14', 'monica.lopez@mail.com', 'hash_pbkdf2_026', TRUE),
 (27, 1000027, 'Miranda', 'Vaca', 'Raúl Ernesto', '1976-09-30', 'raul.miranda@mail.com', 'hash_pbkdf2_027', TRUE),
 (28, 1000028, 'Núñez', 'Walls', 'Elena', '1983-03-21', 'elena.nunez@mail.com', 'hash_pbkdf2_028', TRUE),
 (29, 1000029, 'Ochoa', 'Yáñez', 'Pablo César', '1981-07-11', 'pablo.ochoa@mail.com', 'hash_pbkdf2_029', TRUE),
 (30, 1000030, 'Pacheco', 'Zambrana', 'Teresa', '1978-11-17', 'teresa.pacheco@mail.com', 'hash_pbkdf2_030', TRUE),
 (31, 1000031, 'Colque', 'Mamani', 'Kevin Brandon', '2006-02-10', 'kevin.colque@mail.com', 'hash_pbkdf2_031', TRUE),
 (32, 1000032, 'Santos', 'Paredes', 'Ariana Nicole', '2007-05-24', 'ariana.santos@mail.com', 'hash_pbkdf2_032', TRUE),
 (33, 1000033, 'Tola', 'Ramos', 'Dilan Mateo', '2006-09-12', 'dilan.tola@mail.com', 'hash_pbkdf2_033', TRUE),
 (34, 1000034, 'Uria', 'Salas', 'Camila', '2007-01-19', 'camila.uria@mail.com', 'hash_pbkdf2_034', TRUE),
 (35, 1000035, 'Venegas', 'Tapia', 'Rodrigo', '2005-11-03', 'rodrigo.venegas@mail.com', 'hash_pbkdf2_035', TRUE),
 (36, 1000036, 'Zárate', 'Uño', 'Fernanda', '2006-06-28', 'fernanda.zarate@mail.com', 'hash_pbkdf2_036', TRUE),
 (37, 1000037, 'Alarcón', 'Villarroel', 'Matías', '2007-03-15', 'matias.alarcon@mail.com', 'hash_pbkdf2_037', TRUE),
 (38, 1000038, 'Blanco', 'Wella', 'Jimena', '2006-08-09', 'jimena.blanco@mail.com', 'hash_pbkdf2_038', TRUE),
 (39, 1000039, 'Callisaya', 'Yujra', 'Sebastián', '2005-12-25', 'sebastian.callisaya@mail.com', 'hash_pbkdf2_039', TRUE),
 (40, 1000040, 'Dorado', 'Zenteno', 'Alison', '2007-07-07', 'alison.dorado@mail.com', 'hash_pbkdf2_040', TRUE),
 (41, 1000041, 'Eyzaguirre', 'Aguirre', 'Thiago', '2006-04-04', 'thiago.eyzaguirre@mail.com', 'hash_pbkdf2_041', TRUE),
 (42, 1000042, 'Flores', 'Borda', 'Nayeli', '2007-10-31', 'nayeli.flores@mail.com', 'hash_pbkdf2_042', TRUE),
 (43, 1000043, 'Gómez', 'Callapa', 'Alexandra', '1996-03-08', 'alexandra.gomez@mail.com', 'hash_pbkdf2_043', TRUE),
 (44, 1000044, 'Huanca', 'Daza', 'Sergio', '1998-09-20', 'sergio.huanca@mail.com', 'hash_pbkdf2_044', FALSE),
 (45, 1000045, 'Inca', 'Escobar', 'Beatriz', '2000-01-25', 'beatriz.inca@mail.com', 'hash_pbkdf2_045', TRUE);

-- ---------------------------------------------------------------------------
-- 2. TABLAS BASE SIN FK: facultades, materias, aulas, roles
-- ---------------------------------------------------------------------------
INSERT INTO facultades (id_facultad, nombre) VALUES
 (1, 'Facultad de Ingeniería'),
 (2, 'Facultad de Medicina'),
 (3, 'Facultad de Derecho'),
 (4, 'Facultad de Economía'),
 (5, 'Facultad de Arquitectura'),
 (6, 'Facultad de Ciencias Puras'),
 (7, 'Facultad de Odontología'),
 (8, 'Facultad de Agronomía'),
 (9, 'Facultad de Tecnología'),
 (10, 'Facultad de Humanidades');

INSERT INTO materias (id_materia, nombre_materia) VALUES
 (1, 'Matemáticas'),
 (2, 'Física'),
 (3, 'Química'),
 (4, 'Biología'),
 (5, 'Lenguaje'),
 (6, 'Historia'),
 (7, 'Geografía'),
 (8, 'Inglés'),
 (9, 'Razonamiento Lógico'),
 (10, 'Informática');

INSERT INTO aulas (id_aula, capacidad) VALUES
 (1, 30), (2, 25), (3, 40), (4, 35), (5, 20),
 (6, 50), (7, 28), (8, 32), (9, 45), (10, 22);

INSERT INTO roles (id_rol, nombre) VALUES
 (1, 'SysAdmin'),
 (2, 'Rooter'),
 (3, 'Gerencia'),
 (4, 'Cajero'),
 (5, 'Administración'),
 (6, 'Asesor Coordinador'),
 (7, 'Asesor'),
 (8, 'Apoderado'),
 (9, 'Estudiante'),
 (10, 'Invitado');

-- ---------------------------------------------------------------------------
-- 3. ADMINISTRATIVOS (10), ASESORES (10), APODERADOS (10)
-- ---------------------------------------------------------------------------
INSERT INTO administrativos (id_administrativo, id_usuario) VALUES
 (1, 1), (2, 2), (3, 3), (4, 4), (5, 5),
 (6, 6), (7, 7), (8, 8), (9, 9), (10, 10);

INSERT INTO asesores (id_asesor, id_usuario) VALUES
 (1, 11), (2, 12), (3, 13), (4, 14), (5, 15),
 (6, 16), (7, 17), (8, 18), (9, 19), (10, 20);

INSERT INTO apoderados (id_apoderado, id_usuario, celular) VALUES
 (1, 21, '70111222'),
 (2, 22, '70222333'),
 (3, 23, '70333444'),
 (4, 24, '70444555'),
 (5, 25, '70555666'),
 (6, 26, '70666777'),
 (7, 27, '70777888'),
 (8, 28, '70888999'),
 (9, 29, '70999000'),
 (10, 30, '71000111');

-- ---------------------------------------------------------------------------
-- 4. ESTUDIANTES (12)
-- ---------------------------------------------------------------------------
INSERT INTO estudiantes (id_estudiante, id_usuario, id_apoderado, colegio_de_procedencia, direccion_de_domicilio, foto) VALUES
 (1, 31, 1, 'Colegio San Calixto', 'Av. Arce 123, La Paz', NULL),
 (2, 32, 2, 'Colegio Loretto', 'Calle Loayza 456, La Paz', NULL),
 (3, 33, 3, 'Colegio Don Bosco', 'Av. Busch 789, La Paz', NULL),
 (4, 34, 4, 'Colegio Santa Ana', 'Calle Murillo 101, El Alto', NULL),
 (5, 35, 5, 'Colegio La Salle', 'Av. 6 de Marzo 202, El Alto', NULL),
 (6, 36, 6, 'Colegio San Ignacio', 'Calle Potosí 303, La Paz', NULL),
 (7, 37, 7, 'Colegio Alemán', 'Av. Ballivián 404, La Paz', NULL),
 (8, 38, 8, 'Colegio Amor de Dios', 'Calle Sagárnaga 505, La Paz', NULL),
 (9, 39, 9, 'Colegio Franco Boliviano', 'Av. Villazón 606, Cochabamba', NULL),
 (10, 40, 10, 'Colegio Domingo Savio', 'Calle Heroínas 707, Cochabamba', NULL),
 (11, 41, 1, 'Colegio San Calixto', 'Av. Arce 999, La Paz', NULL),
 (12, 42, 2, 'Colegio Loretto', 'Calle Loayza 888, La Paz', NULL);

-- ---------------------------------------------------------------------------
-- 5. ASIGNACION_ROL (12)
-- ---------------------------------------------------------------------------
INSERT INTO asignacion_rol (id_asignacion_rol, id_rol, id_usuario) VALUES
 (1, 5, 1),
 (2, 5, 2),
 (3, 4, 3),
 (4, 3, 4),
 (5, 5, 5),
 (6, 7, 11),
 (7, 7, 12),
 (8, 6, 13),
 (9, 8, 21),
 (10, 8, 22),
 (11, 9, 31),
 (12, 9, 32);

-- ---------------------------------------------------------------------------
-- 6. CARRERAS (12), CONVOCATORIAS_PREUNIVERSITARIO (10)
-- ---------------------------------------------------------------------------
INSERT INTO carreras (id_carrera, id_facultad, nombre) VALUES
 (1, 1, 'Ingeniería de Sistemas'),
 (2, 1, 'Ingeniería Civil'),
 (3, 2, 'Medicina'),
 (4, 2, 'Enfermería'),
 (5, 3, 'Derecho'),
 (6, 4, 'Administración de Empresas'),
 (7, 5, 'Arquitectura'),
 (8, 6, 'Física'),
 (9, 9, 'Electrónica'),
 (10, 10, 'Psicología'),
 (11, 4, 'Contaduría Pública'),
 (12, 7, 'Odontología');

INSERT INTO convocatorias_preuniversitario (id_convocatoria, id_facultad, fecha_inicio) VALUES
 (1, 1, '2025-01-15'),
 (2, 2, '2025-02-01'),
 (3, 3, '2025-02-15'),
 (4, 4, '2025-03-01'),
 (5, 5, '2025-03-15'),
 (6, 6, '2025-04-01'),
 (7, 1, '2025-07-15'),
 (8, 9, '2025-08-01'),
 (9, 10, '2025-08-15'),
 (10, 2, '2026-01-10');

-- ---------------------------------------------------------------------------
-- 7. TEMAS (12), INSCRIPCIONES (10)
-- ---------------------------------------------------------------------------
INSERT INTO temas (id_tema, id_materia, nombre_tema) VALUES
 (1, 1, 'Álgebra básica'),
 (2, 1, 'Trigonometría'),
 (3, 2, 'Cinemática'),
 (4, 2, 'Dinámica'),
 (5, 3, 'Tabla periódica'),
 (6, 4, 'Genética'),
 (7, 5, 'Gramática'),
 (8, 9, 'Series numéricas'),
 (9, 10, 'Algoritmos'),
 (10, 3, 'Estequiometría'),
 (11, 5, 'Comprensión lectora'),
 (12, 2, 'Electromagnetismo');

INSERT INTO inscripciones (id_inscripcion, id_administrativo, id_estudiante, como_se_entero_de_nosotros, recalculo, preparacion_previa, tiempo_de_preparacion_meses, lugar_de_preparacion) VALUES
 (1, 1, 1, 'Facebook', FALSE, TRUE, 3, 'Colegio'),
 (2, 2, 2, 'Amigo', FALSE, FALSE, 0, NULL),
 (3, 3, 3, 'Volante', TRUE, TRUE, 6, 'Academia Sigma'),
 (4, 4, 4, 'TikTok', FALSE, FALSE, 0, NULL),
 (5, 5, 5, 'Radio', FALSE, TRUE, 2, 'Casa'),
 (6, 6, 6, 'Feria', TRUE, FALSE, 0, NULL),
 (7, 7, 7, 'Instagram', FALSE, TRUE, 4, 'Colegio'),
 (8, 8, 8, 'Hermano', FALSE, FALSE, 0, NULL),
 (9, 9, 9, 'Facebook', TRUE, TRUE, 5, 'Preuniversitario Alfa'),
 (10, 10, 10, 'TikTok', FALSE, FALSE, 0, NULL);

-- ---------------------------------------------------------------------------
-- 8. MATERIAS_ASESORES (10), POSTULACIONES (12)
-- ---------------------------------------------------------------------------
INSERT INTO materias_asesores (id_materia_asesor, id_asesor, id_materia) VALUES
 (1, 1, 1),
 (2, 2, 2),
 (3, 3, 3),
 (4, 4, 4),
 (5, 5, 5),
 (6, 6, 9),
 (7, 7, 10),
 (8, 8, 1),
 (9, 9, 2),
 (10, 10, 5);

INSERT INTO postulaciones (id_postulacion, id_estudiante, id_carrera) VALUES
 (1, 1, 1),
 (2, 2, 3),
 (3, 3, 5),
 (4, 4, 6),
 (5, 5, 2),
 (6, 6, 7),
 (7, 7, 1),
 (8, 8, 10),
 (9, 9, 3),
 (10, 10, 9),
 (11, 11, 4),
 (12, 12, 12);

-- ---------------------------------------------------------------------------
-- 9. CURSOS_CAPACITACION (10), EXAMENES_DE_INGRESO (10),
--    MATERIAS_CONVOCATORIA_ASIGNACION (10)
-- ---------------------------------------------------------------------------
INSERT INTO cursos_capacitacion (id_capacitacion, id_convocatoria, id_aula, nivel, nombre, modalidad, fecha_inicio, fecha_fin, hora_inicio, hora_fin) VALUES
 (1, 1, 1, 'Iniciando', 'Preu Ingeniería Intensivo', 'Presencial', '2025-01-20', '2025-03-15', '08:00', '12:00'),
 (2, 2, 2, 'Intermedio', 'Preu Medicina Mañana', 'Presencial', '2025-02-05', '2025-03-30', '08:00', '12:00'),
 (3, 3, 3, 'Avanzado', 'Preu Derecho Tarde', 'Presencial', '2025-02-20', '2025-04-15', '14:00', '18:00'),
 (4, 4, 4, 'Iniciando', 'Preu Economía Noche', 'Virtual', '2025-03-05', '2025-04-30', '19:00', '21:30'),
 (5, 5, 5, 'Intermedio', 'Preu Arquitectura', 'Presencial', '2025-03-20', '2025-05-15', '09:00', '13:00'),
 (6, 6, 6, 'Avanzado', 'Preu Ciencias Puras', 'Híbrida', '2025-04-05', '2025-05-31', '08:30', '12:30'),
 (7, 7, 7, 'Iniciando', 'Preu Ingeniería II-2025', 'Presencial', '2025-07-20', '2025-09-15', '08:00', '12:00'),
 (8, 8, 8, 'Intermedio', 'Preu Tecnología', 'Virtual', '2025-08-05', '2025-09-30', '19:00', '21:30'),
 (9, 9, 9, 'Avanzado', 'Preu Humanidades', 'Presencial', '2025-08-20', '2025-10-15', '14:00', '18:00'),
 (10, 10, 10, 'Iniciando', 'Preu Medicina 2026', 'Híbrida', '2026-01-15', '2026-03-15', '08:00', '12:00');

INSERT INTO examenes_de_ingreso (id_examen_de_ingreso, id_convocatoria, id_materia, foto) VALUES
 (1, 1, 1, NULL),
 (2, 1, 2, NULL),
 (3, 2, 4, NULL),
 (4, 3, 5, NULL),
 (5, 4, 1, NULL),
 (6, 5, 2, NULL),
 (7, 6, 3, NULL),
 (8, 7, 9, NULL),
 (9, 8, 10, NULL),
 (10, 9, 5, NULL);

INSERT INTO materias_convocatoria_asignacion (id_asignacion_materia_convocatoria, id_convocatoria, id_materia) VALUES
 (1, 1, 1),
 (2, 1, 2),
 (3, 2, 4),
 (4, 3, 5),
 (5, 4, 1),
 (6, 5, 2),
 (7, 6, 3),
 (8, 7, 9),
 (9, 8, 10),
 (10, 9, 5);

-- ---------------------------------------------------------------------------
-- 10. CONTENIDOS (10), ASIGNACIONES_ASESORES (10), ASIGNACIONES_MATERIAS (10)
-- ---------------------------------------------------------------------------
INSERT INTO contenidos (id_contenido, id_examen_de_ingreso, id_tema) VALUES
 (1, 1, 1),
 (2, 1, 2),
 (3, 2, 3),
 (4, 3, 6),
 (5, 4, 7),
 (6, 5, 1),
 (7, 6, 4),
 (8, 7, 5),
 (9, 8, 8),
 (10, 9, 9);

INSERT INTO asignaciones_asesores (id_asignacion_asesor, id_asesor, id_curso_capacitacion) VALUES
 (1, 1, 1),
 (2, 2, 1),
 (3, 3, 2),
 (4, 4, 3),
 (5, 5, 4),
 (6, 6, 5),
 (7, 7, 6),
 (8, 8, 7),
 (9, 9, 8),
 (10, 10, 9);

INSERT INTO asignaciones_materias (id_asignacion_materia, id_materia, id_curso_capacitacion) VALUES
 (1, 1, 1),
 (2, 2, 1),
 (3, 4, 2),
 (4, 5, 3),
 (5, 1, 4),
 (6, 2, 5),
 (7, 3, 6),
 (8, 9, 7),
 (9, 10, 8),
 (10, 5, 9);

-- ---------------------------------------------------------------------------
-- 11. MATRICULACIONES (12), CLASES (12)
-- ---------------------------------------------------------------------------
INSERT INTO matriculaciones (id_matriculacion, id_estudiante, id_curso_capacitacion, esta_vigente, nro_comprobante, nro_credencial) VALUES
 (1, 1, 1, TRUE, 5001, 9001),
 (2, 2, 2, TRUE, 5002, 9002),
 (3, 3, 3, TRUE, 5003, 9003),
 (4, 4, 4, TRUE, 5004, 9004),
 (5, 5, 1, TRUE, 5005, 9005),
 (6, 6, 5, TRUE, 5006, 9006),
 (7, 7, 7, TRUE, 5007, 9007),
 (8, 8, 9, TRUE, 5008, 9008),
 (9, 9, 2, TRUE, 5009, 9009),
 (10, 10, 8, TRUE, 5010, 9010),
 (11, 11, 1, FALSE, 5011, 9011),
 (12, 12, 10, TRUE, 5012, 9012);

INSERT INTO clases (id_clase, id_curso_capacitacion, id_asesor, id_tema, inicioClase) VALUES
 (1, 1, 1, 1, '2025-01-21'),
 (2, 1, 2, 3, '2025-01-22'),
 (3, 2, 3, 6, '2025-02-06'),
 (4, 3, 4, 7, '2025-02-21'),
 (5, 4, 5, 1, '2025-03-06'),
 (6, 5, 6, 4, '2025-03-21'),
 (7, 6, 7, 5, '2025-04-06'),
 (8, 7, 8, 2, '2025-07-21'),
 (9, 8, 9, 9, '2025-08-06'),
 (10, 9, 10, 7, '2025-08-21'),
 (11, 1, 1, 2, '2025-01-23'),
 (12, 2, 3, 10, '2025-02-07');

-- ---------------------------------------------------------------------------
-- 12. AVANCES (12), MENSUALIDADES (12), ASISTENCIAS (12)
-- ---------------------------------------------------------------------------
INSERT INTO avances (id_avance, id_clase, id_tema) VALUES
 (1, 1, 1),
 (2, 2, 3),
 (3, 3, 6),
 (4, 4, 7),
 (5, 5, 1),
 (6, 6, 4),
 (7, 7, 5),
 (8, 8, 2),
 (9, 9, 9),
 (10, 10, 7),
 (11, 11, 2),
 (12, 12, 10);

INSERT INTO mensualidades (id_mensualidad, id_estudiante, id_administrativo, fecha_inicio_mensualidad, fecha_fin_mensualidad, pago) VALUES
 (1, 1, 1, '2025-01-20', '2025-02-20', 350.00),
 (2, 2, 2, '2025-02-05', '2025-03-05', 400.00),
 (3, 3, 3, '2025-02-20', '2025-03-20', 350.00),
 (4, 4, 4, '2025-03-05', '2025-04-05', 300.00),
 (5, 5, 5, '2025-01-20', '2025-02-20', 350.00),
 (6, 6, 6, '2025-03-20', '2025-04-20', 380.00),
 (7, 7, 7, '2025-07-20', '2025-08-20', 350.00),
 (8, 8, 8, '2025-08-20', '2025-09-20', 320.00),
 (9, 9, 9, '2025-02-05', '2025-03-05', 400.00),
 (10, 10, 10, '2025-08-05', '2025-09-05', 360.00),
 (11, 11, 1, '2025-01-20', '2025-02-20', 350.00),
 (12, 12, 2, '2026-01-15', '2026-02-15', 420.00);

INSERT INTO asistencias (id_asistencia, id_clase, id_estudiante, hora_entrada, salida_antes_de_tiempo, permiso, razon_permiso) VALUES
 (1, 1, 1, '2025-01-21 08:05:00', NULL, FALSE, NULL),
 (2, 1, 5, '2025-01-21 08:10:00', NULL, FALSE, NULL),
 (3, 2, 1, '2025-01-22 08:00:00', '2025-01-22 10:30:00', TRUE, 'Cita médica'),
 (4, 3, 2, '2025-02-06 08:02:00', NULL, FALSE, NULL),
 (5, 3, 9, '2025-02-06 08:15:00', NULL, FALSE, NULL),
 (6, 4, 3, '2025-02-21 14:05:00', NULL, FALSE, NULL),
 (7, 5, 4, '2025-03-06 19:05:00', NULL, TRUE, 'Tráfico'),
 (8, 6, 6, '2025-03-21 09:00:00', NULL, FALSE, NULL),
 (9, 7, 7, '2025-07-21 08:03:00', NULL, FALSE, NULL),
 (10, 8, 6, '2025-04-06 08:30:00', '2025-04-06 10:00:00', TRUE, 'Permiso familiar'),
 (11, 9, 10, '2025-08-06 19:01:00', NULL, FALSE, NULL),
 (12, 10, 8, '2025-08-21 14:00:00', NULL, FALSE, NULL);

COMMIT;

-- ---------------------------------------------------------------------------
-- Reiniciar secuencias IDENTITY al máximo ID insertado
-- ---------------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('usuarios', 'id_usuario'), MAX(id_usuario)) FROM usuarios;
SELECT setval(pg_get_serial_sequence('facultades', 'id_facultad'), MAX(id_facultad)) FROM facultades;
SELECT setval(pg_get_serial_sequence('materias', 'id_materia'), MAX(id_materia)) FROM materias;
SELECT setval(pg_get_serial_sequence('aulas', 'id_aula'), MAX(id_aula)) FROM aulas;
SELECT setval(pg_get_serial_sequence('roles', 'id_rol'), MAX(id_rol)) FROM roles;
SELECT setval(pg_get_serial_sequence('administrativos', 'id_administrativo'), MAX(id_administrativo)) FROM administrativos;
SELECT setval(pg_get_serial_sequence('asesores', 'id_asesor'), MAX(id_asesor)) FROM asesores;
SELECT setval(pg_get_serial_sequence('apoderados', 'id_apoderado'), MAX(id_apoderado)) FROM apoderados;
SELECT setval(pg_get_serial_sequence('estudiantes', 'id_estudiante'), MAX(id_estudiante)) FROM estudiantes;
SELECT setval(pg_get_serial_sequence('asignacion_rol', 'id_asignacion_rol'), MAX(id_asignacion_rol)) FROM asignacion_rol;
SELECT setval(pg_get_serial_sequence('carreras', 'id_carrera'), MAX(id_carrera)) FROM carreras;
SELECT setval(pg_get_serial_sequence('convocatorias_preuniversitario', 'id_convocatoria'), MAX(id_convocatoria)) FROM convocatorias_preuniversitario;
SELECT setval(pg_get_serial_sequence('temas', 'id_tema'), MAX(id_tema)) FROM temas;
SELECT setval(pg_get_serial_sequence('inscripciones', 'id_inscripcion'), MAX(id_inscripcion)) FROM inscripciones;
SELECT setval(pg_get_serial_sequence('materias_asesores', 'id_materia_asesor'), MAX(id_materia_asesor)) FROM materias_asesores;
SELECT setval(pg_get_serial_sequence('postulaciones', 'id_postulacion'), MAX(id_postulacion)) FROM postulaciones;
SELECT setval(pg_get_serial_sequence('cursos_capacitacion', 'id_capacitacion'), MAX(id_capacitacion)) FROM cursos_capacitacion;
SELECT setval(pg_get_serial_sequence('examenes_de_ingreso', 'id_examen_de_ingreso'), MAX(id_examen_de_ingreso)) FROM examenes_de_ingreso;
SELECT setval(pg_get_serial_sequence('materias_convocatoria_asignacion', 'id_asignacion_materia_convocatoria'), MAX(id_asignacion_materia_convocatoria)) FROM materias_convocatoria_asignacion;
SELECT setval(pg_get_serial_sequence('contenidos', 'id_contenido'), MAX(id_contenido)) FROM contenidos;
SELECT setval(pg_get_serial_sequence('asignaciones_asesores', 'id_asignacion_asesor'), MAX(id_asignacion_asesor)) FROM asignaciones_asesores;
SELECT setval(pg_get_serial_sequence('asignaciones_materias', 'id_asignacion_materia'), MAX(id_asignacion_materia)) FROM asignaciones_materias;
SELECT setval(pg_get_serial_sequence('matriculaciones', 'id_matriculacion'), MAX(id_matriculacion)) FROM matriculaciones;
SELECT setval(pg_get_serial_sequence('clases', 'id_clase'), MAX(id_clase)) FROM clases;
SELECT setval(pg_get_serial_sequence('avances', 'id_avance'), MAX(id_avance)) FROM avances;
SELECT setval(pg_get_serial_sequence('mensualidades', 'id_mensualidad'), MAX(id_mensualidad)) FROM mensualidades;
SELECT setval(pg_get_serial_sequence('asistencias', 'id_asistencia'), MAX(id_asistencia)) FROM asistencias;
