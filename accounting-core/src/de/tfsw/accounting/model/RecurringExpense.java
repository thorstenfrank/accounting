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

	// RRULE
	private RecurrenceRule rule;

	private int numberOfApplications;
	private LocalDate firstApplication;
	private LocalDate lastApplication;
	
	// Expense template data
	
	/**
	 * 
	 */
	public RecurringExpense() {
		this.firstApplication = LocalDate.now();
		this.rule = new RecurrenceRule();
	}
	
	public Expense apply() {
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public LocalDate getNextApplication() {
		if (rule == null) {
			return null;
		}
		
		if (lastApplication == null) {
			return LocalDate.now();
		}
		
		LocalDate next = lastApplication;
		
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
