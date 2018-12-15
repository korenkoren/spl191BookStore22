package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.VehicleRequestEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
	private CountDownLatch countDownLatch;
	private LinkedList<Future<DeliveryVehicle>> futuresToResolve=new LinkedList<>();

	public ResourceService(CountDownLatch countDownLatch) {
		super("Change_This_Name");
		this.countDownLatch = countDownLatch;
	}

	@Override @SuppressWarnings("unchecked")
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast t) -> {
			for (Future<DeliveryVehicle> futureToResolve : futuresToResolve)
				futureToResolve.resolve(null);
			terminate();
		});
		subscribeEvent(VehicleRequestEvent.class, (VehicleRequestEvent v) -> {
			Future<DeliveryVehicle> future = resourcesHolder.acquireVehicle();
			futuresToResolve.add(future);
			complete(v, future);

		});

		subscribeEvent(ReleaseVehicleEvent.class, (ReleaseVehicleEvent r) -> {
			resourcesHolder.releaseVehicle(r.getDeliveryVehicleFuture().get());
			complete(r, "released");
			futuresToResolve.remove(r.getDeliveryVehicleFuture());
		});
		countDownLatch.countDown();
	}

}
