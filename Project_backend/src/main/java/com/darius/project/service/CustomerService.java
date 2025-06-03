package com.darius.project.service;

import com.darius.project.domain.Customer;
import com.darius.project.repository.GenericRepos.CustomerRepository;
import org.slf4j.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) { this.customerRepository = customerRepository; }

    public Customer findByCustomerName(String customerName) {
        LOGGER.info("Service: Finding customer by name: {}", customerName);

        Customer customer = customerRepository.findByCustomerName(customerName);
        if (customer != null) {
            LOGGER.info("Service: Found customer with name: {}, ID: {}", customerName, customer.getId());
        } else {
            LOGGER.info("Service: No customer found with name: {}", customerName);
        }
        return customer;
    }

    public Customer findById(Integer id) {
        LOGGER.info("Service: Finding customer by ID: {}", id);

        Customer customer = customerRepository.findById(id);
        if (customer != null) {
            LOGGER.info("Service: Found customer with ID: {}, name: {}", id, customer.getCustomerName());
        } else {
            LOGGER.info("Service: No customer found with ID: {}", id);
        }
        return customer;
    }

    public List<Customer> findAll() {
        LOGGER.info("Service: Retrieving all customers");

        List<Customer> customers = new ArrayList<>();
        customerRepository.findAll().forEachRemaining(customers::add);
        LOGGER.info("Service: Retrieved {} customers total", customers.size());
        return customers;
    }

    public void save(Customer customer) {
        if (customer == null) {
            LOGGER.error("Service: Cannot save null customer");
            throw new IllegalArgumentException("Customer cannot be null");
        }

        LOGGER.info("Service: Saving customer with ID: {}, name: {}", customer.getId(), customer.getCustomerName());
        try {
            customerRepository.save(customer.getId(), customer);
            LOGGER.info("Service: Successfully saved customer with ID: {}", customer.getId());
        } catch (Exception e) {
            LOGGER.error("Service: Error saving customer with ID: {}, error: {}", customer.getId(), e.getMessage(), e);
            throw e;
        }
    }

    public void update(Customer customer) {
        if (customer == null) {
            LOGGER.error("Service: Cannot update null customer");
            throw new IllegalArgumentException("Customer cannot be null");
        }

        LOGGER.info("Service: Updating customer with ID: {}, name: {}", customer.getId(), customer.getCustomerName());
        try {
            Customer existingCustomer = findById(customer.getId());
            if (existingCustomer == null) {
                LOGGER.error("Service: Cannot update - customer with ID {} not found", customer.getId());
                throw new IllegalArgumentException("Customer not found with ID: " + customer.getId());
            }

            customerRepository.update(customer.getId(), customer);
            LOGGER.info("Service: Successfully updated customer with ID: {}", customer.getId());
        } catch (Exception e) {
            LOGGER.error("Service: Error updating customer with ID: {}, error: {}", customer.getId(), e.getMessage(), e);
            throw e;
        }
    }

    public void delete(Customer customer) {
        if (customer == null) {
            LOGGER.error("Service: Cannot delete null customer");
            throw new IllegalArgumentException("Customer cannot be null");
        }

        LOGGER.info("Service: Deleting customer with ID: {}, name: {}", customer.getId(), customer.getCustomerName());
        try {
            customerRepository.delete(customer.getId());
            LOGGER.info("Service: Successfully deleted customer with ID: {}", customer.getId());
        } catch (Exception e) {
            LOGGER.error("Service: Error deleting customer with ID: {}, error: {}", customer.getId(), e.getMessage(), e);
            throw e;
        }
    }
}