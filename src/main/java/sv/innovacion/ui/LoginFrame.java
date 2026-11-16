package sv.innovacion.ui;

import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import sv.innovacion.model.Usuario;
import sv.innovacion.service.UsuarioService;
import sv.innovacion.service.VentaService;
import sv.innovacion.util.Estilo;

public class LoginFrame extends JFrame {
    private final UsuarioService servicio;
    private final JTextField txtUsuario = Componentes.campo();
    private final JPasswordField txtClave = Componentes.clave();

    public LoginFrame(UsuarioService servicio) {
        this.servicio = servicio;
        setTitle("Innovación SV | Acceso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 570);
        setMinimumSize(new Dimension(780, 520));
        setLocationRelativeTo(null);
        construir();
    }

    private void construir() {
        JPanel raiz = new JPanel(new GridLayout(1, 2));
        raiz.add(panelMarca());
        raiz.add(panelFormulario());
        setContentPane(raiz);
    }

    private JPanel panelMarca() {
        JPanel p = new JPanel();
        p.setBackground(Estilo.AZUL);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(65, 48, 45, 40));

        JLabel marca = new JLabel("INNOVACIÓN SV");
        marca.setFont(new Font("Segoe UI", Font.BOLD, 25));
        marca.setForeground(Color.WHITE);
        JLabel simbolo = new JLabel("◆");
        simbolo.setFont(new Font("Segoe UI", Font.BOLD, 48));
        simbolo.setForeground(new Color(93, 205, 230));
        JLabel subtitulo = new JLabel("<html><div style='width:290px'>Soluciones inteligentes para la gestión y el control de operaciones.</div></html>");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        subtitulo.setForeground(new Color(224, 236, 247));
        JLabel pie = new JLabel("PLATAFORMA DE GESTIÓN EMPRESARIAL");
        pie.setFont(new Font("Segoe UI", Font.BOLD, 10));
        pie.setForeground(new Color(165, 190, 211));

        p.add(simbolo);
        p.add(Box.createVerticalStrut(12));
        p.add(marca);
        p.add(Box.createVerticalStrut(22));
        p.add(subtitulo);
        p.add(Box.createVerticalGlue());
        p.add(pie);
        return p;
    }

    private JPanel panelFormulario() {
        JPanel exterior = new JPanel(new GridBagLayout());
        exterior.setBackground(Estilo.FONDO);
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(340, 390));

        JLabel titulo = Componentes.titulo("Bienvenido", 28);
        JLabel info = new JLabel("Ingresa tus credenciales para continuar");
        info.setForeground(Estilo.GRIS);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JCheckBox ver = new JCheckBox("Mostrar contraseña");
        ver.setOpaque(false);
        ver.setForeground(Estilo.GRIS);
        ver.addActionListener(e -> txtClave.setEchoChar(ver.isSelected() ? (char) 0 : '•'));

        JButton entrar = Componentes.botonPrincipal("INICIAR SESIÓN");
        entrar.addActionListener(e -> {
            try {
                ingresar();
            } catch (SQLException ex) {
                Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        JButton registro = Componentes.botonTexto("Crear una cuenta");
        registro.addActionListener(e -> new RegistroDialog(this, servicio).setVisible(true));
        JButton recuperar = Componentes.botonTexto("¿Olvidaste tu contraseña?");
        recuperar.addActionListener(e -> new RecuperarDialog(this, servicio).setVisible(true));

        p.add(titulo);
        p.add(Box.createVerticalStrut(7));
        p.add(info);
        p.add(Box.createVerticalStrut(30));
        p.add(Componentes.campoConEtiqueta("USUARIO", txtUsuario));
        p.add(Box.createVerticalStrut(17));
        p.add(Componentes.campoConEtiqueta("CONTRASEÑA", txtClave));
        p.add(Box.createVerticalStrut(8));
        p.add(ver);
        p.add(Box.createVerticalStrut(20));
        p.add(entrar);
        p.add(Box.createVerticalStrut(9));
        p.add(registro);
        p.add(recuperar);
        p.add(Box.createVerticalStrut(15));

        exterior.add(p);
        return exterior;
    }

    private void ingresar() throws SQLException {
        String usuarioTexto = txtUsuario.getText().trim();
        char[] clave = txtClave.getPassword();
        if (usuarioTexto.isEmpty() || clave.length == 0) {
            JOptionPane.showMessageDialog(this, "Ingresa tu usuario y contraseña.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            Arrays.fill(clave, '\0');
            return;
        }
        boolean valido = servicio.autenticar(usuarioTexto, clave);
        Arrays.fill(clave, '\0');
        if (!valido) {
            JOptionPane.showMessageDialog(this, "El usuario o la contraseña no son correctos.",
                    "No se pudo iniciar sesión", JOptionPane.ERROR_MESSAGE);
            txtClave.setText("");
            return;
        }
        Usuario usuario = servicio.buscarPorUsuario(usuarioTexto);
        
        VentaService ventaServicio = new VentaService();
        new DashboardFrame(servicio, usuario, ventaServicio).setVisible(true);
        dispose();
    }
}
