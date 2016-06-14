package amal.xml;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import amal.tracking.LineageTree;


@XmlRootElement(name="AllTracks")
public class AllTracks {
	
	private List<Track> tracks;
	
	public AllTracks(){
		tracks=new ArrayList<Track>();
	}
	
	
	public void setTracks(LineageTree tree){
		for (int i=0; i < tree.getRoots().size(); i++){
			this.tracks.add(new Track(tree, tree.getRoots().get(i), i));
		}
	}
	
	@XmlElement(name="Track")
	public List<Track> getTracks(){
		return tracks;
	}

	
	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}
	
}
