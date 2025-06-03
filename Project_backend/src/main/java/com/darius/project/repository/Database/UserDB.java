package com.darius.project.repository.Database;

import com.darius.project.domain.User;
import com.darius.project.repository.GenericRepos.UserRepository;
import org.slf4j.*;
import java.sql.*;
import java.util.*;

public class UserDB extends RepoDB<Integer, User> implements UserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDB.class);
    public UserDB(){
        openConnection();
        LOGGER.info("UserDB initialized, connection opened");
    }

    @Override
    public User findByUsername(String username) {
        LOGGER.info("Finding user by username: {}", username);

        User user = null;
        try{
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM users where username =? ")){
                statement.setString(1, username);
                var resultSet = statement.executeQuery();
                if(resultSet.next()){
                    user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password")
                    );
                    LOGGER.info("Found user with username: {}, ID: {}", username, user.getId());
                } else {
                    LOGGER.info("No user found with username: {}", username);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error finding user by username: {}, Error: {}", username, e.getMessage(), e);
        }
        return user;
    }

    @Override
    public User findById(Integer id) {
        LOGGER.info("Finding user by ID: {}", id);

        User user = null;
        try{
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM users where id =? ")){
                statement.setInt(1, id);
                var resultSet = statement.executeQuery();
                if(resultSet.next()){
                    user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password")
                    );
                    LOGGER.info("Found user with ID: {}, username: {}", id, user.getUsername());
                } else {
                    LOGGER.info("No user found with ID: {}", id);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error finding user by ID: {}, Error: {}", id, e.getMessage(), e);
        }
        return user;
    }

    @Override
    public void save(Integer id, User user) {
        LOGGER.info("Saving new user with ID: {}, username: {}", id, user.getUsername());

        super.save(id, user);
        try{
            try(PreparedStatement statement = connection.prepareStatement("INSERT INTO Users VALUES(?,?,?)")){
                statement.setInt(1, id);
                statement.setString(2, user.getUsername());
                statement.setString(3, user.getPassword());
                int rowsAffected = statement.executeUpdate();
                LOGGER.info("Successfully saved user with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch (Exception e){
            LOGGER.error("Error saving user with ID: {}, username: {}, Error: {}",
                    id, user.getUsername(), e.getMessage(), e);
        }
    }

    @Override
    public void update(Integer id, User user) {
        LOGGER.info("Updating user with ID: {}, username: {}", id, user.getUsername());

        super.update(id, user);
        try{
            try(PreparedStatement statement = connection.prepareStatement("UPDATE Users SET username=?, password=? WHERE id=?")){
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                statement.setInt(3, id);
                int rowsAffected = statement.executeUpdate();
                LOGGER.info("Successfully updated user with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch (Exception e){
            LOGGER.error("Error updating user with ID: {}, username: {}, Error: {}",
                    id, user.getUsername(), e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        LOGGER.info("Deleting user with ID: {}", id);

        super.delete(id);
        try{
            try(PreparedStatement statement = connection.prepareStatement("DELETE FROM Users WHERE id=?")){
                statement.setInt(1, id);
                int rowsAffected = statement.executeUpdate();
                LOGGER.info("Successfully deleted user with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch (Exception e){
            LOGGER.error("Error deleting user with ID: {}, Error: {}", id, e.getMessage(), e);
        }
    }

    @Override
    public Iterator<User> findAll(){
        LOGGER.info("Retrieving all users");

        ArrayList<User> users = new ArrayList<>();
        try {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users")) {
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    users.add(new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password")
                    ));
                }
                LOGGER.info("Retrieved {} users total", users.size());
            }
        } catch (Exception e){
            LOGGER.error("Error retrieving all users, Error: {}", e.getMessage(), e);
        }
        return users.iterator();
    }

    public List<User> findAllByUsernameContaining(String partialUsername) {
        LOGGER.info("Finding users with username containing: '{}'", partialUsername);

        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + partialUsername + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                ));
            }
            LOGGER.info("Found {} users with username containing: '{}'", users.size(), partialUsername);
        } catch (Exception e) {
            LOGGER.error("Error finding users by partial username: {}, Error: {}",
                    partialUsername, e.getMessage(), e);
        }
        return users;
    }
}