package com.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public class Download {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Download.download();

	}

	private static void download() {
		String path = "http://central.maven.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar";
		InputStream is = null;
		OutputStream os = null;
		try {
			URL url = new URL(path);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(10000);
			connection.setRequestMethod("GET");
			// connection.connect();
			System.out.println(connection.getResponseCode());
			System.out.println(connection.getResponseMessage());
			System.out.println(connection.getContentLength());
			System.out.println(connection.getContentType());
			is =  connection.getInputStream();
			String[] str=path.split("/");
			String name=str[str.length-1];
			os = new FileOutputStream("D://Download//"+name);
//			byte[] bytes = new byte[1024];
//			int len = 0;
//			while ((len = is.read(bytes)) > -1) {
//				os.write(bytes, 0, len);
//			}
			IOUtils.copy(is, os);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}
	}

}
