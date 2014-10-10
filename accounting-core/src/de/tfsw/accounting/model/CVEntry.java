/*
 *  Copyright 2014 Thorsten Frank (accounting@tfsw.de).
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
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public class CVEntry extends AbstractBaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String FIELD_FROM = "from"; //$NON-NLS-1$
	public static final String FIELD_UNTIL = "until"; //$NON-NLS-1$
	public static final String FIELD_CUSTOMER = "customer"; //$NON-NLS-1$
	public static final String FIELD_TITLE = "title"; //$NON-NLS-1$
	public static final String FIELD_TASKS = "tasks"; //$NON-NLS-1$
	public static final String FIELD_DESCRIPTION = "description"; //$NON-NLS-1$
	
	private LocalDate from;
	private LocalDate until;
	private String customer;
	private String title;
	private String tasks;
	private String description;
	
	/**
	 * @return the from
	 */
	public LocalDate getFrom() {
		return from;
	}
	/**
	 * @param from the from to set
	 */
	public void setFrom(LocalDate from) {
		this.from = from;
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
		this.until = until;
	}
	/**
	 * @return the customer
	 */
	public String getCustomer() {
		return customer;
	}
	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the tasks
	 */
	public String getTasks() {
		return tasks;
	}
	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(String tasks) {
		this.tasks = tasks;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
