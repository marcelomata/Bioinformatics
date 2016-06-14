package amal.xml;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TrackID {
	
	@XmlAttribute(name="TRACK_ID")
	private int TrackID;

	public TrackID(){
		TrackID=-1;
	}
	
	public TrackID(int id){
		TrackID=id;
	}



}
