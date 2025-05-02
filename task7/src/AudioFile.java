import java.util.Objects;

public abstract class AudioFile {
	private String pathname = "";
	private String filename = "";
	protected String author = "";
	protected String title = "";
	
	public AudioFile() {
	}
	
	public AudioFile(String path) {
		parsePathname(path);
		parseFilename(this.filename);
	}
	
	public void parsePathname(String path) {
		if (path.isBlank()) {
			pathname = "";
			filename = "";
			return;
		}
		
		String result = removeExtraSlashes(path.trim());
		pathname = makeOsSpecificAdjustments(result.replace("\\", "/"));
		
		// Assigning the Filename
		int lastPathSep;
		if (isWindows()) {
			lastPathSep = pathname.lastIndexOf("\\");
		} else {
			lastPathSep = pathname.lastIndexOf("/");
		}
		if (lastPathSep == -1) {
			filename = pathname;
		} else {
			filename = pathname.substring(lastPathSep + 1).trim();
		}
	}
	
	public void parseFilename(String inputFilename) {
		if (inputFilename.trim().isEmpty()) {
			return;
		}
		inputFilename = removeExtraSlashes(inputFilename);
		int authTitleSepIndex = inputFilename.indexOf(" - ");
		int extensionStartIndex = inputFilename.lastIndexOf(".");
		
		if (authTitleSepIndex == -1) {
			if (extensionStartIndex == -1) {
				title = inputFilename.trim();
			} else {
				title = inputFilename.substring(0, extensionStartIndex);
			}
		} else {
			author = inputFilename.substring(0, authTitleSepIndex).trim();
			if (extensionStartIndex != -1) {
				title = inputFilename.substring(authTitleSepIndex + 2, extensionStartIndex).trim();
			} else {
				title = inputFilename.substring(authTitleSepIndex + 2).trim();
			}
		}
	}
	
	private String removeExtraSlashes(String str) {
		StringBuilder result = new StringBuilder();
		char[] charArr = str.toCharArray();
		// Structuring a string excluding the extra file separators and new lines
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
	
	public abstract void play();
	
	public abstract void togglePause();
	
	public abstract void stop();
	
	protected abstract String formatDuration();
	
	protected abstract String formatPosition();
	
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
		return (Objects.equals(getAuthor(), "")) ? getTitle() : getAuthor() + " - " + getTitle();
	}
}
