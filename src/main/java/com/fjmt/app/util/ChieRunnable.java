package com.fjmt.app.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fjmt.app.bean.QABean;

public class ChieRunnable implements Runnable {

	private String topPageUrl;
	private String catName;
	private String inFileName;
	private String outFileName;
	private List<QABean> qaBeanList = new ArrayList<>();

	public ChieRunnable(String topPageUrl, String catName, String inFileName, String outFileName) {
		this.topPageUrl = topPageUrl;
		this.catName = catName;
		this.inFileName = inFileName;
		this.outFileName = outFileName;
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		try {
			parseDocument(topPageUrl, inFileName, outFileName);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void parseDocument(String topPageUrl, String inFileName, String outFileName) throws IOException {
		File qFile = null;
		BufferedWriter bw1 = null;
		File aFile = null;
		BufferedWriter bw2 = null;
		try {
			qFile = new File(inFileName);
			bw1 = new BufferedWriter(new FileWriter(qFile));
			aFile = new File(outFileName);
			bw2 = new BufferedWriter(new FileWriter(aFile));
			searchQuestion(topPageUrl);

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
			bw1.close();
			bw2.close();
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

		Elements nextPageDiv = document.select("#yschpg");
		if (nextPageDiv == null || nextPageDiv.select("p > span") == null
				|| nextPageDiv.select("p > span").size() < 1) {
			return;
		}
		Elements cullentPageSpan = nextPageDiv.select("p > span");
		String currentPage = cullentPageSpan.get(0).text();
		System.out.println("catName : " + catName + " / currentPage : " + currentPage + " / question loading start");

		// qalst ultag
		Elements elements = document.select("#qalst");
		// a tag を持ってくる これが、質問へのリンク
		Elements aTags = elements.select("a");
		for (Element aTag : aTags) {
			String qaUrl = aTag.attr("href");
			if (qaUrl.indexOf("http") == -1) {
				continue;
			}
//			System.out.print(".");
			// System.out.println(qaUrl);
			parseQuestion(qaUrl);
			// System.out.println();
		}

		if (0 < Integer.parseInt(currentPage)) {
			return;
		}
//		System.out.println();
		Elements nextPageLink = document.select("#yschnxtb");
		if (nextPageLink == null || nextPageLink.select("a") == null || nextPageLink.select("a").size() < 1) {
			return;
		}
		String url = nextPageLink.select("a").get(0).attr("href");
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
		Elements elements = document.select(".ptsQes");
		if (elements.size() < 2) {
			// System.out.println("回答なし");
			return;
		}
		QABean bean = new QABean();
		Element quesElement = elements.get(0);
		Element ansElement = elements.get(1);
		// 質問側
		Elements quesPElem = quesElement.select("p");
		String title = quesPElem.get(0).text();
		Elements textElems = quesPElem.select(".queTxt");
		String mainQues = textElems != null ? textElems.text() : "";

		// 回答側
		Elements ansTextElems = ansElement.select(".queTxt");
		String mainAns = ansTextElems.text();

		// System.out.println("Question : " + title + mainQues);
		// System.out.println("Answer : " + mainAns);
		bean.setQuestion(title + mainQues);
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
