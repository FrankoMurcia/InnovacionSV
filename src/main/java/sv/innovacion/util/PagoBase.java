/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.innovacion.util;

import sv.innovacion.model.Estado;

/**
 *
 * @author Frank
 */
public abstract class PagoBase implements MetodoPago {

    protected boolean validarMonto(double monto) {
        return Double.isFinite(monto) && monto > 0;
    }

    protected Transaccion registrarTransaccion(
            double monto,
            Estado estado) {

        return new Transaccion(monto, getNombre(), estado);
    }

    public abstract boolean ejecutarCobro(double monto);

    @Override
    public Transaccion procesar(double monto) {

        if (!validarMonto(monto)) {
            System.out.println("Error: el monto debe ser mayor que cero.");

            return registrarTransaccion(
                monto,
                Estado.RECHAZADA
            );
        }

        boolean exitoso = ejecutarCobro(monto);

        return registrarTransaccion(
            monto,
            exitoso ? Estado.PAGADA : Estado.RECHAZADA
        );
    }
}

