package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckInventoryEvent;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	private MoneyRegister m;

	public SellingService() {
		super("SellingService");
		m=MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeEvent(BookOrderEvent.class, (BookOrderEvent b) -> {
			CheckInventoryEvent event = new CheckInventoryEvent(b.getBookTitle(), b.getCustomer());
			Future<BookInventoryInfo> future = sendEvent(event);
			future.get();


		});
	}

}
