package com.snaps.common.data.parser;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class GetNewPhotoPrintSaveXMLHandler extends DefaultHandler {
	private static final String TAG = GetNewPhotoPrintSaveXMLHandler.class.getSimpleName();

	private ArrayList<PhotoPrintData> datas;
    private PhotoPrintData baseData, currentData;

    private boolean isHtml5CompatibleVersion = true;

	@Override
	public void startDocument() throws SAXException {
		datas = new ArrayList<PhotoPrintData>();
        baseData = new PhotoPrintData();
	}

    public void setHtml5CompatibleVersion( boolean html5CompatibleVersion ) {
        isHtml5CompatibleVersion = html5CompatibleVersion;
    }

    @Override
	public void endDocument() throws SAXException {
        if( isHtml5CompatibleVersion )
            datas.remove( 0 );
    }


    private boolean getBoolean( String str ) {
        return "yes".equalsIgnoreCase( str );
    }

    private int getInt( String str ) {
        return Integer.parseInt( str );
    }

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals("info")) {
            baseData.setAdjustBrightness( getBoolean(getValue(attributes, "printBrightType")) );
            baseData.setShowPhotoDate( getBoolean(getValue(attributes, "printRegDateType")) );
            baseData.setMakeBorder( getBoolean(getValue(attributes, "printBorderType")) );
            baseData.setGlossyType( getValue(attributes, "printGlossyType") );
            baseData.setCount( getInt(getValue(attributes, "printCnt")) );
            baseData.setImageFull( getBoolean(getValue(attributes, "printImgFullType")) );
		}
        else if (localName.equals("scene") ) {
            currentData = new PhotoPrintData( new MyPhotoSelectImageData() );
            currentData.setDataFromSaveXml( this, localName, attributes, isHtml5CompatibleVersion );
        }
        else if ( localName.equals("objects") && "image".equalsIgnoreCase(getValue(attributes, "type")) )
            currentData.setDataFromSaveXml( this, localName, attributes, isHtml5CompatibleVersion );
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("scene")) {
            if ( currentData != null ) {
                datas.add( currentData );
                currentData = null;
            }
        }
	}

    public ArrayList<PhotoPrintData> getDatas() { return datas; }
    public PhotoPrintData getBaseData() { return baseData; }

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
	}

    public boolean getBooleanValue( Attributes target, String name ) {
        String value = target.getValue(name);
        return value != null && ("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) ? true : false;
    }

	public String getValue(Attributes target, String name) {
		String value = target.getValue(name);
		return (value == null) ? "" : value;
	}

	public int getIntValue(Attributes target, String name) {
		String value = getValue(target, name);
		return value.equals("") ? 0 : Integer.parseInt(value);
	}

    public float getFloatValue(Attributes target, String name) {
        String value = getValue(target, name);
        return value.equals("") ? 0 : Float.parseFloat(value);
    }

	public void parsing(String xmlString) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(this);
			InputSource source = new InputSource();
			source.setCharacterStream(new StringReader(xmlString));
			reader.parse(source);
		} catch (ParserConfigurationException e) {
			Dlog.e(TAG, e);

		} catch (SAXException e) {
			Dlog.e(TAG, e);

		} catch (IOException e) {
			Dlog.e(TAG, e);

		}
	}
}
