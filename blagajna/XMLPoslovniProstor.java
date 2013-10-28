package blagajna;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLPoslovniProstor {

    private static DocumentBuilderFactory docFactory;
    private static DocumentBuilder docBuilder;
    private static DOMImplementation domImpl;
    private static DOMSource docSource;
    private static Document xmlDocument;
    private String apisURI;
    private String prefix;
    // Kljucarnik sa kljucem i sifrom
    private static KeyStore keyStore;
    @SuppressWarnings("unused")
    private static PublicKey publicKey;
    private static PrivateKey privateKey;
    private static X509Certificate cert;
    // Email korisnika kljuca
    private static String email;
    // URL CIS poluzitelja
    private String sURL;
    // ID poruke
    private String idPoruke;
    // Datum i vrijeme
    private Date datumVrijeme;
    // oib firme
    private String oib;
    // Adresa
    private String ulica;
    private String kucniBroj;
    private String dodatakKucnomBroju;
    private String brojPoste;
    private String naselje;
    private String opcina;
    // Radno vrijeme
    private String radnoVrijeme;
    // Datum otkad vrijedi primjena
    private Date datumPocetkaPrimjene;
    // 'Z' znaci zatvaranje poslovnice
    private String oznakaZatvaranja;
    // 'Z' znaci zatvaranje poslovnice
    private String oznakaPoslovnogProstora;
    // sifre gresaka od CISa
    private String sifraGreske;
    private String porukaGreske;

    public String getIdPoruke() {
        return idPoruke;
    }

    public String getDatumVrijeme() {
        String dateTime = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss", Locale.ENGLISH).format(datumVrijeme);
        return dateTime;
    }

    public String getDatumPocetkaPrimjene() {
        String dateTime = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(datumPocetkaPrimjene);
        return dateTime;
    }

    public void setDatumPocetkaPrimjene(Date _datumPocetkaPrimjene) {
        datumPocetkaPrimjene = _datumPocetkaPrimjene;
    }

    public String getOznakaPoslovnogProstora() {
        return oznakaPoslovnogProstora;
    }

    public void setOznakaPoslovnogProstora(String _oznakaPoslovnogProstora) {
        oznakaPoslovnogProstora = _oznakaPoslovnogProstora;
    }

    public String getRadnoVrijeme() {
        return radnoVrijeme;
    }

    public void setRadnoVrijeme(String _radnoVrijeme) {
        radnoVrijeme = _radnoVrijeme;
    }

    public String getApisURI() {
        return apisURI;
    }

    public void setApisURI(String URI) {
        apisURI = URI;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefiks) {
        prefix = prefiks;
    }

    public String getURL() {
        return sURL;
    }

    public void setURL(String URL) {
        sURL = URL;
    }

    public String getUlica() {
        return ulica;
    }

    public String getKucniBroj() {
        return kucniBroj;
    }

    public String getDodatakKucnomBroju() {
        return dodatakKucnomBroju;
    }

    public String getBrojPoste() {
        return brojPoste;
    }

    public String getNaselje() {
        return naselje;
    }

    public String getOpcina() {
        return opcina;
    }

    public void setUlica(String _ulica) {
        ulica = _ulica;
    }

    public void setKucniBroj(String _kucniBroj) {
        kucniBroj = _kucniBroj;
    }

    public void setDodatakKucnomBroju(String _dodatakKucnomBroju) {
        dodatakKucnomBroju = _dodatakKucnomBroju;
    }

    public void setBrojPoste(String _brojPoste) {
        brojPoste = _brojPoste;
    }

    public void setNaselje(String _naselje) {
        naselje = _naselje;
    }

    public void setOpcina(String _opcina) {
        opcina = _opcina;
    }

    public String getOib() {
        return oib;
    }

    public void setOib(String OIB) {
        oib = OIB;
    }

    public String getOznakaZatvaranja() {
        return oznakaZatvaranja;
    }

    public void setOznakaZatvaranja() {
        oznakaZatvaranja = "Z";
    }

    public String getEmail() {

        return email;

    }

    public String getGreska() {

        if (sifraGreske != "" || porukaGreske != "") {
            return sifraGreske + ": " + porukaGreske;
        } else {
            return "";
        }

    }

    public XMLPoslovniProstor() {

        // Inicijalizacija
        //  idealno sve postaviti na pocetne vrijednosti

        docFactory = null;
        docBuilder = null;
        docSource = null;
        domImpl = null;
        xmlDocument = null;

        // Apis-ITov namespace i prefiks
        apisURI = "http://www.apis-it.hr/fin/2012/types/f73";
        prefix = "tns";

        // URL CISa
        sURL = "https://cistest.apis-it.hr:8449/FiskalizacijaServiceTest";

        // Datum & vrijeme, id poruke
        datumVrijeme = null;
        idPoruke = UUID.randomUUID().toString();

        // OIBi
        oib = "123456789";

        // oznaka zatvaranja
        oznakaZatvaranja = "";

        // adresa
        ulica = "";
        kucniBroj = "";
        dodatakKucnomBroju = "";
        brojPoste = "";
        opcina = "";
        naselje = "";

        // radno vrijeme
        radnoVrijeme = "";

        // datum pocetka primjene lokacije/poslovnice
        datumPocetkaPrimjene = new Date();

        // odgovor CISa, i greske
        sifraGreske = "";
        porukaGreske = "";

        // kljucarnik
        keyStore = null;
        publicKey = null;
        privateKey = null;
        cert = null;

    }

    public void buildPoslovniProstor() {

        try {

            // napravi DOM objekte
            docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            docBuilder = docFactory.newDocumentBuilder();
            domImpl = docBuilder.getDOMImplementation();
            xmlDocument = domImpl.createDocument(apisURI, "PoslovniProstorZahtjev", null);

            // datum i vrijeme u formatu prema teh.spec. v1.1
            if (datumVrijeme == null) {
                // ovo je slucaj da datumVrijeme nije postavljeno
                //  npr. ako se koristi buildPoslovniProstor() bez loadPoslovniProstor()
                datumVrijeme = new Date();
            }
            String dateTime = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss", Locale.ENGLISH).format(datumVrijeme);

            // root xml element
            Element xmlPoslovniProstorZahtjev = xmlDocument.getDocumentElement();
            xmlPoslovniProstorZahtjev.setPrefix(prefix);
            // id atribut za potpisivanje, pogledati u signRacun() referencu
            xmlPoslovniProstorZahtjev.setAttribute("Id", "signXmlId");
            xmlPoslovniProstorZahtjev.setIdAttribute("Id", true);
            //xmlRacunZahtjev.setAttributeNS(apisURI, "xsi:schemaLocation", "http://www.apis-it.hr/fin/2012/types/f73 ../schema/FiskalizacijaSchema.xsd");
            xmlPoslovniProstorZahtjev.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

            Element xmlZaglavlje = xmlDocument.createElementNS(apisURI, "Zaglavlje");
            xmlZaglavlje.setPrefix(prefix);

            Element xmlIdPoruke = xmlDocument.createElementNS(apisURI, "IdPoruke");
            xmlIdPoruke.setPrefix(prefix);

            Element xmlDatumVrijeme = xmlDocument.createElementNS(apisURI, "DatumVrijeme");
            xmlDatumVrijeme.setPrefix(prefix);

            Text xmlIdPorukeText = xmlDocument.createTextNode(idPoruke);
            Text xmlDatumVrijemeText = xmlDocument.createTextNode(dateTime);

            xmlDatumVrijeme.appendChild(xmlDatumVrijemeText);
            xmlIdPoruke.appendChild(xmlIdPorukeText);

            xmlZaglavlje.appendChild(xmlIdPoruke);
            xmlZaglavlje.appendChild(xmlDatumVrijeme);

            xmlPoslovniProstorZahtjev.appendChild(xmlZaglavlje);


            Element xmlPoslovniProstor = xmlDocument.createElementNS(apisURI, "PoslovniProstor");
            xmlPoslovniProstor.setPrefix(prefix);

            Element xmlOib = xmlDocument.createElementNS(apisURI, "Oib");
            xmlOib.setPrefix(prefix);

            Text xmlOibText = xmlDocument.createTextNode(oib);

            Element xmlOznPoslProstora = xmlDocument.createElementNS(apisURI, "OznPoslProstora");
            xmlOznPoslProstora.setPrefix(prefix);

            Text xmlOznPoslProstoraText = xmlDocument.createTextNode(oznakaPoslovnogProstora);

            Element xmlAdresniPodatak = xmlDocument.createElementNS(apisURI, "AdresniPodatak");
            xmlAdresniPodatak.setPrefix(prefix);

            xmlOib.appendChild(xmlOibText);
            xmlOznPoslProstora.appendChild(xmlOznPoslProstoraText);

            xmlPoslovniProstor.appendChild(xmlOib);
            xmlPoslovniProstor.appendChild(xmlOznPoslProstora);

            Element xmlAdresa = xmlDocument.createElementNS(apisURI, "Adresa");
            xmlAdresa.setPrefix(prefix);

            Element xmlUlica = xmlDocument.createElementNS(apisURI, "Ulica");
            xmlUlica.setPrefix(prefix);
            Text xmlUlicaText = xmlDocument.createTextNode(ulica);

            xmlUlica.appendChild(xmlUlicaText);
            // ulica bi trebala biti obvezni elementi adrese
            xmlAdresa.appendChild(xmlUlica);

            // kucni broj, dodatak kucnom broju,broj poste,naselje i opcina mogu biti prazni
            if (!(kucniBroj.equalsIgnoreCase(""))) {

                Element xmlKucniBroj = xmlDocument.createElementNS(apisURI, "KucniBroj");
                xmlKucniBroj.setPrefix(prefix);
                Text xmlKucniBrojText = xmlDocument.createTextNode(kucniBroj);

                xmlKucniBroj.appendChild(xmlKucniBrojText);

                xmlAdresa.appendChild(xmlKucniBroj);

            }
            if (!(dodatakKucnomBroju.equalsIgnoreCase(""))) {

                Element xmlKucniBrojDodatak = xmlDocument.createElementNS(apisURI, "KucniBrojDodatak");
                xmlKucniBrojDodatak.setPrefix(prefix);
                Text xmlKucniBrojDodatakText = xmlDocument.createTextNode(dodatakKucnomBroju);

                xmlKucniBrojDodatak.appendChild(xmlKucniBrojDodatakText);

                xmlAdresa.appendChild(xmlKucniBrojDodatak);

            }
            if (!(brojPoste.equalsIgnoreCase(""))) {

                Element xmlBrojPoste = xmlDocument.createElementNS(apisURI, "BrojPoste");
                xmlBrojPoste.setPrefix(prefix);
                Text xmlBrojPosteText = xmlDocument.createTextNode(brojPoste);

                xmlBrojPoste.appendChild(xmlBrojPosteText);

                xmlAdresa.appendChild(xmlBrojPoste);

            }
            if (!(naselje.equalsIgnoreCase(""))) {

                Element xmlNaselje = xmlDocument.createElementNS(apisURI, "Naselje");
                xmlNaselje.setPrefix(prefix);
                Text xmlNaseljeText = xmlDocument.createTextNode(naselje);

                xmlNaselje.appendChild(xmlNaseljeText);
                xmlAdresa.appendChild(xmlNaselje);

            }
            if (!(opcina.equalsIgnoreCase(""))) {

                Element xmlOpcina = xmlDocument.createElementNS(apisURI, "Opcina");
                xmlOpcina.setPrefix(prefix);
                Text xmlOpcinaText = xmlDocument.createTextNode(opcina);

                xmlOpcina.appendChild(xmlOpcinaText);

                xmlAdresa.appendChild(xmlOpcina);

            }

            // dodaj adresu u adresni podatak, i adresni podatak u poslovni prostor
            xmlAdresniPodatak.appendChild(xmlAdresa);
            xmlPoslovniProstor.appendChild(xmlAdresniPodatak);

            // radno vrijeme i pocetak primjene su obvezni
            Element xmlRadnoVrijeme = xmlDocument.createElementNS(apisURI, "RadnoVrijeme");
            xmlRadnoVrijeme.setPrefix(prefix);
            Text xmlRadnoVrijemeText = xmlDocument.createTextNode(radnoVrijeme);

            Element xmlDatumPocetkaPrimjene = xmlDocument.createElementNS(apisURI, "DatumPocetkaPrimjene");
            xmlDatumPocetkaPrimjene.setPrefix(prefix);
            Text xmlDatumPocetkaPrimjeneText = xmlDocument.createTextNode(getDatumPocetkaPrimjene());

            // specijalne namjene, trebaju imati oib autora programa/firme koja odrzava ili firme
            //  koja je uvezla programsku podrsku
            Element xmlSpecNamj = xmlDocument.createElementNS(apisURI, "SpecNamj");
            xmlSpecNamj.setPrefix(prefix);
            Text xmlSpecNamjText = xmlDocument.createTextNode(oib);

            xmlRadnoVrijeme.appendChild(xmlRadnoVrijemeText);
            xmlDatumPocetkaPrimjene.appendChild(xmlDatumPocetkaPrimjeneText);
            xmlSpecNamj.appendChild(xmlSpecNamjText);

            xmlPoslovniProstor.appendChild(xmlRadnoVrijeme);
            xmlPoslovniProstor.appendChild(xmlDatumPocetkaPrimjene);            

            if (oznakaZatvaranja.equalsIgnoreCase("Z")) {
                Element xmlOznakaZatvaranja = xmlDocument.createElementNS(apisURI, "OznakaZatvaranja");
                xmlOznakaZatvaranja.setPrefix(prefix);
                Text xmlOznakaZatvaranjaText = xmlDocument.createTextNode(oznakaZatvaranja);

                xmlOznakaZatvaranja.appendChild(xmlOznakaZatvaranjaText);

                xmlPoslovniProstor.appendChild(xmlOznakaZatvaranja);

            }
            
            xmlPoslovniProstor.appendChild(xmlSpecNamj);

            xmlPoslovniProstorZahtjev.appendChild(xmlPoslovniProstor);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void signPoslovniProstor() {

        String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");

        try {

            NodeList nl = xmlDocument.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");

            if (nl.getLength() > 0) {
                Node parent = nl.item(0).getParentNode();
                parent.removeChild(nl.item(0));
            }

            @SuppressWarnings("rawtypes")
            ArrayList transformList = new ArrayList();

            XMLSignatureFactory factory =
                    XMLSignatureFactory.getInstance("DOM", (Provider) Class.forName(providerName).newInstance());

            DigestMethod digestMethod = factory.newDigestMethod(DigestMethod.SHA1, null);
            Transform envTransform = factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
            Transform exc14nTransform = factory.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#", (TransformParameterSpec) null);

            transformList.add(envTransform);
            transformList.add(exc14nTransform);

            Reference reference = factory.newReference("#signXmlId", digestMethod, transformList, null, null);
            CanonicalizationMethod canonicalizationMethod =
                    factory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null);

            SignatureMethod signatureMethod = factory.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
            SignedInfo signedInfo = factory.newSignedInfo(canonicalizationMethod, signatureMethod, Collections.singletonList(reference));

            KeyInfoFactory keyInfoFactory = factory.getKeyInfoFactory();

            @SuppressWarnings("rawtypes")
            List x509 = new ArrayList();

            X509IssuerSerial issuer = keyInfoFactory.newX509IssuerSerial(cert.getIssuerX500Principal().getName(), cert.getSerialNumber());

            x509.add(cert);
            x509.add(issuer);
            X509Data x509Data = keyInfoFactory.newX509Data(x509);


            @SuppressWarnings("rawtypes")
            List items = new ArrayList();
            items.add(x509Data);

            KeyInfo keyInfo = keyInfoFactory.newKeyInfo(items);

            // workaround for bug:
            //  http://www.coderanch.com/t/534574/Security/xml-signature-interoperability-Java-apache    
            String thisLine = "";
            String xmlString = "";
            BufferedReader br = new BufferedReader(new StringReader(dumpDocument()));
            while ((thisLine = br.readLine()) != null) {
                xmlString = xmlString + thisLine.trim();
            }
            br.close();

            ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlString.getBytes());
            xmlDocument = docBuilder.parse(xmlStream);
            xmlDocument.getDocumentElement().setIdAttribute("Id", true);
            // end of workaround

            // postavi signature xml element i potpisi poruku
            DOMSignContext dsc = new DOMSignContext(privateKey, xmlDocument.getDocumentElement());
            XMLSignature signature = factory.newXMLSignature(signedInfo, keyInfo);

            signature.sign(dsc);

            // provjera potpisa je samo za test i ne radi sa PKCS12 std. keyStore-om
            //if(validateSignature(xmlDocument.getElementsByTagNameNS(XMLSignature.XMLNS,"Signature"))) {
            //	System.out.println("Document signed and verified.");
            //}


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void savePoslovniProstor(String fileName) {

        try {
            String xmlString = dumpDocument();
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            out.write(xmlString);
            out.close();
        } catch (IOException e) {
            System.out.println("IO Exception!");
        }

    }

    public void loadPoslovniProstor(String fileName) {

        try {

            String thisLine = "";
            String xmlString = "";
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((thisLine = br.readLine()) != null) {
                xmlString = xmlString + thisLine.trim();
            }
            br.close();

            ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlString.getBytes());

            // napravi DOM objekte
            docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            docBuilder = docFactory.newDocumentBuilder();
            domImpl = docBuilder.getDOMImplementation();
            xmlDocument = docBuilder.parse(xmlStream);

            // sada treba izvuci iz xml-a vrijednosti za pojedine varijable
            Element xmlPoslovniProstorZahtjev = xmlDocument.getDocumentElement();
            NodeList nList = xmlPoslovniProstorZahtjev.getChildNodes();

            // prefiks
            prefix = xmlPoslovniProstorZahtjev.getPrefix();

            // IdPoruke
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":IdPoruke");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    idPoruke = xmlTag.getTextContent();
                }
            }

            // Datum i vrijeme
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":DatumVrijeme");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    datumVrijeme = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss", Locale.ENGLISH).parse(xmlTag.getTextContent());
                }
            }

            // oib
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":Oib");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    oib = xmlTag.getTextContent();
                }
            }

            // oznaka poslovnog prostora
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":OznPoslProstora");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    oznakaPoslovnogProstora = xmlTag.getTextContent();
                }
            }

            // ulica
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":Ulica");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    ulica = xmlTag.getTextContent();
                }
            }

            // kucni broj
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":KucniBroj");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    kucniBroj = xmlTag.getTextContent();
                }
            }

            // kucni broj, dodatak
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":KucniBrojDodatak");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    dodatakKucnomBroju = xmlTag.getTextContent();
                }
            }

            // postanski broj
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":BrojPoste");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    brojPoste = xmlTag.getTextContent();
                }
            }

            // naselje
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":Naselje");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    naselje = xmlTag.getTextContent();
                }
            }

            // opcina
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":Opcina");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    opcina = xmlTag.getTextContent();
                }
            }

            // radno vrijeme
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":RadnoVrijeme");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    radnoVrijeme = xmlTag.getTextContent();
                }
            }

            // datum pocetka primjene
            nList = xmlPoslovniProstorZahtjev.getElementsByTagName(prefix + ":DatumPocetkaPrimjene");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    datumPocetkaPrimjene = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).parse(xmlTag.getTextContent());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("loadRacun() Exception!");
        }

    }

    public void sendPoslovniPorostor(final int connectTimeOut) {

        try {

            // izgradi SOAP objekt
            MessageFactory msgFactory = MessageFactory.newInstance();
            // SOAP poruka objekt     	 
            SOAPMessage reqMsg = msgFactory.createMessage();
            SOAPPart soapPart = reqMsg.getSOAPPart();

            // SOAP omotnica
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            // dodaj Apis-IT namespace
            soapEnvelope.addNamespaceDeclaration(soapEnvelope.getPrefix(), apisURI);

            // SOAP tijelo poruke
            SOAPBody soapBody = soapEnvelope.getBody();
            // dodaj sadrzaj u soap poruku generiran iz buildRacun()
            soapBody.addDocument(xmlDocument);

//            // povezivanje na url
//            SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
//            SOAPMessage resMsg = con.call(reqMsg, sURL);
//            con.close();

//**********************************************
            // povezivanje na url
            SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
            URL url = new URL(sURL);
            URLStreamHandler handler = new URLStreamHandler() {
                        @Override
                        protected URLConnection openConnection(URL url) throws IOException {
                            URL target = new URL(url.toString());
                            URLConnection connection = target.openConnection();
                            // Connection settings
                            connection.setConnectTimeout(connectTimeOut * 1000); // 10 sec
                            connection.setReadTimeout(connectTimeOut * 6 * 1000); // 1 min
                            return (connection);
                        }
                    };
             URL endpoint = new URL(url, sURL, handler);
            SOAPMessage resMsg = con.call(reqMsg, endpoint);
            con.close();
  //**********************************************
            
            
            // iskoristi SOAP objekte iz odgovora			
            soapPart = resMsg.getSOAPPart();
            soapEnvelope = soapPart.getEnvelope();
            soapBody = soapEnvelope.getBody();

            // soap odgovor pridruzi dokumentu kako bi se mogao ispisati, spremiti itd.
            // npr.
            //   p1.sendPoslovniPorostor();
            //   p1.savePoslovniProstor("pp.res.xml");
            xmlDocument = soapPart;

            // odrada odgovora, zapravo treba samo naci gresku ako je ima
            NodeList nList = null;

            // izvuci greske ako ih ima u odgovoru
            nList = soapBody.getElementsByTagName(prefix + ":Greska");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    NodeList gList = nNode.getChildNodes();
                    for (int i = 0; i < gList.getLength(); i++) {
                        Node gNode = gList.item(i);
                        if (gNode instanceof Element) {
                            Element xmlTag = (Element) gNode;
                            String tagName = xmlTag.getNodeName();
                            String tagValue = xmlTag.getTextContent();
                            if (tagName.equalsIgnoreCase(prefix + ":SifraGreske")) {
                                sifraGreske = tagValue;
                            }
                            if (tagName.equalsIgnoreCase(prefix + ":PorukaGreske")) {
                                porukaGreske = tagValue;
                            }
                        }
                    }
                }
            } else {
                porukaGreske = "";
                sifraGreske = "";
            }

        } catch (Exception e) {
            //e.printStackTrace();
            porukaGreske = e.getMessage();
            sifraGreske = "e001";
        }
    }

    public String dumpDocument() {

        String xmlString = "";

        try {

            // povezi xml dokument sa izvorom za ispis
            docSource = new DOMSource(xmlDocument);

            // Init transformer, to have nice output
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new StringWriter());

            // Transform (format)

            transformer.transform(docSource, result);
            xmlString = result.getWriter().toString();

        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return xmlString;
    }

    public String dumpDocument(DOMSource documentSource) {


        String xmlString = "";

        try {
            // Init transformer, to have nice output
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new StringWriter());

            // Transform (format)
            transformer.transform(documentSource, result);
            xmlString = result.getWriter().toString();

        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return xmlString;
    }

    /*
     * private boolean validateSignature(NodeList nl) {
     *
     * String providerName = System.getProperty("jsr105Provider",
     * "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
     *
     * try {
     *
     * //NodeList nl = xmlDoc.getElementsByTagNameNS(XMLSignature.XMLNS, //
     * "Signature");
     *
     * if (nl.getLength() == 0) { throw new Exception("Cannot find Signature
     * element."); }
     *
     * XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM",
     * (Provider) Class.forName(providerName).newInstance());
     *
     * DOMValidateContext valContext = new DOMValidateContext (new
     * X509KeySelector(keyStore), nl.item(0));
     *
     * XMLSignature signature = factory.unmarshalXMLSignature(valContext);
     *
     * return signature.validate(valContext);
     *
     * } catch (Exception e) { // TODO Auto-generated catch block
     * e.printStackTrace(); }
     *
     * return false;
     *
     * }
     */
    @SuppressWarnings("rawtypes")
    public void loadKeyStore(String javaKeyStoreFile, String keyStorePassword, String keyAlias, String keyPassword) {

        Key key = null;

        // objekt keyStore, koriste proc. za potpisivanje i provjeru potpisa,
        //  i procedura za izracun zastitnog koda
        try {
            // ucitaj jks datoteku
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream(javaKeyStoreFile), keyStorePassword.toCharArray());

            // uzmi kljuc iz jks datoteke
            key = keyStore.getKey(keyAlias, keyPassword.toCharArray());
            // ocekujem privatni kljuc pohranjen u jks datoteci
            if (key instanceof PrivateKey) {
                // postavi globalne objekte tipa Key
                cert = (X509Certificate) keyStore.getCertificate(keyAlias);
                publicKey = cert.getPublicKey();
                privateKey = (PrivateKey) key;

                if (cert.getSubjectAlternativeNames() != null) {
                    Collection altNames = cert.getSubjectAlternativeNames();
                    List item = (List) altNames.iterator().next();
                    email = (String) item.get(1);
                }

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @SuppressWarnings("rawtypes")
    public void loadKeyStore(String keyFile, String keyPassword) {

        PrivateKeyEntry keyEntry = null;

        // objekt keyStore, koriste proc. za potpisivanje i provjeru potpisa,
        //  i procedura za izracun zastitnog koda
        try {
            // ucitaj jks datoteku
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(keyFile), keyPassword.toCharArray());

            // uzmi kljuc iz pfx datoteke
            keyEntry = (PrivateKeyEntry) keyStore.getEntry(keyStore.aliases().nextElement(),
                    new KeyStore.PasswordProtection(keyPassword.toCharArray()));

            cert = (X509Certificate) keyEntry.getCertificate();

            if (cert.getSubjectAlternativeNames() != null) {
                Collection altNames = cert.getSubjectAlternativeNames();
                List item = (List) altNames.iterator().next();
                email = (String) item.get(1);
            }

            publicKey = cert.getPublicKey();
            privateKey = keyEntry.getPrivateKey();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
