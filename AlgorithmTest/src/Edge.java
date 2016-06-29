
public class Edge {
	
	private double cost;
	private int flow;
	private Node source;
	private Node target;
	
	public Edge(Node source, Node target, int flow, double cost) {
		this.source = source;
		this.target = target;
		this.flow = flow;
		this.cost = cost;
	}

}
