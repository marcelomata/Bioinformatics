package trackingSPT.events.enums;

public enum EventType {
	
	MISSING("event - missing"), 
	MITOSIS("event - mitosis"), 
	ASSOCIATION("event - association"), 
	COLOCALIZATION("event - colocalization"), 
	GOING_OUT("event - goingout"), 
	GOING_IN("event - goingin"), 
	SPLITTING("event - splitting"), 
	MERGING("event - merging");
	
	private final String type;
	
	private EventType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return type;
	}

}
