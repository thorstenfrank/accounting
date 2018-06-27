Acconting made easy for the self-employed.

This is **not** a double-entry bookkeeping, automatic this-and-that, ministry-of-finance-comliant-include-all-your-bank-accounts
type of software. It's a simple, bare bones tool that will help you create a handful of invoices each month, track outstanding
payments, record expenses (including recurring ones based on templates), and check out all of the above with some simple reports.
Or you don't want your sensitive business data stored only where you know and control it.

# Building

Run `mvn clean install` in the `accounting-parent` sub-module. When finished, check the `accounting-product/target/products`
directory for compressed executable applications for Linux, MacOS and Windows.

**NOTE**
The project is currently undergoing major upgrade work from an Eclipse 3-based to an e4 RCP application. Until finished, please use the `stable/v1` branch for a fully functional application!
