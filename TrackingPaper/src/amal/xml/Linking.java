package amal.xml;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Linking")
public class Linking {
	
	@XmlElement(name="FeaturePenalties")
	private FeaturePenalties penalties;
	
	public Linking() {
		penalties= new FeaturePenalties();
	}
}
