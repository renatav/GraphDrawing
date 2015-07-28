package graph.layout;

import java.util.HashMap;
import java.util.Map;

public class GraphLayoutProperties {
	
	private Map<PropertyEnums, Object> propeprtiesMap = new HashMap<PropertyEnums, Object>();
	
	public void setProperty(PropertyEnums key, Object value){
		propeprtiesMap.put(key, value);
	}
	
	public Object getProperty(PropertyEnums key){
		return propeprtiesMap.get(key);
	}

}
