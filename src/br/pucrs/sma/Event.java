package br.pucrs.sma;

public class Event {

	private EventType type;
	private double time;
	private Queue originQueue;
	private Queue destinyQueue;

	public Event(EventType type, double time, Queue originQueue) {
		this.type = type;
		this.time = time;
		this.originQueue = originQueue;
	}

	public Event(EventType type, double time, Queue originQueue, Queue destinyQueue) {
		this(type, time, originQueue);
		this.destinyQueue = destinyQueue;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public Queue getOriginQueue() {
		return originQueue;
	}

	public Queue getDestinyQueue() {
		return destinyQueue;
	}

	@Override
	public String toString() {
		return "Event [type=" + type + ", time=" + time + ", originQueue=" + originQueue + ", destinyQueue="
				+ destinyQueue + "]";
	}

}
