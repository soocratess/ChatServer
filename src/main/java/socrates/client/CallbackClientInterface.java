package socrates.client;

import socrates.user.User;

/**
 * This interface is used to define the methods that the server will call on the client
 * to notify it of events related to the user's friends.
 */
public interface CallbackClientInterface extends java.rmi.Remote {
    void friendConnected(String user) throws java.rmi.RemoteException; // This method is called when a friend connects
    void friendDisconnected(String user) throws java.rmi.RemoteException; // This method is called when a friend disconnects
    void friendAdded(String user) throws java.rmi.RemoteException; // This method is called when a friend is added
    void friendDeleted(String user) throws java.rmi.RemoteException; // This method is called when a friend is deleted

    void friendRequest(String user) throws java.rmi.RemoteException; // This method is called when a friend request is received
    void friendRequestAccepted(String user) throws java.rmi.RemoteException; // This method is called when a friend request is accepted
    void friendRequestRejected(String user) throws java.rmi.RemoteException; // This method is called when a friend request is rejected
    void friendRequestDeleted(String user) throws java.rmi.RemoteException; // This method is called when a friend request is deleted
}
