import { Suspense, lazy, useState } from "react"
import { useSearchParams } from "react-router-dom"
import { ENTITIES } from "../../config/entities"
import Skeleton from "../../components/ui/Skeleton"

// split-component-lazy: el CRUD (pesado: tabla + modal + formulario) vive en su
// propio chunk y solo se monta la pestaña activa — las inactivas no piden datos.
// split-prefetch-hints: `preloadCrud` calienta el chunk al pasar el cursor/foco
// por una pestaña antes del clic.
const loadCrud = () => import("../../components/dashboard/CrudPage.jsx")
const CrudPanel = lazy(loadCrud)
const preloadCrud = () => loadCrud()

const TABS = ["capacitaciones", "convocatorias", "facultades", "aulas"]
const CURSOS_TABS = ["materias", "temas"]

export default function CapacitacionesPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const requestedTab = searchParams.get("tab")
  const active = TABS.includes(requestedTab) ? requestedTab : "capacitaciones"
  const [activeCurso, setActiveCurso] = useState("materias")
  const entity = ENTITIES[active]
  const cursoEntity = ENTITIES[activeCurso]

  const handleTabChange = (key) => {
    const next = new URLSearchParams(searchParams)
    next.set("tab", key)
    setSearchParams(next, { replace: true })
  }

  return (
    <section>
      <div className="toggle-group" role="tablist" aria-label="Módulos de capacitaciones">
        {TABS.map((key) => (
          <button
            key={key}
            role="tab"
            aria-selected={active === key}
            type="button"
            className={active === key ? "toggle-chip active" : "toggle-chip"}
            onClick={() => handleTabChange(key)}
            onMouseEnter={preloadCrud}
            onFocus={preloadCrud}
          >
            {ENTITIES[key].label}
          </button>
        ))}
      </div>
      <p className="muted" style={{ marginTop: 12 }}>
        {active === "capacitaciones" && "Cursos de capacitación. Requieren convocatoria y aula existentes."}
        {active === "convocatorias" && "Convocatorias de preuniversitario. Requieren facultad existente."}
        {active === "facultades" && "Facultades a las que pertenecen carreras y convocatorias."}
        {active === "aulas" && "Aulas donde se dictan los cursos."}
      </p>
      <Suspense fallback={<Skeleton lines={6} />}>
        <CrudPanel key={entity.key} entity={entity} />
      </Suspense>

      <h2 style={{ marginTop: 40 }}>Cursos</h2>
      <p className="muted">Materias y temas que conforman los cursos.</p>
      <div className="toggle-group" role="tablist" aria-label="Módulos de cursos">
        {CURSOS_TABS.map((key) => (
          <button
            key={key}
            role="tab"
            aria-selected={activeCurso === key}
            type="button"
            className={activeCurso === key ? "toggle-chip active" : "toggle-chip"}
            onClick={() => setActiveCurso(key)}
            onMouseEnter={preloadCrud}
            onFocus={preloadCrud}
          >
            {ENTITIES[key].label}
          </button>
        ))}
      </div>
      <Suspense fallback={<Skeleton lines={6} />}>
        <CrudPanel key={cursoEntity.key} entity={cursoEntity} />
      </Suspense>
    </section>
  )
}
