/*
import java.util.Objects;

public abstract class AudioFile {
	private String pathname;
	private String filename;
	private String author;
	private String title;
	
	public AudioFile(){}
	
	public AudioFile(String path){
		parsePathname(path);
		parseFilename(filename);
	}
	
	private boolean isWindows() {
		return System.getProperty("os.name").equalsIgnoreCase("win");
	}
	
	public void parsePathname(String path) {
		String regex = "^‿+|‿+$";
		pathname = path.trim()
						.replaceAll(regex, "")
						.replaceAll("/+", "/")
						.replaceAll("\\\\+", "/");
		int lastIndex = pathname.lastIndexOf("/");
		filename = pathname.substring(lastIndex + 1);
	}
	
	
	
	public void parseFilename(String filename){
		int splitIndex = filename.indexOf("‿-‿");
		int dotIndex = filename.lastIndexOf(".");
		
		if(splitIndex == -1 && dotIndex == -1){
			author = "";
			title = filename;
			return;
		}
		
		String regex = "^‿+|‿+$";
		
		if(splitIndex > -1){
			author = filename.substring(0, splitIndex).replaceAll(regex, "");
		}else {
			author = "";
		}
		
		if(splitIndex == -1){
			title = filename.substring(0, dotIndex).replaceAll(regex, "");
		}else {
			title = filename.substring(splitIndex + 2, dotIndex).replaceAll(regex, "");
		}
		
	}
	
	public String getPathname() {
		if (isWindows()){
			pathname = pathname.replace("/", "\\");
		}else {
			String regex = "^[a-z]:";
			pathname = pathname.replaceAll(regex, "/$0").replace(":", "");
		}
		return pathname;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void play();
	
	public void togglePause();
	
	public void stop();
	
	public String formatDuration();
	
	public String formatPosition();
	
	@Override
	public String toString() {
		return (Objects.equals(getAuthor(), ""))? getTitle() : getAuthor() + "‿-‿" + getTitle();
	}
	
//	public static void main(String[] args) {
//		String[] testPathname = {"", "file.mp3", "‿‿/my-tmp/file.mp3", "//my-tmp////part1//file.mp3/",
//						"d:\\\\\\\\part1///file.mp3", "-", "‿-‿"};
//
//		String[] testFilename = {"‿Falco‿‿-‿‿Rock‿me‿‿‿‿Amadeus‿.mp3‿‿",
//						"Frankie‿Goes‿To‿Hollywood‿-‿The‿Power‿Of‿Love.ogg",
//						"audiofile.aux",
//						"‿‿‿A.U.T.O.R‿‿‿-‿‿T.I.T.E.L‿‿.EXTENSION",
//						"Hans-Georg‿Sonstwas‿-‿Blue-eyed‿boy-friend.mp3",
//						".mp3",
//						"Falco‿-‿Rock‿me‿Amadeus.",
//						"-"};
//
//		String regex = "^‿+|‿+$";
//
//		for(String pathname:testPathname){
//			pathname = pathname.trim().replaceAll(regex, "")
//							.replaceAll("/+", "/")
//							.replaceAll("\\\\+", "/")
//							.replaceAll("^[a-z]:", "/$0").replace(":", "");
//			int lastIndex = pathname.lastIndexOf("/");
//			String filename = pathname.substring(lastIndex + 1);
//			System.out.println("1. Unix path: " + pathname + " | Filename: " + filename);
//		}
//
//		System.out.println("\n\n");
//
//		for(String filename : testFilename){
//			String author;
//			String title;
//			int splitIndex = filename.indexOf("‿-‿");
//			int dotIndex = filename.lastIndexOf(".");
//
//			if(splitIndex == -1 && dotIndex == -1){
//				author = "";
//				title = filename;
//				System.out.println("Input: "+ filename + "  >  Author: " + author + "  |  Title: " + title);
//				continue;
//			}
//
//			if(splitIndex > -1){
//				author = filename.substring(0, splitIndex).replaceAll(regex, "");
//			}else {
//				author = "";
//			}
//
//			if(splitIndex == -1){
//				title = filename.substring(0, dotIndex).replaceAll(regex, "");
//			}else {
//				title = filename.substring(splitIndex + 2, dotIndex).replaceAll(regex, "");
//			}
//
////			System.out.println("Split: " + splitIndex + " | Dot: " + dotIndex);
//			System.out.println("Input: "+ filename + "  >  Author: " + author + "  |  Title: " + title);
//		}
//
//	}
}
*/
