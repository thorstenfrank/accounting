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

/**
 * Definition of {@link Expense} objects to be created based on a template and recurrence rule. 
 * 
 * @author Thorsten Frank
 *
 * @see Expense
 * @see RecurrenceRule
 */
public class RecurringExpense extends AbstractExpense {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Property name of {@link #getRule()}. */
	public static final String FIELD_RULE = "rule";
	/** Property name of {@link #isActive()}. */
	public static final String FIELD_ACTIVE = "active";
	/** Property name of {@link #getNumberOfApplications()}. */
	public static final String FIELD_APPLICATIONS = "numberOfApplications";
	/** Property name of {@link #getFirstApplication()}. */
	public static final String FIELD_FIRST = "firstApplication";
	/** Property name of {@link #getLastApplication()}. */
	public static final String FIELD_LAST = "lastApplication";
	
	// RRULE
	private RecurrenceRule rule;
	private boolean active;
	private int numberOfApplications;
	private LocalDate firstApplication;
	private LocalDate lastApplication;
	
	/**
	 * 
	 */
	public RecurringExpense() {
		this.active = true;
		this.firstApplication = LocalDate.now();
		this.numberOfApplications = 0;
		this.rule = new RecurrenceRule();
	}
	
	/**
	 * 
	 * @return
	 */
	public Expense apply() {
		Expense expense = null;
		
		updateActiveState();
		
		if (active) {
			expense = new Expense();
			expense.setCategory(getCategory());
			expense.setDescription(getDescription());
			expense.setExpenseType(getExpenseType());
			expense.setNetAmount(getNetAmount());
			expense.setTaxRate(getTaxRate());
			checkPostConditions();
		}
		
		return expense;
	}
	
	/**
	 * 
	 */
	public void updateActiveState() {
		active = active & checkPreConditions();
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkPreConditions() {
		if (active) {
			if (rule.getCount() != null) {
				return numberOfApplications <= rule.getCount();
			} else if (rule.getUntil() != null) {
				return rule.getUntil().isAfter(LocalDate.now());
			} else {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 */
	private void checkPostConditions() {
		this.numberOfApplications++;
		this.lastApplication = LocalDate.now();
		if (this.firstApplication == null) {
			this.firstApplication = LocalDate.now();
		}
		
		if (active && rule.getCount() != null && numberOfApplications >= rule.getCount()) {
			active = false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public LocalDate getNextApplication() {
		updateActiveState();
		
		if (rule == null || !active) {
			return null;
		}
		
		LocalDate next = lastApplication != null ? lastApplication : firstApplication;
		
		switch (rule.getFrequency()) {
		case DAILY:
			next = next.plusDays(rule.getInterval());
			break;
		case WEEKLY:
			next = next.plusWeeks(rule.getInterval());
			break;
		case MONTHLY:
			next = next.plusMonths(rule.getInterval());
			break;
		case YEARLY:
			next = next.plusYears(rule.getInterval());
			break;
		default:
			break;
		}
		
		return next;
	}
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		updateActiveState();
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the rule
	 */
	public RecurrenceRule getRule() {
		return rule;
	}

	/**
	 * @param rule the rule to set
	 */
	public void setRule(RecurrenceRule rule) {
		if (rule == null) {
			throw new IllegalArgumentException("RecurrenceRule must not be null");
		}
		this.rule = rule;
	}

	/**
	 * @return the numberOfApplications
	 */
	public int getNumberOfApplications() {
		return numberOfApplications;
	}

	/**
	 * @param numberOfApplications the numberOfApplications to set
	 */
	public void setNumberOfApplications(int numberOfApplications) {
		this.numberOfApplications = numberOfApplications;
	}

	/**
	 * @return the firstApplication
	 */
	public LocalDate getFirstApplication() {
		return firstApplication;
	}

	/**
	 * @param firstApplication the firstApplication to set
	 */
	public void setFirstApplication(LocalDate firstApplication) {
		this.firstApplication = firstApplication;
	}

	/**
	 * @return the lastApplication
	 */
	public LocalDate getLastApplication() {
		return lastApplication;
	}

	/**
	 * @param lastApplication the lastApplication to set
	 */
	public void setLastApplication(LocalDate lastApplication) {
		this.lastApplication = lastApplication;
	}
}
