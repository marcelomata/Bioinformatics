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
		ImagePlus imp = null;
		TrackingStrategy tracking;
		if(image.isFile()) {
			Opener open = new Opener();
			imp = open.openImage(image.getAbsolutePath());
			tracking = new TrackingSPT(new ObjectActionSPT4D(imp));
		} else {
			tracking = new TrackingSPT(new ObjectActionSPT4D(arg));
		}
		
		tracking.run();
		
		System.out.println("Rendering");
		GenerateChallengeFormat gen = new GenerateChallengeFormat((TrackingResult3DSPT) tracking.getResult());
		System.out.println(gen.getRoots().size());
		Particles4DJOGLRenderer renderer = new Particles4DJOGLRenderer(new ParticlesTrackingResult((TrackingResult3DSPT) tracking.getResult()));
//		Particles4DJOGLRenderer renderer = new Particles4DJOGLRenderer(new ParticlesTrackingResult(null));
		renderer.run();
		
		
	}
}