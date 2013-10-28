/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blagajna;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author eigorde
 */
public class XMLEcho {

    private String sURL;
    private String testniUzorak;
    private String prefix;    
    private String testniOdgovor;

    /**
     * URL za slanje SOAP echo poruke
     *
     * @return URL adresa
     */
    public String getsURL() {
        return sURL;
    }

    /**
     * Testni uzorak koji je odgovor nakon <I>sendEcho()</I> metode.<BR>
     * Prvo postavite uzorak sa <I>setTestniUzorak</I> metodom.<BR>
     * @return String
     */
    public String getTestniUzorak() {
        return testniOdgovor;
    }
        
    /**
     * URL za slanje SOAP echo poruke
     *
     * @param sURL URL adresa
     */
    public void setsURL(String sURL) {
        this.sURL = sURL;
    }


    /**
     * Testni uzorak koji se zeli poslati.<BR>
     *
     * @param testniUzorak Alfanumericki niz za testiranje. Postavite prije pozive <I>sendEcho()</I>.<BR>
     */
    public void setTestniUzorak(String testniUzorak) {
        this.testniUzorak = testniUzorak;
    }

    /**
     * ApisIT-ov prefiks, za sada je <I>tns</I>
     *
     * @return prefiks
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * ApisIT-ov prefiks, za sada je <I>tns</I>
     *
     * @param prefix prefiks
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * <I>XMLEcho</I> 1. Postavite testni uzorak <BR> 2. Posaljite echo
     * poruku<BR> 3. Provjerite testni uzorak<BR>
     *
     */
    public XMLEcho() {
        // Apis-ITov prefiks
        prefix = "tns";
        // URL
        sURL = "https://cistest.apis-it.hr:8449/FiskalizacijaServiceTest";
        // Testni odgovor
        testniOdgovor = "";
    }

    /**
     * <I>sendEcho</I><BR> 1. Postavite testni uzorak <BR> 2. Posaljite echo
     * poruku<BR> 3. Provjerite testni uzorak<BR>
     *
     */
    public void sendEcho() {

        try {
            // Building the request document
            SOAPFactory soapFactory = SOAPFactory.newInstance();
            // SOAP Message object     	 
            SOAPMessage reqMsg = MessageFactory.newInstance().createMessage();
            SOAPPart soapPart = reqMsg.getSOAPPart();
            // SOAP Envelope
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            // Add Apis-IT namespace
            soapEnvelope.addNamespaceDeclaration("f73", "http://www.apis-it.hr/fin/2012/types/f73");
            // Replace SOAP-ENV with soapenv tags
            soapEnvelope.setPrefix("soapenv");
            soapEnvelope.removeNamespaceDeclaration("SOAP-ENV");
            // SOAP Header
            SOAPHeader header = soapEnvelope.getHeader();
            // Replace SOAP-ENV with soapenv tags
            header.setPrefix("soapenv");
            header.removeNamespaceDeclaration("SOAP-ENV");

            // SOAP Body
            SOAPBody soapBody = soapEnvelope.getBody();
            // Replace SOAP-ENV with soapenv tags
            soapBody.setPrefix("soapenv");
            soapBody.removeNamespaceDeclaration("SOAP-ENV");
            // Add EchoRequest tag in body section
            Name bodyName = soapFactory.createName("EchoRequest", "f73", null);
            SOAPElement bodyElement = soapBody.addBodyElement(bodyName);
            bodyElement.addTextNode(testniUzorak);

            // Connecting and calling
            SOAPConnection soapConnection = SOAPConnectionFactory.newInstance().createConnection();
            //System.out.println("\n\nWaiting for connection ...");
            SOAPMessage resMsg = soapConnection.call(reqMsg, sURL);
            soapConnection.close();

            // iskoristi SOAP objekte iz odgovora			
            soapPart = resMsg.getSOAPPart();
            soapEnvelope = soapPart.getEnvelope();
            soapBody = soapEnvelope.getBody();
                  
            // obrada odgovora
            NodeList nList;
            // izvuci Jir iz odgovora (ako je sve ok)
            nList = soapBody.getElementsByTagName(prefix + ":EchoResponse");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    String tagName = xmlTag.getNodeName();
                    String tagValue = xmlTag.getTextContent();
                    if (tagName.equalsIgnoreCase(prefix + ":EchoResponse")) {
                        testniOdgovor = tagValue;
                    }
                }
            } else {
                testniOdgovor = "";
            }

      } catch (SOAPException e) {
            testniOdgovor = "";
        }
    }
}
