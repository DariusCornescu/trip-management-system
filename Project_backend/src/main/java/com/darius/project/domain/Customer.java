package com.darius.project.domain;
import java.util.Objects;

public class Customer implements Identifiable<Integer> {
    private Integer id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    public Customer(){}

    public Customer(Integer id, String customerName, String customerEmail, String customerPhone) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    @Override
    public Integer getId() { return id; }

    @Override
    public void setId(Integer id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerPhone, customer.customerPhone) && Objects.equals(id, customer.id) && Objects.equals(customerName, customer.customerName) && Objects.equals(customerEmail, customer.customerEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerName, customerEmail, customerPhone);
    }

    @Override
    public String toString() {
        return "Customers{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerPhone=" + customerPhone +
                '}';
    }
}
