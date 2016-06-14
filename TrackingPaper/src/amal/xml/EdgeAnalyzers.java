package amal.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="EdgeAnalyzers")
public class EdgeAnalyzers {

	@XmlElement(name="Analyzer")
	private List<Analyzer> analyzers;
	
	public EdgeAnalyzers(){
		analyzers = new ArrayList<Analyzer>();
	}
	
	public void setAnalyzers(){
		
		Analyzer analyser1 = new Analyzer();
		analyser1.setKey("Edge target");
		analyzers.add(analyser1);
		
		Analyzer analyser2 = new Analyzer();
		analyser2.setKey("Edge mean location");
		analyzers.add(analyser2);
		
		Analyzer analyser3 = new Analyzer();
		analyser3.setKey("Edge velocity");
		analyzers.add(analyser3);
		
		Analyzer analyser4 = new Analyzer();
		analyser4.setKey("MANUAL_EDGE_COLOR_ANALYZER");
		analyzers.add(analyser4);
	}
}
