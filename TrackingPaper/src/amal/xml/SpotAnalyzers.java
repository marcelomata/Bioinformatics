package amal.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="EdgeAnalyzers")
public class SpotAnalyzers {

	@XmlElement(name="Analyzer")
	private List<Analyzer> analyzers;
	
	public SpotAnalyzers(){
		analyzers = new ArrayList<Analyzer>();
	}
	
	public void setAnalyzers(){
		
		Analyzer analyser1 = new Analyzer();
		analyser1.setKey("MANUAL_SPOT_COLOR_ANALYZER");
		analyzers.add(analyser1);
		
		Analyzer analyser2 = new Analyzer();
		analyser2.setKey("Spot descriptive statistics");
		analyzers.add(analyser2);
		
		Analyzer analyser3 = new Analyzer();
		analyser3.setKey("Spot radius estimator"); 
		analyzers.add(analyser3);
		
		Analyzer analyser4 = new Analyzer();
		analyser4.setKey("Spot contrast and SNR");
		analyzers.add(analyser4);
	}
}
