package trackingSPT.math;

import mcib3d.Jama.Matrix;
import trackingSPT.objects.ObjectTree;

public class CostMatrix {
	
	private Matrix costs;
	private ObjectTree[] source;
	private ObjectTree[] target;
	
	public CostMatrix(int m, int n) {
		this.source = new ObjectTree[m];
		this.target = new ObjectTree[n];
		this.costs = new Matrix(m, n);
	}
	
	public void addObjectSource(ObjectTree source, int i) {
		this.source[i] = source;
	}
	
	public void addObjectTarget(ObjectTree target, int j) {
		this.target[j] = target;
	}
	
	public void setCost(int i, int j, double value) {
		this.costs.set(i, j, value);
	}

	public double[][] getCosts() {
		return costs.getArray();
	}
	
	public ObjectTree getSource(int i) {
		return source[i];
	}
	
	public ObjectTree getTarget(int j) {
		return target[j];
	}

//	public void printDistancesTarget(ObjectTree objectTree) {   //TODO
//		ObjectTree t;
//		for (int j = 0; j < target.length; j++) {
//			t = target[j];
//			if(t == objectTree) {
//				double [][]c = costs.getArray();
//				System.out.print("\n\n->");
//				for (int i = 0; i < c.length; i++) {
//					System.out.print(c[i][j]+"\n ");
//				}
//			}
//		}
//		System.out.println("#####TARGETS#####");
//	}
//	
//	public void printDistancesSource(ObjectTree objectTree) {
//		ObjectTree t;
//		for (int j = 0; j < source.length; j++) {
//			t = source[j];
//			if(t == objectTree) {
//				double [][]c = costs.getArray();
//				System.out.print("\n\n->");
//				for (int i = 0; i < c.length; i++) {
//					System.out.print(c[j][i]+"\n ");
//				}
//			}
//		}
//		System.out.println("#####SOURCES#####");
//	}

}
