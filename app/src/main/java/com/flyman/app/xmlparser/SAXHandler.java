package com.flyman.app.xmlparser;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
/**
 *  @ClassName SAXHandler
 *  @description
 *  SAX解析xml
 *  @author Flyman
 *
 */
public class SAXHandler extends DefaultHandler {
    private String preTag;
    private List<Person> mPersonList;
    private Person mPerson;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        Log.e("", "使用Sax开始解析xml");
        mPersonList = new ArrayList<>();
    }

    @Override
    public void endDocument() throws SAXException {
        Log.e("", "结束Sax解析");
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName.equals("person")) {
            String id = attributes.getQName(0);
            String idValue = attributes.getValue(id);
            Log.e("", id + " = " + idValue);
            mPerson = new Person();
            mPerson.setId(id);
        }
        preTag = qName;

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (qName.equals("person")){
            mPersonList.add(mPerson);
            mPerson = null;
        }
        preTag = null;//当解析结束时置为空
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (preTag != null) {
            String value = new String(ch, start, length);
            if (preTag.equals("name")) {
                mPerson.setName(value);
            } else if (preTag.equals("age")) {
                mPerson.setAge(value);
            }
        }

    }
    public List<Person> getPersonList()
    {
        return mPersonList;
    }
}
