package amal.xml;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import amal.tracking.Edge;
import amal.tracking.LineageTree;
import amal.tracking.Spot;

@XmlRootElement
public class Track {
	
	@XmlAttribute(name="name")
	private String name;
	
	@XmlAttribute(name="NUMBER_SPOTS")
	private int nSpots = 0;
	
	@XmlAttribute(name="NUMBER_GAPS")
	private int nGaps = 0;
	
	@XmlAttribute(name="LONGEST_GAP")
	private int longestGap = 0;
	
	@XmlAttribute(name="NUMBER_SPLITS")
	private int nSplits = 0;
	
	@XmlAttribute(name="NUMBER_MERGES")
	private int nMerges = 0;
	
	@XmlAttribute(name="NUMBER_COMPLEX")
	private int nComplex = 0;
	
	@XmlAttribute(name="TRACK_DURATION")
	private double duration = 0;
	
	@XmlAttribute(name="TRACK_START")
	private double start = 0;
	
	@XmlAttribute(name="TRACK_STOP")
	private double stop = 0;

	@XmlAttribute(name="TRACK_DISPLACEMENT")
	private double displacement = 0;
	
	@XmlAttribute(name="TRACK_ID")
	private int TRACK_ID;

	@XmlAttribute(name="TRACK_INDEX")
	private int TRACK_INDEX;
	
	@XmlAttribute(name="TRACK_X_LOCATION")
	private double x = 0;
	
	@XmlAttribute(name="TRACK_Y_LOCATION")
	private double y = 0;
	
	@XmlAttribute(name="TRACK_Z_LOCATION")
	private double z = 0;
	
	@XmlAttribute(name="TRACK_MEAN_SPEED")
	private double meanSpeed = 0;
	
	@XmlAttribute(name="TRACK_MAX_SPEED")
	private double maxSpeed;
	
	@XmlAttribute(name="TRACK_MIN_SPEED")
	private double minSpeed = 0;
	
	@XmlAttribute(name="TRACK_MEDIAN_SPEED")
	private double medianSpeed = 0;
	
	@XmlAttribute(name="TRACK_STD_SPEED")
	private double stdSpeed = 0;
	
	@XmlAttribute(name="TRACK_MEAN_QUALITY")
	private double meanQ;
	
	@XmlAttribute(name="TRACK_MAX_QUALITY")
	private double maxQ = 0;
	
	@XmlAttribute(name="TRACK_MIN_QUALITY")
	private double minQ = 0;
	
	@XmlAttribute(name="TRACK_MEDIAN_QUALITY")
	private double medianQ = 0;
	
	@XmlAttribute(name="TRACK_STD_QUALITY")
	private double stdQ = 0;
	
	@XmlElement(name="Edge")
	private List<Edge> edges;
	
	
	public Track(){
		edges=new ArrayList<Edge>();
		TRACK_INDEX=-1;
		TRACK_ID=-1;
		name="";
	}

	public Track (LineageTree tree, Spot spot, int i) {
		setTrackIndex(i);
		setTrackID(i);
		setName();
		edges = new ArrayList<Edge>();
		addEdge(tree,spot);
		
	}
	
	private void addEdge(LineageTree tree, Spot spot){
	
		ArrayList<Spot> children = tree.getChildren(spot);
		
		
			if (children.size() == 2){
				nSplits++;
			}
			for (int i = 0; i < children.size(); i++){
				addEdge(tree.getGraph().getEdge(spot, children.get(i)));
				addEdge(tree, children.get(i));
	
			}
		
	}
	
	private void addEdge(Edge edge){
		edges.add(edge);
	}

	private void setTrackIndex(int i){
		TRACK_INDEX=i;
	}


	private void setTrackID(int i){
		TRACK_ID=i;
	}


	public void setName(){
		name="Track_"+TRACK_ID;
	}

	public List<Edge> getEdges(){
		return edges;
	}


	public int getID(){
		return this.TRACK_ID;
	}

	
	public boolean isEmpty(){
	return edges.isEmpty();
	}
	
	
}
