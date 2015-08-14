/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.model;

import java.time.LocalDate;

import org.apache.log4j.Logger;

/**
 * Partial RFC 2445 RRULE implementation that supports date-based values, i.e. no time-based ones. 
 * 
 * @author Thorsten Frank
 *
 */
public class RecurrenceRule extends AbstractBaseEntity {

	/**	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(RecurrenceRule.class);
	
	/** Property name for {@link #getUntil()}. */
	public static final String FIELD_UNTIL = "until";
	/** Property name for {@link #getCount()}. */
	public static final String FIELD_COUNT = "count";
	/** Property name for {@link #getFrequency()}. */
	public static final String FIELD_FREQUENCY = "frequency";
	/** Property name for {@link #getInterval()}. */
	public static final String FIELD_INTERVAL = "interval";
	
	/**
	 * Frequency of application of a rule.
	 */
	public enum Frequency {
		DAILY, WEEKLY, MONTHLY, YEARLY;
	}
	
	private LocalDate until;
	private Integer count;
	private Frequency frequency;
	private int interval = 1;
	
	/**
	 * Equivalent to calling {@link #RecurrenceRule(Frequency.MONTHLY)}
	 */
	public RecurrenceRule() {
		this(Frequency.MONTHLY);
	}
	
	/**
	 * @param frequency the {@link Frequency} with which this rule applies
	 */
	public RecurrenceRule(Frequency frequency) {
		this.frequency = frequency;
	}
	
	/**
	 * @return the until
	 */
	public LocalDate getUntil() {
		return until;
	}

	/**
	 * @param until the until to set
	 */
	public void setUntil(LocalDate until) {
		if (count != null) {
			LOG.warn(String.format("Count was previously set to [%d], will remove!", count));
			count = null;
		}
		this.until = until;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count the count to set, may be <code>null</code>, in which case the rule will apply without end
	 */
	public void setCount(Integer count) {
		if (count != null) {
			if (count < 1) {
				throw new IllegalArgumentException("Count cannot be 0 or a negative integer");
			} else if (until != null) {
				LOG.warn(String.format("Until was previously set to [%TD], will remove", until));
				until = null;
			}
		}
		
		this.count = count;
	}

	/**
	 * @return the frequency
	 */
	public Frequency getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the interval
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(int interval) {
		if (interval < 1) {
			throw new IllegalArgumentException("Interval must be a positive integer");
		}
		this.interval = interval;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Every ");
		
		if (interval > 1) {
			sb.append(interval).append(" ");
		}
		
		switch (frequency) {
		case DAILY:
			sb.append("day");
			break;
		case WEEKLY:
			sb.append("week");
			break;
		case MONTHLY:
			sb.append("month");
			break;
		case YEARLY:
			sb.append("year");
			break;
		}
		
		if (interval > 1) {
			sb.append("s ");
		} else {
			sb.append(" ");
		}
		
		if (count != null) {
			if (count == 1) {
				sb.append("once");
			} else {
				sb.append(count).append(" times.");
			}
		} else if (until != null) {
			sb.append("until ").append(until.toString());
		}
		
		return sb.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((count == null) ? 0 : count.hashCode());
		result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
		result = prime * result + interval;
		result = prime * result + ((until == null) ? 0 : until.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecurrenceRule other = (RecurrenceRule) obj;
		if (count == null) {
			if (other.count != null)
				return false;
		} else if (!count.equals(other.count))
			return false;
		if (frequency != other.frequency)
			return false;
		if (interval != other.interval)
			return false;
		if (until == null) {
			if (other.until != null)
				return false;
		} else if (!until.equals(other.until))
			return false;
		return true;
	}
}
