package sv.innovacion.util;

import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;

public final class Estilo {
    public static final Color AZUL = new Color(20, 55, 95);
    public static final Color AZUL_CLARO = new Color(37, 112, 174);
    public static final Color CELESTE = new Color(47, 166, 207);
    public static final Color FONDO = new Color(244, 247, 251);
    public static final Color TEXTO = new Color(38, 51, 66);
    public static final Color GRIS = new Color(104, 119, 135);
    public static final Color BLANCO = Color.WHITE;

    private Estilo() {}

    public static void configurar() {
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("PasswordField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 13));
    }
}
