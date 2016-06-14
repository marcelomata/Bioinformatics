package amal.tracking;

import mcib3d.geom.Object3D;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * The Spot class models the cell detected in a frame. It stores its location,
 * ID and many other features required to generate a XML file that TrackMate can
 * open.
 * <p>
 * Spots are the vertices of the graph modelling the lineage tree. They are
 * assigned a state: default, merging, cloned or fake. Spots are in generally
 * default. When the Hungarian algorithm selects an association leading two
 * spots to merge together, the resulting spot is assigned to a merging state.
 * To preserve the tree structure of the graph, a merging spot is duplicated:
 * the obtained spot's state is set to cloned. Once the links are made, the
 * algorithm performs a verification step aiming to detect erroneous
 * associations. To do so, it adds sometimes spots that are not detected in the
 * frames. These spots are therefore assigned the temporary state fake. Once the
 * algorithm finishes the checking step, it will decide whether to keep these
 * spots and consider them as default or delete them.
 *
 * @author Amal Tiss
 */
@XmlRootElement(name = "Spot")
public class Spot {
    // The possible states of the spot
    public static final int DEFAULT = 0; // default value for all the detected spots
    public static final int MERGING = 1; // for merging spots
    public static final int CLONED = 2; // when a merging spot is duplicated
    public static final int FAKE = 3; // when the algorithm creates a spot. A spot suspected
    // TB
    private Object3D object3D;
    // The ID is unique for every spot
    private int ID;
    // Every spot is assigned a name (used in TrackMate)
    @XmlAttribute(name = "name")
    private String name;
    // not computed in this version of the algorithm
    // TODO add a method to compute this feature
    // Need for the original stack
    @XmlAttribute(name = "MAX_INTENSITY")
    private double maxI = 0;
    // I think it needs to be set to 1 for TrackMate to consider the spot
    // TODO check ...
    @XmlAttribute(name = "VISIBILITY")
    private int visibility;
    // not computed in this version of the algorithm
    // TODO add a method to compute this feature
    // Need for the original stack
    @XmlAttribute(name = "SNR")
    private double snr = 0;
    // not considered in this version of the algorithm
    // TODO check the utility of this feature in TrackMate and set it accordingly
    @XmlAttribute(name = "MANUAL_COLOR")
    private int color = 0;
    // the moment where the spot has been detected
    private double posT;
    // not computed in this version of the algorithm
    // TODO add a method to compute this feature
    // Need for the original stack
    @XmlAttribute(name = "MEDIAN_INTENSITY")
    private double medianI = 0;

    // The position of the spot along the z axis
    private double posZ;

    // not computed in this version of the algorithm
    // TODO add a method to compute this feature
    // Need for the original stack
    @XmlAttribute(name = "MEAN_INTENSITY")
    private double meanI = 0;

    // The position of the spot along the y axis
    private double posY;

    // The radius of the spot detected
    private double radius;

    // the index of the frame containing this spot (starts from 0)
    private int frame;

    // The value of the object modelled by this spot
    private int objectValue = -1;
    private int objectColor;

    // not computed in this version of the algorithm
    // TODO add a method to compute this feature
    // Need for the original stack
    @XmlAttribute(name = "TOTAL_INTENSITY")
    private double totalI = 0;

    // not computed in this version of the algorithm
    // TODO add a method to compute this feature
    // Need for the original stack (may be)
    @XmlAttribute(name = "STANDARD_DEVIATION")
    private double std = 0;

    // The position of the spot along the x axis
    private double posX;

    // not computed in this version of the algorithm
    // TODO add a method to compute this feature
    // Need for the original stack
    @XmlAttribute(name = "MIN_INTENSITY")
    private double minI = 0;

    // not computed in this version of the algorithm
    // TODO add a method to compute this feature
    // Need for the original stack
    @XmlAttribute(name = "CONTRAST")
    private double contrast = 0;

    // not considered in this version of the algorithm
    // TODO check the utility of this feature in TrackMate and set it accordingly
    @XmlAttribute(name = "QUALITY")
    private double quality = 0;

    // not considered in this version of the algorithm
    // TODO check the utility of this feature in TrackMate and set it accordingly
    @XmlAttribute(name = "ESTIMATED_DIAMETER")
    private double estimatedD = 0;

    // Geometric features
    private double area;
    private double compactness;
    private double elongation;
    private double flatness;
    private double sphericity;
    private double volume;

    // the number of frames where the spot has been tracked 
    private int age;

    // To link the spot to another one in the next frame, a search radius around it is 
    // defined to prevent associations with spots too far.
    // searchRadius1 refers to the motion-based search radius
    private double searchRadius1 = 0;
    // searchRadius2 refers to the local-density-based search radius
    private double searchRadius2 = 0;

    // The mean value of the cell's displacements from its initial position when it was 
    // first detected and its current position
    private double meanDisplacement = 0;
    // the standard deviation value of the cell's displacements from its initial position 
    // when it was first detected and its current position --> used to compute searchRadius1
    private double standardDeviationDisplacement = 0;

    // The distance of the spot to its closest neighbour in the frame --> used to compute 
    // searchRadius2
    private double distanceToClosestObjectInFrame;
    // the value of this spot state
    private int state;
    // Tests if the spot is the direct daughter of a splitting cell
    // If so, the cell can not split again. Any linking leading this spot to be a splitting
    // spot is therefore rejected.
    // TODO instead of using a boolean, consider a period (number of frames) after a
    // splitting event where the spots can not be divided
    private boolean resultOfSplitting;

    /**
     * Constructor Necessary to generate the XML file
     */
    public Spot() {

    }

    /**
     * Constructor
     *
     * @param obj:          the cell detected in the frame is an Object3D
     * @param i:            the spot's ID
     * @param j:            the index of the frame where the cell was detected (starts with
     *                      0)
     * @param dist:         the distance from the Object3D @param obj to its nearest
     *                      neighbour in the frame containing them
     * @param timeInterval: the time in minutes between two consecutive frames
     */
    public Spot(Object3D obj, int i, int j, double dist, double timeInterval) {
        object3D = obj;
        this.ID = i;
        this.name = "ID" + this.ID;
        this.frame = j;
        this.visibility = 1;
        this.posT = this.frame * timeInterval;
        setState(DEFAULT);
        this.resultOfSplitting = false;
        // When a spot is detected, its age is set to 1
        // The age must be update after the linking step
        setAge(1);
        // The geometric features are computed using the Object3D
        // I chose not to keep it as a field in the class Spot to save memory space
        // To compute a new geometric feature, one must add the module in this class
        //obj.getBoundingBox();
        setPosX(obj);
        setPosY(obj);
        setPosZ(obj);
        setRadius(obj);

        area = obj.getAreaPixels();
        compactness = obj.getCompactness();
        elongation = obj.getMainElongation();
        flatness = obj.getMedianElongation();
        sphericity = obj.getSphericity();
        volume = obj.getVolumePixels();

        this.objectValue = obj.getValue();

        // This field requires additional information about the frame, that's why the
        // distance is previously computed and is an entry to the constructor
        this.distanceToClosestObjectInFrame = dist;

    }
    // to be a missed detection is also set to FAKE.

    /**
     * Constructor Used to add spots to the tree even if they were not detected
     * in the frames. Generally, these spots are copies of existing ones (for
     * example, the clone of a merging spot).
     *
     * @param spot: the spot to be copied
     * @param i:    the new spot's ID
     */
    public Spot(Spot spot, int i) {

        this.ID = i;
        this.name = "ID" + this.ID;
        this.visibility = 1;

        // Copy some of the spot's fields
        this.posX = spot.getPosX();
        this.posY = spot.getPosY();
        this.posZ = spot.getPosZ();
        this.radius = spot.getRadius();
        this.area = spot.area();
        this.compactness = spot.compactness();
        this.elongation = spot.elongation();
        this.flatness = spot.flatness();
        this.sphericity = spot.sphericity();
        this.volume = spot.volume();
        this.distanceToClosestObjectInFrame = spot.distanceToClosestObjectInFrame();
        this.meanDisplacement = spot.meanDisplacement();
        this.standardDeviationDisplacement = spot.standardDeviationDisplacement();
        this.searchRadius2 = spot.searchRadius2();
        this.setSearchRadius1();

    }

    public void setMaxI(double maxI) {
        this.maxI = maxI;
    }

    public void setMedianI(double medianI) {
        this.medianI = medianI;
    }

    public void setMeanI(double meanI) {
        this.meanI = meanI;
    }

    public void setMinI(double minI) {
        this.minI = minI;
    }

    public Object3D getObject3D() {
        return object3D;
    }

    public void setObject3D(Object3D object3D) {
        this.object3D = object3D;
    }

    /**
     * @return the spot's ID
     */
    @XmlAttribute(name = "ID")
    public int getID() {
        return ID;
    }

    /**
     * @return the index of the frame containing this spot
     */
    @XmlAttribute(name = "FRAME")
    public int getFrame() {
        return frame;
    }

    /**
     * Sets the spot's frame to the entered value
     *
     * @param i: the frame containing this spot
     */
    public void setFrame(int i) {
        this.frame = i;
    }

    /**
     * Sets the spot's value
     *
     * @param i: the new value of this spot
     */
    public void value(int v) {
        this.objectValue = v;
    }

    /**
     * @return the value of the object modelled by this spot
     */
    public int value() {
        return this.objectValue;
    }

    /**
     * @return the position of the spot along the x axis
     */
    @XmlAttribute(name = "POSITION_X")
    public double getPosX() {
        return posX;
    }

    /**
     * Computes the position of the spot along the x axis using the 3D object
     *
     * @param obj: the 3D object detected in the frame
     */
    private void setPosX(Object3D obj) {
        posX = obj.getCenterAsVectorUnit().x;
    }

    /**
     * @return the position of the spot along the y axis
     */
    @XmlAttribute(name = "POSITION_Y")
    public double getPosY() {
        return posY;
    }

    /**
     * Computes the position of the spot along the y axis using the 3D object
     *
     * @param obj: the 3D object detected in the frame
     */
    private void setPosY(Object3D obj) {
        posY = obj.getCenterAsVectorUnit().y;
        //posY=obj.getCenterY();
    }

    public void setPosition(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;
    }

    /**
     * @return the position of the spot along the z axis
     */
    @XmlAttribute(name = "POSITION_Z")
    public double getPosZ() {
        return posZ;
    }

    /**
     * Computes the position of the spot along the z axis using the 3D object
     *
     * @param obj: the 3D object detected in the frame
     */
    private void setPosZ(Object3D obj) {
        posZ = obj.getCenterAsVectorUnit().z;
        //posZ=obj.getCenterZ();
    }

    /**
     * @return the moment where the spot has been detected
     */
    @XmlAttribute(name = "POSITION_T")
    public double getPosT() {
        return posT;
    }

    /**
     * Computes the position of the spot in time using the parameter time
     * interval and the index of the frame containing this spot
     *
     * @param timeInterval: the time (in minutes) separating two consecutive
     *                      frames
     */
    public void setPosT(double timeInterval) {
        this.posT = this.frame * timeInterval;
    }

    /**
     * @return the spot's radius
     */
    @XmlAttribute(name = "RADIUS")
    public double getRadius() {
        return radius;
    }

    /**
     * Computes the radius of the spot using the 3D object
     *
     * @param obj: the 3D object detected in the frame
     */
    private void setRadius(Object3D obj) {
        // the radius is the mean value of the distances from the centre of the object to
        // the points forming its border
        this.radius = obj.getDistCenterMean();

        // If the radius is not a number (case center not in object), it is computed using the spot's bounding box
        if (Double.isNaN(this.radius)) {

            double x = obj.getXmax() - obj.getXmin();
            double y = obj.getYmax() - obj.getYmin();
            double z = obj.getZmax() - obj.getZmin();

            x = x * obj.getResXY() / 2;
            y = y * obj.getResXY() / 2;
            z = z * obj.getResZ() / 2;

            this.radius = 0.95 * Math.max(x, Math.max(y, z));
        }
    }

    /**
     * @return the spot's area
     */
    public double area() {
        return area;
    }

    /**
     * @return the spot's compactness
     */
    public double compactness() {
        return compactness;
    }

    /**
     * @return the spot's elongation
     */
    public double elongation() {
        return elongation;
    }

    /**
     * @return the spot's flatness
     */
    public double flatness() {
        return flatness;
    }

    /**
     * @return the spot's sphericity
     */
    public double sphericity() {
        return sphericity;
    }

    /**
     * @return the spot's volume
     */
    public double volume() {
        return volume;
    }

    /**
     * @return the spot's age
     */
    public int getAge() {
        return age;
    }

    /**
     * Changes the age of the spot to the entered value
     *
     * @param age: the new age of the spot
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return the distance from the spot to its nearest neighbour in their
     * frame
     */
    public double distanceToClosestObjectInFrame() {
        return distanceToClosestObjectInFrame;
    }

    /**
     * Computes the motion-based search radius
     */
    public void setSearchRadius1() {
        // This method can be adapted to take into consideration prior knowledge related to 
        // the cells' movement
        // TODO add a module to predict the future position of the cell to estimate
        // the search radius (may be using Kalman filter)
        this.searchRadius1 = 3 * standardDeviationDisplacement;
    }

    /**
     * @return the spot's motion-based search radius
     */
    public double searchRadius1() {
        return this.searchRadius1;
    }

    /**
     * Sets the spot's local-density-based search radius to the entered value
     *
     * @param searchRadius: the computed local-density-based search radius
     */
    public void setSearchRadius2(double searchRadius) {
        this.searchRadius2 = searchRadius;
    }

    /**
     * @return the spot's local-density-based search radius
     */
    public double searchRadius2() {
        return this.searchRadius2;
    }

    /**
     * Sets the spot's mean displacement to the entered value
     *
     * @param mean: the computed mean displacement
     */
    public void setMeanDisplacement(double mean) {
        this.meanDisplacement = mean;
    }

    /**
     * @return the spot's mean displacement
     */
    public double meanDisplacement() {
        return this.meanDisplacement;
    }

    /**
     * Sets the spot's displacements' standard deviation to the entered value
     *
     * @param sigma: the computed displacements' standard deviation
     */
    public void setStandardDeviationDisplacement(double sigma) {
        this.standardDeviationDisplacement = sigma;
    }

    /**
     * @return the spot's displacements' standard deviation
     */
    public double standardDeviationDisplacement() {
        return this.standardDeviationDisplacement;
    }

    /**
     * @return the spot's state
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the spot's state to the entered value
     *
     * @param s: the new state
     */
    public void setState(int s) {
        state = s;
    }

    /**
     * @return true if the spot is the direct daughter of a splitting cell
     */
    public boolean isResultOfSplitting() {
        return this.resultOfSplitting;
    }

    /**
     * Sets the result of splitting flag to the entered value
     *
     * @param flag : the new value of the result of splitting flag
     */
    public void setResultOfSplitting(boolean flag) {
        this.resultOfSplitting = flag;
    }

    /**
     * @return the value of the object modelled by this spot
     */
    public int getObjectColor() {
        return this.objectColor;
    }

    public void setObjectColor(int color) {
        this.objectColor = color;
    }

    @Override
    public String toString() {
        NumberFormat numberFormat = new DecimalFormat();
        numberFormat.setMaximumFractionDigits(2);
        String res;
        if (getState() == CLONED) res = "C-";
        else if (getState() == FAKE) res = "F-";
        else res = "";
        res = res.concat("Spot{" + "ID=" + ID + ", val=" + objectValue + ", X=" + numberFormat.format(posX) + ", Y=" + numberFormat.format(posY) + ", Z=" + numberFormat.format(posZ) + ", T=" + frame + ", col= " + objectColor + ", rad=" + numberFormat.format(radius) + "}");
        return res;
    }

}
