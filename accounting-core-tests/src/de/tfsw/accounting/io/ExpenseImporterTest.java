/*
 *  Copyright 2013 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashSet;

import org.junit.Test;

import de.tfsw.accounting.BaseTestFixture;
import de.tfsw.accounting.io.ExpenseImporter;
import de.tfsw.accounting.model.Expense;
import de.tfsw.accounting.model.ExpenseCollection;
import de.tfsw.accounting.model.ExpenseImportParams;
import de.tfsw.accounting.model.ExpenseImportResult;
import de.tfsw.accounting.model.ExpenseImportParams.DateFormatPattern;
import de.tfsw.accounting.model.ExpenseImportParams.DecimalMark;

/**
 * @author thorsten
 *
 */
public class ExpenseImporterTest extends BaseTestFixture {
	
	/**
	 * Test method for {@link de.tfsw.accounting.io.ExpenseImporter#importExpenses(java.lang.String, java.util.Set)}.
	 */
	@Test
	public void testImportExpenses() {
		ExpenseImportParams params = new ExpenseImportParams();
		params.setDateFormatPattern(DateFormatPattern.DMY);
		params.setDecimalMark(DecimalMark.COMMA);
		ExpenseImporter importer = new ExpenseImporter(
				new File("ExpenseImportTestData.csv"), getTestUser().getTaxRates(), params);
		parseAndAssert(importer);
	}

	/**
	 * 
	 */
	@Test
	public void testImportCustomDate() {
		ExpenseImportParams params = new ExpenseImportParams();
		params.setDateFormatPattern(DateFormatPattern.YMD);
		params.setDecimalMark(DecimalMark.DOT);		
		ExpenseImporter importer = new ExpenseImporter(
				new File("ExpenseImportTestData_customDate.csv"), getTestUser().getTaxRates(), params);
		parseAndAssert(importer);
	}
	
	/**
	 * 
	 * @param importer
	 */
	private void parseAndAssert(ExpenseImporter importer) {
		ExpenseImportResult result = importer.parse();
		assertEquals(3, result.getExpenses().size());
		
		ExpenseCollection ec = new ExpenseCollection(new HashSet<Expense>(result.getExpenses()));
		
		assertIsTestExpenses(ec);		
	}
	
}
