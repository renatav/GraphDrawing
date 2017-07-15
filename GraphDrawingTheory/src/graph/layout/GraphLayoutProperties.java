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
	
	public void setProperty(PropertyEnums key, Object value){
		propeprtiesMap.put(key, value);
	}
	
	public Object getProperty(PropertyEnums key){
		return propeprtiesMap.get(key);
	}

}
