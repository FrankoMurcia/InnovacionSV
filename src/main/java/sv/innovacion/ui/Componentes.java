package sv.innovacion.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import sv.innovacion.util.Estilo;

final class Componentes {
    private Componentes() {}

    static JLabel titulo(String texto, int tamano) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, tamano));
        l.setForeground(Estilo.TEXTO);
        return l;
    }

    static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Estilo.GRIS);
        return l;
    }

    static JTextField campo() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(300, 42));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 225, 234)),
                new EmptyBorder(8, 11, 8, 11)));
        return f;
    }

    static JPasswordField clave() {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(300, 42));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 225, 234)),
                new EmptyBorder(8, 11, 8, 11)));
        return f;
    }

    static JButton botonPrincipal(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(Estilo.AZUL_CLARO);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(300, 43));
        return b;
    }

    static JButton botonTexto(String texto) {
        JButton b = new JButton(texto);
        b.setForeground(Estilo.AZUL_CLARO);
        b.setBackground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JPanel campoConEtiqueta(String nombre, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 7));
        p.setOpaque(false);
        p.add(etiqueta(nombre), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }
}
