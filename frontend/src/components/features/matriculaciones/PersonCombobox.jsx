import { useEffect, useMemo, useRef, useState } from "react"
import PropTypes from "prop-types"

// Combobox de personas con dos modos:
// - `options`: filtrado local sobre una lista ya cargada.
// - `fetchOptions`: búsqueda remota al escribir.
export default function PersonCombobox({
  id,
  label,
  options,
  fetchOptions,
  value,
  onSelect,
  placeholder,
  hint,
}) {
  const [text, setText] = useState("")
  const [open, setOpen] = useState(false)
  const [remoteMatches, setRemoteMatches] = useState([])
  const [isLoading, setIsLoading] = useState(false)
  const requestRef = useRef(0)

  const isRemote = typeof fetchOptions === "function"

  const selected = useMemo(
    () => (options ?? []).find((option) => String(option.value) === String(value)),
    [options, value],
  )

  useEffect(() => {
    if (selected) setText(selected.label)
  }, [selected])

  const localMatches = useMemo(() => {
    if (!options) return []
    const term = text.trim().toLowerCase()
    if (!term) return []
    return options
      .filter((option) => option.label.toLowerCase().includes(term) && String(option.value) !== String(value))
      .slice(0, 8)
  }, [options, text, value])

  const matches = isRemote ? remoteMatches : localMatches

  const choose = (option) => {
    setText(option.label)
    setOpen(false)
    setRemoteMatches([])
    onSelect(option.value)
  }

  const handleChange = async (event) => {
    const term = event.target.value
    setText(term)
    setOpen(true)
    if (value != null) onSelect(null)

    if (!isRemote) return

    const requestId = requestRef.current + 1
    requestRef.current = requestId

    if (!term.trim()) {
      setRemoteMatches([])
      setIsLoading(false)
      return
    }

    setIsLoading(true)
    try {
      const results = await fetchOptions(term)
      if (requestId !== requestRef.current) return
      setRemoteMatches(
        (Array.isArray(results) ? results : []).slice(0, 8),
      )
    } catch (error) {
      if (requestId !== requestRef.current) return
      console.error(`Error buscando ${label}:`, error)
      setRemoteMatches([])
    } finally {
      if (requestId === requestRef.current) setIsLoading(false)
    }
  }

  return (
    <div className="form-row">
      <label htmlFor={id}>{label}</label>
      <div className="combobox-wrap">
        <input
          id={id}
          type="text"
          value={text}
          placeholder={placeholder ?? "Escribe para buscar..."}
          autoComplete="off"
          onChange={handleChange}
          onFocus={() => setOpen(true)}
          onBlur={() => setOpen(false)}
        />
        {open && isLoading && (
          <div className="combobox-list" role="listbox" aria-label={`Buscando ${label}...`}>
            <div className="combobox-option">Buscando…</div>
          </div>
        )}
        {open && !isLoading && matches.length > 0 && (
          <div className="combobox-list" role="listbox" aria-label={`Sugerencias de ${label}`}>
            {matches.map((option) => (
              <button
                key={option.value}
                type="button"
                role="option"
                aria-selected="false"
                className="combobox-option"
                onMouseDown={(event) => {
                  event.preventDefault()
                  choose(option)
                }}
              >
                {option.label}
              </button>
            ))}
          </div>
        )}
        {open && !isLoading && matches.length === 0 && text.trim() && (
          <div className="combobox-list" role="listbox" aria-label={`Sin resultados para ${label}`}>
            <div className="combobox-option">No se encontraron resultados</div>
          </div>
        )}
      </div>
      {value != null && text ? (
        <p className="muted" style={{ margin: "4px 0 0" }}>Seleccionado: {text}</p>
      ) : (
        hint && <p className="muted" style={{ margin: "4px 0 0" }}>{hint}</p>
      )}
    </div>
  )
}

PersonCombobox.propTypes = {
  id: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  options: PropTypes.arrayOf(
    PropTypes.shape({
      value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
      label: PropTypes.string.isRequired,
    }),
  ),
  fetchOptions: PropTypes.func,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onSelect: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  hint: PropTypes.string,
}
