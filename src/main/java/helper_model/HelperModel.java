package helper_model;

import java.io.Serializable;

public final class HelperModel implements Serializable {
	
	private static transient final HelperModel SHARED = new HelperModel();
	
	private HelperModel() {}
	
	public static HelperModel getHelperModel() {
		return HelperModel.SHARED;
	}
	
	public void printValue(final String value) {
		
		try {
			Thread.sleep(1000);
		}
		catch(InterruptedException e) {
			System.out.println("error uncovered: " + e.getLocalizedMessage());
		}
		
		System.out.println("value from helper: " + value);
	}

}
