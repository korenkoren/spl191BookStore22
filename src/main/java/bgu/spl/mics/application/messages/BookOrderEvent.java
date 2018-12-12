package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {
    private Customer customer;
    private int tick;
    private String bookTitle;

    public BookOrderEvent(Customer customer,int Tick, String bookTitle) {
        this.customer= customer;
        this.bookTitle = bookTitle;
        this.tick = tick;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getTick() {
        return tick;
    }
}
