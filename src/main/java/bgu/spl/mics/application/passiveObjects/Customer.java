package bgu.spl.mics.application.passiveObjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
	@SerializedName("orderSchedule")
	@Expose
	private List<OrderSchedule> ordersList;
	private String name;
	private int id;
	private String address;
	private int distance;
	private CreditCard creditCard;
	List<OrderReceipt> orderReceipts;

	public Customer(List<OrderSchedule> ordersList, String name, int id, String address, int distance, CreditCard creditCard) {
		this.ordersList=ordersList;
		this.name=name;
		this.id=id;
		this.address=address;
		this.distance=distance;
		this.creditCard=creditCard;
		orderReceipts = new LinkedList<>();
	}

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return orderReceipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return creditCard.getAmount();
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditCard.getNumber();
	}

	public List<OrderSchedule> getOrdersList() {
		return ordersList;
	}

	public void chargeCreditCard(int amount) {
		creditCard.setAmount(creditCard.getAmount() - amount);
	}
}
