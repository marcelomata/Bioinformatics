package amal.xml;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="EdgeFeatures")
public class EdgeFeatures {
	
	@XmlElement(name="Feature")
	private List<Feature> list;

	public EdgeFeatures(){
		this.list=new ArrayList<Feature>();
	}

	
	public void setList(ArrayList<Feature> list){
		this.list=list;
	}
}