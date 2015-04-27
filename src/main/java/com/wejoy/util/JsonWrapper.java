package com.wejoy.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonNode;
import org.codehaus.jackson.map.JsonTypeMapper;
import org.codehaus.jackson.map.impl.BooleanNode;
import org.codehaus.jackson.map.impl.NumericNode;


public class JsonWrapper {
	protected static Logger log = Logger.getLogger(JsonWrapper.class);
	private JsonNode root;
	
	public JsonWrapper(String json) throws Exception {
		if (json != null)
			try {
				JsonParser jp = new JsonFactory().createJsonParser(new StringReader(json));
				JsonTypeMapper jtm = new JsonTypeMapper();
				jtm.setDupFieldHandling(org.codehaus.jackson.map.BaseMapper.DupFields.USE_LAST);
				root = jtm.read(jp);
			} catch (Exception e) {				
				log.warn(new StringBuilder(64).append("Error: msg=").append(json), e);
				throw e;
			}
	}
	
	public JsonWrapper(JsonNode root) {
		this.root = root;
	}
	
	public String get(String name) {
		JsonNode node = getJsonNode(name);
		return (node == null ? null : node.getValueAsText());
	}
	
    public String[] getStringArr(String name) {
        List<JsonNode> result = getList(name);
        String[] strs = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            JsonNode node = result.get(i);
            strs[i] = node.getTextValue();
        }
        return strs;
    }
    public List<String> getListVal(String name) {
    	List<JsonNode> result = getList(name);
    	List<String> list = new ArrayList<String>();
    	for (int i = 0; i < result.size(); i++) {
    		JsonNode node = result.get(i);
    		list.add(node.getTextValue());
    	}
    	return list;
    }
    
	public List<JsonNode> getList(String name){
	    JsonNode node = getJsonNode(name);
	    
	    List<JsonNode> result = new ArrayList<JsonNode>();
        if (null == node) {
            return result;
        }
        Iterator<JsonNode> iter = node.getElements();
        while (iter.hasNext()) {
            JsonNode next = iter.next();
            if (next != null) {
                result.add(next);
            }
        }
        return result;
    }

    public String getString(String name) {
        JsonNode node = getJsonNode(name);
        return (node == null ? null : node.getTextValue());
    }	
	
	public long getLong(String name){
		return getLong(name, 0l);
	}
	
	public long getLong(String name, long defaultValue) {
		JsonNode node = getJsonNode(name);
		if (node == null)
			return defaultValue;
		else if (node instanceof NumericNode)
			return ((NumericNode)node).getLongValue();
		else
			return str2long(node.getValueAsText(), defaultValue);
	}
	
	public int getInt(String name) {
		return getInt(name, 0);
	}
	
	public int getInt(String name, int defaultValue) {
		JsonNode node = getJsonNode(name);
		if (node == null)
			return defaultValue;
		else if (node instanceof NumericNode)
			return ((NumericNode)node).getIntValue();
		else
			return str2int(node.getValueAsText(), defaultValue);
	}
	
	public float getFloat(String name) {
		return getFloat(name, 0f);
	}
	
	public float getFloat(String name, float defaultValue) {
		JsonNode node = getJsonNode(name);
		if (node == null)
			return defaultValue;
		else if (node instanceof NumericNode)
			return (float)(((NumericNode)node).getDoubleValue());
		else
			return str2float(node.getValueAsText(), defaultValue);
	}
	
	public boolean getBoolean(String name) {
		return getBoolean(name, false);
	}
	
	public boolean getBoolean(String name, boolean defaultValue) {
		JsonNode node = getJsonNode(name);
		if (node == null)
			return defaultValue;
		else if (node instanceof BooleanNode)
			return ((BooleanNode)node).getBooleanValue();
		else
			return  Boolean.parseBoolean(node.getTextValue());
	}
	
	public JsonWrapper getNode(String name) {
		return new JsonWrapper(getJsonNode(name));
	}
	
	public JsonNode getRootNode() {
        return root;
    }
	
	/**
	 * 对FieldName中包括“.”的node做特殊处理
	 * 不对“.”对解析处理
	 * @param name	待查找的FieldName
	 * @return	如果不存在，则node为null，但JsonWrapper不为null
	 */
	public JsonWrapper getNodeWithPeriod(String name) {
		JsonNode node = root;
		if (name == null) {
			node = null;
		} else {
			node = node.getFieldValue(name);
		}
		return new JsonWrapper(node);
	}
	
	public boolean isEmpty() {
		return (root == null || root.size() == 0);
	}
	
	public JsonNode getJsonNode(String name) {
		if (name == null || root == null)
			return null;
		
		JsonNode node = root;
		StringTokenizer st = new StringTokenizer(name, ".");
		while (st.hasMoreTokens()) {
			String key = st.nextToken().trim();
			if (key.isEmpty() || (node = node.getFieldValue(key)) == null)
				return null;
		}
		return node;
	}
	
	public JsonNode appendJsonNode(JsonNode jsNode){
		if (jsNode == null || root == null)
			return null;
		root.appendElement(jsNode);
		return root;
	}
	
	public String toString(){
		if(root == null){
			return null;
		}
		return root.toString();
	}
	
	private int str2int(String s, int defaultValue) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	private long str2long(String s, long defaultValue) {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

    public boolean isValueNode() {
        return root.isValueNode();
    }

    public boolean isContainerNode() {
        return root.isContainerNode();
    }

    public boolean isMissingNode() {
        return root.isMissingNode();
    }

    public boolean isArray() {
        return root.isArray();
    }

    public boolean isObject() {
        return root.isObject();
    }

    public boolean isNumber() {
        return root.isNumber();
    }

    public boolean isIntegralNumber() {
        return root.isIntegralNumber();
    }

    public boolean isFloatingPointNumber() {
        return root.isFloatingPointNumber();
    }

    public boolean isInt() {
        return root.isInt();
    }

    public boolean isLong() {
        return root.isLong();
    }

    public boolean isDouble() {
        return root.isDouble();
    }

    public boolean isBigDecimal() {
        return root.isBigDecimal();
    }

    public boolean isTextual() {
        return root.isTextual();
    }

    public boolean isBoolean() {
        return root.isBoolean();
    }

    public boolean isNull() {
        return root.isNull();
    }
    
	private float str2float(String s, float defaultValue) {
		try {
			return Float.parseFloat(s);
		} catch (Exception e) {
			try {
				double d = Double.parseDouble(s);
				return (float)d;
			} catch (Exception ex) {
				//ignore
			}
			return defaultValue;
		}
	}
	
	private double str2double(String s, double defaultValue) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			try {
				double d = Double.parseDouble(s);
				return (double)d;
			} catch (Exception ex) {
				//ignore
			}
			return defaultValue;
		}
	}
}
