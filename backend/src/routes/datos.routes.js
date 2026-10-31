import { Router } from "express";
import { getTablas, getTablaDatos } from "../controllers/datos.controllers.js";
import { requireRole } from "../middleware/roleMiddleware.js";
import { ROLE_NAMES } from "../config/roles.js";

const router = Router();

// Solo SysAdmin puede explorar las tablas de la base de datos.
const sysAdminOnly = requireRole(ROLE_NAMES.SYS_ADMIN);

router.get("/datos/tablas", sysAdminOnly, getTablas);
router.get("/datos/tablas/:tabla", sysAdminOnly, getTablaDatos);

export default router;
