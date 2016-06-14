package amal.xml;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="ImageData")
public class ImageData {
	
	@XmlAttribute(name="filename")
	private String filename;
	
	@XmlAttribute(name="folder")
	private String folder;
	
	@XmlAttribute(name="width")
	private int width;
	
	@XmlAttribute(name="height")
	private int height;
	
	@XmlAttribute(name="nslices")
	private int nslices;
	
	@XmlAttribute(name="nframes")
	private int nframes;
	
	@XmlAttribute(name="pixelwidth")
	private double pixelwidth;
	
	@XmlAttribute(name="pixelheight")
	private double pixelheight;
	
	@XmlAttribute(name="voxeldepth")
	private double voxeldepth;
	
	@XmlAttribute(name="timeinterval")
	private double timeinterval;
	
	public ImageData(){
		filename = "";
		folder ="";
		width=0;
		height=0;
		nslices=0;
		nframes=0;
		pixelwidth=0;
		pixelheight=0;
		voxeldepth=0;
		timeinterval=0;
	}
	
	public void setName(String str){
		filename=str;
	}
	
	public void setPath(String str ){
		folder=str;
	}
	
	public void setWidth(int w){
		width=w;
	}

	public void setHeight(int h){
		height=h;
	}
	
	public void setNSlices(int n){
		nslices=n;
	}
	
	public void setNFrames(int n){
		nframes=n;
	}
	
	public void setPixelWidth(double w){
		pixelwidth=w;
	}
	
	public void setPixelHeight(double h){
		pixelheight=h;
	}
	
	public void setVoxelDepth(double d){
		voxeldepth=d;
	}
	
	public void setTimeInterval(double t){
		timeinterval = t;
	}
	
}
