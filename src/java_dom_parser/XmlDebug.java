package java_dom_parser;



/*
 * XmlDebug.java
 *
 * Created on February 7, 2002, 9:46 AM
 */



import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
//import org.ninjasoft.util.Text;

/**
 * XmlDebug (written for the older XML libraries (xerces 1.3.1), for use with
 * the Rosettanet code.
 *
 * The useful public functions you will want to call are:
 *  logDocument
 *  printDocument
 *  validateDocument
 *
 */
public class XmlDebug {
    public static final int INDENT = 3;
    private StringBuffer errorMessages = new StringBuffer();

    /** No need to instantiate, everything pubilc is static */
    private XmlDebug() {}

    /**
     * Take an XML document and log it to c:\xml_log.txt with a timestamp
     * and comment.
     *
     *@param d XML DOM document
     *@param comment comment to put in log file
     */
    public static void logDocument(Document d, String comment)
    {
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            FileOutputStream fos = new FileOutputStream("c:/xml_log.txt", true);
            PrintStream ps = new PrintStream(fos);
            ps.println(formatter.format(new Date()) + " " + comment);
            printDocument(ps, d);
            ps.println("\n\n\n");
        }catch(IOException e){System.out.println(e.toString());}
    }

    /**
     * Print an XML DOM document to stdout.
     *
     *@param d XML DOM document
     */
    public static void printDocument(Document d) {printDocument(System.out, d);}

    /**
     * Print an XML document to stdout, parsing it into an XML DOM first.
     *
     *@param filename path+name of XML text file to read in
     */
    public static void printDocument(String filename)
    {
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            printDocument(dbf.newDocumentBuilder().parse(filename));
        }catch(Exception e){System.out.println(e.toString());}
    }

    /**
     * Print an XML DOM document to the given PrintStream (instead of stdout).
     *
     *@param out the print stream to place the document
     *@param d the XML DOM document
     */
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

    /**
     * Given a node, recursively print it and its children.
     *
     *@param out the PrintStream to print the output
     *@param n the node to print
     *@depth the indent level--initially pass in zero, as the function gets
     *       recusively more deep, this number increments by steps of one
     */
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
        b.append("\"");
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

    /**
     * Check for valid document and return an empty string or the errors discovered.
     *
     *@return empty string for good, error messages for bad
     */
    public static String validateDocument(String filename)
    {
        XmlDebug debug = new XmlDebug();
        return debug.validateDocumentInternal(filename);
    }

    /**
     * Internal function for validateDocument.  This needs to be non-static
     * because instantiating the internal XmlErrorHandler class first requires
     * an instantiation of the outer class.
     */
    private String validateDocumentInternal(String filename)
    {
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new XmlErrorHandler());
            Document doc = db.parse(filename);
        }catch(Exception e){errorMessages.append(e.toString() + "\n");}
        return errorMessages.toString();
    }

    /**
     * Print the command line usage and exit the system.
     */
   

    /**
     * XML Error handler--throw an exception on all events, not just errors and
     * fatal errors, but warnings as well.
     */
    private class XmlErrorHandler implements org.xml.sax.ErrorHandler
    {
        public void error(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException
            {errorMessages.append("Error:" + sAXParseException.toString() + "\n");}
        public void warning(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException
            {errorMessages.append("Warn:" + sAXParseException.toString() + "\n");}
        public void fatalError(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException
            {errorMessages.append("Fatal: " + sAXParseException.toString() + "\n");}
    }

    /**
     * Main routine for command line usage.
     */
    public static void main(String[] argv)
    {
        
        String filename = "simple.xml";
        
       
            printDocument(filename);
        
        {
            String rc = validateDocument(filename);
            if (rc.length() == 0)
                rc = "Good document";
            System.out.println(rc);
        }
    }
}