package amal.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="InitialSpotFilter")
public class InitialSpotFilter {
	
	@XmlAttribute(name="feature")
	private String feature;
	
	@XmlAttribute(name="value")
	private double value;

	@XmlAttribute(name="isabove")
	private boolean isabove;
	
	
	public InitialSpotFilter() {
		feature = "QUALITY";
		value = 0;
		isabove = true;
	}
	
}
