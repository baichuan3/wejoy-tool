package com.wejoy.util.top;


import java.io.Serializable;

import com.wejoy.util.ApiLogger;
import com.wejoy.util.JsonBuilder;
import com.wejoy.util.JsonWrapper;

/**
 * 
 * 默认降序排列，升序排序使用WeightAscNode
 *
 */
public class WeightNode implements Comparable<WeightNode>, Serializable{
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		public String name = "";
		public long weight; //利用微秒来作为权重，millisTime在循环时容易重复，nanoTime则19位，有溢出风险，微秒最佳。仅针对默认值
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
		    if(!(obj instanceof WeightNode)){
		        return false;
		      }
			
			WeightNode other = (WeightNode) obj;
			if(this.name.equals(other.name)){
				return true;
			}
			
			return false;
		}
		
		@Override
		public int compareTo(WeightNode other) {
			//add and delete depends on this method
			boolean equalsName = this.name.equals(other.name);
			if(equalsName){
				return 0;
			}
			
			int delta = Long.signum(this.weight - other.weight);
			
			//确保同样weight但name不同的node能共存 
			if((!equalsName) && (delta == 0)){
				int hashDelta = Long.signum(this.toBKDRHash() - other.toBKDRHash());
				
				return hashDelta > 0 ? 1 : -1;
			}
			
			return delta;
		}
		
	  //Notice：不要直接使用String.toHashCode()，当weight值相等时，排序结果是随机的(由于负数)，导致remove失败	
	  public long toBKDRHash()
	   {
		  String str = this.name; 
		  
		  //使用131会导致溢出
	      long seed = 31; // 31 131 1313 13131 131313 etc..
	      long hash = 0;
	      for(int i = 0; i < str.length(); i++)
	      {
	         hash = (hash * seed) + str.charAt(i);
	      }
	      return hash;
	   }
		
		@Override
		public int hashCode(){
			return this.name.hashCode();
		}
		
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Node{")
            .append(name).append(", ").append(weight)
            .append("}");
            return sb.toString();
        }
		
		public String toJsonWithStringKey(){
			return toJsonBuilder().toString();
		}
		
		public void parseWithStringKey(String json){
			try {
				JsonWrapper wrapper = new JsonWrapper(json);
				this.name = wrapper.get("name");
				this.weight = wrapper.getLong("weight");
			} catch (Exception e) {
				ApiLogger.warn("WeightNode parse error, " , e);
			}
		}
		
		public JsonBuilder toJsonBuilder(){
			JsonBuilder builder = new JsonBuilder();
			builder.append("name", name);
			builder.append("weight", weight);
			
			return builder.flip();
		}
		
		public static WeightNode getInstance(String name){
			WeightNode node = new WeightNode();
			node.name = name;
//			node.weight = System.currentTimeMillis();
			node.weight = System.nanoTime() / 1000;
			return node;
		}
		
		public static void main(String[] args){
			System.out.println(Double.MAX_VALUE);
		}
		
}
