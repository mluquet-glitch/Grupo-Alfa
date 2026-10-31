import { Suspense, lazy, useEffect, useState } from "react"
import PropTypes from "prop-types"
import Modal from "../../ui/Modal"
import TextField from "../../ui/TextField"
import Button from "../../ui/Button"
import Skeleton from "../../ui/Skeleton"

// split-component-lazy: el combobox de estudiante se carga junto al modal.
const PersonCombobox = lazy(() => import("./PersonCombobox"))

const toNumberOrUndefined = (value) => {
  if (value === "" || value == null) return undefined
  const n = Number(value)
  return Number.isNaN(n) ? undefined : n
}

export default function MatriculacionFormModal({
  initial,
  fetchEstudiantes,
  cursoOptions,
  saving,
  formError,
  onClose,
  onSubmit,
}) {
  const [idEstudiante, setIdEstudiante] = useState(initial?.id_estudiante ?? null)
  const [idCurso, setIdCurso] = useState(initial?.id_curso_capacitacion ? String(initial.id_curso_capacitacion) : "")
  const [estaVigente, setEstaVigente] = useState(initial?.esta_vigente ?? true)
  const [nroComprobante, setNroComprobante] = useState(initial?.nro_comprobante ?? "")
  const [nroCredencial, setNroCredencial] = useState(initial?.nro_credencial ?? "")
  const [localError, setLocalError] = useState("")

  useEffect(() => {
    const onKey = (event) => {
      if (event.key === "Escape") onClose()
    }
    window.addEventListener("keydown", onKey)
    return () => window.removeEventListener("keydown", onKey)
  }, [onClose])

  const handleSubmit = (event) => {
    event.preventDefault()
    setLocalError("")
    if (idEstudiante == null) {
      setLocalError("Debes elegir un estudiante de las sugerencias.")
      return
    }
    if (!idCurso) {
      setLocalError('El campo "Curso de capacitación" es obligatorio.')
      return
    }
    onSubmit({
      id_estudiante: Number(idEstudiante),
      id_curso_capacitacion: Number(idCurso),
      esta_vigente: Boolean(estaVigente),
      nro_comprobante: toNumberOrUndefined(nroComprobante),
      nro_credencial: toNumberOrUndefined(nroCredencial),
    })
  }

  return (
    <Modal
      title={initial?.id_matriculacion != null ? "Editar matriculación" : "Nueva matriculación"}
      hint="Relaciona un estudiante con un curso de capacitación."
      onClose={onClose}
    >
      <Modal.Body>
        <form onSubmit={handleSubmit}>
          <Suspense fallback={<Skeleton lines={2} />}>
            <PersonCombobox
              id="m-estudiante"
              label="Estudiante"
              fetchOptions={fetchEstudiantes}
              value={idEstudiante}
              onSelect={setIdEstudiante}
              placeholder="Escribe el nombre del estudiante..."
              hint="Escribe para buscar estudiantes en el servidor."
            />
          </Suspense>
          <div className="form-row">
            <label htmlFor="m-curso">Curso de capacitación</label>
            <select id="m-curso" value={idCurso} onChange={(e) => setIdCurso(e.target.value)} required>
              <option value="">Selecciona…</option>
              {cursoOptions.map((c) => (
                <option key={c.value} value={c.value}>{c.label}</option>
              ))}
            </select>
          </div>
          <div className="form-row-inline" style={{ marginTop: 12 }}>
            <input
              id="m-vigente"
              type="checkbox"
              checked={estaVigente}
              onChange={(e) => setEstaVigente(e.target.checked)}
            />
            <label htmlFor="m-vigente">Vigente</label>
          </div>
          <TextField
            id="m-comprobante"
            label="Nro de comprobante (opcional)"
            type="number"
            value={nroComprobante}
            onChange={(e) => setNroComprobante(e.target.value)}
          />
          <TextField
            id="m-credencial"
            label="Nro de credencial (opcional)"
            type="number"
            value={nroCredencial}
            onChange={(e) => setNroCredencial(e.target.value)}
          />
          {(localError || formError) && (
            <p className="error-message" role="alert">{localError || formError}</p>
          )}
          <Modal.Footer>
            <Button variant="secondary" onClick={onClose}>
              Cancelar
            </Button>
            <Button type="submit" disabled={saving}>
              {saving ? "Guardando..." : "Guardar"}
            </Button>
          </Modal.Footer>
        </form>
      </Modal.Body>
    </Modal>
  )
}

MatriculacionFormModal.propTypes = {
  initial: PropTypes.object,
  fetchEstudiantes: PropTypes.func.isRequired,
  cursoOptions: PropTypes.array.isRequired,
  saving: PropTypes.bool.isRequired,
  formError: PropTypes.string.isRequired,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
}
