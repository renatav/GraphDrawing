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

	@Override
	public String getGraph() {
		return graph;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getStyle() {
		return style;
	}

	@Override
	public List<Map<String, String>> getAestheticCriteria() {
		return aestheticCriteria;
	}

	@Override
	public Map<String, String> getAlgorithm() {
		return algorithm;
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

}
