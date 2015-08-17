# -*- coding: utf-8 -*-

import os, sys
os.chdir("../")
sys.setrecursionlimit(100000)
sys.path.append(os.path.abspath(""))
os.chdir("./Utility")

from Utility.Utils import getWordTag

def createLexicon(corpusFile, outDictName, fullLexicon):
    """
    Generate a dictionary from a golden corpus 'corpusFile':
    Output is a file consisting of lines in which each of them contains a word and the most frequent associated tag
    corpusFile: path to the golden training corpus
    outDictName: file name of the dictionary/lexicon
    fullLexicon: gets True or False value. If it is False, the output lexicon does not contain 1 time occurrence words
    """
    if fullLexicon not in ['True', 'False']:
        print "the third parameter gets \"True\" or \"False\" string-value!!!"
        return
    
    lines = open(corpusFile, "r").readlines()
    wordTagCounter = {}
    for i in xrange(len(lines)):
        #print i
        pairs = lines[i].strip().split()
        for pair in pairs:
            word, tag = getWordTag(pair)
            
            if word not in wordTagCounter:
                wordTagCounter[word] = {}
                wordTagCounter[word][tag] = 1
            else:
                if tag not in wordTagCounter[word]:
                    wordTagCounter[word][tag] = 1
                else:
                    wordTagCounter[word][tag] += 1
    
    # Get the most frequent tag associated to each word    
    from operator import itemgetter
    dictionary = {}
    tagCounter = {}
    for word in wordTagCounter:
        tagFreqDic = wordTagCounter[word]
        pairs = tagFreqDic.items()
        pairs.sort(key = itemgetter(1), reverse=True)
        mostFreqTag = pairs[0][0]
        
        if fullLexicon == 'True': # Get the full lexicon including 1 time occurrence words
            dictionary[word] = mostFreqTag
        else: # Get the lexicon without 1 time occurrence words
            if (len(pairs) == 1 and  pairs[0][1] > 1) or len(pairs) > 1:
                dictionary[word] = mostFreqTag
        
        if mostFreqTag not in tagCounter:
            tagCounter[mostFreqTag] = 1
        else:
            tagCounter[mostFreqTag] += 1
        
    from collections import OrderedDict
    dictionary = OrderedDict(sorted(dictionary.iteritems(), key=itemgetter(0)))
    
    # Get the most frequent tag in the lexicon to label unknown words
    tagCounter = OrderedDict(sorted(tagCounter.iteritems(), key=itemgetter(1), reverse=True))
    defaultTag = tagCounter.keys()[0]
    
    #Write to file
    fileOut = open(outDictName, "w")
    fileOut.write("DefaultTag " + defaultTag + "\n")
    for key in dictionary:
        fileOut.write(key + " " + dictionary[key] + "\n")
    
    fileOut.close()
    
    return dictionary

if __name__ == "__main__":
    #createLexicon("../Sample/En/correctTrain","../Sample/En/fullDict", 'True')
    #createLexicon("../Sample/En/correctTrain","../Sample/En/shortDict", 'False')
    createLexicon(sys.argv[1:][0], sys.argv[1:][1], sys.argv[1:][2])
    pass