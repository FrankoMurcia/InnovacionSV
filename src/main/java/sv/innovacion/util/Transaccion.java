/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.innovacion.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import sv.innovacion.model.Estado;
/**
 *
 * @author Frank
 */
public class Transaccion {

    private int id;
    private int ventaId;
    private double monto;
    private String metodo;
    private Estado estado;
    private LocalDateTime fecha;

    // Constructor utilizado al procesar un pago nuevo.
    public Transaccion(double monto, String metodo, Estado estado) {
        this(0, 0, monto, metodo, estado, LocalDateTime.now());
    }

    // Constructor utilizado al recuperar una transacción de MySQL.
    public Transaccion(
            int id,
            int ventaId,
            double monto,
            String metodo,
            Estado estado,
            LocalDateTime fecha) {

        this.id = id;
        this.ventaId = ventaId;
        this.monto = monto;
        this.metodo = metodo;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public int getVentaId() {
        return ventaId;
    }

    public double getMonto() {
        return monto;
    }

    public String getMetodo() {
        return metodo;
    }

    public Estado getEstado() {
        return estado;
    }

    // Fecha como texto para mostrar en la interfaz.
    public String getFecha() {
        return fecha.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        );
    }

    // Fecha como objeto para realizar consultas y ordenar.
    public LocalDateTime getFechaDateTime() {
        return fecha;
    }

    @Override
    public String toString() {
        return "Transacción #" + id
                + " | Venta: " + ventaId
                + " | Monto: $" + String.format("%.2f", monto)
                + " | Método: " + metodo
                + " | Estado: " + estado
                + " | Fecha: " + getFecha();
    }
}
