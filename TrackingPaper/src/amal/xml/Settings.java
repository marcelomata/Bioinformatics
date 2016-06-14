package amal.xml;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="Settings")
public class Settings {
	
	@XmlElement(name="ImageData")
	private ImageData imData;
	
	@XmlElement(name="BasicSettings")
	private BasicSettings basic;
	
	@XmlElement(name="DetectorSettings")
	private DetectorSettings detector;
	
	@XmlElement(name="InitialSpotFilter")
	private InitialSpotFilter filter;
	
	@XmlElement(name="SpotFilterCollection")
	private SpotFilterCollection spotCollection;
	
	@XmlElement(name="TrackerSettings")
	private TrackerSettings tracker;
	
	@XmlElement(name="TrackFilterCollection")
	private TrackFilterCollection trackCollection;
	
	@XmlElement(name="AnalyzerCollection")
	private AnalyzerCollection analyzerCollection;
	
	
	public Settings(){
		imData = new ImageData();
		basic = new BasicSettings();
		detector = new DetectorSettings();
		filter = new InitialSpotFilter();
		spotCollection = new SpotFilterCollection();
		tracker = new TrackerSettings();
		trackCollection = new TrackFilterCollection();
		analyzerCollection = new AnalyzerCollection();
	}
	
	public void setImData(String name, String path, int w, int h, int nz, 
			int nt, double pw, double ph, double vd, double ti){
		imData.setName(name);
		imData.setPath(path);
		imData.setWidth(w);
		imData.setHeight(h);
		imData.setNSlices(nz);
		imData.setNFrames(nt);
		imData.setPixelWidth(pw);
		imData.setPixelHeight(ph);
		imData.setVoxelDepth(vd);
		imData.setTimeInterval(ti);
	}
	
	public void setBasic(int xs, int xe, int ys, int ye, int zs, int ze, 
			int ts, int te){
		basic.setXStart(xs);
		basic.setXEnd(xe);
		basic.setYStart(ys);
		basic.setYEnd(ye);
		basic.setZStart(zs);
		basic.setZEnd(ze);
		basic.setTStart(ts);
		basic.setTEnd(te);
	}
	
	public void setAnalyzerCollection(){
		analyzerCollection.setAnalyzers();
	}
	
}
