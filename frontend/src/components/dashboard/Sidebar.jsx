import { Link, NavLink, useLocation } from "react-router-dom"
import BrandLogo from "../ui/BrandLogo"
import { ENTITIES, ENTITY_ORDER } from "../../config/entities"
import { canAccessModule } from "../../config/permissions"
import { useAuth } from "../../hooks/useAuth"

const lazyCrud = () => import("./CrudPage.jsx")
const lazyHome = () => import("./DashboardHome.jsx")
const lazyPaseLista = () => import("../../pages/app/PaseListaPage.jsx")
const lazyDataExplorer = () => import("../../pages/app/DataExplorerPage.jsx")

// Sub-opciones que aparecen al pasar el mouse por un módulo del sidebar.
// `defaults` indica la pestaña activa cuando la URL no trae el parámetro.
const SUBMENUS = {
  capacitaciones: {
    defaults: { tab: "capacitaciones" },
    items: [
      { key: "capacitaciones", label: "Capacitaciones", to: "/app/capacitaciones?tab=capacitaciones" },
      { key: "convocatorias", label: "Convocatorias", to: "/app/capacitaciones?tab=convocatorias" },
      { key: "facultades", label: "Facultades", to: "/app/capacitaciones?tab=facultades" },
      { key: "aulas", label: "Aulas", to: "/app/capacitaciones?tab=aulas" },
    ],
  },
  matriculaciones: {
    defaults: { tab: "matriculaciones" },
    items: [
      { key: "matriculaciones", label: "Matriculaciones", to: "/app/matriculaciones?tab=matriculaciones" },
      { key: "mensualidades", label: "Mensualidades", to: "/app/matriculaciones?tab=mensualidades" },
    ],
  },
  "pase-lista": {
    defaults: { tab: "asistencias" },
    items: [
      { key: "asistencias", label: "Asistencias", to: "/app/pase-lista?tab=asistencias" },
      { key: "tomar-clase", label: "Tomar clase", to: "/app/pase-lista?tab=tomar-clase" },
    ],
  },
}

function isSubmenuItemActive(item, location, defaults) {
  const [itemPath, itemQuery] = item.to.split("?")
  if (location.pathname !== itemPath) return false

  const expected = new URLSearchParams(itemQuery ?? "")
  const current = new URLSearchParams(location.search)
  for (const [param, value] of expected.entries()) {
    if ((current.get(param) ?? defaults?.[param]) !== value) return false
  }
  return true
}

export default function Sidebar() {
  const { loggedUser } = useAuth()
  const location = useLocation()
  const visibleEntities = ENTITY_ORDER.filter(
    (key) => !ENTITIES[key].hideFromNav && canAccessModule(loggedUser, key),
  )

  const navItems = []
  if (canAccessModule(loggedUser, "dashboard")) {
    navItems.push({
      key: "dashboard",
      label: "Dashboard",
      to: "/app/dashboard",
      preload: lazyHome,
    })
  }
  visibleEntities.forEach((key) => {
    navItems.push({
      key,
      label: ENTITIES[key].label,
      to: ENTITIES[key].path,
      preload: lazyCrud,
      group: SUBMENUS[key],
    })
  })
  if (canAccessModule(loggedUser, "pase-lista")) {
    navItems.push({
      key: "pase-lista",
      label: "Llamar lista",
      to: "/app/pase-lista",
      preload: lazyPaseLista,
      group: SUBMENUS["pase-lista"],
    })
  }
  if (canAccessModule(loggedUser, "datos")) {
    navItems.push({
      key: "datos",
      label: "Explorador de datos",
      to: "/app/datos",
      preload: lazyDataExplorer,
    })
  }

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <BrandLogo size={44} withName />
      </div>
      <nav aria-label="Navegación principal">
        {navItems.map((item) => (
          <div
            key={item.key}
            className={item.group ? "nav-item-group has-submenu" : "nav-item-group"}
          >
            <NavLink
              to={item.to}
              className={({ isActive }) => (isActive ? "nav-item active" : "nav-item")}
              onMouseEnter={item.preload}
              onFocus={item.preload}
            >
              {item.label}
            </NavLink>
            {item.group && (
              <div className="nav-submenu" role="menu" aria-label={`Opciones de ${item.label}`}>
                {item.group.items.map((subitem) => (
                  <Link
                    key={subitem.key}
                    to={subitem.to}
                    role="menuitem"
                    className={
                      isSubmenuItemActive(subitem, location, item.group.defaults)
                        ? "nav-submenu-link active"
                        : "nav-submenu-link"
                    }
                  >
                    {subitem.label}
                  </Link>
                ))}
              </div>
            )}
          </div>
        ))}
      </nav>
    </aside>
  )
}
