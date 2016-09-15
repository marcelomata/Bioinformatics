import trackingPlugin.PluginTracking;


/**
 * 
 * Main class to run the tracking algorithm
 * 
 * @author Marcelo da Mata
 *
 */
public class Main {
	
	public static void main(String[] args) {
		
		//Any software can use the tool developed here. This is a example of Plugin to ImageJ that uses our framework.
		PluginTracking tracking = new PluginTracking("C:/workspaces/Singapore/datasets/dataset01", 20);
		
		tracking.run("20");
	}

}
																																