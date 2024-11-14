package socrates.client;

/**
 * This interface is used to define the methods that the server will call on the client
 * to notify it of events related to the user's friends.
 */
public interface CallbackClientInterface extends java.rmi.Remote {
    void friendConnected(String user) throws java.rmi.RemoteException; // This method is called when a friend connects
    void friendDisconnected(String user) throws java.rmi.RemoteException; // This method is called when a friend disconnects

    void friendDeleted(String user) throws java.rmi.RemoteException; // This method is called when a friend is deleted
    void friendAdded(String user) throws java.rmi.RemoteException; // This method is called when a friend is added

    void friendRequestReceived(String user) throws java.rmi.RemoteException; // This method is called when a friend request is received
    void friendRequestRejected(String user) throws java.rmi.RemoteException; // This method is called when a friend request is rejected
}
