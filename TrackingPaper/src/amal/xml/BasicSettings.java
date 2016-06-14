package amal.xml;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="BasicSettings")
public class BasicSettings {

	@XmlAttribute(name="xstart")
	private int xstart;

	@XmlAttribute(name="xend")
	private int xend;

	@XmlAttribute(name="ystart")
	private int ystart;

	@XmlAttribute(name="yend")
	private int yend;

	@XmlAttribute(name="zstart")
	private int zstart;

	@XmlAttribute(name="zend")
	private int zend;

	@XmlAttribute(name="tstart")
	private int tstart;

	@XmlAttribute(name="tend")
	private int tend;

	public BasicSettings(){
		xstart=0;
		xend=0;
		ystart=0;
		yend=0;
		zstart=0;
		zend=0;
		tstart=0;
		tend=0;
	}
	
	public void setXStart(int s){
		xstart=s;
	}
	
	public void setXEnd(int e){
		xend=e;
	}
	
	public void setYStart(int s){
		ystart=s;
	}
	
	public void setYEnd(int e){
		yend=e;
	}
	
	public void setZStart(int s){
		zstart=s;
	}
	
	public void setZEnd(int e){
		zend=e;
	}
	
	public void setTStart(int s){
		tstart=s;
	}
	
	public void setTEnd(int e){
		tend=e;
	}
	

}
