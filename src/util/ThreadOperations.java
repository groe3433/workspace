package util;

public class ThreadOperations extends Thread {
	
	private int thread_count;
	
	public ThreadOperations(int count) {
		super("my extending thread");
		System.out.println("my thread created" + this);
		this.thread_count = count;
		start();
	}

	public void run() {
		try {
			for (int i = 0; i < thread_count; i++) {
				System.out.println("Printing the count " + i);
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			System.out.println("my thread interrupted");
		}
		System.out.println("My thread run is over");
	}
}
