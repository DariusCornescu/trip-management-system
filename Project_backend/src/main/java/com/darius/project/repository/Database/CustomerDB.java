package com.darius.project.repository.Database;

import com.darius.project.domain.Customer;
import com.darius.project.repository.GenericRepos.CustomerRepository;
import org.slf4j.*;
import java.sql.*;
import java.util.*;

public class CustomerDB extends RepoDB<Integer, Customer> implements CustomerRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDB.class);
    public CustomerDB() {
        openConnection();
        LOGGER.info("CustomerDB initialized, connection opened");
    }

    @Override
    public Customer findByCustomerName(String customerName) {
        LOGGER.info("Finding customer by name: {}", customerName);

        Customer customer = null;
        try{
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers where customerName =? ")){
                statement.setString(1, customerName);
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()){
                    customer = new Customer(
                            resultSet.getInt("id"),
                            resultSet.getString("customerName"),
                            resultSet.getString("customerEmail"),
                            resultSet.getString("customerPhone")
                    );
                    LOGGER.info("Found customer with name: {}, ID: {}", customerName, customer.getId());
                } else {
                    LOGGER.info("No customer found with name: {}", customerName);
                }
            }
        } catch(Exception e){
            LOGGER.error("Error finding customer by name {}, error: {}", customerName, e.getMessage(), e);
        }
        return customer;
    }

    @Override
    public Customer findById(Integer id) {
        LOGGER.info("Finding customer by ID: {}", id);

        Customer customer = null;
        try{
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE id = ?")){
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()){
                    customer = new Customer(
                            resultSet.getInt("id"),
                            resultSet.getString("customerName"),
                            resultSet.getString("customerEmail"),
                            resultSet.getString("customerPhone")
                    );
                    LOGGER.info("Found customer with ID: {}, name: {}", id, customer.getCustomerName());
                } else {
                    LOGGER.info("No customer found with ID: {}", id);
                }
            }
        } catch(Exception e){
            LOGGER.error("Error finding customer by ID {}, error: {}", id, e.getMessage(), e);
        }
        return customer;
    }

    @Override
    public Iterator<Customer> findAll(){
        LOGGER.info("Retrieving all customers");

        ArrayList<Customer> customers = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement("SELECT * from customers")){
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                customers.add(new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("customerName"),
                        resultSet.getString("customerEmail"),
                        resultSet.getString("customerPhone")
                ));
            }
            LOGGER.info("Retrieved {} customers total", customers.size());
        } catch(Exception e){
            LOGGER.error("Error retrieving all customers, error: {}", e.getMessage(), e);
        }
        return customers.iterator();
    }

    public List<Customer> findAllByNameContaining(String partialName) {
        LOGGER.info("Finding customers with name containing: '{}'", partialName);

        List<Customer> customers = new ArrayList<>();
        try {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE customerName LIKE ?")) {
                statement.setString(1, "%" + partialName + "%");
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    customers.add(new Customer(
                            resultSet.getInt("id"),
                            resultSet.getString("customerName"),
                            resultSet.getString("customerEmail"),
                            resultSet.getString("customerPhone")
                    ));
                }
                LOGGER.info("Found {} customers with name containing: '{}'", customers.size(), partialName);
            }
        } catch (Exception e) {
            LOGGER.error("Error finding customers by partial name: {}, error: {}", partialName, e.getMessage(), e);
        }
        return customers;
    }

    @Override
    public void save(Integer id, Customer customer) {
        LOGGER.info("Saving new customer with ID: {}, name: {}", id, customer.getCustomerName());

        super.save(id, customer);
        try{
            try(PreparedStatement statement = connection.prepareStatement("INSERT INTO customers VALUES(?,?,?,?)")){
                statement.setInt(1, id);
                statement.setString(2, customer.getCustomerName());
                statement.setString(3, customer.getCustomerEmail());
                statement.setString(4, customer.getCustomerPhone());
                int rowsAffected = statement.executeUpdate();
                LOGGER.info("Successfully saved customer with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch (Exception e){
            LOGGER.error("Error saving customer {}, error: {}", customer, e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        LOGGER.info("Deleting customer with ID: {}", id);

        super.delete(id);
        try{
            try(PreparedStatement statement = connection.prepareStatement("DELETE FROM customers WHERE id=?")){
                statement.setInt(1, id);
                int rowsAffected = statement.executeUpdate();
                LOGGER.info("Successfully deleted customer with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch(Exception e){
            LOGGER.error("Error deleting customer with ID {}, error: {}", id, e.getMessage(), e);
        }
    }

    @Override
    public void update(Integer id, Customer customer) {
        LOGGER.info("Updating customer with ID: {}, name: {}", id, customer.getCustomerName());

        super.update(id, customer);
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement("UPDATE customers SET customerName = ?, customerEmail = ?, customerPhone = ? WHERE id = ?")) {
                statement.setString(1, customer.getCustomerName());
                statement.setString(2, customer.getCustomerEmail());
                statement.setString(3, customer.getCustomerPhone());
                statement.setInt(4, id);
                int rowsAffected = statement.executeUpdate();

                connection.commit();
                connection.setAutoCommit(true);
                LOGGER.info("Successfully updated customer with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch (Exception e) {
            LOGGER.error("Error updating customer {}, error: {}", customer, e.getMessage(), e);
            try {
                LOGGER.info("Attempting to rollback transaction");
                connection.rollback();
                connection.setAutoCommit(true);
                LOGGER.info("Transaction rollback successful");
            } catch (Exception rollbackException) {
                LOGGER.error("Error during transaction rollback: {}", rollbackException.getMessage(), rollbackException);
            }
        }
    }
}