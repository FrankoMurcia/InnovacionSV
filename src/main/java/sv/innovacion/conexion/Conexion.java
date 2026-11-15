package sv.innovacion.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexion {
    // Cambia estos valores según la configuración de tu MySQL local.
    private static final String URL =
            "jdbc:mysql://localhost:3306/sistemapagos?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root"; // Escribe aquí tu contraseña local de MySQL.

    private Conexion() {}

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
