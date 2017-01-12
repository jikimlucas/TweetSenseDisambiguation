# Welcome to Tweet Sense Disambiguation  experiment

How we can distigush country Jordan vs Nike Joran or person Jordan.
Pretraining models are from : https://github.com/stanfordnlp/GloVe
NLP Reference : http://nlp.stanford.edu/software/CRF-NER.shtml

You can get glove_final.txt from Pretraining models are from : https://drive.google.com/open?id=0B-zTqtKyBIISb094S0JDWURjWTg


# How it works:
1. Based on pre-trained tweet model, we will create 2 seperate centroids. Pre-trained word vectors : Twitter (2B tweets, 27B tokens, 1.2M vocab, uncased, 200d vectors, 1.42 GB)
https://github.com/stanfordnlp/GloVe
2. Get text from tweet via UI
3. Tokenize and, ask centroids for scores for country vs person.
4. Based on scores, we know, it is about country vs person.

# Frameworks:
Spring boot + Freemarker + ND4J + DL4J

# Machine Learning - Word2Vec
maps words to a vector while preserving meaningful relationships between the words. Words which are similar will end up being close to each other in the map.

