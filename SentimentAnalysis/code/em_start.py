import tensorflow as tf
import jieba
import numpy as np
from gensim.models.word2vec import Word2Vec
from gensim.corpora.dictionary import Dictionary

maxlen = 100

def create_dictionaries(words):
    model = Word2Vec.load('..\\model_new\\Word2vec_model.pkl')
    if (words is not None) and (model is not None):
        gensim_dict = Dictionary()
        gensim_dict.doc2bow(model.wv.vocab.keys(),allow_update=True)

        w2index = {v: k+1 for k, v in gensim_dict.items()}
        #w2vec = {word: model[word] for word in w2index.keys()}
        return w2index
    else:
        print('出错啦')

#查询text中的词向量索引，并进行一一对应
def get_word_vec(text,w2index):
    # text中包含分词好的词语列表
    # sentence为单句话的词语列表
    # word为单个词
    word_vec = []
    for sentence in text:
        new_txt = []
        for word in sentence:
            # 在这里使用try_catch的原因，在word2vec中词频小于10的单词最初没有被添加
            try:
                new_txt.append(w2index[word])
            except:
                new_txt.append(0)
        word_vec.append(new_txt)

    #解决每一个句子分词长短不一致问题
    #为了实现的简便，keras只能接受长度相同的序列输入。因此如果目前序列长度参差不齐，这时需要使用pad_sequences()。该函数是将序列转化为经过填充以后的一个长度相同的新序列新序列。
    word_vec = tf.keras.preprocessing.sequence.pad_sequences(word_vec, maxlen=maxlen)
    return word_vec

def getModel():
    emotion_model = tf.keras.models.load_model('..\\model_new\\emotion.h5')
    return emotion_model

def getMachineJudge(string,model):
    words = jieba.lcut(string)
    words = np.array(words).reshape(1, -1)  # -1表示列自动计算
    w2index = create_dictionaries(words)
    word_vec = get_word_vec(words, w2index).reshape(1, -1)
    result = model.predict(word_vec)
    list = result.tolist()
    result = list[0][1]
    #修正正确率
    f_result = modifyresult(result)
    return f_result

#对result进行适当的修正
#原因 在实际测试中result最小为0.26 最大为0.73 我们需要将对其进行处理
def modifyresult(result):
    f_result = 0.5
    if(result<0.5):
        f_result = (result-0.25)*2;
    else:
        f_result = 0.5+(result-0.5)*2;

    return f_result