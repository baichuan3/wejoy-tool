package com.wejoy.util.top;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wejoy.constant.Constants;
import com.wejoy.util.ApiLogger;

/**
 * <pre>
 * 
 * 目标：提供高并发场景下的通用排序组件，高性能，LockFree
 * 
 * 
 * 特点：
 * 
 * SortedSet默认按升序排列，而TopN默认按降序排列，在程序级别做了装换
 * 
 * 依赖排序因子weight
 * 
 * Notice！！：使用跳表时，由于跳表的随机性，对于插入时按权值排序，删除或查找时按name比对的场景，会以较大的概率失败；
 * 
 * 解决办法：维护单独的Map，存放node.name到node的映射，contains和remove时都依赖次Map。
 * 
 * 
 * 使用说明：
 * 
 * # 涉及到元素更新，需要先copy出副本，在副本上更新，再覆盖回origin，不然可能导致remove失败
 * 
 * 
 * 备注：注意SkipList的特性
 * 
 * 
 * </pre>
 *
 * @param <T>
 */
public class MaxFloatTopN implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Log logger = LogFactory.getLog(MaxFloatTopN.class);
	
	private int maxElementsCount = Constants.TOPN_DEFAULT_MAX_SIZE; //Default
	
	private ConcurrentSkipListSet<WeightFloatNode> sortedSet = new ConcurrentSkipListSet<WeightFloatNode>();
	
	//由于是跳表，当跳表中的Object的权重因子变化时，有可能检索不到该元素，故需要额外维护node.name到node的映射，用来做remove操作
	private ConcurrentHashMap<String, WeightFloatNode> containsMap = new ConcurrentHashMap<String, WeightFloatNode>();
	
	//对应无并发场景，建议使用TreeSet实现
//	private TreeSet<WeightFloatNode> sortedSet = new TreeSet<WeightFloatNode>();
	
	public MaxFloatTopN(){
	}
	
	public MaxFloatTopN(int maxElementsCount){
		this.maxElementsCount = maxElementsCount;
	}
	
	//donothing if exist, otherwise add
	public void add(WeightFloatNode element) {
		try {
			if(element == null || StringUtils.isBlank(element.name)){
				return;
			}
			
			//慎用contains
			if(contains(element)){
				//do nothing
				return;
			}
			
			innerAdd(element);
			
		} catch (Exception e) {
			e.printStackTrace();
			ApiLogger.warn("MaxFloatTopN add error, ", e);
		}
	}
	
	//remove if exist, then add
	public void set(WeightFloatNode element) {
		try {
			if(element == null || StringUtils.isBlank(element.name)){
				return;
			}
			
			boolean removeRet = true;
			if(contains(element)){
				removeRet = remove(element);
				
				if(!removeRet){
					ApiLogger.warn("MaxFloatTopN when set, remove error, name=" + element.name);
				}
			}
				
			if(removeRet){
				innerAdd(element);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ApiLogger.warn("MaxFloatTopN set error, ", e);
		}
	}
	
	private boolean innerAdd(WeightFloatNode element){
		boolean ret = false;
		
		ret = sortedSet.add(element);
		if(ret){
			containsMap.put(element.name, element);
		}
		
		if(!ret){
			ApiLogger.warn("MaxFloatTopN innerAdd error, name=" + element.name);
		}
		
		if (maxElementsCount > 0 && size() > maxElementsCount) {
			removeLast();
		}
		
		return ret;
	}
	
	public boolean incr(WeightFloatNode element){
		return incr(element, 1);
	}
	
	public boolean decr(WeightFloatNode element){
		return decr(element, 1);
	}
	
	public boolean incr(WeightFloatNode element, float delta){
		if(element == null || StringUtils.isBlank(element.name)){
			return false;
		}
		
		boolean ret = true;
		WeightFloatNode node = get(element);
		
		if(node == null){
			element.weight = delta;
		}else{
			float newWeight = node.weight + delta;
			element.weight = newWeight;
		}
		set(element);
		
		return ret;
	}
	
	public boolean decr(WeightFloatNode element, long delta){
		if(element == null || StringUtils.isBlank(element.name)){
			return false;
		}
		
		boolean ret = true;
		WeightFloatNode node = get(element);
		
		if(node != null){
			float newWeight = node.weight - delta;
			element.weight = newWeight;
			if(newWeight < 0){
				remove(element);
			}else{
				set(element);
			}
		}
		
		return ret;
	}
	
	/**
	 * <pre>
	 * remove will only return true for the thread that actually caused the object to be removed.
     * </pre>
     * 
	 * @param element
	 * @return
	 */
	public boolean remove(WeightFloatNode element) {
		boolean ret = true;
		
		try {
			if(element == null || StringUtils.isBlank(element.name)){
				return false;
			}
			
			if(contains(element)){
				WeightFloatNode origin = containsMap.get(element.name);
				ret = sortedSet.remove(origin);
				//只有导致object真正被移除的那个线程，才把object从containMap中移除
				if(ret){
					containsMap.remove(element.name);
				}
				
				if(!ret){
					ApiLogger.warn("MaxFloatTopN remove error, name=" + element.name);
				}
			}
			
			
			return ret;
		} catch (Exception e) {
			ApiLogger.warn("MaxFloatTopN remove error, ", e);
		}
		return false;
	}
	
	public WeightFloatNode removeFirst(){
		try {
			WeightFloatNode node = sortedSet.pollLast();
			containsMap.remove(node.name);
			
			return node;
		} catch (Exception e) {
			ApiLogger.warn("MaxFloatTopN removeFirst error, ", e);
		}
		return null;
	}
	
	public WeightFloatNode removeLast(){
		try {
			WeightFloatNode node = sortedSet.pollFirst();
			containsMap.remove(node.name);
			
			return node;
		} catch (Exception e) {
			ApiLogger.warn("MaxFloatTopN removeLast error, ", e);
		}
		return null;
	}
	
	public WeightFloatNode getFirst(){
		return sortedSet.last();
	}
	
	public WeightFloatNode getLast(){
		return sortedSet.first();
	}
	
	public boolean contains(WeightFloatNode element){
		return containsMap.containsKey(element.name);
	}
	
	/**
	 * <pre>
	 * 
	 * 1）Java语言中并发容器的Size都不是实时更新的，只是一个大概值；
	 * 
	 * 2）在TopN场景中，考虑到缓存的量很小，能接受这种消耗
	 * 
	 * </pre>
	 * @return
	 */
	public int size(){
		return sortedSet.size();
	}
	
	public WeightFloatNode get(WeightFloatNode element){
		if(element == null || StringUtils.isBlank(element.name)){
			return null;
		}
		
		return containsMap.get(element.name);
	}
	
	//获取元素的Copy副本
	public WeightFloatNode getCopy(String name){
		WeightFloatNode copy = new WeightFloatNode();
		copy.name = name;
		if(StringUtils.isNotBlank(copy.name)){
			WeightFloatNode originNode = containsMap.get(copy.name);
			if(originNode != null){
				copy.weight = originNode.weight;
			}
		}
		
		return copy;
	}
	
	/**
	 * 尽可能减少内存Copy
	 * @return
	 */
	public  List<WeightFloatNode> getTopN(){
		return getTopN(maxElementsCount);
	}
	
	public  List<WeightFloatNode> getTopN(int n){
		//不建议使用descendingIterator() ，会导致返回的元素比实际少
		//Iterators are weakly consistent, returning elements reflecting the state of the set at some point at or since the creation of the iterator. 
		Iterator<WeightFloatNode> iterator = sortedSet.iterator();
		
		List<WeightFloatNode> elements = new ArrayList<WeightFloatNode>();
		while(iterator.hasNext()){
			elements.add(iterator.next());
		}
		
		Collections.reverse(elements);
		
		int count = n > elements.size() ? elements.size() : n;
		
		return elements.subList(0, count);
	}
	
	public boolean isEmpty(){
		return sortedSet.isEmpty();
	}
	
    public static void main(String[] args){
    	//case 1

    	WeightFloatNode travel1 = new WeightFloatNode();
    	travel1.name = "云南";
    	travel1.weight = 2;
    	WeightFloatNode travel2 = new WeightFloatNode();
    	travel2.name = "丽江";
    	travel2.weight = 0;
    	WeightFloatNode travel3 = new WeightFloatNode();
    	travel3.name = "三亚";
    	travel3.weight = 0;
    	
    	WeightFloatNode travel4 = new WeightFloatNode();
    	travel4.name = "武汉";
    	travel4.weight = 2;
    	
    	testSet.set(travel1);
    	testSet.set(travel2);
    	testSet.set(travel3);
    	testSet.set(travel4);
    	WeightFloatNode travel5 = new WeightFloatNode();
    	travel5.name = "武汉";
    	travel5.weight = 5;
//    	testSet.set(travel5);
    	
//    	testSet.remove(travel2);
    	WeightFloatNode travel6 = new WeightFloatNode();
//    	travel6.name = "武汉";
//    	travel6.weight = 7;
//    	testSet.set(travel6);
//    	WeightFloatNode travel6 = new WeightFloatNode();
    	travel6.name = "三亚";
    	travel6.weight = 7;
    	testSet.set(travel6);
//    	testSet.remove(travel6);
    	
    	WeightFloatNode travel8 = new WeightFloatNode();
    	travel8.name = "罗成";
    	travel8.weight=10;
    	testSet.set(travel8);
    	
//    	WeightFloatNode nodet = new WeightFloatNode();
//    	nodet.name="武汉";
    			
//    	WeightFloatNode node = testSet.get(nodet);
//    	node.weight++;
//    	testSet.set(node);
    	
//    	MaxTopN test = new MaxTopN();
//    	test.startTask();
    	
    	List<WeightFloatNode> list = testSet.getTopN();
    	for(WeightFloatNode e : list){
    		System.out.println(e.name+ "," + e.weight);
    	}
    	System.out.println("++++++++++++++++++++++++++++++++++++++++");
    	WeightFloatNode nodenode = new WeightFloatNode();
    	nodenode.name = "云南";
    	WeightFloatNode eNode = testSet.get(nodenode);
    	eNode.weight++;
    	testSet.set(eNode);
    	List<WeightFloatNode> list2 = testSet.getTopN();
    	for(WeightFloatNode e : list2){
    		System.out.println(e.name+ "," + e.weight);
    	}
//    	System.out.println("befor thread -----------------");
    	
    	
    	
    	//case 2
//    	MaxTopN<DateMode> testSet2 = new MaxTopN<DateMode>();
//    	DateMode date1 = new DateMode();
//    	long ms = System.currentTimeMillis();
//    	date1.weight = new Date(ms);
//    	
//    	DateMode date2 = new DateMode();
//    	date2.weight = new Date(ms - 20000);
//    	
//    	DateMode date3 = new DateMode();
//    	date3.weight = new Date(ms - 40000);
//    	
//    	DateMode date4 = new DateMode();
//    	date4.weight = new Date(ms - 123400);
//    	
//    	testSet2.add(date1);
//    	testSet2.add(date2);
//    	testSet2.add(date3);
//    	testSet2.add(date4);
//    	List<DateMode> list2 = testSet2.getTopN();
//    	for(DateMode e : list2){
//    		System.out.println(CommonUtil.formatDate(e.weight));
//    	}
    	
		//case3
//    	MaxTopN<NormalNode> testSet3 = new MaxTopN<NormalNode>();
//    	
//		NormalNode date1 = new NormalNode();
//		date1.name = "111";
//		date1.weight = System.nanoTime();
//		testSet3.add(date1);
//		
//		NormalNode date3 = new NormalNode();
//		date3.name = "333";
//		date3.weight = System.nanoTime();
//		testSet3.add(date3);
//		
//		NormalNode date4 = new NormalNode();
//		date4.name = "444";
//		date4.weight = System.nanoTime();
//		testSet3.add(date4);
//		
//		NormalNode date2 = new NormalNode();
//		date2.name = "222";
//		date2.weight = System.nanoTime();
//		testSet3.add(date2);
		
//		testSet3.remove(date1);
//		testSet3.remove(date4);
		
//		NormalNode date5 = new NormalNode();
//		date5.name = "444";
//		date5.weight = 9999;
//		testSet3.set(date5);
//		NormalNode date6 = new NormalNode();
//		date6.name = "777";
//		date6.weight = 9999;
//		testSet3.set(date6);
//		System.out.println(testSet3.contains(date5));
//		System.out.println(testSet3.get(date5).toJsonWithStringKey());
//		System.out.println("remove ret="+testSet3.remove(date5));
//		System.out.println(testSet3.add(date5));
//		testSet3.add(date5);
//		System.out.println(testSet3.getFirst());
//		List<NormalNode> list3 = testSet3.getTopN();
//		for(NormalNode e : list3){
//			System.out.println(e.name);
//		}
		
//		System.out.println(list3.indexOf(date5));
    }
    
    private void startTask(){
    	Thread thread1 = new ModifyTask();
    	thread1.start();
    }
    
	public static MaxFloatTopN testSet = new MaxFloatTopN();
    
    private class ModifyTask extends Thread{
    	@Override
    	public void run(){
        	WeightFloatNode travel7 = new WeightFloatNode();
//        	travel7.name = "武汉";
//        	travel7.weight = 8;
////        	testSet.add(travel7);
//        	testSet.set(travel7);
        	

    	}
    }
}
