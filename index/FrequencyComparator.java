
package edu.buffalo.cse.irf14.index;

import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;

public class FrequencyComparator implements Comparator<Integer> {
	
	// REFERENCE: http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
	
	public final Logger logger = Logger.getLogger(FrequencyComparator.class.getName());
	HashMap<Integer, Integer> freqMap = new HashMap<Integer, Integer>();
	
	public FrequencyComparator (HashMap<Integer, Integer> freqMap) {
		this.freqMap = freqMap;
	}
	
	@Override
	public int compare(Integer i, Integer j) {
		if (freqMap.get(i) >= freqMap.get(j))
			return -1;
		else
			return 1;
	}
	
}