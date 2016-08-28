package graph.test.layout;

import graph.algorithm.AlgorithmExecutor;
import graph.algorithm.ExecuteResult;
import graph.elements.Graph;
import graph.layout.DefaultGraphLayoutProperties;
import graph.layout.GraphLayoutProperties;
import graph.layout.LayoutAlgorithms;
import graph.layout.Layouter;
import graph.layout.PropertyEnums.CircleProperties;
import graph.layout.PropertyEnums.SymmetricProperties;
import graph.symmetry.Permutation;
import graph.test.elements.TestEdge;
import graph.test.elements.TestVertex;
import graph.traversal.DFSTreeTraversal;
import graph.trees.DFSTree;
import graph.util.Util;

import java.awt.Dimension;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;

public class LayoutsSpeedTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args){
		
		List<?>[] elements = Util.generateRandomGraph(100, TestVertex.class, 200, TestEdge.class);
		List<TestVertex> vertices = (List<TestVertex>) elements[0];
		List<TestEdge> edges = (List<TestEdge>) elements[1];
		
		for (int i = 0; i < vertices.size(); i++){
			TestVertex v = vertices.get(i);
			v.setSize(new Dimension(10, 10));
			v.setContent(i + "");
		}
		
		Graph<TestVertex, TestEdge> graph = new Graph<>(vertices, edges);
		Layouter<TestVertex, TestEdge> layouter = new Layouter<TestVertex, TestEdge>();
		layouter.setVertices(vertices);
		layouter.setEdges(edges);
		
		LayoutAlgorithms[] algorithms = new LayoutAlgorithms[12];
		algorithms[0] = LayoutAlgorithms.SPRING;
		algorithms[1] = LayoutAlgorithms.FRUCHTERMAN_REINGOLD;
		algorithms[2] = LayoutAlgorithms.KAMADA_KAWAI;
		algorithms[3] = LayoutAlgorithms.FAST_ORGANIC;
		algorithms[4] = LayoutAlgorithms.ISOM;
		algorithms[5] = LayoutAlgorithms.CIRCLE;
		algorithms[6] = LayoutAlgorithms.CIRCLE;
		algorithms[7] = LayoutAlgorithms.BOX;
		algorithms[8] = LayoutAlgorithms.COMPACT_TREE;
		algorithms[9] = LayoutAlgorithms.RADIAL_TREE;
		algorithms[10] = LayoutAlgorithms.BALLOON;
		algorithms[11] = LayoutAlgorithms.HIERARCHICAL;
		
		GraphLayoutProperties layoutProperties;
		
		for (int i = 0; i < 8; i++){
			
			LayoutAlgorithms algorithm = algorithms[i];
			System.out.println(algorithm);
			if (i == 6){
				layoutProperties = new GraphLayoutProperties();
				layoutProperties.setProperty(CircleProperties.OPTIMIZE_CROSSINGS, false);
			}
			else if (algorithm == LayoutAlgorithms.TUTTE){
				Map<Integer, Integer> permutation = new HashedMap<Integer, Integer>();
				for (int j = 0; j < 100; j++){
					permutation.put(j, j +1);
				}
				layoutProperties = new GraphLayoutProperties();
				
				Permutation p = new Permutation(permutation);
				layoutProperties.setProperty(SymmetricProperties.PERMUTATION, p);
				
			}
//			else if (algorithm == LayoutAlgorithms.CONCENTRIC){
//				Map<Integer, Integer> permutation = new HashedMap<Integer, Integer>();
//				for (int j = 0; j < 99; j++){
//					permutation.put(j, j +1);
//				}
//				permutation.put(99,0);
//				layoutProperties = new GraphLayoutProperties();
//				Permutation p = new Permutation(permutation);
//				layoutProperties.setProperty(SymmetricProperties.PERMUTATION, p);
//			}
			else
				layoutProperties = DefaultGraphLayoutProperties.getDefaultLayoutProperties(algorithm, graph);
			layouter.setLayoutProperties(layoutProperties);
			layouter.setAlgorithm(algorithm);
			
			int totalTime = 0;
			for (int j = 0; j < 3; j++){
				ExecuteResult results = AlgorithmExecutor.execute(layouter, "layout");
				System.out.println(results.getDuration());
				totalTime += results.getDuration();
			}
			System.out.println(totalTime/3);
		}
		
		DFSTreeTraversal<TestVertex, TestEdge> dfsTreeTraversal = new DFSTreeTraversal<>(graph);
		DFSTree<TestVertex, TestEdge> tree = dfsTreeTraversal.formDFSTree();
		
		
		layouter.setVertices(tree.getVertices());
		layouter.setEdges(tree.getTreeEdges());
		
		for (int i = 0; i < 12; i++){
			LayoutAlgorithms algorithm = algorithms[i];
			System.out.println(algorithm);
			if (i == 6){
				layoutProperties = new GraphLayoutProperties();
				layoutProperties.setProperty(CircleProperties.OPTIMIZE_CROSSINGS, false);
			}
			else if (algorithm == LayoutAlgorithms.TUTTE){
				Map<Integer, Integer> permutation = new HashedMap<Integer, Integer>();
				for (int j = 0; j < 100; j++){
					permutation.put(j, j +1);
				}
				layoutProperties = new GraphLayoutProperties();
				
				Permutation p = new Permutation(permutation);
				layoutProperties.setProperty(SymmetricProperties.PERMUTATION, p);
				
			}
//			else if (algorithm == LayoutAlgorithms.CONCENTRIC){
//				Map<Integer, Integer> permutation = new HashedMap<Integer, Integer>();
//				for (int j = 0; j < 99; j++){
//					permutation.put(j, j +1);
//				}
//				permutation.put(99,0);
//				layoutProperties = new GraphLayoutProperties();
//				Permutation p = new Permutation(permutation);
//				layoutProperties.setProperty(SymmetricProperties.PERMUTATION, p);
//			}
			else
				layoutProperties = DefaultGraphLayoutProperties.getDefaultLayoutProperties(algorithm, graph);
			layouter.setLayoutProperties(layoutProperties);
			layouter.setAlgorithm(algorithm);
			
			int totalTime = 0;
			for (int j = 0; j < 3; j++){
				ExecuteResult results = AlgorithmExecutor.execute(layouter, "layout");
				System.out.println(results.getDuration());
				totalTime += results.getDuration();
			}
			System.out.println(totalTime/3);
		}
		
		
		
		
		System.exit(0);
		
		
	}
}
