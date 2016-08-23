package com.fjmt.app.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fjmt.app.util.ChieRunnable;
import com.fjmt.app.util.JSoupUtil;
import com.fjmt.app.util.OshieteRunnable;

public class OshieteMain {

	// private static List<QABean> qaBeanList = new ArrayList<>();

	public static void main(String[] args) throws IOException {

		Map<String, String> chieMap = new HashMap<String, String>();
		String host = "http://oshiete.goo.ne.jp";
		Document doc = JSoupUtil.getDocument("http://oshiete.goo.ne.jp/category/list/");
		Elements elems = doc.select("#category_list");
		for (Element elem : elems) {
			Elements aTags = elem.select("a");
			for (Element aTag : aTags) {
				String linkUrl = aTag.attr("href");
				if (linkUrl.indexOf("//oshiete.goo.ne.jp") != -1) {
					linkUrl = linkUrl.replace("//oshiete.goo.ne.jp", "");
				}
				String name = aTag.text().replaceAll(" ", "");
				// System.out.println(name + ":" + host + linkUrl);
				chieMap.put(name, host + linkUrl);
			}
		}

		int counter = 1;
		for (Map.Entry<String, String> entry : chieMap.entrySet()) {
			OshieteRunnable runner = new OshieteRunnable(entry.getValue(), entry.getKey(),
					"/home/dev10635gce001/chieatume/question_" + entry.getKey() + ".txt",
					"/home/dev10635gce001/chieatume/answer_" + entry.getKey() + ".txt");
//			 "chieatume/question_" + entry.getKey() + ".txt",
//			 "chieatume/answer_" + entry.getKey() + ".txt");
			try {
				runner.parseDocument();
			} catch (RuntimeException e) {
				System.out.println(entry.getKey() + " でエラー");
				e.printStackTrace();
				break;
			} catch (Exception e) {
				System.out.println(entry.getKey() + " でエラー");
				e.printStackTrace();
				System.out.println();
			}
			counter++;
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
//			break;
		}
	}

}