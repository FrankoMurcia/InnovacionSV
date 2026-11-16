package sv.innovacion.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import sv.innovacion.conexion.Conexion;
import sv.innovacion.model.Usuario;

public class UsuarioService {
    private static final int ITERACIONES = 120_000;

    public boolean autenticar(String usuario, char[] clave) throws SQLException {
        String sql = "SELECT nombre_completo, nombre_usuario, correo, rol, password_salt, password_hash "
                   + "FROM usuarios WHERE nombre_usuario = ? "; //AND estado = 'ACTIVO'
        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String salt = rs.getString("password_salt");
                String hashGuardado = rs.getString("password_hash");
                String hashIngresado = hash(clave, salt);
                return MessageDigest.isEqual(
                    hashIngresado.getBytes(StandardCharsets.UTF_8),
                    hashGuardado.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public String registrar(String nombre, String usuario, String correo, char[] clave) throws SQLException {
        String user = usuario.trim();
        String mail = correo.trim();
        if (nombre.isBlank() || user.isBlank() || mail.isBlank()) return "Completa todos los campos.";
        if (!mail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return "Escribe un correo válido.";
        if (user.length() < 4) return "El usuario debe tener al menos 4 caracteres.";
        if (clave.length < 8) return "La contraseña debe tener al menos 8 caracteres.";

        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        String salt = Base64.getEncoder().encodeToString(saltBytes);
        String passwordHash = hash(clave, salt);

        String sql = "INSERT INTO usuarios (nombre_completo, nombre_usuario, correo, password_hash, password_salt, rol) "//, estado
                   + "VALUES (?, ?, ?, ?, ?, 'OPERADOR')"; //, 'ACTIVO'
        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setString(2, user);
            ps.setString(3, mail);
            ps.setString(4, passwordHash);
            ps.setString(5, salt);
            ps.executeUpdate();
            return null;
        } catch (SQLIntegrityConstraintViolationException ex) {
            return "El nombre de usuario o correo ya está registrado.";
        }
    }

    public boolean existeCorreo(String correo) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE correo = ? LIMIT 1";
        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, correo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Usuario buscarPorUsuario(String usuario) throws SQLException {
        String sql = "SELECT nombre_completo, nombre_usuario, correo, rol, password_salt, password_hash "
                   + "FROM usuarios WHERE nombre_usuario = ?";
        try (Connection cn = Conexion.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Usuario(rs.getString("nombre_completo"), rs.getString("nombre_usuario"),
                    rs.getString("correo"), rs.getString("rol"), rs.getString("password_salt"),
                    rs.getString("password_hash"));
            }
        }
    }

    private String hash(char[] clave, String saltTexto) {
        PBEKeySpec spec = new PBEKeySpec(clave,
                Base64.getDecoder().decode(saltTexto), ITERACIONES, 256);
        try {
            byte[] encoded = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(encoded);
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible proteger la contraseña.", ex);
        } finally {
            spec.clearPassword();
        }
    }
}
