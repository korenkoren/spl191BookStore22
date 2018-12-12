package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.VehicleRequestEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

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

	public LogisticsService() {
		super("LogisticService");
		// TODO Implement this
	}

	@Override @SuppressWarnings("unchecked")
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class, (DeliveryEvent d) -> {
			Future<Future<DeliveryVehicle>> future = sendEvent(new VehicleRequestEvent());
			Future<DeliveryVehicle> futureVehicle = future.get();
			DeliveryVehicle vehicle = futureVehicle.get();
			vehicle.deliver(d.getAddress(), d.getDistance());
			sendEvent(new ReleaseVehicleEvent(vehicle));
			complete(d, true);
		});
		
	}

}
