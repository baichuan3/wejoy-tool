package com.wejoy.common.cache;



import java.io.Serializable;

/**
 * 
 * <pre>
 * 
 * 适用于：收发件箱场景，uid--> long[]，id本身需有序，以及与此相似的场景
 * 
 * 支持超过最大值剔除
 * 
 * 默认倒序排序： desc
 * 
 * Q：为何有了MaxTopN组件后，还要写这个组件
 * 
 * A：一切为了节省内存
 * 
 * Notice： 高并发时有潜在的性能问题
 * 
 * </pre>
 * 
 * 
 * 
 */
public class BoxCache implements Serializable{
	private static final long serialVersionUID = -2006977453274132467L;
	
	public int count;//计数器，表示当前元素占用的数目
	public int maxCount; //表示boxCache的最大数目，用来判断是否要剔除
    private long[] elements;//数组，存储实际的数据
    
    private static final int DEFAULT_SIZE = 200;
    private static final int MAX_SIZE = 2000;
    
    //构造器，初始化
    public BoxCache() {
    	this(DEFAULT_SIZE); //默认容量
    }
    
    public BoxCache(int size) {
    	if(size > MAX_SIZE){
    		size = MAX_SIZE;
    	}
    	
        elements = new long[size];
        maxCount = size;
    }
 
    //实现heap接口的add方法
    public synchronized void add(long element) {
    	//重复元素不添加
    	if(indexOf(element) >= 0){
    		return;
    	}
    		
        //初始数据建立 
        if(count == 0){
        	elements[0] = element;
        	incrCount();
        	return;
        }
        
        //如果数组已满则剔除末尾
        if (count >= getMaxSize()) {
        	if(getLast() >= element){
        		return;
        	}
        	
        	//否则剔除最后一位
        	removeLast();
        }
        
        //排序移位
        int index = searchIndex(element);
        long[] temp = new long[elements.length];
        System.arraycopy(elements, 0, temp, 0, index);
        temp[index] = element;
        System.arraycopy(elements, index, temp, (index + 1), (count - index));
        elements = temp;

        incrCount();
    }
    
    //匹配最靠近的index
    public int searchIndex(long element){
    	int index=0;
    	for(; index < count; index++ ){
    		if(elements[index] <= element){
    			break;
    		}
    	}
    	
    	return index;
    }
 
    public long[] getALl() {
    	long[] tmpArr = new long[count];
    	System.arraycopy(elements, 0, tmpArr, 0, count);
    	return tmpArr;
    }
    
    public long[] getBySize(int size) {
    	size = size < count ? size : count;
    	long[] tmpArr = new long[size];
    	System.arraycopy(elements, 0, tmpArr, 0, size);
    	return tmpArr;
    }
    
    //返回第一个元素
    public long getFirst(){
    	if(count == 0){
    		return 0;
    	}
    	
    	return elements[0];
    }
    
    //返回最后一个元素
    public long getLast(){
    	if(count == 0){
    		return 0;
    	}
    	
    	return elements[count - 1];
    }
    
    public long get(int index){
    	return elements[index];
    }
    
    public synchronized void remove(long element){
    	if(elements == null) return;
    	
    	int index = indexOf(element);
    	if(index < 0){
    		return;
    	}
    	
    	System.arraycopy(elements, (index + 1), elements, index, (count - (index + 1)));
    	decrCount();
    }
    
    public synchronized void removeLast(){
    	decrCount();
    }
    
    private void incrCount(){
    	count ++;
    }
    
    private void decrCount(){
    	if(size() > 0){
    		count --;
    	}
    }
    
    public int size(){
    	return count;
    }
    
    public int getMaxSize(){
    	return maxCount;
    }
    
    public int indexOf(long element){
    	int size = size();
    	if(size <= 0){
    		return -1;
    	}
    	
		return indexedBinarySearch(getALl(), element, 0, size - 1);
    }
    
	private  int indexedBinarySearch(long[] values, long key, int fromIndex, int toIndex) {
		int low = fromIndex;
		int high = toIndex;

		if(key == values[low]){
			return low;
		}else if(key == values[high]){
			return high;
		}
		
		while (low <= high) {
			int mid = (low + high) >>> 1;
			long midVal = values[mid];
			int cmp = Long.signum(midVal - key);

			if (cmp > 0)
				low = mid + 1;
			else if (cmp < 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for(int index=0; index<count; index++){
			if(index > 0){
				sb.append(", ");
			}
			
			sb.append(elements[index]);
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args){
		BoxCache boxCache = new BoxCache(4);
		
		boxCache.add(1);
		boxCache.add(0);
		boxCache.add(10);
		boxCache.add(7);
		boxCache.add(21);
		boxCache.add(5);
		boxCache.add(5);
		boxCache.add(11);
		boxCache.add(-1);
		boxCache.add(-10);
		boxCache.add(30);
		
		System.out.println(boxCache.indexOf(1));
		System.out.println(boxCache.indexOf(10));
		
		boxCache.remove(7);
		boxCache.removeLast();
		
		System.out.println(boxCache.getALl().length);
		System.out.println(boxCache.toString());
	}
}
