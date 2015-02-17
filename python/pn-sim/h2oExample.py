'''
Created on Feb 16, 2015

@author: spino327
'''

from pnSim import PetriNet

if __name__ == '__main__':
    # now build a Petri net for two opposite transitions: 
    # combine: formation of water molecule
    # split: dissociation of water molecule 
    
    # combine: 2H + 1O -> 1H2O
    combineSpec = ("combine", [["H",2],["O",1]], [["H2O",1]])
    
    # split: 1H2O -> 2H + 1O 
    splitSpec = ("split", [["H2O"]], [["H",2],["O",1]])
    
    petriNet = PetriNet(
        ["H","O","H2O"],         # species
        [combineSpec,splitSpec]  # transitions
    )
    
    initialLabelling = {"H": 5, "O": 3, "H2O": 4}
    
    steps = 30 
    petriNet.runSimulation(steps, initialLabelling)