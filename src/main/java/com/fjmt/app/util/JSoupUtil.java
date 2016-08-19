package com.fjmt.app.util;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JSoupUtil {


	public static Document getDocument(String url) {
		for (int i = 0; i < 100; i++) {

			try {
				Document document = Jsoup.connect(url).get();
				return document;
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				System.out.println("timeout ");
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
