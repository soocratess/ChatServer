package socrates.bd;

import java.util.ArrayList;

public interface BDAdminInterface {
    boolean login(String username, String password);
    boolean register(String username, String password, String remoteObjectAddress);
    boolean deleteUser(String username, String password);
    boolean existsUser(String username);

    ArrayList<String> getFriends(String user);
    boolean removeFriend(String origin, String destination);

    ArrayList<String> getPendingFriendRequests(String user);
    boolean sendFriendRequest(String origin, String destination);
    boolean acceptFriendRequest(String origin, String destination);
    boolean rejectFriendRequest(String origin, String destination);
    boolean changePassword(String username, String oldPassword, String newPassword);
}
