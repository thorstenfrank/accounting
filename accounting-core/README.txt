# DEPRECATED - this bundle is only available as reference during refactoring/migration and will be deleted!

accounting-core
(c) 2011, 2015 Thorsten Frank (accounting@tfsw.de)

The core API, service and utilities for the accounting application.

Notice for developers:

To generate the XML model (based on AccountingModel.xsd), run the following command in this project's root directory:
	<JDK_HOME>/bin/xjc -d src -p de.tfsw.accounting.io.xml -encoding UTF-8 -no-header -npa ./src/AccountingModel.xsd 