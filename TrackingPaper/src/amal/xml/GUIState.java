package amal.xml;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="GUIState")
public class GUIState {

	@XmlElement(name="View")
	private View view;

	@XmlAttribute(name="state")
	private String state; 

	public GUIState(){
		view = new View();
		state="";
	}


	public void setState(String str){
		state = str;
	}


	public void setView(String str){
		view.setKey(str);;
	}

}
