package com.flyman.app.xmlparser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //InputStream is = getResources().getAssets().open("person"); //asset方式获得xml
        //InputStream is = getResources().openRawResource(R.raw.person); //raw方式获得xml
        //使用 pull解析
        usePullParser();
        //使用dom解析
        useDomParser();
        //SAX(Simple API for XML)解析器是一种基于事件的解析器
        useSAXParser();

    }

    /**
     * 使用Sax解析
     *
     * @param
     * @return nothing
     */
    private void useSAXParser() {
        SAXParserFactory sf = SAXParserFactory.newInstance();
        try {
            SAXParser parser = sf.newSAXParser();
            SAXHandler mSaxHandler = new SAXHandler();
            parser.parse(getResources().openRawResource(R.raw.person), mSaxHandler);
            List<Person> mPersonList = mSaxHandler.getPersonList();
            //输出解析的结果
            showPersonList(mPersonList);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void usePullParser() {
        try {
            InputStream is = getResources().getAssets().open("person.xml");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "utf-8");
            int eventType = parser.getEventType();
            boolean isParser = false;
            Person pullPerson = null;
            List<Person> pullList = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: {
                        Log.e("", "使用pull解析");
                        pullList = new ArrayList<>();
                        break;
                    }
                    case XmlPullParser.START_TAG: {
                        if (nodeName.equals("person")) {
                            Log.e("", "解析到person元素");
                            pullPerson = new Person();
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String id = parser.getAttributeValue(i);
                                Log.e("", "person的属性值是:" + id);
                                pullPerson.setId(id);

                            }
                        } else if (nodeName.equals("name")) {
                            Log.e("", "解析到name元素");
                            String name = parser.nextText();//获取节点name的文本
                            Log.e("", "name的文本内容是:" + name);
                            pullPerson.setName(name);
                        } else if (nodeName.equals("age")) {
                            Log.e("", "解析到age元素");
                            String age = parser.nextText();//获取节点age的文本
                            Log.e("", "age的文本内容是:" + age);
                            pullPerson.setAge(age);
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        Log.e("", "结束解析:" + nodeName + "元素");
                        if (nodeName.equals("person")) {
                            pullList.add(pullPerson);
                            pullPerson = null;
                        }
                        break;
                    }
                    case XmlPullParser.END_DOCUMENT: {
                        Log.e("", "pull结束解析");
                        break;
                    }
                    default: {

                    }
                }
                //开始下一个元素的解析
                if (isParser == false) {
                    eventType = parser.next();
                } else {
                    eventType = XmlPullParser.END_DOCUMENT;
                }
            }
            showPersonList(pullList);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void useDomParser() {
        Log.e("", "使用dom解析");
        InputStream is = getResources().openRawResource(R.raw.person);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document document = db.parse(is);
            Node node = document.getDocumentElement();
//            parserNode(node); //遍历xml所有的元素
//            NodeList mNodeList = document.getElementsByTagName("person");  //按"person"元素名 遍历某部分的数据
//            parserNode(mNodeList);
            Element element = document.getElementById("28");//按"属性 = 28" 遍历某部分的数据
            parserNode(element);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 解析全部的节点
     *
     * @param
     * @return nothing
     */
    private void parserNode(Node node) {
        NodeList mNodeList = node.getChildNodes();
        parserNode(mNodeList);
    }

    /**
     * 解析全部的节点
     *
     * @param
     * @return nothing
     */
    private void parserNode(NodeList mNodeList) {
        for (int i = 0; i < mNodeList.getLength(); i++) {
            Node mNode = mNodeList.item(i);
            int nodeType = mNode.getNodeType();
            //如果是元素节点
            if (nodeType == Node.ELEMENT_NODE) {
                Log.e("", "元素名<" + mNode.getNodeName() + ">");
                //如果元素有属性
                if (mNode.hasAttributes()) {
                    NamedNodeMap mNodeAttributes = mNode.getAttributes();//属性值
                    for (int j = 0; j < mNodeAttributes.getLength(); j++) {
                        Node att = mNodeAttributes.item(j);//每一个属性
                        String attName = att.getNodeName();
                        String attValue = att.getNodeValue();
                        Log.e("", "元素<" + mNode.getNodeName() + ">含有属性 " + "属性名:" + attName + " 属性值:" + attValue);
                    }
                }
                //输出元素的文本值
                if (mNode.getTextContent() != null && mNode.hasAttributes() == false) {
                    Log.e("", "元素<" + mNode.getNodeName() + ">" + " 值为 " + mNode.getTextContent());
                }
                //如果元素节点下还有其他元素,使用递归的方法获取所有的元素
                parserNode(mNode);

            }

        }
    }

    private void showPersonList(List mList) {
        for (Object mPerson : mList) {
            Log.e("", mPerson.toString());
        }
    }
}
