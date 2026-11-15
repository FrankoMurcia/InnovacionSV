package sv.innovacion.model;

public class Usuario {
    private final String nombreCompleto;
    private final String usuario;
    private final String correo;
    private final String rol;
    private final String salt;
    private final String hash;

    public Usuario(String nombreCompleto, String usuario, String correo, String rol,
                   String salt, String hash) {
        this.nombreCompleto = nombreCompleto;
        this.usuario = usuario;
        this.correo = correo;
        this.rol = rol;
        this.salt = salt;
        this.hash = hash;
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public String getUsuario() { return usuario; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public String getSalt() { return salt; }
    public String getHash() { return hash; }
}
