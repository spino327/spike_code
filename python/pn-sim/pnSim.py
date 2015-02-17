#  
# Based on code by David Tanzer, Copyright 2012, GNU Public License 
# Contributors: David Tweed, Ken Webb

# This code is explained, along with the underlying concepts of Petri nets,
# in the article "Petri net programming" on the Azimuth Project blog. 
# See http://johncarlosbaez.wordpress.com/2012/10/01/petri-net-programming/

import string
from random import random,randrange

# Places are represented just by their names, no class is needed 

class Transition(object):
    '''
    This class represents a transition in a petri net graph. 
    
    As in the visual representation, a transition has a name that represents the concept of the 
    computation. Also, a transition has to define the input places and output places.
    
    The input and output places are represented as dictionaries. Fields used in this class: 
    - transitionName
    - inputMap: placesName -> inputCount
    - outputMap: placesName -> outputCount 
    '''

    def __init__(self, transitionName):
        '''
        Initialize a transition object with the given name, and with empty input and 
        output places.
        '''
        self.transitionName = transitionName
        self.inputMap = {} 
        self.outputMap = {} 

    def isEnabled(self, labelling):
        '''
        Checks if self transition is enable. This means that
        there is m tokens in each input place. Where m is the number
        of tokens required, by the transition, from a input place.
        '''
        for inputPlace in self.inputMap.keys():
            if labelling[inputPlace] < self.inputMap[inputPlace]: 
                return False  # not enough tokens
        return True  # good to go 

    def fire(self, labelling):
        '''
        Fires this transition. This means that the runtime has selected this transition
        from the enabled transitions to be fired. Basically, it consumes the tokens from
        the input places, and put one token in each output place.
        '''
        for inputName in self.inputMap.keys():
            labelling[inputName] = labelling[inputName] - self.inputMap[inputName] 
        for outputName in self.outputMap.keys():
            labelling[outputName] = labelling[outputName] + self.outputMap[outputName] 
        
        
class PetriNetBase(object):
    '''
    Petri net base class.
    '''
    # Fields:
    # placesNames
    # Transition list
    # labelling: speciesName -> token count 

    # constructor
    def __init__(self, placesNames, transitionSpecs):
        '''
        Initializes a petri net with the given places and transition specifications.
        '''
        self.placesNames = placesNames
        self.transitions = self.buildTransitions(transitionSpecs)

    def buildTransitions(self, transitionSpecs):
        '''
        Builds the transition list from the transition specs.
        
        in:
            transitionSpecs = 
        '''
        transitions = []

        for (transitionName, inputSpecs, outputSpecs) in transitionSpecs:
            transition = Transition(transitionName)

            for degreeSpec in inputSpecs:
                self.setDegree(transition.inputMap, degreeSpec)

            for degreeSpec in outputSpecs:
                self.setDegree(transition.outputMap, degreeSpec)

            transitions.append(transition)

        return transitions 

    def setDegree(self, dictionary, degreeSpec):
        '''
        
        '''
        placeName = degreeSpec[0]

        if len(degreeSpec) == 2:
            degree = degreeSpec[1]
        else: 
            degree = 1

        dictionary[placeName] = degree

    def getHeader(self):
        '''
        Return the name of the Headers. Thus, it is a string the name of the places, and the word "Transition"
        '''
        return string.join(self.placesNames, ", ") + ", \tTransition"
           
    def getLabelling(self):
        labelling = []
        for placeName in self.placesNames:
            labelling.append(self.labelling[placeName])
        return labelling

class PetriNet(PetriNetBase):

    def runSimulation(self, iterations, initialLabelling): 

        print "it\t" + self.getHeader()  # prints e.g. "H, O, H2O"

        self.labelling = initialLabelling

        for it in range(iterations):
            print str(it) + "\t" + str(self.getLabelling()) + "\t",
            if self.isHalted():
                print "halted"
                return 
            else:
                print self.fireOneRule()

        print "iterations completed" 

    def enabledTransitions(self):
        return filter(
            lambda transition: transition.isEnabled(self.labelling),
            self.transitions)

    def isHalted(self):
        return len(self.enabledTransitions()) == 0

    def fireOneRule(self):
        tmpTransition = self.selectRandom(self.enabledTransitions())
        tmpTransition.fire(self.labelling)
        return tmpTransition.transitionName

    def selectRandom(self, items):
        randomIndex = randrange(len(items))
        return items[randomIndex]
