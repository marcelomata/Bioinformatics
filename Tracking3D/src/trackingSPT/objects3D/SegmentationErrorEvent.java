package trackingSPT.objects3D;

import trackingSPT.events.enums.EventType;

public class SegmentationErrorEvent {
	
	private ObjectTree3D obj1;
	private ObjectTree3D obj2;
	private EventType type;
	
	public SegmentationErrorEvent(ObjectTree3D obj1, ObjectTree3D obj2, EventType type) {
		this.obj1 = obj1;
		this.obj2 = obj2;
		this.type = type;
	}
	
	@Override
	public String toString() {
		String typeError = "";
		if(type == EventType.MERGING) {
			
		}
		return super.toString();
	}

}
