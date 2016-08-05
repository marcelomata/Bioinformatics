import trackingPlugin.PluginTracking;

public class Main {
	
	public static void main(String[] args) {
		
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/N2DH-SIM/");
		
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/N3D-CHO-2/N3DH-CHO-testing/");
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/N3D-CHO-2/N3DH-CHO-training/");
		
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/Raw1-amal/");
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/Raw2-amal/");
		
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/N2DH-GOWT1/");
		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/N2DH-GOWT1-mini/");
//		PluginTracking tracking = new PluginTracking("/home/marcelodmo/Documents/data/Challenge/N2DL-HeLa/");
		
		tracking.run("02");
	}

}
