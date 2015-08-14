package gui.panels.layout;

import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class LayoutPropertyPanel extends JPanel	{

	private static final long serialVersionUID = 1L;
	protected Map<Object, JTextField> textFieldsMap  = new HashMap<Object, JTextField>();

	public LayoutPropertyPanel (Class<?> enumClass){

		setLayout(new MigLayout());
		
		//analyze enum and generate panel
		Object[] consts = enumClass.getEnumConstants();
		Method m = null;
		try {
			m = enumClass.getMethod("getName");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		for (Object enumC : consts){
			if (m == null)
				add(new JLabel(enumC.toString()));
			else
				try {
					add(new JLabel((String) m.invoke(enumC) + ":"));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			JTextField tf = new JTextField(10);
			add(tf, "wrap");
			textFieldsMap.put(enumC, tf);
		}
	}

	public void setDefaultValue(GraphLayoutProperties properties){
		if (properties == null)
			return;
		for (Object key : textFieldsMap.keySet()){
			if (properties.getProperty((PropertyEnums) key) != null)
				textFieldsMap.get(key).setText(properties.getProperty((PropertyEnums) key).toString());
		}
	}
	
	public GraphLayoutProperties getEnteredLayoutProperties(){
		GraphLayoutProperties layoutProperties = new GraphLayoutProperties();
		for (Object key : textFieldsMap.keySet()){
			String content = textFieldsMap.get(key).getText();
			Double doubleValue = null;
			try{
				doubleValue = Double.parseDouble(content);
			}
			catch(Exception ex){
			}
			layoutProperties.setProperty((PropertyEnums) key, doubleValue);
		}
		return layoutProperties;
	}


}
