package amal.xml;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import amal.tracking.LineageTree;
import amal.tracking.Spot;


@XmlRootElement(name="AllSpots")
public class AllSpots {

	@XmlAttribute(name="nspots")
	private int nspots;

	@XmlElement(name="SpotsInFrame")
	List<SpotsInFrame> list;


	public AllSpots(){
		list=new ArrayList<SpotsInFrame>();
		nspots=0;
	}

	private void setSpots(LineageTree tree, Spot spot){
		list.get(spot.getFrame()).addSpot(spot);
		nspots++ ;
		ArrayList<Spot> children = tree.getChildren(spot);
		if( children.size() != 0){
			for (int i = 0; i < children.size(); i++){
			
				setSpots(tree, children.get(i));
			}
		}
		
	}

	public void setSpots(LineageTree tree){
	
		for(int i = 0; i < tree.getLevel() + 1; i++){
			list.add(new SpotsInFrame(i));
		}
		
		for (int i = 0 ; i < tree.getRoots().size(); i++){
			this.setSpots(tree, tree.getRoots().get(i));
		}
		
	}

	
	public ArrayList<SpotsInFrame> spots(){
		return (ArrayList<SpotsInFrame>) list;
	}
}
