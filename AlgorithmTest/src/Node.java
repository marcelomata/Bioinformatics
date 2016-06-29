import java.util.ArrayList;
import java.util.List;

public class Node {
	
	private List<Node> adjacents;
	
	public Node() {
		this.adjacents = new ArrayList<Node>();
	}

	public void addAdjacent(Node target) {
		this.adjacents.add(target);
	}

}
