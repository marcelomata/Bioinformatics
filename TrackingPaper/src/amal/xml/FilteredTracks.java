package amal.xml;


import java.util.ArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="FilteredTracks")
public class FilteredTracks {
	
	@XmlElement(name="TrackID")
	List<TrackID> tracks;
	
	
	AllTracks allTracks;

	public FilteredTracks(){
		tracks=new ArrayList<TrackID>();
		allTracks=new AllTracks();
	}

	
	public void setTracks(AllTracks allTracks){
		
		ArrayList<Track> tracks = (ArrayList<Track>) allTracks.getTracks();
		
		for (int i=0; i < tracks.size(); i++){
			if(!tracks.get(i).getEdges().isEmpty()){
			this.tracks.add(new TrackID(tracks.get(i).getID())); 
			}
		}
	}

	public List<TrackID> getTracks()
	{
		return tracks;
	}

	public void add( TrackID ID)
	{
		this.tracks.add(ID);
	}


}
