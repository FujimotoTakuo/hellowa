package com.fjmt.app.main;

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

public class ChieMain {

	private static List<QABean> qaBeanList = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		parseDocument("http://chiebukuro.yahoo.co.jp/dir/list/d2078297658/list/solved",
				"~/tensorflow/chat-master/datas/question_java.txt",
				"~/tensorflow/chat-master/datas/answer_java.txt");
		//parseDocument("http://chiebukuro.yahoo.co.jp/dir/list/d2079048294/list/solved",
		//		"/home/wonusp/tensorflow/chat-master/datas/question_neko.txt",
		//		"/home/wonusp/tensorflow/chat-master/datas/answer_neko.txt");
		// String topPageUrl =
		// "http://chiebukuro.yahoo.co.jp/dir/list.php?did=2079048294&flg=1&sort=16&type=list&page=31";

	}

	private static void parseDocument(String topPageUrl, String inFileName, String outFileName) throws IOException {
		try {
			searchQuestion(topPageUrl);
		} finally {
			File qFile = new File(inFileName);
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(qFile));
			File aFile = new File(outFileName);
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(aFile));
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

	private static void searchQuestion(String topPageUrl) throws IOException {
		// cate innshokuten
		Document document;
		try {
			document = getDocument(topPageUrl);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		Elements nextPageDiv = document.select("#yschpg");
		if (nextPageDiv == null || nextPageDiv.select("p > span") == null || nextPageDiv.select("p > span").size() < 1) {
			return;
		}
		Elements cullentPageSpan = nextPageDiv.select("p > span");
		String currentPage = cullentPageSpan.get(0).text();
		System.out.println("currentPage : " + currentPage + " / question loading start");

		// qalst ultag
		Elements elements = document.select("#qalst");
		// a tag を持ってくる これが、質問へのリンク
		Elements aTags = elements.select("a");
		for (Element aTag : aTags) {
			String qaUrl = aTag.attr("href");
			if (qaUrl.indexOf("http") == -1) {
				continue;
			}
			System.out.print(".");
			// System.out.println(qaUrl);
			parseQuestion(qaUrl);
			// System.out.println();
		}

		if (100 < Integer.parseInt(currentPage)) {
			return;
		}
		System.out.println();
		Elements nextPageLink = document.select("#yschnxtb");
		if (nextPageLink == null || nextPageLink.select("a") == null || nextPageLink.select("a").size() < 1) {
			return;
		}
		String url = nextPageLink.select("a").get(0).attr("href");
		// System.out.println("nextPage : " + url);
		searchQuestion(url);
	}

	private static void parseQuestion(String url) throws IOException {
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

	private static Document getDocument(String url) throws ClassNotFoundException {
		for (int i = 0; i < 100; i++) {

			try {
				Document document = Jsoup.connect(url).get();
				return document;
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				System.out.println("timeout ");
			} catch (HttpStatusException e) {
				throw new ClassNotFoundException();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("接続失敗");
	}

}
