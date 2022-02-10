package com.snaps.common.utils.net.xml;

import com.snaps.common.utils.log.Dlog;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Server api의 결과인 xml을 DomParser Element 객체에 담고, xml 규칙은 신경쓰지 않고 원하는 값을 추출할 수 있도록 함.
 * 
 * @author crjung
 *
 */
public class XmlResult {
	private static final String TAG = XmlResult.class.getSimpleName();

	Element root;// xml root
	Map<Integer, NodeList> mapDepth = new HashMap<Integer, NodeList>();// xml 안의 배열저장(각 Depth별로 저장)

	static DocumentBuilder builder;

	public XmlResult(String response) throws Exception {
		root = getRoot(response);
	}

	public XmlResult(File file) throws Exception {

		String response = null;
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			response = new String(chars);
			reader.close();
		} catch (Exception e) {
			Dlog.e(TAG, e);
			return;
		}

		root = getRoot(response);
	}

	synchronized void initBuilder() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Dlog.e(TAG, e);
		}
	}
	public void close() {
		mapDepth.clear();
		mapDepth = null;
		root = null;
	}

	/**
	 * xml 문자열로부터 Root Element 도출
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	Element getRoot(String xml) throws Exception {
		if (builder == null)
			initBuilder();
		InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
		Document doc = builder.parse(istream);
		return doc.getDocumentElement();
	}

	/**
	 * element 이름으로 검색하여 NodeList를 반환(반복되는 xml element를 찾음.)
	 * 
	 * @param node
	 * @param elementName
	 * @return
	 */
	public NodeList getElements(Node node, String elementName) {
		return ((Element) node).getElementsByTagName(elementName);
	}
	/**
	 * Xml Node의 값을 추출
	 * 
	 * @param node
	 * @return
	 */
	String getValue(Node node) {
		if (node == null || node.getFirstChild() == null)
			return "";

        Element e = null;
        XPath xPath = XPathFactory.newInstance().newXPath();
		return node.getFirstChild().getNodeValue();
	}
	/**
	 * xml Node 안에 Element를 바로 찾아 값을 추출
	 * 
	 * @param node
	 * @param elementName
	 * @return
	 */
	String getEValue(Node node, String elementName) {
		return getValue(((Element) node).getElementsByTagName(elementName).item(0));
	}
	/**
	 * xml Element 안에 Element를 바로 찾아 값을 추출
	 * 
	 * @param element
	 * @param elementName
	 * @return
	 */
	String getEValue(Element element, String elementName) {
		NodeList nodelist = element.getElementsByTagName(elementName);
		return nodelist == null ? "" : getValue(nodelist.item(0));
	}

	Element getElement(Element element, String elementName) {
		NodeList nodelist = element.getElementsByTagName(elementName);
		return nodelist == null ? null : (Element) nodelist.item(0);
	}

	/**
	 * 단일값 추출
	 * 
	 * @param elementName
	 *            추출하려는 element 이름
	 * @return
	 */
	public String get(String elementName) {
		return getEValue(root, elementName);
	}
	/**
	 * 단일값의 속성값 추출
	 * 
	 * @param elementName
	 * @param attrName
	 * @return
	 */
	public String get(String elementName, String attrName) {
		Element element = getElement(root, elementName);
		if (element != null)
			return element.getAttribute(attrName);
		return "";
	}
	
	/**
	 * root의 속성값 추출
	 * 
	 * @param attrName
	 * @return
	 */
	public String getFromRoot( String attrName ) {
		if( root != null ) 
			return root.getAttribute(attrName);
		return "";
	}

	/**
	 * 1 Depth 리스트정보 추출(이 메서드 호출 후에 getListItemD1을 호출해야 함.)
	 * 
	 * @param elementName
	 * @return 리스트 count
	 */
	public int getList(String elementName) {
		try {
			NodeList nodeList = getElements(root, elementName);
			mapDepth.put(1, nodeList);
			return nodeList.getLength();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return 0;
	}

	public NodeList getNodeList(String elementName) {
		try {
			NodeList nodeList = getElements(root, elementName);
			return nodeList;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return null;
	}

	/**
	 * 1 Depth의 리스트에서 해당 idx의 Element 값 추출(이 메서드 호출 전 getList(String elementName) 를 호출해야 함.)
	 * 
	 * @param idx1
	 *            1Depth 리스트의 idx
	 * @param elementName
	 * @return
	 */
	public String getListItemD1(int idx1, String elementName) {
		if (!mapDepth.containsKey(1))
			return null;
		Node node = mapDepth.get(1).item(idx1);
		return getEValue(node, elementName);
	}

    public Element getListD1( int idx1 ) {
        if (!mapDepth.containsKey(1))
            return null;

        return (Element) mapDepth.get(1).item( idx1 );
    }

	public String getListAttrD1(int idx1, String attrName) {
		if (!mapDepth.containsKey(1))
			return null;
		Node node = mapDepth.get(1).item(idx1);
		return ((Element) node).getAttribute(attrName);
	}

}
