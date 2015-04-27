package com.wejoy.util;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.wejoy.common.cache.LRUCache;

public class SerializeUtil {

	public static synchronized void safeWriteTmp(Object obj, String filePath) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(filePath));
			out.writeObject(obj);
			out.flush();
			out.close();
		} catch (Exception e) {
			ApiLogger.warn("SerializeUtil safeWriteTmp exception!", e);
		}
	}

	public static Object safeReadTmp(String filePath) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));
			Object obj = in.readObject();
			in.close();
			
			return obj;
		} catch (Exception e) {
			ApiLogger.warn("SerializeUtil safeReadTmp exception!", e);
		} 
		return null;
	}
	
	public static void main(String[] args){
		Map<String, Long> map = new LinkedHashMap<String, Long>();
		map.put("000", 1l);
		map.put("1111", 1l);
		map.put("2222", 2l);
		map.put("3333", 3l);
		map.put("4444", 4l);
		
		String filePath = "/data0/public_timeline";
		if(StringUtils.isNotBlank(filePath)){
			Object cacheData = SerializeUtil.safeReadTmp(filePath);
			if(cacheData != null){
				Map<Integer, LRUCache<Long, Long>> bakMap = (Map<Integer, LRUCache<Long, Long>>)cacheData;
				for(LRUCache<Long, Long> value : bakMap.values()){
					System.out.println(value.usedEntries());
					System.out.println(value.toString());
				}
			}
		}
		
	}
	
	public static void writeSafeUtf8(ObjectOutput out, String s) throws IOException {
		if (s == null) {
			out.writeShort(-1);
		} else {
			byte[] bb = s.getBytes("utf-8");
			out.writeShort(bb.length);
			out.write(bb);
		}
	}

	public static String readSafeUtf8(ObjectInput in) throws IOException {
		int len = in.readShort();
		if (len < 0)
			return null;

		byte[] bb = new byte[len];
		in.read(bb);
		return new String(bb, "utf-8");
	}
}
