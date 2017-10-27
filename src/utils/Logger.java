package utils;



public final class Logger {
	
	public static LoggerType typeError = new LoggerType("Error", 0);
	public static LoggerType typeWarning = new LoggerType("Warning", 1);
	public static LoggerType typeInfo = new LoggerType("Info", 2);
	public static LoggerType typeDebug = new LoggerType("Debug", 3);
	
	private static final int IMPORTANCE_TO_PRINT = 2;
	
	public static void log(LoggerType type, String message){
		if (type.getImportance() == 0){
			System.err.println(type.getName().toUpperCase() + " : " + message);
		}
		else if (type.getImportance() <= IMPORTANCE_TO_PRINT){
			System.out.println(type.getName().toUpperCase() + " : " + message);
		}
	}
}
