package sv.innovacion.ui;

import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import sv.innovacion.service.UsuarioService;
import sv.innovacion.util.Estilo;

public class RegistroDialog extends JDialog {
    public RegistroDialog(JFrame padre, UsuarioService servicio) {
        super(padre, "Crear cuenta | Innovación SV", true);
        setSize(430, 560);
        setLocationRelativeTo(padre);
        getContentPane().setBackground(Estilo.FONDO);

        JTextField nombre = Componentes.campo();
        JTextField usuario = Componentes.campo();
        JTextField correo = Componentes.campo();
        JPasswordField clave = Componentes.clave();
        JPasswordField confirmar = Componentes.clave();

        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(25, 38, 25, 38));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(Componentes.titulo("Crear una cuenta", 24));
        p.add(Box.createVerticalStrut(6));
        JLabel nota = new JLabel("El nuevo perfil tendrá el rol de Operador.");
        nota.setForeground(Estilo.GRIS);
        p.add(nota);
        p.add(Box.createVerticalStrut(20));
        p.add(Componentes.campoConEtiqueta("NOMBRE COMPLETO", nombre));
        p.add(Box.createVerticalStrut(12));
        p.add(Componentes.campoConEtiqueta("NOMBRE DE USUARIO", usuario));
        p.add(Box.createVerticalStrut(12));
        p.add(Componentes.campoConEtiqueta("CORREO ELECTRÓNICO", correo));
        p.add(Box.createVerticalStrut(12));
        p.add(Componentes.campoConEtiqueta("CONTRASEÑA (MÍNIMO 8 CARACTERES)", clave));
        p.add(Box.createVerticalStrut(12));
        p.add(Componentes.campoConEtiqueta("CONFIRMAR CONTRASEÑA", confirmar));
        p.add(Box.createVerticalStrut(20));

        JButton crear = Componentes.botonPrincipal("REGISTRAR USUARIO");
        crear.addActionListener(e -> {
            char[] c1 = clave.getPassword();
            char[] c2 = confirmar.getPassword();
            if (!Arrays.equals(c1, c2)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.");
            } else {
                try {
                    String error = servicio.registrar(nombre.getText(), usuario.getText(), correo.getText(), c1);
                    if (error != null) {
                        JOptionPane.showMessageDialog(this, error, "Revisa los datos", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "La cuenta se creó correctamente. Ya puedes iniciar sesión.");
                        dispose();
                    }
                } catch (java.sql.SQLException ex) {
                    JOptionPane.showMessageDialog(this, "No se pudo guardar el usuario en MySQL. Revisa la conexión.\\n" + ex.getMessage(),
                            "Error de base de datos", JOptionPane.ERROR_MESSAGE);
                }
            }
            Arrays.fill(c1, '\0');
            Arrays.fill(c2, '\0');
        });
        p.add(crear);
        add(p);
    }
}
