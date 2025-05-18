package studiplayer.audio;

public class NotPlayableException extends Exception{
	public NotPlayableException(String pathname, String msg){
		super(msg);
	}
	
	public NotPlayableException(String pathname, Throwable t){
		super(t);
	}
	
	public NotPlayableException(String pathname, String msg, Throwable t){
		super(msg, t);
	}
}
