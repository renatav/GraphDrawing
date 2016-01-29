package gui.panels.layout;

import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums;
import gui.components.JDigitsTextField;
import gui.components.JDoubleTextField;
import gui.components.TypeComponents;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class LayoutPropertyPanel extends JPanel	{

	private static final long serialVersionUID = 1L;
	protected Map<Object, Component> componentsMap  = new HashMap<Object, Component>();

	public LayoutPropertyPanel (Class<?> enumClass){

		setLayout(new MigLayout());

		//analyze enum and generate panel
		Object[] consts = enumClass.getEnumConstants();
		Method m = null;
		Method hidden = null;
		Method typeM = null;
		try {
			m = enumClass.getMethod("getName");
			hidden = enumClass.getMethod("isHidden");
			typeM = enumClass.getMethod("getType");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		for (Object enumC : consts){
			try{
				if (hidden != null && (boolean) hidden.invoke(enumC))
					continue;

				if (m == null)
					add(new JLabel(enumC.toString()));
				else
					add(new JLabel((String) m.invoke(enumC) + ":"));

				Class<?> type = (Class<?>) typeM.invoke(enumC);
				JComponent component = TypeComponents.createComponentForType(type);

				add(component, "wrap");
				componentsMap.put(enumC, component);

			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}


		}
	}

	public void setDefaultValue(GraphLayoutProperties properties){
		if (properties == null)
			return;
		for (Object key : componentsMap.keySet()){
			if (properties.getProperty((PropertyEnums) key) != null)
				if (componentsMap.get(key) instanceof JTextField)
					((JTextField)componentsMap.get(key)).setText(properties.getProperty((PropertyEnums) key).toString());
				else if (componentsMap.get(key) instanceof JCheckBox)
					((JCheckBox)componentsMap.get(key)).setSelected((Boolean)properties.getProperty((PropertyEnums) key));
		}
	}

	public GraphLayoutProperties getEnteredLayoutProperties(){
		GraphLayoutProperties layoutProperties = new GraphLayoutProperties();
		for (Object key : componentsMap.keySet()){
			System.out.println(key);
			System.out.println(componentsMap.get(key));
			if (componentsMap.get(key) instanceof JComboBox<?>){
				System.out.println("combo");
				layoutProperties.setProperty((PropertyEnums)key, ((JComboBox<?>)componentsMap.get(key)).getSelectedItem());
			}
			else if (componentsMap.get(key) instanceof JDigitsTextField){
				String content = ((JTextField)componentsMap.get(key)).getText();
				if (content.equals(""))
					layoutProperties.setProperty((PropertyEnums)key, null);
				else{
					try{
						Integer integerValue = Integer.parseInt(content);
						layoutProperties.setProperty((PropertyEnums) key, integerValue);
					}
					catch(Exception ex){
						layoutProperties.setProperty((PropertyEnums)key, null);
					}
				}
			}
			else if (componentsMap.get(key) instanceof JDoubleTextField){
				String content = ((JTextField)componentsMap.get(key)).getText().replace(",","");
				
				if (content.equals(""))
					layoutProperties.setProperty((PropertyEnums)key, null);
				else{
					Double doubleValue = null;
					try{
						doubleValue = Double.parseDouble(content);
						layoutProperties.setProperty((PropertyEnums) key, doubleValue);
					}
					catch(Exception ex){
						if (!content.equals(""))
							layoutProperties.setProperty((PropertyEnums)key, null);
					}
				}
			}
			else if (componentsMap.get(key) instanceof JTextField){
				String content = ((JTextField)componentsMap.get(key)).getText();
				layoutProperties.setProperty((PropertyEnums)key, content);
			}
			else if (componentsMap.get(key) instanceof JCheckBox){
				layoutProperties.setProperty((PropertyEnums)key,((JCheckBox)componentsMap.get(key)).isSelected());
			}


		}
		return layoutProperties;
	}


}
