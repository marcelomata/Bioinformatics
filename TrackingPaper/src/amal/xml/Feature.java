package amal.xml;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Feature")
public class Feature {
	@XmlAttribute(name="feature")
	private String feature;
	
	@XmlAttribute(name="name")
	private String name;
	
	@XmlAttribute(name="shortname")
	private String shortname;
	
	@XmlAttribute(name="dimension")
	private String dimension;
	
	@XmlAttribute(name="isint")
	private boolean isint;
	
	public Feature(){
		this.feature ="";
		this.name="";
		this.shortname = "";
		this.dimension = "";
		this.isint=false;
	}
	
	
	public void setFeature(String feature){
		this.feature=feature;
	}
	
	
	public void setName(String name){
		this.name=name;
	}
	
	
	public void setShortName(String shortname){
		this.shortname=shortname;
	}
	
	
	public void setDimension(String dimension){
		this.dimension=dimension;
	}
	
	
	public void setIsInt(boolean isint){
		this.isint=isint;
	}
}
