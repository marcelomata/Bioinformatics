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
	
	private File dirRes;
	
	public PluginTracking(String resultDirectory) {
		this.dirRes = new File(resultDirectory);
	}
	
	@Override
	public void run(String arg) {
		File image = new File(arg);
		ImagePlus imp = null;
		TrackingStrategy tracking;
		File dirSeg = new File(image.getAbsolutePath()+"/SEG/");
		File dirTrack = new File(image.getAbsolutePath()+"/TRACK/");
		if(image.isFile()) {
			Opener open = new Opener();
			imp = open.openImage(image.getAbsolutePath());
			tracking = new TrackingSPT(dirSeg, dirTrack);
		} else {
			tracking = new TrackingSPT(dirSeg, dirTrack);
		}
		
		tracking.run();
		
		System.out.println("Generating Challenge Format");
		GenerateChallengeFormat gen = new GenerateChallengeFormat((TrackingResult3DSPT) tracking.getResult(), dirSeg, dirRes);
		gen.computeColorChallenge(1);
		gen.challengeFormat(0);
		gen.computeColorChallenge(0);
		gen.saveColoredChallenge(2, 0, 0);
		gen.saveColored(2, 0);
		System.out.println("Rendering");
		Particles4DJOGLRenderer renderer = new Particles4DJOGLRenderer(new ParticlesTrackingResult((TrackingResult3DSPT) tracking.getResult()));
		renderer.run();
		
	}
}