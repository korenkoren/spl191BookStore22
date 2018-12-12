package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBus instance;
	private List <MicroService>microServicesList;
	private int placeInLIst;
	private ConcurrentHashMap<MicroService,BlockingQueue<Message>> hMapAssign;
	private ConcurrentHashMap<Class<? extends Event>,List<MicroService>> hMapSubscribeEvent;
	private ConcurrentHashMap<Class<? extends Broadcast>,List<MicroService>> hMapSubscribeBroadcast;
	private ConcurrentHashMap<Event,Future> hMapEventFuture;

	private MessageBusImpl(){
		instance=new MessageBusImpl();
		microServicesList=new LinkedList<MicroService>();
		hMapAssign = new ConcurrentHashMap<MicroService,BlockingQueue<Message>>();
		hMapSubscribeEvent=new ConcurrentHashMap<Class<? extends Event>,List<MicroService>>();
		hMapSubscribeBroadcast=new ConcurrentHashMap<Class<? extends Broadcast>,List<MicroService>>();
		hMapEventFuture=new ConcurrentHashMap<Event,Future>();
	}

	public static MessageBus getInstance() {
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		microServicesList.add(m);
		hMapSubscribeEvent.put(type,microServicesList);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		microServicesList.add(m);
		hMapSubscribeBroadcast.put(type,microServicesList);
	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		hMapEventFuture.get(e).resolve(result);
		hMapEventFuture.remove(e);

	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		BlockingQueue<Message> q;
		List<MicroService> l=hMapSubscribeBroadcast.get(b.getClass());
		for (int i=0; i < l.size(); i++) {
			q=hMapAssign.get(l.get(i));
			q.add(b);
			hMapAssign.put(l.get(i),q);
		}
		this.notifyAll();
	}

	@Override @SuppressWarnings("unchecked")
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f;
		BlockingQueue<Message> q;
		List<MicroService> l = hMapSubscribeEvent.get(e.getClass());
		if(l.get(placeInLIst%l.size())==null)
			return null;
		q=hMapAssign.get(l.get(placeInLIst%l.size()));
		q.add(e);
		hMapAssign.put(l.get(placeInLIst%l.size()),q);
		f=hMapEventFuture.get(e);//todo warning
		this.notifyAll();
		return f;
	}

	@Override
	public void register(MicroService m) {//todo how to use this method
		Queue q;
		q=hMapAssign.get(m);
	}

	@Override
	public void unregister(MicroService m) {
		hMapAssign.get(m).clear();
		hMapAssign.remove(m);
	}

	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
		while(hMapAssign.get(m).poll()==null) {
			try {
				this.wait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			return hMapAssign.get(m).poll();
	}
}
