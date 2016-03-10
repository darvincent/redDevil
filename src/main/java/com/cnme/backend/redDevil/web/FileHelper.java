package com.cnme.backend.redDevil.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {
	public static void createDir(String dirPath) throws NullPointerException,
			SecurityException {
		if (!dirPath.endsWith(File.separator)) {
			dirPath = dirPath + File.separator;
		}
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static void createFile(String filePath, String content)
			throws IOException, NullPointerException, SecurityException {
		File fi = new File(filePath);
		if (!fi.exists()) {
			fi.createNewFile();
		}
		writeFile(fi, content);
	}

	public static void writeFile(File file, String content) throws IOException,
			NullPointerException, SecurityException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(content.getBytes());
		fos.close();
	}
}
