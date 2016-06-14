package amal.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import amal.tracking.LineageTree;


@XmlRootElement(name="TrackMate")
public class TrackMate {

	@XmlAttribute(name="version")	
	private String version;

	@XmlElement(name="Model")
	private Model model;

	@XmlElement(name="Settings")
	private Settings settings;

	@XmlElement(name="GUIState")
	private GUIState guistate;


	public TrackMate(){
		this.version="";
		this.model=new Model();
		this.guistate = new GUIState();
		this.settings = new Settings();
	}


	public void setVersion(String str){
		this.version=str;
	}

	public void setSettings(String name, String path, int w, int h, int nz, 
			int nt, double pw, double ph, double vd, double ti){
		settings.setImData(name, path, w, h, nz, nt, pw, ph, vd, ti);
		settings.setBasic(0, w-1, 0, h-1, 0, nz-1, 0, nt-1);
		settings.setAnalyzerCollection();
	}

	public void setModel(LineageTree tree){
		model.setFeatures();
		model.setSpatialUnit("pixel");
		model.settimeUnit("frame");
		model.setAllSpots(tree);
		model.setAllTracks(tree);
		model.setFilteredTracks();
	}

	public void setGuiState(){
		guistate.setState("ConfigureViews");
		guistate.setView("HYPERSTACKDISPLAYER");
	}

	
}
