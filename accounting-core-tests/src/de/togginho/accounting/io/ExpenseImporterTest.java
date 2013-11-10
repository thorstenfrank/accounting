/*
 *  Copyright 2013 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashSet;

import org.junit.Test;

import de.togginho.accounting.BaseTestFixture;
import de.togginho.accounting.Constants;
import de.togginho.accounting.model.Expense;
import de.togginho.accounting.model.ExpenseCollection;
import de.togginho.accounting.model.ExpenseImportParams;
import de.togginho.accounting.model.ExpenseImportResult;

/**
 * @author thorsten
 *
 */
public class ExpenseImporterTest extends BaseTestFixture {
	
	/**
	 * Test method for {@link de.togginho.accounting.io.ExpenseImporter#importExpenses(java.lang.String, java.util.Set)}.
	 */
	@Test
	public void testImportExpenses() {
		ExpenseImportParams params = new ExpenseImportParams();
		params.setDateFormatPattern("dd.MM.yyyy");
		params.setDecimalMark(Constants.COMMA);
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
		params.setDateFormatPattern("yyyy-MM-dd");
		params.setDecimalMark(Constants.DOT);		
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
