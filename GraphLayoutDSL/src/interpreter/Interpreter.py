from textx.metamodel import metamodel_from_file
from textx.export import metamodel_export, model_export
from textx.exceptions import TextXSyntaxError
from pythonmodels import MLayoutGraph, MLayoutSubgraphs
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
                   
               
                layoutType = layoutSubgraph.layoutType
                layoutOneSubgraph = Interpreter.execute_one(layoutType, graph)
                layoutOneSubgraph.attr_graphContent = content
              
                subgraphs.append(layoutOneSubgraph)  
            
            return MLayoutSubgraphs(subgraphs)
        
        return 'executed'
    
    @staticmethod
    def execute_one(layout, graph):
            layoutType = layout.howToLayout
            
            if layoutType== 'algorithm':
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
                      
                layoutGraph =  MLayoutGraph(graph = graph, type = layoutType, algorithm =  algorithmProperties)  
                return layoutGraph
            elif layoutType == 'style':
                style = layout.style
                layoutGraph =  MLayoutGraph(graph = graph, type = layoutType, style = style)
                return layoutGraph
            elif layoutType == 'criteria':
                criteriaList = [];
                criteria = layout.aestheticCriteria
                for criterion in criteria:
                    criterionProperties = {}
                    for attr, value in criterion.__dict__.iteritems():
                        if not (attr.startswith('_') or attr == 'parent'):
                            criterionProperties[attr] = value
                    criteriaList.append(criterionProperties)
                layoutGraph = MLayoutGraph(graph = graph, type = layoutType, aestheticCriteria = criteriaList)
                return layoutGraph
