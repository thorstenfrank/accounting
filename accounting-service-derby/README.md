# Accounting Service Derby

Replaces the old ``accounting-core`` bundle and switches from db4o to Apache Derby as storage solution.

The original idea was to have a completely persistence-agnostic service implementation to which persistence layers can
be plugged in - but hey, after all the work with db4o and then the lengthy transition to JPA (end eclipselink!) - 
what? We're then going to move to neo4j or something? Naw.

About the switch from db4o - I really liked it as a simple to use yet reasonably stable, embeddable solution. 
But nobody's been working on it for years, and the flaws it does have became too tiring to work around or ignore. 