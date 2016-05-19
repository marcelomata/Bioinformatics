import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
		for (int i = 1+2; i <= directory.listFiles().length+2; i++) {
			try {
				movie.add(ImageIO.read(new File(directory.getAbsolutePath()+"/seg-1_"+i+".bmp")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
//		TrackingCells2D tracking = new TrackingCells2D(movie);
//		tracking.trackCells();
		
		BufferedImage image = movie.get(0);
//		
		Graphics2D g2d = image.createGraphics();
	    g2d.setBackground(Color.WHITE);
	    g2d.setColor(Color.BLACK);
	    BasicStroke bs = new BasicStroke(2);
	    g2d.setStroke(bs);
	    g2d.drawLine(0, 0, 150, 150);
		
		try {
			ImageIO.write(image, "BMP", new File("./Tests/testDraw.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
