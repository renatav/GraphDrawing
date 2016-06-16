package util;

import java.nio.file.Paths;

import org.python.core.PyInstance;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class Factory {
	
	private PythonInterpreter interpreter = new PythonInterpreter();
	private PyInstance grammarInterpreter;
	
    /**
     * Create a new PythonInterpreter object, then use it to
     * execute some python code.
     * Once the module is imported than we obtain a reference to
     * it and assign the reference to a Java variable
     */

    public Factory() {
		String modulesDir = Paths.get("./src/modules").toAbsolutePath().normalize().toString();
		interpreter.exec("import sys; sys.path.insert(0, '" + modulesDir + "')");
        interpreter.exec("from interpreter.Interpreter import Interpreter");
        grammarInterpreter = (PyInstance) interpreter.eval("Interpreter()");
    }
    

    /**
     * The create method is responsible for performing the actual
     * coercion of the referenced python module into Java bytecode
     */
    public void execute(String model) {

    	PyString ret = (PyString) grammarInterpreter.invoke("execute",new PyString(model));
    	String s = ret.asString();
    	System.out.println(s);
        
    }

}
