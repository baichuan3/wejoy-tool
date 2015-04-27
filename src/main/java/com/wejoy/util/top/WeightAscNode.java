package com.wejoy.util.top;


import java.util.List;

public class WeightAscNode extends WeightNode{
		
		@Override
		public int compareTo(WeightNode other) {
			//add and delete depends on this method
			boolean equalsName = other.name.equals(this.name);
			if(equalsName){
				return 0;
			}
			
			int delta = Long.signum(other.weight - this.weight);
			
			//确保同样weight但name不同的node能共存 
			if((!equalsName) && (delta == 0)){
				int hashDelta = Long.signum(this.toBKDRHash() - other.toBKDRHash());
				
				return hashDelta > 0 ? 1 : -1;
			}
			
			return delta;
		}
		
		public static void main(String[] args){
//			System.out.println(Double.MAX_VALUE);
			
			
			WeightAscNode node1 = new WeightAscNode();
			node1.name = "111";
			node1.weight = 111;
			
			WeightAscNode node2 = new WeightAscNode();
			node2.name = "222";
			node2.weight = 222;
			
			WeightAscNode node3 = new WeightAscNode();
			node3.name = "333";
			node3.weight = 333;
			
			MaxTopN topN = new MaxTopN(2);
			topN.set(node1);
			topN.set(node2);
			topN.set(node3);
			
	    	List<WeightNode> list2 = topN.getTopN();
	    	for(WeightNode e : list2){
	    		System.out.println(e.name+ "," + e.weight);
	    	}
		}
		
}
