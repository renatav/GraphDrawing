package graph.layout.symmetric;

import graph.algorithms.drawing.TutteEmbedding;
import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.TutteProperties;
import graph.symmetry.CyclicSymmetricGraphDrawing;
import graph.symmetry.Permutation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TutteLayout <V extends Vertex, E extends Edge<V>> extends SymmetricLayouter<V, E>{

	public TutteLayout(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {
		super(graph, layoutProperties);
	}

	@Override
	public Drawing<V, E> layout() {


		distance =  (Double) layoutProperties.getProperty(TutteProperties.DISTANCE);
		center = (Point2D) layoutProperties.getProperty(TutteProperties.CENTER);
		p = (Permutation) layoutProperties.getProperty(TutteProperties.PERMUTATION);

		init();


		//for now, before implementing a better way to detect a face
		String faceStrList = (String) layoutProperties.getProperty(TutteProperties.FACE);

		List<V> face = new ArrayList<V>();

		if (faceStrList != null && !faceStrList.equals("")){

			//parse face
			String[] splitFace = faceStrList.split(",");
			for (String faceStr : splitFace){
				V v = graph.getVertexByContent(faceStr.trim());
				if (v != null)
					face.add(v);
			}
		}
		
		else{
			List<List<V>> circles;
			CyclicSymmetricGraphDrawing<V, E> symmetricDrawing = new CyclicSymmetricGraphDrawing<V,E>(graph);
			if (p == null || p.getPermutation().size() == 0)
				circles = symmetricDrawing.execute();
			else
				circles = symmetricDrawing.execute(p);

			Collections.sort(circles, new Comparator<List<V>>() {

				@Override
				public int compare(List<V> o1, List<V> o2) {
					if (o1.size() > o2.size())
						return 1;
					if (o1.size() < o2.size())
						return -1;
					return 0;
				}
			});
			face = circles.get(circles.size() - 1);
		}
		
		TutteEmbedding<V, E> tutteEmbedding = new TutteEmbedding<V, E>(graph);

		Drawing<V,E> drawing = new Drawing<V,E>();

		Map<V, Point2D> vertexPositions;
		vertexPositions = tutteEmbedding.execute(face, center, distance);
		drawing.getVertexMappings().putAll(vertexPositions);

		return drawing;
	}
}