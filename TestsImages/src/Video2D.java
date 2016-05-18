import java.awt.image.BufferedImage;
import java.util.List;

public class Video2D {
	
	private List<Frame> video;
	
	public Video2D(List<BufferedImage> images) {
		loadVideo(images);
	}

	private void loadVideo(List<BufferedImage> images) {
		for (BufferedImage image : images) {
			video.add(new Frame(image));
		}
	}
	
}
