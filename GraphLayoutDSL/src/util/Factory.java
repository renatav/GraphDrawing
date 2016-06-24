package util;

import org.python.core.PyObject;

public class Factory {

    public static Object createJavaObject(Class<?> clazz, PyObject pythonObject){
    	try {
			return pythonObject.__tojava__(Class.forName(clazz.toString().substring(
					clazz.toString().indexOf(" ")+1,
					clazz.toString().length())));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

}
