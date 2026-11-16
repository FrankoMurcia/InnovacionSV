/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.innovacion.service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import sv.innovacion.conexion.Conexion;
import sv.innovacion.model.Estado;
import sv.innovacion.util.Transaccion;
/**
 *
 * @author Frank
 */
public class TransaccionService {

    // Convierte un registro de pagos de MySQL en un objeto Transaccion.
    private Transaccion mapearTransaccion(ResultSet rs)
            throws SQLException {

        return new Transaccion(
            rs.getInt("id"),
            rs.getInt("venta_id"),
            rs.getDouble("monto"),
            rs.getString("metodo"),
            Estado.valueOf(rs.getString("estado")),
            rs.getTimestamp("fecha").toLocalDateTime()
        );
    }

    // 4.1. Buscar transacción por identificador.
    public Transaccion buscarPorId(int id) throws SQLException {

        String sql = """
            SELECT id, venta_id, monto, metodo, estado, fecha
            FROM pagos
            WHERE id = ?
        """;

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearTransaccion(rs);
                }
            }
        }

        return null;
    }

    // 4.2. Consultar transacciones por método.
    public List<Transaccion> buscarPorMetodo(String metodo)
            throws SQLException {

        List<Transaccion> lista = new ArrayList<>();

        String sql = """
            SELECT id, venta_id, monto, metodo, estado, fecha
            FROM pagos
            WHERE metodo = ?
            ORDER BY fecha DESC
        """;

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, metodo);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapearTransaccion(rs));
                }
            }
        }

        return lista;
    }

    // 4.3. Consultar transacciones por estado.
    public List<Transaccion> buscarPorEstado(Estado estado)
            throws SQLException {

        List<Transaccion> lista = new ArrayList<>();

        if (estado == Estado.PENDIENTE) {

            // Una venta pendiente puede no tener todavía un pago registrado.
            String sql = """
                SELECT
                    0 AS id,
                    v.id AS venta_id,
                    v.total AS monto,
                    COALESCE(v.metodoPago, 'SIN PAGO') AS metodo,
                    v.estado AS estado,
                    v.fecha AS fecha
                FROM ventas v
                WHERE v.estado = 'PENDIENTE'
                ORDER BY v.fecha DESC
            """;

            try (Connection cn = Conexion.obtenerConexion();
                 PreparedStatement ps = cn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapearTransaccion(rs));
                }
            }

            return lista;
        }

        String sql = """
            SELECT id, venta_id, monto, metodo, estado, fecha
            FROM pagos
            WHERE estado = ?
            ORDER BY fecha DESC
        """;

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, estado.name());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapearTransaccion(rs));
                }
            }
        }

        return lista;
    }

    // 4.4. Consultar transacciones entre dos fechas.
    public List<Transaccion> buscarPorFecha(
            LocalDate fechaInicio,
            LocalDate fechaFin) throws SQLException {

        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar ambas fechas."
            );
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException(
                "La fecha inicial no puede ser posterior a la fecha final."
            );
        }

        List<Transaccion> lista = new ArrayList<>();

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.plusDays(1).atStartOfDay();

        String sql = """
            SELECT id, venta_id, monto, metodo, estado, fecha
            FROM pagos
            WHERE fecha >= ? AND fecha < ?
            ORDER BY fecha DESC
        """;

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapearTransaccion(rs));
                }
            }
        }

        return lista;
    }

    // 4.5. Consultar transacciones asociadas a una venta.
    public List<Transaccion> buscarPorVenta(int ventaId)
            throws SQLException {

        List<Transaccion> lista = new ArrayList<>();

        String sql = """
            SELECT id, venta_id, monto, metodo, estado, fecha
            FROM pagos
            WHERE venta_id = ?
            ORDER BY fecha DESC
        """;

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, ventaId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapearTransaccion(rs));
                }
            }
        }

        return lista;
    }

    // Consultar todas las transacciones registradas.    
    public List<Transaccion> listarTodas() throws SQLException {
        List<Transaccion> transacciones = new ArrayList<>();

        String sql = """
            SELECT id, venta_id, monto, metodo, estado, fecha
            FROM pagos
            ORDER BY fecha DESC
        """;

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                transacciones.add(mapearTransaccion(rs));
            }
        }

        return transacciones;
    }
}

