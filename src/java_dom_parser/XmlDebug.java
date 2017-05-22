package java_dom_parser;



/*
 * XmlDebug.java
 *
 * Created on May 22, 2017, 13:46 PM
 */



import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import org.w3c.dom.*;
import javax.xml.parsers.*;




public class XmlDebug {
    public static final int INDENT = 3;
    private StringBuffer errorMessages = new StringBuffer();

   
    private XmlDebug() {}

    
    public static void logDocument(Document d, String comment)
    {
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            FileOutputStream fos = new FileOutputStream("E:/xml_log.txt", true);
            PrintStream ps = new PrintStream(fos);
            ps.println(formatter.format(new Date()) + " " + comment);
            printDocument(ps, d);
            ps.println("\n\n\n");
        }catch(IOException e){System.out.println(e.toString());}
    }

    
    public static void printDocument(Document d) {printDocument(System.out, d);}

    public static void printDocument(String filename)
    {
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            printDocument(dbf.newDocumentBuilder().parse(filename));
        }catch(Exception e){System.out.println(e.toString());}
    }

    
    public static void printDocument(PrintStream out, Document d)
    {
        DocumentType dt = d.getDoctype();
        if (dt != null)
        {
            System.out.println("doctype name: " + (dt.getName()));
            System.out.println("doctype public id: " + (dt.getPublicId()));
            System. out.println("doctype system id: " + (dt.getSystemId()));
        }
        else
        {
            out.println("No doctype specified in XML");
        }
        printNodeRecursive(out, d.getDocumentElement(), 0);
    }

    public static void printNode(PrintStream out, Node node)
    {
        printNodeRecursive(out, node, 0);
    }

    public static void printNode(Node node)
    {
        printNodeRecursive(System.out, node, 0);
    }

   
    private static void printNodeRecursive(PrintStream out, Node n, int depth)
    {
        StringBuffer b = new StringBuffer();
        NamedNodeMap map;
        int max;
        Node tempNode;
        NodeList children;
        String value;

        // Get name and value
        for (int i=0; i<depth; i++)
            b.append(" ");
        b.append(n.getNodeName());
        b.append("/\"");
        value = n.getNodeValue();
        if (value != null)
            b.append(printStringEncoded(value));
        b.append("\n");
        // Get optional attributes
        map = n.getAttributes();
        if (map != null)
        {
            max = map.getLength();
            for (int i=0; i < max; i++)
            {
                tempNode = map.item(i);
                b.append(" ");
                b.append(tempNode.getNodeName());
                b.append("=");
                b.append(printStringEncoded(tempNode.getNodeValue()));
            }
        }
        // Print
        out.println(b.toString());
        b = null;
        children = n.getChildNodes();
        max = children.getLength();
        for (int i=0; i<max; i++)
            printNodeRecursive(out, children.item(i), depth + INDENT);
    }

    /**
     * Return a string with newlines converted to "\n"
     */
    private static String printStringEncoded(String s)
    {
        int max = s.length();
        StringBuffer result = new StringBuffer(max);
        for (int i=0; i<max; i++)
        {
            char c = s.charAt(i);
            switch(c)
            {
                case '\r': result.append("\\r"); break;
                case '\n': result.append("\\n"); break;
                case '"':  result.append("\\\""); break;
                case '\\': result.append("\\\\"); break;
                default: result.append(c); break;
            }
        }
        return result.toString();
    }

   
    public static String validateDocument(String filename)
    {
        XmlDebug debug = new XmlDebug();
        return debug.validateDocumentInternal(filename);
    }

   
    private String validateDocumentInternal(String filename)
    {
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new XmlErrorHandler());
            Document doc = db.parse(filename);
        }catch(Exception e){errorMessages.toString();}
        return errorMessages.toString();
    }

   
    private class XmlErrorHandler implements org.xml.sax.ErrorHandler
    {
        public void error(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException
            {errorMessages.append("Error:" + sAXParseException.toString() + "\n");}
        public void warning(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException
            {errorMessages.append("Warn:" + sAXParseException.toString() + "\n");}
        public void fatalError(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException
            {errorMessages.append("Fatal: " + sAXParseException.toString() + "\n");}
    }

    public static void main(String[] argv)
    {
        
        String filename = "simple.xml";
        
       
          //  printDocument(filename);
        
        {
            String rc = validateDocument(filename);
            if (rc.length() == 0)
                rc = "Good document";
            System.out.println(rc);
        }
    }
}