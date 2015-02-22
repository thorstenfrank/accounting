accounting-test-harness
(c) 2015 Thorsten Frank (accounting@tfsw.de)

Provides base functionality and access to commonly used libraries for unit tests (e.g. easymock).

Note that each individual test project still needs to add junit as a dependency separately - don't ask me why,
but if they don't, junit can't seem to find any runnable tests.