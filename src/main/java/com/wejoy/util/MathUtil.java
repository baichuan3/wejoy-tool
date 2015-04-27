package com.wejoy.util;


import java.math.BigDecimal;

public class MathUtil {

	public static float round(float value, int scale){
		BigDecimal decimal = new BigDecimal(value);  
		return decimal.setScale(scale,  BigDecimal.ROUND_HALF_UP).floatValue(); // 四舍五入
	}
	
	public static float truncate(float value, int scale){
		BigDecimal decimal = new BigDecimal(value);  
		return decimal.setScale(scale,  BigDecimal.ROUND_DOWN).floatValue(); // 截断处理
	}
	
	
	public static String getRoundStr(float value, int scale){
		BigDecimal decimal = new BigDecimal(value);  
		return String.valueOf(decimal.setScale(scale,  BigDecimal.ROUND_HALF_UP).floatValue()); // 四舍五入
	}
	
	public static float devide(float d1, float d2){
		if(d2 == 0.0f){
			return 0.0f;
		}
		
		return d1/d2;
	}
	
	public static float devide(int d1, int d2){
		if(d2 == 0.0){
			return 0.0f;
		}
		
		return (d1*1.0f)/d2;
	}
	
	public static float devide(int d1, int d2, int scale){
		if(d2 == 0.0){
			return 0.0f;
		}
		
		return round((d1*1.0f)/d2, scale);
	}
	
	public static float devide(float d1, float d2, int scale){
		if(d2 == 0.0){
			return 0.0f;
		}
		
		return round((d1*1.0f)/d2, scale);
	}
	
	
	public static int getRealCount(int baseCount, int realCount, int counter, int base){
		if(baseCount <= 0){
			return realCount;
		}
		
		if(realCount <= base){
			return baseCount;
		}
		
		return (int) (Math.ceil(baseCount*1.0/counter*realCount));
	}
	
	public static void main(String[] args){
		System.out.println(round(1.642f, 2));
		System.out.println(round(1.647f, 2));
		System.out.println(truncate(1.6f, 2));
		System.out.println(truncate(1.60f, 2));
		System.out.println(truncate(-1.642f, 2));
		System.out.println(truncate(1.647f, 2));
//		System.out.println(Math.ceil(12.6));
//		System.out.println(MathUtil.devide(12, 6));
//		System.out.println(getRealCount(1872, 2390, 2000));
	}
}
