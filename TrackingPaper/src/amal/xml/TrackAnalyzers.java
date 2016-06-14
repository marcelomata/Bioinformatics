package amal.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="TrackAnalyzers")
public class TrackAnalyzers {

	@XmlElement(name="Analyzer")
	private List<Analyzer> analyzers;
	
	public TrackAnalyzers(){
		analyzers = new ArrayList<Analyzer>();
	}
	
	public void setAnalyzers(){
		
		Analyzer analyser1 = new Analyzer();
		analyser1.setKey("Branching analyzer");
		analyzers.add(analyser1);
		
		Analyzer analyser2 = new Analyzer();
		analyser2.setKey("Track duration");
		analyzers.add(analyser2);
		
		Analyzer analyser3 = new Analyzer();
		analyser3.setKey("Track index");
		analyzers.add(analyser3);
		
		Analyzer analyser4 = new Analyzer();
		analyser4.setKey("Track location");
		analyzers.add(analyser4);
		
		Analyzer analyser5 = new Analyzer();
		analyser5.setKey("Velocity");
		analyzers.add(analyser5);
		
		Analyzer analyser6 = new Analyzer();
		analyser6.setKey("TRACK_SPOT_QUALITY");
		analyzers.add(analyser6);
		
	}
}
