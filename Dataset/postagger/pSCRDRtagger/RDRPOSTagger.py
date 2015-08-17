# -*- coding: utf-8 -*-

import os,sys,time

#Set Python & directory paths
os.chdir("../")
sys.setrecursionlimit(100000)  
sys.path.append(os.path.abspath(""))
os.chdir("./pSCRDRtagger")
    
from SCRDRlearner.PosTaggingRDRTree import PosTaggingRDRTree
from Utility.Utils import getWordTag, getRawTextFromFile, readDictionary
from SCRDRlearner.Object import FWObject
from InitialTagger.InitialTagger import InitTagger4Corpus, InitTagger4Sentence

#Change or add other thresholds: thresholds = [(5,5),(3, 2),(2,2)]
thresholds = [(3, 2)]

class SampleRDRTree(PosTaggingRDRTree):
    """
    RDRPOSTagger for a particular language
    """
    def __init__(self):
        self.root = None
    
    def tagRawSentence(self, DICT, rawLine):
        line = InitTagger4Sentence(DICT, rawLine)
        sen = ''
        wordTags = line.replace("“", "''").replace("”", "''").replace("\"", "''").split()
        for i in xrange(len(wordTags)):
            fwObject = FWObject.getFWObject(wordTags, i)
            word, tag = getWordTag(wordTags[i])
            node = self.findFiredNode(fwObject)
            if node.depth > 0:
                sen += word + "/" + node.conclusion + " "
            else: #Fired at root, return initialized tag
                sen += word + "/" + tag + " "
        return sen.strip()

    def tagRawCorpus(self, DICT, rawCorpusInputPath):
        print '\nTagging raw word-segmented corpus: %s' % rawCorpusInputPath
        outW = open(rawCorpusInputPath + ".TAGGED", "w")
        for line in open(rawCorpusInputPath, "r").readlines():
            outW.write(self.tagRawSentence(DICT, line) + "\n")  
        outW.close()
        print "Done!"

def SampleTraining(args = sys.argv[2:]):
    pathToDict = args[0]
    dirPath = os.path.join(args[1] + "/")
    correctTrain = args[2]
    learntRules = args[3]
    
    print '\nTraining RDRPOSTagger for a particular language-driven POS Tagging...'   
    print "Initial tagging..."
    
    #Get raw text from golden corpus
    getRawTextFromFile(dirPath + correctTrain, dirPath + correctTrain + ".RAW")
    DICT = readDictionary(pathToDict)
    #Calling initial tagger for a specific language:
    InitTagger4Corpus(DICT, dirPath + correctTrain + ".RAW", dirPath + correctTrain + ".INIT")
    
    print "Done Initialization!"
    
    print 'Building SCRDR-based POS tagging tree of rules...'
    
    for (improveThreshold, matchThreshold) in thresholds:
        timeStart = time.time()
        outputDir = "T%d-%d/" % (improveThreshold, matchThreshold)
        os.mkdir(dirPath + outputDir)         
        
        rdrTree = PosTaggingRDRTree(improveThreshold, matchThreshold) 
        rdrTree.buildTreeFromCorpus(dirPath + correctTrain + ".INIT", dirPath + correctTrain)
        
        print "Write the tree to file..."
        rdrTree.writeToFileWithoutSeenCases(dirPath + outputDir + learntRules)
        #rdrTree.writeToFile(dirPath + outputDir + learntRules)
        
        print "\nTraining time for threshold %d-%d: %f seconds\n" % (improveThreshold, matchThreshold, time.time() - timeStart)
            
    print '\nCompleted!'

def printInstructions():
    print '\nRDRPOSTagger instructions for a specific language:'
    print '\nTo train RDRPOSTagger for POS tagging:'
    print '\npython RDRPOSTagger.py train PATH-TO-LEXICON PATH-TO-DIRECTORY GOLDEN-CORPUS MODEL-NAME'
    print '\nExample: python RDRPOSTagger.py train ../Sample/En/shortDict ../Sample/En correctTrain postagging.rdr'
    print '\nTo tag a raw word-segmented corpus using the trained tagger:'
    print '\npython RDRPOSTagger.py tag PATH-TO-TRAINED-MODEL PATH-TO-LEXICON PATH-TO-RAW-CORPUS'
    print '\nExample: python RDRPOSTagger.py tag ../Sample/En/T3-2/postagging.rdr ../Sample/En/fullDict ../Sample/En/rawTest'
    print '\nHaving a look at http://rdrpostagger.sourceforge.net to find more information!!!'
    
def runSampleRDRPOSTagger(args = sys.argv[1:]):
    if (len(args) == 0):
        printInstructions()
    elif args[0].lower().find("train") > -1:
        SampleTraining()
    elif args[0].lower().find("tag") > -1:
        r = SampleRDRTree()
        r.constructTreeFromRulesFile(args[1])
        DICT = readDictionary(args[2])
        r.tagRawCorpus(DICT, args[3])
    else:
        printInstructions()
        
if __name__ == "__main__":
    runSampleRDRPOSTagger()
    pass
