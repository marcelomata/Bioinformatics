package trackingPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import amal.tracking.Node;
import amal.tracking.Spot;
import mcib3d.geom.Object3D;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingResult3DSPT;

public class GenerateChallengeFormat {
	
	private final List<Node<Spot>> roots;
	
	public GenerateChallengeFormat(TrackingResult3DSPT result) {
		this.roots = new ArrayList<Node<Spot>>();
		loadRoots(result);
	}
	
	private void loadRoots(TrackingResult3DSPT result) {
		Map<Integer, List<ObjectTree3D>> objects = result.getMotionField().getFinalResult();
		Set<Integer> objectKeys = objects.keySet();
		List<ObjectTree3D> objectsList;
		ObjectTree3D objectTree;
		Object3D obj;
		Node<Spot> node;
		Spot spot;
		int i = 0;
		
		for (Integer integer : objectKeys) {
			objectsList = objects.get(integer);
			i = 0;
			objectTree = objectsList.get(i);
			obj = objectTree.getObject();
			while(obj == null) {
				objectTree = objectsList.get(i);
				obj = objectTree.getObject();
				i++;
			}
			
			if(obj != null) {
				spot = new Spot(obj, integer, objectTree.getFrame(), 0, 1);
				node = new Node<Spot>(spot);
				roots.add(node);
				addChildren(objectTree, node);
			}
		}
	}
	
	private void addChildren(ObjectTree3D objectTree, Node<Spot> node) {
		Spot spot;
		Object3D obj;
		Node<Spot> child;
		List<ObjectTree3D> children = objectTree.getChildren();
		for (ObjectTree3D objectTree3D : children) {
			obj = objectTree3D.getObject();
			if(obj != null) {
				spot = new Spot(obj, objectTree.getFrame(), objectTree.getFrame(), 0, 1);
				child = new Node<Spot>(spot, node);
				node.addChild(child);
				addChildren(objectTree3D, child);
			}
		}
	}
	
	public List<Node<Spot>> getRoots() {
		return roots;
	}

}
