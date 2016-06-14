package amal.xml;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="View")
public class View {
	
	@XmlAttribute(name="key")
	private String key;

	public View(){
		key="";
	}

	
	public void setKey(String str){
		key=str;
	}
}
