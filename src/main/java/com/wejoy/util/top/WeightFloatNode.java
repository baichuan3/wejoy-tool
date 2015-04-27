package com.wejoy.util.top;

import com.wejoy.util.ApiLogger;
import com.wejoy.util.JsonBuilder;
import com.wejoy.util.JsonWrapper;

/**
 * 
 * 默认降序排列，升序排序使用WeightAscNode
 *
 */
public class WeightFloatNode implements Comparable<WeightFloatNode>{
		public String name = "";
		public float weight;
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
		    if(!(obj instanceof WeightFloatNode)){
		        return false;
		      }
			
			WeightFloatNode other = (WeightFloatNode) obj;
			if(this.name.equals(other.name)){
				return true;
			}
			
			return false;
		}
		
		@Override
		public int compareTo(WeightFloatNode other) {
			//add and delete depends on this method
			boolean equalsName = this.name.equals(other.name);
			if(equalsName){
				return 0;
			}
			
			float minus = this.weight - other.weight;
			int delta = 0;
			if(minus > 0){
				delta = 1;
			}else if(minus < 0){
				delta = -1;
			} else{
				delta = 0;
			}
			
			//确保同样weight但name不同的node能共存 
			if((!equalsName) && (delta == 0)){
				int hashDelta = Long.signum(this.toBKDRHash() - other.toBKDRHash());
				
				return hashDelta > 0 ? 1 : -1;
			}
			
			return delta;
		}
		
	  public long toBKDRHash()
	   {
		  String str = this.name; 
		  
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
				this.weight = wrapper.getFloat("weight");
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
		
		public static WeightFloatNode getInstance(String name){
			WeightFloatNode node = new WeightFloatNode();
			node.name = name;
//			node.weight = System.currentTimeMillis();
			node.weight = System.nanoTime() / 1000;
			return node;
		}
		
		public static void main(String[] args){
			System.out.println(Double.MAX_VALUE);
			
			String s1 = new String("111");
			String s2 = new String("111");
			System.out.println(s1.hashCode());
			System.out.println(s2.hashCode());
		}
		
		
}
