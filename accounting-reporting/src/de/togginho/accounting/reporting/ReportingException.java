/*
 *  Copyright 2010 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.reporting;

import de.togginho.commons.exception.ErrorCode;
import de.togginho.commons.exception.TogginhoException;
import de.togginho.commons.resources.MessageResources;

/**
 * Exception thrown by report generators.
 * 
 * <p>This exception uses the numberical range <b><code>2000 - 2999</code></b> for its {@link ErrorCode}s.</p>
 * 
 * @author thorsten frank
 * 
 */
public class ReportingException extends TogginhoException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8463824515911533041L;

	/**
	 * General error code when report generation fails.
	 * 
	 * @see AbstractReportGenerator#generateReportToFile(String)
	 */
	static final ErrorCode REPORT_GENERATION_FAILED = new ErrorCode(2000, "report.generation.failed");
	
	/**
	 * Thrown when the file name supplied to {@link AbstractReportGenerator#generateReportToFile(String)} is
	 * <code>null</code>.
	 */
	static final ErrorCode REPORT_FILE_NAME_NULL = new ErrorCode(2001, "report.file.name.null");
	
	/**
	 * Thrown when the template path supplied by {@link AbstractReportGenerator#getReportTemplatePath()} cannot be
	 * properly looked up through {@link ClassLoader#getResourceAsStream(String)}.
	 * 
	 * @see AbstractReportGenerator#generateReportToFile(String)
	 */
	static final ErrorCode TEMPLATE_NOT_FOUND = new ErrorCode(2002, "template.not.found");
	
	/**
	 * 
	 */
	private static final MessageResources RESOURCES = new MessageResources(ReportingException.class.getName());
	
	/**
	 * @param errorCode
	 */
	public ReportingException(ErrorCode errorCode) {
		super(errorCode);
	}

	/**
	 * @param errorCode
	 * @param errorMessageArguments
	 */
	public ReportingException(ErrorCode errorCode, Object... errorMessageArguments) {
		super(errorCode, errorMessageArguments);
	}

	/**
	 * @param errorCode
	 * @param cause
	 */
	public ReportingException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}

	/**
	 * @param errorCode
	 * @param cause
	 * @param errorMessageArguments
	 */
	public ReportingException(ErrorCode errorCode, Throwable cause, Object... errorMessageArguments) {
		super(errorCode, cause, errorMessageArguments);
	}

	/** 
	 * @see
	 * de.togginho.commons.exception.TogginhoException#getMessageResources()
	 */
	@Override
	public MessageResources getMessageResources() {
		return RESOURCES;
	}

}
