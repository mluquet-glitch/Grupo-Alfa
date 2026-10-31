# CRUDs Principales — Dashboard lateral + 5 módulos académicos

> Stack: React 18 + Vite 4 + axios (web actual, no Expo). Skills aplicadas: `react-vite-best-practices` (23 reglas) y patrones `react-native-architecture` adaptados a web.
> Alcance: dashboard lateral como primera pantalla + CRUD completo (listar, crear, editar, eliminar) de `materias, asesores, capacitaciones, temas, estudiantes`. `preparacion` no existe (era `cursos_capacitacion`) y se omite.
> Backend: `https://grupoalfa-production.up.railway.app` — contrato `GET/POST/PUT/DELETE /api/<recurso>` con `authMiddleware` (Bearer o cookie `jwt`). Detalle de tablas/validadores en `C:\CODIGO\GrupoAlfa\docs\plan-endpoints-academicos.md` §§4–6.

## 1. Estado actual verificado

| Pieza | Archivo | Hallazgo |
|---|---|---|
| Entrada | `src/main.jsx`, `src/App.jsx` → `src/app/App.jsx` | Solo switch login/welcome, sin router ni dashboard |
| Login | `src/components/features/auth/LoginScreen.jsx` | Valida en cliente contra `GET /usuarios` (`correo_electronico` + `contraseña`, exige `usuario_vigente`) |
| Datos | `src/services/usersService.js` | URL hardcodeada `.../usuarios`, sin `apiClient` ni JWT |
| Estado auth | `src/stores/authStore.js` | Solo memoria (`loggedUser`), sin token ni persistencia |
| Hook | `src/hooks/useUsers.js` | `useEffect` + `isMounted`, patrón a reutilizar |
| Estilos | `src/App.css` | Paleta `#14241f/#f5f1e8/#b66b1e`, fuentes vía `@import` Google Fonts (viola `asset-fonts`) |
| Config | `vite.config.js`, `package.json` | Sin `manualChunks`, sin `optimizeDeps`, sin alias `@`, sin `.env` |

## 2. Arquitectura objetivo

```
src/
├── app/App.jsx                  # switch auth → DashboardShell; Suspense estratégicos
├── config/entities.js           # definición de las 5 entidades (endpoint, pk, columnas, campos, filtros)
├── services/
│   ├── apiClient.js             # axios baseURL=VITE_API_URL + interceptor JWT + dispose HMR
│   ├── usersService.js          # migrar a apiClient (sin URL hardcodeada)
│   └── academicService.js       # list/get/create/update/remove(recurso) genérico
├── hooks/
│   ├── useUsers.js              # existente
│   └── useCrud.js               # hook genérico por entidad
├── stores/authStore.js          # {loggedUser, token, signIn(user,token), signOut()} + localStorage
├── vite-env.d.ts                # tipos ImportMetaEnv (VITE_API_URL, VITE_APP_NAME)
├── assets/fonts/*.woff2         # DM Sans + Space Grotesk auto-hospedadas
└── components/
    ├── ui/{BrandMark,ErrorFallback,Skeleton}.jsx
    └── dashboard/
        ├── DashboardShell.jsx   # layout lateral + <main> (primera pantalla, eager)
        ├── Sidebar.jsx          # nav: Dashboard + 5 entidades, prefetch on hover/focus
        ├── DashboardHome.jsx    # lazy: 5 tarjetas-contador + accesos directos
        ├── CrudPage.jsx         # lazy: toolbar buscar + tabla + modal
        ├── DataTable.jsx        # tabla memoizada + editar/eliminar
        └── EntityFormModal.jsx  # lazy: formulario dinámico por schema
```

## 3. Reglas `react-vite-best-practices` aplicadas

### 3.1 Env (`env-vite-prefix`, `env-modes`, `env-sensitive-data`)
- `.env`: `VITE_APP_NAME=GrupoAlfa`
- `.env.development`: `VITE_API_URL=https://grupoalfa-production.up.railway.app/api`
- `.env.production`: `VITE_API_URL=https://grupoalfa-production.up.railway.app/api`
- Solo `VITE_API_URL` / `VITE_APP_NAME` con prefijo. Prohibido `VITE_*SECRET/PASSWORD/PRIVATE/TOKEN`: el JWT vive en `localStorage` (`ga_token`), nunca en env ni en el bundle.
- `src/config.js`: `export const config = { apiUrl: import.meta.env.VITE_API_URL, appName: import.meta.env.VITE_APP_NAME, mode: import.meta.env.MODE }`.

### 3.2 Build (`build-manual-chunks`, `build-target-modern`, `build-sourcemaps`, `build-tree-shaking`)
- `vite.config.js`: `target: 'baseline-widely-available'`; `sourcemap: mode==='production' ? 'hidden' : true`; `manualChunks: { 'vendor-react': ['react','react-dom'], 'vendor-http': ['axios'] }`; `optimizeDeps.include: ['react','react-dom','axios']`; `server.hmr.overlay: true`; alias `@ → ./src`. Minificador OXC por defecto (no Terser).
- Tree-shaking: solo named imports, sin barrel `export *`; `sideEffects: ["*.css"]` si se edita `package.json`.
- Hashing de Vite por defecto (`assets/[name]-[hash]`); no usar `public/` para assets procesados.

### 3.3 Code splitting (`split-route-lazy`, `split-suspense-boundaries`, `split-component-lazy`, `split-prefetch-hints`)
- Eager: `DashboardShell`, `Sidebar`. Lazy: `DashboardHome`, `CrudPage`, `EntityFormModal`.
- Dos límites `Suspense`: uno para contenido principal (skeleton tabla), otro anidado para el modal + `ErrorBoundary` por sección — el sidebar nunca se bloquea por un chunk pendiente.
- Prefetch en `Sidebar`: `onMouseEnter/onFocus → import('../components/dashboard/CrudPage.jsx')`.
- `DashboardHome` dispara los 5 `list()` en paralelo; cada `CrudPage` carga su recurso solo al activarse.

### 3.4 Dev (`dev-dependency-prebundling`, `dev-fast-refresh`, `dev-hmr-config`)
- Un componente por archivo, `export default function`, sin side-effects a nivel módulo ni mezclar constantes en archivos de componentes.
- `apiClient` limpia interceptores en `import.meta.hot.dispose()`.

### 3.5 Assets (`asset-fonts`, `asset-public-dir`)
- Eliminar `@import` Google Fonts de `App.css`. Auto-hospedar `DM Sans` + `Space Grotesk` en `src/assets/fonts/*.woff2` con `@font-face { font-display: swap; unicode-range: latin }` y `preload` de la regular en `index.html`. Salida `assets/fonts/[name]-[hash]`.
- `public/` solo para `favicon.ico`, `robots.txt`, `manifest.json`, `_redirects`, `og-image.png`. Logo `GA` sigue como componente.

### 3.6 Bundle (`bundle-visualizer`)
- `rollup-plugin-visualizer` solo con `ANALYZE=true` + script `analyze`. No en builds normales.

## 4. Capa de datos

### `services/apiClient.js`
`axios.create({ baseURL: import.meta.env.VITE_API_URL, timeout: 10000 })`; request interceptor inyecta `Authorization: Bearer <ga_token>`; response interceptor mapea 401 → `signOut`, 409 → mensaje FK/duplicado (`23505/23503`), 400 → validación.

### `services/academicService.js` (genérico, parametrizado por `entities.js`)
`list(r) → GET /<endpoint>`, `getById(r,id) → GET /<endpoint>/:id` (400 si id no numérico), `create(r,payload) → POST 201`, `update(r,id,payload) → PUT 200`, `remove(r,id) → DELETE 204`.

### `config/entities.js`
| Entidad | Endpoint | PK | Columnas | Campos form | Notas |
|---|---|---|---|---|---|
| materias | `/materias` | `id_materia` | nombre_materia | `nombre_materia` string 1–150 req | — |
| asesores | `/asesores` | `id_asesor` | asesor + usuario (nombre, apellidos, correo) | `id_usuario` int positivo req, existe en usuarios | JOIN a `usuarios`; fix SQL injection previo |
| capacitaciones | `/capacitaciones` | `id_capacitacion` | nombre, nivel, modalidad, fecha_inicio/fin, hora_inicio/fin, convocatoria, aula | `id_convocatoria*`, `id_aula*`, `nombre*` 1–150, `nivel`, `modalidad`, `fecha_inicio`, `fecha_fin` + refine fin≥inicio, `hora_inicio/hora_fin HH:MM` + refine fin>inicio | Tabla real `cursos_capacitacion`; FK RESTRICT exige semilla válida |
| temas | `/temas` | `id_tema` | nombre_tema, materia | `nombre_tema*` 1–150, `id_materia?` nulleable | Filtro `GET /temas?id_materia=X`; JOIN `materias(nombre_materia)`; confirmar nombre exacto de columna (`\d temas`) |
| estudiantes | `/estudiantes` | `id_estudiante` | usuario, apoderado, colegio, dirección | `id_usuario*` (existe), `id_apoderado?` (existe), `colegio_de_procedencia?` ≤150, `direccion_de_domicilio?` ≤255 | `SELECT`/`INSERT`/`UPDATE` sin `foto`; posible 409 si usuario ya es estudiante |

### `hooks/useCrud.js`
`{ items, loading, error, selected, setSelected, reload, save(payload), remove(id) }`. `save` decide POST/PUT por presencia de PK; `remove` tras confirm; `reload` re-fetch. Búsqueda client-side en `CrudPage`.

## 5. UI — primera pantalla y CRUDs

1. **`DashboardShell`**: tras login; sidebar fijo (colapsable en móvil) + `<main>`; header con usuario + `Cerrar sesión`.
2. **`Sidebar`**: 6 entradas (Dashboard, Materias, Asesores, Capacitaciones, Temas, Estudiantes); estado `activeKey` (sin añadir router); prefetch de `CrudPage` en hover/focus.
3. **`DashboardHome`** (vista inicial): 5 tarjetas-contador (un `list` por recurso), estados carga/error por tarjeta, accesos directos que cambian `activeKey`.
4. **`CrudPage`**: título + contador, toolbar buscar (+ filtro `id_materia` en temas), botón Nuevo, `DataTable`, `EntityFormModal` para crear/editar, confirm nativo para eliminar, mensajes de error 400/404/409.
5. **`DataTable`**: `React.memo` por fila, `useCallback` en acciones, columnas desde `entities.js`, paginación simple client-side si >25 filas.
6. **`EntityFormModal`**: inputs generados desde `fields` (text/number/date/time/select), validación requerida/longitud espejo de zod, foco inicial y cierre con Escape.
7. **Estilos**: extender `App.css` (paleta y radios existentes); skeletons con `animate-pulse`; sin frameworks CSS nuevos.

## 6. Auth

- `stores/authStore.js` → `{ loggedUser, token, signIn(user,token), signOut() }` con `localStorage ga_token/ga_user`; `App.jsx` restaura sesión al montar.
- Transitorio: mantener validación actual contra `GET /usuarios` hasta confirmar endpoint de login que emite JWT; luego `POST /api/auth/login → {token, user}`.

## 7. Orden de implementación

1. Env (`.env*`, `vite-env.d.ts`) + `vite.config.js` + `apiClient` + `entities.js` + `useCrud`.
2. `DashboardShell`/`Sidebar`/`DashboardHome` + wiring `App.jsx` + CSS dashboard + fuentes auto-hospedadas.
3. `CrudPage`/`DataTable`/`EntityFormModal` + 5 instancias por `activeKey`.
4. Migración `usersService` a `apiClient`; persistencia de sesión.

## 8. Verificación

- `npm run dev`: login, navegar las 6 vistas, crear/editar/eliminar por entidad con JWT válido; comprobar mensajes 400/404/409 y 204 sin body en delete.
- `npm run build` + `npm run analyze` (`ANALYZE=true`): `vendor-react` separado, sin duplicados, chunks por vista lazy.
- HMR: editar un componente preserva estado de formulario (Fast Refresh).
- Casos límite: `temas` sin materia (SET NULL), `asesores`/`capacitaciones` con RESTRICT (409 al borrar referenciado), `estudiantes` duplicado (409).

## 9. Bloqueantes / supuestos

- Backend con los 5 routers montados en `/api` (ver §8 del doc de endpoints); si no, usar mocks locales con la misma forma y conectar después.
- Confirmar columna de nombre en `temas` (`nombre_tema` vs `nombre`/`titulo`) con `\d temas`.
- Confirmar endpoint de login que devuelve el JWT y semilla válida de `convocatorias_preuniversitario` + `aulas` para crear capacitaciones.
