package graph.layout;

import java.util.HashMap;
import java.util.Map;

/**
 * Class containing a map of layout properties and their values
 * as well as convenience methods for setting and getting values
 * of the properties
 * @author Renata
 */
public class GraphLayoutProperties {
	
	/**
	 * Map of properties and their values
	 */
	private Map<PropertyEnums, Object> propeprtiesMap = new HashMap<PropertyEnums, Object>();
	
	/**
	 * Sets a value of a property with the given key
	 * @param key Property's key
	 * @param value Value of the property 
	 */
	public void setProperty(PropertyEnums key, Object value){
		propeprtiesMap.put(key, value);
	}
	
	/**
	 * Return value of the property given its key
	 * @param key Property's key
	 * @return Value of the property 
	 */
	public Object getProperty(PropertyEnums key){
		return propeprtiesMap.get(key);
	}

}
