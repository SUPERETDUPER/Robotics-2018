package utils;

class LoggerType {
	private final String name;
	private final int importance;
	
	public LoggerType(String name, int importance){
		this.name = name;
		this.importance = importance;
	}
	
	public String getName(){
		return name;
	}
	
	public int getImportance(){
		return importance;
	}
}