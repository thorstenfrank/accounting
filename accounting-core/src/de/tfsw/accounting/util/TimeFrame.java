/*
 *  Copyright 2012 , 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.util;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

import org.apache.log4j.Logger;

import de.tfsw.accounting.Constants;

/**
 * A period of time. Unless otherwise mentioned, all dates/ points in time in this class are <b>inclusive</b>.
 * 
 * @author Thorsten Frank
 */
public class TimeFrame {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(TimeFrame.class);
	
	/**
	 * Start (inclusive).
	 */
	private LocalDate from;
	
	/**
	 * End (inclusive).
	 */
	private LocalDate until;
    
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
    public TimeFrame(LocalDate from, LocalDate until) {
    	setFrom(from);
    	setUntil(until);
    }

	/**
     * @return the start (inclusive)
     */
    public LocalDate getFrom() {
    	return from;
    }

    /**
     * 
     * @return
     */
    public int getFromMonth() {
    	return from.getMonthValue();
    }
    
    /**
     * 
     * @return
     */
    public String getFromMonthLocalized() {
    	return from.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }
    
    /**
     * 
     * @return
     */
    public int getFromYear() {
    	return from.getYear();
    }
    
	/**
     * @param date the start (inclusive)
     */
    public void setFrom(LocalDate date) {
    	from = date;
    	type = TimeFrameType.CUSTOM;
    }

	/**
     * @return the end (inclusive)
     */
    public LocalDate getUntil() {
    	return until;
    }
    
    /**
     * 
     * @return
     */
    public int getUntilMonth() {
    	return until.getMonthValue();
    }

    /**
     * 
     * @return
     */
    public String getUntilMonthLocalized() {
    	return until.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
    }
    
    /**
     * 
     * @return
     */
    public int getUntilYear() {
    	return until.getYear();
    }
    
	/**
     * @param date the end (inclusive)
     */
    public void setUntil(LocalDate date) {
    	until = date;
    	type = TimeFrameType.CUSTOM;
    }
    
    /**
	 * @return the type
	 */
	public TimeFrameType getType() {
		return type;
	}

	/**
	 * Short for <code>setMonth(month.getvalue())</code>.
	 * 
	 * @param month the month to set
	 * 
	 * @see #setMonth(int)
	 */
	public void setMonth(Month month) {
		setMonth(month.getValue());
	}
	
	/**
	 * Sets the month of both from and until dates to the specified one. The other parts of the two dates are adjusted
	 * to reflect being the first and last day of the new month respectively. The year value of the two dates are left
	 * untouched. 
	 * <p>The type of this time frame is then changed to {@link TimeFrameType#SINGLE_MONTH}, unless the previous type 
	 * was {@link TimeFrameType#CUSTOM}, in which case it remains that way.</p>
	 * 
	 * @param month the month to set this time frame to: between 1 ({@link Month#JANUARY}) and 
	 * 				12 ({@link Month#DECEMBER}). Values outside of that range are ignored
	 * 
	 * @see TimeFrameType
	 */
	public void setMonth(int month) {
		
		try {
			Month.of(month);
		} catch (Exception e) {
			LOG.error("Attempt to set month to an illegal value: " + month, e); //$NON-NLS-1$
			return; // TODO maybe throw an exception here...
		}
		
		from = from.withMonth(month).with(TemporalAdjusters.firstDayOfMonth());
		until = until.withMonth(month).with(TemporalAdjusters.lastDayOfMonth());
		if (type != TimeFrameType.CUSTOM) {
			type = TimeFrameType.SINGLE_MONTH;
		}
	}
	
	/**
	 * Sets the year value of both <code>from</code> and <code>until</code> of this time frame to the specified year.
	 * <p>
	 * If an adjustment of days and months is requested, <code>from</code> will be adjusted to represent January 1, and
	 * <code>until</code> December 31, and {@link #getType()} is set to {@link TimeFrameType#WHOLE_YEAR}.
	 * </p>
	 * 
	 * @param year the year value to set this time frame to
	 * @param adjustDaysAndMonths if <code>true</code>, this time frame is adjusted to represent an the entire year
	 * 		  specified by <code>year</code>. If <code>false</code>, the day and months values of both <code>from</code>
	 * 		  and <code>until</code> remain untouched
	 */
	public void setYear(int year, boolean adjustDaysAndMonths) {		
		from = from.withYear(year);
		until = until.withYear(year);
		
		switch (type) {
		case CURRENT_YEAR:
		case LAST_YEAR:
		case WHOLE_YEAR:
			type = TimeFrameType.WHOLE_YEAR;
			break;
		case LAST_MONTH:
		case CURRENT_MONTH:
		case SINGLE_MONTH:
			type = TimeFrameType.SINGLE_MONTH;
			break;
		default:
			type = TimeFrameType.CUSTOM;
		}
		
		if (adjustDaysAndMonths) {
			from = from.with(TemporalAdjusters.firstDayOfYear());
			until = until.with(TemporalAdjusters.lastDayOfYear());
			this.type = TimeFrameType.WHOLE_YEAR;
		}
	}
		
	/**
     * Returns whether the supplied date is within this timeframe.
     * 
     * @param date the {@link LocalDate} to check
     * @return <code>true</code> if the supplied date is within this timeframe, <code>false</code> otherwise
     */
    public boolean isInTimeFrame(LocalDate date) {
    	if (date == null) {
    		return false;
    	}
    	
    	return (from.compareTo(date) < 1 && until.compareTo(date) > -1);
    }
    
    /**
     * 
     * @return
     */
    public boolean isInSameYear() {
    	return from.getYear() == until.getYear();
    }
    
    /**
     * 
     * @return
     */
    public boolean isInSameMonth() {
    	return isInSameYear() && (from.getMonth() == until.getMonth());
    }
    
    /**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((until == null) ? 0 : until.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TimeFrame)) {
			return false;
		}
		TimeFrame other = (TimeFrame) obj;
		if (from == null) {
			if (other.from != null) {
				return false;
			}
		} else if (!from.equals(other.from)) {
			return false;
		}
		if (until == null) {
			if (other.until != null) {
				return false;
			}
		} else if (!until.equals(other.until)) {
			return false;
		}
		return true;
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
    	sb.append(from.toString());
    	sb.append(Constants.BLANK_STRING).append(Constants.HYPHEN).append(Constants.BLANK_STRING);
    	sb.append(until.toString());
        return sb.toString();
    }
    
    /**
     * 
     * @return
     */
    public String toLocalizedString() {
    	StringBuilder sb = new StringBuilder();
    	
    	switch (type) {
		case CUSTOM:
			sb.append(FormatUtil.formatDate(getFrom()));
			sb.append(Constants.HYPHEN);
			sb.append(FormatUtil.formatDate(getUntil()));
			break;
		case SINGLE_MONTH:
			sb.append(from.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
			sb.append(Constants.BLANK_STRING);
		case WHOLE_YEAR:
			sb.append(from.getYear());
			break;
		default:
			sb.append(type.getTranslatedName());
			break;
		}
    	
    	return sb.toString();
    }
    
    /**
     * 
     * @param type
     */
    public void adjustInto(TimeFrameType type) {
    	switch (type) {
		case CURRENT_MONTH:
			adjustIntoCurrentMonth();
			break;
		case LAST_MONTH:
			adjustIntoLastMonth();
			break;
		case CURRENT_YEAR:
			adjustIntoCurrentYear();
			break;
		case LAST_YEAR:
			adjustIntoLastYear();
			break;
		default:
			LOG.warn("Cannot adjustInto(TimeFrameType) with type " + type.name());
			break;
		}
    }
    
    /**
     * 
     */
    public void adjustIntoCurrentMonth() {
    	type = TimeFrameType.CURRENT_MONTH;
		from = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());				
		until = from.with(TemporalAdjusters.lastDayOfMonth());
    }
    
	/**
	 * The current month, starting on the first and ending on the last day, such as Jan 1 - Jan 31, Feb 1 - Feb 28.
	 * 
	 * @return the current month as a time frame
	 * 
	 * @see TimeFrameType#CURRENT_MONTH
	 */
	public static TimeFrame currentMonth() {
		TimeFrame thisMonth = new TimeFrame(TimeFrameType.CURRENT_MONTH);
		thisMonth.adjustIntoCurrentMonth();
		return thisMonth;		
	}
    
	/**
	 * 
	 */
	public void adjustIntoLastMonth() {
		type = TimeFrameType.LAST_MONTH;
		from = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
		until = from.with(TemporalAdjusters.lastDayOfMonth());
	}
	
    /**
     * The month before the current one.
     * 
     * @return a {@link TimeFrame} starting at the first day of last month, ending on the last day of it
     * 
     * @see TimeFrameType#LAST_MONTH
     */
	public static TimeFrame lastMonth() {
		TimeFrame lastMonth = new TimeFrame(TimeFrameType.LAST_MONTH);
		lastMonth.adjustIntoLastMonth();
		return lastMonth;
	}
	
	/**
	 * 
	 */
	public void adjustIntoCurrentYear() {
		type = TimeFrameType.CURRENT_YEAR;
		from = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
		until = from.with(TemporalAdjusters.lastDayOfYear());
	}
	
	/**
	 * The current year from January 1 until December 31.
	 * 
	 * @return the {@link TimeFrame} representing the entire current year
	 * 
	 * @see TimeFrameType#CURRENT_YEAR
	 */
	public static TimeFrame currentYear() {
		TimeFrame thisYear = new TimeFrame(TimeFrameType.CURRENT_YEAR);
		thisYear.adjustIntoCurrentYear();
		return thisYear;
	}
	
	/**
	 * 
	 */
	public void adjustIntoLastYear() {
		type = TimeFrameType.LAST_YEAR;
		from = LocalDate.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear());
		until = from.with(TemporalAdjusters.lastDayOfYear());
	}
	
	/**
	 * Last year, from January 1 until December 31.
	 * 
	 * @return the {@link TimeFrame} representing the entire last year
	 * 
	 * @see TimeFrameType#LAST_YEAR
	 */
	public static TimeFrame lastYear() {
		TimeFrame lastYear = new TimeFrame(TimeFrameType.LAST_YEAR);
		lastYear.adjustIntoLastYear();
		return lastYear;		
	}
	
	/**
	 * Convenience method to obtain a time frame for an entire month. If the supplied month is equal to the current one,
	 * {@link #currentMonth()} is returned, if it represents last month then {@link #lastMonth()}. Otherwise, a new
	 * time frame with type {@link TimeFrameType#SINGLE_MONTH} is created, and the <code>from</code> and 
	 * <code>until</code> values are set to represent the first and last day of the month, respectively.
	 *  
	 * @param yearMonth the month the new time frame should represent
	 * 
	 * @return a new {@link TimeFrame} representing the requested {@link YearMonth}
	 */
	public static TimeFrame of(YearMonth yearMonth) {
		YearMonth now = YearMonth.now();
		if (yearMonth.equals(now)) {
			return currentMonth();
		}
		if (yearMonth.getYear() == now.getYear() && yearMonth.getMonthValue() == now.getMonthValue() - 1) {
			return lastMonth();
		}
		
		TimeFrame tf = new TimeFrame(TimeFrameType.SINGLE_MONTH);
		// we can safely assume that every month starts on day 1
		tf.from = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
		tf.until = tf.from.with(TemporalAdjusters.lastDayOfMonth());
		
		return tf;
	}
	
	/**
	 * Returns a time frame with type {@link TimeFrameType#WHOLE_YEAR} ranging from January 1 to December 31 of the
	 * requested year.
	 * 
	 * @param year the year to build a time frame for
	 * 
	 * @return a new {@link TimeFrame} representing the requested year
	 */
	public static TimeFrame ofYear(int year) {
		TimeFrame tf = new TimeFrame(TimeFrameType.WHOLE_YEAR);
		tf.from = LocalDate.of(year, Month.JANUARY.getValue(), 1);
		tf.until = tf.from.with(TemporalAdjusters.lastDayOfYear());
		return tf;
	}
}