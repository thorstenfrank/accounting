Acconting made easy for the self-employed.

This is **not** a double-entry bookkeeping, automatic this-and-that, ministry-of-finance-comliant-include-all-your-bank-accounts
type of software. It's a simple, bare bones tool that will help you create a handful of invoices each month, track outstanding
payments, record expenses (including recurring ones based on templates), and check out all of the above with some simple reports.

Or you just don't want your sensitive business data stored "in the cloud" (AKA someone else's computer). But you could, if
you really wanted to.

# Building

Run `mvn clean verify` in the `accounting-parent` sub-module. When finished, check the `accounting-product/target/products`
directory for a compressed executable program suitable to your operating system. To build the product for all supported
platforms, run Maven with the `-P=release` profile.

**NOTE**
The project is currently undergoing major upgrade work from an Eclipse 3-based to an e4 RCP application. Until finished, please use the `stable/v1` branch for a fully functional application!

Also ongoing is a migration from db4o to Apache Derby/JPA/Eclipselink as the persistence provider as well as a switch from
a highly customized Jasperreports-based solution to BIRT for generating documents and reports.

# Working with the sources
The sources include Eclipse project meta data. Since it is an Eclipse RCP-based product, and all. When importing the
projects into a workspace, note that at this time, a number of available modules will not compile:
* the old core bundle (and tests): deprecated, only for reference during migration
* ELSTER: migration outstanding
* reporting: the old reporting bundle (accounting-reporting) is deprecated and will be replaced

These bundles (modules) are already excluded from the maven build (`accounting-parent`) and can safely be omitted from
the Eclipse workspace unless explicitly required. Some external dependencies (custom db4o and gson bundles) have already
been deleted.
