from interfaces import ILayoutSubgraphs, ILayoutGraph, IExpression, ITerm,\
    IFactor

class MLayoutGraph(ILayoutGraph):
    '''This class contains all information regarding
    how a graph should be laid out. It is also used
    to describe how a subgraph should be laid out'''
    

    def __init__(self, graph = None, type = None, style = "",  graphContent = False, aestheticCriteria = None, 
        algorithm = None, criteriaExpression = None, exception = ""):
        self.attr_graph = graph
        self.attr_type = type
        self.attr_style = style
        self.attr_aestheticCriteria = aestheticCriteria
        self.attr_algorithm = algorithm
        self.attr_graphContent = graphContent
        self.attr_exception = exception
        self.attr_criteria_expresion= criteriaExpression
    
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
    
    def getCriteriaExpression(self):
        return self.attr_criteria_expresion
    
    
class MLayoutSubgraphs(ILayoutSubgraphs):
    '''This class contains a list of instructions
    regarding how to lay out subgraphs'''
    
    def __init__(self, subgraphs):
        self.att_subgraphs = subgraphs
        
    def getSubgraphs(self):
        return self.att_subgraphs
    
class MExpression(IExpression):
    
    def __init__(self, terms):
        self.attrterms = terms
        
    def getTerms(self):
        return self.att_terms
    
class MTerm(ITerm):
    
    def __init__(self, factors):
        self.att_factors = factors
        
    def getFactors(self):
        return self.att_factors
    
class MFactor(IFactor):
    
    def __init__(self, negative=False, aesthetic_criterion=None, expression=None):
        self.att_negative = negative
        self.att_aestheti_criterion = aesthetic_criterion
        self.att_expression = expression
        
    def isNegative(self):
        return self.att_negative
    
    def getAestheticCriterion(self):
        return self.att_aesthetic_criterion
    
    def getExpression(self):
        return self.att_expression