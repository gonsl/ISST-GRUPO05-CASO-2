INSERT INTO autor (email, nombre) VALUES ('correo1@gmail.com', 'autor1');

INSERT INTO resumen (titulo, descripcion, prime, contenido, autor_id)
VALUES (
    'Historia de la Inteligencia Artificial',
    'Resumen breve sobre la evolución de la IA.',
    false,
    'La inteligencia artificial comenzó como una rama de la informática en los años 50. Desde entonces, ha evolucionado en múltiples ramas incluyendo aprendizaje automático, redes neuronales y procesamiento del lenguaje natural...',
    1
);

INSERT INTO resumen (titulo, descripcion, prime, contenido, autor_id)
VALUES (
    'Resumen sobre el Renacimiento',
    'Una introducción a la era del Renacimiento en Europa.',
    true,
    'El Renacimiento fue un período de la historia europea que marcó el final de la Edad Media y el inicio de la Edad Moderna. Se caracterizó por un renovado interés en el arte, la ciencia y la filosofía clásica...',
    1
);
