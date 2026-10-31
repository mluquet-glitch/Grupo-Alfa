import { Suspense, lazy } from "react"
import { useSearchParams } from "react-router-dom"
import { ENTITIES } from "../../config/entities"
import Skeleton from "../../components/ui/Skeleton"

// split-component-lazy: el CRUD (pesado: tabla + modal + formulario) vive en su
// propio chunk y solo se monta la pestaña activa — las inactivas no piden datos.
const loadCrud = () => import("../../components/dashboard/CrudPage.jsx")
const CrudPanel = lazy(loadCrud)

const TABS = ["capacitaciones", "convocatorias", "facultades", "aulas", "materias", "temas"]
const TAB_HINTS = {
  capacitaciones: "Cursos de capacitación. Requieren convocatoria y aula existentes.",
  convocatorias: "Convocatorias de preuniversitario. Requieren facultad existente.",
  facultades: "Facultades a las que pertenecen carreras y convocatorias.",
  aulas: "Aulas donde se dictan los cursos.",
  materias: "Materias que conforman los cursos.",
  temas: "Temas asociados a una materia.",
}

export default function CapacitacionesPage() {
  const [searchParams] = useSearchParams()
  const requestedTab = searchParams.get("tab")
  const active = TABS.includes(requestedTab) ? requestedTab : "capacitaciones"
  const entity = ENTITIES[active]

  return (
    <section>
      <p className="muted">{TAB_HINTS[active]}</p>
      <Suspense fallback={<Skeleton lines={6} />}>
        <CrudPanel key={entity.key} entity={entity} />
      </Suspense>
    </section>
  )
}
