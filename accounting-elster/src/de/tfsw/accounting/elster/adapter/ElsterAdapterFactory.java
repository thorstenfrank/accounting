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

import de.tfsw.accounting.AccountingService;
import de.tfsw.accounting.Constants;
import de.tfsw.accounting.elster.AccountingElsterPlugin;
import de.tfsw.accounting.model.Address;
import de.tfsw.accounting.model.IncomeStatement;
import de.tfsw.accounting.model.Price;
import de.tfsw.accounting.model.TaxRate;
import de.tfsw.accounting.model.User;
import de.tfsw.accounting.util.CalculationUtil;
import de.tfsw.accounting.util.TimeFrame;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
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
	 * A pattern representing non-digits.
	 */
	private static final String NON_DIGIT = "[^\\d]";

	private static final BigDecimal VAT_19 = new BigDecimal("0.19");
	private static final BigDecimal VAT_7 = new BigDecimal("0.07");
	
	/**
	 * 
	 * @return
	 */
	public static int[] getAvailableYears() {
		return YEARS;
	}
	
	/**
	 * 
	 * @param yearMonth
	 * @return
	 */
	public static ElsterAdapter getAdapter(YearMonth yearMonth) {
		ElsterAdapter adapter = new ElsterAdapter201501();
		
		adapter.init(buildDataObject(yearMonth));
		
		return adapter;
	}
	
	/**
	 * 
	 * @param yearMonth
	 * @return
	 */
	private static ElsterDTO buildDataObject(YearMonth yearMonth) {
		ElsterDTO data = new ElsterDTO(new DefaultTaxNumberGenerationStrategy());
		
		data.setCreationDate(LocalDate.now().toString().replaceAll(NON_DIGIT, Constants.EMPTY_STRING));
		data.setTimeFrameYear(Integer.toString(yearMonth.getYear()));
		data.setTimeFrameMonth(String.format("%02d", yearMonth.getMonthValue()));
		
		AccountingService service = AccountingElsterPlugin.getAccountingService();
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
	private static void buildCompanyName(User source, ElsterDTO target) {
		String[] names = source.getName().split(Constants.BLANK_STRING);
		
		switch (names.length) {
		case 1:
			LOG.debug("Only one name: " + names[0]);
			target.setCompanyName(names[0]);
			break;
		case 2:
			target.setUserLastName(names[1]);
			target.setUserFirstName(names[0]);
			LOG.debug("Split up name: " + names[0] + Constants.BLANK_STRING + names[1]);
			break;
		default:
			LOG.debug("Too many names: " + source.getName());
			break;
		}
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 */
	private static void buildCompanyTaxNumber(User source, ElsterDTO target) {
		target.setCompanyTaxNumberOrig(source.getTaxNumber());
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 */
	private static void buildCompanyAddressData(Address source, ElsterDTO target) {
		target.setCompanyEmail(source.getEmail());
		target.setCompanyPhone(source.getPhoneNumber());
		
		// separate street name and number
		LOG.debug("Parsing street name and number: " + source.getStreet());
		StringBuilder nameBuilder = new StringBuilder();
		for (String part : source.getStreet().split(Constants.BLANK_STRING)) {
			if (part.matches("\\d+")) {
				LOG.debug("Assuming number: " + part);
				target.setCompanyStreetNumber(part);
			} else if (part.matches("\\d+\\W+\\w+")) {
				LOG.debug("Assuming number with addendum: " + part);
				String[] split = part.split("\\W+");
				LOG.debug("Assuming street number: " + split[0]);
				target.setCompanyStreetNumber(split[0]);
				LOG.debug("Assuming addendum: " + split[1]);
				target.setCompanyStreetAddendum(split[1]);
			} else if (part.matches("\\d+.*")) {
				final String strNo = part.split("\\D")[0];
				LOG.debug("Assuming street number: " + strNo);
				target.setCompanyStreetNumber(strNo);
				target.setCompanyStreetAddendum(part.substring(strNo.length()));
				LOG.debug("Assuming addendum: " + target.getCompanyStreetAddendum());
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
		target.setCompanyCountry("Deutschland"); // TODO make me changeable
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 */
	private static void buildAmounts(IncomeStatement source, ElsterDTO target) {
		LOG.debug("Building Amounts...");
		Map<TaxRate, Price> revenueByRate = CalculationUtil.calculateTotalRevenueByTaxRate(source.getRevenue().getInvoices());
		
		BigDecimal rev19 = null;
		BigDecimal rev19tax = null;
		BigDecimal rev7 = null;
		BigDecimal rev7tax = null;
		
		BigDecimal outputTax = BigDecimal.ZERO;
		
		for (TaxRate rate : revenueByRate.keySet()) {
			if (rate != null) {
				if (rate.getRate().compareTo(VAT_19) == 0) {
					LOG.debug("Found USt. 19 %");
					rev19 = adaptRevenue(revenueByRate.get(rate).getNet());
					rev19tax = rev19.multiply(rate.getRate());
					outputTax = outputTax.add(rev19tax);
				} else if (rate.getRate().compareTo(VAT_7) == 0) {
					LOG.debug("Found USt. 7%");
					rev7 = adaptRevenue(revenueByRate.get(rate).getNet());
					rev7tax = rev7.multiply(rate.getRate());
					outputTax = outputTax.add(rev7tax);
				}
			}
		}
		
		if (rev19 != null) {
			target.setRevenue19(rev19);
			target.setRevenue19tax(rev19tax);			
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
	 * 
	 * @param orig
	 * @return
	 */
	private static BigDecimal adaptRevenue(BigDecimal orig) {
		return orig.setScale(0, RoundingMode.DOWN);
	}
}
