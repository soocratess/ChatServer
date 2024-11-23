package bd;

import java.sql.*;
import java.util.ArrayList;

public class BDAdmin implements BDAdminInterface {
    // Change the connection URL for MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/ChatServer"; // localhost if the Java app is on the same machine as Docker
    private static final String USER = "user"; // User configured in docker-compose.yml
    private static final String PASSWORD = "password"; // Password configured in docker-compose.yml
    private Connection connection;

    // Class constructor, called automatically when an object is instantiated
    public BDAdmin() {
        connect(); // Calls the connect method when creating an instance of the class
    }

    // Method to establish a database connection
    public void connect() {
        try {
            // Establish connection to the database with user and password
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }

    // Method to close the database connection
    public void disconnect() {
        try {
            // Close the database connection if it is not already closed
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing the connection: " + e.getMessage());
        }
    }

    // Method for user login
    @Override
    public boolean login(String username, String password) {
        try {
            String sql = "SELECT * FROM USER WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    System.out.println("Query executed without errors");
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    // Method to register a new user
    @Override
    public boolean register(String username, String password) {
        try {
            String sql = "INSERT INTO USER (username, password) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, password);
                int rowsAffected = statement.executeUpdate();
                System.out.println("Rows affected in INSERT " + rowsAffected);
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    // Method to delete a user
    @Override
    public boolean deleteUser(String username, String password) {
        try {
            // Prepare the SQL statement to delete the user with password verification
            String sql = "DELETE FROM USER WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameter values
                statement.setString(1, username);
                statement.setString(2, password);

                // Execute the query and get the number of rows affected
                int rowsAffected = statement.executeUpdate();

                System.out.println("Rows affected in DELETE " + rowsAffected);
                // Return true if at least one row was deleted, indicating success
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // Method to search for a user in the database
    @Override
    public boolean existsUser(String username) {
        try {
            String sql = "SELECT * FROM USER WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    System.out.println("Query executed without errors");
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching for user: " + e.getMessage());
            return false;
        }
    }

    // Method to get a user's friend list
    @Override
    public ArrayList<String> getFriends(String user) {
        try {
            String sql = "SELECT * FROM FRIENDSHIP WHERE pending = 0 AND (requesting_user = ? OR receiving_user = ?)";
            ArrayList<String> friends = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user);
                statement.setString(2, user);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String u1 = resultSet.getString("requesting_user");
                        String u2 = resultSet.getString("receiving_user");
                        System.out.println("requesting user: " + u1);
                        System.out.println("receiving user: " + u2);
                        System.out.println("User: " + user);
                        if (!u1.equalsIgnoreCase(user))
                            friends.add(u1);
                        else friends.add(u2);
                    }
                    System.out.println("Query executed without errors");
                    return friends;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching for user: " + e.getMessage());
            return null;
        }
    }

    // Method to get a user's pending friend requests
    @Override
    public ArrayList<String> getPendingFriendRequests(String user) {
        String sql = "SELECT requesting_user FROM FRIENDSHIP WHERE pending = 1 AND receiving_user = ?";
        ArrayList<String> requests = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(resultSet.getString("requesting_user"));
                }
                System.out.println("Query executed without errors");
                return requests;
            }
        } catch (SQLException e) {
            System.out.println("Error searching for user: " + e.getMessage());
            return null;
        }
    }

    // Method to send a friend request
    @Override
    public boolean sendFriendRequest(String origin, String destination) {
        String sql = "INSERT INTO FRIENDSHIP (requesting_user, receiving_user, pending) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origin);
            statement.setString(2, destination);
            statement.setInt(3, 1);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected in INSERT " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error sending friend request: " + e.getMessage());
            return false;
        }
    }

    // Method to accept a friend request
    @Override
    public boolean acceptFriendRequest(String origin, String destination) {
        String sql = "UPDATE FRIENDSHIP SET pending = 0 WHERE requesting_user = ? AND receiving_user = ? AND pending = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origin);
            statement.setString(2, destination);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected in UPDATE " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error accepting friend request: " + e.getMessage());
            return false;
        }
    }

    // Method to remove a friend
    @Override
    public boolean removeFriend(String origin, String destination) {
        String sql = "DELETE FROM FRIENDSHIP WHERE ((requesting_user = ? AND receiving_user = ?) OR (requesting_user = ? AND receiving_user = ?)) AND pending=0";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origin);
            statement.setString(2, destination);
            statement.setString(3, destination);
            statement.setString(4, origin);

            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected in DELETE " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error removing friend: " + e.getMessage());
            return false;
        }
    }

    // Method to reject a friend request
    @Override
    public boolean rejectFriendRequest(String origin, String destination) {
        String sql = "DELETE FROM FRIENDSHIP WHERE requesting_user = ? AND receiving_user = ? AND pending = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origin);
            statement.setString(2, destination);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected in DELETE " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error rejecting friend request: " + e.getMessage());
            return false;
        }
    }

    // Method to change a user's password
    @Override
    public boolean changePassword(String username, String oldPasswd, String newPasswd) {
        String sql = "UPDATE USER SET password = ? WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newPasswd);
            statement.setString(2, username);
            statement.setString(3, oldPasswd);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected in UPDATE " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error changing the password for " + username + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateRMIAddress(String username, String password, String remoteObjectAddress) {
        String sql = "UPDATE USER SET remote_object_address = ? WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, remoteObjectAddress);
            statement.setString(2, username);
            statement.setString(3, password);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected in UPDATE " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating the RMI address for " + username + ": " + e.getMessage());
            return false;
        }
    }


}