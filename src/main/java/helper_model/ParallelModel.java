package helper_model;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ParallelModel implements Serializable {
	
	private static transient final ParallelModel SHARED = new ParallelModel();
	
	private transient final List<? extends Thread> THREADS = new ArrayList<MyThread>();
	
	private transient final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);
	
	private transient final List<Future<?>> FUTURES = new ArrayList<Future<?>>();
	
	private ParallelModel() {}
	
	public static ParallelModel getHelperModel() {
		return ParallelModel.SHARED;
	}
	
	@SuppressWarnings("unchecked")
	public void awaitBad(final String componentName) {
		
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
	
	public void awaitThreadsBad() {
		this.THREADS.stream().parallel().forEach((thread)-> {
			try {
				synchronized(thread) {
					thread.wait();
					thread.notifyAll();
				}
			}
			catch(InterruptedException e) {
				System.out.println("error uncovered: " + e.getLocalizedMessage());
			}
		});
		System.out.println("exiting");
	}
	
	public void await(final String componentName) {
		
		System.out.println(componentName + " is starting\t" + Thread.currentThread().getName());
		
		try {
			this.FUTURES.add( 
					this.EXECUTOR_SERVICE.submit(
						() -> {
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
				)
			);
		}
		catch(Exception ex) {
			System.out.println("error uncovered: " + ex.getLocalizedMessage());
		}
	}
	
	public void awaitThreads() {
		this.FUTURES.parallelStream().forEach(
				(future) -> {
					try {
						future.get();
					}
					catch(Exception ex) {
						System.out.println(ex.getLocalizedMessage());
					}
				}
			);
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
