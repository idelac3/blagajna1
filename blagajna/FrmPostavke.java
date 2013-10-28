package blagajna;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author root
 */
public class FrmPostavke extends javax.swing.JFrame {

    /**
     * Logiranje gresaka
     */ 
    private Logger logger;
    
    /*
     * -------------------------
     * Osnovne postavke
     * -------------------------
     */ 
    private String naziv;
    private String vlasnik;
    private String oib;
    private String poslovnica;
    private String blagajna;
    private String keyFile;
    private String keyPassword;
    private String sURL;
    private boolean uSustPdv;


    /*
     * -------------------------
     * Ispis postavke
     * -------------------------
     */    
    private int timeout;
    private String fontNaziv;
    private int fontVelicina;
    private int duzinaLinije;
    private String pisac;
    private double papirX;
    private double papirY;
    private double marginaX;
    private double marginaY;
    private double ispisnaPovrsinaX;
    private double ispisnaPovrsinaY;
    private String formatPapira;
    private String oznakaProizvoda;
    private String prijeSlanja;
    private String poslijeSlanja;

    /*
     * -------------------------
     * Sistemske (skrivene) postavke
     * -------------------------
     */    
    private int brRac;
    private String izborBlagajnika;
    private boolean kolicina;
    private boolean arhiviraj;
    private boolean transakcijski;

    /*
     * -------------------------
     * Izgled postavke
     * -------------------------
     */    
    private String izgledFont;
    private int izgledVelicina;
    private int izgledDebljina;
    private int omjer;
    private List<Integer> stupac;

    /**
     * Zastavica koja odredjuje da se FileOpen dialog prikaze samo 1
     * kada korisnik odabere polje za odabir priv.kljuca. (pfx/p12 datoteka)
     */ 
    private boolean prikaziFileOpen = true;
    
    /**
     * Referenca na glavnu formu/prozor.
     */ 
    private FrmBlagajna frmBlagajna;
    private FormWindowAdapter windowAdapter;
    
    /**
     * Popis svih formata papira.<BR> <I>MediaSizeName</I>, <I>Opisni naziv</I>.
     */
    private Hashtable<MediaSizeName, String> paperNames = new Hashtable<MediaSizeName, String>();
    /**
     * Decimal formater.
     */
    DecimalFormat dFormat = new DecimalFormat("0.00");
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
     * Creates new form FrmPostavke
     */
    public FrmPostavke(FrmBlagajna frmBlagajna) {

        this.windowAdapter = new FormWindowAdapter(frmBlagajna, false);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.addWindowListener(windowAdapter);

        // postavi reference
        this.frmBlagajna = frmBlagajna;
        this.izgled = frmBlagajna.izgled;

        String sLog = "";
        // logiranje problema na syslog/datoteku
        //logger = Logger.getLogger("postavke");
        logger = frmBlagajna.getLogger();

        initComponents();

        // postavi pocetne boje
        boja1 = cmdOK.getBackground();
        boja2 = Color.ORANGE;

        // Papiri i formati        
        paperNames.put(MediaSizeName.ISO_A0, "A0");
        paperNames.put(MediaSizeName.ISO_A1, "A1");
        paperNames.put(MediaSizeName.ISO_A2, "A2");
        paperNames.put(MediaSizeName.ISO_A3, "A3");
        paperNames.put(MediaSizeName.ISO_A4, "A4");
        paperNames.put(MediaSizeName.ISO_A5, "A5");
        paperNames.put(MediaSizeName.ISO_A6, "A6");
        paperNames.put(MediaSizeName.ISO_A7, "A7");
        paperNames.put(MediaSizeName.ISO_A8, "A8");
        paperNames.put(MediaSizeName.ISO_A9, "A9");
        paperNames.put(MediaSizeName.ISO_A10, "A10");
        paperNames.put(MediaSizeName.ISO_B0, "B0");
        paperNames.put(MediaSizeName.ISO_B1, "B1");
        paperNames.put(MediaSizeName.ISO_B2, "B2");
        paperNames.put(MediaSizeName.ISO_B3, "B3");
        paperNames.put(MediaSizeName.ISO_B4, "B4");
        paperNames.put(MediaSizeName.ISO_B5, "B5");
        paperNames.put(MediaSizeName.ISO_B6, "B6");
        paperNames.put(MediaSizeName.ISO_B7, "B7");
        paperNames.put(MediaSizeName.ISO_B8, "B8");
        paperNames.put(MediaSizeName.ISO_B9, "B9");
        paperNames.put(MediaSizeName.ISO_B10, "B10");
        paperNames.put(MediaSizeName.NA_LETTER, "North American Letter");
        paperNames.put(MediaSizeName.NA_LEGAL, "North American Legal");
        paperNames.put(MediaSizeName.NA_8X10, "North American 8x10 inch");
        paperNames.put(MediaSizeName.NA_5X7, "North American 5x7 inch");
        paperNames.put(MediaSizeName.EXECUTIVE, "Executive");
        paperNames.put(MediaSizeName.FOLIO, "Folio");
        paperNames.put(MediaSizeName.INVOICE, "Invoice");
        paperNames.put(MediaSizeName.TABLOID, "Tabloid");
        paperNames.put(MediaSizeName.LEDGER, "Ledger");
        paperNames.put(MediaSizeName.QUARTO, "Quarto");
        paperNames.put(MediaSizeName.ISO_C0, "C0");
        paperNames.put(MediaSizeName.ISO_C1, "C1");
        paperNames.put(MediaSizeName.ISO_C2, "C2");
        paperNames.put(MediaSizeName.ISO_C3, "C3");
        paperNames.put(MediaSizeName.ISO_C4, "C4");
        paperNames.put(MediaSizeName.ISO_C5, "C5");
        paperNames.put(MediaSizeName.ISO_C6, "C6");
        paperNames.put(MediaSizeName.ISO_DESIGNATED_LONG, "ISO Designated Long size");
        paperNames.put(MediaSizeName.NA_10X13_ENVELOPE, "North American 10x13 inch");
        paperNames.put(MediaSizeName.NA_9X12_ENVELOPE, "North American 9x12 inch");
        paperNames.put(MediaSizeName.NA_NUMBER_10_ENVELOPE, "North American number 10 business envelope");
        paperNames.put(MediaSizeName.NA_7X9_ENVELOPE, "North American 7x9 inch envelope");
        paperNames.put(MediaSizeName.NA_9X11_ENVELOPE, "North American 9x11 inch envelope");
        paperNames.put(MediaSizeName.NA_10X14_ENVELOPE, "North American 10x14 inch envelope");
        paperNames.put(MediaSizeName.NA_NUMBER_9_ENVELOPE, "North American number 9 business envelope");
        paperNames.put(MediaSizeName.NA_6X9_ENVELOPE, "North American 6x9 inch envelope");
        paperNames.put(MediaSizeName.NA_10X15_ENVELOPE, "North American 10x15 inch envelope");
        paperNames.put(MediaSizeName.MONARCH_ENVELOPE, "Monarch envelope");
        paperNames.put(MediaSizeName.JIS_B0, "Japanese B0");
        paperNames.put(MediaSizeName.JIS_B1, "Japanese B1");
        paperNames.put(MediaSizeName.JIS_B2, "Japanese B2");
        paperNames.put(MediaSizeName.JIS_B3, "Japanese B3");
        paperNames.put(MediaSizeName.JIS_B4, "Japanese B4");
        paperNames.put(MediaSizeName.JIS_B5, "Japanese B5");
        paperNames.put(MediaSizeName.JIS_B6, "Japanese B6");
        paperNames.put(MediaSizeName.JIS_B7, "Japanese B7");
        paperNames.put(MediaSizeName.JIS_B8, "Japanese B8");
        paperNames.put(MediaSizeName.JIS_B9, "Japanese B9");
        paperNames.put(MediaSizeName.JIS_B10, "Japanese B10");
        paperNames.put(MediaSizeName.A, "Engineering ANSI A");
        paperNames.put(MediaSizeName.B, "Engineering ANSI B");
        paperNames.put(MediaSizeName.C, "Engineering ANSI C");
        paperNames.put(MediaSizeName.D, "Engineering ANSI D");
        paperNames.put(MediaSizeName.E, "Engineering ANSI E");
        paperNames.put(MediaSizeName.JAPANESE_POSTCARD, "Japanese Postcard");
        paperNames.put(MediaSizeName.ITALY_ENVELOPE, "Italian Envelope");
        paperNames.put(MediaSizeName.PERSONAL_ENVELOPE, "Personal Envelope");
        paperNames.put(MediaSizeName.NA_NUMBER_11_ENVELOPE, "North American Number 11 Envelope");
        paperNames.put(MediaSizeName.NA_NUMBER_12_ENVELOPE, "North American Number 12 Envelope");
        paperNames.put(MediaSizeName.NA_NUMBER_14_ENVELOPE, "North American Number 14 Envelope");
        //...

        /**
         * Monospaced fontovi, tvz. fontovi sa istom sirinom svakog znaka.
         * Algoritam preuzet sa:
         * http://stackoverflow.com/questions/922052/testing-whether-a-font-is-monospaced-in-java
         */
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilyNames = graphicsEnvironment.getAvailableFontFamilyNames();

        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.createGraphics();

        cboFont.removeAllItems();
        cboIzgledFont.removeAllItems();

        for (String fontFamilyName : fontFamilyNames) {
            boolean isMonospaced = true;

            int fontStyle = Font.PLAIN;
            int fontSize = 12;
            Font font = new Font(fontFamilyName, fontStyle, fontSize);
            FontMetrics fontMetrics = graphics.getFontMetrics(font);

            int firstCharacterWidth = 0;
            boolean hasFirstCharacterWidth = false;
            for (int codePoint = 0; codePoint < 128; codePoint++) {
                if (Character.isValidCodePoint(codePoint) && (Character.isLetter(codePoint) || Character.isDigit(codePoint))) {
                    char character = (char) codePoint;
                    int characterWidth = fontMetrics.charWidth(character);
                    if (hasFirstCharacterWidth) {
                        if (characterWidth != firstCharacterWidth) {
                            isMonospaced = false;
                            break;
                        }
                    } else {
                        firstCharacterWidth = characterWidth;
                        hasFirstCharacterWidth = true;
                    }
                }
            }

            if (isMonospaced) {
                cboFont.addItem(fontFamilyName);
                cboIzgledFont.addItem(fontFamilyName);
            }
        }

        graphics.dispose();
        //...


        /**
         * Pisaci.
         */
        cboPisac.removeAllItems();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService p : printServices) {
            cboPisac.addItem(p.getName());
        }
        //...

        // ucitaj postavke
        postavkeLoad("postavke.txt");

        // postavi izgled        
        ui();
                
        /*
         * Osnovni panel
         */ 
        txtNaziv.setText(naziv);
        txtVlasnik.setText(vlasnik);
        txtOib.setText(oib);
        txtPoslovnica.setText(poslovnica);
        txtBlagajna.setText(blagajna);

        txtKljuc.setText(keyFile);
        txtZaporka.setText(keyPassword);
        txtUrl.setText(sURL);
        chkUSustPdv.setSelected(uSustPdv);

        /*
         * Ispis panel
         */ 
        boolean fontPronadjen = false;
        txtTimeout.setText(String.valueOf(timeout));
        txtDuzinaLinije.setText(String.valueOf(duzinaLinije));
        for (int i = 0; i < cboFont.getItemCount(); i++) {
            if (fontNaziv.equalsIgnoreCase(String.valueOf(cboFont.getItemAt(i)))) {
                cboFont.setSelectedIndex(i);
                fontPronadjen = true;
            }
        }
        if (!fontPronadjen) {
            cboFont.addItem(fontNaziv);
            cboFont.setSelectedItem(fontNaziv);
        }
        txtVelicina.setText(String.valueOf(fontVelicina));
        boolean pisacPronadjen = false;
        for (int i = 0; i < cboPisac.getItemCount(); i++) {
            if (pisac.equalsIgnoreCase(String.valueOf(cboPisac.getItemAt(i)))) {
                cboPisac.setSelectedIndex(i);
                pisacPronadjen = true;
            }
        }
        
        if (!pisacPronadjen) {

            /*
             * Ako pisac nije ispravno namjesten, pokusaj iz default-nog
             * izvuci postavke papira i margina.
             */
            pisac = PrintServiceLookup.lookupDefaultPrintService().getName();
            // za dohvat velicine papira, margina i ispisne povrsine
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            PrintService[] printers = PrinterJob.lookupPrintServices();
            for (PrintService printService : printers) {
                if (printService.getName().equalsIgnoreCase(pisac)) {
                    try {
                        printerJob.setPrintService(printService);
                    } catch (PrinterException ex) {
                        logger.log(Level.SEVERE, "FrmPostavke(), printerJob.setPrintService()", ex);
                    }
                    break;
                }
            }
            PageFormat pageFormat = printerJob.defaultPage();
            Paper paper = pageFormat.getPaper();

            papirX = paper.getWidth();
            papirY = paper.getHeight();

            marginaX = paper.getImageableX();
            marginaY = paper.getImageableY();

            ispisnaPovrsinaX = paper.getImageableHeight();
            ispisnaPovrsinaY = paper.getImageableWidth();
                
        }

        final double k = 25.4 / 72; // Faktor konverzije, point --> mm
        txtPapirX.setText(dFormat.format(papirX * k));
        txtPapirY.setText(dFormat.format(papirY * k));
        txtMarginaX.setText(dFormat.format(marginaX * k));
        txtMarginaY.setText(dFormat.format(marginaY * k));
        txtIspisnaPovrsinaX.setText(dFormat.format(ispisnaPovrsinaX * k));
        txtIspisnaPovrsinaY.setText(dFormat.format(ispisnaPovrsinaY * k));

        popuniFormatePapira();

        for (int i = 0; i < cboFormatPapira.getItemCount(); i++) {
            if (formatPapira.equalsIgnoreCase(String.valueOf(cboFormatPapira.getItemAt(i)))) {
                cboFormatPapira.setSelectedIndex(i);
            }
        }

        txtProizvodOznaka.setText(oznakaProizvoda);
        txtPrijeSlanja.setText(prijeSlanja);
        txtPoslijeSlanja.setText(poslijeSlanja);

        /*
         * Izgled panel
         */                
        fontPronadjen = false;
        for (int i = 0; i < cboIzgledFont.getItemCount(); i++) {
            if (izgledFont.equalsIgnoreCase(String.valueOf(cboIzgledFont.getItemAt(i)))) {
                cboIzgledFont.setSelectedIndex(i);
                fontPronadjen = true;
            }
        }
        if (!fontPronadjen) {
            cboIzgledFont.addItem(izgledFont);
            cboIzgledFont.setSelectedItem(izgledFont);
        }
        sldIzgledVelicina.setValue(izgledVelicina);
        sldIzgledDebljina.setValue(izgledDebljina);
        
        frmBlagajna.latch.countDown();

    }

    /**
     * Ucitava iz datoteke privatni kljuc (pfx / p12 format).<BR> Poziva se samo
     * radi testa da li je korisnik ispravno upisao zaporku za kljuc.<BR>
     *
     * @param keyFile putanja do datoteke
     * @param keyPassword zaporka za koristenje datoteke
     * @return <B>true</B> ako je ispravan unos, inace <B>false</B>.
     */
    private boolean loadKeyStore(String keyFile, char[] keyPassword) {

        KeyStore.PrivateKeyEntry keyEntry;

        // objekt keyStore, koriste proc. za potpisivanje i provjeru potpisa,
        //  i procedura za izracun zastitnog koda
        try {
            // ucitaj jks datoteku
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(keyFile), keyPassword);

            // uzmi kljuc iz pfx datoteke
            keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyStore.aliases().nextElement(),
                    new KeyStore.PasswordProtection(keyPassword));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "loadKeyStore()", e);
            return false;
        }

        return true;

    }

    /**
     * Sprema postavke u datoteku
     *
     * @param fileName naziv datoteke gdje ce se postavke spremiti
     */
    public void postavkeSave(String fileName) {
    
        IniSettings iniSettings = new IniSettings();

        String sekcija;
        
        /**
         * Preslikaj postavke iz lokalnih varijabli.
         */
        sekcija = "Osnovne";
        iniSettings.set(sekcija, "naziv", naziv);
        iniSettings.set(sekcija, "vlasnik", vlasnik);
        iniSettings.set(sekcija, "oib", oib);
        iniSettings.set(sekcija, "poslovnica", poslovnica);
        iniSettings.set(sekcija, "blagajna", blagajna);
        iniSettings.set(sekcija, "kljuc", keyFile);
        iniSettings.set(sekcija, "zaporka", keyPassword);
        iniSettings.set(sekcija, "url", sURL);
        iniSettings.set(sekcija, "USustPdv", String.valueOf(uSustPdv));

        sekcija = "Ispis";
        iniSettings.set(sekcija, "timeout", String.valueOf(timeout));
        iniSettings.set(sekcija, "duzinaLinije", String.valueOf(duzinaLinije));
        iniSettings.set(sekcija, "fontNaziv", String.valueOf(fontNaziv));
        iniSettings.set(sekcija, "fontVelicina", String.valueOf(fontVelicina));
        iniSettings.set(sekcija, "pisac", String.valueOf(pisac));
        final double k = 25.4 / 72; // point --> mm
        iniSettings.set(sekcija, "papirX", dFormat.format(papirX * k));
        iniSettings.set(sekcija, "papirY", dFormat.format(papirY * k));
        iniSettings.set(sekcija, "marginaX", dFormat.format(marginaX * k));
        iniSettings.set(sekcija, "marginaY", dFormat.format(marginaY * k));
        iniSettings.set(sekcija, "ispisnaPovrsinaX", dFormat.format(ispisnaPovrsinaX * k));
        iniSettings.set(sekcija, "ispisnaPovrsinaY", dFormat.format(ispisnaPovrsinaY * k));
        iniSettings.set(sekcija, "formatPapira", formatPapira);
        iniSettings.set(sekcija, "oznakaProizvoda", oznakaProizvoda);
        iniSettings.set(sekcija, "prijeSlanja", prijeSlanja);
        iniSettings.set(sekcija, "poslijeSlanja", poslijeSlanja);

        sekcija = "Sistemske";
        iniSettings.set(sekcija, "brRac", String.valueOf(brRac));
        iniSettings.set(sekcija, "izborBlagajnika", izborBlagajnika);
        iniSettings.set(sekcija, "kolicina", String.valueOf(kolicina));
        iniSettings.set(sekcija, "arhiviraj", String.valueOf(arhiviraj));
        iniSettings.set(sekcija, "transakcijski", String.valueOf(transakcijski));

        sekcija = "Izgled";
        iniSettings.set(sekcija, "font", izgledFont);
        iniSettings.set(sekcija, "velicina", String.valueOf(izgledVelicina));
        iniSettings.set(sekcija, "debljina", String.valueOf(izgledDebljina));

        sekcija = "Stupci";
        for (Integer stupac : new Integer[]{0, 1, 2, 3}) {
            iniSettings.set(sekcija, "stupac" + stupac, String.valueOf(frmBlagajna.getTableColumnWidth(stupac)));
        }
        iniSettings.set(sekcija, "omjer", String.valueOf(frmBlagajna.getSplitterPane()));

        /**
         * Spremi u datoteku.
         */
        try {
            iniSettings.write(fileName);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "postavkeSave(), IOException on " + fileName, ex);
        }


    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlStatus = new javax.swing.JScrollPane();
        txtStatus = new javax.swing.JTextPane();
        pnlIzbornik = new javax.swing.JPanel();
        cmdOK = new javax.swing.JButton();
        cmdIzlaz = new javax.swing.JButton();
        cmdTestIspisa = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlOsnovne = new javax.swing.JPanel();
        lblNaziv = new javax.swing.JLabel();
        lblVlasnik = new javax.swing.JLabel();
        lblOib = new javax.swing.JLabel();
        lblPoslovnica = new javax.swing.JLabel();
        lblPrivatniKljuc = new javax.swing.JLabel();
        lblZaporka = new javax.swing.JLabel();
        lblUrl = new javax.swing.JLabel();
        txtNaziv = new javax.swing.JTextField();
        txtVlasnik = new javax.swing.JTextField();
        txtOib = new javax.swing.JTextField();
        txtPoslovnica = new javax.swing.JTextField();
        txtKljuc = new javax.swing.JTextField();
        txtUrl = new javax.swing.JTextField();
        txtBlagajna = new javax.swing.JTextField();
        lblBlagajna = new javax.swing.JLabel();
        txtZaporka = new javax.swing.JPasswordField();
        chkUSustPdv = new javax.swing.JCheckBox();
        pnlIspis = new javax.swing.JPanel();
        lblTimeout = new javax.swing.JLabel();
        txtTimeout = new javax.swing.JTextField();
        txtDuzinaLinije = new javax.swing.JTextField();
        lblDuzinaLinije = new javax.swing.JLabel();
        lblFont = new javax.swing.JLabel();
        lblVelicina = new javax.swing.JLabel();
        txtVelicina = new javax.swing.JTextField();
        lblPisac = new javax.swing.JLabel();
        lblPapir = new javax.swing.JLabel();
        txtPapirX = new javax.swing.JTextField();
        lblX1 = new javax.swing.JLabel();
        txtPapirY = new javax.swing.JTextField();
        lblMargina = new javax.swing.JLabel();
        txtMarginaX = new javax.swing.JTextField();
        lblX2 = new javax.swing.JLabel();
        txtMarginaY = new javax.swing.JTextField();
        lblIspisnaPovrsina = new javax.swing.JLabel();
        txtIspisnaPovrsinaX = new javax.swing.JTextField();
        lblX3 = new javax.swing.JLabel();
        txtIspisnaPovrsinaY = new javax.swing.JTextField();
        lblFormatPapira = new javax.swing.JLabel();
        cboFormatPapira = new javax.swing.JComboBox();
        cboFont = new javax.swing.JComboBox();
        cboPisac = new javax.swing.JComboBox();
        lblProizvodOznaka = new javax.swing.JLabel();
        txtProizvodOznaka = new javax.swing.JTextField();
        lblPrijeSlanja = new javax.swing.JLabel();
        txtPrijeSlanja = new javax.swing.JTextField();
        lblPoslijeSlanja = new javax.swing.JLabel();
        txtPoslijeSlanja = new javax.swing.JTextField();
        pnlIzgled = new javax.swing.JPanel();
        lblIzgledFont = new javax.swing.JLabel();
        lblIzgledVelicina = new javax.swing.JLabel();
        lblIzgledDebljina = new javax.swing.JLabel();
        cboIzgledFont = new javax.swing.JComboBox();
        sldIzgledVelicina = new javax.swing.JSlider();
        sldIzgledDebljina = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Postavke");
        setAlwaysOnTop(true);

        txtStatus.setEditable(false);
        pnlStatus.setViewportView(txtStatus);

        pnlIzbornik.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        cmdOK.setText("OK");
        cmdOK.setToolTipText("Potvrdi i spremi promjene");
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

        cmdIzlaz.setText("Izlaz");
        cmdIzlaz.setToolTipText("Odustani i vrati na stare vrijednosti");
        cmdIzlaz.setMaximumSize(new java.awt.Dimension(78, 25));
        cmdIzlaz.setMinimumSize(new java.awt.Dimension(78, 25));
        cmdIzlaz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdIzlazActionPerformed(evt);
            }
        });
        cmdIzlaz.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdIzlazFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdIzlazFocusLost(evt);
            }
        });

        cmdTestIspisa.setText("Test ispisa");
        cmdTestIspisa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdTestIspisaActionPerformed(evt);
            }
        });
        cmdTestIspisa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdTestIspisaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdTestIspisaFocusLost(evt);
            }
        });

        javax.swing.GroupLayout pnlIzbornikLayout = new javax.swing.GroupLayout(pnlIzbornik);
        pnlIzbornik.setLayout(pnlIzbornikLayout);
        pnlIzbornikLayout.setHorizontalGroup(
            pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmdOK, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cmdIzlaz, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cmdTestIspisa, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pnlIzbornikLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdIzlaz, cmdOK, cmdTestIspisa});

        pnlIzbornikLayout.setVerticalGroup(
            pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIzbornikLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmdOK, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdTestIspisa, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmdIzlaz, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlIzbornikLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdIzlaz, cmdOK, cmdTestIspisa});

        pnlOsnovne.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblNaziv.setText("Naziv:");

        lblVlasnik.setText("Vlasnik:");

        lblOib.setText("Oib:");

        lblPoslovnica.setText("Poslovnica:");

        lblPrivatniKljuc.setText("Privatni kljuc (pfx):");

        lblZaporka.setText("Zaporka:");

        lblUrl.setText("URL:");

        txtNaziv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNazivActionPerformed(evt);
            }
        });
        txtNaziv.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNazivFocusGained(evt);
            }
        });

        txtVlasnik.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVlasnikFocusGained(evt);
            }
        });

        txtOib.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOibFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtOibFocusLost(evt);
            }
        });

        txtPoslovnica.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPoslovnicaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPoslovnicaFocusLost(evt);
            }
        });

        txtKljuc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtKljucFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtKljucFocusLost(evt);
            }
        });

        txtUrl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUrlFocusGained(evt);
            }
        });

        txtBlagajna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBlagajnaActionPerformed(evt);
            }
        });
        txtBlagajna.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBlagajnaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBlagajnaFocusLost(evt);
            }
        });

        lblBlagajna.setText("Br. blagajne:");

        txtZaporka.setText("jPasswordField1");
        txtZaporka.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtZaporkaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtZaporkaFocusLost(evt);
            }
        });

        chkUSustPdv.setText("U sustavu PDVa");
        chkUSustPdv.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                chkUSustPdvFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                chkUSustPdvFocusLost(evt);
            }
        });

        javax.swing.GroupLayout pnlOsnovneLayout = new javax.swing.GroupLayout(pnlOsnovne);
        pnlOsnovne.setLayout(pnlOsnovneLayout);
        pnlOsnovneLayout.setHorizontalGroup(
            pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOsnovneLayout.createSequentialGroup()
                .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOsnovneLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(lblBlagajna))
                    .addGroup(pnlOsnovneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblZaporka)
                            .addComponent(lblPrivatniKljuc)
                            .addComponent(lblUrl)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOsnovneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPoslovnica, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblOib, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblVlasnik, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblNaziv, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOsnovneLayout.createSequentialGroup()
                        .addComponent(chkUSustPdv)
                        .addGap(0, 374, Short.MAX_VALUE))
                    .addComponent(txtNaziv, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtVlasnik, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtOib, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtPoslovnica, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtBlagajna)
                    .addComponent(txtKljuc)
                    .addComponent(txtZaporka)
                    .addComponent(txtUrl))
                .addContainerGap())
        );
        pnlOsnovneLayout.setVerticalGroup(
            pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOsnovneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNaziv)
                    .addComponent(txtNaziv))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVlasnik)
                    .addComponent(txtVlasnik))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOib)
                    .addComponent(txtOib))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPoslovnica)
                    .addComponent(txtPoslovnica))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBlagajna)
                    .addComponent(lblBlagajna))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtKljuc)
                    .addComponent(lblPrivatniKljuc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblZaporka)
                    .addComponent(txtZaporka, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOsnovneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUrl)
                    .addComponent(lblUrl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUSustPdv)
                .addGap(20, 20, 20))
        );

        jTabbedPane1.addTab("Osnovne", pnlOsnovne);

        lblTimeout.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTimeout.setText("Timeout:");
        lblTimeout.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txtTimeout.setText("jTextField1");
        txtTimeout.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTimeoutFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTimeoutFocusLost(evt);
            }
        });

        txtDuzinaLinije.setText("jTextField1");
        txtDuzinaLinije.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDuzinaLinijeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDuzinaLinijeFocusLost(evt);
            }
        });

        lblDuzinaLinije.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDuzinaLinije.setText("Duzina linije:");
        lblDuzinaLinije.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lblFont.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFont.setText("Font:");
        lblFont.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lblVelicina.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblVelicina.setText("Velicina:");
        lblVelicina.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txtVelicina.setText("jTextField4");
        txtVelicina.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVelicinaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVelicinaFocusLost(evt);
            }
        });

        lblPisac.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPisac.setText("Pisac:");
        lblPisac.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lblPapir.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPapir.setText("Papir:");
        lblPapir.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txtPapirX.setText("jTextField6");
        txtPapirX.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPapirXFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPapirXFocusLost(evt);
            }
        });

        lblX1.setText("x");

        txtPapirY.setText("jTextField7");
        txtPapirY.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPapirYFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPapirYFocusLost(evt);
            }
        });

        lblMargina.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMargina.setText("Margina:");
        lblMargina.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txtMarginaX.setText("jTextField6");
        txtMarginaX.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMarginaXFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMarginaXFocusLost(evt);
            }
        });

        lblX2.setText("x");

        txtMarginaY.setText("jTextField7");
        txtMarginaY.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMarginaYFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMarginaYFocusLost(evt);
            }
        });

        lblIspisnaPovrsina.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIspisnaPovrsina.setText("Ispisna povrsina:");
        lblIspisnaPovrsina.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txtIspisnaPovrsinaX.setText("jTextField6");
        txtIspisnaPovrsinaX.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtIspisnaPovrsinaXFocusGained(evt);
            }
        });

        lblX3.setText("x");

        txtIspisnaPovrsinaY.setText("jTextField7");
        txtIspisnaPovrsinaY.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtIspisnaPovrsinaYFocusGained(evt);
            }
        });

        lblFormatPapira.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFormatPapira.setText("Format papira:");
        lblFormatPapira.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        cboFormatPapira.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFormatPapiraActionPerformed(evt);
            }
        });

        cboFont.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboFontFocusGained(evt);
            }
        });

        cboPisac.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboPisac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPisacActionPerformed(evt);
            }
        });

        lblProizvodOznaka.setText("Proizvod oznaka:");

        txtProizvodOznaka.setText("jTextField1");
        txtProizvodOznaka.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProizvodOznakaFocusGained(evt);
            }
        });

        lblPrijeSlanja.setText("Prije slanja racuna:");

        txtPrijeSlanja.setText("jTextField1");
        txtPrijeSlanja.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPrijeSlanjaFocusGained(evt);
            }
        });

        lblPoslijeSlanja.setText("Poslije slanja racuna:");

        txtPoslijeSlanja.setText("jTextField1");
        txtPoslijeSlanja.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPoslijeSlanjaFocusGained(evt);
            }
        });

        javax.swing.GroupLayout pnlIspisLayout = new javax.swing.GroupLayout(pnlIspis);
        pnlIspis.setLayout(pnlIspisLayout);
        pnlIspisLayout.setHorizontalGroup(
            pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIspisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPoslijeSlanja)
                    .addComponent(lblPrijeSlanja)
                    .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblDuzinaLinije, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTimeout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFont, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblVelicina, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblPisac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblPapir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblMargina, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblIspisnaPovrsina, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFormatPapira, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lblProizvodOznaka))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIspisLayout.createSequentialGroup()
                        .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cboPisac, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboFont, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDuzinaLinije, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTimeout)
                            .addComponent(txtVelicina, javax.swing.GroupLayout.Alignment.LEADING))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIspisLayout.createSequentialGroup()
                        .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPoslijeSlanja, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPrijeSlanja, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProizvodOznaka, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboFormatPapira, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlIspisLayout.createSequentialGroup()
                                .addComponent(txtMarginaX, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblX2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMarginaY, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlIspisLayout.createSequentialGroup()
                                .addComponent(txtPapirX)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblX1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPapirY))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlIspisLayout.createSequentialGroup()
                                .addComponent(txtIspisnaPovrsinaX)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblX3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIspisnaPovrsinaY)))
                        .addGap(259, 259, 259))))
        );
        pnlIspisLayout.setVerticalGroup(
            pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIspisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTimeout)
                    .addComponent(txtTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDuzinaLinije)
                    .addComponent(txtDuzinaLinije, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFont)
                    .addComponent(cboFont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVelicina)
                    .addComponent(txtVelicina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPisac)
                    .addComponent(cboPisac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPapir)
                    .addComponent(txtPapirX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblX1)
                    .addComponent(txtPapirY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMargina)
                    .addComponent(txtMarginaX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblX2)
                    .addComponent(txtMarginaY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIspisnaPovrsinaX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblX3)
                    .addComponent(txtIspisnaPovrsinaY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIspisnaPovrsina))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFormatPapira)
                    .addGroup(pnlIspisLayout.createSequentialGroup()
                        .addComponent(cboFormatPapira, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtProizvodOznaka, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblProizvodOznaka))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrijeSlanja)
                    .addComponent(txtPrijeSlanja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIspisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPoslijeSlanja)
                    .addComponent(txtPoslijeSlanja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Ispis", pnlIspis);

        lblIzgledFont.setText("Font:");

        lblIzgledVelicina.setText("Velicina:");

        lblIzgledDebljina.setText("Debljina dugmica:");

        cboIzgledFont.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboIzgledFont.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboIzgledFontFocusGained(evt);
            }
        });

        sldIzgledVelicina.setMaximum(48);
        sldIzgledVelicina.setMinimum(8);
        sldIzgledVelicina.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldIzgledVelicinaStateChanged(evt);
            }
        });
        sldIzgledVelicina.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sldIzgledVelicinaFocusGained(evt);
            }
        });

        sldIzgledDebljina.setMaximum(60);
        sldIzgledDebljina.setMinimum(10);
        sldIzgledDebljina.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldIzgledDebljinaStateChanged(evt);
            }
        });
        sldIzgledDebljina.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sldIzgledDebljinaFocusGained(evt);
            }
        });

        javax.swing.GroupLayout pnlIzgledLayout = new javax.swing.GroupLayout(pnlIzgled);
        pnlIzgled.setLayout(pnlIzgledLayout);
        pnlIzgledLayout.setHorizontalGroup(
            pnlIzgledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIzgledLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIzgledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIzgledDebljina, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblIzgledVelicina, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblIzgledFont, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlIzgledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboIzgledFont, 0, 491, Short.MAX_VALUE)
                    .addGroup(pnlIzgledLayout.createSequentialGroup()
                        .addGroup(pnlIzgledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(sldIzgledVelicina, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(sldIzgledDebljina, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlIzgledLayout.setVerticalGroup(
            pnlIzgledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIzgledLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlIzgledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIzgledFont)
                    .addComponent(cboIzgledFont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlIzgledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sldIzgledVelicina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIzgledVelicina))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlIzgledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIzgledDebljina)
                    .addComponent(sldIzgledDebljina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(236, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Izgled", pnlIzgled);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlStatus)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlIzbornik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlIzbornik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNazivActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNazivActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNazivActionPerformed

    private void cmdOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOKActionPerformed

        // provjeri da li su svi unosi crne boje,
        // inace ako su crveni, moze biti greska
        boolean greska = false;

        Component[] paneli = {pnlOsnovne, pnlIspis};
        for (Component panel : paneli) {
            JPanel pnl = (JPanel) panel;
            for (Component comp : pnl.getComponents()) {
                if (comp instanceof JTextField && comp.getForeground() == Color.RED) {
                    greska = true;
                }
            }
        }


        if (greska) {
            JOptionPane.showMessageDialog(
                    this, "Provjerite ispravnost unosa. Neispravni unosi su obojani crveno.", "Postavke", JOptionPane.ERROR_MESSAGE);
        } else {
            /**
             * Posebna provjera duzine linije.
             * Moguce je da korisnik unese parametre koji
             * ne odgovaraju ispravno duzini linije.
             */
            
            /* 
             * Faktor konverzije, mm --> point
             */
            final double k = 72 / 25.4;

            double ispisnaPovrsinaX = Double.parseDouble(txtIspisnaPovrsinaX.getText()) * k;
            double ispisnaPovrsinaY = Double.parseDouble(txtIspisnaPovrsinaY.getText()) * k;
            String imeFonta = String.valueOf(cboFont.getSelectedItem());
            int visinaFonta = Integer.parseInt(txtVelicina.getText());
            int brojZnakova = Integer.parseInt(txtDuzinaLinije.getText());

            /**
             * Da li svi znakovi stanu u ispisnu povrsinu (po duzini, X koord.)?
             */
            boolean prilagodba = duzinaLinijeIspravna(ispisnaPovrsinaX, ispisnaPovrsinaY, imeFonta, visinaFonta, brojZnakova);
            if (!prilagodba) {
                txtDuzinaLinije.setText(String.valueOf(maxZnakova));
            }
            
            /**
             * Preslikaj unose iz text polja u lokalne varijable.
             */
            
            // Osnovne postavke
            naziv = txtNaziv.getText();
            vlasnik = txtVlasnik.getText();
            oib = txtOib.getText();
            poslovnica = txtPoslovnica.getText();
            blagajna = txtBlagajna.getText();
            keyFile = txtKljuc.getText();
            keyPassword = String.valueOf(txtZaporka.getPassword());
            sURL = txtUrl.getText();
            uSustPdv = chkUSustPdv.isSelected();

            // Ispis postavke
            timeout = Integer.parseInt(txtTimeout.getText());
            duzinaLinije = Integer.parseInt(txtDuzinaLinije.getText());
            fontNaziv = String.valueOf(cboFont.getSelectedItem());
            fontVelicina = Integer.parseInt(txtVelicina.getText());
            pisac = String.valueOf(cboPisac.getSelectedItem());
            papirX = Double.parseDouble(txtPapirX.getText()) * k;
            papirY = Double.parseDouble(txtPapirY.getText()) * k;
            marginaX = Double.parseDouble(txtMarginaX.getText()) * k;
            marginaY = Double.parseDouble(txtMarginaY.getText()) * k;
            this.ispisnaPovrsinaX = Double.parseDouble(txtIspisnaPovrsinaX.getText()) * k;
            this.ispisnaPovrsinaY = Double.parseDouble(txtIspisnaPovrsinaY.getText()) * k;
            formatPapira = String.valueOf(cboFormatPapira.getSelectedItem());
            oznakaProizvoda = txtProizvodOznaka.getText();
            prijeSlanja = txtPrijeSlanja.getText();
            poslijeSlanja = txtPoslijeSlanja.getText();

            // Izgled postavke
            izgledFont = String.valueOf(cboIzgledFont.getSelectedItem());
            izgledVelicina = sldIzgledVelicina.getValue();
            izgledDebljina = sldIzgledDebljina.getValue();
            izgled = new Izgled(izgledFont, izgledVelicina,
                izgledDebljina);
                    
            // ova funkcija sprema vr.lokalnih var. u postavke.txt
            postavkeSave("postavke.txt");

            // postavi zastavicu prikaza na true
            prikaziFileOpen = true;

            // omoguci prikaz glavnog prozora
            frmBlagajna.toggleScreen(true);

            // azuriraj racun
            frmBlagajna.initHeaderAndFooter();
            frmBlagajna.updateRacun();

            // UI
            ui();
            frmBlagajna.ui();
          
            this.setVisible(false);
        }
    }//GEN-LAST:event_cmdOKActionPerformed

    private void cmdIzlazActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdIzlazActionPerformed

        // vrati boju poljima u crno
        for (Component cmp : pnlOsnovne.getComponents()) {
            cmp.setForeground(Color.BLACK);
        }

        // vrati polja za unos na stare vr.
        onExit();
    }//GEN-LAST:event_cmdIzlazActionPerformed

    // funkcija za selektiranje txt polja
    private void selText(JTextField txtField) {

        txtField.setSelectionStart(0);
        txtField.setSelectionEnd(txtField.getText().length());

    }

    private void txtNazivFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNazivFocusGained

        txtStatus.setText("Naziv objekta ili poslovnog prostora.\nNpr. Kafic Bono-Dias");
        selText(txtNaziv);

    }//GEN-LAST:event_txtNazivFocusGained

    private void txtVlasnikFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVlasnikFocusGained

        txtStatus.setText("Ime i prezime vlasnika objekta ili poslovnog prostora.\nNpr. Marko Markic");
        selText(txtVlasnik);

    }//GEN-LAST:event_txtVlasnikFocusGained

    private void txtOibFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOibFocusGained

        txtStatus.setText("OIB koji ste predali Fini, kod zahtjeva za kljuc.\nNpr. 12345678911");

        if (txtKljuc.getText().endsWith("test.pfx")) {
            txtOib.setText("11980190612");
        }

        selText(txtOib);

    }//GEN-LAST:event_txtOibFocusGained

    private void txtPoslovnicaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPoslovnicaFocusGained

        txtStatus.setText("Naziv poslovnice. Ne smije sadrzavati razmake i znakove osim slove i brojeva.\nNpr. Poslovnica1");
        selText(txtPoslovnica);

    }//GEN-LAST:event_txtPoslovnicaFocusGained

    private void txtUrlFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUrlFocusGained

        txtStatus.setText("URL za slanje racuna i prijave poslovnog prostora. Trebao bi se automatski namjestiti u ovisnosti koristi li se testni certifikat (test.pfx) ili produkcijski.\nNpr. " + sURL);
        selText(txtUrl);

        if (txtKljuc.getText().endsWith("test.pfx")) {
            txtUrl.setText("https://cistest.apis-it.hr:8449/FiskalizacijaServiceTest");
        } else {
            txtUrl.setText("https://cis.porezna-uprava.hr:8449/FiskalizacijaService");
        }
    }//GEN-LAST:event_txtUrlFocusGained

    private void txtKljucFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtKljucFocusGained

        txtStatus.setText("Datoteka koja sadrzi privatni kljuc. Mozete ju prepoznati po nastavku pfx.\nZatrazite produkcijski certifikat of Fine, snimite ga u:\n" + System.getProperty("user.dir"));

        if (prikaziFileOpen) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
            FileFilter filter1 = new ExtensionFileFilter("Privatni kljuc", new String[]{"pfx", "p12"});
            jfc.setFileFilter(filter1);
            //jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtKljuc.setText(jfc.getSelectedFile().toString());
            }

            prikaziFileOpen = false;
        }

        selText(txtKljuc);

    }//GEN-LAST:event_txtKljucFocusGained

    private void txtBlagajnaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBlagajnaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBlagajnaActionPerformed

    private void txtBlagajnaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBlagajnaFocusGained

        txtStatus.setText("Broj blagajne/kase. Postavite na 1, ili na redni broj blagajne ako ih imate vise u istoj poslovnici.");
        selText(txtBlagajna);

    }//GEN-LAST:event_txtBlagajnaFocusGained

    private void txtOibFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOibFocusLost
        // jednostavna logika
        // ako nema 11 znakova, ili nisu svi brojke tada zacrveni
        String tmpOib = txtOib.getText();
        // zastavica greske
        boolean greska = false;

        if (tmpOib.length() != 11) {
            greska = true;
        } else {
            for (int i = 0; i < tmpOib.length(); i++) {
                // da li je znak izvan raspona 0 - 9 ?
                if (tmpOib.charAt(i) < '0' || tmpOib.charAt(i) > '9') {
                    greska = true;
                }
            }
        }

        if (greska) {
            txtOib.setForeground(Color.RED);
        } else {
            txtOib.setForeground(Color.BLACK);
        }

    }//GEN-LAST:event_txtOibFocusLost

    private void txtPoslovnicaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPoslovnicaFocusLost

        // jednostavna logika
        // ako ima vise od 20 znakova, ili nisu svi brojke i slova tada zacrveni
        String tmpPoslovnica = txtPoslovnica.getText();
        // zastavica greske
        boolean greska = false;

        if (tmpPoslovnica.length() > 20) {
            greska = true;
        } else {
            for (int i = 0; i < tmpPoslovnica.length(); i++) {
                // da li je znak izvan raspona 0 - 9 ?
                if (tmpPoslovnica.charAt(i) < '0' || tmpPoslovnica.charAt(i) > 'z') {
                    greska = true;
                }
            }
        }

        if (greska) {
            txtPoslovnica.setForeground(Color.RED);
        } else {
            txtPoslovnica.setForeground(Color.BLACK);
        }

    }//GEN-LAST:event_txtPoslovnicaFocusLost

    private void txtBlagajnaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBlagajnaFocusLost

        // jednostavna logika
        // mora biti broj
        String tmpBlagajna = txtBlagajna.getText();
        // zastavica greske
        boolean greska = false;

        // hm, ne podrzavamo vise od 99 blagajni u jednom poslovnom prostoru
        if (tmpBlagajna.length() > 2) {
            greska = true;
        } else {
            for (int i = 0; i < tmpBlagajna.length(); i++) {
                // da li je znak izvan raspona 0 - 9 ?
                if (tmpBlagajna.charAt(i) < '0' || tmpBlagajna.charAt(i) > '9') {
                    greska = true;
                }
            }
        }

        if (greska) {
            txtBlagajna.setForeground(Color.RED);
        } else {
            txtBlagajna.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtBlagajnaFocusLost

    private void txtKljucFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtKljucFocusLost

        // jednostavna logika
        // provjeri postojanje datoteke

        // zastavica greske
        boolean greska = false;

        File fKljuc = new File(txtKljuc.getText());
        if (!fKljuc.exists()) {
            greska = true;
        }

        if (greska) {
            txtKljuc.setForeground(Color.RED);
        } else {
            txtKljuc.setForeground(Color.BLACK);
        }

    }//GEN-LAST:event_txtKljucFocusLost

    private void cmdOKFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdOKFocusGained
        cmdOK.setBackground(boja2);
    }//GEN-LAST:event_cmdOKFocusGained

    private void cmdOKFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdOKFocusLost
        cmdOK.setBackground(boja1);
    }//GEN-LAST:event_cmdOKFocusLost

    private void cmdIzlazFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIzlazFocusGained
        cmdIzlaz.setBackground(boja2);
    }//GEN-LAST:event_cmdIzlazFocusGained

    private void cmdIzlazFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIzlazFocusLost
        cmdIzlaz.setBackground(boja1);
    }//GEN-LAST:event_cmdIzlazFocusLost

    private void txtZaporkaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtZaporkaFocusGained

        txtStatus.setText("Unesite zaporku koju ste dobili uz vas privatni kljuc. Podatak dostavlja Fina.");

        if (txtKljuc.getText().endsWith("test.pfx")) {
            String zaporka = "POS3xtr4!";
            txtZaporka.setText(zaporka);
            txtOib.setText("11980190612");
            logger.info("Postavljena zaporka: " + zaporka
                    + "\nPostavljen oib: " + txtOib.getText());
        }
        selText(txtZaporka);

    }//GEN-LAST:event_txtZaporkaFocusGained

    private void txtZaporkaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtZaporkaFocusLost

        // jednostavna logika
        // pokusaj ucitati priv.kljuc i tako provjeri zaporku

        // zastavica da li je ok
        boolean ok = loadKeyStore(txtKljuc.getText(), txtZaporka.getPassword());

        if (!ok) {
            txtZaporka.setForeground(Color.RED);
        } else {
            txtZaporka.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtZaporkaFocusLost

    private void chkUSustPdvFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chkUSustPdvFocusGained
        txtStatus.setText("Ako ste u sustavu pdv-a, tada postavite. Ova postavka utjece na prijavu racuna u PU.");
    }//GEN-LAST:event_chkUSustPdvFocusGained

    private void chkUSustPdvFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chkUSustPdvFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_chkUSustPdvFocusLost

    private void txtTimeoutFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTimeoutFocusGained
        txtStatus.setText("Timeout parametar sluzi za definiranje max. vremena koliko program ceka na JIR odgovor iz PU.\n"
                + "Za DSL veze moze biti manje od 5, za wifi i broadband veze sa velikim kasnjenjem treba biti barem 7.\n"
                + "Jedinica: sekunda.");
        selText(txtTimeout);
    }//GEN-LAST:event_txtTimeoutFocusGained

    private void txtTimeoutFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTimeoutFocusLost
        // jednostavna logika
        // mora biti broj
        String tmpTimeout = txtTimeout.getText();
        // zastavica greske
        boolean greska = false;

        // hm, ne podrzavamo vise od 99 sec. timeout
        if (tmpTimeout.length() > 2) {
            greska = true;
        } else {
            for (int i = 0; i < tmpTimeout.length(); i++) {
                // da li je znak izvan raspona 0 - 9 ?
                if (tmpTimeout.charAt(i) < '0' || tmpTimeout.charAt(i) > '9') {
                    greska = true;
                }
            }
        }

        if (greska) {
            txtTimeout.setForeground(Color.RED);
        } else {
            txtTimeout.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtTimeoutFocusLost

    /**
     * Test da li odabrani font, velicina fonta, duzina linije i ispisna povrsina
     * odgovaraju ispravnom ispisu.
     * @param ispisnaPovrsinaX ispisna povrsina u point-ima, sirina
     * @param ispisnaPovrsinaY ispisna povrsina u point-ima, visina
     * @param imeFonta ime fonta
     * @param visinaFonta visina ili velicina fonta
     * @param brojZnakova broj znakova (max.) u liniji teksa
     * @return <I>true</I> ako je duzina linije ispravna, inace <I>false</I>
     */
    private boolean duzinaLinijeIspravna(double ispisnaPovrsinaX, double ispisnaPovrsinaY, String imeFonta, int visinaFonta, int brojZnakova) {
        boolean prilagodba = false;
        /**
         * Metrika, koliko su siroki znakovi.
         */
        
        Font testFont = new Font(imeFonta, Font.PLAIN, visinaFonta);
        
        Graphics2D graphics2D = (Graphics2D) frmBlagajna.getTxtRacun().getGraphics();
        graphics2D.translate(ispisnaPovrsinaX, ispisnaPovrsinaY);                
        graphics2D.setFont(testFont);
        FontMetrics fontMetrics = graphics2D.getFontMetrics(testFont);

        maxZnakova = (int) Math.round(ispisnaPovrsinaX / fontMetrics.stringWidth("A") );
                
        String template = "";
        for (int i = 0; i < brojZnakova; i++) {
            template = template + "A";
        }
        prilagodba = (fontMetrics.stringWidth(template) < ispisnaPovrsinaX );
        
        return prilagodba;
    }
    
    private int maxZnakova = 0;
    
    private void txtDuzinaLinijeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDuzinaLinijeFocusGained
        /* 
         * Faktor konverzije, mm --> point
         */
        final double k = 72 / 25.4;

        double ispisnaPovrsinaX = Double.parseDouble(txtIspisnaPovrsinaX.getText()) * k;
        double ispisnaPovrsinaY = Double.parseDouble(txtIspisnaPovrsinaY.getText()) * k;
        String imeFonta = String.valueOf(cboFont.getSelectedItem());
        int visinaFonta = Integer.parseInt(txtVelicina.getText());
        int brojZnakova = Integer.parseInt(txtDuzinaLinije.getText());

        /**
         * Da li svi znakovi stanu u ispisnu povrsinu (po duzini, X koord.)?
         */
        boolean prilagodba = duzinaLinijeIspravna(ispisnaPovrsinaX, ispisnaPovrsinaY, imeFonta, visinaFonta, brojZnakova);

        if (!prilagodba) {
            txtDuzinaLinije.setForeground(Color.RED);
        } else {
            txtDuzinaLinije.setForeground(Color.BLACK);
        }

        txtStatus.setText("Broj znakova u jednom redu. Max.: " + maxZnakova + ".\n"
                + "Na max.broj znakova koji stanu u jedan red pri ispisu, ovise font i velicina postavke kao i sirina papira.\n");
        selText(txtDuzinaLinije);
    }//GEN-LAST:event_txtDuzinaLinijeFocusGained

    private void txtDuzinaLinijeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDuzinaLinijeFocusLost
        // jednostavna logika
        // mora biti broj
        String tmpDuzineLinije = txtDuzinaLinije.getText();
        // zastavica greske
        boolean greska = false;

        // hm, ne podrzavamo vise od 999 znakova u jednom redu
        if (tmpDuzineLinije.length() > 3) {
            greska = true;
        } else {
            for (int i = 0; i < tmpDuzineLinije.length(); i++) {
                // da li je znak izvan raspona 0 - 9 ?
                if (tmpDuzineLinije.charAt(i) < '0' || tmpDuzineLinije.charAt(i) > '9') {
                    greska = true;
                }
            }
        }
        /* 
         * Faktor konverzije, mm --> point
         */
        final double k = 72 / 25.4;

        double ispisnaPovrsinaX = Double.parseDouble(txtIspisnaPovrsinaX.getText()) * k;
        double ispisnaPovrsinaY = Double.parseDouble(txtIspisnaPovrsinaY.getText()) * k;
        String imeFonta = String.valueOf(cboFont.getSelectedItem());
        int visinaFonta = Integer.parseInt(txtVelicina.getText());
        int brojZnakova = Integer.parseInt(txtDuzinaLinije.getText());

        /**
         * Da li svi znakovi stanu u ispisnu povrsinu (po duzini, X koord.)?
         */
        boolean prilagodba = duzinaLinijeIspravna(ispisnaPovrsinaX, ispisnaPovrsinaY, imeFonta, visinaFonta, brojZnakova);
        
        if (!prilagodba) {
            greska = true;
        }
        
        if (greska) {
            txtDuzinaLinije.setForeground(Color.RED);
        } else {
            txtDuzinaLinije.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtDuzinaLinijeFocusLost

    private void txtVelicinaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVelicinaFocusGained

        selText(txtVelicina);

        txtStatus.setText("Velicina fonta, mjerena u tockama.\n"
                + "Jedinica: 1 tocka = 1/72 inch.");
    }//GEN-LAST:event_txtVelicinaFocusGained

    private void txtVelicinaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVelicinaFocusLost
        // jednostavna logika
        // mora biti broj
        String tmpVelicina = txtVelicina.getText();
        // zastavica greske
        boolean greska = false;

        // hm, ne podrzavamo fontove vece od 99
        if (tmpVelicina.length() > 3) {
            greska = true;
        } else {
            for (int i = 0; i < tmpVelicina.length(); i++) {
                // da li je znak izvan raspona 0 - 9 ?
                if (tmpVelicina.charAt(i) < '0' || tmpVelicina.charAt(i) > '9') {
                    greska = true;
                }
            }
        }

        if (greska) {
            txtVelicina.setForeground(Color.RED);
        } else {
            txtVelicina.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtVelicinaFocusLost

    private void txtPapirXFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPapirXFocusGained
        selText(txtPapirX);
        txtStatus.setText("Sirina papira. U milimetrima.");
    }//GEN-LAST:event_txtPapirXFocusGained

    private void txtPapirYFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPapirYFocusGained
        selText(txtPapirY);
        txtStatus.setText("Duljina papira. U milimetrima.");
    }//GEN-LAST:event_txtPapirYFocusGained

    private void txtMarginaXFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMarginaXFocusGained
        selText(txtMarginaX);
        txtStatus.setText("Lijeva margina gdje pocinje ispis. Ako se koristi POS pisac, moze se postaviti na 0. U milimetrima.");
    }//GEN-LAST:event_txtMarginaXFocusGained

    private void txtMarginaYFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMarginaYFocusGained
        selText(txtMarginaY);
        txtStatus.setText("Gornja margina gdje pocinje ispis. Ako se koristi POS pisac, moze se postaviti na 0. U milimetrima.");
    }//GEN-LAST:event_txtMarginaYFocusGained

    private void txtIspisnaPovrsinaXFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIspisnaPovrsinaXFocusGained
        selText(txtIspisnaPovrsinaX);
        txtStatus.setText("Ispisna povrsina, sirina. Trebala bi biti manja od velicine papira ili jednaka. U milimetrima.");
    }//GEN-LAST:event_txtIspisnaPovrsinaXFocusGained

    private void txtIspisnaPovrsinaYFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIspisnaPovrsinaYFocusGained
        selText(txtIspisnaPovrsinaY);
        txtStatus.setText("Ispisna povrsina, duzina. Trebala bi biti manja od velicine papira ili jednaka. U milimetrima.");
    }//GEN-LAST:event_txtIspisnaPovrsinaYFocusGained

    private void cboFormatPapiraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFormatPapiraActionPerformed
        txtStatus.setText("Odabrani format papira: " + String.valueOf(cboFormatPapira.getSelectedItem()));
    }//GEN-LAST:event_cboFormatPapiraActionPerformed

    private void cboFontFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboFontFocusGained
        txtStatus.setText("Odaberite font koji ima jednaku sirinu svakog znaka tzv. monospaced font.\n"
                + "Ako niste sigurni, odaberite Monospaced, tada ce se izabrati prvi dostupni font sa navedenim svojstvom.\n");

    }//GEN-LAST:event_cboFontFocusGained

    private void cboPisacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPisacActionPerformed
        /**
         * Print lista zadataka.
         */
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        PrintService[] printers = PrinterJob.lookupPrintServices();
        for (PrintService printService : printers) {
            if (printService.getName().equalsIgnoreCase(String.valueOf(cboPisac.getSelectedItem()))) {
                try {
                    printerJob.setPrintService(printService);
                } catch (PrinterException ex) {
                    logger.log(Level.SEVERE, "cboPisacActionPerformed(), printerJob.setPrintService()", ex);
                }
                break;
            }
        }
        /**
         * Format stranice za ispis na papir.
         */
        PageFormat pageFormat = printerJob.defaultPage();
        /**
         * Info za margine i papir.
         */
        Paper paper = pageFormat.getPaper();
        /**
         * Faktor konverzije, point --> mm
         */
        final double k = 25.4 / 72;
        /**
         * Workaround za problematican ispis na uske trake.
         */
        if ((paper.getImageableWidth() < 0) || (paper.getImageableHeight() < 0)) {
            paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight());
            printerJob.validatePage(pageFormat);
        }

        /**
         * Napuni listu sa podrzanim formatima papira.
         */
        popuniFormatePapira();

        txtPapirX.setText(dFormat.format(paper.getWidth() * k));
        txtPapirY.setText(dFormat.format(paper.getHeight() * k));
        txtMarginaX.setText(dFormat.format(paper.getImageableX() * k));
        txtMarginaY.setText(dFormat.format(paper.getImageableY() * k));
        txtIspisnaPovrsinaX.setText(dFormat.format(paper.getImageableWidth() * k));
        txtIspisnaPovrsinaY.setText(dFormat.format(paper.getImageableHeight() * k));

        txtStatus.setText("Odabrani pisac: " + String.valueOf(cboPisac.getSelectedItem()));

    }//GEN-LAST:event_cboPisacActionPerformed

    private void txtPapirXFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPapirXFocusLost

        txtPapirX.setText(
                txtPapirX.getText().replaceAll(",", "."));

        double papirSirina = Double.parseDouble(txtPapirX.getText());
        double margina = Double.parseDouble(txtMarginaX.getText());

        txtIspisnaPovrsinaX.setText(
                dFormat.format(papirSirina - 2 * margina));

    }//GEN-LAST:event_txtPapirXFocusLost

    private void txtMarginaXFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMarginaXFocusLost

        txtMarginaX.setText(
                txtMarginaX.getText().replaceAll(",", "."));

        double papirSirina = Double.parseDouble(txtPapirX.getText());
        double margina = Double.parseDouble(txtMarginaX.getText());

        txtIspisnaPovrsinaX.setText(
                dFormat.format(papirSirina - 2 * margina));

    }//GEN-LAST:event_txtMarginaXFocusLost

    private void txtPapirYFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPapirYFocusLost
        txtPapirY.setText(
                txtPapirY.getText().replaceAll(",", "."));
        double papirDuzina = Double.parseDouble(txtPapirY.getText());
        double margina = Double.parseDouble(txtMarginaY.getText());
        txtIspisnaPovrsinaY.setText(
                dFormat.format(papirDuzina - 2 * margina));

    }//GEN-LAST:event_txtPapirYFocusLost

    private void txtMarginaYFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMarginaYFocusLost
        txtMarginaY.setText(
                txtMarginaY.getText().replaceAll(",", "."));
        double papirDuzina = Double.parseDouble(txtPapirY.getText());
        double margina = Double.parseDouble(txtMarginaY.getText());
        txtIspisnaPovrsinaY.setText(
                dFormat.format(papirDuzina - 2 * margina));

    }//GEN-LAST:event_txtMarginaYFocusLost

    private void cmdTestIspisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdTestIspisaActionPerformed

        /**
         * Testni ispis na temelju odabrani postavki (ne trenutno vazecih).
         * Odabrane postavke su one iz txt polja koje korisnik namjesta i zeli
         * napraviti test prije OK odabira.
         */
        Racun r1 = frmBlagajna.getRacun();
        
        String text = "";                
        String datum = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",
                Locale.ENGLISH).format(new Date());
        DecimalFormat dFormat = new DecimalFormat("0.00");

        String naplata = "Novcanice\n";
        String separator = "";

        double osnovicaIznos = 0.00;
        double pdvIznos = 0.00;
        double pnpIznos = 0.00;

        int duzinaLinije;
        
        try {        
            duzinaLinije = Integer.parseInt(txtDuzinaLinije.getText());
        } catch (Exception e) {
            duzinaLinije = this.duzinaLinije;
        }
        
                
        /* 
         * Faktor konverzije, mm --> point
         */
        final double k = 72 / 25.4;

        double ispisnaPovrsinaX = Double.parseDouble(txtIspisnaPovrsinaX.getText()) * k;
        double ispisnaPovrsinaY = Double.parseDouble(txtIspisnaPovrsinaY.getText()) * k;
        String imeFonta = String.valueOf(cboFont.getSelectedItem());
        int visinaFonta = Integer.parseInt(txtVelicina.getText());
        int brojZnakova = Integer.parseInt(txtDuzinaLinije.getText());

        /*
         * Postavi duzinu linije na max.br.znakova u slucaju da je suvise dugacka.
         */
        if (!duzinaLinijeIspravna(ispisnaPovrsinaX, ispisnaPovrsinaY, imeFonta, visinaFonta, brojZnakova)) {
            duzinaLinije = maxZnakova - 1;
            this.duzinaLinije = duzinaLinije;
            txtDuzinaLinije.setText(String.valueOf(duzinaLinije));
        }
        
        for (int i = 0; i < duzinaLinije; i++) {
            separator = separator + "-";
        }

        /**
         * Kolicinski prikaz artikala na racunu.
         */
        if (this.kolicina) {
            Hashtable<Proizvod, Integer> kolicina = new Hashtable<Proizvod, Integer>();
            for (Proizvod p : r1.getProizvodi()) {
                kolicina.put(p, r1.getProizvodKolicina(p));
            }
            // Prodji po hash tablici kolicine proizvoda i
            //  napuni text varijablu
            for (Proizvod p : kolicina.keySet()) {
                int kolicinaProizvoda = kolicina.get(p);
                text = text + formatLine(p.getNaziv().trim() + " x" + kolicinaProizvoda,
                        dFormat.format(p.getCijena() * kolicinaProizvoda) + " KN", duzinaLinije) + "\n";

                // pokupi iznose pdv-a, pnp-a i osnovice za prikaz na racunu
                osnovicaIznos = osnovicaIznos + p.getOsnovica() * kolicinaProizvoda;
                pdvIznos = pdvIznos + p.getIznosPdv() * kolicinaProizvoda;
                pnpIznos = pnpIznos + p.getIznosPnp() * kolicinaProizvoda;

            }
        } else {
            for (Proizvod p : r1.getProizvodi()) {
                text = text
                        + formatLine(p.getNaziv().trim(),
                        dFormat.format(p.getCijena()) + " KN", duzinaLinije) + "\n";

                // pokupi iznose pdv-a, pnp-a i osnovice za prikaz na racunu
                osnovicaIznos = osnovicaIznos + p.getOsnovica();
                pdvIznos = pdvIznos + p.getIznosPdv();
                pnpIznos = pnpIznos + p.getIznosPnp();
            }
        }
     
        // ako nema pnp-a, nema potrebe ispisivati
        String pnpFormatLine = "";
        if (pnpIznos > 0) {
            pnpFormatLine = formatLine("PNP: ", dFormat.format(pnpIznos) + " KN", duzinaLinije);
        }

        String racun = frmBlagajna.getHeader()                
                + "\nDatum:" + datum
                + "\n" + formatLine("Br.racuna: ",
                brRac + "/" + poslovnica + "/" + blagajna, duzinaLinije)
                + "\n" + formatLine("Blagajnik:", "Test", duzinaLinije)
                + "\n" + formatLine("Placanje:", naplata, duzinaLinije)
                + formatLine(oznakaProizvoda, "IZNOS", duzinaLinije) + "\n"
                + separator
                + "\n" + text
                + separator + "\n"
                + formatLine("Ukupno: ", r1.getUkupanIznosIspis() + " KN", duzinaLinije)
                + "\n\n"
                + formatLine("Osnovica: ", dFormat.format(osnovicaIznos) + " KN", duzinaLinije)
                + "\n"
                + formatLine("PDV: ", dFormat.format(pdvIznos) + " KN", duzinaLinije)
                + "\n"
                + pnpFormatLine
                + "\n"
                + "Z.kod:\n"
                + "JIR:\n"
                + frmBlagajna.getFooter();

        final JTextArea txtTest = new JTextArea(racun);
        txtTest.setFont(
                new Font(
                String.valueOf(cboFont.getSelectedItem()), Font.PLAIN, Integer.parseInt(txtVelicina.getText())));
        JButton cmdIspisi = new JButton("Ispis na pisac");
        cmdIspisi.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                /* 
                 * Faktor konverzije, mm --> point
                 */
                final double k = 72 / 25.4;

                String nazivZadatka = "Testni ispis " + String.valueOf(cboFont.getSelectedItem())
                        + " " + txtVelicina.getText() + " - znakova " + txtDuzinaLinije.getText();

                Ispis ispis = new Ispis(nazivZadatka, String.valueOf(cboPisac.getSelectedItem()),
                        Double.parseDouble(txtPapirX.getText()) * k,
                        Double.parseDouble(txtPapirY.getText()) * k,
                        Double.parseDouble(txtMarginaX.getText()) * k,
                        Double.parseDouble(txtMarginaY.getText()) * k,
                        Double.parseDouble(txtIspisnaPovrsinaX.getText()) * k,
                        Double.parseDouble(txtIspisnaPovrsinaY.getText()) * k);

                try {
                    /**
                     * Ispis.
                     */
                    ispis.print(txtTest.getPrintable(null, null));
                } catch (PrinterException ex) {
                    logger.log(Level.SEVERE, "cmdIspis()", ex);
                }
            }
        });

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();

        p1.add(txtTest);
        p2.add(cmdIspisi);

        Object[] obj = {p1, p2};

        JOptionPane.showMessageDialog(this, obj, "Test ispisa", JOptionPane.PLAIN_MESSAGE);

    }//GEN-LAST:event_cmdTestIspisaActionPerformed

    private void txtProizvodOznakaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProizvodOznakaFocusGained
        selText(txtProizvodOznaka);
        txtStatus.setText("Oznaka na racunu, da li je proizvod ili usluga. Npr. PROIZVOD");
    }//GEN-LAST:event_txtProizvodOznakaFocusGained

    private void txtPrijeSlanjaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPrijeSlanjaFocusGained
        selText(txtPrijeSlanja);
        txtStatus.setText("Naredba za pokrenuti vanjski program prije slanja racuna. Npr. notepad.exe\n"
                + "Varijable: {brRac} {racun.txt}");

    }//GEN-LAST:event_txtPrijeSlanjaFocusGained

    private void txtPoslijeSlanjaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPoslijeSlanjaFocusGained
        selText(txtPoslijeSlanja);
        txtStatus.setText("Naredba za pokrenuti vanjski program poslije slanja racuna. Npr. calc.exe\n"
                + "Varijable: {brRac} {racun.txt}");
    }//GEN-LAST:event_txtPoslijeSlanjaFocusGained

    private void cmdTestIspisaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdTestIspisaFocusGained
        cmdTestIspisa.setBackground(boja2);
    }//GEN-LAST:event_cmdTestIspisaFocusGained

    private void cmdTestIspisaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdTestIspisaFocusLost
        cmdTestIspisa.setBackground(boja1);
    }//GEN-LAST:event_cmdTestIspisaFocusLost

    private void cboIzgledFontFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboIzgledFontFocusGained
        txtStatus.setText("Odaberite font za prikaz na ekranu.");
    }//GEN-LAST:event_cboIzgledFontFocusGained

    private void sldIzgledVelicinaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sldIzgledVelicinaFocusGained
        txtStatus.setText("Odaberite velicinu fonta za prikaz na ekranu.\n"
                + "Trenutna velicina: " + sldIzgledVelicina.getValue());
    }//GEN-LAST:event_sldIzgledVelicinaFocusGained

    private void sldIzgledDebljinaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sldIzgledDebljinaFocusGained
        txtStatus.setText("Odaberite debljinu dugmica za prikaz na ekranu.\n"
                + "Trenutna debljina: " + sldIzgledDebljina.getValue());
    }//GEN-LAST:event_sldIzgledDebljinaFocusGained

    private void sldIzgledVelicinaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldIzgledVelicinaStateChanged
        sldIzgledVelicina.setToolTipText(String.valueOf(sldIzgledVelicina.getValue()));
    }//GEN-LAST:event_sldIzgledVelicinaStateChanged

    private void sldIzgledDebljinaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldIzgledDebljinaStateChanged
        sldIzgledDebljina.setToolTipText(String.valueOf(sldIzgledDebljina.getValue()));
    }//GEN-LAST:event_sldIzgledDebljinaStateChanged

    /**
     * Format niza znakova za ispisa na fiksni broj znakova.<BR> Fiksni broj
     * znakova je odredjen sa duzinaLinije postavkom.<BR>
     *
     * @param str1 prvi niz
     * @param str2 drugi niz
     * @param maxDuzina duljina linije za format
     * @return spojeni niz sa razmakom izmedju <I>str1</I> i <I>str2</I>.
     */
    public String formatLine(String str1, String str2, int maxDuzina) {

        String result;

        if (maxDuzina > (str1.length() + str2.length())) {
            // uzmi prvi arg.
            result = str1;

            // napuni razmacima
            for (int i = 0; i < maxDuzina - (str1.length() + str2.length()); i++) {
                result = result + " ";
            }
            // dodaj drugi dio
            result = result + str2;
        } else {
            /**
             * Algoritam za prelamanje na fiksnu duljinu znakova.
             */
            if (str1.length() > maxDuzina
                    && str2.length() < maxDuzina) {

                String tmp = "";
                // razlom u tmp varijablu
                for (int i = 0; i < str1.length(); i = i + maxDuzina) {
                    if (i + maxDuzina < str1.length()) {
                        tmp = tmp + str1.substring(i, i + maxDuzina) + "\n";
                    } else {
                        tmp = tmp + str1.substring(i) + "\n";
                    }
                }
                result = tmp + formatLine(" ", str2, maxDuzina);
            } else if (str2.length() > maxDuzina
                    && str1.length() < maxDuzina) {

                String tmp = "";
                // razlom u tmp varijablu
                for (int i = 0; i < str2.length(); i = i + maxDuzina) {
                    if (i + maxDuzina < str2.length()) {
                        tmp = tmp + str2.substring(i, i + maxDuzina) + "\n";
                    } else {
                        tmp = tmp + str2.substring(i) + "\n";
                    }
                }
                result = str1 + "\n" + tmp;
            } else if (str1.length() < maxDuzina
                    && str2.length() < maxDuzina) {
                result = str1 + "\n" + formatLine(" ", str2, maxDuzina);
            } else {

                String tmp = "";
                // razlom u tmp varijablu
                for (int i = 0; i < str1.length(); i = i + maxDuzina) {
                    if (i + maxDuzina < str1.length()) {
                        tmp = tmp + str1.substring(i, i + maxDuzina) + "\n";
                    } else {
                        tmp = tmp + str1.substring(i) + "\n";
                    }
                }

                // privremeno spremanje prvog dijela
                result = tmp;

                tmp = "";
                // razlom u tmp varijablu
                for (int i = 0; i < str2.length(); i = i + maxDuzina) {
                    if (i + maxDuzina < str2.length()) {
                        tmp = tmp + str2.substring(i, i + maxDuzina) + "\n";
                    } else {
                        tmp = tmp + str2.substring(i) + "\n";
                    }
                }
                // vrati prvi + drugi dio
                result = result + tmp;

            }
        }

        // vrati rezultat
        return result;
    }

    /**
     * Vrati tzv. <I>MediaSizeName</I> za odabrani papir prema nazivu formata.
     *
     * @param formatPapira <B>Npr.</B> A4, A5, ...
     * @return <B>Npr.</B> MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, ...
     */
    public MediaSizeName getPapirMediaSize(String formatPapira) {
        for (MediaSizeName papirFormat : paperNames.keySet()) {
            if (paperNames.get(papirFormat).equalsIgnoreCase(formatPapira)) {
                return papirFormat;
            }
        }
        return MediaSizeName.ISO_A4;
    }

    /**
     * Napuni izbornik za formate papira sa popisom podrzanih papira.
     */
    private void popuniFormatePapira() {

        // Pronadji trenutni pisac
        PrintService trenutniPisac = null;
        for (PrintService p : PrintServiceLookup.lookupPrintServices(null, null)) {
            if (p.getName().equalsIgnoreCase(String.valueOf(cboPisac.getSelectedItem()))) {
                trenutniPisac = p;
                break;
            }
        }

        if (trenutniPisac != null) {
            cboFormatPapira.removeAllItems();
            cboFormatPapira.addItem("(custom)");
            for (MediaSizeName papirFormat : paperNames.keySet()) {
                // Provjera da li papir/format postoji za odabrani printer
                PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
                if (trenutniPisac.isAttributeValueSupported(papirFormat, new DocFlavor.BYTE_ARRAY(DocFlavor.BYTE_ARRAY.PNG.getMimeType()), attributes)) {
                    // Dodaj na listu formata ako je podrzan
                    cboFormatPapira.addItem(paperNames.get(papirFormat));
                }
            }
            if (cboFormatPapira.getItemCount() > 0) {
                cboFormatPapira.setSelectedIndex(0);
                for (int i = 0; i < cboFormatPapira.getItemCount(); i++) {
                    if (String.valueOf(cboFormatPapira.getItemAt(i)).equalsIgnoreCase("A4")) {
                        cboFormatPapira.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                cboFormatPapira.setSelectedIndex(0);
            }
        }

    }

    /**
     * Ucitava postavke iz datoteke
     *
     * @param fileName naziv datoteke
     */
    private void postavkeLoad(String fileName) {

        IniSettings iniSettings = new IniSettings();
            
        String sekcija;
        
        try {
            iniSettings.read(fileName);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "postavkeLoad(), IOException on " + fileName, ex);
        }

        /* 
         * Provjera tipa postavki:
         * 1. stare postavke imaju prvu liniju: #Postavke
         * 2. nove nemaju, vec su parametri razvrstani po sekcijama
         * 2.1 nove imaju dodatne parametre (npr. za izgled) koji radi kompatibilnosti
         *     trebaju imati neke def.vr. i pri ucitavanju starih postavki.
         */
        if (iniSettings.dump().startsWith("#Postavke")) {

            /*
             * Osnovne postavke
             */
            iniSettings.setDefault("-");
            naziv = iniSettings.get("naziv");
            vlasnik = iniSettings.get("vlasnik");
            oib = iniSettings.get("oib");
            poslovnica = iniSettings.get("poslovnica");
            blagajna = iniSettings.get("blagajna");
            iniSettings.setDefault("test.pfx");
            keyFile = iniSettings.get("kljuc");
            iniSettings.setDefault("POS3xtr4!");
            keyPassword = iniSettings.get("zaporka");
            iniSettings.setDefault("https://cistest.apis-it.hr:8449/FiskalizacijaServiceTest");
            sURL = iniSettings.get("url");
            uSustPdv = (iniSettings.get("USustPdv").equalsIgnoreCase("true"));
            /* ************************** */

            /*
             * Ispisne postavke
             */
            iniSettings.setDefault("Monospaced");
            fontNaziv = iniSettings.get("fontNaziv");
            iniSettings.setDefault("10");
            fontVelicina = Integer.parseInt(iniSettings.get("fontVelicina"));
            timeout = Integer.parseInt(iniSettings.get("timeout"));
            iniSettings.setDefault("45");
            duzinaLinije = Integer.parseInt(iniSettings.get("duzinaLinije"));
            iniSettings.setDefault(PrintServiceLookup.lookupDefaultPrintService().getName());
            pisac = iniSettings.get("pisac");
            // za dohvat velicine papira, margina i ispisne povrsine
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            PrintService[] printers = PrinterJob.lookupPrintServices();
            for (PrintService printService : printers) {
                if (printService.getName().equalsIgnoreCase(pisac)) {
                    try {
                        printerJob.setPrintService(printService);
                    } catch (PrinterException ex) {
                        logger.log(Level.SEVERE, "loadPostavke(), printerJob.setPrintService()", ex);
                    }
                    break;
                }
            }
            PageFormat pageFormat = printerJob.defaultPage();
            Paper paper = pageFormat.getPaper();
            // Faktor konverzije, mm --> point
            final double k = 72 / 25.4;
            iniSettings.setDefault(dFormat.format(paper.getWidth() / k));
            papirX = Double.parseDouble(iniSettings.get("papirX")) * k;
            iniSettings.setDefault(dFormat.format(paper.getHeight() / k));
            papirY = Double.parseDouble(iniSettings.get("papirY")) * k;
            iniSettings.setDefault(dFormat.format(paper.getImageableX() / k));
            marginaX = Double.parseDouble(iniSettings.get("marginaX")) * k;
            iniSettings.setDefault(dFormat.format(paper.getImageableY() / k));
            marginaY = Double.parseDouble(iniSettings.get("marginaY")) * k;
            iniSettings.setDefault(dFormat.format(paper.getImageableHeight() / k));
            ispisnaPovrsinaX = Double.parseDouble(iniSettings.get("ispisnaPovrsinaX")) * k;
            iniSettings.setDefault(dFormat.format(paper.getImageableWidth() / k));
            ispisnaPovrsinaY = Double.parseDouble(iniSettings.get("ispisnaPovrsinaY")) * k;
            iniSettings.setDefault("A4");
            formatPapira = iniSettings.get("formatPapira");
            iniSettings.setDefault("PROIZVOD/USLUGA");
            oznakaProizvoda = iniSettings.get("oznakaProizvoda");
            iniSettings.setDefault("");
            prijeSlanja = iniSettings.get("prijeSlanja");
            poslijeSlanja = iniSettings.get("poslijeSlanja");
            /* ************************** */

            /*
             * Sistemske postavke (skrivene)
             */
            iniSettings.setDefault("1");
            brRac = Integer.parseInt(iniSettings.get("brRac"));
            iniSettings.setDefault("brzi");
            izborBlagajnika = iniSettings.get("izborBlagajnika");
            kolicina = (iniSettings.get("kolicina").equalsIgnoreCase("true"));
            arhiviraj = (iniSettings.get("arhiviraj").equalsIgnoreCase("true"));
            transakcijski = (iniSettings.get("transakcijski").equalsIgnoreCase("true"));
            /* ************************** */

            /*
             * Izgled postavke
             * Najlakse je iskoristiti izgled.txt datoteku koja bi morala postojati.
             */
            IniSettings izgledSettings = new IniSettings();
            
            try {
                izgledSettings.read("izgled.txt");
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "postavkeLoad(), IOException on izgled.txt", ex);
            }

            izgledSettings.setDefault("Monospaced");
            izgledFont = izgledSettings.get("fontNaziv");
            izgledSettings.setDefault("10");
            izgledVelicina = Integer.parseInt(izgledSettings.get("fontVelicina"));            
            izgledSettings.setDefault("35");
            izgledDebljina = Integer.parseInt(izgledSettings.get("debljinaDugmica"));            
            
        } else {
            
            /*
             * Osnovne postavke
             */
            sekcija = "Osnovne";
            iniSettings.setDefault("-");
            naziv = iniSettings.get(sekcija, "naziv");
            vlasnik = iniSettings.get(sekcija, "vlasnik");
            oib = iniSettings.get(sekcija, "oib");
            poslovnica = iniSettings.get(sekcija, "poslovnica");
            blagajna = iniSettings.get(sekcija, "blagajna");
            iniSettings.setDefault("test.pfx");
            keyFile = iniSettings.get(sekcija, "kljuc");
            iniSettings.setDefault("POS3xtr4!");
            keyPassword = iniSettings.get(sekcija, "zaporka");
            iniSettings.setDefault("https://cistest.apis-it.hr:8449/FiskalizacijaServiceTest");
            sURL = iniSettings.get(sekcija, "url"); 
            uSustPdv = (iniSettings.get(sekcija, "USustPdv").equalsIgnoreCase("true"));
            /* ************************** */

            /*
             * Ispisne postavke
             */
            sekcija = "Ispis";
            iniSettings.setDefault("Monospaced");
            fontNaziv = iniSettings.get(sekcija, "fontNaziv");
            iniSettings.setDefault("10");
            fontVelicina = Integer.parseInt(iniSettings.get(sekcija, "fontVelicina"));
            timeout = Integer.parseInt(iniSettings.get(sekcija, "timeout"));
            iniSettings.setDefault("45");
            duzinaLinije = Integer.parseInt(iniSettings.get(sekcija, "duzinaLinije"));
            iniSettings.setDefault(PrintServiceLookup.lookupDefaultPrintService().getName());
            pisac = iniSettings.get(sekcija, "pisac");
            // za dohvat velicine papira, margina i ispisne povrsine
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            PrintService[] printers = PrinterJob.lookupPrintServices();
            for (PrintService printService : printers) {
                if (printService.getName().equalsIgnoreCase(pisac)) {
                    try {
                        printerJob.setPrintService(printService);
                    } catch (PrinterException ex) {
                        logger.log(Level.SEVERE, "loadPostavke(), printerJob.setPrintService()", ex);
                    }
                    break;
                }
            }
            PageFormat pageFormat = printerJob.defaultPage();
            Paper paper = pageFormat.getPaper();
            // Faktor konverzije, mm --> point
            final double k = 72 / 25.4;
            iniSettings.setDefault(dFormat.format(paper.getWidth() / k));
            papirX = Double.parseDouble(iniSettings.get(sekcija, "papirX")) * k;
            iniSettings.setDefault(dFormat.format(paper.getHeight() / k));
            papirY = Double.parseDouble(iniSettings.get(sekcija, "papirY")) * k;
            iniSettings.setDefault(dFormat.format(paper.getImageableX() / k));
            marginaX = Double.parseDouble(iniSettings.get(sekcija, "marginaX")) * k;
            iniSettings.setDefault(dFormat.format(paper.getImageableY() / k));
            marginaY = Double.parseDouble(iniSettings.get(sekcija, "marginaY")) * k;
            iniSettings.setDefault(dFormat.format(paper.getImageableHeight() / k));
            ispisnaPovrsinaX = Double.parseDouble(iniSettings.get(sekcija, "ispisnaPovrsinaX")) * k;
            iniSettings.setDefault(dFormat.format(paper.getImageableWidth() / k));
            ispisnaPovrsinaY = Double.parseDouble(iniSettings.get(sekcija, "ispisnaPovrsinaY")) * k;
            iniSettings.setDefault("A4");
            formatPapira = iniSettings.get(sekcija, "formatPapira");
            iniSettings.setDefault("PROIZVOD/USLUGA");
            oznakaProizvoda = iniSettings.get(sekcija, "oznakaProizvoda");
            iniSettings.setDefault("");
            prijeSlanja = iniSettings.get(sekcija, "prijeSlanja");
            poslijeSlanja = iniSettings.get(sekcija, "poslijeSlanja");
            /* ************************** */

            /*
             * Sistemske postavke (skrivene)
             */
            sekcija = "Sistemske";
            iniSettings.setDefault("1");
            brRac = Integer.parseInt(iniSettings.get(sekcija, "brRac"));
            iniSettings.setDefault("brzi");
            izborBlagajnika = iniSettings.get(sekcija, "izborBlagajnika");
            iniSettings.setDefault("true");
            kolicina = (iniSettings.get(sekcija, "kolicina").equalsIgnoreCase("true"));
            arhiviraj = (iniSettings.get(sekcija, "arhiviraj").equalsIgnoreCase("true"));
            transakcijski = (iniSettings.get(sekcija, "transakcijski").equalsIgnoreCase("true"));
            /* ************************** */
            
            /*
             * Izgled postavke
             */
            sekcija = "Izgled";
            iniSettings.setDefault("Monospaced");
            izgledFont = iniSettings.get(sekcija, "font");
            iniSettings.setDefault("12");
            izgledVelicina = Integer.parseInt(iniSettings.get(sekcija, "velicina"));
            iniSettings.setDefault("35");
            izgledDebljina = Integer.parseInt(iniSettings.get(sekcija, "debljina"));
            /* ************************** */
            
        }
        
        /*
         * Omjer podjele ekrana i sirine stupaca.
         */
        sekcija = "Stupci";
        iniSettings.setDefault("0");
        omjer = Integer.parseInt(iniSettings.get(sekcija, "omjer"));
        
        if (stupac == null) {
            stupac = new ArrayList<Integer>();
        }
        else {
            stupac.clear();
        }
        
        for (Integer index : new Integer[]{0, 1, 2, 3}) {
            iniSettings.setDefault("0");
            String sirinaSupca = iniSettings.get(sekcija, "stupac" + index);
            stupac.add(Integer.parseInt(sirinaSupca));
        }
           
        /* ************************** */       
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboFont;
    private javax.swing.JComboBox cboFormatPapira;
    private javax.swing.JComboBox cboIzgledFont;
    private javax.swing.JComboBox cboPisac;
    private javax.swing.JCheckBox chkUSustPdv;
    private javax.swing.JButton cmdIzlaz;
    private javax.swing.JButton cmdOK;
    private javax.swing.JButton cmdTestIspisa;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblBlagajna;
    private javax.swing.JLabel lblDuzinaLinije;
    private javax.swing.JLabel lblFont;
    private javax.swing.JLabel lblFormatPapira;
    private javax.swing.JLabel lblIspisnaPovrsina;
    private javax.swing.JLabel lblIzgledDebljina;
    private javax.swing.JLabel lblIzgledFont;
    private javax.swing.JLabel lblIzgledVelicina;
    private javax.swing.JLabel lblMargina;
    private javax.swing.JLabel lblNaziv;
    private javax.swing.JLabel lblOib;
    private javax.swing.JLabel lblPapir;
    private javax.swing.JLabel lblPisac;
    private javax.swing.JLabel lblPoslijeSlanja;
    private javax.swing.JLabel lblPoslovnica;
    private javax.swing.JLabel lblPrijeSlanja;
    private javax.swing.JLabel lblPrivatniKljuc;
    private javax.swing.JLabel lblProizvodOznaka;
    private javax.swing.JLabel lblTimeout;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JLabel lblVelicina;
    private javax.swing.JLabel lblVlasnik;
    private javax.swing.JLabel lblX1;
    private javax.swing.JLabel lblX2;
    private javax.swing.JLabel lblX3;
    private javax.swing.JLabel lblZaporka;
    private javax.swing.JPanel pnlIspis;
    private javax.swing.JPanel pnlIzbornik;
    private javax.swing.JPanel pnlIzgled;
    private javax.swing.JPanel pnlOsnovne;
    private javax.swing.JScrollPane pnlStatus;
    private javax.swing.JSlider sldIzgledDebljina;
    private javax.swing.JSlider sldIzgledVelicina;
    private javax.swing.JTextField txtBlagajna;
    private javax.swing.JTextField txtDuzinaLinije;
    private javax.swing.JTextField txtIspisnaPovrsinaX;
    private javax.swing.JTextField txtIspisnaPovrsinaY;
    private javax.swing.JTextField txtKljuc;
    private javax.swing.JTextField txtMarginaX;
    private javax.swing.JTextField txtMarginaY;
    private javax.swing.JTextField txtNaziv;
    private javax.swing.JTextField txtOib;
    private javax.swing.JTextField txtPapirX;
    private javax.swing.JTextField txtPapirY;
    private javax.swing.JTextField txtPoslijeSlanja;
    private javax.swing.JTextField txtPoslovnica;
    private javax.swing.JTextField txtPrijeSlanja;
    private javax.swing.JTextField txtProizvodOznaka;
    private javax.swing.JTextPane txtStatus;
    private javax.swing.JTextField txtTimeout;
    private javax.swing.JTextField txtUrl;
    private javax.swing.JTextField txtVelicina;
    private javax.swing.JTextField txtVlasnik;
    private javax.swing.JPasswordField txtZaporka;
    // End of variables declaration//GEN-END:variables

    /**
     * Vraca u text polja vrijednosti iz lokalnih varijabli.
     */
    private void onExit() {
        /* 
         * Vrati sve postavke na pocetak.
         * Korisnik odabrao Izlaz dugme (Cancel efekt)
         */

        /*
         * Osnove postavke
         */ 
        txtNaziv.setText(naziv);
        txtVlasnik.setText(vlasnik);
        txtOib.setText(oib);
        txtPoslovnica.setText(poslovnica);
        txtBlagajna.setText(blagajna);
        txtKljuc.setText(keyFile);
        txtZaporka.setText(keyPassword);
        txtUrl.setText(sURL);
        chkUSustPdv.setSelected(uSustPdv);

        /*
         * Ispis postavke
         */ 
        txtTimeout.setText(String.valueOf(timeout));
        txtDuzinaLinije.setText(String.valueOf(duzinaLinije));
        for (int i = 0; i < cboFont.getItemCount(); i++) {
            if (fontNaziv.equalsIgnoreCase(String.valueOf(cboFont.getItemAt(i)))) {
                cboFont.setSelectedIndex(i);
            }
        }
        txtVelicina.setText(String.valueOf(fontVelicina));
        for (int i = 0; i < cboPisac.getItemCount(); i++) {
            if (pisac.equalsIgnoreCase(String.valueOf(cboPisac.getItemAt(i)))) {
                cboPisac.setSelectedIndex(i);
            }
        }
        final double k = 25.4 / 72; // point --> mm
        txtPapirX.setText(dFormat.format(papirX * k));
        txtPapirY.setText(dFormat.format(papirY * k));
        txtMarginaX.setText(dFormat.format(marginaX * k));
        txtMarginaY.setText(dFormat.format(marginaY * k));
        txtIspisnaPovrsinaX.setText(dFormat.format(ispisnaPovrsinaX * k));
        txtIspisnaPovrsinaY.setText(dFormat.format(ispisnaPovrsinaY * k));
        for (int i = 0; i < cboFormatPapira.getItemCount(); i++) {
            if (formatPapira.equalsIgnoreCase(String.valueOf(cboFormatPapira.getItemAt(i)))) {
                cboFormatPapira.setSelectedIndex(i);
            }
        }
        txtProizvodOznaka.setText(oznakaProizvoda);
        txtPrijeSlanja.setText(prijeSlanja);
        txtPoslijeSlanja.setText(poslijeSlanja);

        /*
         * Izgled postavke
         */         
        for (int i = 0; i < cboIzgledFont.getItemCount(); i++) {
            if (izgledFont.equalsIgnoreCase(String.valueOf(cboIzgledFont.getItemAt(i)))) {
                cboIzgledFont.setSelectedIndex(i);
            }
        }
        sldIzgledVelicina.setValue(izgledVelicina);
        sldIzgledDebljina.setValue(izgledDebljina);

        
        // postavi zastavicu prikaza na true
        prikaziFileOpen = true;

        frmBlagajna.toggleScreen(true);
        this.setVisible(false);
    }
    
    /*
     * Getter-i i setter-i sa sistemske postavke (skrivene)
     */
    
    /**
     * Slobodni broj racuna.
     * @return broj racuna
     */
    public int getBrRac() {
        return brRac;
    }

    /**
     * Postavi novi slob. broj racuna.
     * @param brRac broj racuna za izdavanje sljedeceg racuna
     */
    public void setBrRac(int brRac) {
        this.brRac = brRac;
    }

    /**
     * Izbor blagajnika<BR>
     * Brzi ili normalan, ovisno o sistemskoj postavci.<BR>
     * Kod normalnog izbora blagajnik mora unijeti svoj oib,
     * dok se kod brzog popis blagajnika rotira.
     * @return <I>brzi</I> ili <I>normalan</I>
     */
    public String getIzborBlagajnika() {
        return izborBlagajnika;
    }

    /**
     * Da li se zeli kolicinski prikaz na racunu ?
     * @return <I>true</I> ili <I>false</I>
     */
    public boolean isKolicina() {
        return kolicina;
    }
    
    /**
     * Da li program treba raditi arhivu i slati na posluzitelj kopiju.
     * @return <I>true</I> ili <I>false</I>
     */
    public boolean isArhiviraj() {
        return arhiviraj;
    }    
    
    /**
     * Da li se zeli omoguciti unos transakcijskog broja.<BR>
     * Ovo radi samo ako se tip naplate T (transakcija).
     * @return <I>true</I> ili <I>false</I>
     */
    public boolean isTransakcijski() {
        return transakcijski;
    }

    // ************************************ //
    
    
    /*
     * Getter-i i setter-i za ispisne postavke
     */

    /**
     * Timeout pri slanju racuna.<BR>
     * Max. vrijeme cekanja na odgovor od PU.
     * @return timeout u sec.
     */
    public int getTimeout() {
        return timeout;
    }
    /**
     * Broj znakova u liniji prije preloma.<BR>
     * Max. vrijednost se racuna na temelju odabranog fonta i velicine.
     * @return duljina linije mjerena brojem znakova
     */
    public int getDuzinaLinije() {
        return duzinaLinije;
    }
    
    /**
     * Font za ispis na pisac.<BR>
     * Prikaz na ekranu moze biti drugaciji.
     * @return ime fonta
     */
    public String getFontNaziv() {
        return fontNaziv;
    }
    
    /**
     * Velicina fonta za ispis na pisacu.
     * @return velicina fonta
     */
    public int getFontVelicina() {
        return fontVelicina;
    }

    /**
     * Naziv odabranog pisaca za ispis racuna.<BR>
     * Naziv je jednak onome u Control Panel -> Printers dijelu.
     * @return naziv pisaca instaliranog u sistemu
     */
    public String getPisac() {
        return pisac;
    }

    /**
     * Velicina papira, sirina.
     * @return interna mjera
     */
    public double getPapirX() {
        return papirX;
    }

    /**
     * Velicina papira, duljina.
     * @return interna mjera
     */
    public double getPapirY() {
        return papirY;
    }

    /**
     * Margina, lijeva.
     * @return interna mjera
     */
    public double getMarginaX() {
        return marginaX;
    }

    /**
     * Margina, gornja.
     * @return interna mjera
     */
    public double getMarginaY() {
        return marginaY;
    }

    /**
     * Ispisna povrsina, sirina.
     * @return interna mjera
     */
    public double getIspisnaPovrsinaX() {
        return ispisnaPovrsinaX;
    }

    /**
     * Ispisna povrsina, visina.
     * @return interna mjera
     */
    public double getIspisnaPovrsinaY() {
        return ispisnaPovrsinaY;
    }

    /**
     * Format papira, npr. A4
     * @return format papira
     */
    public String getFormatPapira() {
        return formatPapira;
    }

    /**
     * Oznaka za rijec PROIZVOD ili USLUGA u zaglavlju racuna.<BR>
     * Moze biti i neka druga rijec.
     * @return oznaka
     */
    public String getOznakaProizvoda() {
        return oznakaProizvoda;
    }

    /**
     * Komanda koja ce se izvrsiti prije slanja racuna.<BR>
     * Ova opcija omogucava pokretanje vanjskih programa.
     * @return komanda
     */
    public String getPrijeSlanja() {
        return prijeSlanja;
    }
    
    /**
     * Komanda koja ce se izvrsiti poslije slanja racuna.<BR>
     * Ova opcija omogucava pokretanje vanjskih programa.
     * @return komanda
     */
    public String getPoslijeSlanja() {
        return poslijeSlanja;
    }
    
    // ************************************ //
    
    /*
     * Getter-i i setter-i za osnovne postavke
     */
    
    /**
     * Naziv firme ili obrta.
     * @return naziv
     */
    public String getNaziv() {
        return naziv;
    }

    /**
     * Vlasnik firme ili obrta.
     * @return vlasnik
     */
    public String getVlasnik() {
        return vlasnik;
    }

    /**
     * OIB firme ili obrta.
     * @return oib
     */
    public String getOib() {
        return oib;
    }

    /**
     * Oznaka poslovnice.
     * @return poslovnica
     */
    public String getPoslovnica() {
        return poslovnica;
    }

    /**
     * Broj blagajne. Pocinje od 1.<BR>
     * Koristi se ako u istoj poslovnici ima vise blagajni.
     * @return br. blagajne
     */
    public String getBlagajna() {
        return blagajna;
    }

    /**
     * Privatni kljuc, datoteka.<BR>
     * Format je <I>pfx</I> ili <I>p12</I>.
     * @return datoteka privatnog kljuca
     */
    public String getKeyFile() {
        return keyFile;
    }

    /**
     * Zaporka za privani kljuc.
     * @return zaporka (nesifrirana)
     */
    public String getKeyPassword() {
        return keyPassword;
    }

    /**
     * URL na koji se salju racuni (xml poruke prema PU).
     * @return url
     */
    public String getsURL() {
        return sURL;
    }
    
    /**
     * U sustavu PDVa postavka.
     * @return <I>true</I> ili <I>false</I>
     */
    public boolean isUSustPdv() {
        return uSustPdv;
    }
    
    // ************************************ //

        
    /*
     * Getter-i i setter-i za izgled
     */
    
    /**
     * Naziv fonta koji se zeli primjeniti na izgled teksta u prozorima.<BR>
     * Moze a i ne mora biti tvz. monospaced font.
     * @return ime fonta
     */
    public String getIzgledFont() {
        return izgledFont;
    }

    /**
     * Velicina fonta.
     * @return velicina fonta.
     */
    public int getIzgledVelicina() {
        return izgledVelicina;
    }

    /**
     * Debljina dugmica (JButton dugmici) koji su postavljeni u prozorima.
     * @return debljina dugmica
     */
    public int getIzgledDebljina() {
        return izgledDebljina;
    }
    
    /**
     * Omjer za JSplitPane, podijela ekrana izmedju racuna i tablice.
     * @return omjer kao decimalna postotna vr.
     */
    public int getOmjer() {
        return omjer;
    }
    
    /**
     * Omjer za JSplitPane, podijela ekrana izmedju racuna i tablice.
     * @param omjer omjer kao decimalna postotna vr.
     */
    public void setOmjer(int omjer) {
        this.omjer = omjer;
    }
    
    /**
     * Popis sirina stupaca. Koristi se kod ui() u FrmBlagajna.
     * @return lista sirina stupaca
     */
    public List<Integer> getStupac() {
        return stupac;
    }
    
    // ************************************ //

    
    /**
     * Korisnicko sucelje postavlja.<BR>
     */
    private void ui() {
        // UI
        
        /*
         * Novi obj. Izgled tipa, za korisnicko sucelje.
         * Koristi ucitane postavke iz FrmPostavke prozora.
         */
        izgled = new Izgled(izgledFont, izgledVelicina,
                izgledDebljina);
        
        // Panel sa tabovima
        jTabbedPane1.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));

        // Osnovni paneli
        Component[] paneli = {pnlOsnovne, pnlIspis, pnlIzbornik, pnlIzgled};
        for (Component panel : paneli) {
            JPanel pnl = (JPanel) panel;
            for (Component comp : pnl.getComponents()) {
                if (comp instanceof JLabel || comp instanceof JTextField || comp instanceof JCheckBox || comp instanceof JComboBox) {
                    comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
                }
                if (comp instanceof JButton) {
                    comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
                    comp.setMinimumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                    comp.setMaximumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                    comp.setPreferredSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                }
            }

        }

        // Status prozor
        txtStatus.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));

    }
}
