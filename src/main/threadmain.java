package main;

import util.ThreadOperations;

public class threadmain {
	public static void main(String[] s) {
		// kick off 10 threads
		ThreadOperations cnt = new ThreadOperations(10);
		try {
			while (cnt.isAlive()) {
				System.out.println("Main thread will be alive till the child thread is live");
				Thread.sleep(1500);
			}
		} catch (InterruptedException e) {
			System.out.println("Main thread interrupted");
		}
		System.out.println("Main thread's run is over");
	}
}