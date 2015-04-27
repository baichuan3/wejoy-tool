package com.wejoy.util.top;


import java.util.Date;

import com.wejoy.util.ApiLogger;
import com.wejoy.util.DateUtil;
import com.wejoy.util.JsonBuilder;
import com.wejoy.util.JsonWrapper;



public class DateNode implements Comparable<Object>{
		public String name;
		public Date weight;
		
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
		public int compareTo(Object o) {
			DateNode other = (DateNode) o;
			//just because use key to delete it
			if(this.name.equals(other.name)){
				return 0;
			}
			
			int delta = Long.signum(weight.getTime() - other.weight.getTime());
			
			//确保同样weight但name不同的node能共存 
			if((!this.name.equals(other.name)) && (delta == 0)){
				int hashCode = (this.hashCode() - other.hashCode());
				
				return hashCode > 0 ? 1 : -1;
			}
			
			return delta;
		}
		
		@Override
		public int hashCode(){
			ApiLogger.warn("WeightNode hashCdoe hits");
			return this.name.hashCode();
		}
		
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Node{")
            .append(name).append(", ").append(DateUtil.formatDate(weight))
            .append("}");
            return sb.toString();
        }
        
		
		public String toJsonWithStringKey(){
			JsonBuilder builder = new JsonBuilder();
			builder.append("name", (String)name);
			builder.append("weight", weight.getTime());
			
			return builder.flip().toString();
		}
		
		public void parseWithStringKey(String json){
			try {
				JsonWrapper wrapper = new JsonWrapper(json);
				this.name = wrapper.get("name");
				this.weight = new Date(wrapper.getLong("weight"));
			} catch (Exception e) {
				ApiLogger.warn("DateNode parse error, " , e);
			}
		}
}
