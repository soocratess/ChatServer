package socrates.bd;

import bd.BDAdmin;
import bd.BDAdminInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BDAdminTest {
    private BDAdminInterface bdAdmin;

    @BeforeEach
    public void setUp() {
        // BDAdmin instance for each test
        bdAdmin = new BDAdmin();
    }

    @AfterEach
    public void tearDown() {
        // Remove any friendships created during tests
        bdAdmin.removeFriend("user1", "user2");

        // Remove test users created in each test
        bdAdmin.deleteUser("testuser_login", "password_login");
        bdAdmin.deleteUser("testuser_search", "password_search");
        bdAdmin.deleteUser("testuser_delete", "password_delete");
        bdAdmin.deleteUser("user1", "password1");
        bdAdmin.deleteUser("user2", "password2");
    }

    @Test
    public void testLogin() {
        // Login test
        bdAdmin.register("testuser_login", "password_login");
        boolean result = bdAdmin.login("testuser_login", "password_login");
        assertTrue(result, "The user should be able to log in successfully.");
        System.out.println("Login successful.");
    }

    @Test
    public void testSearchUser() {
        // Test for searching an existing user
        bdAdmin.register("testuser_search", "password_search");
        boolean found = bdAdmin.existsUser("testuser_search");
        assertTrue(found, "The user should exist in the database.");
        System.out.println("User search successful.");
    }

    @Test
    public void testDeleteUser() {
        // Test for deleting a user
        bdAdmin.register("testuser_delete", "password_delete");
        boolean deleted = bdAdmin.deleteUser("testuser_delete", "password_delete");
        assertTrue(deleted, "The user should be deleted successfully.");
        System.out.println("User deletion successful.");
    }

    @Test
    public void testFriendship() {
        // Test friendship functionality
        bdAdmin.register("user1", "password1");
        bdAdmin.register("user2", "password2");

        // Send friend request
        boolean requestSent = bdAdmin.sendFriendRequest("user1", "user2");
        assertTrue(requestSent, "The friend request should be sent successfully.");

        // Accept friend request
        boolean friendshipAccepted = bdAdmin.acceptFriendRequest("user1", "user2");
        assertTrue(friendshipAccepted, "The friend request should be accepted successfully.");

        // Check friendships
        ArrayList<String> friends = bdAdmin.getFriends("user1");
        assertTrue(friends.contains("user2"), "user2 should be in user1's friends list.");
        System.out.println("Friendship functionality successfully tested.");
    }
}
