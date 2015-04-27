package com.wejoy.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wejoy.common.cache.DiskCacheService;
import com.wejoy.util.ApiLogger;
import com.wejoy.util.CommonUtil;
import com.wejoy.util.JsonWrapper;
import com.wejoy.util.top.MaxFloatTopN;
import com.wejoy.util.top.MaxTopN;
import com.wejoy.util.top.WeightAscNode;
import com.wejoy.util.top.WeightFloatNode;
import com.wejoy.util.top.WeightNode;


/**
 * 基类Service提供以下功能：
 * 
 * 1）topN组件翻页
 * 2）topN组件缓存到diskcache及reload加载
 * 
 *
 */
public class BaseService {
	//按逆序按个读取topN缓存文件，默认值
	protected MaxTopN readTopNDataDiskCacheByOne(String filePath, String filename, String debugId){
		//与时间正相关，每次reload时需清除，避免weight大的活动无法被淘汰
		MaxTopN topNCopy = new MaxTopN(TOP_MAZ_SIZE);
		
		List<String> cacheDataList = diskCacheService.loadData(filePath, filename);
		
		if(cacheDataList != null && !cacheDataList.isEmpty()) {
			for (String data : cacheDataList) {
				try{
					JsonWrapper wrapper = new JsonWrapper(data);
					WeightNode node = new WeightNode();
					node.name = wrapper.get("name");
					node.weight = wrapper.getLong("weight");
					
					if(StringUtils.isBlank(node.name)){
						continue;
					}
					
					topNCopy.set(node);
				}catch(Exception e){
					ApiLogger.warn("readTopNDataDiskCacheByOne load " + debugId + " data error", e);
				}
			}
		}
		
		return topNCopy;
	}
	
	//按正序按个读取topN缓存文件
	protected MaxTopN readTopNDataDiskCacheByOneAsc(String filePath, String filename, String debugId) {
		//与时间正相关，每次reload时需清除，避免weight大的活动无法被淘汰
		MaxTopN topNCopy = new MaxTopN(TOP_MAZ_SIZE);
		
		List<String> cacheDataList = diskCacheService.loadData(filePath, filename);
		
		if(cacheDataList != null && !cacheDataList.isEmpty()) {
			for (String data : cacheDataList) {
				try{
					JsonWrapper wrapper = new JsonWrapper(data);
					WeightAscNode node = new WeightAscNode();
					node.name = wrapper.get("name");
					node.weight = wrapper.getLong("weight");
					
					if(StringUtils.isBlank(node.name)){
						continue;
					}
					
					topNCopy.set(node);
				}catch(Exception e){
					ApiLogger.warn("readTopNDataDiskCacheByOneAsc load " + debugId + " data error", e);
				}
			}
		}
		
		return topNCopy;
	}
	
	protected void dumpTopNDataByOne(MaxTopN topN, String filePath, String filename, String debugId){
		List<String> rawList = new ArrayList<String>();
		List<WeightNode> topNCopy = topN.getTopN();
		for(WeightNode node : topNCopy){
			rawList.add(node.toJsonWithStringKey());
		}
		diskCacheService.writeDiskCache(filePath, filename, rawList);
	}
	
	protected List<Long> getIdsFromTopByOne(long cursor, int count, MaxTopN topN, int topLimit) {
		 return getIdsFromTopByOne(cursor, count, false, topN, topLimit);
	 }
	
	
	/**
	 * 
	 * @param cursor
	 * @param count
	 * @param topN
	 * @param topLimit
	 * @return
	 */
	protected List<Long> getIdsFromTopByOne(long cursor, int count, boolean skipWhenNotExist, MaxTopN topN, int topLimit) {
		List<Long> ids = new ArrayList<Long>();
		
		//tid排序规则可能是无序的，不能简单根据sinceid比大小实现
		if (topN.size() == 0) return ids;
		
		List<WeightNode> nodeList = topN.getTopN(topLimit);
		if(nodeList == null || nodeList.isEmpty()) return ids;
		
		int indexOf = getIndexOfNodeList(cursor, nodeList);
		//确定index位置,不存在直接放回空
		if(indexOf < 0){
			if(skipWhenNotExist){
				return ids;
			}else {
				indexOf = 0;
			}
		}else{
			indexOf = indexOf + 1;
		}

		int validCount = 0;
		int nodeListSize = nodeList.size();
		if(indexOf <= nodeListSize){
			for(int i=indexOf ; i<nodeListSize ; i++){
				long id =CommonUtil.parseLong(nodeList.get(i).name);
				if(++validCount > count) {
					break;
				}
				
				ids.add(id);
			}
		}else{
			//do nothing
		}
		
		return ids;
	}
	
	protected int getIndexOfNodeList(long cursor, List<WeightNode> normalNodes){
		int indexOf = -1;
		
		if(cursor <= 0){
			return indexOf;
		}
		
		WeightNode cursorNode = new WeightNode();
		cursorNode.name = String.valueOf(cursor); 
		indexOf = normalNodes.indexOf(cursorNode);
		
		if(indexOf < 0){
			indexOf = -1;
		}
		
		return indexOf;
	}
	
	protected List<Long> getIdsFromTop(long cursor, int count, MaxTopN topN, int topLimit) {
		List<Long> ids = new ArrayList<Long>();
		
		//id排序规则可能是无序的，不能简单根据sinceid比大小实现
		if (topN.size() == 0) return ids;
		
		List<WeightNode> nodeList = topN.getTopN(topLimit);
		if(nodeList == null || nodeList.isEmpty()) return ids;
		
		int indexOf = getIndexOfNodeList(cursor, nodeList);
		//确定index位置
		if(indexOf < 0){
			indexOf = 0;
		}else{
			indexOf = indexOf + 1;
		}

		int validCount = 0;
		int nodeListSize = nodeList.size();
		if(indexOf <= nodeListSize){
			for(int i=indexOf ; i<nodeListSize ; i++){
				long id =CommonUtil.parseLong(nodeList.get(i).name);
				if(++validCount > count) {
					break;
				}
				
				ids.add(id);
			}
		}else{
			//do nothing
		}
		
		return ids;
	}
	
	
	//按逆序按个读取topN缓存文件，默认值
	protected MaxFloatTopN readFloatTopNDataDiskCacheByOne(String filePath, String filename, String debugId){
		//与时间正相关，每次reload时需清除，避免weight大的活动无法被淘汰
		MaxFloatTopN topNCopy = new MaxFloatTopN(TOP_MAZ_SIZE);
		
		List<String> cacheDataList = diskCacheService.loadData(filePath, filename);
		
		if(cacheDataList != null && !cacheDataList.isEmpty()) {
			for (String data : cacheDataList) {
				try{
					JsonWrapper wrapper = new JsonWrapper(data);
					WeightFloatNode node = new WeightFloatNode();
					node.name = wrapper.get("name");
					node.weight = wrapper.getFloat("weight");
					
					if(StringUtils.isBlank(node.name)){
						continue;
					}
					
					topNCopy.set(node);
				}catch(Exception e){
					ApiLogger.warn("readFloatTopNDataDiskCacheByOne load " + debugId + " data error", e);
				}
			}
		}
		
		return topNCopy;
	}
	
	protected void dumpFloatTopNDataByOne(MaxFloatTopN topN, String filePath, String filename, String debugId){
		List<String> rawList = new ArrayList<String>();
		List<WeightFloatNode> topNCopy = topN.getTopN();
		for(WeightFloatNode node : topNCopy){
			rawList.add(node.toJsonWithStringKey());
		}
		diskCacheService.writeDiskCache(filePath, filename, rawList);
	}
	
	public static final int TOP_MAZ_SIZE = 1000; 
	
	public DiskCacheService diskCacheService;

	public void setDiskCacheService(DiskCacheService diskCacheService) {
		this.diskCacheService = diskCacheService;
	}
	
	public static void main(String[] args){
	}
}
