package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.BookStoreRunner;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable{
	private static MoneyRegister instance=new MoneyRegister();
	private List<OrderReceipt> orderReceipts;

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return instance;
	}

	private MoneyRegister() {
		orderReceipts = new LinkedList<>();
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		orderReceipts.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		int sum = 0;
		for(OrderReceipt o : orderReceipts)
			sum = sum + o.getPrice();
		return sum;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.chargeCreditCard(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		List<OrderReceipt> orderReceipts = new LinkedList<>();
		synchronized (orderReceipts) {
			orderReceipts.addAll(orderReceipts);
		}
		BookStoreRunner.createSerializedObject((Serializable)orderReceipts,filename);
	}

	public List<OrderReceipt> getOrderReceipts() {
		return orderReceipts;
	}
}
