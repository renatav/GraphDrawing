from textx.metamodel import metamodel_from_file
from textx.export import metamodel_export, model_export
from models import MLayoutGraph, MLayoutSubgraphs
import os

class Interpreter():

    def __init__(self):
        meta_model_path = os.path.join(os.getcwd(), 'src\language\layout.tx')
        print meta_model_path
        metamodel = metamodel_from_file(meta_model_path)
        self.metamodel = metamodel

    def execute(self, model_str):
	
        #smisliti sta ce se vracati i kako ce to izgledati
        #mozda ovde napraviti nekiobjekat koji ce se vratiti
        #ali po tako da se pretvori u Java objekat kasnije
        #bolje nego da se vraca neki string
        	
        model = self.metamodel.model_from_str(model_str)
        
        if model.__class__.__name__ == 'LayoutGraph':
            print 'graph'
            layoutType = model.layoutType.howToLayout
            print(layoutType)
            if layoutType== 'algorithm':
                algorithm = model.layoutType.algorithm.name
                print(algorithm)
            elif layoutType== 'style':
                style = model.layoutType.style
                print(style)  
        else:
            if model.layoutSubgraphs:
                for layoutSubgraph in model.layoutSubgraphs:
                    subgraph = layoutSubgraph.subgraph
                    layoutType = layoutSubgraph.layoutType.howToLayout
                    print(layoutType)
                    if layoutType== 'algorithm':
                        algorithm = layoutSubgraph.layoutType.algorithm.name
                        print(algorithm)
                    elif layoutType== 'style':
                        style = layoutSubgraph.layoutType.style
                        print(style)  
            		       
                    if subgraph:  
            	       for vertex in subgraph.vertices:
                           print(vertex.index)
        
        return 'executed'
