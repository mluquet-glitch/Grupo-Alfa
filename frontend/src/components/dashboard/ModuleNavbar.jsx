import { Link, useLocation } from "react-router-dom"
import PropTypes from "prop-types"
import { resolveModuleTab } from "../../config/moduleTabs"

export default function ModuleNavbar({ config }) {
  const location = useLocation()
  if (!config) return null

  const activeTab = resolveModuleTab(config, location.search)

  return (
    <nav className="module-navbar" aria-label="Opciones del módulo">
      {config.items.map((item) => {
        const isActive = item.key === activeTab
        return (
          <Link
            key={item.key}
            to={`${config.path}?tab=${encodeURIComponent(item.key)}`}
            className={isActive ? "module-navbar-link active" : "module-navbar-link"}
            aria-current={isActive ? "page" : undefined}
          >
            {item.label}
          </Link>
        )
      })}
    </nav>
  )
}

ModuleNavbar.propTypes = {
  config: PropTypes.shape({
    path: PropTypes.string.isRequired,
    defaultTab: PropTypes.string.isRequired,
    items: PropTypes.arrayOf(
      PropTypes.shape({
        key: PropTypes.string.isRequired,
        label: PropTypes.string.isRequired,
      }),
    ).isRequired,
  }),
}
