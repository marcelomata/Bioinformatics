package trackingSPT.events.eventfinder.association;

import java.util.List;

import mcib3d.geom.Objects3DPopulation;
import trackingSPT.objects3D.ObjectTree3D;

public interface AssociationSeekerObj {

	Objects3DPopulation getObjectNextFrame();
	
	void addAssociation(ObjectTree3D source, ObjectTree3D target);
	
	void addAllLeftTargetObjects(List<ObjectTree3D> leftTargetObjects);
	
	void addAllLeftSourceObjects(List<ObjectTree3D> leftSourceObjects);
	
}
