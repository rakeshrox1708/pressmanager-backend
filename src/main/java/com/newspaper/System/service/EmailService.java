package com.newspaper.System.service;

import com.newspaper.System.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendInvoiceEmail(String email, Invoice invoice) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("New Invoice Generated");
        System.out.println("Attempting to send email to in sendInvoiceEmail: " + email);
        message.setText(
                "Dear Customer,\n\n" +
                        "Invoice for " + invoice.getBillingMonth() + "/" +
                        invoice.getBillingYear() + " is generated.\n" +
                        "Amount: ₹" + invoice.getTotalAmount() + "\n" +
                        "Due Date: " + invoice.getDueDate() + "\n\n" +
                        "Please login and pay before due date.\n\n" +
                        "Thank you."
        );

        try {
            mailSender.send(message);
            System.out.println("Email sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPaymentConfirmation(String email, Invoice invoice) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Payment Successful");

        System.out.println("Attempting to send email to in sendPaymentConfirmation: " + email);

        message.setText(
                "Dear Customer,\n\n" +
                        "Payment received for invoice " +
                        invoice.getBillingMonth() + "/" +
                        invoice.getBillingYear() + "\n" +
                        "Amount Paid: ₹" + invoice.getTotalAmount() + "\n\n" +
                        "Thank you."
        );

        try {
            mailSender.send(message);
            System.out.println("Email sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}