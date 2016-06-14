package amal.xml;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TrackSplitting")
public class TrackSplitting {
	
	@XmlElement(name="FeaturePenalties")
	private FeaturePenalties penalties;
	
	public TrackSplitting() {
		penalties= new FeaturePenalties();
	}
}
