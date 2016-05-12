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
package de.tfsw.accounting.elster.internal;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.Constants;
import de.tfsw.accounting.elster.Bundesland;
import de.tfsw.accounting.elster.ElsterDTO;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.IncomeStatement;
import de.tfsw.accounting.model.Price;
import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.model.User;
import de.tfsw.accounting.util.CalculationUtil;
import de.tfsw.accounting.util.TimeFrame;

/**
 * Utility class to create or update {@link ElsterDTO} objects.
 * 
 * @author Thorsten Frank
 *
 */
class ElsterDTOBuilder {
	
	/**
	 * 
	 */
	private static final Logger LOG = LogManager.getLogger(ElsterDTOBuilder.class);
	
	/**
	 * Standard 19% german VAT rate.
	 */
	private static final BigDecimal VAT_19 = new BigDecimal("0.19"); //$NON-NLS-1$
	
	/**
	 * Standard 7% german VAT rate.
	 */
	private static final BigDecimal VAT_7 = new BigDecimal("0.07"); //$NON-NLS-1$
	
	/**
	 * 
	 */
	private static final DateTimeFormatter BASIC_ISO_DATE_FORMATTER;
	static {
		BASIC_ISO_DATE_FORMATTER = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendValue(YEAR, 4)
        .appendValue(MONTH_OF_YEAR, 2)
        .appendValue(DAY_OF_MONTH, 2)
        .toFormatter();
	}
	
	/**
	 * Key for preferences. {@link Bundesland} is not a persistent entity within the accounting model, but users
	 * shouldn't have to select this value each and every time they're trying to generate an XML. 
	 */
	private static final String LAST_KNOWN_BUNDESLAND = "lastKnownBundesland"; //$NON-NLS-1$
	
	/**
	 * Accounting service instance needed for retrieving source data.
	 */
	private AccountingService accountingService;
		
	/**
	 * Creates a new DTO builder instance.
	 * 
	 * @throws AccountingException if the supplied service is <code>null</code>
	 */
	protected ElsterDTOBuilder(AccountingService accountingService) {
		if (accountingService == null) {
			throw new AccountingException(Messages.ElsterDTOBuilder_ErrorAccountingServiceNull);
		}
		this.accountingService = accountingService;
	}
	
	/**
	 * Creates and fills a new {@link ElsterDTO} based on the supplied period and using the {@link AccountingService}
	 * supplied with the constructor to query user, address and income statement information.
	 * 
	 * @param period the filing period for which to build the DTO
	 * 
	 * @return a new {@link ElsterDTO} with data for the supplied period
	 */
	protected ElsterDTO createDTO(YearMonth period) {
		ElsterDTO dto = new ElsterDTOImpl(period);
		
		setCreationDate(dto);
		setTimeFrames(dto);
		
		User user = accountingService.getCurrentUser();
		
		setNames(user, dto);
		readBundeslandFromPreferences(dto);
		setTaxNumber(user, dto);
		setContactInformation(user.getAddress(), dto);
		
		buildAmounts(accountingService.getIncomeStatement(TimeFrame.of(period)), dto);
		
		dto.addPropertyChangeListener("finanzAmtBL", new PropertyChangeListener() { //$NON-NLS-1$
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				try {
					IEclipsePreferences prefs = getPrefs();
					prefs.put(LAST_KNOWN_BUNDESLAND, evt.getNewValue().toString());
					prefs.flush();
					LOG.debug("Saved user-chosen Bundesland to preferences: " + evt.getNewValue().toString()); //$NON-NLS-1$
				} catch (BackingStoreException e) {
					LOG.warn("Could not save user-chosen Bundesland value", e); //$NON-NLS-1$
				}
			}
		});
		
		return dto;
	}
	
	/**
	 * Changes the date and monetary amount values of the supplied dto if possible.
	 * Essentially that means if the supplied object is an instance of {@link ElsterDTOImpl}, the filing period
	 * can be altered. If the dto is of another implementation class, a brand new object is returned using
	 * {@link #createDTO(YearMonth)}.
	 *  
	 * @param dto the data transfer object to adapt
	 * 
	 * @param period the filing period to adapt to
	 * 
	 * @return the adapted DTO, or a brand new one if adaption is not possible
	 * 
	 * @see ElsterDTO#getFilingPeriod()
	 */
	protected ElsterDTO adaptToPeriod(ElsterDTO dto, YearMonth period) {
		if (dto instanceof ElsterDTOImpl) {
			((ElsterDTOImpl)dto).setFilingPeriod(period);
			setCreationDate(dto);
			setTimeFrames(dto);

			// reset all monetary values before re-calculating them
			dto.setRevenue7(null);
			dto.setRevenue7tax(null);
			dto.setRevenue19(null);
			dto.setRevenue19tax(null);
			dto.setInputTax(null);
			dto.setTaxSum(null);
			buildAmounts(accountingService.getIncomeStatement(TimeFrame.of(period)), dto);
			
			return dto;
		} else {
			LOG.warn("Supplied ElsterDTO is an unknown implementation class, will return brand new one"); //$NON-NLS-1$
			return createDTO(period);
		}
	}
	
	/**
	 * 
	 * @param dto
	 */
	private void setCreationDate(ElsterDTO dto) {
		dto.setCreationDate(LocalDate.now().format(BASIC_ISO_DATE_FORMATTER));
	}
	
	/**
	 * 
	 * @param dto
	 */
	private void setTimeFrames(ElsterDTO dto) {
		YearMonth period = dto.getFilingPeriod();
		dto.setTimeFrameYear(Integer.toString(period.getYear()));
		dto.setTimeFrameMonth(String.format(RegexPatterns.MONTH_FORMAT_PATTERN, period.getMonthValue()));
	}
	
	/**
	 * 
	 * @param user
	 * @param dto
	 */
	private void setNames(User user, ElsterDTO dto) {
		String[] names = user.getName().split(Constants.BLANK_STRING);
		
		switch (names.length) {
		case 1:
			LOG.debug("Only one name: " + names[0]); //$NON-NLS-1$
			dto.setCompanyName(names[0]);
			break;
		case 2:
			dto.setUserLastName(names[1]);
			dto.setUserFirstName(names[0]);
			LOG.debug("Split up name: " + names[0] + Constants.BLANK_STRING + names[1]); //$NON-NLS-1$
			break;
		default:
			LOG.debug("Too many names to handle: " + user.getName()); //$NON-NLS-1$
			break;
		}
	}
	
	/**
	 * 
	 * @param dto
	 */
	private void readBundeslandFromPreferences(ElsterDTO dto) {
		IEclipsePreferences prefs = getPrefs();
		String lastKnownBL = prefs.get(LAST_KNOWN_BUNDESLAND, null);
		if (lastKnownBL == null) {
			LOG.debug("No previous Bundesland found, user must choose"); //$NON-NLS-1$
		} else {
			LOG.debug("Found previously used Bundesland: " + lastKnownBL); //$NON-NLS-1$
			dto.setFinanzAmtBL(Bundesland.valueOf(lastKnownBL));
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private IEclipsePreferences getPrefs() {
		return InstanceScope.INSTANCE.getNode(ElsterDTOBuilder.class.getPackage().getName());
	}
	
	/**
	 * 
	 * @param user
	 * @param dto
	 */
	private void setTaxNumber(User user, ElsterDTO dto) {
		dto.setCompanyTaxNumberOrig(user.getTaxNumber());
	}
	
	/**
	 * 
	 * @param address
	 * @param dto
	 */
	private void setContactInformation(Address address, ElsterDTO dto) {
		if (address == null) {
			return;
		}
		
		dto.setCompanyEmail(address.getEmail());
		dto.setCompanyPhone(address.getPhoneNumber());
		
		// separate street name and number
		LOG.debug("Parsing street name and number: " + address.getStreet()); //$NON-NLS-1$
		StringBuilder nameBuilder = new StringBuilder();
		for (String part : address.getStreet().split(Constants.BLANK_STRING)) {
			if (part.matches(RegexPatterns.NUMBERS_ONLY_PATTERN)) {
				LOG.debug("Assuming number: " + part); //$NON-NLS-1$
				dto.setCompanyStreetNumber(part);
			} else if (part.matches(RegexPatterns.STREET_WITH_ADDENDUM_PATTERN)) {
				LOG.debug("Assuming number with addendum: " + part); //$NON-NLS-1$
				String[] split = part.split(RegexPatterns.NON_WORD_CHARS);
				LOG.debug("Assuming street number: " + split[0]); //$NON-NLS-1$
				dto.setCompanyStreetNumber(split[0]);
				LOG.debug("Assuming addendum: " + split[1]); //$NON-NLS-1$
				dto.setCompanyStreetAddendum(split[1]);
			} else if (part.matches(RegexPatterns.STREET_WITH_ADDENDUM_PATTERN_2)) {
				final String strNo = part.split(RegexPatterns.SINGLE_NON_DIGIT)[0];
				LOG.debug("Assuming street number: " + strNo); //$NON-NLS-1$
				dto.setCompanyStreetNumber(strNo);
				dto.setCompanyStreetAddendum(part.substring(strNo.length()));
				LOG.debug("Assuming addendum: " + dto.getCompanyStreetAddendum()); //$NON-NLS-1$
			} else {
				if (nameBuilder.length() > 0) {
					nameBuilder.append(Constants.BLANK_STRING);
				}
				nameBuilder.append(part);
			}
		}
		dto.setCompanyStreetName(nameBuilder.toString());
		
		dto.setCompanyPostCode(address.getPostalCode());
		dto.setCompanyCity(address.getCity());
		dto.setCompanyCountry("Deutschland"); // FIXME make me changeable
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 */
	private void buildAmounts(IncomeStatement source, ElsterDTO target) {
		LOG.debug("Building Amounts..."); //$NON-NLS-1$
		Map<TaxRate, Price> revenueByRate = CalculationUtil.calculateTotalRevenueByTaxRate(source.getRevenue().getInvoices());
		
		BigDecimal rev19 = null;
		BigDecimal rev19tax = null;
		BigDecimal rev7 = null;
		BigDecimal rev7tax = null;
		
		BigDecimal outputTax = BigDecimal.ZERO;
		
		for (TaxRate rate : revenueByRate.keySet()) {
			if (rate != null) {
				if (rate.getRate().compareTo(VAT_19) == 0) {
					LOG.debug("Found USt. 19 %"); //$NON-NLS-1$
					rev19 = adaptRevenue(revenueByRate.get(rate).getNet());
					rev19tax = rev19.multiply(rate.getRate()).setScale(2, RoundingMode.HALF_EVEN);
					outputTax = outputTax.add(rev19tax);
				} else if (rate.getRate().compareTo(VAT_7) == 0) {
					LOG.debug("Found USt. 7%"); //$NON-NLS-1$
					rev7 = adaptRevenue(revenueByRate.get(rate).getNet());
					rev7tax = rev7.multiply(rate.getRate()).setScale(2, RoundingMode.HALF_EVEN);;
					outputTax = outputTax.add(rev7tax);
				} else {
					LOG.debug("Unsupported tax rate: " + rate.toShortString()); //$NON-NLS-1$
				}
			}
		}
		
		if (rev19 != null) {
			target.setRevenue19(rev19);
			target.setRevenue19tax(rev19tax);			
		} else {
			target.setRevenue19(BigDecimal.ZERO);
			target.setRevenue19tax(BigDecimal.ZERO);
		}

		if (rev7 != null) {
			target.setRevenue7(rev7);
			target.setRevenue7tax(rev7tax);			
		}

		if (source.getTotalExpenses().getTax() != null) {
			target.setInputTax(source.getTotalExpenses().getTax().setScale(2, RoundingMode.HALF_EVEN));
			target.setTaxSum(outputTax.subtract(target.getInputTax()));
		} else {
			target.setTaxSum(outputTax);
		}
	}
	
	/**
	 * Rounds a supplied revenue amount down to a scale of 0.
	 * 
	 * @param orig source
	 * @return the re-scaled and rounded-down amount
	 */
	private BigDecimal adaptRevenue(BigDecimal orig) {
		return orig.setScale(0, RoundingMode.DOWN);
	}
}
