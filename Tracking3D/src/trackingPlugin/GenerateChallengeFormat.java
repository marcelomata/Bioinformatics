package trackingPlugin;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import amal.tracking.Node;
import amal.tracking.Spot;
import amal.tracking.Tracking;
import mcib3d.geom.Object3D;
import trackingSPT.objects3D.ObjectTree3D;
import trackingSPT.objects3D.TrackingResult3DSPT;

public class GenerateChallengeFormat {
	
	private final List<Node<Spot>> roots;
	private final TrackingResult3DSPT result;
	
	public GenerateChallengeFormat(TrackingResult3DSPT result) {
		this.roots = new ArrayList<Node<Spot>>();
		this.result = result;
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
	
	public void computeColorChallenge(int start) {
        ArrayList<Node<Spot>> tmp = new ArrayList<Node<Spot>>();
        for (Node<Spot> root : roots) {
            tmp.add(root);
        }

        int c = start;

        while (!tmp.isEmpty()) {
            ArrayList<Node<Spot>> tmp2 = new ArrayList<Node<Spot>>();
            for (Node<Spot> node : tmp) {
                propagateColorToDescendant(node, c);
                c++;
                Node<Spot> desc = node.getLastDescendant();
                ArrayList<Node<Spot>> children = desc.getChildren();
                if (!children.isEmpty()) {
                    // System.out.println("child1=" + children.get(0) + " child2=" + children.get(1));
                    tmp2.add(children.get(0));
                    tmp2.add(children.get(1));
                }
            }
            tmp = tmp2;
        }
    }
	
	private void propagateColorToDescendant(Node<Spot> node, int color) {
        ArrayList<Node<Spot>> children = node.getNodesToDescendant();

        for (Node<Spot> child : children) {
            child.getData().setObjectColor(color);
        }
    }
	
	public void challengeFormat(String filename, int minLife) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(filename);
            System.out.println("Analysing splitting events...");
            ArrayList<Node<Spot>> tmp = new ArrayList<Node<Spot>>();
            for (Node<Spot> root : roots) {
                tmp.add(root);
            }
            while (!tmp.isEmpty()) {
                ArrayList<Node<Spot>> tmp2 = new ArrayList<Node<Spot>>();
                for (Node<Spot> root : tmp) {
                    Node<Spot> desc = root.getLastDescendant();
                    //pw.println("");
                    //System.out.println("root=" + root + " desc=" + desc);
                    Node<Spot> parent = root.getParent();
                    int parentlabel;
                    if (parent != null) {
                        parentlabel = root.getParent().getData().getObjectColor();
                    } else {
                        parentlabel = 0;
                    }
                    int total = desc.getData().getFrame() - root.getData().getFrame();
                    // case nb frames not reached yet 
                    if (desc.getData().getFrame() >= result.getNumberOfFrames() - 1) {
                        total = minLife + 1;
                    }
                    if (total >= minLife) {
                        pw.println((root.getData().getObjectColor()) + " " + root.getData().getFrame() + " " + desc.getData().getFrame() + " " + parentlabel);
                    }
                    ArrayList<Node<Spot>> children = desc.getChildren();
                    if (!children.isEmpty()) {
                        // System.out.println("child1=" + children.get(0) + " child2=" + children.get(1));
                        tmp2.add(children.get(0));
                        tmp2.add(children.get(1));
                    }
                }
                tmp = tmp2;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tracking.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

}
