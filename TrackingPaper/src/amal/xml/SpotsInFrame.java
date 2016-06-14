package amal.xml;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import amal.tracking.Spot;

@XmlRootElement(name="SpotsInFrame")
public class SpotsInFrame {
	
	@XmlElement(name="Spot")
	private List<Spot> spots;
	
	@XmlAttribute(name="frame")
	private int frame;
	
	public SpotsInFrame(){
		this.spots = new ArrayList<Spot>();
	}
	
	public SpotsInFrame(int i){
		this();
		setFrame(i);
	}
	
	public void addSpot(Spot spot){
		this.spots.add(spot);
	}
	
	public void setSpots(ArrayList<Spot> spots){
		this.spots = spots;
	}
	
	
	public void setFrame(int i){
		frame=i;
	}
	
	public ArrayList<Spot> spots(){
		return (ArrayList<Spot>) spots;
	}
}
