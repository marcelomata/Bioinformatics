package trackingSPT.objects3D;

import trackingSPT.events.enums.EventType;

public class MissedObject extends SegmentationError{
	
	private ObjectTree3D object;
	
	public MissedObject(ObjectTree3D obj, int frame) {
		super(EventType.MISSING);
		this.object = obj;
	}
	
	public ObjectTree3D getObject() {
		return object;
	}
	
	@Override
	public int getFrameError() {
		return object.getFrame()+1;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append(";Frame source-");
		builder.append(object.getFrame());
		builder.append(";Objects source-");
		builder.append(object.getObject().getValue());
		builder.append(";Frame target-");
		builder.append(object.getFrame()+1);
		
		return builder.toString();
	}

}
