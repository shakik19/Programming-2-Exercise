package studiplayer.audio;

public class NotPlayableException extends Exception{
	public NotPlayableException(String pathname, String msg){
		super(String.format("Path: %s | Message: %s", pathname, msg));
	}
	
	public NotPlayableException(String pathname, Throwable t){
		super(String.format("Path: %s | Message: %s", pathname, t.getMessage()), t);
	}
	
	public NotPlayableException(String pathname, String msg, Throwable t){
		super(String.format("Path: %s | Message: %s", pathname, msg), t);
	}
}
