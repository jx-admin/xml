package org.pullxmldemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.graphics.Paint.Join;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private String fileName = "river.xml";
	
	private String riverStr = "river";
	
	private String nameStr = "name";
	
	private String lengthStr = "length";
	
	private String introductionStr = "introduction";
	
	private String imageurlStr = "imageurl";
	
	private  TextView tv = null;
	
	private String T = "MainActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.pullInfo);
        Button dom = (Button)findViewById(R.id.dom);
        dom.setOnClickListener(this);
        Button sax = (Button)findViewById(R.id.sax);
        sax.setOnClickListener(this);
        Button pull = (Button)findViewById(R.id.pull);
        pull.setOnClickListener(this);
        
        
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    
    private List<River> fetchRiverFormXmlByPull(String fileName) {
    	long startTime = System.currentTimeMillis();
    	List<River> rivers = new ArrayList<River>();
    	River river = null;
    	InputStream inputStream = null;
    	XmlPullParser xmlPullParser = Xml.newPullParser();
    	try {
    		inputStream = getAssets().open(fileName);
			xmlPullParser.setInput(inputStream, "utf-8");
			xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			//xmlPullParser.nextTag();
			int eventType = xmlPullParser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT) {
				switch(eventType) {
				case XmlPullParser.START_TAG:
					String tag = xmlPullParser.getName();
					if(tag.equalsIgnoreCase(riverStr)) {
						river = new River();
						river.setName(xmlPullParser.getAttributeValue(null, nameStr));
						river.setLength(Integer.parseInt(xmlPullParser.getAttributeValue(null, lengthStr)));
					}else if(river != null) {
						if(tag.equalsIgnoreCase(introductionStr)) {
							river.setIntroduction(xmlPullParser.nextText());
						} else if(tag.equalsIgnoreCase(imageurlStr)) {
							river.setImageurl(xmlPullParser.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if(xmlPullParser.getName().equalsIgnoreCase(riverStr) && river != null) {
						rivers.add(river);
						river = null;
					}
					break;
					default:
						break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	Log.d(T, "exec fetchRiverFormXmlByPull use time = " + (System.currentTimeMillis() - startTime));
    	return rivers;
    }
    
    private List<River> fetchRiverFormXmlBySAX(String fileName) {
    	long startTime = System.currentTimeMillis();
    	List<River> rivers = null;
    	SAXParserFactory factory = SAXParserFactory.newInstance();
    	InputStream inputStream = null;
    	try {
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			MySaxHandler handler = new MySaxHandler();
			reader.setContentHandler(handler);
			inputStream = getAssets().open(fileName);
			reader.parse(new InputSource(inputStream));
			rivers = handler.getRivers();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	Log.d(T, "exec fetchRiverFormXmlBySAX use time = " + (System.currentTimeMillis() - startTime));
    	return rivers;
    }
    
    private class MySaxHandler extends DefaultHandler {
    
    	private List<River> rivers = null;
    	
    	private boolean isRiver = false;
    	
    	private boolean isIntrocduce = false;
    	
    	private boolean isImageUrl = false;
    	
    	private River river = null;
    	
    	private String TAG = "MySaxHandler";
    	
    	public MySaxHandler() {
    		rivers = new ArrayList<River>();
    	}
    	
    	@Override
    	public void startDocument() throws SAXException {
    		super.startDocument();
    		Log.d(TAG, "### startDocument");
    	}
    	
    	@Override
    	public void endDocument() throws SAXException {
    		super.endDocument();
    		Log.d(TAG, "### endDocument");
    	}
    	
    	@Override
    	public void startElement(String uri, String localName, String qName,
    			Attributes attributes) throws SAXException {
    		super.startElement(uri, localName, qName, attributes);
    		String tagName = localName.length()>0?localName:qName;
    		if(tagName.equals(riverStr)) {
    			isRiver = true;
    			river = new River();
    			river.setName(attributes.getValue(nameStr));
    			river.setLength(Integer.parseInt(attributes.getValue(lengthStr)));
    		}
    		if(isRiver) {
    			if(tagName.equals(introductionStr)) {
    				isIntrocduce = true;
    			} else if(tagName.equals(imageurlStr)) {
    				isImageUrl = true;
    			}
    		}
    			
    	}
    	
    	@Override
    	public void endElement(String uri, String localName, String qName)
    			throws SAXException {
    		super.endElement(uri, localName, qName);
    		//Log.d(TAG, "### endElement uri=" + uri + " localName=" + localName + " qName=" + qName);
    		String tagName=localName.length()!=0?localName:qName;
    		if(tagName.equals(riverStr)) {
    			isRiver = false;
    			rivers.add(river);
    		}
    		if(isRiver) {
    			if(tagName.equals(introductionStr)) {
    				isIntrocduce = false;
    			} else if(tagName.equals(imageurlStr)) {
    				isImageUrl = false;
    			}
    		}
    	}
    	
    	@Override
    	public void characters(char[] ch, int start, int length)
    			throws SAXException {
    		super.characters(ch, start, length);
    		if(isIntrocduce) {
    			river.setIntroduction(river.getIntroduction() == null ? "" : river.getIntroduction() + new String(ch,start,length));
    		}else if(isImageUrl) {
    			river.setImageurl(river.getImageurl()==null ? "" : river.getImageurl()+ new String(ch,start,length));
    		}
    	}
    	
    	public List<River> getRivers(){
    		return rivers;
    	}
    	
    }
    
    private List<River> fetchRiverFromXmlByDom(String fileName) {
    	long startTime = System.currentTimeMillis();
    	List<River> rivers = new ArrayList<River>();
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	InputStream inputStream = null;
    	try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			inputStream = getAssets().open(fileName);
			Document document = builder.parse(inputStream);
			Element root =  document.getDocumentElement();
			NodeList nodeList = root.getElementsByTagName(riverStr);
			int noteListSize = nodeList.getLength();
			River river = null;
			for(int i=0;i<noteListSize;i++) {
				river = new River();
				Element element = (Element) nodeList.item(i);
				river.setName(element.getAttribute(nameStr));
				river.setLength(Integer.parseInt(element.getAttribute(lengthStr)));
				Element introTag = (Element) element.getElementsByTagName(introductionStr).item(0);
				river.setIntroduction(introTag.getFirstChild().getNodeValue());
				Element imageUrlTag = (Element) element.getElementsByTagName(imageurlStr).item(0);
				river.setImageurl(imageUrlTag.getFirstChild().getNodeValue());
				rivers.add(river);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
    	Log.d(T, "exec fetchRiverFromXmlByDom use time = " + (System.currentTimeMillis() - startTime));
    	return rivers;
    }

    private void  fetchDataFromXmlByJson(String str) {
    	try {
			JSONObject jsonObject = new JSONObject(str).getJSONObject("calendar");
			JSONArray jsonArray = jsonObject.getJSONArray("calendarlist");
			for(int i=0;i<jsonArray.length();i++) {
				JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
				System.out.println(jsonObject2.getString("calendar_id"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
   
    	
    }
    
    
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dom:
			StringBuilder sb1 = new StringBuilder();
			List<River> list1 = fetchRiverFromXmlByDom(fileName);
	        for(River r:list1) {
	        	sb1.append("name=" + r.getName() + " length=" + r.getLength() + " introduce=" + r.getIntroduction() + " image url =" + r.getImageurl() +"\n\r");
	        }
	        tv.setText(sb1.toString());
			break;
		case R.id.sax:
			StringBuilder sb2 = new StringBuilder();
			List<River> list2 = fetchRiverFormXmlBySAX(fileName);
	        for(River r:list2) {
	        	sb2.append("name=" + r.getName() + " length=" + r.getLength() + " introduce=" + r.getIntroduction() + " image url =" + r.getImageurl() +"\n\r");
	        }
	        tv.setText(sb2.toString());
			break;
		case R.id.pull:
			StringBuilder sb3 = new StringBuilder();
			List<River> list3 = fetchRiverFormXmlByPull(fileName);
	        for(River r:list3) {
	        	sb3.append("name=" + r.getName() + " length=" + r.getLength() + " introduce=" + r.getIntroduction() + " image url =" + r.getImageurl() +"\n\r");
	        }
	        tv.setText(sb3.toString());
			break;
			
		default:
			break;
		}
	}
}
