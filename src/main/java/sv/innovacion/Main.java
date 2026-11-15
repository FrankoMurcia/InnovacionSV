package sv.innovacion;

import javax.swing.SwingUtilities;
import sv.innovacion.service.UsuarioService;
import sv.innovacion.ui.LoginFrame;
import sv.innovacion.util.Estilo;

public class Main {
    public static void main(String[] args) {
        Estilo.configurar();
        UsuarioService usuarios = new UsuarioService();
        SwingUtilities.invokeLater(() -> new LoginFrame(usuarios).setVisible(true));
    }
}
