package trackingPlugin;

import java.io.File;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.image3d.ImageHandler;

public class ImageJStatic {
	
	public static ImageHandler getImageSeg(String dirSeg, String baseSeg, int firstSeg, int padSeg, int t) {
        //System.out.println("Opening seg " + t);
//        String fileName = dirSeg + "/" + baseSeg + IJ.pad(t+firstSeg, padSeg) + ".tif";
		String fileName = dirSeg+"/"+baseSeg;
//        String fileName = dirSeg + "/" + baseSeg + t + ".tif";
        //System.out.println("Opening " + fileName);
        ImagePlus plus = IJ.openImage(fileName);
        if (plus == null) {
            System.out.println("No image " + fileName);
            return null;
        }

        return ImageHandler.wrap(plus);
    }
	
	public static ImageHandler getImageSeg(String dirSeg, String baseSeg) {
        //System.out.println("Opening seg " + t);
        String fileName = dirSeg + "/" + baseSeg;
//        String fileName = dirSeg + "/" + baseSeg + t + ".tif";
        //System.out.println("Opening " + fileName);
        ImagePlus plus = IJ.openImage(fileName);
        if (plus == null) {
            System.out.println("No image " + fileName);
            return null;
        }

        return ImageHandler.wrap(plus);
    }
	
	public static ImageHandler getImage(File directory, String fileName) {
//	 	String fname = directory.getAbsolutePath()+"/"+fileName+IJ.pad(t, 2)+".tif";
	 	String fname = directory.getAbsolutePath()+"/"+fileName;
        ImagePlus plus = IJ.openImage(fname);
        if (plus == null) {
            System.out.println("No image " + fname);
            return null;
        }

        return ImageHandler.wrap(plus);
    }
	
	public static ImageHandler getImageRaw(File directory, String fileName, int t) {
	 	String fname = directory.getAbsolutePath()+"/"+fileName;
        ImagePlus plus = IJ.openImage(fname);
        if (plus == null) {
            System.out.println("No image " + fname);
            return null;
        }

        return ImageHandler.wrap(plus);
    }

}
