package interpreter.java;


import interfaces.ILayout;
import interfaces.ILayoutGraph;
import interfaces.ILayoutSubgraphs;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import models.java.LayoutGraph;
import models.java.LayoutSubgraphs;

import org.python.core.PyInstance;
import org.python.core.PyObjectDerived;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import util.Factory;

/**
 * Interpreter. Receives the input string and calls python interpreter.
 * Singleton, since there is no need to construct the python and parse the grammar
 * several times.
 */
public class Interpreter {

	private PythonInterpreter interpreter = new PythonInterpreter();
	private PyInstance grammarInterpreter;
	private static Interpreter instance;
	
    /**
     * Create a new PythonInterpreter object, then use it to
     * execute some python code.
     * Once the module is imported than we obtain a reference to
     * it and assign the reference to a Java variable
     */

    public Interpreter() {
    	
		
		File f = new File(Interpreter.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String root = f.getAbsolutePath();
		String modulesDir = Paths.get(root,"modules").toAbsolutePath().normalize().toString();
		String modelsDir = Paths.get(root,"models").toAbsolutePath().normalize().toString();
		String languageDir = Paths.get(root,"language").toAbsolutePath().normalize().toString();
		modulesDir = modulesDir.replace("\\", "\\\\").replace("%20", " ");
		modelsDir = modelsDir.replace("\\", "\\\\").replace("%20", " ");
		languageDir = languageDir.replace("\\", "\\\\").replace("%20", " ");
		interpreter.exec("import sys; sys.path.insert(0, '" + modulesDir + "')");
		interpreter.exec("import sys; sys.path.insert(0, '" + modelsDir + "')");
		interpreter.exec("import sys; sys.path.insert(0, '" + languageDir + "')");
		

	//	interpreter.exec("import sys; sys.path.insert(0, '" + modulesDir + "')");
	//	interpreter.exec("import sys; sys.path.insert(0, '" + modelsDir + "')");
		interpreter.exec("import sys; print sys.path");
        interpreter.exec("from interpreter.Interpreter import Interpreter");
        grammarInterpreter = (PyInstance) interpreter.eval("Interpreter()");
    }
    
    public static Interpreter getInstance(){
    	if (instance == null)
    		instance = new Interpreter();
    	return instance;
    		
    }
    

    /**
     * The create method is responsible for performing the actual
     * coercion of the referenced python module into Java bytecode
     */
    public ILayout execute(String model) {

    	PyObjectDerived interpreted = (PyObjectDerived) grammarInterpreter.
    			invoke("execute",new PyString(model));
    	String type = interpreted.getType().toString();
    	ILayout ret;
    	if (type.contains("MLayoutGraph")){
    		ILayoutGraph pyLayoutGraph =  (ILayoutGraph) Factory.createJavaObject(ILayoutGraph.class, interpreted);
    		LayoutGraph layoutGraph = new LayoutGraph(pyLayoutGraph.getGraph(), 
    				pyLayoutGraph.getType(), pyLayoutGraph.getStyle(),
    				pyLayoutGraph.getAestheticCriteria(), pyLayoutGraph.getAlgorithm());
    		ret = layoutGraph;
    	}
    	else{
    		ILayoutSubgraphs pyLayoutSubgraphs =  (ILayoutSubgraphs) Factory.createJavaObject(ILayoutSubgraphs.class, interpreted);
    		List<ILayoutGraph> subgraphs = new ArrayList<ILayoutGraph>();
    		for (ILayoutGraph pyLayoutGraph : pyLayoutSubgraphs.getSubgraphs()){
    			LayoutGraph layoutGraph = new LayoutGraph(pyLayoutGraph.getGraph(), 
        				pyLayoutGraph.getType(), pyLayoutGraph.getStyle(),
        				pyLayoutGraph.getAestheticCriteria(), pyLayoutGraph.getAlgorithm());
    			subgraphs.add(layoutGraph);
    		}
    		LayoutSubgraphs layoutSubgraphs = new LayoutSubgraphs(subgraphs);
    		ret = layoutSubgraphs;
    	}
    	
    	return ret;	 
        
    }
    
}
