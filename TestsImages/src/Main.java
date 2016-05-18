import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) {
		File directory = new File("./video1-small part/");
		List<BufferedImage> movie = new ArrayList<BufferedImage>();
		for (int i = 1; i <= directory.listFiles().length; i++) {
			try {
				movie.add(ImageIO.read(new File(directory.getAbsolutePath()+"/seg-1_"+i+".bmp")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		TrackingCells2D video = new TrackingCells2D(movie);
		
		
		
	}

}
