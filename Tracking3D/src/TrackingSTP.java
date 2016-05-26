

public class TrackingSTP extends TrackingStrategy {
	
	@Override
	public void build() {
		super.addAction(new AssociationMinDistance());
		super.addAction(new EventSeekerExample());
		super.addAction(new HandlerExemple());
	}

}
