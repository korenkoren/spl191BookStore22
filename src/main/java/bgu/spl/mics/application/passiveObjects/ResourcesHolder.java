package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private static ResourcesHolder instance = new ResourcesHolder();
	private Queue<DeliveryVehicle> vehicles;
	private Queue<Future<DeliveryVehicle>> futures;
	/**
     * Retrieves the single instance of this class.
     */

	public ResourcesHolder() {
		vehicles = new ConcurrentLinkedQueue<>();
		futures = new ConcurrentLinkedQueue<>();
	}

	public static ResourcesHolder getInstance() {
		return instance;
	}


	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> future = new Future<>();
		if(!vehicles.isEmpty()) {
			DeliveryVehicle vehicle = vehicles.poll();
			future.resolve(vehicle);
		}
		else
			futures.add(future);

		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if(futures.isEmpty())
			vehicles.add(vehicle);
		else
			futures.poll().resolve(vehicle);
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		this.vehicles.addAll(Arrays.asList(vehicles));
	}

}
