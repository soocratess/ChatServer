package socrates.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import socrates.bd.BDAdminInterface;
import socrates.client.CallbackClientInterface;
import socrates.user.User;

import java.rmi.RemoteException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CallbackServerTest {
    private CallbackServer server;
    private BDAdminInterface mockBD;
    private CallbackClientInterface mockClient;

    @BeforeEach
    public void setUp() throws RemoteException {
        // Crear mocks de las dependencias
        mockBD = mock(BDAdminInterface.class);
        mockClient = mock(CallbackClientInterface.class);

        // Inicializar el servidor con el mock de BDAdmin
        server = new CallbackServer();
        server.setBd(mockBD);  // Asignar el mock a la instancia de BDAdmin en el servidor
    }

    @Test
    public void testLoginSuccess() throws RemoteException {
        String username = "testUser";
        String password = "testPassword";

        // Configurar el comportamiento del mock BDAdmin
        when(mockBD.login(username, password)).thenReturn(true);
        when(mockBD.getFriends(username)).thenReturn(new ArrayList<>());

        User user = server.login(mockClient, username, password);

        assertNotNull(user, "El usuario debería poder iniciar sesión correctamente.");
        assertTrue(server.getConnectedUsers().containsKey(username), "El usuario debería estar en la lista de usuarios conectados.");
    }

    @Test
    public void testLoginFailInvalidCredentials() throws RemoteException {
        String username = "testUser";
        String password = "wrongPassword";

        // Configurar el comportamiento del mock BDAdmin
        when(mockBD.login(username, password)).thenReturn(false);

        User user = server.login(mockClient, username, password);

        assertNull(user, "El inicio de sesión debería fallar con credenciales inválidas.");
    }

    @Test
    public void testLogOutSuccess() throws RemoteException {
        String username = "testUser";
        String password = "testPassword";

        // Configurar el comportamiento del mock BDAdmin
        when(mockBD.login(username, password)).thenReturn(true);
        when(mockBD.getFriends(username)).thenReturn(new ArrayList<>());

        // Log in para poder hacer log out
        server.login(mockClient, username, password);

        boolean result = server.logOut(username, password);
        assertTrue(result, "El usuario debería poder cerrar sesión correctamente.");
        assertFalse(server.getConnectedUsers().containsKey(username), "El usuario no debería estar en la lista de usuarios conectados después de cerrar sesión.");
    }

    @Test
    public void testRegisterSuccess() throws RemoteException {
        String username = "newUser";
        String password = "newPassword";

        // Configurar el comportamiento del mock BDAdmin
        when(mockBD.register(username, password, null)).thenReturn(true);
        when(mockBD.login(username, password)).thenReturn(true);
        when(mockBD.getFriends(username)).thenReturn(new ArrayList<>());

        User user = server.register(mockClient, username, password);

        assertNotNull(user, "El usuario debería poder registrarse correctamente.");
        assertTrue(server.getConnectedUsers().containsKey(username), "El usuario debería estar en la lista de usuarios conectados después de registrarse.");
    }

    @Test
    public void testSendFriendRequestSuccess() throws RemoteException {
        String username = "user1";
        String friendName = "user2";
        String password = "password";

        // Configurar el mock BDAdmin para simular usuarios y estado de la amistad
        when(mockBD.login(username, password)).thenReturn(true);
        when(mockBD.existsUser(friendName)).thenReturn(true);
        when(mockBD.getFriends(username)).thenReturn(new ArrayList<>());
        when(mockBD.getPendingFriendRequests(username)).thenReturn(new ArrayList<>());

        boolean result = server.sendFriendRequest(username, friendName, password);

        assertTrue(result, "La solicitud de amistad debería enviarse correctamente.");
        verify(mockBD, times(1)).sendFriendRequest(username, friendName);
    }

    @Test
    public void testRemoveFriendSuccess() throws RemoteException {
        String username = "user1";
        String friendName = "user2";
        String password = "password";

        // Configurar el mock BDAdmin para simular una amistad existente
        when(mockBD.login(username, password)).thenReturn(true);
        when(mockBD.getFriends(username)).thenReturn(new ArrayList<>() {{ add(friendName); }});

        boolean result = server.removeFriend(username, friendName, password);

        assertTrue(result, "El usuario debería poder eliminar a un amigo correctamente.");
        verify(mockBD, times(1)).removeFriend(username, friendName);
    }

    // Más pruebas pueden agregarse aquí para cada método...

}
