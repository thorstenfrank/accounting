Plugin unit tests for accounting-service-derby

Had to add org.eclipse.persistence.jpa.jpql as a bundle requirement otherwise the Tycho maven build fails due to
ClassNotFoundExceptions