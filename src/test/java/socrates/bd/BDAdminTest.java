package socrates.bd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BDAdminTest {
    private BDAdminInterface bdAdmin;

    @BeforeEach
    public void setUp() {
        // Instancia de BDAdmin para cada prueba
        bdAdmin = new BDAdmin();
    }

    @AfterEach
    public void tearDown() {
        // Eliminar cualquier amistad creada durante las pruebas
        bdAdmin.borrarAmigo("user1", "user2");

        // Eliminar usuarios de prueba creados en cada test
        bdAdmin.borrarUsuario("testuser_login", "password_login");
        bdAdmin.borrarUsuario("testuser_search", "password_search");
        bdAdmin.borrarUsuario("testuser_delete", "password_delete");
        bdAdmin.borrarUsuario("user1", "password1");
        bdAdmin.borrarUsuario("user2", "password2");
    }

    @Test
    public void testIniciarSesion() {
        // Prueba de inicio de sesión
        bdAdmin.registrarse("testuser_login", "password_login", "direccion_remota");
        boolean resultado = bdAdmin.iniciarSesion("testuser_login", "password_login");
        assertTrue(resultado, "El usuario debería poder iniciar sesión correctamente.");
        System.out.println("Inicio de sesión exitoso.");
    }

    @Test
    public void testBuscarUsuario() {
        // Prueba la búsqueda de un usuario existente
        bdAdmin.registrarse("testuser_search", "password_search", "direccion_remota");
        boolean encontrado = bdAdmin.buscarUsuario("testuser_search");
        assertTrue(encontrado, "El usuario debería existir en la base de datos.");
        System.out.println("Búsqueda de usuario exitosa.");
    }

    @Test
    public void testBorrarUsuario() {
        // Prueba la eliminación de un usuario
        bdAdmin.registrarse("testuser_delete", "password_delete", "direccion_remota");
        boolean eliminado = bdAdmin.borrarUsuario("testuser_delete", "password_delete");
        assertTrue(eliminado, "El usuario debería eliminarse correctamente.");
        System.out.println("Eliminación de usuario exitosa.");
    }

    @Test
    public void testAmistad() {
        // Prueba la funcionalidad de amistad
        bdAdmin.registrarse("user1", "password1", "direccion_remota1");
        bdAdmin.registrarse("user2", "password2", "direccion_remota2");

        // Enviar petición de amistad
        boolean peticionEnviada = bdAdmin.enviarPeticion("user1", "user2");
        assertTrue(peticionEnviada, "La petición de amistad debería enviarse correctamente.");

        // Aceptar petición de amistad
        boolean amistadAceptada = bdAdmin.aceptarPeticion("user1", "user2");
        assertTrue(amistadAceptada, "La petición de amistad debería aceptarse correctamente.");

        // Comprobar amistades
        ArrayList<String> amigos = bdAdmin.obtenerAmistades("user1");
        assertTrue(amigos.contains("user2"), "user2 debería estar en la lista de amistades de user1.");
        System.out.println("Funcionalidad de amistad probada exitosamente.");
    }
}
