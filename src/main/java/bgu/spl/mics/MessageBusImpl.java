package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBus instance = new MessageBusImpl();
	private ConcurrentHashMap<MicroService,BlockingQueue<Message>> hMapAssign;
	private ConcurrentHashMap<Class<? extends Event>,Queue<MicroService>> hMapSubscribeEvent;
	private ConcurrentHashMap<Class<? extends Broadcast>,Queue<MicroService>> hMapSubscribeBroadcast;
	private ConcurrentHashMap<MicroService, Queue<Class<? extends Message>>> hMapSubscribe;
	private ConcurrentHashMap<Event,Future> hMapEventFuture;

	private MessageBusImpl(){
		hMapAssign = new ConcurrentHashMap<>();
		hMapSubscribeEvent=new ConcurrentHashMap<>();
		hMapSubscribeBroadcast=new ConcurrentHashMap<>();
		hMapEventFuture=new ConcurrentHashMap<>();
		hMapSubscribe = new ConcurrentHashMap<>();
	}

	public static MessageBus getInstance() {
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		hMapSubscribeEvent.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		hMapSubscribeEvent.get(type).add(m);

		if(!hMapSubscribe.get(m).contains(type))
			hMapSubscribe.get(m).add(type);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		hMapSubscribeBroadcast.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		hMapSubscribeBroadcast.get(type).add(m);

		if(!hMapSubscribe.get(m).contains(type))
			hMapSubscribe.get(m).add(type);
	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		Future future = hMapEventFuture.get(e);
		if(future != null) {
			hMapEventFuture.get(e).resolve(result);
			hMapEventFuture.remove(e);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Queue<MicroService> queue = hMapSubscribeBroadcast.get(b.getClass());
		if(queue != null) {
			for(MicroService m : queue) {
				Queue<Message> messageQueue =hMapAssign.get(m);
				if(messageQueue != null)
					messageQueue.add(b);
			}
		}
	}

	@Override @SuppressWarnings("unchecked")
	public <T> Future<T> sendEvent(Event<T> e) {
		MicroService m;
		Future<T> f=null;
		Queue<MicroService> queue=hMapSubscribeEvent.get(e.getClass());
		if (queue != null) {
			synchronized (e.getClass()) {
				m=hMapSubscribeEvent.get(e.getClass()).poll();
				if (m != null)
					queue.add(m);
			}
			if (m != null) {
				synchronized (m) {
					Queue<Message> messages = hMapAssign.get(m);
					if(messages != null) {
						f = new Future<>();
						hMapEventFuture.put(e, f);
						messages.add(e);
					}
				}
			}
		}
		return f;
	}

	@Override
	public void register(MicroService m) {
		hMapAssign.putIfAbsent(m, new LinkedBlockingQueue<>());
		hMapSubscribe.putIfAbsent(m, new ConcurrentLinkedQueue<>());
	}

	@Override @SuppressWarnings("unchecked")
	public void unregister(MicroService m) {
		Queue<Message> messageQueue;
		synchronized (m) {
			messageQueue=hMapAssign.remove(m);
		}
		for(Class<? extends Message> message : hMapSubscribe.get(m)) {
			if(Event.class.isAssignableFrom(message)) {
				hMapSubscribeEvent.get(message).remove(m);
			}
			else if(Broadcast.class.isAssignableFrom(message))
				hMapSubscribeBroadcast.get(message).remove(m);
//			boolean removed =
//			while(!removed)
//				removed = hMapSubscribeEvent.get(message).remove(m);

		}
		hMapSubscribe.remove(m);
		for(Message e : messageQueue)
			complete((Event)e, null);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return hMapAssign.get(m).take();
	}
}
