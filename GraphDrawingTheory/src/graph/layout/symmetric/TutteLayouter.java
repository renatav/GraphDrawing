package graph.layout.symmetric;

import graph.algorithms.drawing.TutteEmbedding;
import graph.drawing.Drawing;
import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.SymmetricProperties;
import graph.layout.PropertyEnums.TutteProperties;
import graph.symmetry.CyclicSymmetricGraphDrawing;
import graph.symmetry.Permutation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TutteLayouter <V extends Vertex, E extends Edge<V>> extends SymmetricLayouter<V, E>{


	@Override
	public Drawing<V, E> layout(Graph<V, E> graph,
			GraphLayoutProperties layoutProperties) {

		if (layoutProperties.getProperty(SymmetricProperties.DISTANCE) != null)
			distance =  (Double) layoutProperties.getProperty(SymmetricProperties.DISTANCE);
		if (layoutProperties.getProperty(SymmetricProperties.PERMUTATION) != null)
			p = (Permutation) layoutProperties.getProperty(SymmetricProperties.PERMUTATION);
		if (layoutProperties.getProperty(SymmetricProperties.CENTER) != null)
			center = (Point2D) layoutProperties.getProperty(SymmetricProperties.CENTER);

		init(graph);


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