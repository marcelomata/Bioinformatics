package trackingSPT.objects;

public class MissedObject {
	
	private TemporalObject object;
	private int frameEvent;
	
	public MissedObject(TemporalObject obj, int frame) {
		this.object = obj;
		this.frameEvent = frame;
	}
	
	public TemporalObject getObject() {
		return object;
	}
	
	public int getFrameEvent() {
		return frameEvent;
	}

}
