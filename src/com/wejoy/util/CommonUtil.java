package com.wejoy.util;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class CommonUtil {
	public static  Random random = new Random();
	public static final int RANDOM_TIMES = 10;
	
	public static long parseLong(String value) {
		return parseLong(value, 0);
	}
	
	public static long parseLong(String value, long defaultValue) {
		if (value == null || "".equals(value.trim()))
			return defaultValue;

		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			ApiLogger.error(e);
		}

		return defaultValue;
	}
	
	public static long parseLong(Long value){
		return value != null ? value.longValue() : 0;
	}

	public static int parseInteger(String value) {
		return parseInteger(value, 0);
	}

	public static int parseInteger(String value, int defaultValue) {
		if (value == null || "".equals(value.trim()))
			return defaultValue;

		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			ApiLogger.error(e);
		}

		return defaultValue;
	}
	
	public static int parseInteger(Integer value){
		return value != null ? value.intValue() : 0;
	}
	
	public static float parseFloat(String value) {
		return parseFloat(value, 0.0f);
	}
	
	public static float parseFloat(String value, float defaultValue) {
		if (value == null || "".equals(value.trim()))
			return defaultValue;
		
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
			ApiLogger.error(e);
		}
		
		return defaultValue;
	}
	
	public static boolean parseBoolean(String value){
		return parseBoolean(value, false);
	}
	
	public static boolean parseBoolean(String value, boolean defaultValue) {
		if (value == null || "".equals(value))
			return defaultValue;

		try {
			return Boolean.valueOf(value);
		} catch (Exception e) {
			ApiLogger.error(e);
		}

		return defaultValue;
	}
	
	public static List<String> strToList(String value){
		return strToList(value, ",");
	}
	
	public static List<String> strToList(String value, String split){
		List<String> ret = new ArrayList<String>();
		
		if(StringUtils.isBlank(value))
			 return ret;
		
		String[] arr = value.split(split);
		
		return arrToList(arr);
		
	}
	
	public static List<String> arrToList(String[] arr) {
		if (arr == null) return null;
		
		List<String> list = new ArrayList<String> ();
		
		for (int i = 0; i < arr.length ; i ++) {
			list.add(arr[i]);
		}
		
		return list;
	}
	
	public static List<Long> arr2longlist(long[] arr) {
		if (arr == null) return null;
		
		List<Long> list = new ArrayList<Long> ();
		
		for (int i = 0; i < arr.length ; i ++) {
			list.add(arr[i]);
		}
		
		return list;
	}
	
	public static String list2str(List<String> list){
		if(list == null || list.isEmpty()){
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		for(String str : list){
			builder.append(str).append(",");
		}
		builder.setLength(builder.length() - 1);
		
		return builder.toString();
	}
	
	public static String listlong2str(List<Long> list){
		if(list == null || list.isEmpty()){
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		for(Long str : list){
			builder.append(str).append(",");
		}
		builder.setLength(builder.length() - 1);
		
		return builder.toString();
	}
	
	public static String getRealPath(String configPath){
		return getRealPath(configPath, true);
	}
	
	public static String getRealPath(String configPath, boolean withEndSeparator){
		 String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		 if(StringUtils.isBlank(rootPath)){
			 return null;
		 }
		 
		 if(StringUtils.isBlank(configPath)){
			 return rootPath;
		 }
		 
		 StringBuilder builder = new StringBuilder();
		 builder.append(rootPath).append(configPath);
		 String separator = File.separator;
		 if(withEndSeparator){
			 if(!configPath.endsWith(separator)){
			 builder.append(separator);
			 }
		 }
		 
		 return builder.toString();
	}

	/**
	 * 内容长度
	 * 
	 * <pre>
	 * 		ascii 0~255 为1字节，其他的为2字节
	 * </pre>
	 * 
	 * @param msg
	 * @return
	 */
	public static int textLength(String msg) {
		int count = 0;
		for (int i = 0; i < msg.length(); i++) {
			char v = msg.charAt(i);
			if (v > (char) 255) {
				count += 2;
			} else {
				count++;
			}
		}

		return count;
	}
	
	//按ascii字符截取，一个汉字两个字符
	public static String subString(String str, int num) {
		char[] charArray = str.toCharArray();
		String subStr = "";
		int index = 0;
		for (int i = 0, j = 0; i < num;) {
			if (j >= charArray.length) {
				break;
			}
			char tempchar = charArray[j];

			if (tempchar > (char) 255) {
				// 如果这个字符是一个汉字
				i = i + 2;
				if (i > num) {
					// 如果num正好截取到半个汉字的时候，跳过此次for循环。
					// 如果num正好截取到一个完整的汉字的时候，继续执行下面的index++等语句。
					if (i == (num + 1)) {
						continue;
					}
				}
			} else {
				i++;
			}
			index++;
			j++;
		}
		subStr = str.substring(0, index);
		return subStr;
	}
	
	public static String subStringForChinese(String str, int num) {
		if (str == null || str.length() <= num) return str;
		
		String subStr = str.substring(0, num);
		
		return subStr;
	}
	
	public static void main(String[] args) {
		String a ="ta你你你你你你你你你他我你你你你你你你";
		
		String b = CommonUtil.subStringForChinese(a, 10);
		String b1 = CommonUtil.subString(a, 20);
		
		System.out.println(b);
		System.out.println(b1);
	}
	
	public static String[] str2arr(String source) {
		if (StringUtils.isBlank(source) || source.equalsIgnoreCase("null")) {
			return null;
		}
		
		if("[]".equals(source)) {
			return new String[]{};
		}
		
		if ('[' != source.charAt(0) || ']' != source.charAt(source.length()-1))  {
			try {
				String[] ret = StringUtils.split(source, ",");
				return ret;
			} catch (Exception e) {
				return new String[]{};
			}
		}
		
		try {
			String[] ret = StringUtils.split(source, ',');
			
			for (int i = 0; i < ret.length; i++) {
				if (i == 0) {
					ret[i] = ret[i].replace('[', ' ');
				}
				if (i == ret.length-1) {
					ret[i] = ret[i].replace(']', ' ');
				}
				ret[i] = ret[i].trim();
			}
			
			return ret;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static long[] str2arrlong(String source){
		String[] arrStr = str2arr(source);
		
		if(arrStr == null || arrStr.length == 0) return null;
		
		List<Long> list = new ArrayList<Long>();
		for(String str : arrStr){
			long value = CommonUtil.parseLong(str);
			if(value > 0){
				list.add(value);
			}
		}
		
		return list2longarray(list);
	}
	
	public static long[] list2longarray(List<Long> list){
		if(list == null || list.isEmpty()) return null;
		
		long[] arr = new long[list.size()];
		int i = 0;
		for(Long ele : list){
			arr[i] = ele;
			i++;
		}
		return arr;
	}
	
	public static String[] list2arr(List<String> list){
		if(list == null || list.isEmpty()) return null;
		
		String[] arr = new String[list.size()];
		int i = 0;
		for(String ele : list){
			arr[i] = ele;
			i++;
		}
		return arr;
	}
	
	public static String longarr2str(long[] arr){
		if(arr == null || arr.length == 0){
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		for(long value : arr){
			builder.append(value).append(",");
		}
		
		if(builder.length() > 0){
			builder.setLength(builder.length() - 1);
		}
		
		return builder.toString();
	}
	
	public static String strarr2str(String[] arr){
		if(arr == null || arr.length == 0){
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		for(String value : arr){
			builder.append(value).append(",");
		}
		
		if(builder.length() > 0){
			builder.setLength(builder.length() - 1);
		}
		
		return builder.toString();
	}
	
	public static String getNotNullStr(String str){
		if(StringUtils.isBlank(str)){
			return "";
		}
		
		return str;
	}

    public static Map<String, String> filterNullStr(Map<String, String> map, String[] keys){
    	if(map == null || map.isEmpty()){
    		return map;
    	}
    	
    	if(keys == null || keys.length <= 0){
    		return Collections.emptyMap();
    	}
    	
    	for(String key : keys){
    		if(StringUtils.isBlank(map.get(key))){
    			map.remove(key);
    		}
    	}
    	
    	return map;
    }
    
    public static Map<String, Long> filterNullLong(Map<String, Long> map, String[] keys){
    	if(map == null || map.isEmpty()){
    		return map;
    	}
    	
    	if(keys == null || keys.length <= 0){
    		return Collections.emptyMap();
    	}
    	
    	for(String key : keys){
    		if(map.get(key) == null || map.get(key) == 0){
    			map.remove(key);
    		}
    	}
    	
    	return map;
    }
    
    public static String replaceStr(String originStr, String replaceStr){
    	if(StringUtils.isBlank(originStr) || StringUtils.isBlank(replaceStr)){
    		return originStr;
    	}
    	
    	return originStr.replace(replaceStr ,"");
    }
    

	
	public static List<Long> str2listlong(String str){
		if (StringUtils.isBlank(str)) {
			return Collections.emptyList();
		}
		
		List<Long> list = new ArrayList<Long>();
		try {
			String[] ret = StringUtils.split(str, ',');
			
			for (int i = 0; i < ret.length; i++) {
				String tmpStr = ret[i];
				if(StringUtils.isNotBlank(tmpStr)){
					list.add(CommonUtil.parseLong(tmpStr.trim()));
				}
			}
		} catch (Exception e) {
		}
		
		return list;
	}
	
	public static String urlencode(String text){
		if(StringUtils.isBlank(text)){
			return text;
		}
		
		text = URLEncoder.encode(text);
		
		return text;
	}
	
//		移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
//		联通：130、131、132、152、155、156、185、186
//		电信：133、153、180、189、（1349卫通）
	public static boolean isMobileNumber(String mobile){
//			Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		//宽松匹配
		Pattern p = Pattern.compile("^(13|14|15|16|17|18|19)\\d{9}$");
		Matcher m = p.matcher(mobile);
		return m.matches();
	}
}

