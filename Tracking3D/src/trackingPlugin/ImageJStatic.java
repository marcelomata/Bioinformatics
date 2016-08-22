package trackingPlugin;

import java.io.File;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.geom.Object3D;
import mcib3d.geom.ObjectCreator3D;
import mcib3d.geom.Objects3DPopulation;
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

	public static void drawImageObjects(Object3D obj1, Object3D obj2, ImagePlus imagePlus, String dir) {
          // The object responsible of creating the new 3D image where the cell's detections
          // will be coloured according to the colour of the first occurrence of the cell
          ObjectCreator3D creator = new ObjectCreator3D(imagePlus.getImageStack());
          creator.clear();
          
          creator.drawObject(obj1);
          creator.drawObject(obj2);
          
          // Create the new image containing the differently-coloured objects
          ImagePlus res = new ImagePlus("Res", creator.getStack());

          // Save the image under the specified folder
          IJ.saveAsTiff(res, dir+ "/TEST/" + "TestColoc" + obj1.getValue()+obj2.getValue() + ".tif");
	}
	
	public static void drawImageObjects(Object3D obj, ImagePlus imagePlus, String dir, String addName) {
        // The object responsible of creating the new 3D image where the cell's detections
        // will be coloured according to the colour of the first occurrence of the cell
        ObjectCreator3D creator = new ObjectCreator3D(imagePlus.getImageStack());
        creator.clear();
        
        creator.drawObject(obj);
        
        // Create the new image containing the differently-coloured objects
        ImagePlus res = new ImagePlus("Res", creator.getStack());

        // Save the image under the specified folder
        IJ.saveAsTiff(res, dir+ "/TEST/" + "TestColoc" + obj.getValue() + addName + ".tif");
	}

}
