package trackingSPT;


import trackingInterface.TrackingStrategy;
import trackingSPT.actions.HandlerSimple;
import trackingSPT.actions.eventsfinder.EventSeekerStrategyAction;
import trackingSPT.objects.MovieObjectAction;
import trackingSPT.objects.ObjectActionSPT4D;
import trackingSPT.objects.TemporalPopulation;
import trackingSPT.objects.TrackingResultSPT;
import trackingSPT.objects.events.EventMap;

public class TrackingSPT extends TrackingStrategy {
	
	private EventMap eventList;
	private TemporalPopulation object3DToAssociate;
	protected ObjectActionSPT4D inObject;
	
	public TrackingSPT(ObjectActionSPT4D inObject) {
		super(inObject);
	}

	@Override
	public void build() {
		addTrackingAction(new EventSeekerStrategyAction(object3DToAssociate, eventList));
		addTrackingAction(new HandlerSimple(eventList));
	}

	@Override
	public void run() {
		TrackingAction current;
		
		while(inObject.getFrameTime() < inObject.getSize()) {
			System.out.println("Current Frame -> "+inObject.getFrameTime());
			object3DToAssociate = inObject.getAssociationLastResult(result);
			current = (TrackingAction) nextAction();
			current.execute();
			
			current = (TrackingAction) nextAction();
			current.execute();
			inObject.nextFrame();
			System.out.println("################################");
		}
	}

	@Override
	public void init(MovieObjectAction movie) {
		this.eventList = new EventMap();
		this.inObject = (ObjectActionSPT4D) movie;
		this.result = new TrackingResultSPT(this.inObject);
		this.result.init();
		object3DToAssociate = inObject.getAssociationLastResult(result);
	}

}
