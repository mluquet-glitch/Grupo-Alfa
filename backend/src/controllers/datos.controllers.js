import { pool } from "../db.js";

const DEFAULT_LIMIT = 20;
const MAX_LIMIT = 100;

const quoteIdentifier = (name) => `"${String(name).replace(/"/g, '""')}"`;

const parsePositiveInt = (value, fallback) => {
  const parsed = Number.parseInt(value, 10);
  return Number.isFinite(parsed) && parsed > 0 ? parsed : fallback;
};

export const getTablas = async (req, res, next) => {
  try {
    const { rows } = await pool.query(
      `SELECT table_name
       FROM information_schema.tables
       WHERE table_schema = 'public' AND table_type = 'BASE TABLE'
       ORDER BY table_name ASC`,
    );
    res.status(200).json({ tablas: rows.map((row) => row.table_name) });
  } catch (error) {
    next(error);
  }
};

export const getTablaDatos = async (req, res, next) => {
  try {
    const tabla = String(req.params.tabla ?? "");

    const existe = await pool.query(
      `SELECT 1
       FROM information_schema.tables
       WHERE table_schema = 'public' AND table_type = 'BASE TABLE' AND table_name = $1`,
      [tabla],
    );
    if (existe.rows.length === 0) {
      return res.status(404).json({ message: "Tabla no encontrada" });
    }

    const { rows: columnRows } = await pool.query(
      `SELECT column_name
       FROM information_schema.columns
       WHERE table_schema = 'public' AND table_name = $1
       ORDER BY ordinal_position ASC`,
      [tabla],
    );
    const columnas = columnRows.map((row) => row.column_name);

    const { rows: pkRows } = await pool.query(
      `SELECT kcu.column_name
       FROM information_schema.table_constraints tc
       JOIN information_schema.key_column_usage kcu
         ON kcu.constraint_name = tc.constraint_name
         AND kcu.constraint_schema = tc.constraint_schema
         AND kcu.table_schema = tc.table_schema
         AND kcu.table_name = tc.table_name
       WHERE tc.table_schema = 'public'
         AND tc.table_name = $1
         AND tc.constraint_type = 'PRIMARY KEY'
       ORDER BY kcu.ordinal_position ASC
       LIMIT 1`,
      [tabla],
    );
    const orderColumn = pkRows[0]?.column_name ?? columnas[0];

    const limit = Math.min(parsePositiveInt(req.query.limit, DEFAULT_LIMIT), MAX_LIMIT);
    const page = parsePositiveInt(req.query.page, 1);
    const offset = (page - 1) * limit;

    const countResult = await pool.query(
      `SELECT COUNT(*)::int AS total FROM public.${quoteIdentifier(tabla)}`,
    );
    const total = countResult.rows[0]?.total ?? 0;

    const orderBy = orderColumn ? ` ORDER BY ${quoteIdentifier(orderColumn)} ASC` : "";
    const { rows } = await pool.query(
      `SELECT * FROM public.${quoteIdentifier(tabla)}${orderBy} LIMIT $1 OFFSET $2`,
      [limit, offset],
    );

    res.status(200).json({
      tabla,
      columnas,
      filas: rows,
      total,
      pagina: page,
      limite: limit,
      totalPaginas: Math.max(1, Math.ceil(total / limit)),
    });
  } catch (error) {
    next(error);
  }
};
