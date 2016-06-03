package trackingSPT.math;

import java.util.ArrayList;
import java.util.List;

import mcib3d.Jama.Matrix;
import trackingSPT.objects.TemporalObject;

public class CostMatrix {
	
	private Matrix costs;
	private List<TemporalObject> source;
	private List<TemporalObject> target;
	
	public CostMatrix(int m, int n) {
		this.source = new ArrayList<TemporalObject>();
		this.target = new ArrayList<TemporalObject>();
		this.costs = new Matrix(m, n);
	}
	
	public void addAssociation(TemporalObject source, TemporalObject target) {
		this.source.add(source);
		this.target.add(target);
	}
	
	public void setCost(int i, int j, double value) {
		this.costs.set(i, j, value);
	}

	
	
	

}
