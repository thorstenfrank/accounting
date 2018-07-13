package de.tfsw.accounting.ui.databinding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * Intended for strings.
 *
 */
public class NotEmptyValidator implements IValidator {

	private static final Logger LOG = LogManager.getLogger(NotEmptyValidator.class);
	
	private String label;
		
	/**
	 * @param label
	 */
	public NotEmptyValidator(String label) {
		super();
		this.label = label;
	}

	@Override
	public IStatus validate(Object value) {
		if (value instanceof String) {
			final String string = String.class.cast(value);
			if (string == null || string.isEmpty()) {
				LOG.trace("Empty value for [{}]", label);
				return ValidationStatus.error(label + " must not be empty!");
			}
		}
		
		return ValidationStatus.ok();
	}

	
}
