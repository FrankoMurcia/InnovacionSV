package sv.innovacion.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import sv.innovacion.util.MetodoPago;

/**
 *
 * @author Frank
 */
public class Venta {

    private int id;
    private LocalDateTime fecha;
    private double total;
    private Estado estado;

    // Se guarda el nombre del método de pago, no el objeto procesador.
    // Será null mientras la venta esté pendiente.
    private String metodoPago;

    // Constructor para ventas nuevas o consultas sin método de pago.
    public Venta(int id, LocalDateTime fecha, double total, Estado estado) {
        this(id, fecha, total, estado, (String) null);
    }

    // Constructor completo.
    public Venta(int id, LocalDateTime fecha, double total, Estado estado, String metodoPago) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
        this.metodoPago = metodoPago;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public double getTotal() {
        return total;
    }

    public Estado getEstado() {
        return estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public void marcarPagada(String metodoPago) {
        this.estado = Estado.PAGADA;
        this.metodoPago = metodoPago;
    }

    public void registrar() {
        System.out.println("Venta #" + id + " registrada.");
    }

    public void mostrar() {
        System.out.println(
            "Venta #" + id
            + " | Fecha: "
            + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            + " | Total: $" + String.format("%.2f", total)
            + " | Estado: " + estado
            + " | Método de pago: "
            + (metodoPago == null ? "Sin registrar" : metodoPago)
        );
    }
}

