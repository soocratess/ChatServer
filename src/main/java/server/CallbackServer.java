package server;

import cliente_servidor.CallbackServerInterface;
import bd.BDAdmin;
import bd.BDAdminInterface;
import cliente_servidor.ICallbackCliente;
import cliente_servidor.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

// TODO review if notification callbacks on the client are correctly implemented after each funcition
// TODO review if security is correctly implemented in each function
// TODO test each function on test class
public class CallbackServer extends UnicastRemoteObject implements CallbackServerInterface {
    private HashMap<String, User> connectedUsers; // Connected users
    private BDAdminInterface bd; // Database control

    /// Constructor
    public CallbackServer() throws RemoteException {
        super();
        connectedUsers = new HashMap<String, User>();
        bd = new BDAdmin();
    }

    public HashMap<String, User> getConnectedUsers() {
        return connectedUsers;
    }

    public BDAdminInterface getBd() {
        return bd;
    }

    public void setBd(BDAdminInterface bd) {
        this.bd = bd;
    }

    @Override
    public User login(ICallbackCliente client, String name, String password) throws RemoteException {

        User user = null;

        // Verifies the client is not null
        if (client == null) {
            System.out.println("Unable to log in: client is null");
        } else if (connectedUsers.containsKey(name)) { // Verifies the user is not already connected
            System.out.println("Unable to log in: user is already connected");
        } else if (bd.login(name, password)) { // Logs in the user
            System.out.println("Successfully loged in user " + name);

            // Obtains the list of friends and connected friends
            ArrayList<String> friends, connectedFriends;
            friends = obtainFriendList(name, password);
            connectedFriends = new ArrayList<>();

            // Obtains the list of connected friends
            for (String friend : friends) {
                if (connectedUsers.containsKey(friend)) connectedFriends.add(friend);
            }

            // Obtains th elist of friend requests
            ArrayList<String> friendRequests = obtainFriendRequests(name, password);

            // Creates the user
            user = new User(client, name, friends, connectedFriends, friendRequests);

            // Connected user
            connectedUsers.put(name, user);
            notifyConnectionToFriends(name);
            System.out.println("User " + name + " logged in");
        } else {
            System.out.println("Unable to log in: invalid credentials");
        }

        return user;
    }

    @Override
    public boolean logOut(String name, String password) throws RemoteException {
        // Verifies the credentials are correct
        if (!bd.login(name, password)) {
            System.out.println("Unable to log out: invalid credentials");
            return false;
        }
        // Verifies the user is connected
        if (!connectedUsers.containsKey(name)) {
            System.out.println("Unable to log out: user is not connected");
            return false;
        }

        // Notifies the user's friends
        notifyDisconnectionToFriends(name);

        // Removes the user from the connected users
        connectedUsers.remove(name);
        System.out.println("User " + name + " logged out");
        return true;
    }

    @Override
    public User register(ICallbackCliente client, String name, String password) throws RemoteException {

        User user = null;

        if (client == null) {
            System.out.println("Unable to register user " + name);
            return user;
        } else {
            if (bd.register(name, password)) {
                user = login(client, name, password);
                bd.updateRMIAddress(name, password, user.getRMIAddress());
                System.out.println("Successfully registered user " + name);
            }
        }

        return user;
    }

    @Override
    public boolean deleteAccount(String name, String password) throws RemoteException {
        // deletes an account
        if (!bd.login(name, password)) {
            System.out.println("Unable to delete account: invalid credentials");
            return false;
        } else if (bd.deleteUser(name, password)) {
            notifyDisconnectionToFriends(name);
            connectedUsers.remove(name);
            System.out.println("User " + name + " deleted");
        } else {
            System.out.println("Unable to delete account: error deleting user");
            return false;
        }
        return true;
    }

    @Override
    public boolean sendFriendRequest(String name, String friendName, String password) throws RemoteException {
        // Verifies the credentials are correct
        if (!bd.login(name, password)) {
            System.out.println("Unable to send friend request: invalid credentials");
            return false;
        } else if (!bd.existsUser(friendName)) {
            System.out.println("Unable to send friend request: friend does not exist");
            return false;
        } else if (bd.getFriends(name).contains(friendName)) {
            System.out.println("Unable to send friend request: users are already friends");
            return false;
        } else if (bd.getPendingFriendRequests(name).contains(friendName) ||
                bd.getPendingFriendRequests(friendName).contains(name)) {
            System.out.println("Unable to send friend request: friend request already exists");
            return false;
        } else {
            bd.sendFriendRequest(name, friendName);
            if (connectedUsers.get(friendName) != null)
                connectedUsers.get(friendName).getClient().solicitudAmistadNueva(name);
            System.out.println("Friend request sent from " + name + " to " + friendName);
            return true;
        }
    }

    @Override
    public boolean removeFriend(String name, String friendName, String password) throws RemoteException {
        // Verifies the credentials are correct
        if (!bd.login(name, password)) {
            System.out.println("Unable to remove friend: invalid credentials");
            return false;
        } else if (!bd.getFriends(name).contains(friendName)) {
            System.out.println("Unable to remove friend: users are not friends");
            return false;
        } else { // Removes the friend
            bd.removeFriend(name, friendName); // Removes the friend from the database
            if (connectedUsers.get(friendName) != null)
                connectedUsers.get(friendName).getClient().amigoEliminado(name); // Notifies the user (friend)
            if (connectedUsers.get(name) != null)
                connectedUsers.get(name).getClient().amigoEliminado(friendName);
            System.out.println("Friend removed from " + name + " friends list");
            return true;
        }
    }

    @Override
    public boolean acceptFriendRequest(String name, String friendName, String password) throws RemoteException {
        // Verifies the credentials are correct
        if (!bd.login(name, password)) {
            System.out.println("Unable to accept friend request: invalid credentials");
            return false;
        } else if (!bd.getPendingFriendRequests(name).contains(friendName)) {
            System.out.println("Unable to accept friend request: friend request does not exist");
            return false;
        } else { // Accepts the friend request
            if (bd.acceptFriendRequest(friendName, name)) { // Accepts the friend request in the database
                if (connectedUsers.get(friendName) != null)
                    connectedUsers.get(friendName).getClient().amigoNuevo(name); // Notifies the user (friend)
                if (connectedUsers.get(name) != null)
                    connectedUsers.get(name).getClient().amigoNuevo(friendName); // Notifies the user (name)
                System.out.println("Friend request accepted from " + name + " to " + friendName);
                return true;
            } else {
                System.out.println("Unable to accept friend request: error accepting friend request");
                return false;
            }
        }
    }

    @Override
    public boolean rejectFriendRequest(String name, String friendName, String password) throws RemoteException {
        // Verifies the credentials are correct
        if (!bd.login(name, password)) {
            System.out.println("Unable to reject friend request: invalid credentials");
            return false;
        } else if (!bd.getPendingFriendRequests(name).contains(friendName)) {
            System.out.println("Unable to reject friend request: friend request does not exist");
            return false;
        } else { // Rejects the friend request
            if (bd.rejectFriendRequest(friendName, name)) { // Rejects the friend request in the database
                if (connectedUsers.get(friendName) != null)
                    connectedUsers.get(friendName).getClient().solicitudAmistadRechazada(name); // Notifies the user (friend)
                if (connectedUsers.get(name) != null)
                    connectedUsers.get(name).getClient().solicitudAmistadRechazada(friendName); // Notifies the user (name)
                System.out.println("Friend request rejected from " + name + " to " + friendName);
                return true;
            } else {
                System.out.println("Unable to reject friend request: error rejecting friend request");
                return false;
            }
        }
    }

    @Override
    public ArrayList<String> obtainFriendRequests(String name, String password) throws RemoteException {
        // Verifies the credentials are correct
        if (!bd.login(name, password)) {
            System.out.println("Unable to obtain friend requests: invalid credentials");
            return new ArrayList<String>();
        } else {
            ArrayList<String> friendRequests = bd.getPendingFriendRequests(name);
            System.out.println("Friend requests obtained for user " + name);
            return friendRequests;
        }
    }

    @Override
    public ArrayList<String> obtainFriendList(String name, String password) throws RemoteException {
        // Verifies the credentials are correct
        if (!bd.login(name, password)) {
            System.out.println("Unable to obtain friend list: invalid credentials");
            return new ArrayList<String>();
        } else {
            ArrayList<String> friends = bd.getFriends(name);
            System.out.println("Friend list obtained for user " + name);
            return friends;
        }
    }

    @Override
    public ArrayList<String> obtainConnectedFriendList(String name, String password) throws RemoteException {
        // Verifies the credentials are correct
        if (!bd.login(name, password)) {
            System.out.println("Unable to obtain connected friend list: invalid credentials");
            return new ArrayList<String>();
        } else { // Obtains the connected friend list
            ArrayList<String> connectedFriends = new ArrayList<String>();
            for (String friend : bd.getFriends(name)) { // Iterates over the user's friends
                if (connectedUsers.containsKey(friend)) connectedFriends.add(friend); // Adds the connected friends
            }

            System.out.println("Connected friend list obtained for user " + name);
            return connectedFriends; // Returns the connected friend list
        }
    }

    @Override
    public boolean changePassword(String name, String password, String newPassword) throws RemoteException {
        // Verifies the credentials are correct
        if (!bd.login(name, password)) {
            System.out.println("Unable to change password: invalid credentials");
            return false;
        } else {
            if (bd.changePassword(name, password, newPassword)) {
                System.out.println("Password changed for user " + name);
                return true;
            } else {
                System.out.println("Unable to change password: error changing password");
                return false;
            }
        }
    }

    @Override
    public String startChat(String name, String password, String friendName) throws RemoteException {
        // Verifies the credentials are correct
        if (!bd.login(name, password)) {
            System.out.println("Unable to start chat: invalid credentials");
            return null;
        } else if (!bd.getFriends(name).contains(friendName)) {
            System.out.println("Unable to start chat: users are not friends");
            return null;
        } else {
            System.out.println("Chat started between " + name + " and " + friendName);
            return connectedUsers.get(friendName).getRMIAddress();
        }
    }

    private synchronized void notifyDisconnectionToFriends(String user) throws RemoteException {
        if (connectedUsers.get(user) == null || connectedUsers.get(user).getFriends() == null) return;
        // Notifies the user's friends
        for (String friend : bd.getFriends(user)) {
            if (connectedUsers.containsKey(friend)) {
                connectedUsers.get(friend).getClient().amigoDesconectado(user);
            }
        }
    }

    @Override
    public boolean updateRMIAddress(String name, String password, ICallbackCliente client) throws RemoteException {
        // Verifies the credentials are correct
        if (!bd.login(name, password)) {
            System.out.println("Unable to update RMI address: invalid credentials");
            return false;
        } else {
            connectedUsers.get(name).setClient(client);
            System.out.println("RMI address updated for user " + name);
            return true;
        }
    }

    private synchronized void notifyConnectionToFriends(String user) throws RemoteException {
        if (connectedUsers.get(user) == null || connectedUsers.get(user).getFriends() == null ||
                connectedUsers.get(user).getFriendsConnected() == null || connectedUsers.get(user).getFriendsConnected().isEmpty())
            return;
        // Notifies the user's friends
        for (String friend : connectedUsers.get(user).getFriends()) {
            if (connectedUsers.containsKey(friend)) {
                connectedUsers.get(friend).getClient().amigoConectado(user);
            }
        }
    }
}
