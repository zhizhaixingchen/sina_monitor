import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
import jieba
from gensim.models.word2vec import Word2Vec
from gensim.corpora.dictionary import Dictionary
import tensorflow as tf
import re

#常量规定
vocab_dim = 150     #每个词向量维数
n_iterations = 1    #ideally more..
n_exposures = 5    #所有频数超过5的词语
window_size = 7
n_epoch = 15         #规定训练次数
input_length = 150
maxlen = 150        #单句子最多词数量
batch_size=32

#加载积极消极中立词库
def loadDataFromLocal():
    #情感语料库
    combined_emotion,y_emotion = get_data_emotion()
    #评论语料库
    combined_comments,y_comments = get_data_comments()

    #语料库合并
    combined_total = np.append(combined_emotion,combined_comments)
    y_total = np.append(y_emotion,y_comments)
    return combined_total,y_total

#评论数据
def get_data_comments():
    neg = pd.read_csv('/data0/dhk/pycharm_code/SentimentAnalysis/data/new_neg.csv', header=None, index_col=None, error_bad_lines=False)
    pos = pd.read_csv('/data0/dhk/pycharm_code/SentimentAnalysis/data/new_pos.csv', header=None, index_col=None, error_bad_lines=False)
    combined_comments = np.concatenate((pos[0],neg[0]))
    #积极为1 消极为0
    y_comments = np.concatenate((np.ones(len(pos), dtype=int), np.zeros(len(neg), dtype=int)))
    return combined_comments,y_comments


#情感方面
def get_data_emotion():
    #存储句子
    sentence = []
    #存储情感
    state = []
    re_c = '^\d*,([0|1]),(.*)'
    p = re.compile(re_c)
    with open('/data0/dhk/pycharm_code/SentimentAnalysis/data/train.txt', 'rt', encoding='utf-8') as f2:
        while 1:
            line = f2.readline()
            if not line:
                break
            content = p.findall(line)
            if len(content[0]) == 2:
                state.append(content[0][0])
                sentence.append(content[0][1])

    #将所有数据存储在一个tuple中
    combined_emotion = np.array(sentence).flatten()
    #创建对应的数字标识
    #0表示负面, 1表示正面
    y_emotion = np.array(state).flatten()
    return combined_emotion,y_emotion

#使用结巴分词对第一步加载的语句进行分词
def get_word_from_combined(combined):
    text = []
    for i in combined:
        # 去除换行符号
        i.replace('\n', '')
        # 进行分词
        # 去除标点符号
        # punc = '[\s+\.\!\/_,$%^*(+\"\')]+|[+——()?【】“”！，。？、~@#￥%……&*（）：;；:《》\\\<>]+'
        # i = re.sub(punc,'',i)
        result = jieba.lcut(i)
        # 分词存储
        text.append(result)
    return text

#创建词语字典，并返回每个词语的索引，词向量，以及每个句子所对应的词语索引
def getWordDic(combined,text):
    #计算分词总数目
    token_count = sum([len(sentence) for sentence in combined])
    #对分词结果进行输出
    model = Word2Vec(vector_size=vocab_dim,
                     min_count=n_exposures,
                     window=window_size)
    model.build_vocab(text)  # input: list
    model.train(text, total_examples=token_count, epochs=model.epochs)
    #对word2vec分词模型进行保存
    model.save('/data0/dhk/pycharm_code/SentimentAnalysis/model_new/Word2vec_model.pkl')

    #输出单词词频
    gensim_dict = Dictionary()
    gensim_dict.doc2bow(model.wv.index_to_key,allow_update=True)   #去除重复，进行归类

    #gensim_dict  key对应的标号 从1开始     value 词语
    #w2index      key词语                 value 对应的标号  从0开始
    #交换原因 在之后 我们更过是通过词语查询标号。而不是通过标号查询词语
    #combined中进行分词后的词语存在text中，我们根据词语查询到词语指定索引，再根据词语查询出对应的词向量  进行一一对应
    w2index = {v: k + 1 for k, v in gensim_dict.items()}  # 所有频数超过10的词语的索引,(k->v)=>(v->k)

    #查询所有词语的词向量 并存储在w2vec中，格式   word：对应词向量
    w2vec = {word: model.wv[word] for word in w2index.keys()}  # 所有频数超过10的词语的词向量, (word->model(word))
    return w2index,w2vec

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

#训练前的参数获取
'''
训练输入数据
训练输出数据
测试输入数据
测试输出数据
另还有
所有单词所索引数目
词语的词向量。
'''
def train_before_param(word_vec,y,w2index,w2vec):
    x_train, x_test, y_train, y_test = train_test_split(word_vec, y, test_size=0.1)
    n_summary = len(w2index) + 1  # 所有单词的索引数，频数小于10的词语索引为0，所以加1
    # 初始化词向量列表   一共有 单词索引数行   指定向量列
    embedding_weights = np.zeros((n_summary, vocab_dim))

    # 遍历所有的单词，并在w2vec中查询出对应的词向量
    #key词语对应唯一索引   value 词语对应唯一词向量
    for word, index in w2index.items():  # 从索引为1的词语开始，对每个词语对应其词向量
        embedding_weights[index, :] = w2vec[word]

    # 转换为one-hot
    y_train = tf.keras.utils.to_categorical(y_train, num_classes=2)
    y_test = tf.keras.utils.to_categorical(y_test, num_classes=2)

    return x_train,y_train,x_test,y_test,n_summary,embedding_weights

def train(x_train,y_train,n_summary,embedding_weights):
    model = tf.keras.models.Sequential()
    model.add(tf.keras.layers.Embedding(output_dim=vocab_dim,
                                        input_dim=n_summary,
                                        mask_zero=True,
                                        weights=[embedding_weights],
                                        input_length=input_length))  # Adding Input Length
    model.add(tf.keras.layers.LSTM(50, activation='tanh'))
    model.add(tf.keras.layers.Dropout(0.5))
    model.add(tf.keras.layers.Dense(25, activation='softmax'))
    model.add(tf.keras.layers.Dropout(0.3))
    model.add(tf.keras.layers.Dense(2, activation='softmax'))
    model.add(tf.keras.layers.Activation('softmax'))

    model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])
    model_file = '/data0/dhk/pycharm_code/SentimentAnalysis/model_new/emotion.h5'
    # callback = tf.keras.callbacks.ModelCheckpoint(filepath=model_file,
    #                                               save_weights_only=False,
    #                                               verbose=2)
    #save_weights_only 只保存权重 如果只保存权重，那么我们在使用的时候需要定义模型结构
    #verbose = 0 为不在标准输出流输出日志信息
    #verbose = 1 为输出进度条记录
    #verbose = 2 为每个epoch输出一行记录

    model.fit(x_train, y_train,epochs=n_epoch)
    model.evaluate(x_test,y_test,verbose=2)
    model.save(model_file)

if __name__ == '__main__':
    #combined 评论语句
    #y 对应状态(积极消极中立)
    combined,y = loadDataFromLocal()
    #text combined进行jieba分词后的结果
    text = get_word_from_combined(combined)

    #使用word2vec建立高维向量词典
    #w2index key 词语(标点符号A-Z a-z 汉字)  value 索引(1,2,3,4,5)
    #w2vec   key 词语  value 词向量
    w2index,w2vec = getWordDic(combined,text)

    #word_vec 找出text中的每一个词语对应的词向量索引，并存储在word_vec中，并进行长度一致化
    word_vec = get_word_vec(text,w2index)

    #x_train 训练输入数据
    #y_train 训练输出数据
    #x_test 测试输入数据
    #y_test 测试输出数据
    #n_summary 所有单词所索引数目
    #embedding_weights 词语的词向量    key 词语索引 value 词向量
    x_train,y_train,x_test,y_test,n_summary,embedding_weights = train_before_param(word_vec,y,w2index,w2vec)

    train(x_train,y_train,n_summary,embedding_weights)


