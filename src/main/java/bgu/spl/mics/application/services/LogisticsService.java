package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.VehicleRequestEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch countDownLatch;
	public LogisticsService(CountDownLatch countDownLatch) {
		super("LogisticService");
		this.countDownLatch = countDownLatch;
	}

	@Override @SuppressWarnings("unchecked")
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast t) -> terminate());
		subscribeEvent(DeliveryEvent.class, (DeliveryEvent d) -> {
			Future<Future<DeliveryVehicle>> future = sendEvent(new VehicleRequestEvent());
			if (future != null) {
				Future<DeliveryVehicle> futureVehicle = future.get();
				if (futureVehicle != null) {
					DeliveryVehicle vehicle = futureVehicle.get();
					if(vehicle != null) {
						vehicle.deliver(d.getAddress(), d.getDistance());
						sendEvent(new ReleaseVehicleEvent(new Future<DeliveryVehicle>()));
						complete(d, null);
					}
				}
			}
			complete(d, null);
		});
		countDownLatch.countDown();
	}

}
