package trackingSPT.objects3D;

import trackingSPT.events.enums.EventType;

public abstract class SegmentationError {
	
	protected EventType type;
	
	public SegmentationError(EventType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
	public abstract int getFrameError();

}
