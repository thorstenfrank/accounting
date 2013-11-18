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

import de.togginho.accounting.Constants;

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
	private Calendar from;
	
	/**
	 * End (inclusive).
	 */
	private Calendar until;
    
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
    	this.from = Calendar.getInstance();
    	this.until = Calendar.getInstance();
    	setFrom(from);
    	setUntil(until);
    }

	/**
     * @return the start (inclusive)
     */
    public Date getFrom() {
    	return from.getTime();
    }

	/**
     * @param date the start (inclusive)
     */
    public void setFrom(Date date) {
    	from.setTime(date);
    	setToStartOfDay(from);
    	type = TimeFrameType.CUSTOM;
    }

	/**
     * @return the end (inclusive)
     */
    public Date getUntil() {
    	return until.getTime();
    }

	/**
     * @param date the end (inclusive)
     */
    public void setUntil(Date date) {
    	until.setTime(date);
    	setToEndOfDay(until);
    	type = TimeFrameType.CUSTOM;
    }
    
    /**
	 * @return the type
	 */
	public TimeFrameType getType() {
		return type;
	}

	/**
	 * 
	 * @param month
	 */
	public void setMonth(int month, boolean adjustDays) {
		if (month >= Calendar.JANUARY && month <= Calendar.DECEMBER) {
			from.set(Calendar.MONTH, month);
			until.set(Calendar.MONTH, month);
			
			if (adjustDays) {
				from.getTimeInMillis(); // force the calendar to update itself
				from.set(Calendar.DAY_OF_MONTH, from.getMinimum(Calendar.DAY_OF_MONTH));
				until.set(Calendar.DAY_OF_MONTH, from.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
		}
	}
	
	/**
	 * 
	 * @param year
	 */
	public void setYear(int year, boolean adjustDaysAndMonths) {
		from.set(Calendar.YEAR, year);
		until.set(Calendar.YEAR, year);
		
		if (adjustDaysAndMonths) {
			from.set(Calendar.DAY_OF_MONTH, 1);
			from.set(Calendar.MONTH, Calendar.JANUARY);
			until.set(Calendar.DAY_OF_MONTH, 31);
			until.set(Calendar.MONTH, Calendar.DECEMBER);
		}
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
    	return (from.getTimeInMillis() <= date.getTime() && until.getTimeInMillis() >= date.getTime());
    }
    
    /**
     * 
     * @return
     */
    public boolean isWithinOneYear() {
    	return from.get(Calendar.YEAR) == until.get(Calendar.YEAR);
    }
    
    /**
     * 
     * {@inheritDoc}.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder(getClass().getName());
    	sb.append(Constants.BLANK_STRING);
    	sb.append(FormatUtil.formatDate(from.getTime()));
    	sb.append(Constants.BLANK_STRING).append(Constants.HYPHEN).append(Constants.BLANK_STRING);
    	sb.append(FormatUtil.formatDate(until.getTime()));
        return sb.toString();
    }
    
	/**
	 * This month, starting on the first day at 00:00:00:000, ending on the last day at 23:59:59:999.
	 * @return
	 */
	public static TimeFrame currentMonth() {
		TimeFrame thisMonth = new TimeFrame(TimeFrameType.CURRENT_MONTH);
		
		thisMonth.from = Calendar.getInstance();
		thisMonth.setToStartOfDay(thisMonth.from);
		thisMonth.from.set(Calendar.DAY_OF_MONTH, 1);
				
		thisMonth.until = Calendar.getInstance();
		thisMonth.setToEndOfDay(thisMonth.until);
		thisMonth.until.set(Calendar.DAY_OF_MONTH, thisMonth.until.getActualMaximum(Calendar.DAY_OF_MONTH));
				
		return thisMonth;		
	}
    
    /**
     * Last month, starting on the first day at 00:00:00:000, ending on the last day at 23:59:59:999.
     * 
     * @return a {@link TimeFrame} starting at the first day of last month, ending on the last day of it
     */
	public static TimeFrame lastMonth() {
		TimeFrame lastMonth = new TimeFrame(TimeFrameType.LAST_MONTH);
		
		lastMonth.from = Calendar.getInstance();
		lastMonth.from.set(Calendar.DAY_OF_MONTH, 1);
		lastMonth.from.add(Calendar.MONTH, -1);
		lastMonth.setToStartOfDay(lastMonth.from);
		
		lastMonth.until = Calendar.getInstance();
		lastMonth.until.setTime(lastMonth.from.getTime());
		lastMonth.until.set(Calendar.DAY_OF_MONTH, lastMonth.until.getActualMaximum(Calendar.DAY_OF_MONTH));
		lastMonth.setToEndOfDay(lastMonth.until);
		
		return lastMonth;
	}
	

	
	/**
	 * This year, starting on 1 January at 00:00:00:000, ending on 31 December at 23:59:59:999.
	 * 
	 * @return the {@link TimeFrame} between 1 January and 31 December of this year
	 */
	public static TimeFrame currentYear() {
		TimeFrame thisYear = new TimeFrame(TimeFrameType.CURRENT_YEAR);
		
		thisYear.from = Calendar.getInstance();
		thisYear.from.set(Calendar.MONTH, Calendar.JANUARY);
		thisYear.from.set(Calendar.DAY_OF_MONTH, 1);
		thisYear.setToStartOfDay(thisYear.from);
		
		thisYear.until = Calendar.getInstance();
		thisYear.until.set(Calendar.MONTH, Calendar.DECEMBER);
		thisYear.until.set(Calendar.DAY_OF_MONTH, 31);
		thisYear.setToEndOfDay(thisYear.until);
				
		return thisYear;
	}
	
	/**
	 * Last year, starting on 1 January at 00:00:00:000, ending on 31 December at 23:59:59:999.
	 * 
	 * @return the {@link TimeFrame} between 1 January and 31 December of last year
	 */
	public static TimeFrame lastYear() {
		TimeFrame lastYear = new TimeFrame(TimeFrameType.LAST_YEAR);
		
		lastYear.from = Calendar.getInstance();
		lastYear.from.set(Calendar.MONTH, Calendar.JANUARY);
		lastYear.from.set(Calendar.DAY_OF_MONTH, 1);
		lastYear.from.add(Calendar.YEAR, -1);
		lastYear.setToStartOfDay(lastYear.from);
		
		lastYear.until = Calendar.getInstance();
		lastYear.until.setTime(lastYear.from.getTime());
		lastYear.until.set(Calendar.MONTH, Calendar.DECEMBER);
		lastYear.until.set(Calendar.DAY_OF_MONTH, 31);
		lastYear.setToEndOfDay(lastYear.until);
		
		return lastYear;		
	}
	
	/**
	 * Nullifies hours, minutes, seconds and millisecons.
	 * 
	 * @param cal the {@link Calendar} to nullify
	 */
	private void setToStartOfDay(Calendar cal) {
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
	private void setToEndOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);		
	}
}