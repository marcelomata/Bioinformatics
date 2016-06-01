package trackingSTP.math;

import java.util.ArrayList;
import java.util.List;

import mcib3d.Jama.Matrix;
import mcib3d.geom.Object3D;

public class CostMatrix {
	
	private Matrix costs;
	private List<Object3D> source;
	private List<Object3D> target;
	
	public CostMatrix() {
		this.source = new ArrayList<Object3D>();
		this.target = new ArrayList<Object3D>();
		this.costs = new Matrix(source.size(), target.size());
	}
	
	public void addAssociation(Object3D source, Object3D target) {
		this.source.add(source);
		this.target.add(target);
	}
	
	public void setCost(int i, int j, double value) {
		this.costs.set(i, j, value);
	}

	
	
	

}
