cd /Users/Antonello/CRF/Dataset
/Applications/Praat.app/Contents/MacOS/Praat tokenizer.praat
cd /Users/Antonello/CRF/Dataset/postagger/pSCRDRtagger
python EnRDRPOSTagger.py tag ../Models/English.RDR ../Dicts/English.DICT ../../tokens.txt

