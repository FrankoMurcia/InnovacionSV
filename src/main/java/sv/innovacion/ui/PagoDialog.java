/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.innovacion.ui;

import javax.swing.JComboBox;
import sv.innovacion.util.MetodoPago;
import javax.swing.*;
import java.awt.*;

import sv.innovacion.util.MetodoPago;
import sv.innovacion.model.Efectivo;
import sv.innovacion.model.Tarjeta;
import sv.innovacion.model.Transferencia;
import sv.innovacion.model.PagoElectronico;
import java.awt.Dimension;

/**
 *
 * @author Frank
 */
public class PagoDialog extends JDialog {

    private final double totalVenta;
    private JLabel etiquetaCambio;

    private JComboBox<String> comboMetodoPago;

    private JTextField campoMontoRecibido;
    private JTextField campoNumeroTarjeta;
    private JComboBox<String> comboTipoTarjeta;
    private JTextField campoReferencia;
    private JTextField campoCuenta;
    private JTextField campoProveedor;

    private final JPanel panelFormularios;
    private final CardLayout cardLayout;

    private boolean confirmado = false;

    private MetodoPago metodoPago;

    public PagoDialog(Frame owner, double totalVenta) {

        super(owner, "Procesamiento de pago", true);

        this.totalVenta = totalVenta;

        configurarMetodosPago();

        cardLayout = new CardLayout();
        panelFormularios = new JPanel(cardLayout);

        construirVentana();

        setSize(520, 490);
        setMinimumSize(new Dimension(480, 450));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void configurarMetodosPago() {

        comboMetodoPago = new JComboBox<>(new String[] {
            "Efectivo",
            "Tarjeta",
            "Transferencia",
            "Pago electrónico"
        });

        campoMontoRecibido = new JTextField(18);
        campoNumeroTarjeta = new JTextField(18);
        campoReferencia = new JTextField(18);
        campoCuenta = new JTextField(18);
        campoProveedor = new JTextField(18);

        comboTipoTarjeta = new JComboBox<>(
            new String[] {
                "Visa",
                "Mastercard",
                "Otra"
            }
        );
    }

    private void construirVentana() {

        JPanel principal = new JPanel(new BorderLayout(15, 20));

        principal.setBackground(new Color(245, 247, 250));

        principal.setBorder(
            BorderFactory.createEmptyBorder(25, 28, 25, 28)
        );

        // ENCABEZADO

        JPanel encabezado = new JPanel();

        encabezado.setOpaque(false);
        encabezado.setLayout(
            new BoxLayout(encabezado, BoxLayout.Y_AXIS)
        );

        JLabel titulo = new JLabel("Registrar pago");

        titulo.setFont(new Font("Segoe UI", Font.BOLD, 23));
        titulo.setForeground(new Color(30, 41, 59));

        JLabel descripcion = new JLabel(
            "Selecciona el método de pago e ingresa los datos solicitados."
        );

        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descripcion.setForeground(new Color(107, 114, 128));

        JLabel total = new JLabel(
            String.format("Total de venta: $%.2f", totalVenta)
        );

        total.setFont(new Font("Segoe UI", Font.BOLD, 15));
        total.setForeground(new Color(37, 99, 235));

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(7));
        encabezado.add(descripcion);
        encabezado.add(Box.createVerticalStrut(15));
        encabezado.add(total);

        principal.add(encabezado, BorderLayout.NORTH);

        // SELECTOR DE MÉTODO

        JPanel selector = new JPanel(new BorderLayout(0, 8));
        selector.setOpaque(false);

        JLabel etiquetaMetodo = new JLabel("Método de pago");

        etiquetaMetodo.setFont(
            new Font("Segoe UI", Font.BOLD, 13)
        );

        comboMetodoPago.setFont(
            new Font("Segoe UI", Font.PLAIN, 13)
        );

        selector.add(etiquetaMetodo, BorderLayout.NORTH);
        selector.add(comboMetodoPago, BorderLayout.CENTER);

        // FORMULARIOS

        panelFormularios.setOpaque(false);

        panelFormularios.add(
            crearFormularioEfectivo(),
            "Efectivo"
        );

        panelFormularios.add(
            crearFormularioTarjeta(),
            "Tarjeta"
        );

        panelFormularios.add(
            crearFormularioTransferencia(),
            "Transferencia"
        );

        panelFormularios.add(
            crearFormularioElectronico(),
            "Pago electrónico"
        );

        JPanel centro = new JPanel(new BorderLayout(0, 15));
        centro.setOpaque(false);

        centro.add(selector, BorderLayout.NORTH);
        centro.add(panelFormularios, BorderLayout.CENTER);

        principal.add(centro, BorderLayout.CENTER);

        // BOTONES

        JPanel botones = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );

        botones.setOpaque(false);

        JButton cancelar = new JButton("CANCELAR");

        JButton confirmar = new JButton("CONFIRMAR PAGO");

        confirmar.setBackground(new Color(37, 99, 235));
        confirmar.setForeground(Color.WHITE);
        confirmar.setFont(
            new Font("Segoe UI", Font.BOLD, 12)
        );

        confirmar.setFocusPainted(false);

        cancelar.addActionListener(e -> dispose());

        confirmar.addActionListener(e -> confirmarPago());

        botones.add(cancelar);
        botones.add(confirmar);

        principal.add(botones, BorderLayout.SOUTH);

        setContentPane(principal);

        // CAMBIAR FORMULARIO SEGÚN MÉTODO

        comboMetodoPago.addActionListener(e -> {

            String metodo = comboMetodoPago.getSelectedItem().toString();

            cardLayout.show(panelFormularios, metodo);
        });

        cardLayout.show(panelFormularios, "Efectivo");
    }

    // FORMULARIO DE EFECTIVO

    private JPanel crearFormularioEfectivo() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblMonto = new JLabel("Monto recibido:");

        // AQUÍ SE CONSTRUYE EL CAMPO
        campoMontoRecibido = new JTextField(15);

        campoMontoRecibido.setPreferredSize(
            new Dimension(200, 30)
        );

        campoMontoRecibido.setMinimumSize(
            new Dimension(200, 30)
        );

        campoMontoRecibido.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 30)
        );

        campoMontoRecibido.setFont(
            new Font("Segoe UI", Font.PLAIN, 13)
        );

        campoMontoRecibido.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Etiqueta para mostrar el cambio
        etiquetaCambio = new JLabel("Cambio: $0.00");

        etiquetaCambio.setFont(
            new Font("Arial", Font.BOLD, 15)
        );

        etiquetaCambio.setForeground(Color.GRAY);
        
        etiquetaCambio.setAlignmentX(Component.LEFT_ALIGNMENT);

        etiquetaCambio.setBorder(
            BorderFactory.createEmptyBorder(8, 0, 0, 0)
        );

        // Detectar cambios mientras se escribe
        campoMontoRecibido.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {

                @Override
                public void insertUpdate(
                        javax.swing.event.DocumentEvent e) {
                    actualizarCambio();
                }

                @Override
                public void removeUpdate(
                        javax.swing.event.DocumentEvent e) {
                    actualizarCambio();
                }

                @Override
                public void changedUpdate(
                        javax.swing.event.DocumentEvent e) {
                    actualizarCambio();
                }
            }
        );

        panel.add(lblMonto);
        panel.add(campoMontoRecibido);

        // Debajo del campo aparecerá el cambio.
        panel.add(etiquetaCambio);

        return panel;
    }

    // FORMULARIO DE TARJETA

    private JPanel crearFormularioTarjeta() {

        JPanel panel = crearPanelFormulario();

        agregarCampo(
            panel,
            "Número de tarjeta",
            campoNumeroTarjeta
        );

        agregarCampo(
            panel,
            "Tipo de tarjeta",
            comboTipoTarjeta
        );

        return panel;
    }

    // FORMULARIO DE TRANSFERENCIA

    private JPanel crearFormularioTransferencia() {

        JPanel panel = crearPanelFormulario();

        agregarCampo(
            panel,
            "Número de referencia",
            campoReferencia
        );

        return panel;
    }

    // FORMULARIO DE PAGO ELECTRÓNICO

    private JPanel crearFormularioElectronico() {

        JPanel panel = crearPanelFormulario();

        agregarCampo(
            panel,
            "Cuenta o identificador",
            campoCuenta
        );

        agregarCampo(
            panel,
            "Proveedor electrónico",
            campoProveedor
        );

        return panel;
    }

    // ESTILO GENERAL DE LOS FORMULARIOS

    private JPanel crearPanelFormulario() {

        JPanel panel = new JPanel();

        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);

        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(228, 234, 240)
                ),
                BorderFactory.createEmptyBorder(22, 20, 22, 20)
            )
        );

        panel.setLayout(
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        return panel;
    }

    private void agregarCampo(
        JPanel panel,
        String etiqueta,
        JComponent campo
    ) {

        JLabel label = new JLabel(etiqueta);

        label.setFont(
            new Font("Segoe UI", Font.BOLD, 12)
        );

        label.setForeground(new Color(55, 65, 81));

        campo.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 36)
        );

        campo.setFont(
            new Font("Segoe UI", Font.PLAIN, 13)
        );

        panel.add(label);
        panel.add(Box.createVerticalStrut(7));
        panel.add(campo);
        panel.add(Box.createVerticalStrut(20));
    }

    // VALIDAR Y CREAR MÉTODO DE PAGO

    private void confirmarPago() {

        try {

            metodoPago = crearMetodoPago();

            confirmado = true;

            dispose();

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Datos incorrectos",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    public MetodoPago crearMetodoPago() {

        String tipo = comboMetodoPago.getSelectedItem().toString();

        switch (tipo) {

            case "Efectivo":

                String montoTexto =
                    campoMontoRecibido.getText().trim();

                if (montoTexto.isEmpty()) {
                    throw new IllegalArgumentException(
                        "Ingresa el monto recibido."
                    );
                }

                double recibido;

                try {
                    recibido = Double.parseDouble(montoTexto);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(
                        "El monto recibido no es válido."
                    );
                }

                if (!Double.isFinite(recibido)
                        || recibido < totalVenta) {
                    throw new IllegalArgumentException(
                        "El monto recibido debe ser igual o mayor "
                        + "al total de la venta."
                    );
                }

                return new Efectivo(recibido);

            case "Tarjeta":

                String numero =
                    campoNumeroTarjeta.getText().trim();

                String digitos = numero.replaceAll("[ -]", "");

                if (!digitos.matches("\\d{13,19}")) {
                    throw new IllegalArgumentException(
                        "Ingresa un número de tarjeta válido."
                    );
                }

                String tipoTarjeta =
                    comboTipoTarjeta.getSelectedItem().toString();

                return new Tarjeta(digitos, tipoTarjeta);

            case "Transferencia":

                String referencia =
                    campoReferencia.getText().trim();

                if (referencia.isEmpty()) {
                    throw new IllegalArgumentException(
                        "Ingresa el número de referencia."
                    );
                }

                return new Transferencia(referencia);

            case "Pago electrónico":

                String cuenta =
                    campoCuenta.getText().trim();

                String proveedor =
                    campoProveedor.getText().trim();

                if (cuenta.isEmpty() || proveedor.isEmpty()) {
                    throw new IllegalArgumentException(
                        "Completa la cuenta y el proveedor electrónico."
                    );
                }

                return new PagoElectronico(cuenta, proveedor);

            default:

                throw new IllegalArgumentException(
                    "Selecciona un método de pago válido."
                );
        }
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    
    private void actualizarCambio() {

        String texto = campoMontoRecibido.getText().trim();

        if (texto.isEmpty()) {
            etiquetaCambio.setText("Cambio: $0.00");
            etiquetaCambio.setForeground(Color.GRAY);
            return;
        }

        try {

            double recibido = Double.parseDouble(texto);

            if (!Double.isFinite(recibido) || recibido < 0) {
                etiquetaCambio.setText("Ingrese un monto válido.");
                etiquetaCambio.setForeground(Color.RED);
                return;
            }

            double diferencia = recibido - totalVenta;

            if (diferencia < 0) {

                etiquetaCambio.setText(
                    String.format("Faltan: $%.2f", Math.abs(diferencia))
                );

                etiquetaCambio.setForeground(Color.RED);

            } else {

                etiquetaCambio.setText(
                    String.format("Debe devolver: $%.2f", diferencia)
                );

                etiquetaCambio.setForeground(
                    diferencia == 0 ? new Color(0, 130, 0)
                                    : new Color(0, 100, 200)
                );
            }

        } catch (NumberFormatException e) {

            etiquetaCambio.setText("Ingrese un monto válido.");
            etiquetaCambio.setForeground(Color.RED);
        }
    }
}

