/*
 *  Copyright 2012 thorsten frank (thorsten.frank@gmx.de).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.togginho.accounting.util;

import java.util.Calendar;
import java.util.Date;

/**
 * A period of time. Unless otherwise mentioned, all dates/ points in time in this class are <b>inclusive</b>.
 * 
 * @author thorsten
 *
 */
public class TimeFrame {
	
	/**
	 * Start (inclusive).
	 */
	private Date from;
	
	/**
	 * End (inclusive).
	 */
	private Date until;
    
	/**
	 * 
	 */
	private TimeFrameType type;
	
	/**
	 * This constructor is private, since it's only needed the static convenience methods.
	 */
	private TimeFrame(TimeFrameType type) {
		this.type = type;
	}
	
	/**
	 * Creates a new time frame.
	 * 
     * @param from Start (inclusive)
     * @param until End (inclusive)
     */
    public TimeFrame(Date from, Date until) {
    	type = TimeFrameType.CUSTOM;
	    this.from = from;
	    this.until = until;
    }

	/**
     * @return the start (inclusive)
     */
    public Date getFrom() {
    	return from;
    }

	/**
     * @param from the start (inclusive)
     */
    public void setFrom(Date from) {
    	this.from = from;
    }

	/**
     * @return the end (inclusive)
     */
    public Date getUntil() {
    	return until;
    }

	/**
     * @param until the end (inclusive)
     */
    public void setUntil(Date until) {
    	this.until = until;
    }
    
    /**
	 * @return the type
	 */
	public TimeFrameType getType() {
		return type;
	}

	/**
     * Returns whether the supplied date is within this timeframe.
     * 
     * @param date the {@link Date} to check
     * @return <code>true</code> if the supplied date is within this timeframe, <code>false</code> otherwise
     */
    public boolean isInTimeFrame(Date date) {
    	if (date == null) {
    		return false;
    	}
    	return (from.compareTo(date) <= 0 && until.compareTo(date) >= 0);
    }
        
	/**
	 * This month, starting on the first day at 00:00:00:000, ending on the last day at 23:59:59:999.
	 * @return
	 */
	public static TimeFrame currentMonth() {
		TimeFrame thisMonth = new TimeFrame(TimeFrameType.CURRENT_MONTH);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		setToStartOfDay(cal);
		thisMonth.from = cal.getTime();
		
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setToEndOfDay(cal);
		thisMonth.until = cal.getTime();
		
		return thisMonth;		
	}
    
    /**
     * Last month, starting on the first day at 00:00:00:000, ending on the last day at 23:59:59:999.
     * 
     * @return a {@link TimeFrame} starting at the first day of last month, ending on the last day of it
     */
	public static TimeFrame lastMonth() {
		TimeFrame lastMonth = new TimeFrame(TimeFrameType.LAST_MONTH);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, -1);
		setToStartOfDay(cal);
		lastMonth.from = cal.getTime();
		
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setToEndOfDay(cal);
		lastMonth.until = cal.getTime();
		
		return lastMonth;
	}
	

	
	/**
	 * This year, starting on 1 January at 00:00:00:000, ending on 31 December at 23:59:59:999.
	 * 
	 * @return the {@link TimeFrame} between 1 January and 31 December of this year
	 */
	public static TimeFrame currentYear() {
		TimeFrame thisYear = new TimeFrame(TimeFrameType.CURRENT_YEAR);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		setToStartOfDay(cal);
		thisYear.from = cal.getTime();
		
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setToEndOfDay(cal);
		thisYear.until = cal.getTime();
		
		return thisYear;
	}
	
	/**
	 * Last year, starting on 1 January at 00:00:00:000, ending on 31 December at 23:59:59:999.
	 * 
	 * @return the {@link TimeFrame} between 1 January and 31 December of last year
	 */
	public static TimeFrame lastYear() {
		TimeFrame lastYear = new TimeFrame(TimeFrameType.LAST_YEAR);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.YEAR, -1);
		setToStartOfDay(cal);
		lastYear.from = cal.getTime();
		
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setToEndOfDay(cal);
		lastYear.until = cal.getTime();
		
		return lastYear;		
	}
	
	/**
	 * Nullifies hours, minutes, seconds and millisecons.
	 * 
	 * @param cal the {@link Calendar} to nullify
	 */
	private static void setToStartOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}
	
	/**
	 * Sets hours, minutes, seconds and milliseconds to their respective maximum values.
	 * 
	 * <p><ul>
	 * <li>Hours: 23</li>
	 * <li>Minutes: 59</li>
	 * <li>Seconds: 59</li>
	 * <li>Milliseconds: 999</li>
	 * </ul></p>
	 * 
	 * @param cal the {@link Calendar} to manipulate
	 */
	private static void setToEndOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);		
	}
}