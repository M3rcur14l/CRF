# -*- coding: utf-8 -*-
import re
from Utility.Utils import readDictionary, isAbbre, isVnProperNoun,  isVnUpperChar

VNUNKNWORDS = readDictionary("../jSCRDRtagger/addDicts/VNOTHERS.DICT")
VNNAMES = readDictionary("../jSCRDRtagger/addDicts/VNNAMES.DICT")

def VnInitTagger4Sentence(VNFREQ, sentence):
    """
    Initial tagger for Vietnamese sentence.
    VNUNKNWORDS and VNNAMES were not utilized in the version as described in our CICLing 2011 paper
    """
    words = sentence.strip().split()
    taggedSen = ''
    for word in words:
        if word in VNFREQ:
            taggedSen += word + "/" + VNFREQ[word] + " "
        elif word in VNUNKNWORDS:
            taggedSen += word + "/" + VNUNKNWORDS[word] + " "
        elif word in VNNAMES:
            taggedSen += word + "/Np "      
        else:         
            if (re.search(r"[0-9]+", word) != None):
                taggedSen += word + "/M "
            elif(len(word) == 1 and isVnUpperChar(word[0])):
                taggedSen += word + "/Y "
            else:
                if (isAbbre(word)):
                    taggedSen += word + "/Ny "
                elif (isVnProperNoun(word)):#
                    taggedSen += word + "/Np "
                else:
                    taggedSen += word + "/N "                                     
    return taggedSen.strip()

def VnInitTagger4Corpus(VNFREQ, input, output):
    """
    Initial tagger for Vietnamese corpus
    """
    
    lines = open(input, "r").readlines()
    fileOut = open(output, "w")
    for line in lines:
        fileOut.write(VnInitTagger4Sentence(VNFREQ, line) + "\n")
    fileOut.close      

if __name__ == "__main__":
    #VNFREQ = readDictionary("../Dicts/VN.DICT")
    #VnInitTagger4Corpus(VNFREQ, "../Sample/Vn/rawTest", "../Sample/Vn/rawTest.INIT")
    pass