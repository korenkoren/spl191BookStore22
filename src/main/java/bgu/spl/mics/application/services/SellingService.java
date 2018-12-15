package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.concurrent.CountDownLatch;

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
	private int currentTick;
	private CountDownLatch countDownLatch;

	public SellingService(CountDownLatch countDownLatch) {
		super("SellingService");
		m=MoneyRegister.getInstance();
		currentTick = -1;
		this.countDownLatch = countDownLatch;
	}

	@Override @SuppressWarnings("unchecked")
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast t) -> terminate());
		subscribeBroadcast(TickBroadcast.class, (TickBroadcast t) -> this.currentTick = t.getCurrentTick());

		subscribeEvent(BookOrderEvent.class, (BookOrderEvent b) -> {
			int processTick = currentTick;
			OrderReceipt orderReceipt = null;
			CheckInventoryEvent event = new CheckInventoryEvent(b.getBookTitle(), b.getCustomer());
			Future<BookInventoryInfo> future = sendEvent(event);
			if(future.get() != null) {
				BookInventoryInfo book = future.get();
				m.chargeCreditCard(b.getCustomer(), book.getPrice());
				orderReceipt = new OrderReceipt(this.getName(), b.getCustomer().getId(), b.getBookTitle(), book.getPrice(), currentTick, b.getTick(), processTick);
				m.file(orderReceipt);
			}
			complete(b, orderReceipt);
		});
		countDownLatch.countDown();
	}

}


