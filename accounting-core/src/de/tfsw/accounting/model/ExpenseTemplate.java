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
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.Messages;

/**
 * Definition of {@link Expense} objects to be created based on a template and recurrence rule. 
 * 
 * @author Thorsten Frank
 *
 * @see Expense
 * @see RecurrenceRule
 */
public class ExpenseTemplate extends AbstractExpense {

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
	
	/** Placeholder patterns. */
	private static final PlaceholderPattern[] PLACEHOLDERS = {
		new PlaceholderPattern("{month}", "MM"),
		new PlaceholderPattern("{year}", "yy"),
		new PlaceholderPattern("{year_long}", "yyyy"),
	};
	
	// RRULE
	private RecurrenceRule rule;
	private boolean active;
	private int numberOfApplications;
	private LocalDate firstApplication;
	private LocalDate lastApplication;
	
	/**
	 * Calls <code>this(true)</code> 
	 */
	public ExpenseTemplate() {
		this(true);
	}
	
	/**
	 * Calls <code>this(active, LocalDate.now())</code>.
	 */
	public ExpenseTemplate(boolean active) {
		this(active, LocalDate.now());
	}
	
	/**
	 * Calls <code>this(active, firstApplication, new RecurrenceRule())</code>.
	 */
	public ExpenseTemplate(boolean active, LocalDate firstApplication) {
		this(active, firstApplication, new RecurrenceRule());
	}
	
	/**
	 * Creates a new template for creating expenses.
	 * 
	 * @param active whether this template is activated or not
	 * @param firstApplication the date from which this template is valid
	 * @param rule the rules governing when and how often this template is applicable
	 */
	public ExpenseTemplate(boolean active, LocalDate firstApplication, RecurrenceRule rule) {
		setActive(active);
		setFirstApplication(firstApplication);
		setRule(rule);
		setNumberOfApplications(0); // default value
	}
	
	/**
	 * This is a shortcut for {@link #apply(LocalDate)} using {@link #getNextApplication()} as a parameter.
	 * 
	 * <p>
	 * If this template is not active, or invalid according to the rules defined in the {@link RecurrenceRule} of this 
	 * template at the time this method is called, <code>null</code> is returned. In contract to 
	 * {@link #apply(LocalDate)}, this method will not throw an exception if the next application date is in the 
	 * future, it will simply return <code>null</code>.
	 * </p>
	 * 
	 * @return a new {@link Expense} based on this template, or <code>null</code> if this template is inactive or invalid
	 * 
	 * @see #isActive()
	 * @see #getNextApplication()
	 */
	public Expense apply() {
		try {
			return applyInternal(getNextApplication());
		} catch (AccountingException e) {
			return null;
		}
	}
	
	/**
	 * Creates a new {@link Expense} based on this template's base information (e.g. description, net amount, etc.).
	 * 
	 * <p>
	 * This template's {@link #getLastApplication()} will be set to the supplied <code>paymentDate</code>.
	 * </p>
	 * 
	 * @param paymentDate the desired {@link Expense#getPaymentDate()}, must not be <code>null</code> and must not be
	 * 					  future
	 * 
	 * @return an {@link Expense} based on this template or <code>null</code> if the supplied date was <code>null</code>
	 * 
	 * @throws AccountingException if the supplied date is in the future
	 */
	public Expense apply(LocalDate paymentDate) {
		updateActiveState();
		return applyInternal(paymentDate);
	}
	
	/**
	 * 
	 * @param paymentDate
	 * @return
	 */
	private Expense applyInternal(LocalDate paymentDate) {
		if (!active || paymentDate == null) {
			return null;
		} else if (paymentDate.isAfter(LocalDate.now())) {
			throw new AccountingException(Messages.bind(Messages.ExpenseTemplate_errorDateInFuture, paymentDate));
		}
		
		Expense expense = new Expense();
		expense = new Expense();
		expense.setCategory(getCategory());
		expense.setExpenseType(getExpenseType());
		expense.setNetAmount(getNetAmount());
		expense.setPaymentDate(paymentDate);			
		expense.setTaxRate(getTaxRate());
		setDescription(expense);
		
		lastApplication = paymentDate;
		numberOfApplications++;
		updateActiveState();
		
		return expense;		
	}
	
	/**
	 * 
	 * @param expense
	 */
	private void setDescription(Expense expense) {		
		String desc = this.getDescription();
		
		for (PlaceholderPattern pattern : PLACEHOLDERS) {
			if (pattern.isApplicable(desc)) {
				desc = desc.replace(
						pattern.placeholder, 
						expense.getPaymentDate().format(DateTimeFormatter.ofPattern(pattern.pattern)));
			}
		}
		
		expense.setDescription(desc);
	}
	
	/**
	 * @return <code>true</code> if {@link #isActive()} is <code>true</code> <b>and</b> 
	 * 		   if {@link #getNextApplication()} is not in the future.
	 */
	public boolean isApplicable() {
		if (active) {
			return getNextApplication().isAfter(LocalDate.now()) == false;
		}
		
		return false;
	}
	
	/** 
	 * @return the dates of all possible applications of this template
	 */
	public Set<LocalDate> getOutstandingApplications() {
		SortedSet<LocalDate> outstanding = new TreeSet<LocalDate>();
		
		LocalDate reference = getNextApplication();
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		while (reference.isBefore(tomorrow)) {
			outstanding.add(reference);
			reference = getNextApplicationInternal(reference);
		}
		
		return outstanding;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumberOfOutstandingApplications() {
		return getOutstandingApplications().size();
	}
	
	/**
	 * Returns the date this template can next be applied, or <code>null</code> if this template is inactive.
	 * 
	 * <p>
	 * The next application date is calculated in reference to {@link #getLastApplication()} 
	 * (or {@link #getFirstApplication() if the former is <code>null</code>} and the interval and frequency defined by 
	 * this template's {@link RecurrenceRule}. Also taken into consideration are either the maximum number of 
	 * applications or a defined end date of the rule, if either one of these has been set.
	 * </p>
	 * 
	 * @return the next possible application date of this template or <code>null</code> if it is inactive
	 * 
	 * @see RecurrenceRule
	 */
	public LocalDate getNextApplication() {
		updateActiveState();
		return getNextApplicationInternal(lastApplication);
	}
	
	/**
	 * 
	 * @return
	 */
	private LocalDate getNextApplicationInternal(LocalDate lastApplicationDate) {
		if (rule == null || !active) {
			return null;
		}
		
		// if the first application is set to the future or today, we don't need to do anything else...
		LocalDate today = LocalDate.now();
		
		if (firstApplication.isAfter(today) || firstApplication.equals(today)) {
			return firstApplication;
		} else if (lastApplicationDate == null) {
			// if this template never ran and the first application date is in the past (see above),
			// then the next application is equal to the first
			return firstApplication;
		} else {
			// the calculation base is EITHER
			// - the last application
			// - if this rule has never been applied before, the configured first application
			LocalDate next = lastApplicationDate;
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
			
			if (rule.getUntil() != null && rule.getUntil().isBefore(next)) {
				return null;
			}
			
			return next;
		}
	}
	
	/**
	 * Resets this template's number of applications and the last application date, and activates this template if it
	 * was previously de-activated. This template will then be in the same pristine state as when it was originally 
	 * created, and {@link #getNextApplication()} will be equal to {@link #getFirstApplication()}.
	 * 
	 * @see #getNumberOfApplications()
	 * @see #getLastApplication()
	 */
	public void reset() {
		setLastApplication(null);
		setNumberOfApplications(0);
		if (!active) {
			setActive(true);
		}
		updateActiveState();
	}
	
	/**
	 * 
	 */
	private void updateActiveState() {
		if (active) {
			if (rule.getCount() != null) {
				active = numberOfApplications < rule.getCount();
			} else if (rule.getUntil() != null)  {
				active = rule.getUntil().isAfter(lastApplication != null ? lastApplication : LocalDate.now());
			}
		}
	}
	
	/**
	 * A template is only considered for creating actual {@link Expense} instances if it is active. Deactivating a
	 * template can be done manually by calling <code>setActive(false)</code>. Consider this the master off switch.
	 * 
	 * <p>
	 * If a template is active, it's active state is checked every time a call to {@link #apply()}, 
	 * {@link #getNextApplication()} is made, and depends on the configuration of the {@link RecurrenceRule} associated
	 * with this template:
	 * 
	 * <ul>
	 * <li>if {@link RecurrenceRule#getCount()} is not <code>null</code>, that integer must be larger than, or equal to
	 * this template's {@link #getNumberOfApplications()}.</li>
	 * <li>if {@link RecurrenceRule#getUntil()} is not <code>null</code>, then that date must be after this template's
	 * {@link #getLastApplication()}</li>
	 * </p>
	 * 
	 * @return <code>true</code> if this template is active
	 * 
	 * @see #setActive(boolean)
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Activates or de-activates this template. 
	 * 
	 * <p>
	 * Simply calling <code>setActive(true)</code> on an inactive template may have no effect, as the active state is 
	 * dynamically computed before and after every application, based on whether the {@link RecurrenceRule} of this 
	 * template is limited either by the number of applications or an end date. In other words, to revive an inactive 
	 * template, make sure to change either of these limiting factors as well.
	 *  
	 * @param active <code>true</code> if this template should be activated
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
	 * Setting this date in the past may have the effect of this template being able to be applied multiple times,
	 * depending on the {@link RecurrenceRule} configuration. For example, if the first application date is set to 5
	 * days in the past, and the rule is set to daily, this template can produce 5 actual expense instances.
	 * 
	 * <p>
	 * Setting this date to a value in the future will cause no expenses to be applied until the actual date reaches
	 * the configured one.
	 * </p>
	 * 
	 * @param firstApplication the date from which this template should be valid. May be in the past, present or the
	 * 	      future, but never <code>null</code>
	 * 
	 * @throws IllegalArgumentException if the supplied date is <code>null</code>
	 */
	public void setFirstApplication(LocalDate firstApplication) {
		if (firstApplication == null) {
			throw new IllegalArgumentException("FirstApplication must not be null!");
		}
		this.firstApplication = firstApplication;
	}

	/**
	 * @return the date when this template was applied last, i.e. it's {@link #apply()} method was called.
	 * 		   May be <code>null</code> if this template was never applied.
	 */
	public LocalDate getLastApplication() {
		return lastApplication;
	}

	/**
	 * This method should not be called by clients!
	 * 
	 * @param lastApplication the lastApplication to set
	 */
	public void setLastApplication(LocalDate lastApplication) {
		this.lastApplication = lastApplication;
	}
	
	/**
	 * 
	 * @author Thorsten Frank
	 *
	 */
	private static class PlaceholderPattern {
		private String placeholder;
		private String pattern;
		
		private PlaceholderPattern(String placeholder, String pattern) {
			this.placeholder = placeholder;
			this.pattern = pattern;
		}
		
		private boolean isApplicable(String text) {
			return text.contains(placeholder);
		}
	}
}
