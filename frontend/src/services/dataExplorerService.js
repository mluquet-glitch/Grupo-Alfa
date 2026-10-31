import { apiClient } from "./apiClient"

export async function fetchDatabaseTables() {
  const response = await apiClient.get("/datos/tablas")
  return Array.isArray(response.data?.tablas) ? response.data.tablas : []
}

export async function fetchTableRows(tabla, page = 1, limit = 20) {
  const response = await apiClient.get(`/datos/tablas/${encodeURIComponent(tabla)}`, {
    params: { page, limit },
  })
  return response.data
}
