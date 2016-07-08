package interfaces;

import java.util.List;
import java.util.Map;

public interface ILayoutGraph extends ILayout{

	public String getGraph();
	public boolean isGraphContent();
	public String getType();
	public String getStyle();
	public List<Map<String, Object>> getAestheticCriteria();
	public Map<String, Object> getAlgorithm();
	public String getException();
}
