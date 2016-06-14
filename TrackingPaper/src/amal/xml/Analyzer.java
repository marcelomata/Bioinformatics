package amal.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Analyzer")
public class Analyzer {
	
	@XmlAttribute(name="key")
	private String key;

	public Analyzer(){
		key="";
	}
	
	public void setKey(String str){
		key = str;
	}
}
