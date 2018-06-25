Logging support for the accounting bundles.
Re-exports log4j for use by other plugins and provides default log configurations.

The default log configuration simply writes to stdout (using log4j-test.xml, which is used by default by
log4j). This file is omitted during the build/export (see build.properties), so that when running the application
in a production environment, logs are written to files in user.home/.accounting.

Different log configurations can be used by applying the "log4j.configurationFile" system property, as is the
case e.g. during maven surefire tests (see accounting-parent POM for details) 

Apache Log4j
Copyright 1999-2014 Apache Software Foundation

This product includes software developed at
The Apache Software Foundation (http://www.apache.org/).

ResolverUtil.java
Copyright 2005-2006 Tim Fennell

Dumbster SMTP test server
Copyright 2004 Jason Paul Kitchen

TypeUtil.java
Copyright 2002-2012 Ramnivas Laddad, Juergen Hoeller, Chris Beams

Modified for use within the accounting application
(c) by Thorsten Frank (accounting@tfsw.de)