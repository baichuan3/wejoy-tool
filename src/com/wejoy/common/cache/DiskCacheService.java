package com.wejoy.common.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import com.wejoy.util.ApiLogger;
import com.wejoy.util.CommonUtil;
import com.wejoy.util.FileUtil;

public class DiskCacheService {
	
	public static final String FILE_LINE_SEPERATOR = "\n";
	
	public static final String CHARSET = "utf-8";
	
	public List<String> loadData(String filePath, String filename){
		return loadDataFromFile(filePath, filename);
	}
	
	public List<String> loadData(String filePath){
		return loadDataFromFile(filePath);
	}
	
	public List<String> loadDataFromFile(String filePath, String filename) {
			File file = getFile(filePath, filename);
			
			List<String> info = new ArrayList<String>(); 
			if(file != null) {
				info = readFromFile(file);
			}
			return info;
	}
	
	public List<String> loadDataFromFile(String filePath) {
		File file = getFile(filePath);
		
		List<String> info = new ArrayList<String>(); 
		if(file != null) {
			info = readFromFile(file);
		}
		return info;
	}
	
	public String loadSingleData(String path, String filename){
		File file = getFile(path, filename);
		byte[] data = FileUtil.readFileFromDiskCache(file);
		
		try {
			if(data == null){
				return null;
			}
			return new String(data, CHARSET);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public File getFile(String path, String filename){
		String filePath = CommonUtil.getRealPath(path);
		ApiLogger.info("load filepath=" + filePath);
		File file = new File(filePath + filename);

		return file;
	}
	
	public File getFile(String path){
		String filePath =  CommonUtil.getRealPath(path, false);
		ApiLogger.info("load filepath=" + filePath);
		File file = new File(filePath);
		
		return file;
	}
	
	public File getLastModifiedFile(String path, String filename){
		String filePath = CommonUtil.getRealPath(path);
		ApiLogger.info("load filepath=" + filePath);
		File f = new File(filePath);
		File[] flist = f.listFiles();
		long lastModified = -1;
		int lastModifiedIndex = -1;
		
		for (int i = 0; i < flist.length; i++) {
			String currFile = flist[i].getName();
			
			if (currFile.startsWith(filename)) {
				if(lastModified < flist[i].lastModified()) {
					lastModified = flist[i].lastModified();
					lastModifiedIndex = i;
				}
			}
		}
		
		if(lastModifiedIndex >= 0) {
			return flist[lastModifiedIndex];
		}
		else {
			return null;
		}
	}
	
	public List<String> readFromFile(File file){
		List<String> list = new ArrayList<String>();
		
		FileInputStream fin = null;
		BufferedReader br = null;
		
		try {
			fin = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fin));
			String line  = null;
			
			while((line = br.readLine()) != null) {
				if(!line.startsWith("#")) {
					list.add(new String(line.getBytes("UTF-8")));
				}
			}
		}catch(Exception e){
			ApiLogger.warn("readFromFile error, ", e);
		}
		finally {
			if(fin != null) {
				try {
					IOUtils.closeQuietly(fin);
					IOUtils.closeQuietly(br);
				} catch (Exception e) {
					ApiLogger.warn("readFromFile close io error, ", e);
				}
			}
		}
		
		return list;
	}
	
	//覆盖写
	public void writeDiskCache(String filePath, String fileName, List<String> rawDatas){
//		BufferedWriter output = null;
		FileOutputStream fo = null;
		try{
			String realFilePath = CommonUtil.getRealPath(filePath);
			
			File dir = new File(realFilePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			File file = new File(realFilePath + fileName);
			//
			if(file.exists()){
				file.delete();
			}
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
//			output = new BufferedWriter(new FileWriter(file));
			fo = new FileOutputStream(file);
			
			for(String rawData : rawDatas){
				try{
//					output.write(rawData + FILE_LINE_SEPERATOR);
					fo.write((rawData + FILE_LINE_SEPERATOR).getBytes(CHARSET));
				}catch(Exception e){
					ApiLogger.warn("hot iterator error", e);
				}
			}
			
			fo.flush();
			
		}catch(Exception e){
			ApiLogger.warn("DiskCacheService writeDiskCache error, " , e);
		}finally{
			IOUtils.closeQuietly(fo);
		}
	}
	
	//覆盖写
	public void writeDiskCache(String filePath, String fileName, String rawData){
//		BufferedWriter output = null;
		FileOutputStream fo = null;
		try{
			String realFilePath = CommonUtil.getRealPath(filePath);
			
			File dir = new File(realFilePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			File file = new File(realFilePath + fileName);
			//
			if(file.exists()){
				file.delete();
			}
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
//			output = new BufferedWriter(new FileWriter(file));
			fo = new FileOutputStream(file);
			
			try{
				fo.write((rawData + FILE_LINE_SEPERATOR).getBytes(CHARSET));
			}catch(Exception e){
				ApiLogger.warn("hot iterator error", e);
			}
			
			fo.flush();
			
		}catch(Exception e){
			ApiLogger.warn("DiskCacheService writeDiskCache error, " , e);
		}finally{
			IOUtils.closeQuietly(fo);
		}
	}
	
	//追加写
	public static void appendLine(String filePath, String fileName, String content) {
		try {
			String realFilePath = CommonUtil.getRealPath(filePath);

			File dir = new File(realFilePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(realFilePath + fileName);

			if (!file.exists()) {
				file.createNewFile();
			}

			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.writeBytes(content + FILE_LINE_SEPERATOR);
			randomFile.close();

		} catch (IOException e) {
			ApiLogger.warn("DiskCacheService appendLine error, " , e);
		}finally{
		}
	}
	
	//移除行
	public static void removeLine(String filePath, String fileName, String line) {
		removeLine(filePath, fileName, line, false);
	}

	public static void removeLine(String filePath, String fileName, String line, boolean startWithMode) {
		String realFilePath = CommonUtil.getRealPath(filePath);

		File dir = new File(realFilePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(realFilePath + fileName);

		File tempFile = new File(file.getParent() + "\\temp" + file.getName());
		//TODO 存在先删除，避免上次删除失败
		if(tempFile.exists()){
			tempFile.delete();
			ApiLogger.info("DiskCacheService removeLine temp file already eixst, then remove it.");
		}
		
		PrintWriter pw = null;
		Scanner read = null;

		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel src = null;
		FileChannel dest = null;

		try {
			pw = new PrintWriter(tempFile);
			read = new Scanner(file);

			while (read.hasNextLine()) {
				String currline = read.nextLine();

				if(startWithMode){
					if (currline.startsWith(line)) {
						continue;
					} else {
						pw.println(currline);
					}
				}else{
					if (line.equalsIgnoreCase(currline)) {
						continue;
					} else {
						pw.println(currline);
					}
				}
			
			}

			pw.flush();

			fis = new FileInputStream(tempFile);
			src = fis.getChannel();
			fos = new FileOutputStream(file);
			dest = fos.getChannel();

			dest.transferFrom(src, 0, src.size());
		} catch (IOException e) {
			ApiLogger.warn("DiskCacheService removeLine error, " , e);
		} finally {
			pw.close();
			read.close();

			try {
				fis.close();
				fos.close();
				src.close();
				dest.close();
			} catch (IOException e) {
				ApiLogger.warn("DiskCacheService removeLine clone resouce error, " , e);
			}

			if (tempFile.delete()) {
				ApiLogger.info("DiskCacheService removeLine remove temp file succ");
			} else {
				ApiLogger.warn("DiskCacheService removeLine remove temp file error");
			}
		}
	}
	
	public static void resetFile(String filePath, String fileName) {
		try {
			String realFilePath = CommonUtil.getRealPath(filePath);

			File dir = new File(realFilePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(realFilePath + fileName);

			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} catch (IOException e) {
			ApiLogger.warn("DiskCacheService resetFile error, " , e);
		}finally{
		}
	}
	
	public static void main(String[] args){
		String filePath = "/data0/";
		String fileName = "ppx.txt";
		List<String> rawDatas = new ArrayList<String>();
		rawDatas.add("hell1");
		rawDatas.add("hell0");
		rawDatas.add("ppx");
		
		DiskCacheService diskCacheService = new DiskCacheService();
		diskCacheService.writeDiskCache(filePath, fileName, rawDatas);
		System.out.println("done");
	}
}
