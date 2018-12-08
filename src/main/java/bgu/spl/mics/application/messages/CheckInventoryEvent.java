package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;

public class CheckInventoryEvent implements Event<BookInventoryInfo> {
    private String bookTitle;
    private Customer customer;

    public CheckInventoryEvent(String bookTitle, Customer customer) {
        this.bookTitle = bookTitle;
        this.customer = customer;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Customer getCustomer() {
        return customer;
    }

}
