package com.newspaper.System.scheduler;

import com.newspaper.System.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BillingScheduler {

    @Autowired
    private InvoiceService invoiceService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void generateInvoices() {
        invoiceService.generateInvoicesForPreviousMonth();
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void checkOverdue() {
        invoiceService.markOverdueInvoices();
    }
}