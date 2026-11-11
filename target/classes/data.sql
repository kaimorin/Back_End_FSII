-- ── Especialistas (mismos del frontend: Home.jsx y Agenda.jsx) ────────
INSERT INTO especialistas (nombre, especialidad, modalidad, descripcion, activo)
VALUES
  ('Maria de Judas',   'Nutrición Clínica',   'Presencial y Online',
   'Experta en el manejo de enfermedades metabólicas. Crea dietas terapéuticas para controlar condiciones como diabetes, resistencia a la insulina e hipertensión.', true),

  ('Maximo Tul''Onazo', 'Nutrición Deportiva', 'Presencial',
   'Enfocado en la recomposición corporal y el rendimiento físico. Diseña planes para aumentar masa muscular, mejorar la recuperación y optimizar la energía.', true),

  ('Mario Di West',    'Control de Peso',     'Online',
   'Centrado en la pérdida de grasa sostenible y la educación alimentaria. Trabaja en modificar hábitos a largo plazo sin utilizar dietas extremas o restrictivas.', true),

  ('Shaqueal O''neal', 'Salud Digestiva',     'Presencial y Online',
   'Experto en el microbioma intestinal. Su enfoque está en tratar inflamaciones, síndrome de intestino irritable y alergias alimentarias para mejorar la digestión.', true);

-- ── Pacientes de ejemplo (coinciden con Dashboard.jsx) ─────────────
INSERT INTO pacientes (nombre, apellido, rut, telefono, comuna, ciudad, patologias, destinatario)
VALUES
  ('Lucas',  'Gómez',   '12.345.678-9', '+56 9 1111 1111', 'santiago',    'Santiago', NULL, 'para-mi'),
  ('Elena',  'Rostova',  '13.456.789-0', '+56 9 2222 2222', 'providencia', 'Santiago', NULL, 'para-mi'),
  ('Camila', 'Rojas',    '14.567.890-1', '+56 9 3333 3333', 'las-condes',  'Santiago', NULL, 'para-mi');

-- ── Citas de ejemplo (coinciden con Dashboard.jsx) ──────────────────
INSERT INTO citas (doctor, especialidad, modalidad, fecha, hora, metodo_pago, estado,
                   paciente_nombre, paciente_apellido, paciente_rut, paciente_telefono, precio_base)
VALUES
  ('Maria de Judas',   'control-peso',       'Presencial',
   'Miércoles, 19 de febrero de 2025', '9:30 AM',  'webpay',      'Confirmado',
   'Kanye', 'West',   '15.000.000-1', '+56 9 9999 9999', 35000),

  ('Maximo Tul''Onazo', 'nutricion-deportiva', 'Online',
   'Miércoles, 19 de febrero de 2025', '10:15 AM', 'mercadopago', 'Pendiente',
   'Elena', 'Rostova', '13.456.789-0', '+56 9 2222 2222', 35000),

  ('Maria de Judas',   'nutricion-clinica',   'Presencial',
   'Miércoles, 19 de febrero de 2025', '11:00 AM', 'webpay',      'Atendido',
   'Lucas', 'Gómez',   '12.345.678-9', '+56 9 1111 1111', 35000);

-- ── Usuario admin de ejemplo ─────────────────────────────────────────
-- Contraseña: Admin1234!  (BCrypt hash — NO es texto plano)
INSERT INTO usuarios (nombre, apellido, correo, rut, telefono, recibir_noticias, rol, clave)
VALUES ('Admin', 'Sistema', 'admin@nutrivida.cl', '11.111.111-1', '+56 9 0000 0000', false, 'ADMIN',
        '$2a$10$8K1p/a0dL1LXMIgoEDFrwOXaiCBG.WzFtAjMrPvM4RFO3RfEKvmHm');
