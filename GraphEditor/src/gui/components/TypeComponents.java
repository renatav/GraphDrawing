package gui.components;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class TypeComponents {

	private static Map<Class<?>, Class<?>> typeComponentsMap;
	
	static {
		typeComponentsMap = new HashMap<Class<?>, Class<?>>();
		typeComponentsMap.put(String.class, JTextField.class);
		typeComponentsMap.put(Integer.class, JDigitsTextField.class);
		typeComponentsMap.put(Boolean.class, JCheckBox.class);
		typeComponentsMap.put(Double.class, JDoubleTextField.class);
	}
	
	
	public static JComponent createComponentForType(Class<?> type){
		try {
			Object ret = typeComponentsMap.get(type).newInstance();
			if (ret instanceof JTextField)
				((JTextField)ret).setColumns(10);
			return (JComponent) ret;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
