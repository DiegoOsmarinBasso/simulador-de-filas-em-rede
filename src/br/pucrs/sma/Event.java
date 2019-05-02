package br.pucrs.sma;

public class Event {

	private EventType type;
	private double time;
	private Queue queue;

	public Event(EventType type, double time) {
		this.type = type;
		this.time = time;
	}

	public Event(EventType type, double time, Queue queue) {
		this(type, time);
		this.queue = queue;
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

	public Queue getQueue() {
		return queue;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}

}
