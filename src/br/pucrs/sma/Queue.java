package br.pucrs.sma;

import java.util.ArrayList;

public class Queue {

	private String name;
	private double firstArrival;
	private int servers, capacity;
	private double minIn, maxIn, minOut, maxOut;
	private ArrayList<String> queues;
	private ArrayList<Double> probabilities;
	private double sumProbabilities;
	private int positionInArray;
	private int currentSize;
	private int clientLoss;
	private static final double MAX_PROBABILITY = 1.0;
	private static final double DONT_HAVE_EXTERNAL_ARRIVALS = -1.0;

	public Queue() {
		this.firstArrival = DONT_HAVE_EXTERNAL_ARRIVALS;
		this.queues = new ArrayList<>();
		this.probabilities = new ArrayList<>();
		this.sumProbabilities = 0.0;
		this.positionInArray = 0;
		this.currentSize = 0;
		this.clientLoss = 0;
	}

	public Queue(String name, int servers, int capacity, double minIn, double maxIn, double minOut, double maxOut) {
		this();
		this.name = name;
		this.servers = servers;
		this.capacity = capacity;
		this.minIn = minIn;
		this.maxIn = maxIn;
		this.minOut = minOut;
		this.maxOut = maxOut;
	}

	public Queue(String name, int servers, int capacity, double minOut, double maxOut) {
		this();
		this.name = name;
		this.servers = servers;
		this.capacity = capacity;
		this.minIn = DONT_HAVE_EXTERNAL_ARRIVALS;
		this.maxIn = DONT_HAVE_EXTERNAL_ARRIVALS;
		this.minOut = minOut;
		this.maxOut = maxOut;
	}

	public String getName() {
		return name;
	}

	public double getFirstArrival() {
		return firstArrival;
	}

	public void setFirstArrival(double firstArrival) {
		this.firstArrival = firstArrival;
	}

	public int getServers() {
		return servers;
	}

	public int getCapacity() {
		return capacity;
	}

	public double getMinIn() {
		return minIn;
	}

	public double getMaxIn() {
		return maxIn;
	}

	public double getMinOut() {
		return minOut;
	}

	public double getMaxOut() {
		return maxOut;
	}

	public int getPositionInArray() {
		return positionInArray;
	}

	public void setPositionInArray(int positionInArray) {
		this.positionInArray = positionInArray;
	}

	public int getCurrentSize() {
		return currentSize;
	}

	public void increaseCurrentSize() {
		currentSize++;
	}

	public void decreaseCurrentSize() {
		currentSize--;
	}

	public int getClientLoss() {
		return clientLoss;
	}

	public void increaseClientLoss() {
		clientLoss++;
	}

	public boolean addDestiny(String queue, double probability) {
		if (sumProbabilities + probability > MAX_PROBABILITY)
			return false;

		sumProbabilities += probability;
		queues.add(queue);
		probabilities.add(probability);
		return true;
	}

	public String getDestiny(double rand) {
		double probabilityCheck = 0.0;
		String queue = null;

		for (int i = 0; i < probabilities.size(); i++) {
			probabilityCheck += probabilities.get(i);
			if (rand < probabilityCheck) {
				queue = queues.get(i);
				break;
			}
		}

		return queue;
	}

	@Override
	public String toString() {
		return "Queue [name=" + name + ", firstArrival=" + firstArrival + ", servers=" + servers + ", capacity="
				+ capacity + ", minIn=" + minIn + ", maxIn=" + maxIn + ", minOut=" + minOut + ", maxOut=" + maxOut
				+ ", queues=" + queues + ", probabilities=" + probabilities + ", sumProbabilities=" + sumProbabilities
				+ ", positionInArray=" + positionInArray + "]";
	}

}
