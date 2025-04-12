import java.util.Objects;

public class AudioFile {
	private String pathname = "";
	private String filename = "";
	private String author = "";
	private String title = "";
	
	public AudioFile() {
	}
	public AudioFile(String path){
		parsePathname(path);
		parseFilename(pathname);
	}
	
	public void parsePathname(String path) {
		path = path.trim();
		if (path.isBlank()) {
			pathname = "";
			filename = "";
			return;
		}
		String result = removeExtraSlashes(path);
		result = removeExtraSpaces(result);
		
		pathname = makeOsSpecificAdjustments(
						result.trim().replace("\\", "/")
		);
		// Assigning the Filename
		int lastIndex = -1;
		if (isWindows()) {
			lastIndex = pathname.lastIndexOf("\\");
		} else {
			lastIndex = pathname.lastIndexOf("/");
		}
		if (lastIndex == -1){
			filename = pathname;
		} else{
			filename = pathname.substring(lastIndex + 1);
		}
	}
	
	public void parseFilename(String filename) {
		filename = removeExtraSlashes(filename);
		int authTitleSepIndex = filename.indexOf(" - ");
		int extensionStartIndex = filename.lastIndexOf(".");
		
		if (authTitleSepIndex == -1) {
			author = "";
		} else {
			author = removeExtraSpaces(
							filename.substring(0, authTitleSepIndex)
			
			);
		}
		
		if (authTitleSepIndex == -1 && extensionStartIndex != -1) {
			title = removeExtraSpaces(
							filename.substring(0, extensionStartIndex)
			
			);
		} else if ((authTitleSepIndex != -1 && extensionStartIndex != -1)) {
			title = removeExtraSpaces(
							filename.substring(authTitleSepIndex + 2, extensionStartIndex)
			
			);
		} else {
			title = removeExtraSpaces(filename);
		}
	}
	
	private String removeExtraSlashes(String str) {
		StringBuilder result = new StringBuilder();
		char[] charArr = str.toCharArray();
		
		// Structuring a string excluding the extra file separators
		if (charArr[0] != '\n') {
			result.append(charArr[0]);
		}
		for (int i = 1; i < str.length(); i++) {
			if (charArr[i] != '\n') {
				if (charArr[i] == '\\' || charArr[i] == '/') {
					if (charArr[i - 1] != charArr[i]) {
						result.append(charArr[i]);
					}
				} else {
					result.append(charArr[i]);
				}
			}
		}
		return result.toString();
	}
	
	private String removeExtraSpaces(String str) {
		while (str.startsWith(" ")) {
			str = str.substring(1);
		}
		while (str.endsWith(" ")) {
			str = str.substring(0, str.length() - 1);
		}
		return str.trim();
	}
	
	private boolean isWindows() {
		return System.getProperty("os.name").equalsIgnoreCase("win");
	}
	
	private String makeOsSpecificAdjustments(String path) {
		if (isWindows()) {
			return path.replace("/", "\\");
		} else {
			// Adjusting the Windows Drive letter for Unix systems
			if (path.length() > 1 && path.charAt(1) == ':') {
				StringBuilder str = new StringBuilder();
				return str
								.append("/")
								.append(path)
								.toString()
								.replace(":", "");
			}
			return path;
		}
	}
	
	public String getPathname() {
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
	
	@Override
	public String toString() {
		return (Objects.equals(getAuthor(), ""))? getTitle() : getAuthor() + "‿-‿" + getTitle();
	}
}
