from interfaces import ILayoutSubgraphs, ILayoutGraph

class MLayoutGraph(ILayoutGraph):
    '''This class contains all information regarding
    how a graph should be laid out. It is also used
    to describe how a subgraph should be laid out'''
    

    def __init__(self, graph = None, type = None, style = "",  graphContent = False, aestheticCriteria = None, 
        algorithm = None, exception = ""):
        self.attr_graph = graph
        self.attr_type = type
        self.attr_style = style
        self.attr_aestheticCriteria = aestheticCriteria
        self.attr_algorithm = algorithm
        self.attr_graphContent = graphContent
        self.attr_exception = exception
    
    def getGraph(self):
        return self.attr_graph
    
    def getType(self):
        return self.attr_type
    
    def getStyle(self):
        return self.attr_style
    
    def getAestheticCriteria(self):
        return self.attr_aestheticCriteria
    
    def getAlgorithm(self):
        return self.attr_algorithm
       
    def isGraphContent(self):
        return self.attr_graphContent
    
    def getException(self):
        return self.attr_exception
    
    
class MLayoutSubgraphs(ILayoutSubgraphs):
    '''This class contains a list of instructions
    regarding how to lay out subgraphs'''
    
    def __init__(self, subgraphs):
        self.att_subgraphs = subgraphs
        
    def getSubgraphs(self):
        return self.att_subgraphs