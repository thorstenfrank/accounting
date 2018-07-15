package de.tfsw.accounting;

import java.util.List;

import de.tfsw.accounting.model.Dummy;

public interface DummyService {

	List<Dummy> getDummies();
	
	void saveDummy(Dummy dummy);
}
