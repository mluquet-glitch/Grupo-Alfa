import { NavLink } from "react-router-dom"
import BrandLogo from "../ui/BrandLogo"
import { ENTITIES, ENTITY_ORDER } from "../../config/entities"
import { canAccessModule } from "../../config/permissions"
import { useAuth } from "../../hooks/useAuth"

const lazyCrud = () => import("./CrudPage.jsx")
const lazyHome = () => import("./DashboardHome.jsx")
const lazyPaseLista = () => import("../../pages/app/PaseListaPage.jsx")
const lazyDataExplorer = () => import("../../pages/app/DataExplorerPage.jsx")

export default function Sidebar() {
  const { loggedUser } = useAuth()
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
    })
  })
  if (canAccessModule(loggedUser, "pase-lista")) {
    navItems.push({
      key: "pase-lista",
      label: "Llamar lista",
      to: "/app/pase-lista",
      preload: lazyPaseLista,
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
          <div key={item.key} className="nav-item-group">
            <NavLink
              to={item.to}
              className={({ isActive }) => (isActive ? "nav-item active" : "nav-item")}
              onMouseEnter={item.preload}
              onFocus={item.preload}
            >
              {item.label}
            </NavLink>
          </div>
        ))}
      </nav>
    </aside>
  )
}
