package trackingSPT.objects3D;

public class MissedObject {
	
	private ObjectTree3D object;
	private int frameEvent;
	
	public MissedObject(ObjectTree3D obj, int frame) {
		this.object = obj;
		this.frameEvent = frame;
	}
	
	public ObjectTree3D getObject() {
		return object;
	}
	
	public int getFrameEvent() {
		return frameEvent;
	}

}
