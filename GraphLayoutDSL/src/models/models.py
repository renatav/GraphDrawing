from factory.interfaces import ILayoutSubgraphs, ILayoutGraph

class MLayoutGraph(ILayoutGraph):
    '''This class contains all information regarding
    how a graph should be laid out. It is also used
    to describe how a subgraph should be laid out'''
    
    def __init__(self, graph, type, style = "", aestheticCriteria = None, algorithm = None):
        self.graph = graph
        self.type = type
        self.style = style
        self.aestheticCriteria = aestheticCriteria
        self.algorithm = algorithm
    
    def getGraph(self):
        return self.graph
    
    def getType(self):
        return self.type
    
    def getStyle(self):
        return self.style
    
    def getAestheticCriteria(self):
        return self.aestheticCriteria
    
    def getAlgorithm(self):
        return self.algorithm
    
class MLayoutSubgraphs(ILayoutSubgraphs):
    '''This class contains a list of instructions
    regarding how to lay out subgraphs'''
    
    def __init__(self, subgraphs):
        self.subgraphs = subgraphs
        
    def getSubgraphs(self):
        return self.subgraphs