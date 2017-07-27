package graph.test.layout;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;

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
import graph.trees.dfs.DFSTree;
import graph.util.Util;

public class LayoutsSpeedTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args){

		Map<LayoutAlgorithms, Long> timesMap= new HashMap<LayoutAlgorithms, Long>();
		GraphLayoutProperties layoutProperties = new GraphLayoutProperties();
		Layouter<TestVertex, TestEdge> layouter = new Layouter<TestVertex, TestEdge>();
		layouter.setLayoutProperties(layoutProperties);
		
		LayoutAlgorithms[] algorithms = new LayoutAlgorithms[14];
		algorithms[0] = LayoutAlgorithms.SPRING;
		algorithms[1] = LayoutAlgorithms.FRUCHTERMAN_REINGOLD;
		algorithms[2] = LayoutAlgorithms.KAMADA_KAWAI;
		algorithms[3] = LayoutAlgorithms.ISOM;
		algorithms[4] = LayoutAlgorithms.FAST_ORGANIC;
		algorithms[5] = LayoutAlgorithms.ORGANIC;
		algorithms[6] = LayoutAlgorithms.SPTING2;
		algorithms[7] = LayoutAlgorithms.HIERARCHICAL;
		algorithms[8] = LayoutAlgorithms.TREE;
		algorithms[9] = LayoutAlgorithms.BALLOON;
		algorithms[10] = LayoutAlgorithms.RADIAL_TREE;
		algorithms[11] = LayoutAlgorithms.COMPACT_TREE;
		algorithms[12] = LayoutAlgorithms.RADIAL_TREE2;
		algorithms[13] = LayoutAlgorithms.NODE_LINK_TREE;
		
		int iterNum = 5;
		
		int start = 7;
		int end = 13;

		for (int iter = 0; iter < iterNum; iter ++){

			List<?>[] elements = Util.generateRandomGraph(1000, TestVertex.class, 2000, TestEdge.class);
			List<TestVertex> vertices = (List<TestVertex>) elements[0];
			List<TestEdge> edges = (List<TestEdge>) elements[1];

			for (int i = 0; i < vertices.size(); i++){
				TestVertex v = vertices.get(i);
				v.setSize(new Dimension(10, 10));
				v.setContent(i + "");
			}

			Graph<TestVertex, TestEdge> graph = new Graph<>(vertices, edges);
			
			DFSTreeTraversal<TestVertex, TestEdge> dfsTreeTraversal = new DFSTreeTraversal<>(graph);
			DFSTree<TestVertex, TestEdge> tree = dfsTreeTraversal.formDFSTree();
			
			layouter.setVertices(tree.getVertices());
			layouter.setEdges(tree.getEdges());


			//		algorithms[5] = LayoutAlgorithms.CIRCLE;
			//		algorithms[6] = LayoutAlgorithms.CIRCLE;
			//		algorithms[7] = LayoutAlgorithms.BOX;
			//		algorithms[8] = LayoutAlgorithms.COMPACT_TREE;
			//		algorithms[9] = LayoutAlgorithms.RADIAL_TREE;
			//		algorithms[10] = LayoutAlgorithms.BALLOON;
			//		algorithms[11] = LayoutAlgorithms.HIERARCHICAL;

			//GraphLayoutProperties layoutProperties;

			for (int i = start; i < end; i++){

				LayoutAlgorithms algorithm = algorithms[i];
				layouter.setAlgorithm(algorithm);
				
				System.out.println("Current algoritm " + algorithm);
				
				ExecuteResult results = AlgorithmExecutor.execute(layouter, "layout");
				if (!timesMap.containsKey(algorithm))
					timesMap.put(algorithm, 0L);
				timesMap.put(algorithm, timesMap.get(algorithm) + results.getDuration());
				System.out.println("Time " +results.getDuration());
				
			}
		}
					
					
				
				//			if (i == 6){
				//				layoutProperties = new GraphLayoutProperties();
				//				layoutProperties.setProperty(CircleProperties.OPTIMIZE_CROSSINGS, false);
				//			}
				//			else if (algorithm == LayoutAlgorithms.TUTTE){
				//				Map<Integer, Integer> permutation = new HashedMap<Integer, Integer>();
				//				for (int j = 0; j < 100; j++){
				//					permutation.put(j, j +1);
				//				}
				//				layoutProperties = new GraphLayoutProperties();
				//				
				//				Permutation p = new Permutation(permutation);
				//				layoutProperties.setProperty(SymmetricProperties.PERMUTATION, p);
				//				
				//			}
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
				//			else
				//				layoutProperties = DefaultGraphLayoutProperties.getDefaultLayoutProperties(algorithm, graph);
				//			layouter.setLayoutProperties(layoutProperties);
				//			layouter.setAlgorithm(algorithm);
				//			
				//			int totalTime = 0;
				//			for (int j = 0; j < 3; j++){
				//				ExecuteResult results = AlgorithmExecutor.execute(layouter, "layout");
				//				System.out.println(results.getDuration());
				//				totalTime += results.getDuration();
				//			}
				//			System.out.println(totalTime/3);
			//}
//

//
//
//			layouter.setVertices(tree.getVertices());
//			layouter.setEdges(tree.getTreeEdges());

//			for (int i = 0; i < 12; i++){
//				LayoutAlgorithms algorithm = algorithms[i];
//				System.out.println(algorithm);
//				if (i == 6){
//					layoutProperties = new GraphLayoutProperties();
//					layoutProperties.setProperty(CircleProperties.OPTIMIZE_CROSSINGS, false);
//				}
//				else if (algorithm == LayoutAlgorithms.TUTTE){
//					Map<Integer, Integer> permutation = new HashedMap<Integer, Integer>();
//					for (int j = 0; j < 100; j++){
//						permutation.put(j, j +1);
//					}
//					layoutProperties = new GraphLayoutProperties();
//
//					Permutation p = new Permutation(permutation);
//					layoutProperties.setProperty(SymmetricProperties.PERMUTATION, p);
//
//				}
//				//			else if (algorithm == LayoutAlgorithms.CONCENTRIC){
//				//				Map<Integer, Integer> permutation = new HashedMap<Integer, Integer>();
//				//				for (int j = 0; j < 99; j++){
//				//					permutation.put(j, j +1);
//				//				}
//				//				permutation.put(99,0);
//				//				layoutProperties = new GraphLayoutProperties();
//				//				Permutation p = new Permutation(permutation);
//				//				layoutProperties.setProperty(SymmetricProperties.PERMUTATION, p);
//				//			}
//				else
//					layoutProperties = DefaultGraphLayoutProperties.getDefaultLayoutProperties(algorithm, graph);
//				layouter.setLayoutProperties(layoutProperties);
//				layouter.setAlgorithm(algorithm);
//
//				int totalTime = 0;
//				for (int j = 0; j < 3; j++){
//					ExecuteResult results = AlgorithmExecutor.execute(layouter, "layout");
//					System.out.println(results.getDuration());
//					totalTime += results.getDuration();
//				}
//				System.out.println(totalTime/3);
	//		}

		//}

		for (int i = start; i < end; i++){

			LayoutAlgorithms algorithm = algorithms[i];
			System.out.println(algorithm + " " + timesMap.get(algorithm)/iterNum);
		}
		
		System.exit(0);
		

	}
}
