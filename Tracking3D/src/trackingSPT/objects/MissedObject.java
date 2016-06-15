package trackingSPT.objects;

public class MissedObject {
	
	private ObjectTree object;
	private int frameEvent;
	
	public MissedObject(ObjectTree obj, int frame) {
		this.object = obj;
		this.frameEvent = frame;
	}
	
	public ObjectTree getObject() {
		return object;
	}
	
	public int getFrameEvent() {
		return frameEvent;
	}

}
