public class AudioFile {
	private String pathname = "";
	private String filename = "";
	private String author = "";
	private String title = "";
	
	public AudioFile() {
	}
	
	public void parsePathname(String path) {
		path = path.trim();
		if (path.isBlank()) {
			pathname = "";
			filename = "";
			return;
		}
		
		StringBuilder result = removeRedundantSymbols(path);
/*		char[] charArr = path.toCharArray();
		
		if (charArr[0] != '‿') {
			result.append(charArr[0]);
		}
		
		// Building a pathname excluding the redundant spaces '‿' and file separators
		for (int i = 1; i < path.length(); i++) {
			if (charArr[i] != '‿') {
				if (charArr[i] == '\\' || charArr[i] == '/') {
					if (charArr[i - 1] != charArr[i]) {
						result.append(charArr[i]);
					}
				} else {
					result.append(charArr[i]);
				}
			}
		}*/
		
		pathname = makeOsSpecificAdjustments(
						result.toString()
										.trim()
										.replace("\\", "/"));
		
		// Assigning the Filename
		int lastIndex;
		if (isWindows()){
			lastIndex = pathname.lastIndexOf("\\");
		}else {
			 lastIndex = pathname.lastIndexOf("/");
		}
		filename = pathname.substring(lastIndex + 1);
	}
	
	public void parseFilename(String filename){
		int authTitleSepIndex = filename.indexOf("‿-‿");
		int extensionStartIndex = filename.lastIndexOf(".");
		
		if(authTitleSepIndex == -1 && extensionStartIndex == -1){
			author = "";
			title = filename;
			return;
		}
	}
	
	private StringBuilder removeRedundantSymbols(String str){
		StringBuilder result = new StringBuilder();
		char[] charArr = str.toCharArray();
		
		if (charArr[0] != '‿') {
			result.append(charArr[0]);
		}
		
		// Structuring a string excluding the redundant spaces '‿' and file separators
		for (int i = 1; i < str.length(); i++) {
			if (charArr[i] != '‿') {
				if (charArr[i] == '\\' || charArr[i] == '/') {
					if (charArr[i - 1] != charArr[i]) {
						result.append(charArr[i]);
					}
				} else {
					result.append(charArr[i]);
				}
			}
		}
		return result;
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
				return str.append("/").append(path).toString().replace(":", "");
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
}
