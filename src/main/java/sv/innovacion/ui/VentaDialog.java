
package sv.innovacion.ui;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

import sv.innovacion.model.Venta;
import sv.innovacion.model.Estado;
import sv.innovacion.service.VentaService;
import sv.innovacion.util.Estilo;

public class VentaDialog extends JDialog {

    private final VentaService servicio;

    private JTextField campoTotal;

    public VentaDialog(JFrame padre, VentaService servicio) {

        super(padre, "Registrar venta | Innovacion SV", true);

        this.servicio = servicio;

        configurarVentana();
        construirInterfaz();
    }

    // Configuración general
    private void configurarVentana() {

        setSize(460, 390);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setBackground(Estilo.FONDO);
    }

    // Construcción de la interfaz
    private void construirInterfaz() {

        JPanel principal = new JPanel();

        principal.setOpaque(false);
        principal.setBorder(
            BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        principal.setLayout(
            new BoxLayout(principal, BoxLayout.Y_AXIS)
        );

        // Título
        JLabel titulo = Componentes.titulo("Nueva venta", 25);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        principal.add(titulo);

        principal.add(Box.createVerticalStrut(5));

        JLabel subtitulo = new JLabel(
            "Registra una nueva operación comercial"
        );

        subtitulo.setForeground(Estilo.GRIS);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        principal.add(subtitulo);

        principal.add(Box.createVerticalStrut(25));

        // Campo del monto
        campoTotal = Componentes.campo();

        campoTotal.setFont(new Font("SansSerif", Font.BOLD, 17));
        campoTotal.setHorizontalAlignment(JTextField.LEFT);
        campoTotal.setToolTipText("Ejemplo: 125.50");

        JLabel etiquetaTotal = new JLabel("Monto total de la venta ($)");

        etiquetaTotal.setFont(
            new Font("SansSerif", Font.BOLD, 13)
        );

        etiquetaTotal.setForeground(Estilo.GRIS);

        etiquetaTotal.setAlignmentX(Component.LEFT_ALIGNMENT);

        principal.add(etiquetaTotal);

        principal.add(Box.createVerticalStrut(8));

        campoTotal.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 42)
        );

        campoTotal.setAlignmentX(Component.LEFT_ALIGNMENT);

        principal.add(campoTotal);

        principal.add(Box.createVerticalStrut(20));

        // Fecha automática
        JLabel etiquetaFecha = new JLabel("Fecha de registro");

        etiquetaFecha.setFont(
            new Font("SansSerif", Font.BOLD, 13)
        );

        etiquetaFecha.setForeground(Estilo.GRIS);

        etiquetaFecha.setAlignmentX(Component.LEFT_ALIGNMENT);

        principal.add(etiquetaFecha);

        principal.add(Box.createVerticalStrut(7));

        JLabel fecha = new JLabel(
            LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            )
        );

        fecha.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fecha.setAlignmentX(Component.LEFT_ALIGNMENT);

        principal.add(fecha);

        principal.add(Box.createVerticalStrut(18));

        // Estado inicial
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        panelEstado.setOpaque(false);
        panelEstado.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel estado = new JLabel("PENDIENTE");

        estado.setOpaque(true);
        estado.setBackground(new Color(255, 243, 205));
        estado.setForeground(new Color(133, 100, 4));

        estado.setFont(
            new Font("SansSerif", Font.BOLD, 11)
        );

        estado.setBorder(
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        );

        panelEstado.add(estado);

        principal.add(panelEstado);

        principal.add(Box.createVerticalStrut(25));

        // Botones
        JPanel botones = new JPanel(new GridLayout(1, 2, 12, 0));

        botones.setOpaque(false);
        botones.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 42)
        );

        JButton cancelar = crearBotonSecundario("Cancelar");
        JButton registrar = Componentes.botonPrincipal("Registrar venta");

        cancelar.addActionListener(e -> dispose());

        registrar.addActionListener(e -> registrarVenta());

        botones.add(cancelar);
        botones.add(registrar);

        principal.add(botones);

        add(principal);

        getRootPane().setDefaultButton(registrar);

        SwingUtilities.invokeLater(() -> campoTotal.requestFocusInWindow());
    }

    // Registrar venta
    private void registrarVenta() {

        String textoTotal = campoTotal.getText().trim();

        if (textoTotal.isEmpty()) {
            mostrarAdvertencia("Ingresa el monto de la venta.");
            campoTotal.requestFocusInWindow();
            return;
        }

        double monto;

        try {
            monto = Double.parseDouble(
                textoTotal.replace(",", ".")
            );

        } catch (NumberFormatException ex) {
            mostrarAdvertencia(
                "Ingresa un monto numérico válido. Ejemplo: 125.50"
            );

            campoTotal.requestFocusInWindow();
            return;
        }

        if (!Double.isFinite(monto) || monto <= 0) {
            mostrarAdvertencia(
                "El monto debe ser mayor que cero."
            );

            campoTotal.requestFocusInWindow();
            return;
        }

        if (Double.compare(
                monto,
                Math.round(monto * 100.0) / 100.0
            ) != 0) {

            mostrarAdvertencia(
                "El monto no puede tener más de dos decimales."
            );

            campoTotal.requestFocusInWindow();
            return;
        }

        try {

            // Crear venta sin método de pago.
            Venta venta = new Venta(
                0,
                LocalDateTime.now(),
                monto,
                Estado.PENDIENTE
            );

            boolean resultado = servicio.registrar(venta);

            if (resultado) {

                JOptionPane.showMessageDialog(
                    this,
                    "La venta se registró correctamente.\n"
                    + "Estado: PENDIENTE",
                    "Venta registrada",
                    JOptionPane.INFORMATION_MESSAGE
                );

                dispose();

            } else {

                JOptionPane.showMessageDialog(
                    this,
                    "No fue posible registrar la venta.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (java.sql.SQLException ex) {

            JOptionPane.showMessageDialog(
                this,
                "No se pudo guardar la venta en MySQL.\n"
                + ex.getMessage(),
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // Mostrar advertencias
    private void mostrarAdvertencia(String mensaje) {

        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Validación de datos",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    
    private JButton crearBotonSecundario(String texto) {

        JButton boton = new JButton(texto);

        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setForeground(Estilo.GRIS);
        boton.setBackground(Color.WHITE);

        boton.setFocusPainted(false);
        boton.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(210, 215, 225)
                ),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            )
        );

        boton.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        return boton;
    }

}
