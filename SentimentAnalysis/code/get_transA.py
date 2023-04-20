from flask import Flask,request,jsonify
from gevent import pywsgi
import em_start as et
app = Flask(__name__)
model = None
# 为解决端口拥堵，定时任务用A，实时任务用B
@app.route('/judge_emotion',methods=['GET'])
def trans_data():
    string = request.args["sentence"]
    result = et.getMachineJudge(string,model)
    print("sentence = "+string+"\t"+str(result))
    return {"positive":result}

#热点新闻批量获取并返回
@app.route('/judgeEmotionList',methods=['POST'])
def get_list_emotion():
    emotion_result = []
    form_str = request.form['emotion_str']
    list_str = form_str[1:-1].split(",")
    for str1 in list_str:
        result = et.getMachineJudge(str1,model)
        emotion_result.append(result)
        print("sentence = "+str1+"\t"+str(result))
    return jsonify(emotion_result)

if __name__ == '__main__':
    model = et.getModel()
    server = pywsgi.WSGIServer(('0.0.0.0', 5000), app)
    server.serve_forever()
    app.run(threaded=True)
