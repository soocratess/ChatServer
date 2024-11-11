package socrates.client;

import socrates.user.User;

/**
 * This interface is used to define the methods that the server will call on the client
 * to notify it of events related to the user's friends.
 */
public interface CallbackClientInterface extends java.rmi.Remote {
    void friendConnected(User user) throws java.rmi.RemoteException; // This method is called when a friend connects
    void friendDisconnected(User user) throws java.rmi.RemoteException; // This method is called when a friend disconnects
    void friendAdded(User user) throws java.rmi.RemoteException; // This method is called when a friend is added
    void friendDeleted(User user) throws java.rmi.RemoteException; // This method is called when a friend is deleted

    void friendRequest(User user) throws java.rmi.RemoteException; // This method is called when a friend request is received
    void friendRequestAccepted(User user) throws java.rmi.RemoteException; // This method is called when a friend request is accepted
    void friendRequestRejected(User user) throws java.rmi.RemoteException; // This method is called when a friend request is rejected
    void friendRequestDeleted(User user) throws java.rmi.RemoteException; // This method is called when a friend request is deleted
}
