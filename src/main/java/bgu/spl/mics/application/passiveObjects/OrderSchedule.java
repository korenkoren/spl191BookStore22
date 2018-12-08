package bgu.spl.mics.application.passiveObjects;

public class OrderSchedule {
    private int tick;
    private String bookTitle;

    public OrderSchedule(int tick, String bookTitle) {
        this.tick=tick;
        this.bookTitle=bookTitle;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick=tick;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle=bookTitle;
    }
}
