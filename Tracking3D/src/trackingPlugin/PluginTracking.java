package trackingPlugin;
import java.io.File;

import ij.plugin.PlugIn;
import trackingInterface.TrackingStrategy;
import trackingSPT.TrackingSPT;
import trackingSPT.objects3D.TrackingResult3DSPT;

public class PluginTracking implements PlugIn {
	
	private String parentDir;
	private int numMaxFrames;
	
	/**
	 * 
	 * @param parentDir the directory 
	 * @param numMaxFrames
	 */
	public PluginTracking(String parentDir, int numMaxFrames) {
		this.parentDir = parentDir;
		this.numMaxFrames = numMaxFrames;
	}
	
	@Override
	public void run(String test) {
		TrackingStrategy tracking;
		String segDir = "/"+test+"_GT/";
		String resDir = "/"+test+"_RES/";
		String rawDir = "/"+test+"/";
		File dirSeg = new File(parentDir+segDir+"SEG/");
		File dirTrack = new File(parentDir+resDir+"TRACK/");
		File dirRaw = new File(parentDir+rawDir);
		File image = new File(parentDir);	
		if(image.isFile()) {
			tracking = new TrackingSPT(dirSeg, dirTrack, numMaxFrames);
		} else {
			if(!dirSeg.exists()) {
				dirSeg.mkdirs();
			}
			if(!dirRaw.exists()) {
				dirRaw.mkdirs();
			}
			tracking = new TrackingSPT(dirSeg, dirRaw, numMaxFrames);
		}
		
		tracking.run();
		
		Log.println("Generating Challenge Format");
		GenerateChallengeFormat gen = new GenerateChallengeFormat((TrackingResult3DSPT) tracking.getResult(), dirSeg, dirTrack.getParentFile(), numMaxFrames);
		gen.computeColorChallenge(1);
		gen.challengeFormat(0);
		gen.computeColorChallenge(0);
		gen.saveColoredChallenge(2, 0, 0);
		gen.saveColored(2, 0);
//		Log.println("Rendering");
//		Particles4DJOGLRenderer renderer = new Particles4DJOGLRenderer(new ParticlesTrackingResult((TrackingResult3DSPT) tracking.getResult()));
//		renderer.run();
		
	}
}