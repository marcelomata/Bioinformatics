import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.ImageProcessor;
import ij.process.PolygonFiller;

public class TestingFrames {

	public static void main(String[] args) {
		File directory = new File("./slices/");
		List<BufferedImage> images = new ArrayList<BufferedImage>();
		for (int i = 1; i <= directory.listFiles().length; i++) {
			try {
				images.add(ImageIO.read(new File(directory.getAbsolutePath()+"/seg-1_"+i+".bmp")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		BufferedImage image = images.get(7);
		
		ImagePlus imp = new ImagePlus("Teste", image);
		ImageStack stack = imp.getStack();
		ImageProcessor ip = stack.getProcessor(1);
		ParticleAnalyzer pa = new ParticleAnalyzer();
		pa.setup("", imp);
		pa.run(ip);
		List<Roi> particles = pa.getParticles();
		Roi r = null;
		
		whiteImage(image);
		double center[] = null;
		for (int i = 0; i < particles.size(); i++) {
			r = particles.get(i);
			center = r.getContourCentroid();
			image.setRGB((int)center[0], (int)center[1], Color.BLACK.getRGB());
		}
		
		try {
			ImageIO.write(image, "BMP", new File("./Tests/test.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		new ImagePlus("Teste", image).show();
		
	}
	
	public static void whiteImage(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				image.setRGB(j, i, Color.WHITE.getRGB());
			}
		}
	}

}
