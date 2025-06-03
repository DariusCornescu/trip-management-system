package com.darius.project.repository.GenericRepos;
import com.darius.project.domain.Customer;

public interface CustomerRepository extends GenericRepository<Integer, Customer> {
    Customer findByCustomerName(String customerName);
}
