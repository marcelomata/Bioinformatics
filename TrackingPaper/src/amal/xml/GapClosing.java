package amal.xml;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GapClosing")
public class GapClosing {

	@XmlElement(name="FeaturePenalties")
	private FeaturePenalties penalties;
	
	public GapClosing(){
		penalties= new FeaturePenalties();
	}
}
