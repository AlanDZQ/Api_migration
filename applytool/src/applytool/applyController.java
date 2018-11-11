package applytool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class applyController {
	private String old_codesnip;
	private String new_codesnip;	
	private List<patternSet> stored_pattern_sets;
	
	public applyController(String filename) {
		stored_pattern_sets = new ArrayList<>();
		readToPattern(filename);
	}
	
	public void fitPattern(patternSet one_set) {
		stored_pattern_sets.add(one_set);
	}
	
	public boolean check_code(String oldc) {
		this.old_codesnip = oldc;
		boolean rst = false;
		for (int i=0; i<stored_pattern_sets.size(); i++) {
			patternSet thepset = stored_pattern_sets.get(i);
			
			if (thepset.checkWith(oldc)) {
				rst = true;
				oldc = thepset.applyTo(oldc);
			}
		}
//		if (rst == true)
		this.new_codesnip = oldc;		
		return rst;
	}
	
	public String getNewCode() {
		return this.new_codesnip;
	}
	
	public void readToPattern(String filename) {
        try {
            // read file content from file
//            StringBuffer sb= new StringBuffer("");
        	patternSet rst = new patternSet();
            List<String> sb = new ArrayList();
            List<String> oldp = new ArrayList();
            List<String> newp = new ArrayList();
            
            FileReader reader = new FileReader(filename);
            BufferedReader br = new BufferedReader(reader);
            String str = null;
            int label = 0;
            
            while((str = br.readLine()) != null) {
            	if (str.equals("===")) {
            		if (oldp.isEmpty() && newp.isEmpty()) continue;
            		patternSet inter_p = new patternSet();
            		inter_p.addpattern(oldp, newp);
                    this.stored_pattern_sets.add(inter_p);      
                    label = 0;
                    oldp = new ArrayList();
                    newp = new ArrayList();
                    continue;
            	}
            	if (str.startsWith("=====")) {
            		label = 1;
            		continue;
            	}
            	if (label == 0) {
            		oldp.add(str.trim());
            	}
            	else {
            		newp.add(str.trim());
            	}            	
//                System.out.println(str);
            }
           
            br.close();
            reader.close();            
        }
        catch(FileNotFoundException e) {
        	e.printStackTrace();
        }
        catch(IOException e) {
        	e.printStackTrace();
        }
        return;
    }
	
	public static String readToString(String filename) {
        try {
            // read file content from file
            String sb= new String();
            
            FileReader reader = new FileReader(filename);
            BufferedReader br = new BufferedReader(reader);
            String str = null;
           
            while((str = br.readLine()) != null) {
                  sb += str+"\r\n";
//                  System.out.println(str);
            }
           
            br.close();
            reader.close();
            return sb;
        }
        catch(FileNotFoundException e) {
        	e.printStackTrace();
        }
        catch(IOException e) {
        	e.printStackTrace();
        }
        return "";
    }
	
	public static void main(String[] args) {
		applyController A = new applyController("C:\\Users\\v-shenxu\\Desktop\\sz_research\\api_mig\\applytool\\bin\\applytool\\patternfile.txt");		
//		A.fitPattern(patterns);
		String oldc = readToString("C:\\Users\\v-shenxu\\Desktop\\sz_research\\api_mig\\applytool\\bin\\applytool\\input1.txt");
		A.check_code(oldc);
		System.out.println(A.getNewCode());
	}
}