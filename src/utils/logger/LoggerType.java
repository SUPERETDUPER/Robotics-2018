package utils.logger;

class LoggerType {
	private final String name;
	private final int importance;
    private final String ansiColor;

    LoggerType(String name, int importance, String ansiColor) {
		this.name = name;
		this.importance = importance;
        this.ansiColor = ansiColor;
	}
	
	public String getName(){
		return name;
	}
	
	public int getImportance(){
		return importance;
	}

    public String getColor() {
        return ansiColor;
    }
}