package com.wejoy.util;
 
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DateUtil {
	public static String DEFAULT_CHARSET = "utf-8";
	public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static String DATE_NORMAL_FORMAT = "yyyy-MM-dd";
	public static String DATE_YY_FORMAT = "yyMMdd";
	public static String DATE_MONTH_FORMAT = "yyMM";
	public static String DATE_WEEK_FORMAT = "yyww";
	public static String hourDateFormat = "yyMMddHH";
	public static String yearDateFormat = "yyyy";
	public static String DATE_FORMAT_HM = "yyyy-MM-dd HH:mm";
	
	public static String DATE_TRAVEL_FORMAT = "yyyy.MM.dd";
	public static String monthDateFormat = "MM";
	public static String dayDateFormat = "dd";
	public static String hhmmssDateFormat = "HH:mm:ss";
	
	public static DateFormat dataTimeFormat = new SimpleDateFormat(DATE_NORMAL_FORMAT);
	public static DateFormat simpleDateTimeFormat = new SimpleDateFormat("yyMMdd");
	public static SimpleDateFormat defaultSdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT); 
	public static SimpleDateFormat hourDateSdf = new SimpleDateFormat("yyMMddHH"); 
	
	public final static long ONE_DAY_THRESHOLD = 24 * 60 * 60; //单位：秒
	public static final long A_DAY_MILLIS = 24*60*60*1000; //单位，ms
	
	private static ThreadLocal<SimpleDateFormat> simpleDateFormatProvider = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		}
	};
	
	public static String formatDate(Date date) {
		return simpleDateFormatProvider.get().format(date);
	}
	
	public static String formatTime(long time) {
		try{
			Date date = new Date(time);
			String ret = defaultSdf.format(date);
			return ret;
		}catch(Exception e){
			ApiLogger.warn("DateUtil formatTime error, " ,e );
			return null;
		}
	}
	
	public static String formatTime(long time, String format) {
		try{
			Date date = new Date(time);
			SimpleDateFormat dsf = new SimpleDateFormat(format);
			String ret = dsf.format(date);
			return ret;
		}catch(Exception e){
			ApiLogger.warn("DateUtil formatTime error, " ,e );
			return null;
		}
	}
	
	public static String formatDate(Date date, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		} catch (Exception e) {
			ApiLogger.warn("DateUtil formatDate error, ", e);
			return null;
		}
	}
	
	public static String formatDate(String str, String originFormat, String destFormat){
		Date date = parseDate(str, originFormat);
		if(date == null) return null;
		
		return formatDate(date, destFormat);
	}
	
	public static Date parseDate(String str, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(str);
		} catch (Exception e) {
			ApiLogger.warn("parseDate error, ", e);
			return null;
		}
	}
	
	public static long parseDateTime(String str, String format) {
		try {
			Date date = parseDate(str, format);
			if(date != null){
				return date.getTime();
			}else{
				return 0;
			}
		} catch (Exception e) {
			ApiLogger.warn("parseDateTime error, ", e);
			return 0;
		}
	}
	
	public static List<String> getDatesAfter(long time, int days){
		List<String> dateList = new ArrayList<String>();
		
		Calendar thusDay = new GregorianCalendar();
		thusDay.setTimeInMillis(time);
		dateList.add(formatDate(thusDay.getTime()));
		
		for(int afterDay =1; afterDay<days; afterDay++){
			thusDay.add(Calendar.DAY_OF_YEAR, 1);
			
			dateList.add(formatDate(thusDay.getTime()));
		}
		
		return dateList;
	}
	
	public static List<String> getDatesAfterAll(long time){
		long curr = System.currentTimeMillis();
		
		List<String> dateList = new ArrayList<String>();
		
		Calendar thusDay = new GregorianCalendar();
		thusDay.setTimeInMillis(time);
		dateList.add(formatDate(thusDay.getTime(), hourDateFormat));
		
		while(thusDay.getTimeInMillis() <= curr){
			thusDay.add(Calendar.HOUR_OF_DAY, 1);
			
			dateList.add(formatDate(thusDay.getTime(), hourDateFormat));
		}
		
		return dateList;
	}
	
	public static long getDateBefore(Date date, long deltaSecond){
		return date.getTime() - deltaSecond;
	}
	
	public static long getZeroTimeByTime(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar.getTimeInMillis();
	}
	
	public static long getAfternoonTimeByTime(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		return calendar.getTimeInMillis();
	}
	
	public static long getCurrZeroTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar.getTimeInMillis();
	}
	
	public static long getDayZeroTime(int days){
		Calendar calendar = Calendar.getInstance();
		
		calendar.add(Calendar.DAY_OF_YEAR, days);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar.getTimeInMillis();
	}
	
	public static long getZeroTimeByHour(int hours){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		return calendar.getTimeInMillis();
	}
	
	public static Date addDay(Date date, int days){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		
		return calendar.getTime();
	}
	
	public static long addDayTime(long time, int days){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		
		return calendar.getTime().getTime();
	}
	
	public static long addHour(long time, int hours){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		
		return calendar.getTime().getTime();
	}
	
	public static long addSecond(long time, int seconds){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.add(Calendar.SECOND, seconds);
		
		return calendar.getTime().getTime();
	}
	
	//unit: millsecond
	public static boolean isTodayDate(long dateTime){
		long currZeroTime = getCurrZeroTime();
		
		long delta = dateTime - currZeroTime;
		return (delta <= A_DAY_MILLIS && delta >= 0) ? true : false;
	}
	
	public static String getPaddingMonthOrDay(String monthOrDay){
		if(StringUtils.isBlank(monthOrDay)){
			return monthOrDay;
		}
		
		if(monthOrDay.length() == 1){
			monthOrDay = "0" + monthOrDay;
		}
		
		return monthOrDay;
	}
	
	public static boolean isAfterToday(String presentDate, String format){
		Date date = parseDate(presentDate, format);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		long delta = date.getTime()/1000 - calendar.getTimeInMillis()/1000; //消除掉ms带来的误差
		
		return delta >= 0 ? true : false;
	}
	
	public static boolean isBeforeDate(String presentDate, String format, int days){
		Date date = parseDate(presentDate, format);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, days);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		long delta = calendar.getTimeInMillis()/1000 - date.getTime()/1000; //消除掉ms带来的误差
		
		return delta >= 0 ? true : false;
	}

	public static String getCurrYear(){
		return formatDate(new Date(), yearDateFormat);
	}
	
	public static String getCurrMonth(){
		return formatDate(new Date(), monthDateFormat);
	}
	
	public static String getCurrDay(){
		return formatDate(new Date(), dayDateFormat);
	}
	
	public static int getCurrHour(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	//11~14点
	public static boolean isMiddleDay(){
		int currHour = getCurrHour();
		
		return currHour >= 11 && currHour <= 14;
	}
	
	//20~23点
	public static boolean isNightDay(){
		int currHour = getCurrHour();
		
		return currHour >= 20 && currHour <= 23;
	}
	
    //unit ： ms
    public static boolean isValidSoon(long time, long period){
    	long currTime = System.currentTimeMillis();
    	if((time - currTime) >  period){
    		return false;
    	}
    	
    	return true;
    }
    
    //以天为单位,计算相邻两天相隔的天数
    public static int getDaysBetweenTwoTime(long date1, long date2){
    	int days = 0;
    	long nextdate = getZeroTimeByTime(date1);
    	for(;;){
    		if(nextdate < date2){
    			days++;
    			nextdate = addDayTime(nextdate, 1);
    		}else{
    			break;
    		}
    	}
    	
    	return days;
    }
    
    //period_time单位是秒，表示距离今天0点开始的秒数
    public static int getCurrPeriodTime(){
    	long currZeroTime = getCurrZeroTime();
    	long currTime = System.currentTimeMillis();
    	
    	return (int) ((currTime - currZeroTime) / 1000);
    }
    
    public static int getPeriodTimeFromTime(long time){
    	long zeroTime = getZeroTimeByTime(time);
    	
    	return (int)((time - zeroTime)/1000);
    }
    
    public static boolean isTodayTime(long time){
    	long zeroTime = getZeroTimeByTime(time);
    	long currZeroTime = getCurrZeroTime();
    	
    	return ((zeroTime/1000) == (currZeroTime/1000));
    }
    
    //默认的是以Sunday为一周的第一天，调整为按自然周来计算，周一为第一天
    public static int getDayOfWeek(long time){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(time);
    	//一周第一天是否为星期天
    	boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);
    	//获取周几
    	int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
    	//若一周第一天为星期天，则-1
    	if(isFirstSunday){
    	  weekDay = weekDay - 1;
    	  if(weekDay == 0){
    	    weekDay = 7;
    	  }
    	}	
    	
    	return weekDay;
    }
    
    public static int getDayOfMonth(long time){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(time);
    	int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
    	return monthDay;
    }
    
    //unit ： second
    public static boolean isValidDate(long time, long period){
    	long currTime = System.currentTimeMillis() / 1000;
    	if((currTime - time) >  period){
    		return false;
    	}
    	
    	return true;
    }
    
    //unit ： second
    public static boolean isValidBeforeDate(long time, long period){
    	long currTime = System.currentTimeMillis() / 1000;
    	if((time - currTime) >  period){
    		return false;
    	}
    	
    	return true;
    }
    
	
	public static long getTimeTag(){
		return  System.nanoTime()/1000;
	}
	
	//获取当天过期的剩余时间
	public static long getExpireDayTime(){
		long nextZeroTime =  DateUtil.getDayZeroTime(1);
		long currTime = System.currentTimeMillis();
		
		return nextZeroTime - currTime;
	}
    
	public static void main(String[] args){
		long id = 3727946928049989l;
//		long time = UuidHelper.getTimeFromId(id) * 1000;
//		System.out.println(getDatesAfter(time, 7).toString());
//		long time = parseDateTime("2014-09-23 14:56:59", "yyyy-MM-dd hh24:mm:ss");
//		System.out.println(time - System.currentTimeMillis());
//		System.out.println(new Date(getCurrZeroTime(System.currentTimeMillis())));
//		System.out.println(getDatesAfterAll(time).toString());
		String presentDate = "2014-08-29";
		if(DateUtil.isAfterToday(presentDate, DateUtil.DATE_NORMAL_FORMAT) && DateUtil.isBeforeDate(presentDate, DateUtil.DATE_NORMAL_FORMAT, 3)){
//			System.out.println(presentDate);
		}
//		System.out.println(isBeforeDate(presentDate, "yyyy-MM-dd", 3));
		System.out.println(getCurrHour());
//		System.out.println(CommonUtil.formatDate(getZeroTimeByHour(1)));
		System.out.println(formatTime(System.currentTimeMillis(), hourDateFormat));
//		System.out.println(getCurrHour2());
		
		long time = System.currentTimeMillis();
		System.out.println(time);
		System.out.println(getDaysBetweenTwoTime(time, DateUtil.addHour(DateUtil.getCurrZeroTime()+1000, 24)));
		System.out.println(getCurrPeriodTime());
		System.out.println(getAfternoonTimeByTime(time));
		System.out.println(formatTime(getAfternoonTimeByTime(time), DEFAULT_DATE_FORMAT));
		System.out.println(getPeriodTimeFromTime(time));
		System.out.println(formatTime(time, hhmmssDateFormat));
		System.out.println(parseDateTime("2015-03-04 12:30:00", DEFAULT_DATE_FORMAT));
		System.out.println(parseDateTime("2015-03-05 14:00:00", DEFAULT_DATE_FORMAT));
//		System.out.println(formatTime(1425639000786l, DEFAULT_DATE_FORMAT));
		System.out.println(formatTime(1426341600263l, DEFAULT_DATE_FORMAT));
		long statTime = DateUtil.addDayTime(1426341600263l, -1);
		System.out.println(statTime); //1426255200263 
		System.out.println(formatTime(statTime, DEFAULT_DATE_FORMAT));
		
//		1425725400107
//		System.out.println(Integer.MAX_VALUE);
		
//		Calendar calendar = Calendar.getInstance();
//		System.out.println(calendar.get(Calendar.WEEK_OF_YEAR));
//		System.out.println(calendar.get(Calendar.WEEK_OF_MONTH));
//		System.out.println(formatTime(1426168800000l, DATE_WEEK_FORMAT));
//		System.out.println(formatTime(1426168800000l, DATE_MONTH_FORMAT));
		
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTimeInMillis(statTime);
//		System.out.println(getDayOfWeek(statTime));
//		System.out.println(getDayOfMonth(statTime));
		
	}
}
