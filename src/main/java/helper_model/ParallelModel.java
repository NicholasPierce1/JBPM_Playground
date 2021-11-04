package helper_model;

import java.io.Serializable;
import java.util.*;

public final class ParallelModel implements Serializable {
	
	private static transient final ParallelModel SHARED = new ParallelModel();
	
	private transient final List<? extends Thread> THREADS = new ArrayList<MyThread>();
	
	private ParallelModel() {}
	
	public static ParallelModel getHelperModel() {
		return ParallelModel.SHARED;
	}
	
	@SuppressWarnings("unchecked")
	public void await(final String componentName) {
		
		System.out.println(componentName + " is starting\t" + Thread.currentThread().getName());
		
//		try {
		final MyThread myThread = new MyThread(componentName);
		myThread.start();
		((List<MyThread>)this.THREADS).add(myThread);
//			//myThread.wait();
//		}
//		catch(InterruptedException e) {
//			System.out.println("error uncovered: " + e.getLocalizedMessage());
//		}
	}
	
	public void awaitThreads() {
		this.THREADS.stream().parallel().forEach((thread)-> {
			try {
				synchronized(thread) {
					thread.wait();
				}
			}
			catch(InterruptedException e) {
				System.out.println("error uncovered: " + e.getLocalizedMessage());
			}
		});
	}
	
	static final class MyThread extends Thread{
		
		final String componentName;
		
		MyThread(String componentName){
			this.componentName = componentName;
		}
		
		@Override
		public void run() {
			int secondsToWait = new Random().nextInt(5);
			
			try {
				Thread.sleep(secondsToWait * 1000);
			}
			catch(InterruptedException e) {
				System.out.println("error uncovered: " + e.getLocalizedMessage());
			}
			finally {
				final StringBuilder outgoingText = new StringBuilder(componentName);
				System.out.println(outgoingText.append(" is finished"));
			}
		}
		
	}

}
