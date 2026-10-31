-- =============================================================================
-- SCRIPT DE AUDITORÍA — Tablas de auditoría y triggers para todas las tablas
-- Base de datos: PostgreSQL
-- Proyecto: Sistema Académico Grupo Alfa
-- =============================================================================

-- =============================================================================
-- 1. TABLA MAESTRA DE AUDITORÍA (única para todas las entidades)
-- =============================================================================

CREATE TABLE IF NOT EXISTS auditoria (
    id_auditoria SERIAL PRIMARY KEY,
    tabla_origen VARCHAR(100) NOT NULL,
    id_registro_origen INT NOT NULL,
    accion VARCHAR(10) NOT NULL CHECK (accion IN ('INSERT', 'UPDATE', 'DELETE')),
    valores_antiguos JSONB,
    valores_nuevos JSONB,
    usuario_id INT,
    usuario_nombre VARCHAR(150),
    fecha_operacion TIMESTAMP NOT NULL DEFAULT now(),
    ip_origen VARCHAR(45),
    observaciones TEXT
);

CREATE INDEX idx_auditoria_tabla_id ON auditoria(tabla_origen, id_registro_origen);
CREATE INDEX idx_auditoria_accion ON auditoria(accion);
CREATE INDEX idx_auditoria_fecha ON auditoria(fecha_operacion);
CREATE INDEX idx_auditoria_usuario ON auditoria(usuario_id);

-- =============================================================================
-- 2. FUNCIÓN GENÉRICA DE AUDITORÍA
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_auditar_cambio()
RETURNS TRIGGER AS $$
DECLARE
    v_valores_antiguos JSONB;
    v_valores_nuevos JSONB;
    v_accion VARCHAR(10);
    v_tabla VARCHAR(100);
BEGIN
    v_tabla := TG_TABLE_NAME;

    IF TG_OP = 'DELETE' THEN
        v_accion := 'DELETE';
        v_valores_antiguos := to_jsonb(OLD);
        v_valores_nuevos := NULL;
    ELSIF TG_OP = 'UPDATE' THEN
        v_accion := 'UPDATE';
        v_valores_antiguos := to_jsonb(OLD);
        v_valores_nuevos := to_jsonb(NEW);
    ELSIF TG_OP = 'INSERT' THEN
        v_accion := 'INSERT';
        v_valores_antiguos := NULL;
        v_valores_nuevos := to_jsonb(NEW);
    END IF;

    INSERT INTO auditoria (
        tabla_origen,
        id_registro_origen,
        accion,
        valores_antiguos,
        valores_nuevos,
        usuario_id,
        usuario_nombre,
        fecha_operacion,
        ip_origen,
        observaciones
    ) VALUES (
        v_tabla,
        COALESCE(NEW.id_usuario, OLD.id_usuario, NEW.id_estudiante, OLD.id_estudiante,
                 NEW.id_asesor, OLD.id_asesor, NEW.id_administrativo, OLD.id_administrativo,
                 NEW.id_apoderado, OLD.id_apoderado, NEW.id_matriculacion, OLD.id_matriculacion,
                 NEW.id_capacitacion, OLD.id_capacitacion, NEW.id_clase, OLD.id_clase,
                 NEW.id_inscripcion, OLD.id_inscripcion, NEW.id_postulacion, OLD.id_postulacion,
                 NEW.id_mensualidad, OLD.id_mensualidad, NEW.id_asistencia, OLD.id_asistencia,
                 NEW.id_avance, OLD.id_avance, NEW.id_contenido, OLD.id_contenido,
                 NEW.id_examen_de_ingreso, OLD.id_examen_de_ingreso,
                 NEW.id_convocatoria, OLD.id_convocatoria,
                 NEW.id_facultad, OLD.id_facultad,
                 NEW.id_carrera, OLD.id_carrera,
                 NEW.id_aula, OLD.id_aula,
                 NEW.id_materia, OLD.id_materia,
                 NEW.id_tema, OLD.id_tema,
                 NEW.id_rol, OLD.id_rol,
                 NEW.id_asignacion_rol, OLD.id_asignacion_rol),
        v_accion,
        v_valores_antiguos,
        v_valores_nuevos,
        current_setting('app.usuario_id', true)::INT,
        current_setting('app.usuario_nombre', true),
        now(),
        current_setting('app.ip_origen', true),
        current_setting('app.observaciones', true)
    );

    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =============================================================================
-- 3. TRIGGERS INDIVIDUALES PARA CADA TABLA
-- =============================================================================

-- Usuarios
DROP TRIGGER IF EXISTS trg_auditoria_usuarios ON usuarios;
CREATE TRIGGER trg_auditoria_usuarios
    AFTER INSERT OR UPDATE OR DELETE ON usuarios
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Administrativos
DROP TRIGGER IF EXISTS trg_auditoria_administrativos ON administrativos;
CREATE TRIGGER trg_auditoria_administrativos
    AFTER INSERT OR UPDATE OR DELETE ON administrativos
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Facultades
DROP TRIGGER IF EXISTS trg_auditoria_facultades ON facultades;
CREATE TRIGGER trg_auditoria_facultades
    AFTER INSERT OR UPDATE OR DELETE ON facultades
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Aulas
DROP TRIGGER IF EXISTS trg_auditoria_aulas ON aulas;
CREATE TRIGGER trg_auditoria_aulas
    AFTER INSERT OR UPDATE OR DELETE ON aulas
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Asesores
DROP TRIGGER IF EXISTS trg_auditoria_asesores ON asesores;
CREATE TRIGGER trg_auditoria_asesores
    AFTER INSERT OR UPDATE OR DELETE ON asesores
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Materias
DROP TRIGGER IF EXISTS trg_auditoria_materias ON materias;
CREATE TRIGGER trg_auditoria_materias
    AFTER INSERT OR UPDATE OR DELETE ON materias
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Temas
DROP TRIGGER IF EXISTS trg_auditoria_temas ON temas;
CREATE TRIGGER trg_auditoria_temas
    AFTER INSERT OR UPDATE OR DELETE ON temas
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Apoderados
DROP TRIGGER IF EXISTS trg_auditoria_apoderados ON apoderados;
CREATE TRIGGER trg_auditoria_apoderados
    AFTER INSERT OR UPDATE OR DELETE ON apoderados
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Estudiantes
DROP TRIGGER IF EXISTS trg_auditoria_estudiantes ON estudiantes;
CREATE TRIGGER trg_auditoria_estudiantes
    AFTER INSERT OR UPDATE OR DELETE ON estudiantes
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Inscripciones
DROP TRIGGER IF EXISTS trg_auditoria_inscripciones ON inscripciones;
CREATE TRIGGER trg_auditoria_inscripciones
    AFTER INSERT OR UPDATE OR DELETE ON inscripciones
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Roles
DROP TRIGGER IF EXISTS trg_auditoria_roles ON roles;
CREATE TRIGGER trg_auditoria_roles
    AFTER INSERT OR UPDATE OR DELETE ON roles
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- AsignacionRol
DROP TRIGGER IF EXISTS trg_auditoria_asignacion_rol ON asignacion_rol;
CREATE TRIGGER trg_auditoria_asignacion_rol
    AFTER INSERT OR UPDATE OR DELETE ON asignacion_rol
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Carreras
DROP TRIGGER IF EXISTS trg_auditoria_carreras ON carreras;
CREATE TRIGGER trg_auditoria_carreras
    AFTER INSERT OR UPDATE OR DELETE ON carreras
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Convocatorias Preuniversitario
DROP TRIGGER IF EXISTS trg_auditoria_convocatorias_preuniversitario ON convocatorias_preuniversitario;
CREATE TRIGGER trg_auditoria_convocatorias_preuniversitario
    AFTER INSERT OR UPDATE OR DELETE ON convocatorias_preuniversitario
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Examenes de ingreso
DROP TRIGGER IF EXISTS trg_auditoria_examenes_de_ingreso ON examenes_de_ingreso;
CREATE TRIGGER trg_auditoria_examenes_de_ingreso
    AFTER INSERT OR UPDATE OR DELETE ON examenes_de_ingreso
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Contenidos
DROP TRIGGER IF EXISTS trg_auditoria_contenidos ON contenidos;
CREATE TRIGGER trg_auditoria_contenidos
    AFTER INSERT OR UPDATE OR DELETE ON contenidos
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Materias - Asesores
DROP TRIGGER IF EXISTS trg_auditoria_materias_asesores ON materias_asesores;
CREATE TRIGGER trg_auditoria_materias_asesores
    AFTER INSERT OR UPDATE OR DELETE ON materias_asesores
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Postulaciones
DROP TRIGGER IF EXISTS trg_auditoria_postulaciones ON postulaciones;
CREATE TRIGGER trg_auditoria_postulaciones
    AFTER INSERT OR UPDATE OR DELETE ON postulaciones
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Cursos de Capacitación
DROP TRIGGER IF EXISTS trg_auditoria_cursos_capacitacion ON cursos_capacitacion;
CREATE TRIGGER trg_auditoria_cursos_capacitacion
    AFTER INSERT OR UPDATE OR DELETE ON cursos_capacitacion
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Materias por Convocatoria
DROP TRIGGER IF EXISTS trg_auditoria_materias_convocatoria_asignacion ON materias_convocatoria_asignacion;
CREATE TRIGGER trg_auditoria_materias_convocatoria_asignacion
    AFTER INSERT OR UPDATE OR DELETE ON materias_convocatoria_asignacion
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Asignaciones de Asesores
DROP TRIGGER IF EXISTS trg_auditoria_asignaciones_asesores ON asignaciones_asesores;
CREATE TRIGGER trg_auditoria_asignaciones_asesores
    AFTER INSERT OR UPDATE OR DELETE ON asignaciones_asesores
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Asignaciones de Materias
DROP TRIGGER IF EXISTS trg_auditoria_asignaciones_materias ON asignaciones_materias;
CREATE TRIGGER trg_auditoria_asignaciones_materias
    AFTER INSERT OR UPDATE OR DELETE ON asignaciones_materias
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Matriculaciones
DROP TRIGGER IF EXISTS trg_auditoria_matriculaciones ON matriculaciones;
CREATE TRIGGER trg_auditoria_matriculaciones
    AFTER INSERT OR UPDATE OR DELETE ON matriculaciones
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Clases
DROP TRIGGER IF EXISTS trg_auditoria_clases ON clases;
CREATE TRIGGER trg_auditoria_clases
    AFTER INSERT OR UPDATE OR DELETE ON clases
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Avances
DROP TRIGGER IF EXISTS trg_auditoria_avances ON avances;
CREATE TRIGGER trg_auditoria_avances
    AFTER INSERT OR UPDATE OR DELETE ON avances
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Mensualidades
DROP TRIGGER IF EXISTS trg_auditoria_mensualidades ON mensualidades;
CREATE TRIGGER trg_auditoria_mensualidades
    AFTER INSERT OR UPDATE OR DELETE ON mensualidades
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- Asistencias
DROP TRIGGER IF EXISTS trg_auditoria_asistencias ON asistencias;
CREATE TRIGGER trg_auditoria_asistencias
    AFTER INSERT OR UPDATE OR DELETE ON asistencias
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambio();

-- =============================================================================
-- FIN DEL SCRIPT DE AUDITORÍA
-- =============================================================================
