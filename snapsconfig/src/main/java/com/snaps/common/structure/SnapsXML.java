package com.snaps.common.structure;

import android.util.Log;
import android.util.Xml;

import com.snaps.common.utils.log.Dlog;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;


/**
 *
 * com.snaps.kakao.structure
 * SnapsXML.java
 *
 * @author ParkJaeMyung
 * @Date : 2013. 5. 25.
 * @Version : 
 */
public class SnapsXML {
	private static final String TAG = SnapsXML.class.getSimpleName();
	private XmlSerializer xml;
	
	public SnapsXML( XmlSerializer xml ) {
		this.xml = xml;
	}
	
	public SnapsXML( StringWriter writer ) {
		this.xml = Xml.newSerializer();
		
		try{
			this.xml.setOutput( writer );
			this.xml.startDocument(null, Boolean.valueOf(true));
			this.xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		} catch ( Exception e ) {
			Dlog.e(TAG, e);
		}
	}
	
	public SnapsXML ( FileOutputStream file ) {
		this.xml = Xml.newSerializer();
		
		try{
			this.xml.setOutput( file , "UTF-8" );
			this.xml.startDocument(null, Boolean.valueOf(true));
			this.xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		} catch ( Exception e ) {
			Dlog.e(TAG, e);
		}
	}
	
	/**
	 * @return
	 */
	public XmlSerializer getXMLSerializer () {
		return this.xml;
	}
	
	/**
	 * @param namespace
	 * @param name
	 */
	public void startTag ( String namespace , String name ) {
		try	{
			this.xml.startTag( namespace , name );
		} catch ( Exception e ) {
			Dlog.e(TAG, e);

		}
	}

    public void cData( String text ) {
        try {
            this.xml.cdsect( text );
        } catch ( Exception e ) {
			Dlog.e(TAG, e);
        }
    }
	
	/**
	 * @param namespace
	 * @param name
	 */
	public void endTag ( String namespace , String name ) {
		try	{
			this.xml.endTag( namespace , name );
		} catch ( Exception e ) {
			Dlog.e(TAG, e);
		}
	}
	
	/**
	 * @param value
	 */
	public void text ( String value ) {
		try {
			xml.text(value);
		} catch (IllegalArgumentException e) {
			Dlog.e(TAG, e);
			try {
				xml.text("");
			} catch (IllegalArgumentException e1) {
				Dlog.e(TAG, e1);
			} catch (IllegalStateException e1) {
				Dlog.e(TAG, e1);
			} catch (IOException e1) {
				Dlog.e(TAG, e1);
			}	
		} catch(Exception e2) {
			Dlog.e(TAG, e2);
		}
	}
	
	/**
	 * @param namespcae
	 * @param name
	 * @param value
	 */
	public void attribute ( String namespcae , String name , String value ) {
		if ( value != null ) {
			try {
				this.xml.attribute( namespcae, name , value );
			} catch ( Throwable e ) {
				Dlog.e(TAG, e);
			}
		}
	}
	
	/**
	 * @param namespace
	 * @param name
	 * @param value
	 */
	public void addTag ( String namespace , String name , String value ) {
		try	{
			startTag( namespace , name );
			text( value );
			endTag( namespace , name );
		} catch ( Exception e ) {
			Dlog.e(TAG, e);
		}
	}
	
	/**
	 * 
	 */
	public void endDocument () {
		try {
			this.xml.endDocument();
			this.xml.flush();
		} catch ( Exception e ) {
			Dlog.e(TAG, e);
		}
	}
}
