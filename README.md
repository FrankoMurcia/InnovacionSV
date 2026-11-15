# Innovación SV — Java Swing + MySQL

## Requisitos
- Apache NetBeans
- JDK 17 o superior
- MySQL 8.x y la base `sistemapagos`

## Paso 1. Crear la tabla de usuarios
En MySQL Workbench abre y ejecuta `database/crear_tabla_usuarios.sql`.
Este script solo agrega la tabla `usuarios`; no elimina las tablas existentes.

## Paso 2. Configurar la conexión
Abre `src/main/java/sv/innovacion/conexion/Conexion.java` y ajusta:
- USUARIO: tu usuario de MySQL (por ejemplo, root).
- CLAVE: la contraseña de ese usuario.
- URL: host, puerto y nombre de base de datos si no usas localhost:3306/sistemapagos.

No compartas ni publiques las credenciales de tu base de datos.

## Paso 3. Abrir en NetBeans
File > Open Project > selecciona la carpeta InnovacionSV.
NetBeans descargará MySQL Connector/J mediante Maven. Ejecuta el proyecto.

## Paso 4. Crear el primer administrador
Por seguridad, el registro público crea cuentas con rol OPERADOR.
Después de registrar el primer usuario, puedes promoverlo manualmente desde MySQL Workbench:
UPDATE usuarios SET rol='ADMINISTRADOR' WHERE nombre_usuario='tu_usuario';

## Funciones conectadas
- Login contra la tabla `usuarios`.
- Registro de usuarios en MySQL.
- Consulta de existencia de correo para la pantalla de recuperación (no envía correo).

## Importante
Esta versión conecta y administra usuarios. Los módulos de ventas, pagos y transacciones todavía muestran datos de demostración; la siguiente etapa será conectarlos a tus tablas reales.
No procesa pagos reales ni debe almacenar números completos de tarjetas o credenciales financieras.
