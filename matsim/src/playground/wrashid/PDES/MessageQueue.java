package playground.wrashid.PDES;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.matsim.gbl.Gbl;
import org.matsim.plans.Person;

public class MessageQueue {
	//TreeMap<String, Message> queue = new TreeMap<String, Message>();
	//private PriorityBlockingQueue<Message> queue1 = new PriorityBlockingQueue<Message>(10000);
	// PriorityQueue is better for performance than PriorityBlockingQueue, but requires all methods of this class
	// to be set synchronized
	private PriorityQueue<Message> queue1 = new PriorityQueue<Message>(10000);
	private LinkedList<Message> messageBuffer=new LinkedList<Message>();
	private volatile static int counter=0;
	public volatile double arrivalTimeOfLastRemovedMessage=0;
	private Object bufferLock=new Object();

	synchronized public void putMessage(Message m) {
		assert(!queue1.contains(m)):"inconsistency";
		assert(m.firstLock!=null);
		queue1.add(m);
		//assert(queue1.contains(m)):"inconsistency";
		// This assertion was removed, because of concurrent access this might be violated
	}
	
	
	synchronized public double getArrivalTimeOfNextMessage(){
		return queue1.peek().messageArrivalTime;
	}
	

	public static int getCounter() {
		return counter;
	}


	synchronized public void removeMessage(Message m) {
		//assert(queue1.contains(m)):"inconsistency";
		//if (!queue1.contains(m)){
			// if the message (e.g. DeadlockPreventionMessage) has already been fetched, it should not be allowed
			// to execute!!!
		//	m.killMessage(); 
		//}
		
		
		// in case the message is in the buffer and not in the queue yet
		synchronized(bufferLock){
			messageBuffer.remove(m);
		}
		
		
		queue1.remove(m);
	}

	synchronized public Message getNextMessage() {
		counter++;

		//if (counter % 10000==0){
		//	System.out.println("event:" + counter);
		//}
		
		Message m = queue1.poll();
		//arrivalTimeOfLastRemovedMessage=m.messageArrivalTime;
		return m;
	}

	synchronized public boolean isEmpty() {
		if (queue1.isEmpty() || queue1.peek() instanceof NullMessage && queue1.size()==0){
			return true;
		} else {
			return false;
		}
	}

	public void bufferMessage(Message m){
		synchronized(bufferLock){
			messageBuffer.add(m);
		}
	}
	
	public void emptyBuffer(){
		synchronized(bufferLock){
			while (!messageBuffer.isEmpty()){
				queue1.add(messageBuffer.poll());
			}
		}
	}
}
