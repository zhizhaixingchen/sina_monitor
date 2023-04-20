package com.sina.util;
import com.sina.pojo.SinaComments;
import com.sina.pojo.SingleBlog;
import com.sina.pojo.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class GetCommentById {
	public static void main(String[] args) {
//		String access_token = "2.00UQIkHH0zGJcI593013b7fcBEkAWE";
//		String id = "4616719383658859";
//		Comments cm = new Comments(access_token);
//		try {
//			CommentWapper comments = cm.getCommentById(id);
//			System.out.println(comments);
//		} catch (WeiboException e) {
//			e.printStackTrace();
//		}

		//System.out.println("\\u234");
		blogdetail();
		//getComments();
	}

	//详细微博
	private static void blogdetail() {
		String target = "https://weibo.com/2803301701/K70WKflPR?wvr=6";
		try {
			URL url = new URL(target);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConn.setRequestProperty("Charset", "utf-8");
			urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
			urlConn.setRequestProperty("Accept", "*/*");
			urlConn.setRequestProperty("Accept-Language", "zh-cn");
			urlConn.setRequestProperty("cookie","SCF=Au2eJQkyRy3oY5E_toowetXk7pnv7v9F1NnzAlpOcwPzAUrw8BqNpHS_aRlQnPJJWME6mjd_93QFlgRdxZW6q9A.; UOR=souky.eol.cn,widget.weibo.com,cn.bing.com; SINAGLOBAL=8209711189153.645.1581563099315; ULV=1616203893344:92:80:41:2501337776855.4634.1616203893341:1616146484708; webim_unReadCount=%7B%22time%22%3A1616147634920%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A0%2C%22msgbox%22%3A0%7D; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9W5ME1Qa7AH5CbcsV.zi1T_N; ALF=1647682480; SUB=_2AkMXCBSof8NxqwFRmPgcyWLiZYR1zQvEieKhVOVzJRMxHRl-yT9kqk87tRB6PIg6R2wzUr862dWmDkZM5YEb5M_vD5kM; login_sid_t=3ee402677939fc8f0559de10e2ffe109; cross_origin_proto=SSL; _s_tentry=cn.bing.com; Apache=2501337776855.4634.1616203893341; wb_view_log=1536*8641.25; WBtopGlobal_register_version=2021032010");
			if(urlConn.getResponseCode() == 200){
				//获取输入流并读取内容
				InputStream inputStream = urlConn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
				String line = "";
				String word = "";
				while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
					line = line.replace("\\r","");
					line = line.replace("\\n","");
					line = line.replace("\\t","");
					line = line.replace("\\","");
					line = line.replace(">n",">");
					word+=line;
				}
				br.close();
				System.out.println(word);
				//贪婪模式  * + {n,} 默认情况是贪婪模式匹配
				//非贪婪模式，?跟在 * + {n,} 等的后面时，表示非贪婪模式，注意和子表达式后面的?区分开，子表达式后的?表示匹配0次或1次
				String t = "(<div class=\"WB_text W_f14\" .*?</div>)";
				Pattern p = Pattern.compile(t);
				Matcher matcher = p.matcher(word);
				String rawDiv = "";
				if(matcher.find()){
					rawDiv = matcher.group(1);
				}
				Document parse = Jsoup.parse(rawDiv);
				//获取微博username
				String username = parse.attr("nick-name");
				//获取内容
				String context = parse.wholeText();
				System.out.println(context.replace(" ",""));


				String t2 = "(<ul class=\"WB_row_line WB_row_r4 clearfix S_line2\">.*?</ul>)";
				Pattern p2 = Pattern.compile(t2);
				Matcher m2 = p2.matcher(word);
				String raw2 = "";
				if(m2.find()){
					raw2 = m2.group(1);
				}
				Document ul = Jsoup.parse(raw2);
				Element e_transfer_before = ul.getElementsByClass("W_ficon ficon_forward S_ficon").get(0);
				Element e_transfer = e_transfer_before.nextElementSibling();
				String transfer = e_transfer.text();
				System.out.println("转发 = "+transfer);

				Element e_comment_before = ul.getElementsByClass("W_ficon ficon_repeat S_ficon").get(0);
				Element e_comment = e_comment_before.nextElementSibling();
				String comment = e_comment.text();
				System.out.println("评论 = "+comment);

				Element e_like_before = ul.getElementsByClass("W_ficon ficon_praised S_txt2").get(0);
				Element e_like = e_like_before.nextElementSibling();
				String like = e_like.text();

				//获取头像
				String t3 = "<img alt=\"\" src=(\".*?\")";
				Pattern p3 = Pattern.compile(t3);
				Matcher m3 = p3.matcher(word);
				String imgUrl = "";
				if(m3.find()){
					imgUrl = m3.group(1);
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	//该微博对应评论
	//https://weibo.com/aj/v6/comment/small?mid=4613885942831048
	private static void getComments(){
		List<User> list = new ArrayList<>();
		String target = "https://weibo.com/aj/v6/comment/small?mid=4616719383658859";
		try {
			URL url = new URL(target);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConn.setRequestProperty("Charset", "utf-8");
			urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
			urlConn.setRequestProperty("Accept", "*/*");
			urlConn.setRequestProperty("Accept-Language", "zh-cn");
			urlConn.setRequestProperty("cookie","SCF=Au2eJQkyRy3oY5E_toowetXk7pnv7v9F1NnzAlpOcwPzAUrw8BqNpHS_aRlQnPJJWME6mjd_93QFlgRdxZW6q9A.; UOR=souky.eol.cn,widget.weibo.com,cn.bing.com; SINAGLOBAL=8209711189153.645.1581563099315; ULV=1616203893344:92:80:41:2501337776855.4634.1616203893341:1616146484708; webim_unReadCount=%7B%22time%22%3A1616147634920%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A0%2C%22msgbox%22%3A0%7D; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9W5ME1Qa7AH5CbcsV.zi1T_N; ALF=1647682480; SUB=_2AkMXCBSof8NxqwFRmPgcyWLiZYR1zQvEieKhVOVzJRMxHRl-yT9kqk87tRB6PIg6R2wzUr862dWmDkZM5YEb5M_vD5kM; login_sid_t=3ee402677939fc8f0559de10e2ffe109; cross_origin_proto=SSL; _s_tentry=cn.bing.com; Apache=2501337776855.4634.1616203893341; wb_view_log=1536*8641.25; WBtopGlobal_register_version=2021032010");
			if(urlConn.getResponseCode() == 200) {
				//获取输入流并读取内容
				InputStream inputStream = urlConn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
				String line = "";
				String word = "";
				while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
					line = line.replace("\\r", "");
					line = line.replace("\\n", "");
					line = line.replace("\\t", "");
					line = line.replace("\\\"", "\"");
					line = line.replace("\\/", "/");
					line = line.replace(">n", ">");
					word += line;
				}
				br.close();
				//System.out.println(word);
				String tt = "\"html\":\"(.*),\"count\":";
				Pattern pattern = Pattern.compile(tt);
				Matcher matcher = pattern.matcher(word);
				String rawCommit = "";
				if (matcher.find()){
					String group = matcher.group(1);
					rawCommit = ConvertUtil.unicodeToString(group);
				}
				Document total = Jsoup.parse(rawCommit);
				Elements comments = total.getElementsByAttributeValue("node-type", "root_comment");
				for (Element singleComments : comments) {
					Element imgDoc = singleComments.getElementsByClass("WB_face W_fl").get(0) ;
					Element img = imgDoc.getElementsByTag("img").get(0);
					String username = img.attr("alt");
					String imgUrl = img.attr("src");
					String uid = img.attr("usercard").substring(3);
					String host = "https:"+imgDoc.child(0).attr("href");

					//内容
					Element e_content = singleComments.getElementsByClass("WB_text").get(0);
					String content = e_content.text().split("^.*?：")[1];

					//点赞数
					Element e_like_before = singleComments.getElementsByClass("W_ficon ficon_praised S_txt2").get(0);
					Element e_like = e_like_before.nextElementSibling();
					String like = e_like.text();

					//时间
					Element e_time = singleComments.getElementsByClass("WB_from S_txt2").get(0);
					String time = e_time.text();
//
					SinaComments sinaComments = new SinaComments();
					sinaComments.setPub_time(time);
					sinaComments.setContext(content);
					sinaComments.setLike(Long.parseLong(like));
					User user = new User(Long.parseLong(uid),username,imgUrl,host,null,null,null,null);

					sinaComments.setUser(user);
					list.add(user);
					System.out.println(user);
				}


			}
		}catch (IOException e) {
			e.printStackTrace();
		}

	}

}
