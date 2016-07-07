package models.java;

import interfaces.ILayoutGraph;

import java.util.List;
import java.util.Map;

public class LayoutGraph implements ILayoutGraph{

	
	private String graph;
	private String type;
	private String style;
	private List<Map<String, Object>> aestheticCriteria;
	private Map<String, Object> algorithm;
	private boolean graphContent;
	
	
	public LayoutGraph(String graph, String type, String style,
			List<Map<String, Object>> aestheticCriteria,
			Map<String, Object> algorithm, boolean graphContent) {
		super();
		this.graph = graph;
		this.type = type;
		this.style = style;
		this.aestheticCriteria = aestheticCriteria;
		this.algorithm = algorithm;
		this.graphContent = graphContent;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LayoutGraph [graph=" + graph + ", type=" + type + ", style="
				+ style + ", aestheticCriteria=" + aestheticCriteria
				+ ", algorithm=" + algorithm + "]";
	}


	public String getGraph() {
		return graph;
	}


	public String getType() {
		return type;
	}


	public String getStyle() {
		return style;
	}


	public List<Map<String, Object>> getAestheticCriteria() {
		return aestheticCriteria;
	}


	public Map<String, Object> getAlgorithm() {
		return algorithm;
	}


	public boolean isGraphContent() {
		return graphContent;
	}

}
