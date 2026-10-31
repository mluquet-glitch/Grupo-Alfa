import { useEffect, useMemo, useState } from "react"
import PropTypes from "prop-types"
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  ScatterChart,
  Scatter,
  Cell,
  ReferenceLine,
} from "recharts"
import { ENTITIES, ENTITY_ORDER } from "../../config/entities"
import { canAccessModule } from "../../config/permissions"
import { useAuth } from "../../hooks/useAuth"
import { listResource } from "../../services/academicService"
import Card from "../ui/Card"
import Skeleton from "../ui/Skeleton"

const COLORS = ["#2196f3", "#4fc3f7", "#1976d2", "#b3e5fc", "#0d47a1", "#64b5f6", "#42a5f5"]
const CHART_KEYS = ["usuarios", "materias", "asesores", "capacitaciones", "matriculaciones", "temas", "estudiantes"]

function safeRows(data) {
  return Array.isArray(data) ? data : []
}

function shortenLabel(value, max = 22) {
  const text = String(value ?? "")
  return text.length > max ? `${text.slice(0, max - 1)}…` : text
}

function OccupancyTooltip({ active, payload }) {
  if (!active || !payload?.length) return null
  const point = payload[0]?.payload
  if (!point) return null
  return (
    <div className="chart-tooltip">
      <p className="chart-tooltip-title">{point.capacitacion ?? point.curso}</p>
      <p>{point.aula}</p>
      <p>
        <strong>{point.ocupados}</strong> de {point.capacidad} estudiantes ({point.porcentaje}%)
      </p>
    </div>
  )
}

OccupancyTooltip.propTypes = {
  active: PropTypes.bool,
  payload: PropTypes.array,
}

export default function DashboardHome({ onNavigate }) {
  const { loggedUser } = useAuth()
  const visibleKeys = useMemo(
    () => ENTITY_ORDER.filter((key) => canAccessModule(loggedUser, key)),
    [loggedUser],
  )

  const [resources, setResources] = useState({})
  const [counts, setCounts] = useState({})
  const [rolesData, setRolesData] = useState([])
  const [asignacionesRolData, setAsignacionesRolData] = useState([])
  const [aulasData, setAulasData] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    let mounted = true
    async function load() {
      setIsLoading(true)
      setError("")
      try {
        const [
          usuariosResult,
          materiasResult,
          asesoresResult,
          capacitacionesResult,
          matriculacionesResult,
          temasResult,
          estudiantesResult,
          rolesResult,
          asignacionesRolResult,
          aulasResult,
        ] = await Promise.allSettled([
          listResource(ENTITIES.usuarios.endpoint),
          listResource(ENTITIES.materias.endpoint),
          listResource(ENTITIES.asesores.endpoint),
          listResource(ENTITIES.capacitaciones.endpoint),
          listResource(ENTITIES.matriculaciones.endpoint),
          listResource(ENTITIES.temas.endpoint),
          listResource(ENTITIES.estudiantes.endpoint),
          listResource("roles"),
          listResource("asignaciones-rol"),
          listResource("aulas"),
        ])

        const safe = (result) =>
          result.status === "fulfilled" ? safeRows(result.value) : []

        const recursos = {
          usuarios: safe(usuariosResult),
          materias: safe(materiasResult),
          asesores: safe(asesoresResult),
          capacitaciones: safe(capacitacionesResult),
          matriculaciones: safe(matriculacionesResult),
          temas: safe(temasResult),
          estudiantes: safe(estudiantesResult),
        }

        const rolesData = safe(rolesResult)
        const asignacionesRolData = safe(asignacionesRolResult)
        const aulasData = safe(aulasResult)

        if (mounted) {
          setResources(recursos)
          setRolesData(rolesData)
          setAsignacionesRolData(asignacionesRolData)
          setAulasData(aulasData)
          setCounts(
            Object.fromEntries(
              Object.entries(recursos).map(([k, v]) => [k, v.length]),
            ),
          )
        }
      } catch {
        if (mounted) setError("No se pudo cargar los datos del dashboard.")
      } finally {
        if (mounted) setIsLoading(false)
      }
    }
    load()
    return () => {
      mounted = false
    }
  }, [])

  const barData = useMemo(() => {
    return CHART_KEYS.map((key, i) => ({
      name: ENTITIES[key]?.label ?? key,
      value: resources[key]?.length ?? 0,
      color: COLORS[i % COLORS.length],
    }))
  }, [resources])

  const rolesPorUsuario = useMemo(() => {
    const conteo = {}
    asignacionesRolData.forEach((asig) => {
      const rolId = asig.id_rol
      const rol = rolesData.find((r) => r.id_rol === rolId)
      const nombre = rol ? rol.nombre : `Rol ${rolId}`
      conteo[nombre] = (conteo[nombre] ?? 0) + 1
    })
    return Object.entries(conteo).map(([nombre, cantidad]) => ({ nombre, cantidad }))
  }, [rolesData, asignacionesRolData])

  const pieData = useMemo(() => {
    return CHART_KEYS
      .filter((key) => (resources[key]?.length ?? 0) > 0)
      .map((key) => ({
        name: ENTITIES[key]?.label ?? key,
        value: resources[key].length,
      }))
  }, [resources])

  const scatterData = useMemo(() => {
    const puntos = []
    CHART_KEYS.forEach((key, i) => {
      const rows = resources[key] ?? []
      rows.forEach((row, j) => {
        puntos.push({
          x: j,
          y: (resources[key]?.length ?? 0) + i * 10,
          z: (row.id_usuario ?? row.id_estudiante ?? row.id_capacitacion ?? row.id_matriculacion ?? j) % 50 + 5,
          name: `${ENTITIES[key]?.label ?? key} #${j + 1}`,
        })
      })
    })
    return puntos
  }, [resources])

  const ocupacionPorCapacitacion = useMemo(() => {
    const capacitaciones = resources.capacitaciones ?? []
    const aulas = aulasData ?? []
    const matriculaciones = resources.matriculaciones ?? []

    const mapaAulas = new Map(aulas.map((a) => [String(a.id_aula), a]))

    const estudiantesPorCurso = new Map()
    matriculaciones.forEach((m) => {
      if (m.esta_vigente === false) return
      const cursoId = String(m.id_curso_capacitacion)
      if (!estudiantesPorCurso.has(cursoId)) estudiantesPorCurso.set(cursoId, new Set())
      estudiantesPorCurso
        .get(cursoId)
        .add(String(m.id_estudiante ?? m.id_matriculacion))
    })

    return capacitaciones.map((c) => {
      const aula = mapaAulas.get(String(c.id_aula))
      const capacidad = Number(aula?.capacidad) || 0
      const ocupados = estudiantesPorCurso.get(String(c.id_capacitacion))?.size ?? 0
      const porcentaje = capacidad > 0 ? Math.min((ocupados / capacidad) * 100, 100) : 0
      const aulaLabel = aula ? `Aula ${aula.id_aula}` : "Sin aula"
      const capacitacionLabel = c.nombre || `Capacitación ${c.id_capacitacion}`
      return {
        curso: capacitacionLabel,
        capacitacion: capacitacionLabel,
        aula: aulaLabel,
        capacidad,
        ocupados,
        porcentaje: Math.round(porcentaje),
      }
    })
  }, [resources, aulasData])

  if (isLoading) return <Skeleton lines={5} />
  if (error) return <p className="error-message">{error}</p>

  return (
    <section>
      <h2>Resumen académico</h2>
      <p className="muted">Datos reales de la base de datos: usuarios, materias, asesores, capacitaciones, matriculaciones, temas y estudiantes.</p>

      <div className="cards">
        {visibleKeys.map((key) => (
          <Card key={key} onClick={() => onNavigate(key)} label={ENTITIES[key].label}>
            <Card.Header>
              <span className="card-label">{ENTITIES[key].label}</span>
            </Card.Header>
            <Card.Body>
              <span className="card-count">{counts[key] ?? "—"}</span>
            </Card.Body>
            <Card.Footer>
              <span className="card-link">Abrir CRUD →</span>
            </Card.Footer>
          </Card>
        ))}
      </div>

      <div className="dashboard-grid">
        <section className="chart-panel">
          <h3>Usuarios por rol (Barras)</h3>
          <div className="chart-wrap">
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={rolesPorUsuario}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="nombre" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="cantidad" fill="#2196f3" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </section>

        <section className="chart-panel chart-panel-wide">
          <h3>Ocupación en aula por curso (Dispersión)</h3>
          <p className="muted chart-panel-hint">
            Eje Y: cursos. Eje X: porcentaje de ocupación del aula. Cada punto representa un curso.
          </p>
          {ocupacionPorCapacitacion.length === 0 ? (
            <p className="muted">No hay capacitaciones registradas para mostrar la ocupación.</p>
          ) : (
            <div className="chart-wrap">
              <ResponsiveContainer width="100%" height={340}>
                <ScatterChart margin={{ top: 12, right: 24, bottom: 24, left: 8 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis
                    dataKey="porcentaje"
                    type="number"
                    domain={[0, 100]}
                    tick={{ fontSize: 11 }}
                    tickFormatter={(value) => `${value}%`}
                    label={{ value: "Ocupación en aula (%)", position: "insideBottom", offset: -14 }}
                  />
                  <YAxis
                    dataKey="curso"
                    type="category"
                    allowDuplicatedCategory={false}
                    interval={0}
                    width={180}
                    tick={{ fontSize: 11 }}
                    tickFormatter={(value) => shortenLabel(value, 24)}
                    label={{ value: "Curso", angle: -90, position: "insideLeft" }}
                  />
                  <Tooltip content={<OccupancyTooltip />} cursor={{ strokeDasharray: "3 3" }} />
                  <Scatter name="Ocupación" data={ocupacionPorCapacitacion} fill="#2196f3" />
                </ScatterChart>
              </ResponsiveContainer>
            </div>
          )}
        </section>
      </div>
    </section>
  )
}

DashboardHome.propTypes = {
  onNavigate: PropTypes.func.isRequired,
}
