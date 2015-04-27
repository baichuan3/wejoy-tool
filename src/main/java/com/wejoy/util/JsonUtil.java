package com.wejoy.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.JsonNode;

public class JsonUtil {
	protected static Logger log = Logger.getLogger(JsonUtil.class);
	
	public static String getJsonTextValue(JsonNode node, String defaultValue){
		return node != null ? node.getTextValue() : defaultValue;
	}
	
	public static boolean getJsonBooleanValue(JsonNode node, boolean defaultValue){
		return node != null ? node.getBooleanValue() : defaultValue;
	}
	
	public static long getJsonLongValue(JsonNode node, long defaultValue){
		return node != null ? node.getLongValue() : defaultValue;
	}
	
	public static int getJsonIntValue(JsonNode node, int defaultValue){
		return node != null ? node.getIntValue() : defaultValue;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getJsonMultiValues(JsonNode node){
		if(node == null){
			return Collections.EMPTY_LIST;			
		}
		List<String> results = new ArrayList<String>();
		Iterator<JsonNode> values = node.getElements();		
		while(values.hasNext()){
			String value = values.next().toString();
			if(value.trim().length() > 0){
				results.add(value.trim());
			}
		}
		return results;
	}
	
    /**
     * get Long array with node
     * 
     * @param node
     * @return
     */
	@SuppressWarnings("unchecked")
    public static List<Long> getJsonMultiValuesLong(JsonNode node){
        if(node == null){
            return Collections.EMPTY_LIST;          
        }
        List<Long> results = new ArrayList<Long>();
        Iterator<JsonNode> values = node.getElements();     
        while(values.hasNext()){
            JsonNode value = values.next();
            results.add(value.getLongValue());
        }
        return results;
    }
	
	/**
	 * xml 1.0: 0-31,127控制字符为非法内容，转为空格
	 * @param value
	 * @return
	 */
	public static String toJsonStr(String value) {
		if (value == null)
			return null;
				
		StringBuilder buf = new StringBuilder(value.length());
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
            switch(c) {
                case '"':
                	buf.append("\\\"");
                    break;
                case '\\':
                    buf.append("\\\\");
                    break;
                case '\n':
                    buf.append("\\n");
                    break;
                case '\r':
                    buf.append("\\r");
                    break;
                case '\t':
                    buf.append("\\t");
                    break;
                case '\f':
                    buf.append("\\f");
                    break;
                case '\b':
                    buf.append("\\b");
                    break;
                    
                default:
                	if (c < 32 || c == 127) {
                		buf.append(" ");
                	} else {
                		buf.append(c);
                	}
            }
		}
		return buf.toString();
	}
	
	/**
	 * 获取存储JsonBuilder的iterable对应的json串
	 * @param iterable
	 * @return
	 */
	public static String toJson4Iterable(Iterable<JsonBuilder> iterable) {
		StringBuilder sb = new StringBuilder();
		
		if (iterable != null){
			sb.append("[");
			int i = 0;
			for(JsonBuilder s :iterable){
				if(i++>0){
					sb.append(",");
				}
				sb.append(s.toString());
			}
			sb.append("]");
		}else{
			sb.append("[]");
		}
		
		return sb.toString();
	}	
	
    public static String toJson(long[] ids) {
        String str = "[]";
        if (ids != null && ids.length > 0) {
            int iMax = ids.length - 1;
            StringBuilder b = new StringBuilder();
            b.append('[');
            for (int i = 0;; i++) {
                b.append('"').append(ids[i]).append('"');
                if (i == iMax) {
                    b.append(']').toString();
                    break;
                }
                b.append(", ");
            }
            str = b.toString();
        }
        return str;
    }
    
	public static String toJsonArrStr(List<JsonBuilder> jsonArr){
		StringBuilder builder = new StringBuilder();
		
		if (jsonArr != null && !jsonArr.isEmpty()){
			builder.append("[");
			int i=0;
			for(JsonBuilder s :jsonArr){
				if(i++>0){
					builder.append(",");
				}
				builder.append(s.toString());
			}
			builder.append("]");
		}else{
			builder.append("[]");
		}
		
		return builder.toString();
	}
	
	public static String liststr2JsonArrStr(List<String> jsonArr){
		StringBuilder builder = new StringBuilder();
		
		if (jsonArr != null && !jsonArr.isEmpty()){
			builder.append("[");
			int i=0;
			for(String s : jsonArr){
				if(i++>0){
					builder.append(",");
				}
				builder.append("\"").append(s).append("\"");
			}
			builder.append("]");
		}else{
			builder.append("[]");
		}
		
		return builder.toString();
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException{
	}
}
