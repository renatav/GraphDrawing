package interpreter.java;

import interfaces.ILayout;
import interfaces.ILayoutGraph;
import interfaces.ILayoutSubgraphs;

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

public class Interpreter {

	private PythonInterpreter interpreter = new PythonInterpreter();
	private PyInstance grammarInterpreter;
	
    /**
     * Create a new PythonInterpreter object, then use it to
     * execute some python code.
     * Once the module is imported than we obtain a reference to
     * it and assign the reference to a Java variable
     */

    public Interpreter() {
		String modulesDir = Paths.get("./src/modules").toAbsolutePath().normalize().toString();
		String modelsDir = Paths.get("./src/models").toAbsolutePath().normalize().toString();
		interpreter.exec("import sys; sys.path.insert(0, '" + modulesDir + "')");
		interpreter.exec("import sys; sys.path.insert(0, '" + modelsDir + "')");
        interpreter.exec("from interpreter.Interpreter import Interpreter");
        grammarInterpreter = (PyInstance) interpreter.eval("Interpreter()");
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
