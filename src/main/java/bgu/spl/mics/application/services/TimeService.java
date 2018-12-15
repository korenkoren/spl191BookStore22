package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int speed;
	private int duration;
	private int currentTick;

	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
		currentTick = 0;
	}

	@Override
	protected void initialize() {
		while(currentTick < duration - 1) {
			currentTick = currentTick + 1;
			sendBroadcast(new TickBroadcast(currentTick));
			System.out.println("Tick number "+currentTick);
			try {
				Thread.sleep(speed);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sendBroadcast(new TerminateBroadcast());
		terminate();
	}

}
