package trackingSPT.objects3D;

import java.util.ArrayList;
import java.util.List;

import trackingSPT.events.enums.EventType;

public class SplittedObject extends SegmentationError {
	
	private ObjectTree3D objectSource;
	private List<ObjectTree3D> targetObjects;
	private int frameEvent;
	
	public SplittedObject(ObjectTree3D source, int frame) {
		super(EventType.SPLITTING);
		this.objectSource = source;
		this.targetObjects = new ArrayList<ObjectTree3D>();
		this.frameEvent = frame;
	}
	
	public ObjectTree3D getObjectSource() {
		return objectSource;
	}
	
	public List<ObjectTree3D> getTargetObjects() {
		return targetObjects;
	}
	
	public int getFrameEvent() {
		return frameEvent;
	}
	
	public int getId() {
		return objectSource.getId();
	}

	public void addTarget(ObjectTree3D target1) {
		this.targetObjects.add(target1);
	}
	
	@Override
	public int getFrameError() {
		return targetObjects.get(0).getFrame();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append(";Frame source-");
		builder.append(objectSource.getFrame());
		builder.append(";Objects source-");
		builder.append(objectSource.getObject().getValue());
		builder.append(";Frame target-");
		builder.append(targetObjects.get(0).getFrame());
		builder.append(";Objects target-");
		for (ObjectTree3D obj : targetObjects) {
			builder.append(obj.getObject().getValue());
			builder.append(" ");
		}
		builder.replace(builder.length()-1, builder.length()-1, "");
		
		return builder.toString();
	}

}
