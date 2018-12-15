package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.concurrent.CountDownLatch;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {

	private Customer customer;
	private CountDownLatch countDownLatch;
	public APIService(Customer customer, CountDownLatch countDownLatch) {
		super("APIService");
		this.customer = customer;
		this.countDownLatch = countDownLatch;
	}

	@Override @SuppressWarnings("unchecked")
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast t) -> terminate());
		subscribeBroadcast(TickBroadcast.class, (TickBroadcast t) -> {
			for (int i=0; i < customer.getOrdersList().size(); i++) {
				if(customer.getOrdersList().get(i).getTick() == t.getCurrentTick()) {
					BookOrderEvent event = new BookOrderEvent(customer, customer.getOrdersList().get(i).getTick(), customer.getOrdersList().get(i).getBookTitle());
					Future<OrderReceipt> future = sendEvent(event);
					if (future.get() != null) {
						customer.addReceipt(future.get());
						System.out.println(customer.getName() + " ordered " + future.get().getBookTitle());
						sendEvent(new DeliveryEvent(customer.getAddress(), customer.getDistance()));
					}
				}
			}
		});
		countDownLatch.countDown();
	}

}
