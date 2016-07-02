package models.java;

import interfaces.ILayoutGraph;

import java.util.List;
import java.util.Map;

public class LayoutGraph implements ILayoutGraph{

	
	private String graph;
	private String type;
	private String style;
	private List<Map<String, String>> aestheticCriteria;
	private Map<String, String> algorithm;
	
	
	public LayoutGraph(String graph, String type, String style,
			List<Map<String, String>> aestheticCriteria,
			Map<String, String> algorithm) {
		super();
		this.graph = graph;
		this.type = type;
		this.style = style;
		this.aestheticCriteria = aestheticCriteria;
		this.algorithm = algorithm;
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


	public List<Map<String, String>> getAestheticCriteria() {
		return aestheticCriteria;
	}


	public Map<String, String> getAlgorithm() {
		return algorithm;
	}

}
