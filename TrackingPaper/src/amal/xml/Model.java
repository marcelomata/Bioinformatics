package amal.xml;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import amal.tracking.LineageTree;




@XmlRootElement(name="Model")
public class Model {
	
	@XmlAttribute(name="spatialunits")
	private String spatialUnit;
	
	@XmlAttribute(name="timeunits")
	private String timeUnit;
	
	@XmlElement(name="FeatureDeclarations")
	private FeatureDeclarations features;
	
	@XmlElement(name="AllSpots")
	private AllSpots spots;
	
	@XmlElement(name="AllTracks")
	private AllTracks tracks;
	
	@XmlElement(name="FilteredTracks")
	private FilteredTracks filteredTracks;
	
	


	public Model(){
		spatialUnit="";
		timeUnit="";
		features = new FeatureDeclarations();
		spots = new AllSpots();
		tracks = new AllTracks();
		filteredTracks = new FilteredTracks();
	
	}

	public void setSpatialUnit(String str){
		spatialUnit=str;
	}

	
	public void settimeUnit(String str){
		timeUnit=str;
	}
	
	
	public void setFeatures(){
		features.run();
	}
	
	
	public void setAllSpots(LineageTree tree){
		spots.setSpots(tree);
	}
	
	
	public void setAllTracks(LineageTree tree){
		this.tracks.setTracks(tree);
	}
	
	
	public void setFilteredTracks(){
		this.filteredTracks.setTracks(this.tracks);
		
	}
	


}
