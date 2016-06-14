/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amal;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Duplicator;
import mcib3d.image3d.ImageHandler;

/**
 *
 * @author thomasb
 */
public class DataSet {

    int ID;
    String name;

    String dirSeg;
    String dirRaw;
    String baseSeg = "Seg-";
    String baseRaw = "Raw-";
    int firstSeg = 1;
    int firstRaw = 1;
    int padRaw = 3;
    int padSeg = 3;

    int nbFrames;
    double calXY, calZ;
    double calT;

    public DataSet(String name, int nbFrames) {
        this.name = name;
        this.nbFrames = nbFrames;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirSeg() {
        return dirSeg;
    }

    public void setDirSeg(String dirSeg) {
        this.dirSeg = dirSeg;
    }

    public String getDirRaw() {
        return dirRaw;
    }

    public void setDirRaw(String dirRaw) {
        this.dirRaw = dirRaw;
    }

    public String getBaseSeg() {
        return baseSeg;
    }

    public void setBaseSeg(String baseSeg) {
        this.baseSeg = baseSeg;
    }

    public String getBaseRaw() {
        return baseRaw;
    }

    public void setBaseRaw(String baseRaw) {
        this.baseRaw = baseRaw;
    }

    public int getFirstSeg() {
        return firstSeg;
    }

    public void setFirstSeg(int firstSeg) {
        this.firstSeg = firstSeg;
    }

    public int getFirstRaw() {
        return firstRaw;
    }

    public void setFirstRaw(int firstRaw) {
        this.firstRaw = firstRaw;
    }

    public int getPadRaw() {
        return padRaw;
    }

    public void setPadRaw(int padRaw) {
        this.padRaw = padRaw;
    }

    public int getPadSeg() {
        return padSeg;
    }

    public void setPadSeg(int padSeg) {
        this.padSeg = padSeg;
    }

    public int getNbFrames() {
        return nbFrames;
    }

    public void setNbFrames(int nbFrames) {
        this.nbFrames = nbFrames;
    }

    public double getCalXY() {
        return calXY;
    }

    public void setCalXY(double calXY) {
        this.calXY = calXY;
    }

    public double getCalZ() {
        return calZ;
    }

    public void setCalZ(double calZ) {
        this.calZ = calZ;
    }

    public double getCalT() {
        return calT;
    }

    public void setCalT(double calT) {
        this.calT = calT;
    }

    public ImageHandler getImageRaw(int t) {
        String fileName = dirRaw + baseRaw + IJ.pad(t, padRaw) + ".tif";
        ImagePlus plus = IJ.openImage(fileName);
        if (plus == null) {
            IJ.log("No image " + fileName);
            return null;
        }

        return ImageHandler.wrap(plus);
    }
    
    public ImagePlus getImageRawPlus(int t) {
//        String fileName = dirRaw + baseRaw + IJ.pad(t, padRaw) + ".tif";
//        ImagePlus plus = IJ.openImage(fileName);
//        if (plus == null) {
//            IJ.log("No image " + fileName);
//            return null;
//        }
//
//        return ImageHandler.wrap(plus);
        
        //System.out.println("Opening seg " + t);
        String fileName = dirRaw + baseRaw + IJ.pad(0, padRaw) + ".tif";
        //System.out.println("Opening " + fileName);
        ImagePlus plus = IJ.openImage(fileName);
        if (plus == null) {
            System.out.println("No image " + fileName);
            return null;
        }
        
        Duplicator dup = new Duplicator();
        int[] dim = plus.getDimensions();
        ImagePlus timedup = dup.run(plus, 1, 1, 1, dim[3], t, t);

        return timedup;
    }

    public ImageHandler getImageSeg(int t) {
        //System.out.println("Opening seg " + t);
        String fileName = dirSeg + baseSeg + IJ.pad(t+firstSeg, padSeg) + ".tif";
        //System.out.println("Opening " + fileName);
        ImagePlus plus = IJ.openImage(fileName);
        if (plus == null) {
            System.out.println("No image " + fileName);
            return null;
        }
        
        Duplicator dup = new Duplicator();
        int[] dim = plus.getDimensions();
        ImagePlus timedup = dup.run(plus, 1, 1, 1, dim[3], t, t);

        return ImageHandler.wrap(timedup);
    }
    
    public ImagePlus getImageSegPlus(int t) {
        //System.out.println("Opening seg " + t);
        String fileName = dirSeg + baseSeg + IJ.pad(firstSeg, padSeg) + ".tif";
        //System.out.println("Opening " + fileName);
        ImagePlus plus = IJ.openImage(fileName);
        if (plus == null) {
            System.out.println("No image " + fileName);
            return null;
        }
        
        Duplicator dup = new Duplicator();
        int[] dim = plus.getDimensions();
        ImagePlus timedup = dup.run(plus, 1, 1, 1, dim[3], t, t);

        return timedup;
    }

}
