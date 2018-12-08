package bgu.spl.mics.application.passiveObjects;

public class CreditCard {
    private int number;
    private int amount;

    public CreditCard(int number, int amount) {
        this.number=number;
        this.amount=amount;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number=number;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount=amount;
    }
}
