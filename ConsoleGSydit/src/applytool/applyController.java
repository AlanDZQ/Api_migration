package applytool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import partial.code.grapa.dependency.graph.DataFlowAnalysisEngine;
import partial.code.grapa.tool.FileUtils;
import partial.code.grapa.version.detect.CodeElementVisitor;
import partial.code.grapa.version.detect.VersionPair;
import partial.code.grapa.version.detect.Versions;

import com.ibm.wala.cast.java.translator.jdt.JDTSourceModuleTranslator;

import edu.vt.cs.append.CommonValue;
import edu.vt.cs.changes.ChangeDistillerClient;
import edu.vt.cs.changes.ChangeFact;
import edu.vt.cs.changes.CommitComparatorClient;
import edu.vt.cs.changes.api.CDGraphConvertor;
import edu.vt.cs.changes.api.DefUseChangeDetector;
import edu.vt.cs.changes.api.GraphConvertor2;

public class applyController {
	private ArrayList<File> the_files;
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
	
	public boolean check_code(String the_file_name) {
		the_files = new ArrayList<File>();
		File the_file = new File(the_file_name);
		the_files.add(the_file);
		String oldc = readToString(the_file_name);
		dependency_check();
		this.old_codesnip = oldc;
		boolean rst = false;
		for (int i=0; i<stored_pattern_sets.size(); i++) {
			patternSet thepset = stored_pattern_sets.get(i);
			
			if (thepset.checkWith(oldc)) {
				rst = true;
//				String mock_newc = thepset.applyTo(oldc);
//				dependency_check();
//				if (semantic_check(oldc, mock_newc))
//					oldc = thepset.applyTo(oldc);
			}
		}
		if (rst == true)
			this.new_codesnip = oldc;
		return rst;
	}
	
	public boolean dependency_check() {
		String j2seDir = "/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/";
		String libDir = "/home/shengzhex/Documents/research_repo/luna_api_migration/lucene/";
		String oldversion = "2.3.2";
		
		VersionPair pair = new VersionPair();
		Set<File> old_set = new HashSet<File>();
		for (File x : the_files) {
			old_set.add(x);
		}
		pair.left = createVersions(old_set, oldversion);
		
		
		DataFlowAnalysisEngine leftEngine = new DataFlowAnalysisEngine();
		leftEngine.setExclusionsFile(null);
		
		leftEngine.addtoScope("left", pair.left.pTable, j2seDir, libDir, null, oldversion, this.the_files);
		leftEngine.initClassHierarchy();
		Map<IFile, CompilationUnit> leftMap = new HashMap<IFile, CompilationUnit>(JDTSourceModuleTranslator.fileToCu);
		boolean bLeftSuccess = false;
		if (!leftMap.isEmpty()) {
			bLeftSuccess = true;
			DefUseChangeDetector detector = new DefUseChangeDetector();
			CompilationUnit apply_cunit = leftMap.get(the_files.get(0));
			GraphConvertor2 apply_data_c = new GraphConvertor2();
			apply_data_c.init(leftEngine.getCurrentIR(), lfg,
					   oldMethod,
					   oldMapper.getLineMapper(apply_cunit), apply_cunit);  
			CDGraphConvertor apply_control_c = new CDGraphConvertor();
		}
		
		return false;
	}
	
    private Versions createVersions(Set<File> fSet,  String ver) {
    	Versions v = new Versions();
    	v.versions.add(ver);
    	for (File f : fSet) {
    		String sourcecode = FileUtils.getContent(f); //removed by nameng
    		ASTParser parser = ASTParser.newParser(AST.JLS8);
    		parser.setSource(sourcecode.toCharArray());
    		CompilationUnit tree = (CompilationUnit) parser.createAST(null);
    		CodeElementVisitor visitor = new CodeElementVisitor();
			tree.accept(visitor);
			if(visitor.bUnit){
				v.testFiles.add(f);
			}else{
				PackageDeclaration p = tree.getPackage();
				System.out.println(p);
				String paName = "";
				if(p!=null){
					System.out.println(p.getName());
					System.out.println(p.getName().getFullyQualifiedName());
					
					paName = p.getName().getFullyQualifiedName();
				}
				v.pTable.put(f, paName);
			}
    	}
    	return v;
    }
	
	public boolean semantic_check(String olc, String mock_newc) {
		ChangeDistillerClient client = new ChangeDistillerClient();
		String old_version = "2.3.2";
		String new_version = "4.7.0";
		String project_name = "mock";
		String lib_name = "lucene";
		CommonValue.set_possible_name("org.apache.lucene");
		CommonValue.common_old_version = lib_name + old_version;
		CommonValue.common_new_version = lib_name + new_version;
		CommonValue.common_project_name = project_name;
		String folderName = "/home/shengzhex/Desktop/research_repo/luna_api_migration/inputoutput";			
		List<ChangeFact> cfList = client.parseChanges(folderName);
		CommitComparatorClient client2 = new CommitComparatorClient(project_name, lib_name);
		client2.analyzeCommitForMigration(cfList, folderName, old_version, new_version);

		if (checkDepMap(CommonValue.dependencymap)) {
			return true;
		}
		return false;
	}
	
	public boolean checkDepMap(int[][] dependencymap) {
		// todo: make this to a automatic process
		// Set<String> pattern_dependencies
		String pattern_dependency = "1,2";
		int x = Integer.parseInt(pattern_dependency.split(",")[0]);
		int y = Integer.parseInt(pattern_dependency.split(",")[1]);
		if (dependencymap[x][y] == 0)
			return false;
		return true;
	}
	
	public String getNewCode() {
		return this.new_codesnip;
	}
	
	private boolean _checkOnePattern(patternSet one_set, String oldc) {
		return one_set.checkWith(oldc);		
	}			
	
	private String _applyOnePattern(patternSet one_set, String oldc) {
		return one_set.applyTo(oldc);		
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
            		patternSet inter_p = new patternSet();
            		inter_p.addpattern(oldp, newp);
                    this.stored_pattern_sets.add(inter_p);      
                    label = 0;
            	}
            	if (str.startsWith("=====")) {
            		label = 1;
            		continue;
            	}
            	if (label == 0) {
            		oldp.add(str);
            	}
            	else {
            		newp.add(str);
            	}            	
                System.out.println(str);
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
	
	public String readToString(String filename) {
        try {
            // read file content from file
            String sb= new String();
            
            FileReader reader = new FileReader(filename);
            BufferedReader br = new BufferedReader(reader);
            String str = null;
           
            while((str = br.readLine()) != null) {
                  sb += str+"\r\n";
                  System.out.println(str);
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
		applyController A = new applyController("/home/shengzhex/Desktop/research_repo/api_migration/inputoutput/patternfile.txt");		
//		A.fitPattern(patterns);
//		String the_file = "/home/shengzhex/Desktop/research_repo/api_migration/inputoutput/case1.txt";
		String the_file = "/home/shengzhex/Desktop/research_repo/api_migration/applytool/bin/applytool/input1.java";
//		String oldc = readToString(the_file);
		A.check_code(the_file);
	}
}