
package sv.innovacion.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sv.innovacion.model.Estado;
import sv.innovacion.model.Venta;
import sv.innovacion.service.VentaService;

public class ProcesarPagos {

    private final List<Transaccion> transacciones;
    private final VentaService ventaService;

    public ProcesarPagos(VentaService ventaService) {
        this.ventaService = ventaService;
        this.transacciones = new ArrayList<>();
    }

    public Transaccion procesarPago(
            Venta venta,
            MetodoPago metodo) throws SQLException {

        if (venta == null || metodo == null) {
            throw new IllegalArgumentException(
                "La venta y el método de pago son obligatorios."
            );
        }

        if (venta.getEstado() != Estado.PENDIENTE) {
            throw new IllegalStateException(
                "La venta no está pendiente de pago."
            );
        }

        // El servicio procesa el pago y lo persiste en MySQL.
        Transaccion transaccion = ventaService.confirmarPago(
            venta.getId(),
            metodo
        );

        // Guardar el resultado en el historial local.
        transacciones.add(transaccion);

        // Actualizar el objeto Venta en memoria.
        if (transaccion.getEstado() == Estado.PAGADA) {
            venta.marcarPagada(metodo.getNombre());
        }

        return transaccion;
    }

    public List<Transaccion> getTransacciones() {
        return new ArrayList<>(transacciones);
    }

    public void consultarTransacciones() {

        System.out.println("\n=== Historial de Transacciones ===");

        if (transacciones.isEmpty()) {
            System.out.println("No existen transacciones.");
            return;
        }

        for (Transaccion transaccion : transacciones) {
            System.out.println(transaccion);
        }
    }
}
