package amal.xml;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="TrackerSettings")
public class TrackerSettings {
	
	@XmlElement(name = "Linking")
	private Linking link;
	
	@XmlElement(name = "GapClosing")
	private GapClosing gap;
	
	@XmlElement(name = "TrackSplitting")
	private TrackSplitting split;
	
	@XmlElement(name = "TrackMerging")
	private TrackMerging merge;
	
	public TrackerSettings(){
		link = new Linking();
		gap = new GapClosing();
		split = new TrackSplitting();
		merge = new TrackMerging();
	}
	
	

}
