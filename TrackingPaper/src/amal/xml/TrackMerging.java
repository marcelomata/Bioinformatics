package amal.xml;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TrackMerging")
public class TrackMerging {

	@XmlElement(name="FeaturePenalties")
	private FeaturePenalties penalties;
	
	public TrackMerging() {
		penalties= new FeaturePenalties();
	}
}
