import java.util.ArrayList;
import java.util.List;

public class Graph {
	
	private List<Node> nodes;
	private List<Edge> edges;
	
	public Graph() {
		this.nodes = new ArrayList<Node>();
		this.edges = new ArrayList<Edge>();
	}
	
	public void addNewNode(Node n) {
		this.nodes.add(n);
	}
	
	public void addAdjacentNode(Node source, Node target) {
		addAdjacentNode(source, target, 0);
	}
	
	public void addAdjacentNode(Node source, Node target, int flow) {
		addAdjacentNode(source, target, flow, 0.0);
	}
	
	public void addAdjacentNode(Node source, Node target, int flow, double cost) {
		int index = this.nodes.indexOf(source);
		if(index >= 0) {
			this.nodes.get(index).addAdjacent(target);
			this.edges.add(new Edge(source, target, flow, cost));
		}
	}

}
