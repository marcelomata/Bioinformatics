package trackingPlugin;
import java.io.File;

import cell3DRenderer.Particles4DJOGLRenderer;
import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.PlugIn;
import trackingInterface.TrackingStrategy;
import trackingSPT.TrackingSPT;
import trackingSPT.objects3D.ObjectActionSPT4D;
import trackingSPT.objects3D.TrackingResult3DSPT;

public class PluginTracking implements PlugIn {
	@Override
	public void run(String arg) {
		File image = new File(arg);	
		Opener open = new Opener();
		ImagePlus imp = open.openImage(image.getAbsolutePath());
		
		TrackingStrategy tracking = new TrackingSPT(new ObjectActionSPT4D(imp));
		tracking.run();
		
		System.out.println("Rendering");
		Particles4DJOGLRenderer renderer = new Particles4DJOGLRenderer(new ParticlesTrackingResult((TrackingResult3DSPT) tracking.getResult()));
//		Particles4DJOGLRenderer renderer = new Particles4DJOGLRenderer(new ParticlesTrackingResult(null));
		renderer.run();
		
		
	}
}