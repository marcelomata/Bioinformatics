package trackingSPT.math;

import mcib3d.geom.Point3D;
import trackingSPT.objects3D.ObjectTree3D;

public class FunctionBorderDistance implements FunctionCalcObjectRelation {
	
	@Override
	public double calculate(ObjectTree3D obj1, ObjectTree3D obj2) {
		Point3D pObj2 = obj2.getObject().getCenterAsPoint();
		double xMax = pObj2.getX();
		double yMax = pObj2.getY();
//		double zMax = pObj2.getZ();
		Point3D pObj1 = obj1.getObject().getCenterAsPoint();
		double distanceX = Math.min(pObj1.getX(), xMax - pObj1.getX());
		double distanceY = Math.min(pObj1.getY(), yMax - pObj1.getY());
//		double distanceZ = Math.min(pObj1.getZ(), zMax - pObj1.getZ());
		double distance = Math.min(distanceX, distanceY);
//		distance = Math.min(distance, distanceZ);
		return distance;
	}

}
