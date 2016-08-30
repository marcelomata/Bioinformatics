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
		
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/N2DH-GOWT1/", 91);
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/N2DH-GOWT1-mini/", 10);
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/N2DH-SIM/", 50);
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/N2DL-HeLa/", 91);
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/N3D-CHO-2/N3DH-CHO-training/", 91);
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/Raw1-amal/");
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/Raw2-amal/");
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/Test110/");
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/Test1/");
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/Test2/");
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/Test3");
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/Test4");
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/Test21");
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/'Challenge/Test22");
		
		//Any software can use the tool developed here. This is a example of Plugin to ImageJ that uses our framework.
		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/A workflow to proc 3D+t/data/", 20);
		
		tracking.run("20");
	}

}
																																