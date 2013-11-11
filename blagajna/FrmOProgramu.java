package blagajna;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author eigorde
 */
public class FrmOProgramu extends javax.swing.JFrame {

    /**
     * Logger za greske i poruke. Referenca na logger u FrmBlagajna.
     */
    private Logger logger;
    /**
     * Kljucarnik sa kljucem i sifrom.
     */
    private KeyStore keyStore;
    /**
     * Javni kljuc.
     */
    private PublicKey publicKey;
    /**
     * Privatni kljuc.
     */
    private PrivateKey privateKey;
    /**
     * Certifikat iz kljucarnika.
     */
    private X509Certificate cert;
    /**
     * Email korisnika kljuca.
     */
    private String email;
    /**
     * Serijski broj kljuca, koristi se za reg.programa.<BR> Ako je email prazan
     * tada se oib koristi za izracun.<BR> Pogledati
     * <I>calculateSerijskiBroj()</I> funkciju.
     */
    private String serijskiBroj;
    /**
     * Ref. na postavke prozor.
     */
    private FrmPostavke postavke;
    private FormWindowAdapter windowAdapter;
    /**
     * Ref. na gl. prozor (FrmBlagajna).
     */
    private FrmBlagajna blagajna;
    /**
     * Dretva za provjeru registracije programa.
     */
    private Thread regThread;
    /**
     * Dretva za slanje stringova na syslog.
     */
    private Thread syslogThread;
    /**
     * Flag za logiranje, da li je vec napravljeno logiranje u
     * <I>oprogramu.log</I>.
     */
    private boolean logZapis;
    /**
     * Flag za info poruku, da li je vec poslana na syslog.<BR>
     */
    private boolean infoZapis;
    /**
     * Verzija programa. Npr. <I>v.1.03</I>.<BR>
     */
    public String ver;
    /**
     * Timer za pokretanje arhiviranja i slanja arhive na posluzitelj.<BR>
     */
    private Timer timerArhiv;
    /**
     * Boja dugmica kada nema fokus
     */
    private Color boja1;
    /**
     * Boja dugmica kada ima fokus
     */
    private Color boja2;
    /**
     * Javni objekt za dohvat izgleda korisnickog sucelja.<BR> Font, velicina,
     * debljina, ... <BR>
     */
    public Izgled izgled;
    /**
     * Adrese posluzitelja za slanje http i https zahtjeva.<BR> <B>Format:</B>
     * <I>protocol://address:port</I><BR> <B>Npr:</B>
     * <I>https://sfsolserver.no-ip.org:8001</I><BR> <B>Npr:</B>
     * <I>http://blagajna.no-ip.org:443</I><BR>
     *
     * @since 14/03/2013
     */
    public List<String> urlPosluzitelji = new ArrayList<String>();
    /**
     * Adrese syslog posluzitelja.<BR> <B>Format:</B> <I>address:port</I><BR>
     * <B>Npr:</B> <I>sfsolserver.no-ip.org:5001</I><BR>
     */
    public List<String> syslogPosluzitelji = new ArrayList<String>();

    /**
     * Creates new form FrmOProgramu.
     *
     * @param blagajna referenca na <I>FrmBlagajna</I> objekt.
     */
    public FrmOProgramu(FrmBlagajna blagajna) {

        /**
         * Napravi reference.
         */
        this.blagajna = blagajna;        

        this.postavke = blagajna.getFrmPostavke();

        this.windowAdapter = new FormWindowAdapter(blagajna, false);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.addWindowListener(windowAdapter);

        /**
         * Verzija programa.
         */
        ver = "1.10";

        try {

            initComponents();

            // postavi pocetne boje
            boja1 = cmdOK.getBackground();
            boja2 = Color.ORANGE;

            ui();

            // logiranje problema na syslog/datoteku
            logger = blagajna.getLogger();

            // pri zatvaranju sakrij prozor
            this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            // napravi poveznicu
            this.postavke = blagajna.getFrmPostavke();

            logZapis = false;
            infoZapis = false;

        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        }

    }

    /**
     * Pokreni proceduru arhiviranja racuna, postavki, kljuceva, itd.<BR>
     * Nakon toga ide sinkronizacija.<BR>
     * <B>NAPOMENA:</B> Ova procedura ne blokira izvrsavanje koda.
     * @param odgoda Odgoda za arhiviranje u sekundama.
     */
    public void pokreniArhiviranje(int odgoda) {

        // Arhiviraj sa odgodom
        timerArhiv = new Timer(odgoda * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Thread arhiviranjeThread = new Thread(new Runnable() {
                    @Override
                    public void run() {  
                        Benchmark bm = new Benchmark();
                        bm.start();
                        try {
                            Zipper zipper = new Zipper();
                            // otvori privremenu datoteku
                            File tempFile = File.createTempFile(serijskiBroj + "-", ".zip"); 
                            // prilagodi putanju, tako da format datoteke bude SN.zip
                            String tempFileAdjusted = tempFile.getAbsolutePath().substring(0, tempFile.getAbsolutePath().indexOf('-')) + ".zip";
                            // Referenca na folder Racuni
                            File dirRacuni = new File("Racuni");
                            // Popis racuna
                            File[] popisRacuna = dirRacuni.listFiles();
                            // dodaj u arhivu racune
                            for (File racun : popisRacuna) {         
                                zipper.addFile("Racuni/" + racun.getName());
                            }
                            // dodaj ostale datoteke
                            zipper.addFile("postavke.txt");
                            zipper.addFile("podnozje.txt");
                            zipper.addFile("zaglvalje.txt");
                            zipper.addFile("distributeri.txt");
                            zipper.addFile("blagajnik.txt");
                            zipper.addFile("cjenik.txt");
                            zipper.addFile("firme.txt");
                            zipper.addFile("izgled.txt");
                            zipper.addFile("pacijenti.txt");
                            zipper.addFile("pp.response.xml");
                            zipper.addFile("pp.xml");
                            zipper.addFile("pp.signed.xml");
                            zipper.addFile("blagajna.log");
                            zipper.addFile(postavke.getKeyFile());
                            zipper.zipIt(tempFileAdjusted);
                            // Sinkroniziraj pomocu liste syslog posluzitelja.
                            if (postavke.isArhiviraj()) {
                                for (String posluzitelj : syslogPosluzitelji) {                                
                                    // Uzmi ime iz popisa
                                    String ime = posluzitelj.substring(0, posluzitelj.indexOf(':'));
                                    // Uzmi syslog port i uvecaj za 1
                                    int port = Integer.parseInt(posluzitelj.substring(posluzitelj.indexOf(':') + 1)) + 1;
                                    // Sinkroniziraj
                                    SyncClient syncClient = new SyncClient();
                                    syncClient.connect(ime, port);
                                    syncClient.sync(tempFileAdjusted);
                                    syncClient.disconnect();
                                }
                            }
                            
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, "timerArhiv(), arhiviranjeThread()", ex);
                        }
                        logger.info("Arhiviranje je trajalo: " + bm.getSec() + " sec.");
                    }
                });
                arhiviranjeThread.start();
                timerArhiv.stop();
            }
        });
        /**
         * Pokreni arhiviranje samo ako je produkcijska okolina.
         */
        if (!postavke.getKeyFile().endsWith("test.pfx")) {
            timerArhiv.start();
        }        
    }
    /**
     * Prikaz podataka u text polju (podaci, distributeri, autori).<BR> Provjera
     * registracije.<BR>
     */
    public void osvjeziPoruku() {
        // prikazuje trenutne podatke/postavke
        String autori =
                "Autori\n"
                + "------------------------------\n"
                + "Vedran Kolonic <vedran.kolonic@gmail.com>\n"
                + "Igor Delac <igor.delac@gmail.com>\n\n";

        String distributeri =
                "Distribucija\n"
                + "------------------------------\n"
                + "ALISYS j.t.d\n"
                + "ing. Lj.Kursar\n"
                + "tel: 031/531-135\n"
                + "mail:alisys.os@gmail.com\n"
                + "Frankopanska 172\n"
                + "31 000 OSIJEK\n\n";

        // Pokusaj uzeti info o distribuciji iz txt datoteke
        try {

            String line;
            int lineNum = 0;

            File distributerFile = new File("distributeri.txt");
            if (distributerFile.exists()) {
                // prvo ucitaj zaglavlje, max. 10 linija
                BufferedReader br = new BufferedReader(new FileReader(
                        distributerFile));
                distributeri = "Distribucija\n"
                        + "------------------------------\n";
                while (((line = br.readLine()) != null) && lineNum < 10) {
                    lineNum++;
                    distributeri = distributeri + line + "\n";
                }
                br.close();
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "osvjeziPoruku(), distributeri.txt read error.", e);
        }

        // ovo je testna funkcija za ucitavanje kljuca
        // osim ucitavanje kljuca, dobiva se i email adresa za kontakt
        loadKeyStore(this.postavke.getKeyFile(),
                this.postavke.getKeyPassword());

        if (email.length() > 0) {
            // izracunaj serijski br. iz email adrese
            serijskiBroj = calculateSerijskiBroj(email);
        } else {
            // izracunaj serijski br. iz OIBa
            serijskiBroj = calculateSerijskiBroj(postavke.getOib());
        }

        String podaci;

        if (postavke.getKeyFile().endsWith("test.pfx")
                && postavke.getKeyPassword().startsWith("POS3x")) {
            podaci = "Blagajna v." + ver + "\n"
                    + "------------------------------\n"
                    + "      Vlasnik: " + postavke.getVlasnik() + "\n"
                    + "          OIB: " + postavke.getOib() + "\n"
                    + "        Kljuc: " + postavke.getKeyFile() + "\n"
                    + "        Email: -\n"
                    + "Serijski broj: -\n\n";
        } else {
            podaci = "Blagajna v." + ver + "\n"
                    + "------------------------------\n"
                    + "      Vlasnik: " + postavke.getVlasnik() + "\n"
                    + "          OIB: " + postavke.getOib() + "\n"
                    + "        Kljuc: " + postavke.getKeyFile() + "\n"
                    + "        Email: " + email + "\n"
                    + "Serijski broj: " + serijskiBroj + "\n\n";
        }

        txtOProgramu.setText(podaci + distributeri + autori);

        if (!logZapis) {
            logger.info(podaci + distributeri + autori);
            logZapis = true;
        }
        // provjeri registraciju
        checkRegistracija();

    }

    /**
     * Funkcija za provjeru <I>registracije</I>.<BR> Ako dobije blokadu sa
     * posluzitelja, tada se ispis onemoguci sa setBlokiran() funkcijom u
     * frmBlagajna.<BR>
     */
    private void checkRegistracija() {

        regThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // Izvrsi preuzimanje samo ako su obje liste posluzitelja prazne
                    //  inace nema smisla ponovno preuzimati i puniti liste
                    if (urlPosluzitelji.isEmpty() && syslogPosluzitelji.isEmpty()) {
                        // preuzmi listu http(s) i syslog posluzitelja
                        String httpsServeri = http_connect_get("http://www.inet.hr/~dadelac/https.txt");
                        for (String posluzitelj : httpsServeri.split("[\\r\\n]+")) {
                            // Osnovna provjera
                            if (posluzitelj.startsWith("http")) {
                                urlPosluzitelji.add(posluzitelj);
                            }
                        }
                        String syslogServeri = http_connect_get("http://www.inet.hr/~dadelac/syslog.txt");
                        for (String posluzitelj : syslogServeri.split("[\\r\\n]+")) {
                            // Osnovna provjera, mora imati : i ne pocinje sa # znakom
                            if (posluzitelj.indexOf(":") > 0 && !posluzitelj.startsWith("#")) {
                                syslogPosluzitelji.add(posluzitelj);
                            }
                        }
                    }

                    // posalji poruku o korisniku
                    if (!infoZapis) {
                        String infoPoruka = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH).format(new Date()) + ", INFO v." + ver
                                + ", Vlasnik: " + postavke.getVlasnik()
                                + ", OIB: " + postavke.getOib()
                                + ", SN: " + serijskiBroj
                                + ", P/B: " + postavke.getPoslovnica() + "/" + postavke.getBlagajna();
                        if (email.length() > 0) {
                            infoPoruka = infoPoruka + ", Email: " + email;
                        }
                        infoPoruka = infoPoruka + "\n";

                        for (String syslogPosluzitelj : syslogPosluzitelji) {
                            String posluzitelj = syslogPosluzitelj.substring(0, syslogPosluzitelj.indexOf(':'));
                            String port = syslogPosluzitelj.substring(syslogPosluzitelj.indexOf(':') + 1);
                            logger.info("Saljem INFO poruku na " + posluzitelj + ":" + port);
                            syslog_send(posluzitelj,
                                    Integer.parseInt(port), infoPoruka);
                        }
                        infoZapis = true;
                    }

                    // Otvori https/http vezu za svaki posluzitelj i procitaj sadrzaj 
                    //  datoteke koja ima ser.br. kao ime
                    for (String url : urlPosluzitelji) {

                        // Inicijalno registracijski string je prazan
                        String reg = "";

                        // Provjeri da li url zavrsava sa / znakom radi izgradnje pravilne http adrese
                        if (url.endsWith("/")) {
                            // Da li je http ili https veza
                            if (url.startsWith("https:")) {
                                reg = https_connect_get(url + serijskiBroj + ".reg");
                            } else if (url.startsWith("http:")) {
                                reg = http_connect_get(url + serijskiBroj + ".reg");
                            }
                        } else {
                            if (url.startsWith("https:")) {
                                reg = https_connect_get(url + "/" + serijskiBroj + ".reg");
                            } else if (url.startsWith("http:")) {
                                reg = http_connect_get(url + "/" + serijskiBroj + ".reg");
                            }
                        }

                        // Da li smo nesto dobili ?
                        if (reg.length() > 0) {
                            // provjera da li sadrzi "BLO" niz negdje
                            if (reg.contains("BLO")) {
                                // ugasi ispis
                                blagajna.setBlokiran(true);
                                // dodaj u text polje
                                txtOProgramu.append("\n\nProgram blokiran: " + reg + "\nMolimo registrirajte kopiju.");
                            } else {
                                blagajna.setBlokiran(false);
                                txtOProgramu.append("\n\nProgram registriran: " + reg);
                                cmdRegistriraj.setEnabled(false);
                            }
                            // izlaz iz for petlje, program je ill registriran ili blokiran
                            // pa nema potrebe nastaviti sa http/https upitima prema drugim serverima
                            break;
                        }
                    }

                } catch (Exception e) {
                    //logger.log(Level.SEVERE, "checkRegistracija(), sendThread()", e);
                }
            }
        });

        // koristi li se testni kljuc?
        if (postavke.getKeyFile().endsWith("test.pfx")
                && postavke.getKeyPassword().startsWith("POS3x")) {
            txtOProgramu.append("\n\nKoristite testni kljuc.\nPribavite privatni kljuc kod Fine.\n");
        } else {
            // kreni u provjeru registracije prema posluzitelju
            regThread.start();
        }
    }

    /**
     * Ovu proceduru najbolje pozvati iz dretve tako da se program ne
     * zakoci.<BR> Povratna vr. ce cesto biti "" kod neispravne internet veze,
     * nedostupnosti posluzitelja, itd.<BR>
     *
     * @param httpURL URL sa kojeg se snima datoteka
     * @return povratna vrijednost je sadrzaj datoteke
     */
    private String http_connect_get(String httpURL) {

        String message = "";

        try {

            URL myurl = new URL(httpURL);
            HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                message = message + inputLine + "\n";
            }
            in.close();

        } catch (Exception e) {
            //logger.log(Level.SEVERE, "https_connect_get()", e);
        }

        return message;

    }

    /**
     * Ovu proceduru najbolje pozvati iz dretve tako da se program ne
     * zakoci.<BR> Povratna vr. ce cesto biti "" kod neispravne internet veze,
     * nedostupnosti posluzitelja, itd.<BR>
     *
     * @param httpsURL URL sa kojeg se snima datoteka
     * @return povratna vrijednost je sadrzaj datoteke
     */
    private String https_connect_get(String httpsURL) {

        String message = "";

        try {

            URL myurl = new URL(httpsURL);
            HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                message = message + inputLine;
            }
            in.close();

        } catch (Exception e) {
            //logger.log(Level.SEVERE, "https_connect_get()", e);
        }

        return message;

    }

    // ova procedura zaobilazi provjeru hostname-a u URLu pri otvaranju 
    // SSL veze prema https posluzitelju, tj. nije vazno koje je dns ime posluzitelja
    static {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname,
                            javax.net.ssl.SSLSession sslSession) {
                        //for localhost testing only
                        //if (hostname.equals("localhost")) {
                        return true; // ne treba nam provjera imena servera za ssl
                        //}
                        //return false;
                    }
                });
    }
    /*
     * Ovdje su kratke instrukcije kako sa apache2 posluziteljem
     * i snakeoil certifikatom postaviti https/ssl funkcionalnost
     * 
     ## dodati def. site za ssl (/var/www) i modul
     a2ensite default-ssl
     a2enmod ssl

     ## provjera https porta
     netstat -lnt|grep 443

     ## importiranje snakeoil cert. u cacerts za java programe
     /usr/local/jdk1.7.0/bin/keytool -importcert -trustcacerts -file /etc/ssl/certs/ssl-cert-snakeoil.pem -alias snakeoil -keystore cacerts -storepass changeit

     ## izrada pfx kljuca za Firefox
     openssl pkcs12 -export -out /home/maintu/tester.pfx -inkey private/ssl-cert-snakeoil.key -in certs/ssl-cert-sna$

     ## urediti
     nano /etc/apache2/sites-available/default-ssl
     ## odkomentirati
     SSLCACertificatePath /etc/ssl/certs

     ## reload https servisa
     service apache2 reload


     */

    /**
     * Ucitava iz datoteke privatni kljuc (pfx / p12 format).<BR>
     *
     * @param keyFile putanja do datoteke
     * @param keyPassword zaporka za koristenje datoteke
     */
    private void loadKeyStore(String keyFile, String keyPassword) {

        KeyStore.PrivateKeyEntry keyEntry;

        // objekt keyStore, koriste proc. za potpisivanje i provjeru potpisa,
        //  i procedura za izracun zastitnog koda
        try {
            // ucitaj jks datoteku
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(keyFile), keyPassword.toCharArray());

            // uzmi kljuc iz pfx datoteke
            keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyStore.aliases().nextElement(),
                    new KeyStore.PasswordProtection(keyPassword.toCharArray()));

            cert = (X509Certificate) keyEntry.getCertificate();

            if (cert.getSubjectAlternativeNames() != null) {
                Collection altNames = cert.getSubjectAlternativeNames();
                List item = (List) altNames.iterator().next();
                email = (String) item.get(1);
            } else {
                email = "";
            }

            publicKey = cert.getPublicKey();
            privateKey = keyEntry.getPrivateKey();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "loadKeyStore()", e);
        }

    }

    /**
     * Serijski broj.<BR>
     *
     * @return vraca vr. serijskog broja, pozvati calculateSerijskiBroj() za
     * izracun
     */
    public String getSerijskiBroj() {
        return serijskiBroj;
    }

    /**
     * Izracunava serijski broj za zadani niz koristeci privatni kljuc.<BR>
     * Prije toga obavezno ucitati kljuc sa loadKeyStore() funkcijom.<BR>
     *
     * @param source niz znakova, npr. email adresa, koja se moze izvuci iz
     * kljuca ili oib se koristi ako je email prazan
     * @return povratna vrijednost je jedinstveni 8 znakovni alfa-numericki niz
     */
    private String calculateSerijskiBroj(String source) {

        // vr.koju funkcija vraca za predani String
        String returnValue;

        // koristi elektronicki potpis i RSA-SHA1 funkciju
        byte[] signature = null;

        try {
            // napravi biljeznika
            Signature signer = Signature.getInstance("SHA1withRSA");
            // koristi privatni kljuc
            signer.initSign((PrivateKey) privateKey);
            signer.update(source.getBytes());
            // potpisi string
            signature = signer.sign();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "getSerialNumber()", e);
        }

        // neka inicijalna vr. ser. broja
        // zapravo se radi o izracunatoj vr. ako se koristi test.pfx
        returnValue = "A1B2C3D4";

        if (signature.length > 4) {
            // vrati prva 4 znaka u hex formatu
            returnValue = String.format("%02X", signature[0])
                    + String.format("%02X", signature[1])
                    + String.format("%02X", signature[2])
                    + String.format("%02X", signature[3]);
        }

        return returnValue;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPoruka = new javax.swing.JScrollPane();
        txtOProgramu = new javax.swing.JTextArea();
        pnlIzbornik = new javax.swing.JPanel();
        cmdOK = new javax.swing.JButton();
        cmdRegistriraj = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("O programu");

        txtOProgramu.setColumns(20);
        txtOProgramu.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        txtOProgramu.setRows(5);
        txtOProgramu.setFocusable(false);
        pnlPoruka.setViewportView(txtOProgramu);

        cmdOK.setText("OK");
        cmdOK.setToolTipText("Zatvori prozor");
        cmdOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOKActionPerformed(evt);
            }
        });
        cmdOK.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdOKFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdOKFocusLost(evt);
            }
        });

        cmdRegistriraj.setText("Registriraj");
        cmdRegistriraj.setToolTipText("Posalji zahtjev za registraciju kopije autorima");
        cmdRegistriraj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRegistrirajActionPerformed(evt);
            }
        });
        cmdRegistriraj.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdRegistrirajFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdRegistrirajFocusLost(evt);
            }
        });

        javax.swing.GroupLayout pnlIzbornikLayout = new javax.swing.GroupLayout(pnlIzbornik);
        pnlIzbornik.setLayout(pnlIzbornikLayout);
        pnlIzbornikLayout.setHorizontalGroup(
            pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIzbornikLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdOK, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdRegistriraj, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlIzbornikLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdOK, cmdRegistriraj});

        pnlIzbornikLayout.setVerticalGroup(
            pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIzbornikLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmdOK, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmdRegistriraj, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlIzbornikLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdOK, cmdRegistriraj});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPoruka, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlIzbornik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlIzbornik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlPoruka, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOKActionPerformed

        blagajna.toggleScreen(true);
        this.setVisible(false);

    }//GEN-LAST:event_cmdOKActionPerformed

    private void cmdRegistrirajActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRegistrirajActionPerformed

        // povratna vr. dialoga
        int retVal;

        // paneli
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        JPanel p5 = new JPanel();

        // text polja
        JTextField txtVlasnik = new JTextField(14);
        JTextField txtOIB = new JTextField(14);
        JTextField txtEmail = new JTextField(14);

        // pocetne vr.
        txtVlasnik.setText(this.postavke.getVlasnik());
        txtOIB.setText(this.postavke.getOib());
        txtEmail.setText(email);

        // koristi li se testni kljuc ?
        if (postavke.getKeyFile().endsWith("test.pfx")
                && postavke.getKeyPassword().startsWith("POS3x")) {
            txtEmail.setText("-");
        }

        // povezivanje
        p1.add(new JLabel("Vlasnik: "));
        p1.add(txtVlasnik);
        p2.add(new JLabel("  OIB: "));
        p2.add(txtOIB);
        p3.add(new JLabel("Email: "));
        p3.add(txtEmail);
        p4.add(new JLabel("Serijski broj: " + serijskiBroj));
        p5.add(new JLabel("\nRegistrirati kopiju programa?"));

        // obj. koji sadrzi sve panele
        Object[] message = {p1, p2, p3, p4, p5};

        // slozi izgled
        for (int i = 0; i < message.length; i++) {
            JPanel panel = (JPanel) message[i];
            for (Component comp : panel.getComponents()) {
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
            }
        }

        retVal = JOptionPane.showOptionDialog(
                this, message, "Registracija", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);
        if (retVal == JOptionPane.OK_OPTION) {
            for (String syslogPosluzitelj : syslogPosluzitelji) {

                // Postavi parametre za syslog
                String reg = "REG: " + txtVlasnik.getText() + ", " + txtOIB.getText() + ", " + txtEmail.getText() + "\n";
                String posluzitelj = syslogPosluzitelj.substring(0, syslogPosluzitelj.indexOf(':') - 1);
                String port = syslogPosluzitelj.substring(syslogPosluzitelj.indexOf(':') + 1);

                logger.info("Saljem REG poruku na " + posluzitelj + ":" + port);

                syslog_send(posluzitelj,
                        Integer.parseInt(port), reg);
            }
            JOptionPane.showMessageDialog(this, "Registracijski podaci poslani. Naknadno cete biti kontaktirani u slucaju potrebe.", "Registracija", JOptionPane.INFORMATION_MESSAGE);

        }

    }//GEN-LAST:event_cmdRegistrirajActionPerformed

    /**
     * Syslog protokolom salje podatke na posluzitelj.<BR>
     *
     * @param hostname naziv posluzitelja (adresa ili dns naziv)
     * @param port port, najcesce udp 514
     * @param message poruka koja se zeli poslati, niz znakova
     */
    private void syslog_send(final String hostname, final int port, final String message) {
        // salje tekst.niz na syslog servis

        syslogThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // niz okteta
                byte[] sendData = message.getBytes();
                // socket, adresa i paket
                DatagramSocket clientSocket = null;
                InetAddress ipAddress;
                DatagramPacket sendPacket;

                try {
                    //
                    clientSocket = new DatagramSocket();
                    ipAddress = InetAddress.getByName(hostname);
                    sendPacket = new DatagramPacket(
                            sendData, sendData.length, ipAddress, port);
                    clientSocket.send(sendPacket);
                    clientSocket.close();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "syslog_send()", e);
                } finally {
                    clientSocket.close();
                }


            }
        });

        // pokreni dretvu za slanje logova
        if (!(syslogThread.isAlive())) {
            syslogThread.start();
        }


    }

    /**
     * Korisnicko sucelje postavlja.<BR>
     */
    public void ui() {
        // UI
        izgled = blagajna.izgled;

        // Panel sa dugmicima                
        for (Component comp : pnlIzbornik.getComponents()) {
            if (comp instanceof JButton) {
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
                
                comp.setMinimumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                comp.setMaximumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                comp.setPreferredSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));



            } else {
                // font i velicina, prema postavkama
                comp.setFont(
                        new Font(izgled.getFontNaziv(), Font.PLAIN,
                        izgled.getFontVelicina()));
            }
        }
        
        // Panel sa tekst porukom
        for (Component comp : pnlPoruka.getComponents()) {
            if (comp instanceof JButton) {
                                
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
                
                comp.setMinimumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                comp.setMaximumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                comp.setPreferredSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));

            } else {
                // font i velicina, prema postavkama
                comp.setFont(
                        new Font(izgled.getFontNaziv(), Font.PLAIN,
                        izgled.getFontVelicina()));
            }
        }
    }

    private void cmdOKFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdOKFocusGained
        cmdOK.setBackground(boja2);
    }//GEN-LAST:event_cmdOKFocusGained

    private void cmdOKFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdOKFocusLost
        cmdOK.setBackground(boja1);
    }//GEN-LAST:event_cmdOKFocusLost

    private void cmdRegistrirajFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdRegistrirajFocusGained
        cmdRegistriraj.setBackground(boja2);
    }//GEN-LAST:event_cmdRegistrirajFocusGained

    private void cmdRegistrirajFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdRegistrirajFocusLost
        cmdRegistriraj.setBackground(boja1);
    }//GEN-LAST:event_cmdRegistrirajFocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdOK;
    private javax.swing.JButton cmdRegistriraj;
    private javax.swing.JPanel pnlIzbornik;
    private javax.swing.JScrollPane pnlPoruka;
    private javax.swing.JTextArea txtOProgramu;
    // End of variables declaration//GEN-END:variables
}
