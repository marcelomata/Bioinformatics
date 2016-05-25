import java.io.File;

import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.PlugIn;
import mcib3d.geom.Objects3DPopulation;

public class PluginTracking implements PlugIn {
	@Override
	public void run(String arg) {
		File image = new File("/home/marcelodmo/Documents/data/droso-seg.tif");	
		Opener open = new Opener();
		ImagePlus imp = open.openImage(image.getAbsolutePath());
		Objects3DPopulation population = new Objects3DPopulation(imp);
		System.out.println(population.getNbObjects());
		population.getObject(0).getLabelImage().show();
	}
}