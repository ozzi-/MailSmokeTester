package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Helper {
	public static void sleep(int s) {
		int ms = s*1000;
		try {
			System.out.println(Settings.indentMarker+"Sleeping for "+s+" s");
		    Thread.sleep(ms);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
	
	static String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, "UTF-8");
	}
}
