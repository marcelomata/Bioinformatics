package trackingPlugin;
import java.io.File;

import cell3DRenderer.Particles4DJOGLRenderer;
import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.PlugIn;
import trackingInterface.TrackingStrategy;
import trackingSPT.TrackingSPT;
import trackingSPT.objects.ObjectActionSPT4D;
import trackingSPT.objects.TrackingResultSPT;

public class PluginTracking implements PlugIn {
	@Override
	public void run(String arg) {
		File image = new File(arg);	
		Opener open = new Opener();
		ImagePlus imp = open.openImage(image.getAbsolutePath());
		
		TrackingStrategy tracking = new TrackingSPT(new ObjectActionSPT4D(imp));
		tracking.run();
		
		System.out.println("Rendering");
		Particles4DJOGLRenderer renderer = new Particles4DJOGLRenderer(new ParticlesTrackingResult((TrackingResultSPT) tracking.getResult()));
//		Particles4DJOGLRenderer renderer = new Particles4DJOGLRenderer(new ParticlesTrackingResult(null));
		renderer.run();
		
		
//		Objects3DPopulation population = new Objects3DPopulation(imp);
//		System.out.println(population.getNbObjects());
////		population.getObject(0).getLabelImage().show();
//		
//		// extract current time 
//        Duplicator dup = new Duplicator();
//        int[] dim = imp.getDimensions();
//        int selectedTime = imp.getFrame();
//        System.out.println(selectedTime);
//        System.out.println(imp.getNFrames());
//        ImagePlus timedup = null;
//        for (int i = 0; i < imp.getNFrames(); i++) {
//        	timedup = dup.run(imp, 1, 1, 1, dim[3], i+1, i+1);			
//        	timedup.show();
//		}
	}
}