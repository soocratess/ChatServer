package socrates.bd;

import java.util.ArrayList;

public interface BDAdminInterface {
    boolean iniciarSesion(String username, String contrasena);
    boolean registrarse(String username, String contrasena, String direccionObjetoRemoto);
    boolean borrarUsuario(String username, String contrasena);
    boolean buscarUsuario(String username);
    ArrayList<String> obtenerAmistades(String usuario);
    ArrayList<String> obtenerPeticiones(String usuario);
    boolean enviarPeticion(String origen, String destino);
    boolean aceptarPeticion(String origen, String destino);
    boolean borrarAmigo(String origen, String destino);
    boolean rechazarAmistad(String origen, String destino);
    boolean cambiarClaveAcceso(String username, String oldPasswd, String newPasswd);
}
