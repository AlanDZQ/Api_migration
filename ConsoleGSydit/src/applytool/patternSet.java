package applytool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class patternSet {
	List<String> pattern_o;
	List<String> pattern_n;
	
	// map from ith |pattern in pattern_o| to several |patterns in pattern_new|
	// showing that the ith pattern should be changed to a list of pattern
	Map<Integer, List<Integer>> map_relationship;
	
	// to implement: the dual-direct map between real name in the code snippet
	// and the abs name in the pattern
	Map<String, String> realToabs;
	Map<String, String> absToreal;
	
	public patternSet() {
		pattern_o = new ArrayList<>();
		pattern_n = new ArrayList<>();
		map_relationship = new HashMap<>();
	}
	
	public patternSet(ArrayList<String> old_p, ArrayList<String> new_p, Map<Integer, List<Integer>> x) {
		pattern_o = new ArrayList<>();
		pattern_n = new ArrayList<>();
		map_relationship = new HashMap<>();
		
		// copy old_pattern to this instance
		for(int i=0;i<old_p.size();i++)
		{
			pattern_o.add(old_p.get(i));
		}
		// copy old_pattern to this instance
		for(int i=0;i<new_p.size();i++)
		{
			pattern_n.add(new_p.get(i));
		}
		
		map_relationship = x;		
	}
	
	public void reset() {
		this.map_relationship = new HashMap<>();
	}
	
	public void addpattern(List<String> oldp, List<String> newp) {
		this.pattern_o = oldp;
		this.pattern_n = newp;		
	}
	
	// check if the pattern can be found in order in this code snippet
	public boolean checkWith(String oldc) {
		String inter_p = oldc;		
		for (int i=0;i<pattern_o.size(); i++) {
			String one_sentence = pattern_o.get(i);
			int next_pos = inter_p.indexOf(one_sentence);
			if (next_pos == -1) return false;
			inter_p = inter_p.substring(next_pos + one_sentence.length());
		}
		return true;
	}
	
	// do the replacement in order
	public String applyTo(String oldc) {
		String rst = new String();
		String inter_p = oldc;		
		for (int i=0;i<pattern_o.size(); i++) {
			String one_sentence = pattern_o.get(i);
			int next_pos = inter_p.indexOf(one_sentence);
			if (next_pos == 0){
				// to do: add a list of map_relationship[i] pattern_n
				rst += pattern_n.get(i);				
			} 
			else {
				rst += inter_p.substring(0, next_pos - 1);		
				rst += pattern_n.get(i);
			}
			
			inter_p = inter_p.substring(next_pos + one_sentence.length());
		}
		if (!inter_p.isEmpty()) rst += inter_p;
		return rst;
	}
}