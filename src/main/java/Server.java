import server.CallbackServer;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.Scanner;

public class Server {

    private static final int RMIPORT = 1099;
    private static final String HOSTNAME = "localhost";
    public static String REGISTRY_URL = "rmi://" + HOSTNAME + ":" + RMIPORT + "/callback";

    public static void main(String[] args) {
        try {
            // Start the RMI registry
            startRegistry(RMIPORT);

            // Start the server
            CallbackServer server = new CallbackServer();

            // Bind the server to the registry
            Naming.rebind(REGISTRY_URL, server);

            System.out.println("Servidor de objetos iniciado en " + REGISTRY_URL);

            // Create a separate thread to listen for "?" command
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String input = scanner.nextLine();
                    if ("?".equals(input.trim())) {
                        printConnectedUsers(server);
                    }
                }
            }).start();

        } catch (Exception ex) {
            System.out.println("No se ha podido iniciar el servidor de objetos: " + ex.getMessage());
            System.exit(1);
        }



    }

    private static void startRegistry(int RMIPortNum) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list( );
        } catch (RemoteException e) {
            // No valid registry at that port.
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
        }
    }

    private static void printConnectedUsers(CallbackServer server) {
        try {
            System.out.println("Clientes conectados:");
            server.getConnectedUsers().forEach((name, user) -> {
                System.out.println(" - " + name);
            });
        } catch (Exception e) {
            System.out.println("Error al obtener la lista de usuarios conectados: " + e.getMessage());
        }
    }
}