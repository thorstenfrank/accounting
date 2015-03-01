Accounting ELSTER Adapters
(c) 2015 Thorsten Frank (accounting@tfsw.de)

To add a new adapter:

1) Add the schema definition in folder elster-schema
2)
	a) If the new schema definition is structurally equal to the base:
		Create a subclass of de.tfsw.accounting.elster.adapter.base.BaseElsterInterfaceAdapter and override the getVersion() method
	
	b) If your adapter uses a schema that differs structurally from the base:
		- Create the JAXB model classes: <JDK_HOME>/bin/xjc -d src -p <packageName> -encoding UTF-8 -no-header ./elster-schema/<xsd_file_name>.xsd
	 	- Create an implementation of de.tfsw.accounting.elster.ElsterInterfaceAdapter that maps and returns the root element of your newly
	 	  created model

3) Add your implementation as an extension in plugin.xml under the existing point de.tfsw.accounting.elster.elsterInterfaceApdapter:
	      <elsterAdapter class="my.fully.qualified.ImplementationClassName" />
	      
4) Update the version of the plugin, update the product definition to include the new version... and deploy