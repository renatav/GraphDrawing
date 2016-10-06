# Graph Analysis and Drawing Library - Grad

Site: http://www.gradlibrary.net
Javadoc: https://renatav.github.io

Grad is a library for the Java programming language whose main goals are to provide a large number of graph drawing and analysis algorithms
and a very simple integration with existing tools. It consists of three projects:
- GraphDrawingTheory - the core of the library, containing numerous algorithms
- GraphEditor - a simple graph editor that can be used to test the layout and anaylisis features
- GraphLayoutDSL - defines a domain-specific language (DSL) for specifying how a graph should be laid out

##Implemented analysis algorithms
Grad offers implementations of several noteable graph analysis algorithms.

###Graph traversal
- Dijkstra's shortest path
- Depth-first search and construction of DFS trees

###Graph connectivity
- Checking connectivity, biconnectivity and triconnectivity of graphs
- Finding graph cut vertices and blocks (biconnected components) of a graph
- Hopcroft-Tarjan division of a graph into triconnected components
- Construction of BC (block-cut vertex) trees
- Construction of SPQR trees (not thoroughly teseted yet)
- Planar augmentation - an algorithm for turning a connected graph into a biconnected one. Based on Fialko and Mutzel's 5/3 approximation algorithm

###Cyclic properties
- Johnson's algorithms for finding simple cycles of a directed graph
- Paton's algorithm for finding cycles of an undirected graph

###Planarity testing
- Fraysseix-Mendez planarity testing
- Boyer-Myrvold planarity testing which finds the outside face
- PQ tree planarity testing with Booth and Lueker's algorithm, which finds the upwards embedding
- Finding an embedding and planar faces given an upwards embedding

###Graph symmetry
- McKay's canonical graph labeling algorithm (nauty) for finding permutations
- De Fraysseix's heuristic for graph symmetry detection
- Analyzing permutations and finding permtuation groups
- Forming permutation cycles

###Other
- Checking if a graph is bipartite
- Checking if a tree is binary
- Finding topological ordering
- Finding st-ordering

##Layout algorithms
Grad has several original implementaions of different graph drawing (layout) algorithms and ports some excellet implementations
form other open-soucce libraries. It strives to provide a very easy way of calling any desired algorithm.

###Original implementations
- Box layout (simple positioning of graph vertices into a table-like structure)
- Circle layout with or without the optimization of edge crossings
- Tutte straight-line drawing
- Concentric symmetric drawing based on Car and Kocay's algorithm
- Chiba's convex drawing of planar graphs
- Work in progress: orthogonal drawing

###Ported algorithms
- H-V tree drawing
- Tree balloon layout
- Node link tree layout
- Radial tree layout
- Hierarchical layout
- Spring, Kamada-Kawai and force-dircted layout
- Organic and fast-organic layouts
- ISOM layout





