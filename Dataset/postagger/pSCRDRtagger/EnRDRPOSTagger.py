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
from InitialTagger.EnInitialTagger import EnInitTagger4Corpus, EnInitTagger4Sentence

#Change or add other thresholds: thresholds = [(5,5),(3, 2),(2,2)]
thresholds = [(3, 2)]

class EnRDRTree(PosTaggingRDRTree):
    """
    RDRPOSTagger for English
    """
    def __init__(self):
        self.root = None
    
    def tagRawSentence(self, DICT, rawLine):
        line = EnInitTagger4Sentence(DICT, rawLine)
        sen = ''
        wordTags = line.replace("“", "''").replace("”", "''").replace("\"", "''").split()
        for i in xrange(len(wordTags)):
            fwObject = FWObject.getFWObject(wordTags, i)
            word, tag = getWordTag(wordTags[i])
            node = self.findFiredNode(fwObject)
            sen += word + "/" + node.conclusion + " "
        return sen.strip()
    
    def tagRawCorpus(self, DICT, rawCorpusInputPath):
        print '\nTagging English raw corpus: %s' % rawCorpusInputPath
        outW = open(rawCorpusInputPath + ".TAGGED", "w")
        for line in open(rawCorpusInputPath, "r").readlines():
            outW.write(self.tagRawSentence(DICT, line) + "\n")  
        outW.close()
        print "Done!"

def EnTraining(args = sys.argv[2:]):
    pathToDict = args[0]
    dirPath = os.path.join(args[1] + "/")
    correctTrain = args[2]
    learntRules = args[3]
        
    print '\nTraining RDRPOSTagger for English POS Tagging...'   
    print "Initial tagging..."
    
    getRawTextFromFile(dirPath + correctTrain, dirPath + correctTrain + ".RAW")
    DICT = readDictionary(pathToDict)
    EnInitTagger4Corpus(DICT, dirPath + correctTrain + ".RAW", dirPath + correctTrain + ".INIT")
    
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
    print '\nRDRPOSTagger instructions for English:'
    print '\nTo train RDRPOSTagger for English POS tagging:'
    print '\npython EnRDRPOSTagger.py train PATH-TO-LEXICON PATH-TO-DIRECTORY GOLDEN-CORPUS MODEL-NAME'
    print '\nExample: python EnRDRPOSTagger.py train ../Sample/En/shortDict ../Sample/En correctTrain postagging.rdr'
    print '\nTo tag a English raw word-segmented corpus using the trained tagger:'
    print '\npython EnRDRPOSTagger.py tag PATH-TO-TRAINED-MODEL PATH-TO-LEXICON PATH-TO-RAW-CORPUS'
    print '\nExample: python EnRDRPOSTagger.py tag ../Sample/En/T3-2/postagging.rdr ../Sample/En/fullDict ../Sample/En/rawTest'
    print '\nHaving a look at http://rdrpostagger.sourceforge.net to find more information!!!'
    
def runEnRDRPOSTagger(args = sys.argv[1:]):
    if (len(args) == 0):
        printInstructions()
    elif args[0].lower().find("train") > -1:
        EnTraining()
    elif args[0].lower().find("tag") > -1:
        r = EnRDRTree()
        r.constructTreeFromRulesFile(args[1])
        DICT = readDictionary(args[2])
        r.tagRawCorpus(DICT, args[3])
    else:
        printInstructions()
        
if __name__ == "__main__":
    runEnRDRPOSTagger()
    pass
