package socrates.bd;

import java.sql.*;
import java.util.ArrayList;

public class BDAdmin implements BDAdminInterface {
    // Cambiar la URL de conexión para MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/ChatServer"; // localhost si la aplicación Java está en la misma máquina que Docker
    private static final String USER = "user"; // Usuario configurado en docker-compose.yml
    private static final String PASSWORD = "password"; // Contraseña configurada en docker-compose.yml
    private Connection connection;

    // Constructor de la clase, se llama automáticamente al instanciar un objeto
    public BDAdmin() {
        conectar(); // Llama al metodo conectar al crear una instancia de la clase
    }

    // Metodo para establecer la conexión a la base de datos
    public void conectar() {
        try {
            // Establecer la conexión a la base de datos con usuario y contraseña
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión establecida a la base de datos.");
        } catch (SQLException e) {
            System.out.println("Error al conectarse a la bd: " + e.getMessage());
        }
    }

    // Metodo para cerrar la conexión a la base de datos
    public void desconectar() {
        try {
            // Cerrar la conexión a la base de datos si no está cerrada
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }

    // Metodo para iniciar sesión de usuario
    public boolean iniciarSesion(String username, String contrasena) {
        try {
            String sql = "SELECT * FROM USUARIO WHERE username = ? AND contrasena = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, contrasena);
                try (ResultSet resultSet = statement.executeQuery()) {
                    System.out.println("Consulta realizada sin errores");
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al iniciar sesion: " + e.getMessage());
            return false;
        }
    }

    // Metodo para registrar un nuevo usuario
    public boolean registrarse(String username, String contrasena, String direccionObjetoRemoto) {
        try {
            String sql = "INSERT INTO USUARIO (username, contrasena, direccion_objeto_remoto) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, contrasena);
                statement.setString(3, direccionObjetoRemoto);
                int rowsAffected = statement.executeUpdate();
                System.out.println("Filas afectadas en INSERT " + rowsAffected);
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    // Metodo para borrar un usuario
    public boolean borrarUsuario(String username, String contrasena) {
        try {
            // Preparar la declaración SQL para borrar el usuario con verificación de contraseña
            String sql = "DELETE FROM USUARIO WHERE username = ? AND contrasena = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Establecer los valores de los parámetros
                statement.setString(1, username);
                statement.setString(2, contrasena);

                // Ejecutar la consulta y obtener el número de filas afectadas
                int filasAfectadas = statement.executeUpdate();

                System.out.println("Filas afectadas en DELETE " + filasAfectadas);
                // Devolver true si se eliminó al menos una fila, indicando éxito
                return filasAfectadas > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al borrar usuario: " + e.getMessage());
            return false;
        }
    }

    // Metodo para buscar un usuario en la base de datos
    public boolean buscarUsuario(String username) {
        try {
            String sql = "SELECT * FROM USUARIO WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    System.out.println("Consulta realizada sin errores");
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
            return false;
        }
    }

    // Metodo para obtener la lista de amigos de un usuario
    public ArrayList<String> obtenerAmistades(String usuario) {
        try {
            String sql = "SELECT * FROM AMISTAD WHERE pendiente = 0 AND (usuario_que_pide = ? OR usuario_que_recibe = ?)";
            ArrayList<String> amigos = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, usuario);
                statement.setString(2, usuario);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String u1 = resultSet.getString("usuario_que_pide");
                        String u2 = resultSet.getString("usuario_que_recibe");
                        System.out.println("user q pide: " + u1);
                        System.out.println("user q recibe: " + u2);
                        System.out.println("User: " + usuario);
                        if (!u1.equalsIgnoreCase(usuario))
                            amigos.add(u1);
                        else amigos.add(u2);
                    }
                    System.out.println("Consulta realizada sin errores");
                    return amigos;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
            return null;
        }
    }

    // Metodo para obtener la lista de peticiones de amistad pendientes de un usuario
    public ArrayList<String> obtenerPeticiones(String usuario) {
        String sql = "SELECT usuario_que_pide FROM AMISTAD WHERE pendiente = 1 AND usuario_que_recibe = ?";        ArrayList<String> solicitudes = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, usuario);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    solicitudes.add(resultSet.getString("usuario_que_pide"));
                }
                System.out.println("Consulta realizada sin errores");
                return solicitudes;
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
            return null;
        }
    }

    // Metodo para enviar una petición de amistad
    public boolean enviarPeticion(String origen, String destino) {
        String sql = "INSERT INTO AMISTAD (usuario_que_pide, usuario_que_recibe, pendiente) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origen);
            statement.setString(2, destino);
            statement.setInt(3, 1);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Filas afectadas en INSERT " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al pedir amistad: " + e.getMessage());
            return false;
        }
    }

    // Metodo para aceptar una petición de amistad
    public boolean aceptarPeticion(String origen, String destino) {
        String sql = "UPDATE AMISTAD SET pendiente = 0 WHERE usuario_que_pide = ? AND usuario_que_recibe = ? AND pendiente = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origen);
            statement.setString(2, destino);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Filas afectadas en UPDATE " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al aceptar amistad: " + e.getMessage());
            return false;
        }
    }

    // Metodo para eliminar a un amigo
    public boolean borrarAmigo(String origen, String destino) {
        String sql = "DELETE FROM AMISTAD WHERE ((usuario_que_pide = ? AND usuario_que_recibe = ?) OR (usuario_que_pide = ? AND usuario_que_recibe = ?)) AND pendiente=0";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origen);
            statement.setString(2, destino);
            statement.setString(3, destino);
            statement.setString(4, origen);

            int rowsAffected = statement.executeUpdate();
            System.out.println("Filas afectadas en DELETE " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar amistad: " + e.getMessage());
            return false;
        }
    }

    // Metodo para rechazar una petición de amistad
    public boolean rechazarAmistad(String origen, String destino) {
        String sql = "DELETE FROM AMISTAD WHERE usuario_que_pide = ? AND usuario_que_recibe = ? AND pendiente = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origen);
            statement.setString(2, destino);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Filas afectadas en DELETE " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al rechazar peticion de amistad: " + e.getMessage());
            return false;
        }
    }

    // Metodo para cambiar la contraseña del usuario
    public boolean cambiarClaveAcceso(String username, String oldPasswd, String newPasswd) {
        // UPDATE AMISTAD SET pendiente = 0 WHERE usuario_que_pide = ? AND usuario_que_recibe = ? AND pendiente = 1
        String sql = "UPDATE USUARIO SET contrasena = ? WHERE username = ? AND contrasena = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newPasswd);
            statement.setString(2, username);
            statement.setString(3, oldPasswd);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Filas afectadas en UPDATE " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al cambiar la contraseña de " + username + ": " + e.getMessage());
            return false;
        }
    }
}