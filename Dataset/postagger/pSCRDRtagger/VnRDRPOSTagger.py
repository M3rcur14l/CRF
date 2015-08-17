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
from InitialTagger.VnInitialTagger import VnInitTagger4Corpus, VnInitTagger4Sentence

#Change or add other thresholds: thresholds = [(5,5),(3, 2),(2,2)]
thresholds = [(3, 2)]

class VnRDRTree(PosTaggingRDRTree):
    """
    RDRPOSTagger for Vietnamese
    """
    def __init__(self):
        self.root = None
    
    def tagRawSentence(self, DICT, rawLine):
        line = VnInitTagger4Sentence(DICT, rawLine)
        sen = ''
        wordTags = line.replace("“", "''").replace("”", "''").replace("\"", "''").split()
        for i in xrange(len(wordTags)):
            fwObject = FWObject.getFWObject(wordTags, i)
            word, tag = getWordTag(wordTags[i])
            node = self.findFiredNode(fwObject)
            sen += word + "/" + node.conclusion + " "
        return sen.strip()

    def tagRawCorpus(self, DICT, rawCorpusInputPath):
        print '\nTagging Vietnamese raw word-segmented corpus: %s' % rawCorpusInputPath
        outW = open(rawCorpusInputPath + ".TAGGED", "w")
        for line in open(rawCorpusInputPath, "r").readlines():
            outW.write(self.tagRawSentence(DICT, line) + "\n")  
        outW.close()
        print "Done!"

def VnTraining(args = sys.argv[2:]):
    pathToDict = args[0]
    dirPath = os.path.join(args[1] + "/")
    correctTrain = args[2]
    learntRules = args[3]
    
    print '\nTraining RDRPOSTagger for Vietnamese POS Tagging...'   
    print "Initial tagging..."
    
    getRawTextFromFile(dirPath + correctTrain, dirPath + correctTrain + ".RAW")
    DICT = readDictionary(pathToDict)
    VnInitTagger4Corpus(DICT, dirPath + correctTrain + ".RAW", dirPath + correctTrain + ".INIT")
    
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
    print '\nRDRPOSTagger instructions for Vietnamese:'
    print '\nTo train RDRPOSTagger for Vietnamese POS tagging:'
    print '\npython VnRDRPOSTagger.py train PATH-TO-LEXICON PATH-TO-DIRECTORY GOLDEN-CORPUS MODEL-NAME'
    print '\nExample: python VnRDRPOSTagger.py train ../Sample/Vn/shortDict ../Sample/Vn correctTrain postagging.rdr'
    print '\nTo tag a Vietnamese raw word-segmented corpus using the trained tagger:'
    print '\npython VnRDRPOSTagger.py tag PATH-TO-TRAINED-MODEL PATH-TO-LEXICON PATH-TO-RAW-CORPUS'
    print '\nExample: python VnRDRPOSTagger.py tag ../Sample/Vn/T3-2/postagging.rdr ../Sample/Vn/fullDict ../Sample/Vn/rawTest'
    print '\nHaving a look in section 2 at http://rdrpostagger.sourceforge.net to find more information!!!'
    
def runVnRDRPOSTagger(args = sys.argv[1:]):
    if (len(args) == 0):
        printInstructions()
    elif args[0].lower().find("train") > -1:
        VnTraining()
    elif args[0].lower().find("tag") > -1:
        r = VnRDRTree()
        r.constructTreeFromRulesFile(args[1])
        DICT = readDictionary(args[2])
        r.tagRawCorpus(DICT, args[3])
    else:
        printInstructions()
        
if __name__ == "__main__":
    runVnRDRPOSTagger()
    pass
