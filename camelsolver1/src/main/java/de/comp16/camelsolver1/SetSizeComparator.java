package de.comp16.camelsolver1;

import java.util.Comparator;
import java.util.Set;

public class SetSizeComparator implements Comparator<Set> {
    
	@Override
    public int compare(Set x, Set y) {
		if (x.size() > y.size()) return 1;
		if (x.size() < y.size()) return -1;
		return 0;		
    }

}
