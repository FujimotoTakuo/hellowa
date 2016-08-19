package com.fjmt.app.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fjmt.app.util.JSoupUtil;

public class Main {

	private static List<String> lines = new ArrayList<>();

	public static void main(String[] args) throws IOException {
//		Document doc = JSoupUtil.getDocument("http://yozora.kazumi386.org/9/1/ndck913.html");
		parseDocument(JSoupUtil.getDocument("http://yozora.kazumi386.org/9/1/ndck913.html"),
				"~/tensorflow/chat-master/datas/first.txt",
				"~/tensorflow/chat-master/datas/second.txt");
		//parseDocument(JSoupUtil.getDocument("http://yozora.kazumi386.org/9/1/ndc913.html"),
		//		"~/tensorflow/chat-master/datas/first_all.txt",
		//		"~/tensorflow/chat-master/datas/second_all.txt");
		

	}
	
	private static void parseDocument(Document doc, String inFileName, String outFileName) throws IOException {
		Elements aTags = doc.select("li > a");
		for (Element aTag : aTags) {
			String bookPageTop = aTag.attr("href");
			Document bookDoc = JSoupUtil.getDocument(bookPageTop);
			Elements dlTable = bookDoc.select(".download");
			Elements dlTableTr = dlTable.get(0).select("tr");
			for (Element trElem : dlTableTr) {
				Elements tdElem = trElem.select("td");
				if (tdElem.size() < 1) {
					continue;
				}
				String fileDesc = tdElem.get(0).text();
				if (fileDesc.indexOf("XHTMLファイル") != -1) {
					String bookContentLink = trElem.select("a").get(0).attr("href");
					if (".".equals(bookContentLink.substring(0, 1))) {
						String tempBaseUrl = bookPageTop.substring(0, bookPageTop.lastIndexOf("/"));
						bookContentLink = tempBaseUrl + bookContentLink.substring(1);
						System.out.println(tempBaseUrl);
						System.out.println(bookContentLink);
					}
					System.out.println();
					parseText(bookContentLink);
				}
			}
		}

		File qFile = new File(outFileName);
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(qFile));
		File aFile = new File(inFileName);
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(aFile));
		// 結果出力
		for (String ln : lines) {
			if (StringUtils.isBlank(ln)) {
				continue;
			}
//			System.out.println(ln);
			bw1.write(ln);
	        bw1.newLine();
	        bw2.newLine();
	        bw2.write(ln);
		}
		bw1.close();
		bw2.close();
	}
	
	private static void parseText(String url) throws IOException {

		Document document = JSoupUtil.getDocument(url);
		String text = document.select(".main_text").text();
		String[] textCharas = text.split("");
		StringBuilder line = new StringBuilder();
		boolean isMaru = false;
		boolean isKakko = false;
		boolean isRubi = false;
		for (String str : textCharas) {
			// ブランクは除外
			if (StringUtils.isBlank(str)) {
				continue;
			}

			if (isRubi) {
				if ("）".equals(str)) {
					// ルビ終了 閉じかっこも追加しないのでスキップ
					isRubi = false;
					continue;
				} else {
					// ルビだからスキップ
					continue;
				}
			}

			// 。」のぱたーんがあるので、すぐには改行しない。
			if ("。".equals(str)) {
				line.append(str);
				isMaru = true;
				continue;
			}
			// 閉じかっこは、閉じかっこの後ろで改行する
			if ("」".equals(str)) {
				line.append(str);
				lines.add(line.toString());
				line = new StringBuilder();
				isMaru = false;
				isKakko = false;
				continue;
			}
			// 開きかっこは、開き括弧の前で改行する
			if ("「".equals(str)) {
				lines.add(line.toString());
				line = new StringBuilder();
				line.append(str);
				isMaru = false;
				isKakko = true;
				continue;
			}

			// ルビは消す 閉じかっこまでスキップする
			if ("（".equals(str)) {
				isRubi = true;
				continue;
			}

			// 前の文字が 。 だった場合には、改行してisMaruはfalseに戻す。
			if (isMaru && !isKakko) {
				lines.add(line.toString());
				line = new StringBuilder();
				isMaru = false;
			}
			// 文字追加
			line.append(str);

		}
		lines.add("--------------------------------");
		
	}
}
