package com.fjmt.app.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fjmt.app.util.JSoupUtil;
import com.fjmt.app.util.OkWaveRunnable;
import com.fjmt.app.util.OshieteRunnable;
import com.fjmt.app.util.Util;

public class OkWaveMain {

	// private static List<QABean> qaBeanList = new ArrayList<>();
	private static List<String> excludeList = new ArrayList<String>(){
		{
			add("このQ&Aコミュニティーについて");
			add("特別企画");
			add("おしゃべり広場");
			add("みんなで話そう！");
			add("年代別広場");
			add("10代");
			add("20代");
			add("30代");
			add("40代");
			add("50代");
			add("60代以上");
			add("学生広場");
			add("非モテ広場");
			add("子育て広場");
			add("企業公式");
			add("専門家に質問！教えてレーシック");
			add("大規模災害");
			add("東日本大震災情報");
			add("医療相談");
			add("安否確認：北海道");
			add("安否確認：青森");
			add("安否確認：岩手");
			add("安否確認：宮城");
			add("安否確認：秋田");
			add("安否確認：山形");
			add("安否確認：福島");
			add("安否確認：茨城");
			add("安否確認：栃木");
			add("安否確認：群馬");
			add("安否確認：千葉");
			add("安否確認：新潟");
			add("安否確認：長野");
			add("安否確認：その他の地域");
			add("物資・支援情報：北海道");
			add("物資・支援情報：青森");
			add("物資・支援情報：岩手");
			add("物資・支援情報：宮城");
			add("物資・支援情報：秋田");
			add("物資・支援情報：山形");
			add("物資・支援情報：福島");
			add("物資・支援情報：茨城");
			add("物資・支援情報：栃木");
			add("物資・支援情報：群馬");
			add("物資・支援情報：千葉");
			add("物資・支援情報：新潟");
			add("物資・支援情報：長野");
			add("物資・支援情報：その他の地域");
			add("交通・運行情報");
			add("応援メッセージ");
			add("地震の知識");
			add("災害対策");
			add("避難所のくらし・工夫");
			add("節電");
			add("募金・ボランティア・物資支援");
			add("妊婦・出産・子供の相談");
			add("外国の方からの相談");
			add("その他（東日本大震災)");
		}
	};

	public static void main(String[] args) throws IOException {

		Map<String, String> chieMap = new HashMap<String, String>();
//		String host = "http://oshiete.goo.ne.jp";
		Document doc = JSoupUtil.getDocument("http://okwave.jp/category");
		Elements elems = doc.select(".ok_cs_category");
		for (Element elem : elems) {
			Elements aTags = elem.select("a");
			for (Element aTag : aTags) {
				if (excludeList.contains(aTag.text()) || StringUtils.isBlank(aTag.attr("href"))) {
					continue;
				}
				String linkUrl = Util.parseUrl("http://okwave.jp/category", aTag.attr("href"));
				String name = aTag.text().replaceAll(" ", "");
				 System.out.println(name + ":" + linkUrl);
				chieMap.put(name, linkUrl);
			}
		}

		int counter = 1;
		for (Map.Entry<String, String> entry : chieMap.entrySet()) {
			OkWaveRunnable runner = new OkWaveRunnable(entry.getValue(), entry.getKey(),
					"/home/dev10635gce001/chieatume/okwave/question_" + entry.getKey() + ".txt",
					"/home/dev10635gce001/chieatume/okwave/answer_" + entry.getKey() + ".txt");
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
//			counter++;
//			if (counter == 3) {
//				break;
//			}
//			try {
//				Thread.sleep(60000);
//			} catch (InterruptedException e) {
//				// TODO 自動生成された catch ブロック
//				e.printStackTrace();
//			}
//			break;
		}
	}

}