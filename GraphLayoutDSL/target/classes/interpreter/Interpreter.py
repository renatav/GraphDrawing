from textx.metamodel import metamodel_from_file
from textx.export import metamodel_export, model_export
from textx.exceptions import TextXSyntaxError
from pythonmodels import MLayoutGraph, MLayoutSubgraphs, MExpression, MTerm, MFactor
import os
import sys

class Interpreter():

    def __init__(self,):
        metamodel_dir = sys.path[0] 
        metamodel_path = os.path.join(metamodel_dir, "layout.tx")
        metamodel = metamodel_from_file(metamodel_path)
        self.metamodel = metamodel

    def execute(self, model_str):
	
        try:
            model = self.metamodel.model_from_str(model_str)
        except TextXSyntaxError as e:
            print(e.message)
            return MLayoutGraph(exception = e.message)
            
        
        if model.__class__.__name__ == 'LayoutGraph':
                layoutGraph = Interpreter.execute_one(model.layoutType, 'graph')
                print('graph')
                return layoutGraph
        else:
            print('subgraphs')
            subgraphs = []
            for layoutSubgraph in model.layoutSubgraphs:
                   
                subgraph = layoutSubgraph.subgraph
                if subgraph == None:
                   graph = 'others'
                else:
                    vertices = ''
                    content = True
                    for i,vertex in enumerate(subgraph.vertices):
                        if vertex.index:
                            vertices = vertices + str(vertex.index)
                            content = False
                        else:
                            vertices = vertices + vertex.content
                        if i < len(subgraph.vertices) - 1:
                                 vertices = vertices + ','
                        graph = vertices
                   
               
                layout_type = layoutSubgraph.layoutType
                layoutOneSubgraph = Interpreter.execute_one(layout_type, graph)
                layoutOneSubgraph.attr_graphContent = content
              
                subgraphs.append(layoutOneSubgraph)  
            
            return MLayoutSubgraphs(subgraphs)
        
        return 'executed'
    
    @staticmethod
    def execute_one(layout, graph):
        
            if layout.__class__.__name__ == "LayoutStyle":
                layout_type = "style"
            elif layout.__class__.__name__ == "LayoutAlgorithm":
                layout_type = "algorithm"
            elif layout.__class__.__name__ == "AestheticCriteria":
                layout_type = "criteria"
            else:
                layout_type = "mathCriteria"
            
            print(layout_type)
            
            if layout_type == 'algorithm':
                #a map that will contain all information about the algorithm
                algorithmProperties = {}
                algorithm = layout.algorithm
                #the algorithm could be of numerous classes
                for attr, value in algorithm.__dict__.iteritems():
                    if not (attr.startswith('_') or attr == 'parent'):
                        if attr == 'properties':
                            for property in value:
                                 for propertyAttr, propertyValue in property.__dict__.iteritems():
                                    if not (propertyAttr.startswith('_') or propertyAttr == 'parent'):
                                           algorithmProperties[propertyAttr] = propertyValue
                        else:
                            algorithmProperties[attr] = value
                      
                layoutGraph =  MLayoutGraph(graph = graph, type = layout_type, algorithm =  algorithmProperties)  
                return layoutGraph
            elif layout_type == 'style':
                style = layout.style
                layoutGraph =  MLayoutGraph(graph=graph, type=layout_type, style=style)
                return layoutGraph
            elif layout_type == 'criteria':
                criteriaList = [];
                criteria = layout.aestheticCriteria
                for criterion in criteria:
                    criterionProperties = {}
                    for attr, value in criterion.__dict__.iteritems():
                        if not (attr.startswith('_') or attr == 'parent'):
                            criterionProperties[attr] = value
                    criteriaList.append(criterionProperties)
                layoutGraph = MLayoutGraph(graph=graph, type=layout_type, aestheticCriteria=criteriaList)
                return layoutGraph
            elif layout_type == 'mathCriteria':
                m_expression = Interpreter.form_expression(layout.expression)
                layout_graph = MLayoutGraph(graph=graph, type=layout_type, criteriaExpression=m_expression )
                print(layout_graph.getCriteriaExpression())
                return layout_graph
               
    @staticmethod          
    def form_expression(expression):
            
        terms = [expression.expressionStartTerm]
        for orTerm in expression.expressionTerms:
            terms.append(orTerm.term)
            
        m_terms = [] 
        for term in terms:
            
            factors = [term.termStartFactor]
            for andFactor in term.termFactors:
                factors.append(andFactor.factor)
            m_factors = []
            
            for factor in factors:
                
                criterionProperties = None
                m_expression = None
                
                if factor.criterion is not None:
                    criterionProperties = {}
                    for attr, value in factor.criterion.__dict__.iteritems():
                        if not (attr.startswith('_') or attr == 'parent'):
                            criterionProperties[attr] = value
                            
                if factor.expression is not None:
                    m_expression = Interpreter.form_expression(factor.expression)
                    
                m_factor = MFactor(factor.negative, criterionProperties, m_expression)
                m_factors.append(m_factor)
                
            m_term = MTerm(m_factors)
            m_terms.append(m_term)
            
        return MExpression(m_terms)        
                
            