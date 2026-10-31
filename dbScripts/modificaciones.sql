-- =============================================================================
-- SCRIPT DE MODIFICACIONES DE BASE DE DATOS
-- =============================================================================
-- Ejecutar sobre una base de datos existente creada con Script.sql.
-- Es idempotente: puede ejecutarse más de una vez sin duplicar la columna ni
-- la restricción.

BEGIN;

-- -----------------------------------------------------------------------------
-- Cursos de capacitación: fecha de finalización
-- -----------------------------------------------------------------------------

ALTER TABLE cursos_capacitacion
    ADD COLUMN IF NOT EXISTS fecha_fin DATE;

COMMENT ON COLUMN cursos_capacitacion.fecha_fin
    IS 'Fecha de finalización del curso de capacitación.';

-- La fecha de fin no puede ser anterior a la fecha de inicio.
ALTER TABLE cursos_capacitacion
    DROP CONSTRAINT IF EXISTS chk_cursos_capacitacion_fechas;

ALTER TABLE cursos_capacitacion
    ADD CONSTRAINT chk_cursos_capacitacion_fechas
    CHECK (
        fecha_inicio IS NULL
        OR fecha_fin IS NULL
        OR fecha_fin >= fecha_inicio
    );

COMMIT;
