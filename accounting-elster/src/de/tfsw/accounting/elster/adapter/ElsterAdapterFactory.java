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
package de.tfsw.accounting.elster.adapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;

import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.Constants;
import de.tfsw.accounting.elster.IDs;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.IncomeStatement;
import de.tfsw.accounting.model.Price;
import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.model.User;
import de.tfsw.accounting.util.CalculationUtil;
import de.tfsw.accounting.util.TimeFrame;

/**
 * Factory to provide a concrete {@link ElsterAdapter} for a given time period that represents the period for which
 * VAT input/output is to be filed with the German electronic tax system (ELSTER).
 * 
 * @author Thorsten Frank
 *
 */
public class ElsterAdapterFactory {
		
	/**
	 * 
	 */
	private static final Logger LOG = Logger.getLogger(ElsterAdapterFactory.class);
	
	/**
	 * FIXME make this configurable
	 */
	private static final int[] YEARS = new int[]{2015};
	
	/**
	 * Standard 19% german VAT rate.
	 */
	private static final BigDecimal VAT_19 = new BigDecimal("0.19"); //$NON-NLS-1$
	
	/**
	 * Standard 7% german VAT rate.
	 */
	private static final BigDecimal VAT_7 = new BigDecimal("0.07"); //$NON-NLS-1$

	/**
	 * Access to needed services.
	 */
	private ServiceProvider serviceProvider;
	
	/**
	 * Creates a new factory for {@link ElsterAdapter} instances.
	 * 
	 * @param serviceProvider supply of necessary services
	 */
	public ElsterAdapterFactory(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
		init();
	}
	
	/**
	 * 
	 */
	private void init() {
		// lookup adapter extensions
		LOG.debug("Looking for defined adapters..."); //$NON-NLS-1$
		IConfigurationElement[] configElements = 
				serviceProvider.getExtensionRegistry().getConfigurationElementsFor(IDs.ADAPTER_EXTENSION_ID);
		LOG.debug(String.format("Found %d adapter definitions", configElements.length)); //$NON-NLS-1$
	}
	
	/**
	 * 
	 * @return
	 */
	public int[] getAvailableYears() {
		return YEARS;
	}
	
	/**
	 * 
	 * @param yearMonth
	 * @return
	 */
	public ElsterAdapter getAdapter(YearMonth yearMonth) {
		@SuppressWarnings("rawtypes") // we don't need to know the actual Interface type
		AbstractElsterAdapter adapter = new ElsterAdapter201501();
		
		adapter.init(buildDataObject(adapter, yearMonth));
		
		return adapter;
	}
	
	/**
	 * 
	 * @param yearMonth
	 * @return
	 */
	private ElsterDTO buildDataObject(@SuppressWarnings("rawtypes") AbstractElsterAdapter adapter, YearMonth yearMonth) {
		ElsterDTO data = new ElsterDTO(adapter.getTaxNumberConverter());
		
		data.setCreationDate(LocalDate.now().toString().replaceAll(RegexPatterns.NON_DIGITS, Constants.EMPTY_STRING));
		data.setTimeFrameYear(Integer.toString(yearMonth.getYear()));
		data.setTimeFrameMonth(String.format(RegexPatterns.MONTH_FORMAT_PATTERN, yearMonth.getMonthValue()));
		
		AccountingService service = serviceProvider.getAccountingService();
		User user = service.getCurrentUser();
		
		buildCompanyName(user, data);
		buildCompanyTaxNumber(user, data);
		buildCompanyAddressData(user.getAddress(), data);
		
		buildAmounts(service.getIncomeStatement(TimeFrame.of(yearMonth)), data);
		
		return data;
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 */
	private void buildCompanyName(User source, ElsterDTO target) {
		String[] names = source.getName().split(Constants.BLANK_STRING);
		
		switch (names.length) {
		case 1:
			LOG.debug("Only one name: " + names[0]); //$NON-NLS-1$
			target.setCompanyName(names[0]);
			break;
		case 2:
			target.setUserLastName(names[1]);
			target.setUserFirstName(names[0]);
			LOG.debug("Split up name: " + names[0] + Constants.BLANK_STRING + names[1]); //$NON-NLS-1$
			break;
		default:
			LOG.debug("Too many names: " + source.getName()); //$NON-NLS-1$
			break;
		}
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 */
	private void buildCompanyTaxNumber(User source, ElsterDTO target) {
		target.setCompanyTaxNumberOrig(source.getTaxNumber());
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 */
	private void buildCompanyAddressData(Address source, ElsterDTO target) {
		target.setCompanyEmail(source.getEmail());
		target.setCompanyPhone(source.getPhoneNumber());
		
		// separate street name and number
		LOG.debug("Parsing street name and number: " + source.getStreet()); //$NON-NLS-1$
		StringBuilder nameBuilder = new StringBuilder();
		for (String part : source.getStreet().split(Constants.BLANK_STRING)) {
			if (part.matches(RegexPatterns.NUMBERS_ONLY_PATTERN)) {
				LOG.debug("Assuming number: " + part); //$NON-NLS-1$
				target.setCompanyStreetNumber(part);
			} else if (part.matches(RegexPatterns.STREET_WITH_ADDENDUM_PATTERN)) {
				LOG.debug("Assuming number with addendum: " + part); //$NON-NLS-1$
				String[] split = part.split(RegexPatterns.NON_WORD_CHARS);
				LOG.debug("Assuming street number: " + split[0]); //$NON-NLS-1$
				target.setCompanyStreetNumber(split[0]);
				LOG.debug("Assuming addendum: " + split[1]); //$NON-NLS-1$
				target.setCompanyStreetAddendum(split[1]);
			} else if (part.matches(RegexPatterns.STREET_WITH_ADDENDUM_PATTERN_2)) {
				final String strNo = part.split(RegexPatterns.SINGLE_NON_DIGIT)[0];
				LOG.debug("Assuming street number: " + strNo); //$NON-NLS-1$
				target.setCompanyStreetNumber(strNo);
				target.setCompanyStreetAddendum(part.substring(strNo.length()));
				LOG.debug("Assuming addendum: " + target.getCompanyStreetAddendum()); //$NON-NLS-1$
			} else {
				if (nameBuilder.length() > 0) {
					nameBuilder.append(Constants.BLANK_STRING);
				}
				nameBuilder.append(part);
			}
		}
		target.setCompanyStreetName(nameBuilder.toString());
		
		target.setCompanyPostCode(source.getPostalCode());
		target.setCompanyCity(source.getCity());
		target.setCompanyCountry("Deutschland"); // FIXME make me changeable
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
					rev19tax = rev19.multiply(rate.getRate());
					outputTax = outputTax.add(rev19tax);
				} else if (rate.getRate().compareTo(VAT_7) == 0) {
					LOG.debug("Found USt. 7%"); //$NON-NLS-1$
					rev7 = adaptRevenue(revenueByRate.get(rate).getNet());
					rev7tax = rev7.multiply(rate.getRate());
					outputTax = outputTax.add(rev7tax);
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
			target.setInputTax(source.getTotalExpenses().getTax());
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
