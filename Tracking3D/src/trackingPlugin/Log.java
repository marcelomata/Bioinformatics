package trackingPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
	
	private static boolean DEBUG = false;
	private static StringBuilder builder = new StringBuilder();
	private static File fileLog;
	private static String nameLogFile = String.valueOf(System.currentTimeMillis());
	
	public static void println(String string) {
		if(DEBUG) {
			System.out.println(string);
			builder.append(string);
			builder.append("\n");
			writeFile(builder);
		}
	}

	private static void writeFile(StringBuilder builder2) {
		if(fileLog == null) {
			init();
		}
		
		FileWriter fw = null;
		
		try {
			if(!fileLog.exists()) {
				fileLog.createNewFile();
			}
			fw = new FileWriter(fileLog);
			fw.write(builder.toString());
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void init() {
		File fileDirErrors = new File("./log");
		if(!fileDirErrors.exists()) {
			fileDirErrors.mkdirs();
		}
		fileLog = new File("./log/log"+nameLogFile+".txt");
	}

	public static void print(String string) {
		if(DEBUG) {
			System.out.print(string);
			builder.append(string);
			writeFile(builder);
		}
	}

}
