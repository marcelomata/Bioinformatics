package trackingSPT;


import trackingInterface.TrackingStrategy;
import trackingSPT.events.eventfinder.EventSeekerTrackingAction;
import trackingSPT.events.eventhandler.HandlerSimple;
import trackingSPT.objects3D.MovieObjectAction;
import trackingSPT.objects3D.ObjectActionSPT4D;
import trackingSPT.objects3D.TrackingContextSPT;
import trackingSPT.objects3D.TrackingResult3DSPT;

public class TrackingSPT extends TrackingStrategy {
	
	
	protected ObjectActionSPT4D inObject;
	private TrackingContextSPT context;
	
	public TrackingSPT(ObjectActionSPT4D inObject) {
		super(inObject);
	}

	@Override
	public void build() {
		addTrackingAction(new EventSeekerTrackingAction(context));
		addTrackingAction(new HandlerSimple(context));
	}

	@Override
	public void run() {
		TrackingAction current;
		
		while(inObject.getFrameTime() < inObject.getSize()) {
			System.out.println("Current Frame -> "+inObject.getFrameTime());
			context.clear();
			context.setTemporalPopulation(inObject.getTemporalPopulation3D());
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
		this.inObject = (ObjectActionSPT4D) movie;
		this.result = new TrackingResult3DSPT(this.inObject);
		this.context = new TrackingContextSPT((TrackingResult3DSPT)result);
	}

}
