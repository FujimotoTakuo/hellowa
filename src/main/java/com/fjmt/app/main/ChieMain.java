package com.fjmt.app.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fjmt.app.util.ChieRunnable;
import com.fjmt.app.util.JSoupUtil;

public class ChieMain {

	// private static List<QABean> qaBeanList = new ArrayList<>();

	public static void main(String[] args) throws IOException {

		Map<String, String> chieMap = new HashMap<String, String>();

		Document doc = JSoupUtil.getDocument("http://chiebukuro.yahoo.co.jp/dir/dir_list.php");
		Elements elems = doc.select(".cat-txt-block");
		for (Element elem : elems) {
			Elements aTags = elem.select("a");
			for (Element aTag : aTags) {
				String linkUrl = aTag.attr("href") + "/list/solved";
				String name = aTag.text();
				System.out.println(name + " : " + linkUrl);
				chieMap.put(name, linkUrl);
			}
		}

		// if (true) {
		// return;
		// }

		// {
		// /**
		// *
		// */
		// private static final long serialVersionUID = 1L;
		//
		// {
		// put("biz", "http://chiebukuro.yahoo.co.jp/dir/list/d2080401239");
		// put("rakugo", "http://chiebukuro.yahoo.co.jp/dir/list/d2079495797");
		// put("kabuki", "http://chiebukuro.yahoo.co.jp/dir/list/d2079495733");
		// put("manner", "http://chiebukuro.yahoo.co.jp/dir/list/d2080401823");
		// put("dousui", "http://chiebukuro.yahoo.co.jp/dir/list/d2079943698");
		// put("gurume", "http://chiebukuro.yahoo.co.jp/dir/list/d2080401703");
		// put("oogiri", "http://chiebukuro.yahoo.co.jp/dir/list/d2080375481");
		// put("sugaku", "http://chiebukuro.yahoo.co.jp/dir/list/d2080401652");
		// put("shigoto", "http://chiebukuro.yahoo.co.jp/dir/list/d2079640005");
		// put("icloud", "http://chiebukuro.yahoo.co.jp/dir/list/d2080401403");
		// put("youtube", "http://chiebukuro.yahoo.co.jp/dir/list/d2080401382");
		// put("virus", "http://chiebukuro.yahoo.co.jp/dir/list/d2080401313");
		// put("inful", "http://chiebukuro.yahoo.co.jp/dir/list/d2079761879");
		// put("juken", "http://chiebukuro.yahoo.co.jp/dir/list/d2079526999");
		// put("iphone", "http://chiebukuro.yahoo.co.jp/dir/list/d2080401478");
		// put("mvno", "http://chiebukuro.yahoo.co.jp/dir/list/d2080401496");
		// put("mainte", "http://chiebukuro.yahoo.co.jp/dir/list/d2078297778");
		// put("camp", "http://chiebukuro.yahoo.co.jp/dir/list/d2078297761");
		//
		// }
		// };
		int counter = 1;
		for (Map.Entry<String, String> entry : chieMap.entrySet()) {
			if (counter % 20 == 0) {
				try {
					// 10min遅延
					Thread.sleep(600000);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			ChieRunnable runner = new ChieRunnable(entry.getValue(), entry.getKey(),
					"/home/dev10635gce004/chieatume/question_" + entry.getKey() + ".txt",
					"/home/dev10635gce004/chieatume/answer_" + entry.getKey() + ".txt");
			runner.parseDocument("http://chiebukuro.yahoo.co.jp/dir/list/d2080375479/list/solved",
					"question_" + entry.getKey() + ".txt", "answer_" + entry.getKey() + ".txt");
			Thread th = new Thread(runner);
			th.start();
			counter++;
			break;
		}

		// parseDocument("http://chiebukuro.yahoo.co.jp/dir/list/d2079048294/list/solved",
		// "/home/dev10635gce001/tensorflow/chat-master/datas/question_neko.txt",
		// "/home/dev10635gce001/tensorflow/chat-master/datas/answer_neko.txt");
		// parseDocument("http://chiebukuro.yahoo.co.jp/dir/list/d2079048294/list/solved",
		// "/home/dev10635gce001/tensorflow/chat-master/datas/question_neko.txt",
		// "/home/dev10635gce001/tensorflow/chat-master/datas/answer_neko.txt");
		// String topPageUrl =
		// "http://chiebukuro.yahoo.co.jp/dir/list.php?did=2079048294&flg=1&sort=16&type=list&page=31";

	}

}