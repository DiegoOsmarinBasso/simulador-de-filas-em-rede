package br.pucrs.sma;

public class LinearCongruentialGenerator {

	// Constants
	private static int a = 2342;
	private static int c = 11;
	private static int m = Integer.MAX_VALUE;
	private static long seed;
	private static double rand;

	// Set new seed, it is initialized with ZERO
	public static void setNewSeed() {
		seed = System.currentTimeMillis();
	}

	public static void setNewSeed(Integer informedSeed) {
		seed = informedSeed;
	}

	public static long getSeed() {
		return seed;
	}

	// Update seed and generate new random
	public static double nextRand() {
		seed = (a * seed + c) % m;
		rand = (double) seed / m;
		return rand;
	}

	// Update seed, generate new random and calculate interval
	public static double nextRand(double min, double max) {
		nextRand();
		return (max - min) * rand + min;
	}

}
