package sv.innovacion.ui;

import java.awt.*;
import javax.swing.*;
import sv.innovacion.service.UsuarioService;
import sv.innovacion.util.Estilo;

public class RecuperarDialog extends JDialog {
    public RecuperarDialog(JFrame padre, UsuarioService servicio) {
        super(padre, "Recuperación de acceso", true);
        setSize(390, 250);
        setLocationRelativeTo(padre);
        getContentPane().setBackground(Estilo.FONDO);

        JTextField correo = Componentes.campo();
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(Componentes.titulo("Recuperar acceso", 22));
        p.add(Box.createVerticalStrut(8));
        JLabel texto = new JLabel("<html>Escribe el correo asociado a tu cuenta.<br>Esta versión solo comprueba si existe.</html>");
        texto.setForeground(Estilo.GRIS);
        p.add(texto);
        p.add(Box.createVerticalStrut(16));
        p.add(Componentes.campoConEtiqueta("CORREO ELECTRÓNICO", correo));
        p.add(Box.createVerticalStrut(17));
        JButton comprobar = Componentes.botonPrincipal("COMPROBAR CORREO");
        comprobar.addActionListener(e -> {
            if (correo.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Escribe tu correo.");
            } else {
                try {
                    if (servicio.existeCorreo(correo.getText())) {
                        JOptionPane.showMessageDialog(this, "La cuenta existe. La recuperación por correo se habilitará cuando conectemos un servicio de correo.");
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró una cuenta con ese correo.");
                    }
                } catch (java.sql.SQLException ex) {
                    JOptionPane.showMessageDialog(this, "No se pudo consultar MySQL. Revisa la conexión.\\n" + ex.getMessage(),
                            "Error de base de datos", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        p.add(comprobar);
        add(p);
    }
}
