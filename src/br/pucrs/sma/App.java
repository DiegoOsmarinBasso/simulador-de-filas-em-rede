package br.pucrs.sma;

import java.io.BufferedReader;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author DiegoOsmarinBasso
 *
 */
public class App {

	public static void main(String[] args) {

		ArrayList<Queue> queues = new ArrayList<>();

		if (args.length != 1) {
			System.out.println("\nEntrada invalida!");
			System.out.println("\nUso conforme exemplo abaixo:");
			System.out.println("java -jar simuladorDeFilas.jar seu_arquivo\n");
			System.exit(0);
		}

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(args[0]))) {

			while (reader.ready()) {

				String line = reader.readLine();

				// INFINITE_QUEUE_SIZE
				if (line.startsWith("INFINITE_QUEUE_SIZE")) {
					String[] values = reader.readLine().split(" ");
					Simulator.INFINITE_QUEUE_SIZE = Integer.parseInt(values[0]);
				}
				// QUEUES
				else if (line.startsWith("QUEUES")) {
					int positionInArray = 0;
					while (true) {
						String[] values = reader.readLine().split(" ");

						// Linha em branco, passar para proxima entrada
						if (values.length == 0 || "".equals(values[0])) {
							break;
						}
						// Quatro valores, fila INFINITA que NAO possui entrada externa
						else if (values.length == 4) {
							queues.add(new Queue(values[0], Integer.parseInt(values[1]), Simulator.INFINITE_QUEUE_SIZE,
									Double.parseDouble(values[2]), Double.parseDouble(values[3])));
						}
						// Cinco valores, fila que NAO possui entrada externa
						else if (values.length == 5) {
							queues.add(new Queue(values[0], Integer.parseInt(values[1]), Integer.parseInt(values[2]),
									Double.parseDouble(values[3]), Double.parseDouble(values[4])));
						}
						// Seis valores, fila INFINITA que possui entrada externa
						else if (values.length == 6) {
							queues.add(new Queue(values[0], Integer.parseInt(values[1]), Simulator.INFINITE_QUEUE_SIZE,
									Double.parseDouble(values[2]), Double.parseDouble(values[3]),
									Double.parseDouble(values[4]), Double.parseDouble(values[5])));
						}
						// Sete valores, fila que possui entrada externa
						else if (values.length == 7) {
							queues.add(new Queue(values[0], Integer.parseInt(values[1]), Integer.parseInt(values[2]),
									Double.parseDouble(values[3]), Double.parseDouble(values[4]),
									Double.parseDouble(values[5]), Double.parseDouble(values[6])));
						}
						// Entrada inválida
						else {
							throw new IllegalArgumentException("Linha inválida para a entrada QUEUES!\n"
									+ "Linha começa com " + values[0] + "...");
						}
						queues.get(queues.size() - 1).setPositionInArray(positionInArray++);
					}
				}
				// FIRST_ARRIVAL
				else if (line.startsWith("FIRST_ARRIVAL")) {
					if (queues.isEmpty()) {
						throw new IllegalArgumentException(
								"As entradas FIRST_ARRIVAL devem vir depois das entradas QUEUES!");
					}
					while (true) {
						String[] values = reader.readLine().split(" ");

						// Linha em branco, passar para proxima entrada
						if (values.length == 0 || "".equals(values[0])) {
							break;
						}
						// Dois valores, nome da fila e primeira chegada
						else if (values.length == 2) {
							for (Queue q : queues) {
								if (values[0].equals(q.getName())) {
									q.setFirstArrival(Double.parseDouble(values[1]));
									break;
								}
							}
						}
						// Entrada inválida
						else {
							throw new IllegalArgumentException("Linha inválida para a entrada FIRST_ARRIVAL!\n"
									+ "Linha começa com " + values[0] + "...");
						}
					}
				}
				// NETWORK
				else if (line.startsWith("NETWORK")) {
					if (queues.isEmpty()) {
						throw new IllegalArgumentException("As entradas NETWORK devem vir depois das entradas QUEUES!");
					}
					while (true) {
						String[] values = reader.readLine().split(" ");

						// Linha em branco, passar para proxima entrada
						if (values.length == 0 || "".equals(values[0])) {
							break;
						}
						// Tres valores, nome fila origem, nome fila destino e probabilidade
						else if (values.length == 3) {
							for (Queue q : queues) {
								if (values[0].equals(q.getName())) {
									if (q.addDestiny(values[1], Double.parseDouble(values[2])))
										break;
									else
										throw new IllegalArgumentException(
												"Soma das probabilidades para " + values[0] + " é maior que 1.0!");
								}
							}
						}
						// Entrada inválida
						else {
							throw new IllegalArgumentException("Linha inválida para a entrada NETWORK!\n"
									+ "Linha começa com " + values[0] + "...");
						}
					}
				}
				// RAND_PER_SEED
				else if (line.startsWith("RAND_PER_SEED")) {
					String[] values = reader.readLine().split(" ");
					Simulator.RANDS_NUM = Integer.parseInt(values[0]);
				}
				// SEEDS
				else if (line.startsWith("SEEDS")) {

					Simulator.EXECUTIONS = 0;

					while (true) {
						String[] values = reader.readLine().split(" ");

						// Linha em branco, passar para proxima entrada
						if (values.length == 0 || "".equals(values[0])) {
							break;
						}
						// Um valor, semente a ser usada na execucao
						else if (values.length == 1) {
							Simulator.SEEDS.add(Integer.parseInt(values[0]));
							Simulator.EXECUTIONS += 1;
						}
						// Entrada inválida
						else {
							throw new IllegalArgumentException("Linha inválida para a entrada RAND_PER_SEED!\n"
									+ "Linha começa com " + values[0] + "...");
						}
					}
				}
				// NUMBER_RANDOM_SEEDS
				else if (line.startsWith("NUMBER_RANDOM_SEEDS")) {
					String[] values = reader.readLine().split(" ");
					Simulator.EXECUTIONS = Integer.parseInt(values[0]);
				}
			}

		} catch (IllegalArgumentException e) {
			System.out.println("\n" + e.getMessage() + "\n");
		} catch (MalformedInputException e) {
			System.out.println("\nArquivo não pode conter acentos ou caracteres especiais!\n");
		} catch (Exception e) {
			System.out.println("\nArquivo " + args[0] + " não encontrado!\n");
		}

		// Conferindo dados
		for (Queue q : queues) {
			System.out.println(q);
		}

		// RODAR SIMULACAOES
		Simulator.run(queues);
	}
}
