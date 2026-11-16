
package sv.innovacion.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import sv.innovacion.conexion.Conexion;
import sv.innovacion.model.Venta;
import sv.innovacion.model.Estado;
import sv.innovacion.util.MetodoPago;
import sv.innovacion.util.Transaccion;

public class VentaService {

    public boolean registrar(Venta venta) throws SQLException {

        String sql = """
            INSERT INTO ventas (fecha, total, estado, metodoPago)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(venta.getFecha()));
            ps.setDouble(2, venta.getTotal());
            ps.setString(3, venta.getEstado().name());

            if (venta.getMetodoPago() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, venta.getMetodoPago());
            }

            return ps.executeUpdate() > 0;
        }
    }

    public Venta buscarPorId(int id) throws SQLException {

        String sql = """
            SELECT id, fecha, total, estado, metodoPago
            FROM ventas
            WHERE id = ?
        """;

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                return new Venta(
                    rs.getInt("id"),
                    rs.getTimestamp("fecha").toLocalDateTime(),
                    rs.getDouble("total"),
                    Estado.valueOf(rs.getString("estado")),
                    rs.getString("metodoPago")
                );
            }
        }
    }

    public List<Venta> listarTodas() throws SQLException {

        List<Venta> ventas = new ArrayList<>();

        String sql = """
            SELECT id, fecha, total, estado, metodoPago
            FROM ventas
            ORDER BY fecha DESC
        """;

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Venta venta = new Venta(
                    rs.getInt("id"),
                    rs.getTimestamp("fecha").toLocalDateTime(),
                    rs.getDouble("total"),
                    Estado.valueOf(rs.getString("estado")),
                    rs.getString("metodoPago")
                );

                ventas.add(venta);
            }
        }

        return ventas;
    }

    public boolean actualizarEstado(int id, Estado estado)
            throws SQLException {

        String sql = "UPDATE ventas SET estado = ? WHERE id = ?";

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, estado.name());
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean marcarPagada(int id) throws SQLException {
        return actualizarEstado(id, Estado.PAGADA);
    }

    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM ventas WHERE id = ?";

        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Procesa un pago y registra el resultado en MySQL.
     *
     * Si el pago es aprobado:
     * - Registra la transacción en pagos.
     * - Actualiza el estado de la venta.
     * - Guarda el nombre del método utilizado.
     *
     * Si es rechazado:
     * - Registra el intento rechazado.
     * - Mantiene la venta pendiente.
     */
    public Transaccion confirmarPago(int id, MetodoPago metodoPago)
            throws SQLException {

        if (metodoPago == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar un método de pago."
            );
        }

        String consultaVenta = """
            SELECT total, estado
            FROM ventas
            WHERE id = ?
            FOR UPDATE
        """;

        String insertarPago = """
            INSERT INTO pagos (venta_id, monto, metodo, estado)
            VALUES (?, ?, ?, ?)
        """;

        String actualizarVenta = """
            UPDATE ventas
            SET estado = ?, metodoPago = ?
            WHERE id = ? AND estado = ?
        """;

        try (Connection cn = Conexion.obtenerConexion()) {

            boolean autoCommitOriginal = cn.getAutoCommit();

            try {
                cn.setAutoCommit(false);

                double total;
                Estado estadoActual;

                // 1. Consultar y bloquear la venta.
                try (PreparedStatement ps =
                        cn.prepareStatement(consultaVenta)) {

                    ps.setInt(1, id);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                "No existe la venta con ID " + id
                            );
                        }

                        total = rs.getDouble("total");

                        estadoActual = Estado.valueOf(
                            rs.getString("estado")
                        );
                    }
                }

                // 2. Verificar que la venta esté pendiente.
                if (estadoActual != Estado.PENDIENTE) {
                    throw new IllegalStateException(
                        "La venta ya no está pendiente de pago."
                    );
                }

                // 3. Procesar el método de pago.
                Transaccion transaccion = metodoPago.procesar(total);

                // 4. Registrar el intento en MySQL.
                try (PreparedStatement ps =
                        cn.prepareStatement(insertarPago)) {

                    ps.setInt(1, id);
                    ps.setDouble(2, transaccion.getMonto());
                    ps.setString(3, transaccion.getMetodo());
                    ps.setString(4, transaccion.getEstado().name());

                    ps.executeUpdate();
                }

                // 5. Actualizar la venta únicamente si fue aprobado.
                if (transaccion.getEstado() == Estado.PAGADA) {

                    try (PreparedStatement ps =
                            cn.prepareStatement(actualizarVenta)) {

                        ps.setString(1, Estado.PAGADA.name());
                        ps.setString(2, transaccion.getMetodo());
                        ps.setInt(3, id);
                        ps.setString(4, Estado.PENDIENTE.name());

                        int filas = ps.executeUpdate();

                        if (filas != 1) {
                            throw new SQLException(
                                "No fue posible actualizar el estado de la venta."
                            );
                        }
                    }
                }

                // 6. Confirmar todas las operaciones.
                cn.commit();

                return transaccion;

            } catch (SQLException | RuntimeException e) {

                cn.rollback();
                throw e;

            } finally {

                cn.setAutoCommit(autoCommitOriginal);
            }
        }
    }
}
