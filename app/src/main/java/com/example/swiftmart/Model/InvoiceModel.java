package com.example.swiftmart.Model;

import java.util.List;

public class InvoiceModel {
    private String clientName;
    private String companyName;
    private String billingAddress;
    private String phone;
    private String email;
    private String invoiceNumber;
    private String invoiceDate;
    private List<Service> services;
    private double taxRate;
    private String paymentMethod;
    private String dueDate;
    private String bankAccount;
    private String additionalNotes;
    private String invoiceId;
    private String orderId;
    private String orderDate;

    public InvoiceModel(String clientName,
                        String companyName,
                        String billingAddress,
                        String phone,
                        String email,
                        String invoiceNumber,
                        String invoiceDate,
                        double taxRate,
                        String paymentMethod,
                        String dueDate,
                        String bankAccount,
                        String additionalNotes,
                        String invoiceId,
                        String orderId,
                        String orderDate,
                        List<Service> services) {
        this.clientName = clientName;
        this.companyName = companyName;
        this.billingAddress = billingAddress;
        this.phone = phone;
        this.email = email;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.taxRate = taxRate;
        this.paymentMethod = paymentMethod;
        this.dueDate = dueDate;
        this.bankAccount = bankAccount;
        this.additionalNotes = additionalNotes;
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.services = services; // Initialize services list here
    }

    // Getter methods
    public String getClientName() {
        return clientName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public List<Service> getServices() {
        return services;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public static class Service {
        private String description;
        private int quantity;
        private double rate;

        public Service(String description, int quantity, double rate) {
            this.description = description;
            this.quantity = quantity;
            this.rate = rate;
        }

        public String getDescription() {
            return description;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getRate() {
            return rate;
        }
    }
}
