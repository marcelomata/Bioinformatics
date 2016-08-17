package trackingPlugin;

public class Log {
	
	private static final boolean DEBUG = false;
	
	public static void println(String string) {
		if(DEBUG) {
			System.out.println(string);
		}
	}

	public static void print(String string) {
		if(DEBUG) {
			System.out.print(string);
		}
	}

}
