package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckInventoryEvent;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory;
	public InventoryService(Inventory inventory) {
		super("Change_This_Name");
		this.inventory = inventory;
	}

	@Override
	protected void initialize() {
		subscribeEvent(CheckInventoryEvent.class, (CheckInventoryEvent c) -> {
			BookInventoryInfo book = null;
			int price = inventory.checkAvailabiltyAndGetPrice(c.getBookTitle());
			if(price != -1 && c.getCustomer().getAvailableCreditAmount() >= price) {
				OrderResult orderResult = inventory.take(c.getBookTitle());
				if(orderResult == OrderResult.SUCCESSFULLY_TAKEN) {
					book = new BookInventoryInfo(c.getBookTitle(), 1, price);
				}
			}
			MessageBusImpl.getInstance().complete(c, book);
		});
		
	}

}
