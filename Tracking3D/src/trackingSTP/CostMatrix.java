package trackingSTP;

import java.util.List;

import mcib3d.Jama.Matrix;
import mcib3d.geom.Object3D;

public class CostMatrix {
	
	private Matrix costs;
	private List<Object3D> source;
	private List<Object3D> target;
	
	public CostMatrix(List<Object3D> source, List<Object3D> target) {
		this.source = source;
		this.target = target;
		this.costs = new Matrix(source.size(), target.size());
		calculateCostMatrix();
	}

	private void calculateCostMatrix() {
		for (int i = 0; i < source.size(); i++) {
			
		}
	}
	
	

}
