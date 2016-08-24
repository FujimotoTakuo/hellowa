package com.fjmt.app.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Util {

	public static String parseUrl(String baseUrl, String targetUrl) {
		if ("http".equals(targetUrl.substring(0, 4))) {
			// httpで始まってるので、普通のURL
			return targetUrl;
		}

		if ("//".equals(targetUrl.substring(0, 2))) {
			// 「//」で始まっているので、「http:」をくっつけて返す
			return "http:" + targetUrl;
		}

		if ("/".equals(targetUrl.substring(0, 1))) {
			targetUrl = targetUrl.substring(1);
		}

		if ("./".equals(targetUrl.substring(0, 2))) {
			targetUrl = targetUrl.substring(2);
		}
		// 「/」を元URLに埋め込んでいく
		Pattern p = Pattern.compile("(http://|https://)", Pattern.CASE_INSENSITIVE);
		String baseHostUrl = p.matcher(baseUrl).replaceAll("");
		String[] baseUrlSplitArray = baseHostUrl.split("/");
		String[] targetUrlSplitArray = targetUrl.split("/");

		// レングスの差を調整する
		// aaaa.jp | abc | def | efg.html -> aaaa.jp | abc | def | efg.html
		// aaa | bbb.html -> aaa | bbb.html
		// aaaa.jp | abc -> aaaa.jp | | abc
		// aaa | ccc | bbb.html -> | aaa | ccc | bbb.html ->
		// http://aaaa.jp/aaa/bbb.html
		int loopCounter = ((baseUrlSplitArray.length - 1) > targetUrlSplitArray.length) ? baseUrlSplitArray.length - 1
				: targetUrlSplitArray.length;

		String createUrl = "";
		boolean isFirst = true;

		// 後ろからやってく
		for (int i = 0; i < loopCounter; i++) {
			// http://aaaa.jp/abc/def/efg.html -> length4 aaaa.jp abc def
			// efg.html
			// /aaa/bbb.html -> length2 aaa bbb.html
			// 目標 http://aaaa.jp/abc/aaa/bbb.html
			
			// 後ろからやってく。レングスから1引いたのが一番最後のインデックス。そこから、ループで1個ずつ前のインデックスへ
			boolean isGet = (baseUrlSplitArray.length - 1) - i >= 0;
			String baseSlash = isGet ? baseUrlSplitArray[(baseUrlSplitArray.length - 1) - i] : "";
			isGet = (targetUrlSplitArray.length - 1) - i >= 0;
			String targetSlash = isGet ? targetUrlSplitArray[(targetUrlSplitArray.length - 1) - i] : "";
			String appendStr = StringUtils.isBlank(targetSlash) ? baseSlash : targetSlash;
			// メモリとか、無視
			if (isFirst) {
				createUrl = appendStr;
				isFirst = false;
			} else {
				createUrl = appendStr + "/" + createUrl;
			}
		}
		createUrl = "http://" + baseUrlSplitArray[0] + "/" + createUrl;
		// System.out.println(createUrl);
		return createUrl;
	}

	public static void main(String[] args) {
		parseUrl("http://aaaa.jp/abc/def/efg.html", "/aaa/bbb.html");
		parseUrl("http://aaaa.jp/abc/abc/abc/abc/def/efg.html", "/aaa/vvv/vvv/bbb.html");
		parseUrl("http://aaaa.jp/abc/def/abc/abc/efg.html", "bbb.html");
	}

}
