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
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
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
import javax.xml.parsers.ParserConfigurationException;
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

/**
 * <H1>XMLRacun</H1>
 * <BR>
 * Izrada xml poruke sa stavkama za slanje prema PU.<BR>
 * Podrzava:<BR>
 * <UL>
 *  <LI>ucitavanje kljuca iz JKS ili PKCS12 formata</LI>
 *  <LI>postavljanje OIBa firme i OIBa blagajnika</LI>
 *  <LI>postavljanje <I>u sustavu pdva</I> oznaku</LI>
 *  <LI>URL adresu PU</LI>
 *  <LI>broj racuna, poslovni prostor, naplatni uredjaj</LI>
 *  <LI>dodavanje i uklanjanje pdv i pnp stavki, svaku zasebno</LI>
 *  <LI>postavljanje ukupnog iznosa na racunu</LI>
 *  <LI>nacin placanja</LI>
 *  <LI>zastitni kod</LI>
 *  <LI>JIR</LI>
 *  <LI>timeout kod slanja racuna</LI>
 *  <LI>ucitavanje racuna iz xml datoteke</LI>
 *  <LI>storniranje racuna</LI>
 *  <LI>izgradnja xml racuna</LI>
 *  <LI>potpis racuna</LI>
 *  <LI>slanje racuna u PU</LI>
 * 
 * </UL>
 * <B>NAPOMENA:</B>PU prima decimalne brojeve sa <B>tockom</B> a ne zarezom,
 * pa treba koristiti en_US lokalizaciju:<BR>
 * <I>Locale.setDefault(new Locale("en", "US"));</I>
 * <H2>Primjeri</H2>
 * <BR>
 * Slanje racuna:
 * <PRE>
 *  // Napravi xml racun
 *  XMLRacun x1 = new XMLRacun();
 *
 *  // Postavi neke osnovne parametre
 *  x1.loadKeyStore("fiskal 1..p12", "zaporka1");
 *  x1.setOib("17000498571");
 *  x1.setUSustPdv(true);
 *  x1.setOibOper("67236335772");
 *  x1.setURL("https://cis.porezna-uprava.hr:8449/FiskalizacijaService");
 *  x1.setBrRac(1, "POSL1", "1");
 *  // Dodaj neke pdv i pnp stavke
 *  x1.addPDV(25.00, 100.00, 25.00);
 *  x1.addPDV(25.00, 200.00, 50.00);
 *  x1.addPNP(3.00, 100.00, 3.00);
 *  // Ukupni iznos na racunu
 *  x1.setIznosUkupno(478.00);
 *  // Nacin placanja
 *  x1.setNacinPlacanja("G");
 *  // Izgradnja, spremanje u xml, potpis i slanje racuna sa timeoutom 10 sec.
 *  x1.buildRacun();
 *  x1.saveRacun("Racuni/racun1.xml");
 *  x1.signRacun();
 *  x1.sendRacun(10);
 *  // Dohavati zastitni kod i JIR
 *  zkod = x1.getZastitniKod();
 *  jir = x1.getJIR();
 * </PRE>
 * 
 * Ucitavanje racuna iz xml-a:
 * <PRE>
 *  x1 = new XMLRacun();
 *  x1.loadRacun("racun1.xml");
 * </PRE>
 * Nakon ucitavanja, takav racun se moze koristiti za ponovno slanje, storniranje, itd.<BR>
 * <BR>
 * Storniranje racuna:
 * <PRE>
 * XMLRacun x1 = new XMLRacun();
 * 
 * x1.loadRacun("racun1.xml");
 * // ucitaj priv. kljuc
 * x1.loadKeyStore("fiskal 1.p12", "zaporka1");
 * // URL - ovo je malo nespretno, jer se moze dogoditi da ne ode na pravi server
 * //  gdje je i originalno racun napravljen
 * x1.setURL("https://cis.porezna-uprava.hr:8449/FiskalizacijaService");
 * // postavi storniranje
 * x1.stornirajRacun();
 * // izgradi ponovno xml racun
 * x1.buildRacun();
 * // spremi u xml datoteku
 * x1.saveRacun("racun1.storno.xml");
 * // potpisi
 * x1.signRacun();
 * // posalji racun, 10 sec. timeout
 * x1.sendRacun(10);
 * // spremi racun
 * x1.saveRacun("racun1.storno.response.xml");
 * </PRE>
 * Odmah nakon instanciranja racuna sa <I>new XMLRacun()</I>, treba postaviti kljuc
 * i zaporku sa <I>loadKeyStore(...)</I> funkcijom. 
 * 
 * @author eigorde
 */
public class XMLRacun {

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
    // email adresa iz privatnog kljuca
    private static String email;
    // URL CIS poluzitelja
    private String sURL;
    // ID poruke
    private String idPoruke;
    // Datum i vrijeme
    private Date datumVrijeme;
    // oib firme i blagajnika
    private String oib;
    private String oibOper;
    // broj racuna
    private int brOznRac;
    private String oznPosPr;
    private String oznNapUr;
    // ukupni iznos na racunu za platiti
    private double iznosUkupno;
    // kako se placa:
    //  G - gotovinom, K -karticom, C - cekom, T - transakcija, O - ostalo
    private String nacinPlacanja;
    // naknadno dostavljen racun
    private boolean naknDost;
    // U sustavu pdv-a
    private boolean uSustPdv;
    // Jedinstveni broj racuna
    private String jir;
    // sifre gresaka od CISa
    private String sifraGreske;
    private String porukaGreske;
    // zastitni kod poruke (racuna)
    private String zastitniKod;
    // lista PDV stavki na racunu
    private ArrayList<Double> arrayPDV = new ArrayList<Double>();
    // lista PNP stavki na racunu
    private ArrayList<Double> arrayPNP = new ArrayList<Double>();
    // suma pdv-a i pnp-a, pogledati addPDV() i addPNP()
    private double ukupnoPdv;
    private double ukupnoPnp;

    /**
     * ID element poruke u zaglavlju xml-a. Postavlja se kao uuid vrijednost
     * kod izrade novog racuna, ili se ucitava iz postojeceg xml racuna.
     * @return id poruke ili xml racuna
     */
    public String getIdPoruke() {
        return idPoruke;
    }

    /**
     * Datum racuna u zaglavlju <I>Racun</I> xml taga. Postavlja se kao trenutna vrijednost
     * kod izrade novog racuna, ili se ucitava iz postojeceg xml racuna.
     * @return datum poruke ili xml racuna
     */
    public Date getDatVrijeme() {
        return datumVrijeme;
    }
    
    /**
     * Datum poruke u zaglavlju xml-a, <I>RacunZahtjev</I> tag. Postavlja se kao trenutna vrijednost
     * kod izrade novog racuna, ili se ucitava iz postojeceg xml racuna.
     * @return datum poruke ili xml racuna
     */
    public String getDatumVrijeme() {
        String dateTime = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss", Locale.ENGLISH).format(datumVrijeme);
        return dateTime;
    }

    /**
     * Ukupni PDV (porez na dodanu vrijednost).
     * @return zbroj svih stavik na racunu
     */
    public double getUkupnoPdv() {
        return ukupnoPdv;
    }
    
    /**
     * Ukupni PNP (porez na potrosnju).
     * @return zbroj svih stavik na racunu
     */
    public double getUkupnoPnp() {
        return ukupnoPnp;
    }

    /**
     * ApisIT-ov namespace za xml poruku.
     * @return <I>http://www.apis-it.hr/fin/2012/types/f73</I>
     */
    public String getApisURI() {
        return apisURI;
    }

    /**
     * ApisIT-ov namespace i prefiks za xml poruku.
     * @param URI npr. <I>http://www.apis-it.hr/fin/2012/types/f73</I>
     */    
    public void setApisURI(String URI) {
        apisURI = URI;
    }

    /**
     * Prefiks za xml poruku.
     * @return prefiks npr. <I>tns</I>
     */ 
    public String getPrefix() {
        return prefix;
    }
    
    /**
     * Prefiks za xml poruku.
     * @param prefiks npr. <I>tns</I>
     */ 
    public void setPrefix(String prefiks) {
        prefix = prefiks;
    }

    /**
     * URL adresa za slanje racuna.
     * @return url adresa na koju se salje xml poruka soap protokolom,
     * npr. <I>https://cistest.apis-it.hr:8449/FiskalizacijaServiceTest</I>
     */ 
    public String getURL() {
        return sURL;
    }

    /**
     * Ukupni iznos na racunu. Ovaj parametar treba postaviti jer se ne racuna
     * automatski pri dodavanju stavki na racun.
     * @return ukupni iznos na racunu
     */
    public double getIznosUkupno() {
        return iznosUkupno;
    }

    /**
     * Ukupni iznos na racunu. Ovaj parametar treba postaviti jer se ne racuna
     * automatski pri dodavanju stavki na racun.
     * @param iznos ukupni iznos na racunu
     */
    public void setIznosUkupno(double iznos) {
        iznosUkupno = iznos;
    }

    /**
     * Jedinstveni identifikator racuna. Dobiva se nakon slanja racuna ili ucitavanja
     * vec poslanog racuna iz xml datoteke.
     * @return jir ili prazan niz ako je doslo do greske kod slanja racuna
     */ 
    public String getJir() {
        return jir;
    }
    
    /**
     * URL adresa za slanje racuna.
     * @param URL url adresa na koju se salje xml poruka soap protokolom,
     * npr. <I>https://cistest.apis-it.hr:8449/FiskalizacijaServiceTest</I>
     */ 
    public void setURL(String URL) {
        sURL = URL;
    }

    /**
     * Da li je racun bio naknadno dostavljen u PU.
     * @return <I>true</I> ako je racun bio naknadno dostavljen
     */
    public boolean getNaknadnoDostavljen() {
        return naknDost;
    }

    /**
     * Postavlja oznaku da se racun naknadno dostavlja u PU.
     * 
     */
    public void setNaknadonDostavljen() {
        naknDost = true;
    }

    /**
     * Da li je racun u sustavu PDVa.
     * @return <I>true</I> ako je racun u sustavu PDVa
     */
    public boolean isUSustPdv() {
        return uSustPdv;
    }
    
    /**
     * Da li je racun u sustavu PDVa.
     * @param uSustPdv <I>true</I> ako je racun u sustavu PDVa
     */
    public void setUSustPdv(boolean uSustPdv) {
        this.uSustPdv = uSustPdv;
    }

    /**
     * Broj racuna.
     * @return broj racuna
     */
    public int getBrRac() {
        return brOznRac;
    }

    /**
     * Oznaka poslovnog prostora.
     * @return oznaka, npr. <I>POSLOV1</I>
     */
    public String getPoslovniProstor() {
        return oznPosPr;
    }

    /**
     * Oznaka naplatnog uredjaja.
     * @return oznaka, npr. <I>1</I>
     */
    public String getNaplatniUredjaj() {
        return oznNapUr;
    }

    /**
     * Postavlja broj racuna, oznaku poslovnog prostora i naplatnog uredjaja.
     * @param brojRacuna broj racuna, npr. <I>1</I>
     * @param oznakaPoslovnogProstora ime poslovnice, npr. <I>POSL1</I>
     * @param oznakaNaplatnogUredjaja redni broj blagajne, npr. <I>1</I>
     */
    public void setBrRac(int brojRacuna, String oznakaPoslovnogProstora, String oznakaNaplatnogUredjaja) {
        brOznRac = brojRacuna;
        oznPosPr = oznakaPoslovnogProstora;
        oznNapUr = oznakaNaplatnogUredjaja;
    }


    /**
     * Nacin placanja.
     * @return oznaku nacina placanja. Npr. N - novcanice, T - transakcija, itd.
     */
    public String getNacinPlacanja() {
        return nacinPlacanja;
    }
    /**
     * Nacin placanja.
     * @param nacinPlacanjaRacuna oznaka nacina placanja. Npr. N - novcanice, T - transakcija, itd.
     */
    public void setNacinPlacanja(String nacinPlacanjaRacuna) {
        nacinPlacanja = nacinPlacanjaRacuna;
    }

    /**
     * Oib firme.
     * @return oib
     */
    public String getOib() {
        return oib;
    }
    /**
     * Oib firme.
     * @param OIB oib
     */
    public void setOib(String OIB) {
        oib = OIB;
    }
    
    /**
     * Oib blagajnika.
     * @return oib blagajnika
     */
    public String getOibOper() {
        return oibOper;
    }
    /**
     * Oib blagajnika.
     * @param OIB oib blagajnika
     */
    public void setOibOper(String OIB) {
        oibOper = OIB;
    }

    /**
     * Zastitni kod. Postavlja se kod izrade racuna automatski, ili kod ucitavanja iz xml-a.
     * @return zastitni kod, npr. <I>8eac8c7bbae046a15eabc35deaa4ef95</I>
     */
    public String getZastitniKod() {
        return zastitniKod;
    }

    /**
     * Email adresa korisnika programa.
     * @return email adresa
     */
    public String getEmail() {
        return email;
    }

    /**
     * Greska u slucaju slanja xml poruke.
     * @return oznaka i opis greske
     */
    public String getGreska() {

        if (sifraGreske != "" || porukaGreske != "") {
            return sifraGreske + ": " + porukaGreske;
        } else {
            return "";
        }

    }

    /**
     * Dodaje pdv stavku na racun.
     * @param stopa stopa u postotku, npr. <I>24<I/>
     * @param osnovica iznos osnovice
     * @param iznos iznos poreza
     */
    public void addPDV(double stopa, double osnovica, double iznos) {

        arrayPDV.add(stopa);
        arrayPDV.add(osnovica);
        arrayPDV.add(iznos);

        ukupnoPdv = ukupnoPdv + iznos;

    }
    /**
     * Dodaje pnp stavku na racun.
     * @param stopa stopa u postotku, npr. <I>3<I/>
     * @param osnovica iznos osnovice
     * @param iznos iznos poreza
     */
    public void addPNP(double stopa, double osnovica, double iznos) {

        arrayPNP.add(stopa);
        arrayPNP.add(osnovica);
        arrayPNP.add(iznos);

        ukupnoPnp = ukupnoPnp + iznos;

    }

    /**
     * Ocisti sve stavke sa popisa PDV stavki.
     */
    public void erasePDV() {
        ukupnoPdv = 0;
        arrayPDV.clear();
    }
    
    /**
     * Ocisti sve stavke sa popisa PNP stavki.
     */
    public void erasePNP() {
        ukupnoPnp = 0;
        arrayPDV.clear();
    }

    /**
     * Ukloni jednu stavku.
     * @param stavka redni broj stavke
     */
    public void delPDV(int stavka) {

        // svaka 3 elementa cine jednu stavku
        //  (0, 1, 2)
        //  (3, 4, 5)
        //  ...
        // arg. stavka se ovdje odnosi na redni br. stavke, i počinje sa 0

        // smanji ukupni iznos pdv-a/pnp-a za iznos iz stavke
        ukupnoPdv = ukupnoPdv - arrayPDV.get(stavka + 2);

        arrayPDV.remove(3 * stavka); // stopa
        arrayPDV.remove(3 * stavka); // osnovica
        arrayPDV.remove(3 * stavka); // iznos


    }
    
    /**
     * Ukloni jednu stavku.
     * @param stavka redni broj stavke
     */
    public void delPNP(int stavka) {

        // svaka 3 elementa cine jednu stavku
        //  (0, 1, 2)
        //  (3, 4, 5)
        //  ...
        // arg. stavka se ovdje odnosi na redni br. stavke, i počinje sa 0

        // smanji ukupni iznos pdv-a/pnp-a za iznos iz stavke
        ukupnoPnp = ukupnoPnp - arrayPNP.get(stavka + 2);

        arrayPNP.remove(3 * stavka);
        arrayPNP.remove(3 * stavka);
        arrayPNP.remove(3 * stavka);


    }

    /**
     * Inicijalizacija xml racuna.
     */
    public XMLRacun() {

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

        // br. racuna, poslovnice i naplatnog uredjaja
        brOznRac = 1;
        oznNapUr = "1";
        oznPosPr = "POSLOVNICA1";

        // OIBi
        oib = "123456789";
        oibOper = "123456789";

        // ukupni iznos za naplatiti
        iznosUkupno = 0;

        // sume pdv-a i pnp-a
        ukupnoPdv = 0;
        ukupnoPnp = 0;

        // nacin placanja
        nacinPlacanja = "G";

        // naknadno dostavljen racun
        naknDost = false;
        
        // obicno je u sustavu pdv-a
        uSustPdv = true;

        // zastitni kod se ne moze u startu izracunati
        zastitniKod = "";

        // jedinstveni broj racuna, odgovor CISa, i greske
        jir = "";
        sifraGreske = "";
        porukaGreske = "";

        // kljucarnik
        keyStore = null;
        publicKey = null;
        privateKey = null;
        cert = null;

    }

    /**
     * Izgradi xml racun na temelju postavljeni vrijednosti.
     * Prije izgradnje obavezno postavite sve potrebne parametre (broj racuna, oib-e, pdv i pnp stavke, ...)
     * ili ucitajte racun iz xml datoteke.
     */
    public void buildRacun() {

        try {

            // napravi DOM objekte
            docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            docBuilder = docFactory.newDocumentBuilder();
            domImpl = docBuilder.getDOMImplementation();
            xmlDocument = domImpl.createDocument(apisURI, "RacunZahtjev", null);

            // datum i vrijeme u formatu prema teh.spec. v1.1
            if (datumVrijeme == null) {
                // ovo je slucaj da datumVrijeme nije postavljeno
                //  npr. ako se koristi buildRacun() bez loadRacun()
                datumVrijeme = new Date();
            }
            String dateTime = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss", Locale.ENGLISH).format(datumVrijeme);

            // format za iznosUkupno
            DecimalFormat dFormat = new DecimalFormat("0.00");

            // zastitni kod racuna, 
            //  f(oib, datum, vrijeme, privatni kljuc, iznosUkupno, brRac)
            if (zastitniKod.equalsIgnoreCase("")) {
                // ako je zastitniKod prazan, onda ga izracunaj
                //  inace, mozda je postavljen sa loadRacun() procedurom
                zastitniKod = izracunajZastitniKod();
            }


            // root xml element
            Element xmlRacunZahtjev = xmlDocument.getDocumentElement();
            xmlRacunZahtjev.setPrefix(prefix);
            // id atribut za potpisivanje, pogledati u signRacun() referencu
            xmlRacunZahtjev.setAttribute("Id", "signXmlId");
            xmlRacunZahtjev.setIdAttribute("Id", true);
            //xmlRacunZahtjev.setAttributeNS(apisURI, "xsi:schemaLocation", "http://www.apis-it.hr/fin/2012/types/f73 ../schema/FiskalizacijaSchema.xsd");
            xmlRacunZahtjev.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

            Element xmlZaglavlje = xmlDocument.createElementNS(apisURI, "Zaglavlje");
            xmlZaglavlje.setPrefix(prefix);

            Element xmlIdPoruke = xmlDocument.createElementNS(apisURI, "IdPoruke");
            xmlIdPoruke.setPrefix(prefix);

            Element xmlDatumVrijeme = xmlDocument.createElementNS(apisURI, "DatumVrijeme");
            xmlDatumVrijeme.setPrefix(prefix);

            Text xmlIdPorukeText = xmlDocument.createTextNode(idPoruke);
            Text xmlDatumVrijemeText = xmlDocument.createTextNode(new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss", Locale.ENGLISH).format(new Date()));

            xmlDatumVrijeme.appendChild(xmlDatumVrijemeText);
            xmlIdPoruke.appendChild(xmlIdPorukeText);

            xmlZaglavlje.appendChild(xmlIdPoruke);
            xmlZaglavlje.appendChild(xmlDatumVrijeme);

            Element xmlRacun = xmlDocument.createElementNS(apisURI, "Racun");
            xmlRacun.setPrefix(prefix);

            Element xmlOib = xmlDocument.createElementNS(apisURI, "Oib");
            xmlOib.setPrefix(prefix);

            Text xmlOibText = xmlDocument.createTextNode(oib);

            Element xmlUSustPdv = xmlDocument.createElementNS(apisURI, "USustPdv");
            xmlUSustPdv.setPrefix(prefix);

            Text xmlUSustPDVText = xmlDocument.createTextNode(String.valueOf(uSustPdv));

            Element xmlDatVrijeme = xmlDocument.createElementNS(apisURI, "DatVrijeme");
            xmlDatVrijeme.setPrefix(prefix);

            Text xmlDatVrijemeText = xmlDocument.createTextNode(dateTime);

            Element xmlOznSlijed = xmlDocument.createElementNS(apisURI, "OznSlijed");
            xmlOznSlijed.setPrefix(prefix);

            Text xmlOznSlijedText = xmlDocument.createTextNode("P");

            xmlOib.appendChild(xmlOibText);
            xmlUSustPdv.appendChild(xmlUSustPDVText);
            xmlDatVrijeme.appendChild(xmlDatVrijemeText);
            xmlOznSlijed.appendChild(xmlOznSlijedText);

            xmlRacun.appendChild(xmlOib);
            xmlRacun.appendChild(xmlUSustPdv);
            xmlRacun.appendChild(xmlDatVrijeme);
            xmlRacun.appendChild(xmlOznSlijed);

            Element xmlBrRac = xmlDocument.createElementNS(apisURI, "BrRac");
            xmlBrRac.setPrefix(prefix);

            Element xmlBrOznRac = xmlDocument.createElementNS(apisURI, "BrOznRac");
            xmlBrOznRac.setPrefix(prefix);

            Text xmlBrOznRacText = xmlDocument.createTextNode(String.valueOf(brOznRac));

            Element xmlOznPosPr = xmlDocument.createElementNS(apisURI, "OznPosPr");
            xmlOznPosPr.setPrefix(prefix);

            Text xmlOznPosPrText = xmlDocument.createTextNode(oznPosPr);

            Element xmlOznNapUr = xmlDocument.createElementNS(apisURI, "OznNapUr");
            xmlOznNapUr.setPrefix(prefix);

            Text xmlOznNapUrText = xmlDocument.createTextNode(oznNapUr);

            xmlBrOznRac.appendChild(xmlBrOznRacText);
            xmlOznPosPr.appendChild(xmlOznPosPrText);
            xmlOznNapUr.appendChild(xmlOznNapUrText);
            xmlBrRac.appendChild(xmlBrOznRac);
            xmlBrRac.appendChild(xmlOznPosPr);
            xmlBrRac.appendChild(xmlOznNapUr);

            xmlRacun.appendChild(xmlBrRac);

            // PDV stavka na racunu
            if (arrayPDV.size() > 0) {
                Element xmlPdv = xmlDocument.createElementNS(apisURI, "Pdv");
                xmlPdv.setPrefix(prefix);

                for (int idx = 0; idx < arrayPDV.size(); idx = idx + 3) {

                    Element xmlPorez = xmlDocument.createElementNS(apisURI, "Porez");
                    xmlPorez.setPrefix(prefix);
                    Element xmlStopa = xmlDocument.createElementNS(apisURI, "Stopa");
                    xmlStopa.setPrefix(prefix);
                    Text xmlStopaText = xmlDocument.createTextNode(dFormat.format(arrayPDV.get(idx)));
                    Element xmlOsnovica = xmlDocument.createElementNS(apisURI, "Osnovica");
                    xmlOsnovica.setPrefix(prefix);
                    Text xmlOsnovicaText = xmlDocument.createTextNode(dFormat.format(arrayPDV.get(idx + 1)));
                    Element xmlIznos = xmlDocument.createElementNS(apisURI, "Iznos");
                    xmlIznos.setPrefix(prefix);
                    Text xmlIznosText = xmlDocument.createTextNode(dFormat.format(arrayPDV.get(idx + 2)));

                    xmlStopa.appendChild(xmlStopaText);
                    xmlOsnovica.appendChild(xmlOsnovicaText);
                    xmlIznos.appendChild(xmlIznosText);
                    xmlPorez.appendChild(xmlStopa);
                    xmlPorez.appendChild(xmlOsnovica);
                    xmlPorez.appendChild(xmlIznos);
                    xmlPdv.appendChild(xmlPorez);

                }

                xmlRacun.appendChild(xmlPdv);
            }
            //

            // PNP stavka na racunu
            if (arrayPNP.size() > 0) {
                Element xmlPnp = xmlDocument.createElementNS(apisURI, "Pnp");
                xmlPnp.setPrefix(prefix);

                for (int idx = 0; idx < arrayPNP.size(); idx = idx + 3) {

                    Element xmlPorez = xmlDocument.createElementNS(apisURI, "Porez");
                    xmlPorez.setPrefix(prefix);
                    Element xmlStopa = xmlDocument.createElementNS(apisURI, "Stopa");
                    xmlStopa.setPrefix(prefix);
                    Text xmlStopaText = xmlDocument.createTextNode(dFormat.format(arrayPNP.get(idx)));
                    Element xmlOsnovica = xmlDocument.createElementNS(apisURI, "Osnovica");
                    xmlOsnovica.setPrefix(prefix);
                    Text xmlOsnovicaText = xmlDocument.createTextNode(dFormat.format(arrayPNP.get(idx + 1)));
                    Element xmlIznos = xmlDocument.createElementNS(apisURI, "Iznos");
                    xmlIznos.setPrefix(prefix);
                    Text xmlIznosText = xmlDocument.createTextNode(dFormat.format(arrayPNP.get(idx + 2)));

                    xmlStopa.appendChild(xmlStopaText);
                    xmlOsnovica.appendChild(xmlOsnovicaText);
                    xmlIznos.appendChild(xmlIznosText);
                    xmlPorez.appendChild(xmlStopa);
                    xmlPorez.appendChild(xmlOsnovica);
                    xmlPorez.appendChild(xmlIznos);
                    xmlPnp.appendChild(xmlPorez);

                }

                xmlRacun.appendChild(xmlPnp);
            }
            //


            Element xmlIznosUkupno = xmlDocument.createElementNS(apisURI, "IznosUkupno");
            xmlIznosUkupno.setPrefix(prefix);

            Text xmlIznosUkupnoText = xmlDocument.createTextNode(dFormat.format(iznosUkupno));

            Element xmlNacinPlac = xmlDocument.createElementNS(apisURI, "NacinPlac");
            xmlNacinPlac.setPrefix(prefix);

            Text xmlNacinPlacText = xmlDocument.createTextNode(nacinPlacanja);

            Element xmlOibOper = xmlDocument.createElementNS(apisURI, "OibOper");
            xmlOibOper.setPrefix(prefix);

            Text xmlOibOperText = xmlDocument.createTextNode(oibOper);

            Element xmlZastKod = xmlDocument.createElementNS(apisURI, "ZastKod");
            xmlZastKod.setPrefix(prefix);

            Text xmlZastKodText = xmlDocument.createTextNode(zastitniKod);

            Element xmlNakDost = xmlDocument.createElementNS(apisURI, "NakDost");
            xmlNakDost.setPrefix(prefix);

            Text xmlNakDostText = xmlDocument.createTextNode(String.valueOf(naknDost));

            xmlIznosUkupno.appendChild(xmlIznosUkupnoText);
            xmlNacinPlac.appendChild(xmlNacinPlacText);
            xmlOibOper.appendChild(xmlOibOperText);
            xmlZastKod.appendChild(xmlZastKodText);
            xmlNakDost.appendChild(xmlNakDostText);

            xmlRacun.appendChild(xmlIznosUkupno);
            xmlRacun.appendChild(xmlNacinPlac);
            xmlRacun.appendChild(xmlOibOper);
            xmlRacun.appendChild(xmlZastKod);
            xmlRacun.appendChild(xmlNakDost);

            xmlRacunZahtjev.appendChild(xmlZaglavlje);
            xmlRacunZahtjev.appendChild(xmlRacun);

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Potpisi xml racun. Prije koristenja ove funkcije, treba 
     * pozvati <I>buildRacun()</I> da se dobije xml oblik racuna.
     */
    @SuppressWarnings("unchecked")
    public void signRacun() {

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

    /**
     * Sprema racun u xml datoteku. Prije koristenja ove funkcije, treba 
     * pozvati <I>buildRacun()</I> da se dobije xml oblik racuna.
     * @param fileName naziv datoteke
     */
    public void saveRacun(String fileName) {

        try {
            String xmlString = dumpDocument();
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            out.write(xmlString);
            out.close();
        } catch (IOException e) {
            System.out.println("IO Exception!");
        }

    }

    /**
     * Ucitava odgovor PU iz xml datoteke. Osnovni zadatak je da se ucita JIR iz odgovora PU,
     * u slucaju da xml na sadrzi JIR, tada se u JIR postavlja prazan niz "".
     * 
     * @param fileName naziv datoteke
     */
    public void loadRacunOdgovor(String fileName) {

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
            Element xmlRacunOdgovor = xmlDocument.getDocumentElement();
            NodeList nList = xmlRacunOdgovor.getChildNodes();

            // prefiks, ovo ne radi, jer je u odgovoru prefix = soap, a ne tns
            //prefix = xmlRacunOdgovor.getPrefix();
            // pa cemo koristiti Apis-ITov prefiks            
            prefix = "tns";

            // Jir
            nList = xmlRacunOdgovor.getElementsByTagName(prefix + ":Jir");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    jir = xmlTag.getTextContent();
                }
            } else {
                jir = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("loadRacunOdgovor() Exception!");
        }

    }

    /**
     * Ucitava racun iz xml datoteke. Kod ucitavanja se postavlja:
     * <UL>
     *  <LI>id poruke</LI>
     *  <LI>datum i vrijeme racuna</LI>
     *  <LI>oib</LI>
     *  <LI>u sustavu pdv-a</LI>
     *  <LI>broj racuna, oznaka poslovnog prostora i naplatnog uredjaja</LI>
     *  <LI>oib</LI>
     *  <LI>stavke: pdv i pnp</LI>
     *  <LI>ukupni iznos</LI>
     *  <LI>nacin placanja</LI>
     *  <LI>oib blagajnika</LI>
     *  <LI>zastitni kod</LI>
     *  <LI>naknadno dostavljen</LI>
     * </UL>
     * @param fileName naziv datoteke
     */
    public void loadRacun(String fileName) {

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
            Element xmlRacunZahtjev = xmlDocument.getDocumentElement();
            NodeList nList = xmlRacunZahtjev.getChildNodes();

            // prefiks
            prefix = xmlRacunZahtjev.getPrefix();

            // IdPoruke
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":IdPoruke");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    idPoruke = xmlTag.getTextContent();
                }
            }

            // Datum i vrijeme, iz <Racun> taga, a ne iz <Zaglavlje> dijela xml-a
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":DatVrijeme");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    datumVrijeme = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss", Locale.ENGLISH).parse(xmlTag.getTextContent());
                }
            }

            // oib
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":Oib");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    oib = xmlTag.getTextContent();
                }
            }

            // U sustavu pdv-a
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":USustPdv");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    uSustPdv = Boolean.parseBoolean(xmlTag.getTextContent());
                }
            }

            // broj racuna
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":BrOznRac");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    brOznRac = Integer.parseInt(xmlTag.getTextContent());
                }
            }
            // poslovnica
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":OznPosPr");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    oznPosPr = xmlTag.getTextContent();
                }
            }
            // broj naplatnog uredjaja
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":OznNapUr");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    oznNapUr = xmlTag.getTextContent();
                }
            }

            // izvuci PDV i PNP stavke racuna
            arrayPDV.clear(); ukupnoPdv = 0;
            arrayPNP.clear(); ukupnoPnp = 0;
            iznosUkupno = 0;
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":Porez");
            for (int i = 0; i < nList.getLength(); i++) {
                // <Porez>
                Node nNode = nList.item(i);
                if (nNode instanceof Element) {
                    // <Stopa>, <Osnovica>, <Iznos>
                    NodeList gList = nNode.getChildNodes();
                    String tStopa = null;
                    String tOsnovica = null;
                    String tIznos = null;
                    for (int ii = 0; ii < gList.getLength(); ii++) {
                        Node gNode = gList.item(ii);
                        if (gNode instanceof Element) {
                            Element xmlTag = (Element) gNode;
                            String tagName = xmlTag.getNodeName();
                            if (tagName.equalsIgnoreCase(prefix + ":Stopa")) {
                                tStopa = xmlTag.getTextContent();
                            }
                            if (tagName.equalsIgnoreCase(prefix + ":Osnovica")) {
                                tOsnovica = xmlTag.getTextContent();
                            }
                            if (tagName.equalsIgnoreCase(prefix + ":Iznos")) {
                                tIznos = xmlTag.getTextContent();
                            }
                        }
                    }

                    // dohvati <Pdv> ili <Pnp> xml tag koji sadrzi <Porez> 
                    Node parentNode = nNode.getParentNode();

                    if (parentNode.getNodeName().equalsIgnoreCase(prefix + ":Pdv")) {
                        addPDV(Double.parseDouble(tStopa),
                                Double.parseDouble(tOsnovica),
                                Double.parseDouble(tIznos));
                    } else if (parentNode.getNodeName().equalsIgnoreCase(prefix + ":Pnp")) {
                        addPNP(Double.parseDouble(tStopa),
                                Double.parseDouble(tOsnovica),
                                Double.parseDouble(tIznos));
                    }

                }
            }

            // iznos ukupno
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":IznosUkupno");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    iznosUkupno = Double.parseDouble(xmlTag.getTextContent());
                }
            }

            // nacin placanja
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":NacinPlac");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    nacinPlacanja = xmlTag.getTextContent();
                }
            }

            // oib operatera
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":OibOper");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    oibOper = xmlTag.getTextContent();
                }
            }

            // zastitni kod
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":ZastKod");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    zastitniKod = xmlTag.getTextContent();
                }
            }

            // naknadno dostavljen racun
            nList = xmlRacunZahtjev.getElementsByTagName(prefix + ":NakDost");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    naknDost = Boolean.parseBoolean(xmlTag.getTextContent());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("loadRacun() Exception!");
        }

    }

    /**
     * Salje racun u PU. Prije koristenja ove funkcije, treba 
     * pozvati <I>buildRacun()</I> da se dobije xml oblik racuna.
     * @param connectTimeOut max.vrijeme na odgovor iz PU, u sekundama
     */
    public void sendRacun(final int connectTimeOut) {

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

            // povezivanje na url
            //SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
            //SOAPMessage resMsg = con.call(reqMsg, sURL);
            //con.close();

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
                    connection.setConnectTimeout(connectTimeOut * 1000); // u sekundama
                    connection.setReadTimeout(connectTimeOut * 6 * 1000);
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
            //   r1.sendRacun();
            //   r1.saveRacun("racun1.res.xml");
            xmlDocument = soapPart;

            // obrada odgovora
            NodeList nList = null;

            // izvuci Jir iz odgovora (ako je sve ok)
            nList = soapBody.getElementsByTagName(prefix + ":Jir");
            if (nList.getLength() > 0) {
                Node nNode = nList.item(0);
                if (nNode instanceof Element) {
                    Element xmlTag = (Element) nNode;
                    String tagName = xmlTag.getNodeName();
                    String tagValue = xmlTag.getTextContent();
                    if (tagName.equalsIgnoreCase(prefix + ":Jir")) {
                        jir = tagValue;
                    }
                }
            } else {
                jir = "";
            }

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

    /**
     * Stornira racun. Storniranje ide u koracima:<BR>
     * <UL>
     *  <LI>ucitaj xml racun iz datoteke</LI>
     *  <LI>postavi kljuc i zaporku</LI>
     *  <LI>postavi url adresu</LI>
     *  <LI>pozovi <I>stornirajRacun()</I></LI>
     *  <LI>pozovi <I>buildRacun()</I></LI>
     *  <LI>pozovi <I>signRacun()</I></LI>
     *  <LI>pozovi <I>sendRacun()</I></LI>
     * </UL>
     * Primjer se moze vidjeti u dokumentaciji klase.
     */
    public void stornirajRacun() {
    
        // procedura za storniranje bi trebala napraviti:
        //  1. ukupni iznos staviti u neg.vr.
        //  2. stavke pdv-a (osnovicu i iznos) staviti u neg.vr.
        //  3. stavke pnp-a isto
        //  4. ponovno izracunati z.kod
        
        iznosUkupno = -(iznosUkupno);
        
        // uzmi u obzir samo osnovicu i porez
        for(int i = 0; i < arrayPDV.size(); i = i + 3) {
            // prvo za pdv, osnovica i porez svake stavke
            arrayPDV.set(i + 1, -(arrayPDV.get(i) + 1) );
            arrayPDV.set(i + 2, -(arrayPDV.get(i) + 2) );
        }
        // sada za pnp
        for(int i = 0; i < arrayPNP.size(); i = i + 3) {
            // prvo za pdv, osnovica i porez svake stavke
            arrayPNP.set(i + 1, -(arrayPNP.get(i) + 1) );
            arrayPNP.set(i + 2, -(arrayPNP.get(i) + 2) );
        }
        
        zastitniKod = izracunajZastitniKod();
        
        ukupnoPdv = -(ukupnoPdv);
        ukupnoPnp = -(ukupnoPnp);
                
    }
    
    /**
     * Prikaz racuna u xml formatu.
     * @return xml oblik racuna
     */
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
    
    /**
     * Prikaz DOM objekta u xml formatu.
     * @param documentSource izvor za prikaz u xml formatu
     * @return xml sadrzaj DOM objekta
     */
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

    /*private boolean validateSignature(NodeList nl) {

     String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");

     try {
			
     //NodeList nl = xmlDoc.getElementsByTagNameNS(XMLSignature.XMLNS, 
     //		"Signature");

     if (nl.getLength() == 0) {
     throw new Exception("Cannot find Signature element.");
     }

     XMLSignatureFactory factory = 
     XMLSignatureFactory.getInstance("DOM",
     (Provider) Class.forName(providerName).newInstance());

     DOMValidateContext valContext = new DOMValidateContext
     (new X509KeySelector(keyStore), nl.item(0));

     XMLSignature signature = factory.unmarshalXMLSignature(valContext);

     return signature.validate(valContext);

     } catch (Exception e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     }

     return false;

     }*/
    
    /**
     * Ucitava datoteku sa popisom kljuceva.
     * @param javaKeyStoreFile JKS datoteka
     * @param keyStorePassword zaporka
     * @param keyAlias naziv kljuca u JKSu
     * @param keyPassword zaporka kljuca
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

    /**
     * Ucitava kljuc iz datoteke.
     * @param keyFile naziv datoteke
     * @param keyPassword zaporka
     */
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
            publicKey = cert.getPublicKey();
            privateKey = keyEntry.getPrivateKey();

            if (cert.getSubjectAlternativeNames() != null) {
                Collection altNames = cert.getSubjectAlternativeNames();
                List item = (List) altNames.iterator().next();
                email = (String) item.get(1);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Izracunava zastitni kod prema predlozenom algoritmu od strane ApisIT-a.<BR>
     * Prije poziva obavezno postavite datum i vrijeme, broj racuna, oznake posl.prostora i
     * naplatnog uredjaja, te ukupni iznos.
     * @return zastitni kod
     */
    private String izracunajZastitniKod() {
        // algoritam preuzet iz Teh.spec. v1.1

        // za pocetak uzmi oib
        String medjurezultat = oib;

        // datum i vrijeme izdavanja racuna zapisain kao tekst u
        //  formatu 'dd.MM.gggg HH:mm:ss')
        medjurezultat = medjurezultat + new SimpleDateFormat(
                "dd.MM.yyyy HH:mm:ss", Locale.ENGLISH).format(datumVrijeme);
        // dodaj brojcanu oznaku racuna
        medjurezultat = medjurezultat + String.valueOf(brOznRac);
        // dodaj oznaku poslovnog prostora
        medjurezultat = medjurezultat + oznPosPr;
        // dodaj oznaku naplatnog uredjaja
        medjurezultat = medjurezultat + oznNapUr;
        // dodaj ukupni iznos racuna		
        medjurezultat = medjurezultat + String.valueOf(iznosUkupno);

        // elektronicki potpisi medjurezultat koristeci RSA-SHA1 potpis
        byte[] potpisano = null;
        try {
            // napravi biljeznika
            Signature biljeznik = Signature.getInstance("SHA1withRSA");
            // koristi privatni kljuc
            biljeznik.initSign((PrivateKey) privateKey);
            biljeznik.update(medjurezultat.getBytes());
            // potpisi string
            potpisano = biljeznik.sign();
        } catch (Exception e) {
            // nije uspjelo citanje privatnog kljuca
            e.printStackTrace();
        }
        // rezultatIspis = izracunajMD5(elektronicki potpisani medjurezultat)
        String rezultatIspis = MD5Hex(potpisano.toString());
        // kraj
        return rezultatIspis;
    }

    private String MD5Hex(String s) {
        String result = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(s.getBytes());
            result = toHex(digest);
        } catch (Exception e) {
            // this won't happen, we know Java has MD5!
        }
        return result;
    }

    private String toHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (int i = 0; i < a.length; i++) {
            sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
            sb.append(Character.forDigit(a[i] & 0x0f, 16));
        }
        return sb.toString();
    }
}
