package org.pullxmldemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class NetworkActivity extends Activity {

	public static final String WIFI = "Wi-Fi";

	public static final String ANY = "Any";

	private static final String URL = "http://stackoverflow.com/feeds/tag?tagnames=android&sort=newest";

	// Whether there is a Wi-Fi connection.
	private static boolean wifiConnected = false;
	// Whether there is a mobile connection.
	private static boolean mobileConnected = false;
	// Whether the display should be refreshed.
	public static boolean refreshDisplay = true;
	public static String sPref = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadPage();
	}

	public void loadPage() {
		new DownloadXmlTask().execute(URL);
	}

	private class DownloadXmlTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return loadXmlFromNetwork(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}

	private String loadXmlFromNetwork(String urlString) {
		InputStream stream = null;
		// Instantiate the parser
		StackOverflowXmlParser stackOverflowXmlParser = new StackOverflowXmlParser();
		List<Entry> entries = null;
		String title = null;
		String url = null;
		String summary = null;
		Calendar rightNow = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");
		
		// Checks whether the user set the preference to include summary text
	    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	    boolean pref = sharedPrefs.getBoolean("summaryPref", false);
	        
	    StringBuilder htmlString = new StringBuilder();
	    htmlString.append("<h3>" + "page title" + "</h3>");
	    htmlString.append("<em>" +  " update " + 
	            formatter.format(rightNow.getTime()) + "</em>");
	    try {
	        stream = downloadUrl(urlString);        
	        entries = stackOverflowXmlParser.parse(stream);
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        if (stream != null) {
	            try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } 
	     }
	    for (Entry entry : entries) {       
	        htmlString.append("<p><a href='");
	        htmlString.append(entry.link);
	        htmlString.append("'>" + entry.title + "</a></p>");
	        // If the user set the preference to include summary text,
	        // adds it to the display.
	        if (pref) {
	            htmlString.append(entry.summary);
	        }
	    }
	    return htmlString.toString();
	}
	
	private InputStream downloadUrl(String urlString) throws IOException {
	    URL url = new URL(urlString);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setReadTimeout(10000 /* milliseconds */);
	    conn.setConnectTimeout(15000 /* milliseconds */);
	    conn.setRequestMethod("GET");
	    conn.setDoInput(true);
	    // Starts the query
	    conn.connect();
	    return conn.getInputStream();      
	}
}
