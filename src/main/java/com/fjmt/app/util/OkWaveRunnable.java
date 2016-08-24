package com.fjmt.app.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fjmt.app.bean.QABean;

public class OkWaveRunnable implements Runnable {

	private String topPageUrl;
	private String catName;
	private String inFileName;
	private String outFileName;
	private List<QABean> qaBeanList = new ArrayList<>();
	private static String HOST = "http://oshiete.goo.ne.jp";

	public OkWaveRunnable(String topPageUrl, String catName, String inFileName, String outFileName) {
		this.topPageUrl = topPageUrl;
		this.catName = catName;
		this.inFileName = inFileName;
		this.outFileName = outFileName;
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		try {
			parseDocument();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void parseDocument() throws IOException {
		File qFile = null;
		BufferedWriter bw1 = null;
		File aFile = null;
		BufferedWriter bw2 = null;
		try {
			qFile = new File(inFileName);
			bw1 = new BufferedWriter(new FileWriter(qFile, true));
			aFile = new File(outFileName);
			bw2 = new BufferedWriter(new FileWriter(aFile, true));
			searchQuestion(topPageUrl);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("\n" + "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
			System.out.println(catName + " Done. " + qaBeanList.size());
			System.out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
			// 結果確認用
			for (QABean bean : qaBeanList) {
				bw1.write(bean.getQuestion());
				bw1.newLine();
				bw2.write(bean.getAnswer());
				bw2.newLine();
			}
			if (bw1 != null) {
				bw1.close();
			}
			if (bw2 != null) {
				bw2.close();
			}
		}

	}

	private void searchQuestion(String topPageUrl) throws IOException {
		// cate innshokuten
		Document document;
		try {
			document = getDocument(topPageUrl);
		} catch (ClassNotFoundException e) {
			return;
		}
		Elements cullentPageSpan = document.select("div#ok_paging > span.blk_q");
		String currentPage = cullentPageSpan.get(0).text();
		System.out.print("catName : " + catName + " / currentPage : " + currentPage + " / question loading start");

		// a tag を持ってくる これが、質問へのリンク
		Elements aTags = document.select("p.qat > a");
		// a tag を持ってくる これが、質問へのリンク
		// Elements aTags = elements.select("a");
		int counter = 1;
		for (Element aTag : aTags) {
			String qaUrl = Util.parseUrl(topPageUrl, aTag.attr("href"));
			System.out.print(".");
			// System.out.println(aTag.text());
			// System.out.println(qaUrl);
			parseQuestion(qaUrl);
			// System.out.println();
			counter++;
			// if (counter > 2) {
			// break;
			// }
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		System.out.println();
		if (40 < Integer.parseInt(currentPage)) {
			return;
		}
		// System.out.println();
		Elements nextPageLink = document.select("div#ok_paging > span.ok_next > a");
		if (nextPageLink == null || nextPageLink.size() == 0 || nextPageLink.get(0) == null
				|| nextPageLink.get(0).attr("href") == null) {
			return;
		}
		String url = Util.parseUrl(topPageUrl, nextPageLink.get(0).attr("href"));
		// System.out.println("nextPage : " + url);
		searchQuestion(url);
	}

	private void parseQuestion(String url) throws IOException {
		Document document;
		try {
			document = getDocument(url);
		} catch (ClassNotFoundException e) {
			// とりあえず、
			return;
		}
		QABean bean = new QABean();
		// 質問側
		Elements quesPElem = document.select("div.q_desc");
		String mainQues = quesPElem.get(0).text();

		// 回答側
		Elements ansTextElems = document.select("div.a_desc");
		if (ansTextElems == null || ansTextElems.size() == 0) {
			// 解凍なし
			return;
		}
		String mainAns = ansTextElems.get(0).text();

		// System.out.println("Question : " + mainQues);
		// System.out.println("Answer : " + mainAns);
		bean.setQuestion(mainQues);
		bean.setAnswer(mainAns);
		qaBeanList.add(bean);
	}

	private Document getDocument(String url) throws ClassNotFoundException {
		Document document = JSoupUtil.getDocument(url);
		return document;
	}

	public String getTopPageUrl() {
		return topPageUrl;
	}

	public void setTopPageUrl(String topPageUrl) {
		this.topPageUrl = topPageUrl;
	}

	public String getInFileName() {
		return inFileName;
	}

	public void setInFileName(String inFileName) {
		this.inFileName = inFileName;
	}

	public String getOutFileName() {
		return outFileName;
	}

	public void setOutFileName(String outFileName) {
		this.outFileName = outFileName;
	}

}
