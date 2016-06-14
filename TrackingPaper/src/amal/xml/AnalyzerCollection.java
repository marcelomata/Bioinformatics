package amal.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="AnalyzerCollection")
public class AnalyzerCollection {
	
	@XmlElement(name="SpotAnalyzers")
	private SpotAnalyzers spotAnalyzers;
	
	@XmlElement(name="EdgeAnalyzers")
	private EdgeAnalyzers edgeAnalyzers;
	
	@XmlElement(name="TrackAnalyzers")
	private TrackAnalyzers trackAnalyzers;
	
	public AnalyzerCollection(){
		edgeAnalyzers = new EdgeAnalyzers();
		trackAnalyzers = new TrackAnalyzers();
		spotAnalyzers = new SpotAnalyzers();
	}
	
	public void setAnalyzers(){
		edgeAnalyzers.setAnalyzers();
		trackAnalyzers.setAnalyzers();
		spotAnalyzers.setAnalyzers();
	}
}