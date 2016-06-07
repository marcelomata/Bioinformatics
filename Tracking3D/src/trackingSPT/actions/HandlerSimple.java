package trackingSPT.actions;


import trackingInterface.ObjectAction;
import trackingSPT.enums.EventType;
import trackingSPT.objects.TrackingResultSPT;

public class HandlerSimple extends Handler {

	@Override
	public ObjectAction execute() {
		System.out.println("Association Events -> "+this.objectAction.getEventList(EventType.ASSOCIATION).size());
		System.out.println("Splitting Events -> "+this.objectAction.getEventList(EventType.SPLITTING).size());
		System.out.println("Merging Events -> "+this.objectAction.getEventList(EventType.MERGING).size());
		
		return new TrackingResultSPT();
	}

}
