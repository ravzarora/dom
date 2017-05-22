package java_dom_parser;


import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MyDomParser {

	public static void main(String[] args) {
    DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
    try {
		DocumentBuilder builder= factory.newDocumentBuilder();
		Document doc =builder.parse("simple.xml");
		NodeList foodList =doc.getElementsByTagName("food");
		for(int i=0;i<foodList.getLength();i++){
			Node f=foodList.item(i);
			if(f.getNodeType()==Node.ELEMENT_NODE){
				Element foodItem=(Element) f;
			    String name =foodItem.getAttribute("name");
				NodeList namelist=foodItem.getChildNodes();
				for(int j=0;j<namelist.getLength();j++){
					Node n=namelist.item(j);
					if(n.getNodeType()==Node.ELEMENT_NODE){
						Element name1 =(Element) n;
						System.out.println("Food :"+name1.getTagName()+ name1.getTextContent());
					}
				}
			}
		}
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	

}
