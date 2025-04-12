public class MyPathnameTest {
	public static void main(String[] args) {
		AudioFile audio = new AudioFile();
		
		audio.parsePathname("   ");
		System.out.println("Pathname: " + audio.getPathname());
		System.out.println("Filename: " + audio.getFilename());
		
		audio.parsePathname("file.mp3");
		System.out.println("Pathname: " + audio.getPathname());
		System.out.println("Filename: " + audio.getFilename());
		
		audio.parsePathname("‿‿/my-tmp/file.mp3");
		System.out.println("Pathname: " + audio.getPathname());
		System.out.println("Filename: " + audio.getFilename());
		
		audio.parsePathname("//my-tmp////part1//file.mp3/");
		System.out.println("Pathname: " + audio.getPathname());
		System.out.println("Filename: " + audio.getFilename());
		
		audio.parsePathname("d:\\\\\\\\part1///file.mp3");
		System.out.println("Pathname: " + audio.getPathname());
		System.out.println("Filename: " + audio.getFilename());
		
		audio.parsePathname("-");
		System.out.println("Pathname: " + audio.getPathname());
		System.out.println("Filename: " + audio.getFilename());
		
		audio.parsePathname("‿-‿");
		System.out.println("Pathname: " + audio.getPathname());
		System.out.println("Filename: " + audio.getFilename());
	}
}
