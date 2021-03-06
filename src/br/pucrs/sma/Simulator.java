package br.pucrs.sma;

import java.util.ArrayList;

/**
 * @author DiegoOsmarinBasso
 *
 */
public class Simulator {

	public static int EXECUTIONS = 5;
	public static int RANDS_NUM = 100000;
	public static ArrayList<Integer> SEEDS = new ArrayList<>();
	public static int INFINITE_QUEUE_SIZE = 100;

	public static void run(ArrayList<Queue> queues) {

		// Array to calculate the means of the EXECUTIONS
		// | 0 Clients | 1 Client | ... | Total Time | Losses | --> size capacity + 3
		ArrayList<double[]> executionsMeans = new ArrayList<>();
		for (int i = 0; i < queues.size(); i++) {
			executionsMeans.add(new double[queues.get(i).getCapacity() + 3]);
		}

		// Executions of the algorithm
		for (int execution = 0; execution < EXECUTIONS; execution++) {

			/**
			 * INITIALIZATIONS
			 */
			// Zero the queues currentSize e clientLoss
			for (Queue q : queues) {
				q.zeroCurrentSize();
				q.zeroClientLoss();
			}

			// set new seed every new execution, use clock as seed if SEEDS not informed
			if (SEEDS.isEmpty()) {
				LinearCongruentialGenerator.setNewSeed();
			}
			// else use SEEDS informed
			else {
				LinearCongruentialGenerator.setNewSeed(SEEDS.get(execution));
			}

			// Times variable for Arrivals, Passes and Departures
			double timeElapsed = 0.0;
			double lastEvent = 0.0;

			// Keeping track of queues times
			// | 0 Clients | 1 Client | ... | Total Time | --> size capacity + 2
			ArrayList<double[]> times = new ArrayList<>();
			for (int i = 0; i < queues.size(); i++) {
				times.add(new double[queues.get(i).getCapacity() + 2]);
			}

			// Keeping track of events
			ArrayList<Event> events = new ArrayList<>();

			// Add first events, the first arrivals
			for (Queue q : queues) {
				if (q.getFirstArrival() >= 0.0)
					events.add(new Event(EventType.ARRIVAL, q.getFirstArrival(), q));
			}

			/**
			 * INFORMING USER
			 */
			System.out.println("Simulacao #" + execution + ": usando semente " + LinearCongruentialGenerator.getSeed());

			/**
			 * RUNING SIMULATION Run the algorithm rands_num times
			 */
			for (int randsUsed = 0; randsUsed < RANDS_NUM;) {

				// Get the next event, its queue and time array
				Event nextEvent = getNextEvent(events);
				Queue eventQueue = nextEvent.getOriginQueue();
				double[] queueTimeArray = times.get(eventQueue.getPositionInArray());

				// Accounting the time since last event
				timeElapsed = nextEvent.getTime() - lastEvent;
				for (int i = 0; i < times.size(); i++) {
					times.get(i)[queues.get(i).getCurrentSize()] += timeElapsed;
					times.get(i)[queues.get(i).getCapacity() + 1] = nextEvent.getTime();
				}
				lastEvent = nextEvent.getTime();

				// New arrival
				if (nextEvent.getType() == EventType.ARRIVAL) {

					// If there is room, increment the queue
					if (eventQueue.getCurrentSize() < eventQueue.getCapacity()) {
						eventQueue.increaseCurrentSize();

						// If there are idle servers, compute service time
						if (eventQueue.getCurrentSize() <= eventQueue.getServers()) {

							String destiny = eventQueue.getDestiny(LinearCongruentialGenerator.nextRand());
							double time = queueTimeArray[eventQueue.getCapacity() + 1] + LinearCongruentialGenerator
									.nextRand(eventQueue.getMinOut(), eventQueue.getMaxOut());
							randsUsed += 2;

							// If null, schedule the client to leave the system
							if (destiny == null) {
								events.add(new Event(EventType.DEPARTURE, time, eventQueue));
							}
							// Else schedule the client to pass from one queue to another
							else {
								events.add(new Event(EventType.PASSAGE, time, eventQueue,
										getQueueByName(queues, destiny)));
							}
						}
					}
					// Else account loss
					else {
						eventQueue.increaseClientLoss();
					}
					// Schedule new arrival
					events.add(new Event(EventType.ARRIVAL, queueTimeArray[eventQueue.getCapacity() + 1]
							+ LinearCongruentialGenerator.nextRand(eventQueue.getMinIn(), eventQueue.getMaxIn()),
							eventQueue));
					randsUsed++;
				}
				// New departure
				else {
					// Accounting the eventQueue departure
					eventQueue.decreaseCurrentSize();

					// If still has queue, compute service time
					if (eventQueue.getCurrentSize() >= eventQueue.getServers()) {

						String destiny = eventQueue.getDestiny(LinearCongruentialGenerator.nextRand());
						double time = queueTimeArray[eventQueue.getCapacity() + 1]
								+ LinearCongruentialGenerator.nextRand(eventQueue.getMinOut(), eventQueue.getMaxOut());
						randsUsed += 2;

						// If null, schedule the client to leave the system
						if (destiny == null) {
							events.add(new Event(EventType.DEPARTURE, time, eventQueue));
						}
						// Else schedule the client to pass from one queue to another
						else {
							events.add(new Event(EventType.PASSAGE, time, eventQueue, getQueueByName(queues, destiny)));
						}
					}
					// Accounting passage from one queue to another
					if (nextEvent.getType() == EventType.PASSAGE) {

						Queue receivingQueue = nextEvent.getDestinyQueue();

						if (receivingQueue.getCurrentSize() < receivingQueue.getCapacity()) {
							receivingQueue.increaseCurrentSize();

							// If there are idle servers in the receiving queue, compute service time
							if (receivingQueue.getCurrentSize() <= receivingQueue.getServers()) {

								double[] receivingQueueTimeArray = times.get(receivingQueue.getPositionInArray());
								String destiny = receivingQueue.getDestiny(LinearCongruentialGenerator.nextRand());
								double time = receivingQueueTimeArray[receivingQueue.getCapacity() + 1]
										+ LinearCongruentialGenerator.nextRand(receivingQueue.getMinOut(),
												receivingQueue.getMaxOut());
								randsUsed += 2;

								// If null, schedule the client to leave the system
								if (destiny == null) {
									events.add(new Event(EventType.DEPARTURE, time, receivingQueue));
								}
								// Else schedule the client to pass from one queue to another
								else {
									events.add(new Event(EventType.PASSAGE, time, receivingQueue,
											getQueueByName(queues, destiny)));
								}
							}
						}
						// Else account loss
						else {
							receivingQueue.increaseClientLoss();
						}
					}
				}

			}

			/**
			 * COMPUTE TOTALS TO CALCULATE MEANS
			 */
			// | 0 Clients | 1 Client | ... | Total Time | Losses | --> size capacity + 3
			for (int i = 0; i < executionsMeans.size(); i++) {
				double[] executionMean = executionsMeans.get(i);
				double[] time = times.get(i);
				for (int j = 0; j < time.length; j++) {
					executionMean[j] += time[j];
				}
				executionMean[executionMean.length - 1] += queues.get(i).getClientLoss();
			}
		}

		/**
		 * CALCULATE MEANS AND SHOW RESULTS
		 */
		for (int i = 0; i < executionsMeans.size(); i++) {
			double[] executionMean = executionsMeans.get(i);

			// Calculate means
			for (int j = 0; j < executionMean.length; j++) {
				executionMean[j] /= EXECUTIONS;
			}

			// Show outputs
			Queue queue = queues.get(i);
			System.out.println("\n\n*******************************************************");
			System.out.println(
					"Fila " + queue.getName() + " (G/G/" + queue.getServers() + "/" + queue.getCapacity() + ")");
			if (queue.getMinIn() > 0.0)
				System.out.printf("Chegadas %3.1f ... %3.1f\n", queue.getMinIn(), queue.getMaxIn());
			System.out.printf("Servicos %3.1f ... %3.1f\n", queue.getMinOut(), queue.getMaxOut());
			System.out.println("*******************************************************");
			for (int k = 0; k <= queue.getCapacity(); k++) {
				System.out.printf("Media de tempo para fila tamanho %d: %12.4f\n", k, executionMean[k]);
				if (executionMean[k] == 0.0)
					break;
			}
			System.out.printf("\nMedia de clientes perdidos: %.4f\n", executionMean[executionMean.length - 1]);
		}
		System.out.println("\n\n*******************************************************");
		System.out.printf("Media de tempo total de excucao: %.4f\n",
				executionsMeans.get(0)[executionsMeans.get(0).length - 2]);
		System.out.println("*******************************************************");
	}

	private static Queue getQueueByName(ArrayList<Queue> queues, String destiny) {
		for (Queue q : queues) {
			if (destiny.equals(q.getName()))
				return q;
		}
		return null;
	}

	/**
	 * @param events
	 * @return nextEvent
	 */
	private static Event getNextEvent(ArrayList<Event> events) {

		// Discover next event
		Event e = events.get(0);
		for (int i = 1; i < events.size(); i++) {
			if (events.get(i).getTime() < e.getTime()) {
				e = events.get(i);
			}
		}

		// Delete event from events array
		events.remove(e);

		// return the event for processing
		return e;
	}

}
