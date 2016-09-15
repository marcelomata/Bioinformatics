package trackingSPT.objects3D;

import java.util.ArrayList;
import java.util.List;

import trackingSPT.events.enums.EventType;

/**
 * 
 * A new class can be created to be used in both splitting and merging cases.
 * 
 * @author Marcelo da Mata
 *
 */
public class MergedObject extends SegmentationError {
	
	private ObjectTree3D objectTarget;
	private List<ObjectTree3D> sourceObjects;
	private int frameEvent;
	
	public MergedObject(ObjectTree3D target, int frame) {
		super(EventType.MERGING);
		this.objectTarget = target;
		this.sourceObjects = new ArrayList<ObjectTree3D>();
		this.frameEvent = frame;
	}
	
	public ObjectTree3D getObjectTarget() {
		return objectTarget;
	}
	
	public List<ObjectTree3D> getSourceObjects() {
		return sourceObjects;
	}
	
	public int getFrameEvent() {
		return frameEvent;
	}
	
	@Override
	public int getFrameError() {
		return objectTarget.getFrame();
	}
	
	public int getId() {
		return objectTarget.getId();
	}
	
	public void addSource(ObjectTree3D source) {
		this.sourceObjects.add(source);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append(";Frame source-");
		builder.append(sourceObjects.get(0).getFrame());
		builder.append(";Objects source-");
		for (ObjectTree3D obj : sourceObjects) {
			builder.append(obj.getObject().getValue());
			builder.append(" ");
		}
		builder.replace(builder.length()-1, builder.length()-1, "");
		builder.append(";Frame target-");
		builder.append(objectTarget.getFrame());
		builder.append(";Objects target-");
		builder.append(objectTarget.getObject().getValue());
		
		return builder.toString();
	}

}
