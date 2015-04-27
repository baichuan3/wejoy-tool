package com.wejoy.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.lang3.StringUtils;

public class FileUtil {
	public static final Set<String> executableExtensionSet = new HashSet<String>();
	public static final Set<String> imageExtensionSet = new HashSet<String>();
	public static final Set<String> invalidString = new HashSet<String>();
	
	public static final int EXTENSION_BYTE_SIZE = 1024;
	
	// 可执行文件后缀名
	static {
		executableExtensionSet.add("exe");
		executableExtensionSet.add("bat");
		executableExtensionSet.add("msi");
		executableExtensionSet.add("vbs");
		executableExtensionSet.add("cmd");
		executableExtensionSet.add("scr");
	}
    
    // 图片格式：bmp,jpg,tiff,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,png,raw
    static {
        imageExtensionSet.add("jpg");
        imageExtensionSet.add("tiff");
        imageExtensionSet.add("bmp");
        imageExtensionSet.add("gif");
        imageExtensionSet.add("exif");
        imageExtensionSet.add("psd");
        imageExtensionSet.add("png");

        imageExtensionSet.add("jpeg");
        imageExtensionSet.add("pcx");
        imageExtensionSet.add("tga");
        imageExtensionSet.add("fpx");
        imageExtensionSet.add("svg");
        imageExtensionSet.add("cdr");
        imageExtensionSet.add("pcd");
        imageExtensionSet.add("dxf");
        imageExtensionSet.add("ufo");
        imageExtensionSet.add("eps");
        imageExtensionSet.add("hdri");
        imageExtensionSet.add("ai");
        imageExtensionSet.add("png");
        imageExtensionSet.add("raw");
    }
    
	// 异常文件后缀名
	static {
		invalidString.add("<");
		invalidString.add(">");
		invalidString.add("&");
		invalidString.add("\"");
		invalidString.add("'");
		invalidString.add("u003c");
        invalidString.add("u003e");
	}
	
	public static List<String> readData(String dataFile) {
		List<String> list = new ArrayList<String>();
		
		if (StringUtils.isBlank(dataFile)) {
			ApiLogger.error("DataLoader readData error : dataFile not exist");
			return list;
		}
		
		File file = new File(dataFile);
		
		if (!file.exists()) {
			ApiLogger.error("DataLoader readData error : dataFile not exist");
			return list;
		}
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader (new File(dataFile)));
			
			String line = null;
			while((line = reader.readLine()) != null) {
				list.add(line.trim());
			}
			
		} catch (Exception e) {
			ApiLogger.error("DataLoader readData error : read repairFile", e);
		
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					ApiLogger.error(e1);
				}
			}
		}finally{
			try {
				reader.close();
//				file.delete();
//				ApiLogger.warn("===> Delete file "+dataFile);
			} catch (Exception e) {
				ApiLogger.error(e);
			}
			
		}
		
		return list;
	}
	
	/** 写文件 */
	public static boolean writeFileToDiskCache(String filename, byte[] fileOfByte) {
		try {
			File file = new File(filename);

			if (!file.exists()) {
				File parent = file.getParentFile();

				if (!parent.exists())
					parent.mkdirs();
			}
			
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(fileOfByte);
			bos.flush();
			bos.close();

		} catch (Exception e) {
			ApiLogger.error("writeFileToDiskCache error, file = " + filename, e);

			return false;
		}

		return true;
	}
	
	/** 写文本文件 */ 
	public static boolean writeTextFileToDiskCache(File file, String str) {
		PrintWriter out =null;
		try {
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (!parent.exists())
				parent.mkdirs();
		}
		out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8")));
		out.print(str);
		out.close();
		} catch (Exception e) {
			ApiLogger.error("writeFileToDiskCache error, file = " + file.getName(), e);
			return false;
		}
		return true;
	}
	
	public static String getRealPath(String filePath, String filename){
		String path = CommonUtil.getRealPath(filePath);
		
		if(StringUtils.isNotBlank(path)){
			return path + filename;
		}else{
			return null;
		}
	}
	
	public static void main(String[] args){
		List<String> reList = new ArrayList<String>();
		List<String> mcqList = readData("/Users/badu/shell/mcq");
		List<String> ofList = readData("/Users/badu/shell/of");
		List<String> dmList = readData("/Users/badu/shell/dm");
		
		for(String str : mcqList){
			if(!(ofList.contains(str) || dmList.contains(str))){
				reList.add(str);
			}
		}
		
		StringBuilder builder = new StringBuilder();
		for(String id : reList){
			builder.append(id).append("\n");
		}
		writeFileToDiskCache("/Users/badu/shell/remain", builder.toString().getBytes());
	}
	
    /**
     * 传入文件的前部门字节流，获取文件的实际扩展名
     *
     * @param metaStream
     * @return
     */
    public static String getFileExtension(byte[] metaStream) {
        if (metaStream == null)
            return "";

        String extension = "";

        try {
            // onlyMimeMatch true
            MagicMatch match = Magic.getMagicMatch(metaStream, true);
            if (match != null) {

                extension = match.getExtension();
            }
            if ("".equals(extension)) {
                String[] exs = match.getMimeType().split("/");
                extension = exs[1];

            }

        } catch (Exception e) {
        }

        return extension;
    }
    
    public static boolean isImageFile(String fileExtension) {
        if (fileExtension == null)
            return false;

        return imageExtensionSet.contains(fileExtension);
    }
    
    public static boolean isImage(byte[] fileOfByte){
    	return isImageFile(getFileExtension(fileOfByte));
    }
    
	/** 读文件 */
	public static byte[] readFileFromDiskCache(String filename) {
		byte[] buffer = null;
		
		try {
			File file = new File(filename);

			if (!file.exists())
				return null;
			
			buffer = new byte[(int) (file.length())];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.read(buffer);
			bis.close();

		} catch (Exception e) {
			ApiLogger.error("readFileFromDiskCache error, file = " + filename, e);
		} finally{
		}

		return buffer;
	}
	
	/** 读文件 */
	public static byte[] readFileFromDiskCache(File file) {
		byte[] buffer = null;
		
		try {
			if (!file.exists())
				return null;
			
			buffer = new byte[(int) (file.length())];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.read(buffer);
			bis.close();

		} catch (Exception e) {
			ApiLogger.error("readFileFromDiskCache error, file = ", e);
		} finally{
		}

		return buffer;
	}
	
	/** 读文件 得到文本内的String */
	public static String readTextFileFromDiskCache(File file) {
		if(!file.exists()){
			ApiLogger.error("FileUtil readTxtFileFromDiskCache error : file not exist");
			return "";
		}
		StringBuffer sb =new StringBuffer();
		BufferedReader br = null;
		try {
			//构造BufferedReader对象
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}
		catch (IOException e) {
			ApiLogger.error("FileUtil readTxtFileFromDiskCache error : read repairFile", e);
		}
		finally {
			//关闭BufferedReader
			if (br != null) {
				try {
					br.close();
				}
				catch (IOException e) {
					ApiLogger.error(e);
				}
			}
		}
		return sb.toString();
	}
	
	
	
	public static String getFileExtension(byte[] byteOfFile, String filename) {
		if(byteOfFile == null) return getFileExtension(filename);
		
		int length = byteOfFile.length < EXTENSION_BYTE_SIZE ? byteOfFile.length : EXTENSION_BYTE_SIZE;

		byte[] tempByte = new byte[length];
		System.arraycopy(byteOfFile, 0, tempByte, 0, length);

		// 对于文本文件，该方法取不出文件类型，就直接取扩展名
		String fileExtension = getFileExtension(tempByte);
		if (StringUtils.isBlank(fileExtension)) {
			fileExtension = getFileExtension(filename);
		}

		return fileExtension;
	}
	
	/**
	 * 获取文件的后缀名，小写
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileExtension(String filename) {
		if (filename == null)
			return null;

		int index = filename.lastIndexOf('.');

		if (index == -1 || index == filename.length()) {
			return null;
		}

		String extension = filename.substring(index + 1).toLowerCase();

		return extension;

	}
	
	/**
	 * 截取文件名
	 * 
	 * @param filename
	 * @param maxLength
	 * @return
	 */
	public static String getSuitableFilename(String filename, int maxLength) {
		if (StringUtils.isBlank(filename))
			return filename;

		if (filename.length() <= maxLength)
			return filename;

		String extention = getFileExtension(filename);
		if (StringUtils.isBlank(extention)) {
			filename = filename.substring(0, maxLength);
		} else {
			int len = extention.length();
			filename = filename.substring(0, maxLength - len - 1) + "." + extention;
		}

		return filename;
	}
	
	/**
	 * 判断文件是否可执行
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isExecutable(String filename) {
		if (filename == null)
			return false;

		String extensionName = getFileExtension(filename);

		return executableExtensionSet.contains(extensionName);
	}
	
	
	/**
	 * check the filename valid.
	 * 
	 * @param filename
	 * @return
	 */
	public static String getValidFileName(String filename)
	{
		if (filename == null)
			return null;
		
		for (String tmp : invalidString) {
			filename = filename.replaceAll(tmp, "x");
		}
		return filename;
	}
}
