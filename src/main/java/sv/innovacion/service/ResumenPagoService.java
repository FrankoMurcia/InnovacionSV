/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.innovacion.service;
import sv.innovacion.conexion.Conexion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 *
 * @author Frank
 */
public class ResumenPagoService {

    public List<ResumenMetodo> obtenerResumen() throws SQLException {

        Map<String, ResumenMetodo> resumen = new LinkedHashMap<>();

        resumen.put("Efectivo", new ResumenMetodo("Efectivo"));
        resumen.put("Tarjeta", new ResumenMetodo("Tarjeta"));
        resumen.put("Transferencia", new ResumenMetodo("Transferencia"));
        resumen.put("Pago electrónico", new ResumenMetodo("Pago electrónico"));

        String sql = """
            SELECT metodo, estado, COUNT(*) AS cantidad,
                   COALESCE(SUM(monto), 0) AS importe
            FROM pagos
            GROUP BY metodo, estado
        """;

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String metodo = clasificarMetodo(rs.getString("metodo"));
                String estado = rs.getString("estado");

                long cantidad = rs.getLong("cantidad");
                BigDecimal importe = rs.getBigDecimal("importe");

                ResumenMetodo fila = resumen.get(metodo);

                // Ignorar métodos que no pertenecen a las cuatro categorías.
                if (fila == null) {
                    continue;
                }

                fila.agregarOperaciones(cantidad);

                if (estado != null) {
                    switch (estado.trim().toUpperCase(Locale.ROOT)) {
                        case "PAGADA" -> {
                            fila.agregarAprobadas(cantidad);
                            fila.agregarImporteAprobado(importe);
                        }
                        case "RECHAZADA" ->
                            fila.agregarRechazadas(cantidad);
                        case "PENDIENTE" ->
                            fila.agregarPendientes(cantidad);
                    }
                }
            }
        }

        return new ArrayList<>(resumen.values());
    }

    private String clasificarMetodo(String metodo) {

        if (metodo == null || metodo.trim().isEmpty()) {
            return "";
        }

        String nombre = metodo.trim().toLowerCase(Locale.ROOT)
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");

        if (nombre.contains("efectivo")) {
            return "Efectivo";
        }

        if (nombre.contains("tarjeta")) {
            return "Tarjeta";
        }

        if (nombre.contains("transferencia")) {
            return "Transferencia";
        }

        if (nombre.contains("electron")) {
            return "Pago electrónico";
        }

        return "";
    }

    public static class ResumenMetodo {

        private final String metodo;
        private long totalOperaciones;
        private long aprobadas;
        private long rechazadas;
        private long pendientes;
        private BigDecimal importeAprobado = BigDecimal.ZERO;

        public ResumenMetodo(String metodo) {
            this.metodo = metodo;
        }

        public void agregarOperaciones(long cantidad) {
            totalOperaciones += cantidad;
        }

        public void agregarAprobadas(long cantidad) {
            aprobadas += cantidad;
        }

        public void agregarRechazadas(long cantidad) {
            rechazadas += cantidad;
        }

        public void agregarPendientes(long cantidad) {
            pendientes += cantidad;
        }

        public void agregarImporteAprobado(BigDecimal importe) {
            if (importe != null) {
                importeAprobado = importeAprobado.add(importe);
            }
        }

        public String getMetodo() {
            return metodo;
        }

        public long getTotalOperaciones() {
            return totalOperaciones;
        }

        public long getAprobadas() {
            return aprobadas;
        }

        public long getRechazadas() {
            return rechazadas;
        }

        public long getPendientes() {
            return pendientes;
        }

        public BigDecimal getImporteAprobado() {
            return importeAprobado;
        }
    }
}
