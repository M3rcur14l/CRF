# daCRF
Developer: Antonello Fodde

daCRF is a Natural language processing program. The name “daCSF” stands for “Dialogue Act Classifier”. By starting from a dataset of file audio, which contains recorded dialogues tagged following DAMLS dialogue act tag set, it extracts from them several characteristics. Using these characteristics, which is called “features”, it will create a model able to classify dialogue acts relying only on those extracted features. Using a cross-validation approach, it will be able to compute the accuracy of classification.