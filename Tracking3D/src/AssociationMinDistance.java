
public class AssociationMinDistance extends Associate {

	@Override
	public ObjectAction execute(ObjectAction object) {
		return new AssociatedObjectList();
	}


}
