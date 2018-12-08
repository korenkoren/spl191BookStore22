package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {
    private Customer customer;
    private String bookTitle;

    public BookOrderEvent(Customer customer, String bookTitle) {
        this.customer= customer;
        this.bookTitle = bookTitle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getBookTitle() {
        return bookTitle;
    }
}
