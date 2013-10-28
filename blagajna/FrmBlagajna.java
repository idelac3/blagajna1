package blagajna;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;


/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author root
 */
public class FrmBlagajna extends javax.swing.JFrame {

    /**
     * Objekt za logiranje gresaka
     */
    private static Logger logger;
    /**
     * Handler kojim se postavi kako se logira i gdje
     */
    private Handler handler;
    /**
     * PoslovniProstor prozor
     */
    private FrmPoslovniProstor frmPP;
    /**
     * Postavke prozor
     */
    private FrmPostavke frmPostavke;
    /**
     * Promet prozor
     */
    private FrmPromet frmPromet;
    /**
     * O programu prozor
     */
    private FrmOProgramu frmOProgramu;
    /**
     * Cjenik prozor
     */
    private FrmCjenik frmCjenik;
    /**
     * tablica proizvoda/usluga -> model podataka ispod same tablice
     */
    private RacunJTableDataModel tableModel = new RacunJTableDataModel(false);
    /**
     * sorter tablice
     */
    private TableRowSorter<RacunJTableDataModel> sorter;
    /**
     * Racun, sa trenutno odabranim stavkama iz tablice
     */
    private Racun r1;
    /**
     * zaglavlje racuna, za ispis
     */
    private String header;
    /**
     * podnozje racuna, za ispis
     */
    private String footer;
    /**
     * trenutni broj racuna za izdati (ispisati, poslati i pohraniti)
     */
    private int brRac;
    /**
     * jir se izracunava za svaki racun
     */
    private String jir;
    /**
     * zastitni kod se izracunava za svaki racun
     */
    private String zkod;
    /**
     * Popis blagajnika. <BR> <B>Format:</B><I>OIB, Oznaka</I> <BR>
     */
    private Hashtable<String, String> blagajnik;
    /**
     * oznaka blagajnika (za ispis) i oib (za slanje u xml poruci)
     */
    private String oibBlagajnika;
    /**
     * nacin placanja, 0 - gotovina 1 - kartica ... 3 - ostalo
     */
    private int nacinPlacanja;
    /**
     * Transakcijski niz
     */
    private String transakcijskiNiz;
    /**
     * Dodatak ako se treba prikazati i ispisati r1/r2 na racunu.
     */
    private String r1r2Dodatak;
    /**
     * Dodatak ako se treba prikazati i ispisati uredjaj na racunu.
     */
    private String uredjajDodatak;
    /**
     * Napomena, proizvoljni tekst na racunu.
     */
    private String napomena;
    /**
     * dodati komentar za sta se koristi
     */
    private List<String> proizvodi;
    /**
     * dodati komentar za sta se koristi
     */
    private Map<String, Proizvod> proizvodiMap;
    /**
     * dretva za provjeru internet veze
     */
    private Thread internetVezaThread;
    /**
     * Vremenski interval promjene.<BR> U sekundama.<BR>
     */
    private int vremenskiInterval;
    /**
     * dretva za slanje racuna
     */
    private Thread sendThread;
    /**
     * timer za odbrojavanje i cekanje na odziv posluzitelja kod slanja racuna
     */
    private Timer timerSlanjeRacuna;
    /**
     * brojac za odbrojavanje i cekanje na odziv posluzitelja kod slanja racuna
     */
    private int count;
    /**
     * tzv. latch varijabla-brojac, za cekanje da se druge forme ucitaju, pa se
     * nastavi izvodjenje programa,tj. da drugi prozor moze poslati signal da se
     * izvodjenje u FrmBlagajna nastavi
     */
    public CountDownLatch latch;
    /**
     * adapter za prozor, npr. kako ce se ponasati pri zatvaranju
     */
    BlagajnaWindowAdapter windowAdapter = new BlagajnaWindowAdapter();
    /**
     * blokada ispisa, ako padne provjera registracije
     */
    private boolean blokiran;
    /**
     * Popust na cijenu, tj. ukupni iznos na racunu. Od 0.00 do 0.99, npr. 0.17
     * -> 17%
     */
    private double popust;
    /**
     * Zadnje koristena vr. za popust. Postavlja se u cmdPopust()
     */
    private String zadnjiPopust;
    /**
     * Pacijenti, popis iz pacijenti.txt datoteke.<BR> <I>Karton</I>,
     * <I>Ime</I>.<BR>
     */
    private Hashtable<Integer, String> pacijenti;
    /**
     * Koji je pacijent trenutno odabran? Sifra pacijenta.
     */
    private int odabraniPacijent;
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
     * Lokalizacija.<BR> Program ce sacuvati postavku lokalizaije, trenutnu u
     * sustavu, pa ce prebaciti na en_US, tako da ako treba, moze se uvijek
     * pozvati originalna.<BR>
     */
    public Locale lokalizacija;

    /**
     * Glavni prozor, prikaz i pocetak programa
     */
    public FrmBlagajna() {

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(windowAdapter);

        try {

            // logiranje problema na syslog/datoteku
            logger = Logger.getLogger("blagajna");
            // na sta se salju logovi
            //handler = new SocketHandler("localhost", 514);
            handler = new FileHandler("blagajna.log", false);

            // koji format cemo koristiti: text ili xml
            handler.setFormatter(new SimpleFormatter());
            // sta se logira: sve, ili samo greske (Level.SEVERE)
            handler.setLevel(Level.ALL);
            // dodaj logger-u handler 
            logger.addHandler(handler);

            // postavi GUI
            initComponents();

            // postavi pocetne boje
            boja1 = cmdIspis.getBackground();
            boja2 = Color.ORANGE;

            // po defaultu, ne blokiraj ispis racuna
            blokiran = false;

            // tzv. latch varijabla-brojac, za cekanje da se druge forme ucitaju,
            //  pa se nastavi izvodjenje programa
            latch = new CountDownLatch(1);

            // postavi CA cert.
            System.setProperty("javax.net.ssl.trustStore", "cacerts");

            // Logiranje nekih uobicajenih varijabli sustava
            String sLog = "";
            sLog = sLog + ("\n          Java folder: " + System.getProperty("java.home"));
            sLog = sLog + ("\n   Operacijski sustav: " + System.getProperty("os.name"));
            sLog = sLog + ("\nBlagajna je u folderu: " + System.getProperty("user.dir"));
            sLog = sLog + ("\n       CA certifikati: " + System.getProperty("javax.net.ssl.trustStore"));
            sLog = sLog + ("\nTrenutna lokalizacija: " + Locale.getDefault());

            // Spremi lokalizaciju sustava
            lokalizacija = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());

            // postavi lokalizaciju na en_US tako da dec. iznosi 
            //  uvijek budu formatirani sa tockom a ne zarezom                        
            Locale.setDefault(new Locale("en", "US"));
            sLog = sLog + ("\nPostavljam lokalizac.: " + Locale.getDefault());

            // napravi prozor PoslovniProstor ako ne postoji
            if (this.frmPostavke == null) {
                // predaj adresu FrmBlagajna prozora prozoru frmPostavke
                this.frmPostavke = new FrmPostavke(this);
            }
            // ovdje program ceka da se forma postavke ucita
            latch.await();

            // postavi korisnicko sucelje
            ui();

            // postavi traku, koristi se kod cmdIspis
            progresBar.setMaximum(frmPostavke.getTimeout());
            progresBar.setMinimum(0);

            // napravi inicijalizaciju zaglavlja i podnozja racuna za txtRacun
            initHeaderAndFooter();
            tblProizvod.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // postavi tablici proizvoda/usluga obj.model podataka
            tblProizvod.setModel(tableModel);

            // iskoristi fontNaziv za postavljanje kurir fonta za prikaz i ispis
            sLog = sLog + ("\nPostavljam ispis font: " + frmPostavke.getFontNaziv() + ", " + frmPostavke.getFontVelicina());

            sLog = sLog + ("\nDefault pisac u sust.: "
                    + PrintServiceLookup.lookupDefaultPrintService().getName());
            // lista pisaca instaliranih u sustavu
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            if (printServices.length > 0) {
                PrintService odabraniPisac = PrintServiceLookup.lookupDefaultPrintService();
                for (PrintService printer : printServices) {
                    if (printer.getName().equalsIgnoreCase(frmPostavke.getPisac())) {
                        sLog = sLog + ("\nOdabrani sust.  pisac: " + printer.getName());
                        odabraniPisac = printer;
                    }
                }

                /**
                 * Decimal formater.
                 */
                DecimalFormat dFormat = new DecimalFormat("0.00");
                /**
                 * Print lista zadataka.
                 */
                PrinterJob printerJob = PrinterJob.getPrinterJob();
                printerJob.setPrintService(odabraniPisac);
                /**
                 * Format stranice za ispis na papir.
                 */
                PageFormat pageFormat = printerJob.defaultPage();

                /**
                 * Info za margine i papir.
                 */
                Paper paper = pageFormat.getPaper();
                paper.setImageableArea(frmPostavke.getMarginaX(),
                        frmPostavke.getMarginaY(),
                        frmPostavke.getIspisnaPovrsinaX(),
                        frmPostavke.getIspisnaPovrsinaY());
                printerJob.validatePage(pageFormat);


                String printJobInfo = "\n Printer, ime zadatka: " + printerJob.getJobName()
                        + ", ispisna povrsina: " + dFormat.format(pageFormat.getImageableWidth() * 25.4 / 72) + "x" + dFormat.format(pageFormat.getImageableHeight() * 25.4 / 72) + " mm";
                String paperInfo = "\n                Papir: " + dFormat.format(paper.getWidth() * 25.4 / 72) + "x" + dFormat.format(paper.getHeight() * 25.4 / 72) + " mm"
                        + ", ispisna povrsina: " + dFormat.format(paper.getImageableWidth() * 25.4 / 72) + "x" + dFormat.format(paper.getImageableHeight() * 25.4 / 72) + " mm"
                        + ", vrh(x, y): " + dFormat.format(paper.getImageableX() * 25.4 / 72) + "x" + dFormat.format(paper.getImageableY() * 25.4 / 72) + " mm" //+ ", dno(x, y): " + dFormat.format(paper.getWidth() - paper.getImageableWidth() - paper.getImageableX()) + "x"
                        //+ dFormat.format(paper.getHeight() - paper.getImageableY() - paper.getImageableHeight())
                        ;

                //System.out.println(", Bottom (x, y): " + dFormat.format(paper.getWidth() - paper.getImageableWidth() - paper.getImageableX()) + "x"
                //        + dFormat.format(paper.getHeight() - paper.getImageableY() - paper.getImageableHeight()));

                sLog = sLog + printJobInfo;
                sLog = sLog + paperInfo;

                /**
                 * Metrika, koliko su siroki znakovi.
                 */
                Font testFont = new Font(frmPostavke.getFontNaziv(), Font.PLAIN, frmPostavke.getFontVelicina()); 
                Graphics2D graphics2D = (Graphics2D) txtRacun.getGraphics();
                graphics2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                graphics2D.setFont(testFont);
                FontMetrics fontMetrics = graphics2D.getFontMetrics(testFont);
                int fontHeight = fontMetrics.getHeight();
                int fontAscent = fontMetrics.getAscent();

                String template = "";
                for (int i = 0; i < frmPostavke.getDuzinaLinije(); i++) {
                    template = template + "A";
                }

                /**
                 * Da li svi znakovi stanu u ispisnu povrsinu (po duzini, X
                 * koord.)?
                 */
                boolean prilagodba;
                prilagodba = fontMetrics.stringWidth(template) < frmPostavke.getIspisnaPovrsinaX();

                String metricsInfo = "\nMetrik"
                        + "\n        Duzina linije: " + dFormat.format(fontMetrics.stringWidth(template) * 25.4 / 72) + " mm"
                        + "\n        Visina linije: " + dFormat.format(fontHeight * 25.4 / 72) + " mm, ascent linije: " + dFormat.format(fontAscent * 25.4 / 72) + " mm"
                        + "\n         Sirina znaka: " + dFormat.format(fontMetrics.stringWidth(template) * 25.4 / (frmPostavke.getDuzinaLinije() * 72)) + " mm"
                        + "\n    Prilagodba duzine: " + String.valueOf(prilagodba)
                        + " ( duzinaLinije < sirina papira )";

                sLog = sLog + metricsInfo + "\n";

                if (!prilagodba) {
                    // upozori korisnika da korigira parametar duzinaLinije
                    lblStatus.setForeground(Color.RED);
                    if (paper.getImageableWidth() < 0) {
                        lblStatus.setText("Postavite duzinu linije na "
                                + Math.round(paper.getWidth() * frmPostavke.getDuzinaLinije() / fontMetrics.stringWidth(template)) + ".");
                    } else {
                        lblStatus.setText("Postavite duzinu linije na "
                                + Math.round(paper.getImageableWidth() * frmPostavke.getDuzinaLinije() / fontMetrics.stringWidth(template)) + ".");
                    }
                }

            } else {
                JOptionPane.showMessageDialog(this, "Instalirajte barem jedan pisac i postavite kao default.", "Blagajna", JOptionPane.ERROR_MESSAGE);
            }

            File fileCjenik = new File("cjenik.txt");
            if (fileCjenik.exists()) {
                // ucitaj cjenik
                initCjenik("cjenik.txt");
            } else {
                logger.severe("Datoteka cjenik.txt ne postoji!.");
            }

            // Inicijalna vr. broja racuna, ako nista ne postoji
            brRac = 1;

            // sada provjeri da li postoji folder Racun gdje se trebaju spremati racuni
            File dirRacun = new File("Racuni");
            if (dirRacun.exists()) {

                // izvadi iz postavki zadnji spremljeni brRac
                // NAPOMENA: u postavkama brRac mozda nije azuriran i ne moze
                //  se bez provjere uzeti vrijednost
                int tmpBrRac = frmPostavke.getBrRac();

                // sada ide provjera postojanja datoteka u Racuni folderu
                // napravi File obj. za provjeru postoji li datoteka
                File txtFile = new File("Racuni/racun" + tmpBrRac + ".txt");
                while (txtFile.exists()) {
                    // ako datoteka postoji, povecaj brojac 
                    //  i provjeri ponovno
                    tmpBrRac++;
                    txtFile = new File("Racuni/racun" + tmpBrRac + ".txt");
                }

                // azuriraj brRac
                brRac = tmpBrRac;

                sLog = sLog + ("\nSljedeci br.rac.: " + brRac + "\n\n");

            } else {
                // ako folder ne postoji, napravi i postavi brRac na 1
                dirRacun.mkdir();
                brRac = 1;
            }

            // azuriraj u postvkama brRac
            frmPostavke.setBrRac(brRac);


            // napravi Racun objekt za pohranu odabranih stavki iz tablice
            r1 = new Racun();

            initSearch();

            proizvodi = new ArrayList<String>();
            proizvodiMap = new HashMap<String, Proizvod>();
            for (Object o : tableModel.getData()) {
                Proizvod p = (Proizvod) o;
                proizvodi.add(p.getNaziv());
                proizvodiMap.put(p.getNaziv(), p);
            }

            // inicijaliziraj sorter
            sorter = new TableRowSorter<RacunJTableDataModel>(tableModel);
            tblProizvod.setRowSorter(sorter);


            // Velicina stupaca
//            tblProizvod.getColumnModel().getColumn(0).setPreferredWidth(150);
//            tblProizvod.getColumnModel().getColumn(1).setPreferredWidth(50);
//            tblProizvod.getColumnModel().getColumn(2).setPreferredWidth(50);
//            tblProizvod.getColumnModel().getColumn(3).setPreferredWidth(50);


            // postavi nacin placanja na 0 (gotovnisko)
            nacinPlacanja = 0;
            transakcijskiNiz = "";

            // r1 / r2 dodatak je inicijalno prazan
            r1r2Dodatak = "";
            // uredjaj dodatak
            uredjajDodatak = "";
            // napomena
            napomena = "";

            // Inicijalizacija popisa blagajnika
            blagajnik = new Hashtable<String, String>();
            // provjeri da li datoteka postoji
            File fileBlagajnik = new File("blagajnik.txt");
            if (fileBlagajnik.exists()) {
                // ucitaj blagajnike
                loadBlagajnik("blagajnik.txt");
            } else {
                logger.severe("Datoteka blagajnik.txt ne postoji!.");
            }

            // Koliko ima blagajnika na popisu ?
            if (blagajnik.size() < 1) {
                // ako nema blagajnika, tada koristi naziv i oib vlasnika
                oibBlagajnika = frmPostavke.getOib();
                blagajnik.put(frmPostavke.getOib(), frmPostavke.getVlasnik());
                // ugasni dugmice za prozor blagajnika
                cmdBlagajnik.setVisible(false);
            } else if (blagajnik.size() > 1) {
                // provjera da li je brzni nacin odabira blagajnika aktiviran
                if (frmPostavke.getIzborBlagajnika().equalsIgnoreCase("brzi")) {
                    // samo omoguci dugme Blagajnik
                    cmdBlagajnik.setVisible(true);
                    // odaberi prvog                    
                    for (String key : blagajnik.keySet()) {
                        oibBlagajnika = key;
                        break;
                    }
                } else {
                    // ima ih vise, tada prikazi dialog za izbor prema OIBu
                    createOibConfirmDialog(true);
                }
            } else {
                // odaberi prvog                    
                for (String key : blagajnik.keySet()) {
                    oibBlagajnika = key;
                    break;
                }
                // ugasni dugmice za prozor blagajnika
                cmdBlagajnik.setVisible(false);
            }

            // napravi formu O programu
            if (this.frmOProgramu == null) {
                this.frmOProgramu = new FrmOProgramu(this);
                frmOProgramu.osvjeziPoruku();
            }

            //naslov gl. prozora
            setTitle(this.getTitle() + " v." + frmOProgramu.ver);

            // postavi pocetne vrijednosti
            jir = "";
            zkod = "";
            popust = 0;
            zadnjiPopust = "0.00";

            // azuriraj racun
            updateRacun();

            // dodaj u log odabranog blagajnika i oib
            sLog = sLog + "\n\nBLAGAJNIK\n" + blagajnik.get(oibBlagajnika) + ": " + oibBlagajnika + "\n\n";
            // ima li oib 11 znamenki?
            if (oibBlagajnika.length() != 11) {
                sLog = sLog + "Greska: OIB " + oibBlagajnika + " nema 11 znakova.";
            }

            // dodaj u log ucitane postavke
            sLog = sLog + "POSTAVKE\n";
            sLog = sLog + "Privatni kljuc: " + frmPostavke.getKeyFile() + "\n";
            sLog = sLog + "       Zaporka: " + frmPostavke.getKeyPassword() + "\n";
            sLog = sLog + "           OIB: " + frmPostavke.getOib() + "\n";
            sLog = sLog + "           URL: " + frmPostavke.getsURL() + "\n";
            sLog = sLog + "    Poslovnica: " + frmPostavke.getPoslovnica() + "\n";
            sLog = sLog + "  Br. blagajne: " + frmPostavke.getBlagajna() + "\n";
            sLog = sLog + "       Vlasnik: " + frmPostavke.getVlasnik() + "\n";
            sLog = sLog + "         Naziv: " + frmPostavke.getNaziv() + "\n";
            sLog = sLog + "Font i velicin: " + frmPostavke.getFontNaziv() + " " + frmPostavke.getFontVelicina() + "\n";
            sLog = sLog + " i.net timeout: " + frmPostavke.getTimeout() + "\n";
            sLog = sLog + " Duzina linije: " + frmPostavke.getDuzinaLinije() + "\n\n";

            File kljuc = new File(frmPostavke.getKeyFile());
            sLog = sLog + "PRIVATNI KLJUC\n";
            if (kljuc.exists()) {
                sLog = sLog + "Privatni kljuc: Datoteka postoji.\n";
                sLog = sLog + " Serijski broj: " + frmOProgramu.getSerijskiBroj() + "\n\n";
            } else {
                sLog = sLog + "Privatni kljuc: Ne postoji datoteka!\n\n";
                JOptionPane.showMessageDialog(this, "Kljuc:\n" + frmPostavke.getKeyFile() + " ne postoji. Molimo snimite od Fine privatni kljuc/certifikat.", "Blagajna", JOptionPane.ERROR_MESSAGE);
            }
            sLog = sLog + "Zaglavlje rac.:\n" + header + "\n\n";
            sLog = sLog + "Podnozje  rac.:\n" + footer + "\n\n";

            // pokreni proceduru provjere internet veze
            vremenskiInterval = frmPostavke.getTimeout();
            provjeriInternetVezu();

            // Dodatak za pacijenti.txt
            final File txtPacijenti = new File("pacijenti.txt");
            if (txtPacijenti.exists()) {
                /**
                 * Inicijalizacija strukture za pacijente.
                 */
                pacijenti = new Hashtable<Integer, String>();
                /**
                 * Dretva koja učitava pojedinačno svakog pacijenta, liniju po
                 * liniju.
                 */
                Thread pacijentThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Otvori datoteku za citanje
                        BufferedReader br;
                        try {
                            // uzmi vrijeme pocetka slanja soap poruke
                            Benchmark bm = new Benchmark();
                            bm.start();

                            br = new BufferedReader(new FileReader(txtPacijenti));

                            // Pomocne varijable
                            String line;
                            int lineNum = 0;

                            while ((line = br.readLine()) != null) {
                                // prva linija ima br.vrijednos 1, a ne 0
                                lineNum++;
                                // barem 5 znakova duljine treba biti da se obradi
                                //  i imati ; razdvajanje
                                if (line.length() > 5 && !line.startsWith("#")) {

                                    String imeKarton = line.substring(line.indexOf(';') + 1);

                                    // Sada imamo ime;karton podatak
                                    // Treba jos i to razdvojiti
                                    if (imeKarton.indexOf(';') > 1) {
                                        // Ime i karton
                                        String ime = imeKarton.substring(0, imeKarton.indexOf(';'));
                                        String karton = imeKarton.substring(imeKarton.indexOf(';') + 1, imeKarton.length());
                                        try {
                                            // Dodaj pacijenta                                            
                                            pacijenti.put(Integer.parseInt(karton), ime);
                                        } catch (NumberFormatException nfe) {
                                            // nista :(
                                        }

                                    }

                                }
                            }
                            // zatvori stream
                            br.close();

                            // koliko ih ima ?                            
                            logger.info("Broj pacijenata: " + lineNum + ", ucitano: " + bm.getMSec() + " msec.");

                            // dugme ucini vidljivim ako ima pacijenata
                            cmdPacijent.setVisible(pacijenti.size() > 0);

                        } catch (FileNotFoundException ex) {
                            logger.log(Level.SEVERE, "pacijentThread()", ex);
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, "pacijentThread()", ex);
                        }
                    }
                });
                pacijentThread.start();
            } else {
                cmdPacijent.setVisible(false);
            }
            // Inicijalizacija pacijenta
            odabraniPacijent = 0;


            // posalji logove na syslog/datoteku
            logger.info(sLog);

        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        // ako postoji popis uredjaja, tada prikazi dugme
        File uredjaji = new File("uredjaji.txt");
        cmdUredjaj.setVisible(uredjaji.exists());


        // override defaultnog ponašanja Enter tipke nad tablicom, konkretno ovdje se sprečava
        // propadanje u novi red i istvremeno updejta račun
        createKeybindings(tblProizvod);


        // postavlja "vruce tipke" F1, F2...
        setUpHotKeys();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSplitter = new javax.swing.JSplitPane();
        pnlProizvod = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProizvod = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        pnlRacun = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtRacun = new javax.swing.JTextArea();
        progresBar = new javax.swing.JProgressBar();
        pnlScrollIzbornik = new javax.swing.JScrollPane();
        pnlIzbornik = new javax.swing.JPanel();
        cmdIspis = new javax.swing.JButton();
        cmdNovi = new javax.swing.JButton();
        cmdPoslovniProstor = new javax.swing.JButton();
        cmdIzlaz = new javax.swing.JButton();
        cmdPostavke = new javax.swing.JButton();
        cmdCjenik = new javax.swing.JButton();
        cmdNacinPlacanja = new javax.swing.JButton();
        cmdBlagajnik = new javax.swing.JButton();
        cmdPromet = new javax.swing.JButton();
        cmdOProgramu = new javax.swing.JButton();
        cmdPopust = new javax.swing.JButton();
        cmdR1R2 = new javax.swing.JButton();
        cmdPacijent = new javax.swing.JButton();
        cmdUredjaj = new javax.swing.JButton();
        cmdNapomena = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Blagajna");

        pnlProizvod.setBorder(javax.swing.BorderFactory.createTitledBorder("Proizvodi"));

        tblProizvod.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Proizvod", "Cijena", "PDV", "PNP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProizvod.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblProizvod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblProizvodKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dodajNaRacun(evt);
            }
        });
        jScrollPane2.setViewportView(tblProizvod);

        txtSearch.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtSearchInputMethodTextChanged(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearchKeyTyped(evt);
            }
        });

        lblStatus.setText("Internet veza");

        javax.swing.GroupLayout pnlProizvodLayout = new javax.swing.GroupLayout(pnlProizvod);
        pnlProizvod.setLayout(pnlProizvodLayout);
        pnlProizvodLayout.setHorizontalGroup(
            pnlProizvodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProizvodLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlProizvodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                    .addComponent(txtSearch)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlProizvodLayout.setVerticalGroup(
            pnlProizvodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProizvodLayout.createSequentialGroup()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus))
        );

        pnlSplitter.setLeftComponent(pnlProizvod);

        pnlRacun.setBorder(javax.swing.BorderFactory.createTitledBorder("Racun"));
        pnlRacun.setPreferredSize(new java.awt.Dimension(201, 364));

        txtRacun.setEditable(false);
        txtRacun.setColumns(20);
        txtRacun.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        txtRacun.setRows(5);
        txtRacun.setFocusable(false);
        jScrollPane1.setViewportView(txtRacun);

        javax.swing.GroupLayout pnlRacunLayout = new javax.swing.GroupLayout(pnlRacun);
        pnlRacun.setLayout(pnlRacunLayout);
        pnlRacunLayout.setHorizontalGroup(
            pnlRacunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRacunLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRacunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                    .addComponent(progresBar, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlRacunLayout.setVerticalGroup(
            pnlRacunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRacunLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progresBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlSplitter.setRightComponent(pnlRacun);

        pnlIzbornik.setBorder(javax.swing.BorderFactory.createTitledBorder("Izbornik"));

        cmdIspis.setText("Ispis");
        cmdIspis.setToolTipText("Ispis i prijava racuna");
        cmdIspis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdIspisActionPerformed(evt);
            }
        });
        cmdIspis.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdIspisFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdIspisFocusLost(evt);
            }
        });

        cmdNovi.setText("Novi racun");
        cmdNovi.setToolTipText("Napravi novi racun, za ponovni ispis");
        cmdNovi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNoviActionPerformed(evt);
            }
        });
        cmdNovi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdNoviFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdNoviFocusLost(evt);
            }
        });

        cmdPoslovniProstor.setText("Poslovni prostor");
        cmdPoslovniProstor.setToolTipText("Prijavite poslovni prostor");
        cmdPoslovniProstor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPoslovniProstorActionPerformed(evt);
            }
        });
        cmdPoslovniProstor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdPoslovniProstorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdPoslovniProstorFocusLost(evt);
            }
        });

        cmdIzlaz.setText("Izlaz");
        cmdIzlaz.setToolTipText("Kraj rada");
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

        cmdPostavke.setText("Postavke");
        cmdPostavke.setToolTipText("Postavke programa");
        cmdPostavke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPostavkeActionPerformed(evt);
            }
        });
        cmdPostavke.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdPostavkeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdPostavkeFocusLost(evt);
            }
        });

        cmdCjenik.setText("Cjenik / Proizvodi");
        cmdCjenik.setToolTipText("Dodavanje, brisanje i promjena proizvoda u cjeniku");
        cmdCjenik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCjenikActionPerformed(evt);
            }
        });
        cmdCjenik.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdCjenikFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdCjenikFocusLost(evt);
            }
        });

        cmdNacinPlacanja.setText("Nacin placanja");
        cmdNacinPlacanja.setToolTipText("Odabir nacina placanja za racun");
        cmdNacinPlacanja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNacinPlacanjaActionPerformed(evt);
            }
        });
        cmdNacinPlacanja.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdNacinPlacanjaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdNacinPlacanjaFocusLost(evt);
            }
        });

        cmdBlagajnik.setText("Blagajnik / Zakljucaj");
        cmdBlagajnik.setToolTipText("Odabir blagajnika");
        cmdBlagajnik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBlagajnikActionPerformed(evt);
            }
        });
        cmdBlagajnik.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdBlagajnikFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdBlagajnikFocusLost(evt);
            }
        });

        cmdPromet.setText("Promet");
        cmdPromet.setToolTipText("Ispis, storniranje, ponovno slanje, statistika po danima");
        cmdPromet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPrometActionPerformed(evt);
            }
        });
        cmdPromet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdPrometFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdPrometFocusLost(evt);
            }
        });

        cmdOProgramu.setText("O programu");
        cmdOProgramu.setToolTipText("Registracija programa, prijava logova");
        cmdOProgramu.setMaximumSize(new java.awt.Dimension(57, 23));
        cmdOProgramu.setMinimumSize(new java.awt.Dimension(57, 23));
        cmdOProgramu.setPreferredSize(new java.awt.Dimension(57, 23));
        cmdOProgramu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOProgramuActionPerformed(evt);
            }
        });
        cmdOProgramu.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdOProgramuFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdOProgramuFocusLost(evt);
            }
        });

        cmdPopust.setText("Popust");
        cmdPopust.setToolTipText("Popust, prije izdavanja racuna odaberite");
        cmdPopust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPopustActionPerformed(evt);
            }
        });
        cmdPopust.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdPopustFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdPopustFocusLost(evt);
            }
        });

        cmdR1R2.setText("R1 / R2");
        cmdR1R2.setToolTipText("R1 ili R2 podaci na racunu");
        cmdR1R2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdR1R2ActionPerformed(evt);
            }
        });
        cmdR1R2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdR1R2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdR1R2FocusLost(evt);
            }
        });

        cmdPacijent.setText("Pacijent");
        cmdPacijent.setToolTipText("Odabira pacijenta");
        cmdPacijent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPacijentActionPerformed(evt);
            }
        });
        cmdPacijent.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdPacijentFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdPacijentFocusLost(evt);
            }
        });

        cmdUredjaj.setText("Uredjaj");
        cmdUredjaj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdUredjajActionPerformed(evt);
            }
        });
        cmdUredjaj.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdUredjajFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdUredjajFocusLost(evt);
            }
        });

        cmdNapomena.setText("Napomena");
        cmdNapomena.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNapomenaActionPerformed(evt);
            }
        });
        cmdNapomena.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdNapomenaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdNapomenaFocusLost(evt);
            }
        });

        javax.swing.GroupLayout pnlIzbornikLayout = new javax.swing.GroupLayout(pnlIzbornik);
        pnlIzbornik.setLayout(pnlIzbornikLayout);
        pnlIzbornikLayout.setHorizontalGroup(
            pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIzbornikLayout.createSequentialGroup()
                .addGroup(pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cmdOProgramu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdPromet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdBlagajnik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdNacinPlacanja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdPoslovniProstor, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(cmdNovi, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(cmdIspis, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(cmdIzlaz, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(cmdPostavke, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .addComponent(cmdCjenik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdPopust, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdR1R2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdPacijent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdUredjaj, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdNapomena, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlIzbornikLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdBlagajnik, cmdCjenik, cmdIspis, cmdIzlaz, cmdNacinPlacanja, cmdNapomena, cmdNovi, cmdOProgramu, cmdPacijent, cmdPopust, cmdPoslovniProstor, cmdPostavke, cmdPromet, cmdR1R2, cmdUredjaj});

        pnlIzbornikLayout.setVerticalGroup(
            pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIzbornikLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmdIspis, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdNovi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdPoslovniProstor, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdPostavke, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdCjenik)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdNacinPlacanja, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdBlagajnik, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdPromet, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdPopust, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdR1R2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdPacijent, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdUredjaj, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdNapomena, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addComponent(cmdOProgramu, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdIzlaz, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pnlIzbornikLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdBlagajnik, cmdCjenik, cmdIspis, cmdIzlaz, cmdNacinPlacanja, cmdNapomena, cmdNovi, cmdOProgramu, cmdPacijent, cmdPopust, cmdPoslovniProstor, cmdPostavke, cmdPromet, cmdR1R2, cmdUredjaj});

        pnlScrollIzbornik.setViewportView(pnlIzbornik);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlSplitter, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlScrollIzbornik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlSplitter, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlScrollIzbornik, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdCjenikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCjenikActionPerformed

        // napravi prozor FrmEditirajProizvode ako ne postoji
        if (this.frmCjenik == null) {
            this.frmCjenik = new FrmCjenik(this);
        }
        toggleScreen(false);
        this.frmCjenik.initCjenik();
        this.frmCjenik.setVisible(true);

    }//GEN-LAST:event_cmdCjenikActionPerformed

    private void cmdNacinPlacanjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNacinPlacanjaActionPerformed

        // pomjeri nacin placanja za +1
        nacinPlacanja++;

        // ako predje 3, vrati na 0
        if (nacinPlacanja > 3) {
            nacinPlacanja = 0;
        }

        // ako je transakcijski racun ukljucen, ponudi unos transakcije.
        if (nacinPlacanja == 2) {
            if (frmPostavke.isTransakcijski()) {
                String tmp = JOptionPane.showInputDialog(this, "Transakcijski broj:",
                        "Blagajna", JOptionPane.INFORMATION_MESSAGE);
                if (tmp == null) {
                    // korisnik se predomislio            
                    // :D        
                    transakcijskiNiz = "";
                } else {
                    transakcijskiNiz = "\n" + tmp;
                }
            } else {
                transakcijskiNiz = "";
            }
        } else {
            transakcijskiNiz = "";
        }

        // azuriraj izgled racuna
        updateRacun();

    }//GEN-LAST:event_cmdNacinPlacanjaActionPerformed

    private void cmdBlagajnikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBlagajnikActionPerformed

        // provjeri da li se koristi brzi odabir blagajnika
        if (frmPostavke.getIzborBlagajnika().equalsIgnoreCase("brzi")) {
            // pronadji trenutno odabranog blagajnika
            // nadji oznaku blagajnika prema oib-u
            boolean pronadjenOIB = false;
            for (String oib : blagajnik.keySet()) {
                if (pronadjenOIB) {
                    oibBlagajnika = oib;
                    // Azuriraj racun
                    updateRacun();
                    // Prikazi u statusu
                    lblStatus.setText("Blagajnik: " + blagajnik.get(oibBlagajnika) + ", OIB:" + oibBlagajnika);
                    lblStatus.setForeground(Color.BLACK);
                    // Postavi zastavicu da nije pronadjen
                    pronadjenOIB = false;
                    // izadji iz for petlje
                    break;
                }
                if (oib.equalsIgnoreCase(oibBlagajnika)) {
                    pronadjenOIB = true;
                }
            }
            // Ako je zadnji oib u popisu blagajnika ...
            if (pronadjenOIB) {
                for (String oib : blagajnik.keySet()) {
                    // Uzmi prvog
                    oibBlagajnika = oib;
                    // Azuriraj racun
                    updateRacun();
                    // Prikazi u statusu
                    lblStatus.setText("Blagajnik: " + blagajnik.get(oibBlagajnika) + ", OIB:" + oibBlagajnika);
                    lblStatus.setForeground(Color.BLACK);
                    // izadji iz for petlje
                    break;
                }
            }

        } else {
            toggleScreen(false);
            createOibConfirmDialog(false);
        }
    }//GEN-LAST:event_cmdBlagajnikActionPerformed

    private void cmdPrometActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPrometActionPerformed

        if (this.frmPromet == null) {
            this.frmPromet = new FrmPromet(this.frmPostavke, this);
        }
        this.frmPromet.setVisible(true);

    }//GEN-LAST:event_cmdPrometActionPerformed

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        // TODO add your handling code here:

        // sa ENTER se dodaje odabrani proizvod
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
            Proizvod p;
            int rowCount = tblProizvod.getRowCount();
            if (rowCount == 1) {
                int modelRow = tblProizvod.convertRowIndexToModel(0);
                p = tableModel.getDataObject(modelRow);
            } else {
                p = proizvodiMap.get(txtSearch.getText());
            }
            // dodaj ga na racun
            if (p != null) {
                r1.addProizvod(p);
                updateRacun();
            }
        }
        if (evt.getKeyChar() == KeyEvent.VK_DELETE) {
            Proizvod p;
            int rowCount = tblProizvod.getRowCount();
            if (rowCount == 1) {
                int modelRow = tblProizvod.convertRowIndexToModel(0);
                p = tableModel.getDataObject(modelRow);
            } else {
                p = proizvodiMap.get(txtSearch.getText());
            }
            // dodaj ga na racun
            if (p != null) {
                r1.removeProizvod(p);
                updateRacun();
            }
        }




    }//GEN-LAST:event_txtSearchKeyPressed

    private void txtSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchKeyTyped

    private void txtSearchInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtSearchInputMethodTextChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInputMethodTextChanged

    private void cmdOProgramuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOProgramuActionPerformed

        //toggleScreen(false);
        frmOProgramu.osvjeziPoruku();
        frmOProgramu.setVisible(true);

    }//GEN-LAST:event_cmdOProgramuActionPerformed

    private void cmdPopustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPopustActionPerformed

        // Popust na ukupnu cijenu

        // povratna vr. dialoga
        int retVal;

        // paneli
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();

        // text polja
        final JTextField txtCijena = new JTextField(14);
        final JTextField txtPopust = new JTextField(14);

        // pocetne vr.
        txtCijena.setText(r1.getUkupanIznosIspis());
        txtCijena.setEditable(false);
        txtPopust.setText(zadnjiPopust);
        txtPopust.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPopust.setSelectionStart(0);
                txtPopust.setSelectionEnd(txtPopust.getText().length());
            }
        });
        txtCijena.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCijena.setSelectionStart(0);
                txtCijena.setSelectionEnd(txtCijena.getText().length());
            }
        });

        // povezivanje
        p1.add(new JLabel("Ukupan iznos: "));
        p1.add(txtCijena);
        p2.add(new JLabel("Popust (%): "));
        p2.add(txtPopust);

        // obj. koji sadrzi sve panele
        Object[] message = {p1, p2};

        // slozi izgled
        for (int i = 0; i < message.length; i++) {
            JPanel panel = (JPanel) message[i];
            for (Component comp : panel.getComponents()) {
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
            }
        }

        retVal = JOptionPane.showOptionDialog(
                this, message, "Popust", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);
        if (retVal == JOptionPane.OK_OPTION) {

            try {
                // probaj pretvoriti niz u broj
                double tmpPopust = Double.parseDouble(txtPopust.getText());
                // provjeri da li je od 1% do 99%
                if (tmpPopust >= 0.00 && popust < 100.00) {
                    // postavi popust, kao dec.broj (0 do 1)
                    popust = tmpPopust / 100;
                    // postavi zadnje koristenu vr. popusta, tako da
                    //  korisnik ne mora opet postavljati u text polje
                    zadnjiPopust = txtPopust.getText();
                    // azuriraj txtRacun
                    updateRacun();
                } else {
                    JOptionPane.showMessageDialog(
                            this, "Molimo unesite popust u postotku od 0.00 % do 99.00 %.\nNpr. 10.00 za 10% popusta.\nPazite da koristite tocku kao decimalni separator.", "Popust", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this, "Molimo unesite popust u postotku od 1.00 % do 99.00 %.\nNpr. 10.00 za 10% popusta.\nPazite da koristite tocku kao decimalni separator.", "Popust", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_cmdPopustActionPerformed

    private void cmdIspisFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIspisFocusGained
        cmdIspis.setBackground(boja2);
    }//GEN-LAST:event_cmdIspisFocusGained

    private void cmdIspisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIspisFocusLost
        cmdIspis.setBackground(boja1);
    }//GEN-LAST:event_cmdIspisFocusLost

    private void cmdNoviFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdNoviFocusGained
        cmdNovi.setBackground(boja2);
    }//GEN-LAST:event_cmdNoviFocusGained

    private void cmdNoviFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdNoviFocusLost
        cmdNovi.setBackground(boja1);
    }//GEN-LAST:event_cmdNoviFocusLost

    private void cmdPoslovniProstorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPoslovniProstorFocusGained
        cmdPoslovniProstor.setBackground(boja2);
    }//GEN-LAST:event_cmdPoslovniProstorFocusGained

    private void cmdPoslovniProstorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPoslovniProstorFocusLost
        cmdPoslovniProstor.setBackground(boja1);
    }//GEN-LAST:event_cmdPoslovniProstorFocusLost

    private void cmdPostavkeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPostavkeFocusGained
        cmdPostavke.setBackground(boja2);
    }//GEN-LAST:event_cmdPostavkeFocusGained

    private void cmdPostavkeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPostavkeFocusLost
        cmdPostavke.setBackground(boja1);
    }//GEN-LAST:event_cmdPostavkeFocusLost

    private void cmdCjenikFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdCjenikFocusGained
        cmdCjenik.setBackground(boja2);
    }//GEN-LAST:event_cmdCjenikFocusGained

    private void cmdCjenikFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdCjenikFocusLost
        cmdCjenik.setBackground(boja1);
    }//GEN-LAST:event_cmdCjenikFocusLost

    private void cmdNacinPlacanjaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdNacinPlacanjaFocusGained
        cmdNacinPlacanja.setBackground(boja2);
    }//GEN-LAST:event_cmdNacinPlacanjaFocusGained

    private void cmdNacinPlacanjaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdNacinPlacanjaFocusLost
        cmdNacinPlacanja.setBackground(boja1);
    }//GEN-LAST:event_cmdNacinPlacanjaFocusLost

    private void cmdBlagajnikFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdBlagajnikFocusGained
        cmdBlagajnik.setBackground(boja2);
    }//GEN-LAST:event_cmdBlagajnikFocusGained

    private void cmdBlagajnikFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdBlagajnikFocusLost
        cmdBlagajnik.setBackground(boja1);
    }//GEN-LAST:event_cmdBlagajnikFocusLost

    private void cmdPrometFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPrometFocusGained
        cmdPromet.setBackground(boja2);
    }//GEN-LAST:event_cmdPrometFocusGained

    private void cmdPrometFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPrometFocusLost
        cmdPromet.setBackground(boja1);
    }//GEN-LAST:event_cmdPrometFocusLost

    private void cmdPopustFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPopustFocusGained
        cmdPopust.setBackground(boja2);
    }//GEN-LAST:event_cmdPopustFocusGained

    private void cmdPopustFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPopustFocusLost
        cmdPopust.setBackground(boja1);
    }//GEN-LAST:event_cmdPopustFocusLost

    private void cmdOProgramuFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdOProgramuFocusGained
        cmdOProgramu.setBackground(boja2);
    }//GEN-LAST:event_cmdOProgramuFocusGained

    private void cmdOProgramuFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdOProgramuFocusLost
        cmdOProgramu.setBackground(boja1);
    }//GEN-LAST:event_cmdOProgramuFocusLost

    private void cmdIzlazFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIzlazFocusGained
        cmdIzlaz.setBackground(boja2);
    }//GEN-LAST:event_cmdIzlazFocusGained

    private void cmdIzlazFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIzlazFocusLost
        cmdIzlaz.setBackground(boja1);
    }//GEN-LAST:event_cmdIzlazFocusLost

    private void cmdR1R2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdR1R2ActionPerformed

        // povratna vr. dialoga
        int retVal;

        // paneli
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        JPanel p5 = new JPanel();
        JPanel p6 = new JPanel();

        // text polja
        final JTextField txtFirma = new JTextField(14);
        final JTextField txtOIB = new JTextField(14);
        final JTextField txtAdresa = new JTextField(14);
        final JTextField txtKontakt = new JTextField(14);
        ButtonGroup r1r2Grupa = new ButtonGroup();
        JRadioButton optR1 = new JRadioButton("R1", true);
        JRadioButton optR2 = new JRadioButton("R2", false);
        r1r2Grupa.add(optR1);
        r1r2Grupa.add(optR2);

        /**
         * Popis firmi za R1/R2.<BR> <B>Format:</B> <I>Firma, ostal</I>
         */
        final Hashtable<String, String> popisFirmi = new Hashtable<String, String>();

        // Datoteka koja sadrzi popis
        final File txtFirme = new File("firme.txt");
        if (txtFirme.exists()) {
            /**
             * Dretva koja učitava pojedinačno svaku firmu, liniju po liniju.
             */
            Thread firmeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Otvori datoteku za citanje
                    BufferedReader br;
                    try {

                        br = new BufferedReader(new FileReader(txtFirme));

                        // Pomocne varijable
                        String line;
                        int lineNum = 0;

                        while ((line = br.readLine()) != null) {
                            // prva linija ima br.vrijednos 1, a ne 0
                            lineNum++;
                            // barem 5 znakova duljine treba biti da se obradi
                            //  i imati ; razdvajanje
                            if (line.length() > 5 && line.indexOf(';') > 1) {

                                // prvi dio do znaka ; je kljuc (firma)
                                String firma = line.substring(0, line.indexOf(';'));
                                // a ostatak se sprema kao vrijednost (value) u hash tablicu
                                String ostalo = line.substring(line.indexOf(';') + 1);

                                popisFirmi.put(firma, ostalo);

                            }
                        }
                        // zatvori stream
                        br.close();

                    } catch (FileNotFoundException ex) {
                        logger.log(Level.SEVERE, "firmeThread()", ex);
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "firmeThread()", ex);
                    }
                }
            });
            firmeThread.start();
        }

        txtFirma.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (evt.getKeyChar() >= '0' && evt.getKeyChar() <= 'z') {
                    for (String firma : popisFirmi.keySet()) {
                        int selIndex = txtFirma.getText().length();
                        if (firma.length() >= selIndex) {
                            if (firma.substring(0, selIndex).toLowerCase().equalsIgnoreCase(
                                    txtFirma.getText().toLowerCase())) {
                                txtFirma.setText(firma);
                                txtFirma.setCaretPosition(selIndex);
                                txtFirma.setSelectionStart(selIndex);
                                txtFirma.setSelectionEnd(txtFirma.getText().length());
                                break;
                            }
                        }
                    }
                }
            }
        });
        txtFirma.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFirma.setSelectionStart(0);
                txtFirma.setSelectionEnd(txtFirma.getText().length());
            }
        });
        txtOIB.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtFirma.getText().length() > 0) {
                    txtOIB.setSelectionStart(0);
                    txtOIB.setSelectionEnd(txtOIB.getText().length());
                } else {
                    txtOIB.setText("");
                    txtAdresa.setText("");
                    txtKontakt.setText("");
                }
            }
        });
        txtAdresa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAdresa.setSelectionStart(0);
                txtAdresa.setSelectionEnd(txtAdresa.getText().length());
            }
        });
        txtKontakt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtKontakt.setSelectionStart(0);
                txtKontakt.setSelectionEnd(txtKontakt.getText().length());
            }
        });

        txtFirma.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                String key = txtFirma.getText();
                if (popisFirmi.containsKey(key)) {
                    String tmp1 = popisFirmi.get(key);
                    txtOIB.setText(tmp1.substring(0, tmp1.indexOf(';')));
                    String tmp2 = tmp1.substring(tmp1.indexOf(';') + 1);
                    txtAdresa.setText(tmp2.substring(0, tmp2.indexOf(';')));
                    txtKontakt.setText(tmp2.substring(tmp2.indexOf(';') + 1));
                }
            }
        });

        // pocetne vr.
        if (r1r2Dodatak.length() > 0) {
            // Razlomi na pocetne linije
            String[] r1r2Elementi = r1r2Dodatak.split("[\\r\\n]+");

            int index = 0;
            for (String r1r2Element : r1r2Elementi) {
                if (index == 0) {
                    if (r1r2Element.equalsIgnoreCase("R1")) {
                        optR1.setSelected(true);
                    }
                    if (r1r2Element.equalsIgnoreCase("R2")) {
                        optR2.setSelected(true);
                    }      
                }
                else if (index == 1) {
                    txtFirma.setText(r1r2Element.substring(r1r2Element.indexOf(":") + 2).trim());
                }
                else if (index == 2) {
                    txtOIB.setText(r1r2Element.substring(r1r2Element.indexOf(":") + 2).trim());
                }
                else if (index == 3) {
                    txtAdresa.setText(r1r2Element.substring(r1r2Element.indexOf(":") + 2).trim());
                }                
                else if (index == 4) {
                    txtKontakt.setText(r1r2Element.substring(r1r2Element.indexOf(":") + 2).trim());
                }                
                index++;
            }

        } else {
            txtFirma.setText("");
            txtOIB.setText("");
            txtAdresa.setText("");
            txtKontakt.setText("");
        }

        // povezivanje
        p1.add(optR1);
        p1.add(optR2);
        p2.add(new JLabel("  Firma: "));
        p2.add(txtFirma);
        p3.add(new JLabel("    OIB: "));
        p3.add(txtOIB);
        p4.add(new JLabel(" Adresa: "));
        p4.add(txtAdresa);
        p5.add(new JLabel("Kontakt: "));
        p5.add(txtKontakt);
        p6.add(new JLabel("Za ukloniti R1/R2 sa racuna, ispraznite polja."));

        // obj. koji sadrzi sve panele
        Object[] message = {p1, p2, p3, p4, p5, p6};

        // slozi izgled
        for (int i = 0; i < message.length; i++) {
            JPanel panel = (JPanel) message[i];
            for (Component comp : panel.getComponents()) {
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
            }
        }

        retVal = JOptionPane.showOptionDialog(
                this, message, "R1 / R2", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);
        if (retVal == JOptionPane.OK_OPTION) {
            if (optR1.isSelected()) {
                r1r2Dodatak = "R1\n";
            } else {
                r1r2Dodatak = "R2\n";
            }
            r1r2Dodatak = r1r2Dodatak
                    + formatLine("  Firma:", txtFirma.getText()) + "\n"
                    + formatLine("    OIB:", txtOIB.getText());
            if (txtAdresa.getText().length() > 0) {
                r1r2Dodatak = r1r2Dodatak + "\n"
                        + formatLine(" Adresa:", txtAdresa.getText());
            }
            if (txtKontakt.getText().length() > 0) {
                r1r2Dodatak = r1r2Dodatak + "\n"
                        + formatLine("Kontakt:", txtKontakt.getText());
            }
            // Provjeri da li su polja prazna i ...
            int len = 0;
            for (int i = 0; i < message.length; i++) {
                JPanel panel = (JPanel) message[i];
                for (Component comp : panel.getComponents()) {
                    if (comp instanceof JTextField) {
                        JTextField textPolje = (JTextField) comp;
                        len = len + textPolje.getText().length();
                    }
                }
            }

            if (len == 0) {
                // ... ako su prazna, ukloni r1r2 dodatak na racunu.
                r1r2Dodatak = "";
            } else {
                // ... ako nisu, napravi novi zapis u popisu firmi
                // ili azuriraj postojeci i spremi u firme.txt datoteku.
                popisFirmi.put(txtFirma.getText(), txtOIB.getText() + ";" + txtAdresa.getText() + ";" + txtKontakt.getText());
                try {
                    PrintWriter printWriter = new PrintWriter(new FileWriter(txtFirme));
                    for (String key : popisFirmi.keySet()) {
                        printWriter.append(key + ";" + popisFirmi.get(key) + "\n");
                    }
                    printWriter.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "cmdR1R2ActionPerformed()", ex);
                }

            }

            updateRacun();
        }

    }//GEN-LAST:event_cmdR1R2ActionPerformed

    private void cmdR1R2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdR1R2FocusGained
        cmdR1R2.setBackground(boja2);
    }//GEN-LAST:event_cmdR1R2FocusGained

    private void cmdR1R2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdR1R2FocusLost
        cmdR1R2.setBackground(boja1);
    }//GEN-LAST:event_cmdR1R2FocusLost

    private void cmdPacijentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPacijentActionPerformed

        // povratna vr. dialoga
        int retVal;

        // paneli
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();

        // text polja
        final JTextField txtIme = new JTextField(14);
        final JTextField txtKarton = new JTextField(14);

        final JLabel lblInfo = new JLabel("Ako je novi pacijent, novi zapis ce biti dodan.");

        txtKarton.setToolTipText("Broj kartona. Ako je pacijent novi, tada unesite broj kartona koji nije dodijeljen nekom pacijentu.");

        // pocetne vr.
        if (odabraniPacijent > 0) {
            txtIme.setText(pacijenti.get(odabraniPacijent));
            txtKarton.setText(String.valueOf(odabraniPacijent));
        }

        // Eventi, na pritisak tipke, fokus, ...
        txtIme.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                    txtKarton.requestFocus();
                }
            }
        });
        txtIme.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (evt.getKeyChar() >= '0' && evt.getKeyChar() <= 'z') {
                    for (String ime : pacijenti.values()) {
                        int selIndex = txtIme.getText().length();
                        if (ime.length() >= selIndex) {
                            if (ime.substring(0, selIndex).toLowerCase().equalsIgnoreCase(
                                    txtIme.getText().toLowerCase())) {
                                txtIme.setText(ime);
                                txtIme.setCaretPosition(selIndex);
                                txtIme.setSelectionStart(selIndex);
                                txtIme.setSelectionEnd(txtIme.getText().length());
                                break;
                            }
                        }
                    }
                }
            }
        });

        txtKarton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                    txtIme.requestFocus();
                }
            }
        });
        txtIme.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtIme.setSelectionStart(0);
                txtIme.setSelectionEnd(txtIme.getText().length());
            }
        });
        txtIme.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                for (Integer key : pacijenti.keySet()) {
                    if (pacijenti.get(key).equalsIgnoreCase(txtIme.getText())) {
                        txtKarton.setText(String.valueOf(key));
                        break;
                    }
                }
            }
        });
        txtKarton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtKarton.setSelectionStart(0);
                txtKarton.setSelectionEnd(txtKarton.getText().length());
            }
        });
        txtKarton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                int karton;
                try {
                    karton = Integer.parseInt(txtKarton.getText());
                } catch (NumberFormatException ex) {
                    karton = 0;
                }
                if (pacijenti.containsKey(karton) && karton > 0) {
                    txtIme.setText(pacijenti.get(karton));
                } else {
                    lblInfo.setText("Novi pacijent. Karton " + karton + " ne postoji.");
                    lblInfo.setForeground(Color.BLUE);
                }
            }
        });

        // povezivanje
        p1.add(new JLabel("   Ime: "));
        p1.add(txtIme);
        p2.add(new JLabel("Karton: "));
        p2.add(txtKarton);
        p3.add(new JLabel("Uesite ime ili broj kartona."));
        p4.add(lblInfo);

        // obj. koji sadrzi sve panele
        Object[] message = {p1, p2, p3, p4};

        // slozi izgled
        for (int i = 0; i < message.length; i++) {
            JPanel panel = (JPanel) message[i];
            for (Component comp : panel.getComponents()) {
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
            }
        }

        retVal = JOptionPane.showOptionDialog(
                this, message, "Pacijent", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);
        if (retVal == JOptionPane.OK_OPTION) {

            /**
             * Broj kartona, prema kojemu se radi izbor pacijenta.
             */
            int karton = 0;

            try {

                // Ako br.kartona nije postavljen
                if (txtKarton.getText().length() == 0) {
                    // Da li postoji pacijent ?
                    if (pacijenti.containsValue(txtIme.getText())) {
                        /**
                         * Postavi karton prema imenu.
                         */
                        for (Integer key : pacijenti.keySet()) {
                            if (pacijenti.get(key).equalsIgnoreCase(txtIme.getText())) {
                                karton = key;
                                break;
                            }
                        }
                    } else {
                        // Ako ne postoji, onda pridjeli neki slob.br. kartona
                        int maxKarton = 0;
                        for (Integer key : pacijenti.keySet()) {
                            if (key > maxKarton) {
                                maxKarton = key;
                            }
                        }
                        karton = maxKarton + 1;
                    }
                } else {
                    karton = Integer.parseInt(txtKarton.getText());
                }

                /**
                 * Karton je ispravno unesen.
                 */
                if (pacijenti.containsKey(karton)) {
                    /**
                     * Ime i karton se slazu.
                     */
                    if (pacijenti.get(karton).equalsIgnoreCase(
                            txtIme.getText())) {
                        odabraniPacijent = karton;
                    } else {
                        /**
                         * Ime i karon se ne slazu. Prioritet ima ime.
                         */
                        if (pacijenti.containsValue(txtIme.getText())) {
                            /**
                             * Postavi karton prema imenu.
                             */
                            for (Integer key : pacijenti.keySet()) {
                                if (pacijenti.get(key).equalsIgnoreCase(txtIme.getText())) {
                                    odabraniPacijent = key;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    /**
                     * Dodaj novog pacijenta.
                     */
                    pacijenti.put(karton, txtIme.getText());
                    odabraniPacijent = karton;

                    if (JOptionPane.showOptionDialog(this, "Ime: " + txtIme.getText() + "\nKarton: " + karton + "\nTrajno spremiti pacijenta?",
                            "Novi pacijent", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)
                            == JOptionPane.OK_OPTION) {
                        try {
                            PrintWriter txtPacijent = new PrintWriter(new FileWriter("pacijenti.txt", true));
                            txtPacijent.append("0;" + txtIme.getText() + ";" + karton + "\r\n");
                            txtPacijent.close();
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, "cmdPacijentActionPerformed()", ex);
                        }
                    }

                }
            } catch (NumberFormatException nfe) {
                odabraniPacijent = 0;
            }
            updateRacun();
        }

    }//GEN-LAST:event_cmdPacijentActionPerformed

    private void cmdPacijentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPacijentFocusGained
        cmdPacijent.setBackground(boja2);
    }//GEN-LAST:event_cmdPacijentFocusGained

    private void cmdPacijentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPacijentFocusLost
        cmdPacijent.setBackground(boja1);
    }//GEN-LAST:event_cmdPacijentFocusLost

    private void cmdUredjajFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdUredjajFocusGained
        cmdUredjaj.setBackground(boja2);
    }//GEN-LAST:event_cmdUredjajFocusGained

    private void cmdUredjajFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdUredjajFocusLost
        cmdUredjaj.setBackground(boja1);
    }//GEN-LAST:event_cmdUredjajFocusLost

    private void cmdUredjajActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUredjajActionPerformed

        // povratna vr. dialoga
        int retVal;

        // paneli
        JPanel p1 = new JPanel();

        // text polja
        final JTextField txtUredjaj = new JTextField(14);

        /**
         * Popis uredjaja za racun.
         */
        final List<String> popisUredjaja = new ArrayList<String>();

        // Datoteka koja sadrzi popis
        final File txtUredjaji = new File("uredjaji.txt");
        if (txtUredjaji.exists()) {
            /**
             * Dretva koja učitava pojedinačno svaki uređaj, liniju po liniju.
             */
            Thread uredjajThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Otvori datoteku za citanje
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new FileReader(txtUredjaji));
                        // Pomocne varijable
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (line.length() > 0) {
                                popisUredjaja.add(line);
                            }
                        }
                        // zatvori stream
                        br.close();

                    } catch (FileNotFoundException ex) {
                        logger.log(Level.SEVERE, "uredjajThread()", ex);
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "uredjajThread()", ex);
                    }
                }
            });
            uredjajThread.start();
        }

        txtUredjaj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (evt.getKeyChar() >= '0' && evt.getKeyChar() <= 'z') {
                    for (String uredjaj : popisUredjaja) {
                        int selIndex = txtUredjaj.getText().length();
                        if (uredjaj.length() >= selIndex) {
                            if (uredjaj.substring(0, selIndex).toLowerCase().equalsIgnoreCase(
                                    txtUredjaj.getText().toLowerCase())) {
                                txtUredjaj.setText(uredjaj);
                                txtUredjaj.setCaretPosition(selIndex);
                                txtUredjaj.setSelectionStart(selIndex);
                                txtUredjaj.setSelectionEnd(txtUredjaj.getText().length());
                                break;
                            }
                        }
                    }
                }
            }
        });
        txtUredjaj.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUredjaj.setSelectionStart(0);
                txtUredjaj.setSelectionEnd(txtUredjaj.getText().length());
            }
        });



        // pocetne vr.
        if (uredjajDodatak.length() > 0) {
            txtUredjaj.setText(uredjajDodatak);
        }

        // povezivanje                
        p1.add(new JLabel("Uredjaj: "));
        p1.add(txtUredjaj);

        // obj. koji sadrzi sve panele
        Object[] message = {p1};

        // slozi izgled
        for (int i = 0; i < message.length; i++) {
            JPanel panel = (JPanel) message[i];
            for (Component comp : panel.getComponents()) {
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
            }
        }

        retVal = JOptionPane.showOptionDialog(
                this, message, "Uredjaj", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);
        if (retVal == JOptionPane.OK_OPTION) {
            uredjajDodatak = txtUredjaj.getText();
            if (uredjajDodatak.length() == 0) {
                // ... ako su prazna, ukloni dodatak na racunu.
                uredjajDodatak = "";
            } else {
                // ako ga nema na popisu, dodaj
                if (!popisUredjaja.contains(uredjajDodatak)) {
                    popisUredjaja.add(uredjajDodatak);
                    // ... i napravi novi zapis u popisu uređaja
                    try {
                        PrintWriter printWriter = new PrintWriter(new FileWriter(txtUredjaji, true));
                        printWriter.append(uredjajDodatak + "\n");
                        printWriter.close();
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "cmdUredjajActionPerformed()", ex);
                    }
                }
            }

            updateRacun();
        }

    }//GEN-LAST:event_cmdUredjajActionPerformed

    private void cmdNapomenaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdNapomenaFocusGained
        cmdNapomena.setBackground(boja2);
    }//GEN-LAST:event_cmdNapomenaFocusGained

    private void cmdNapomenaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdNapomenaFocusLost
        cmdNapomena.setBackground(boja1);
    }//GEN-LAST:event_cmdNapomenaFocusLost

    private void cmdNapomenaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNapomenaActionPerformed

        // povratna vr. dialoga
        int retVal;

        // paneli
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();

        // text polja
        JTextArea txtNapomena = new JTextArea(10, frmPostavke.getDuzinaLinije());

        // povezivanje                
        p1.add(new JLabel("NAPOMENA"));
        p2.add(txtNapomena);

        // obj. koji sadrzi sve panele
        Object[] message = {p1, p2};

        // slozi izgled
        for (int i = 0; i < message.length; i++) {
            JPanel panel = (JPanel) message[i];
            for (Component comp : panel.getComponents()) {
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
            }
        }

        txtNapomena.setText(napomena);

        retVal = JOptionPane.showOptionDialog(
                this, message, "Napomena", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);
        if (retVal == JOptionPane.OK_OPTION) {
            if (txtNapomena.getText().length() > 0) {
                napomena = txtNapomena.getText();
            } else {
                napomena = "";
            }

            updateRacun();
        }
    }//GEN-LAST:event_cmdNapomenaActionPerformed

    private void cmdNoviActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdNoviActionPerformed

        // isprazni racun
        this.r1 = new Racun();

        // ocisti zkod, jir, nacin placanja
        jir = "";
        zkod = "";
        nacinPlacanja = 0;
        transakcijskiNiz = "";
        popust = 0;
        r1r2Dodatak = "";
        uredjajDodatak = "";
        odabraniPacijent = 0;
        napomena = "";

        // Ukljuci dugmice opet
        cmdIspis.setEnabled(true);
        cmdBlagajnik.setEnabled(true);
        cmdNacinPlacanja.setEnabled(true);
        cmdPopust.setEnabled(true);


        // osvjezi txtRacun prikaz
        updateRacun();

        //obrisi filter
        txtSearch.setText("");
        newFilter();

        // traku postavi na pocetak
        progresBar.setValue(0);

        //postavi fokus na resetku
        tblProizvod.requestFocus();

    }// GEN-LAST:event_cmdNoviActionPerformed

    private void cmdPoslovniProstorActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdPoslovniProstorActionPerformed

        // napravi prozor PoslovniProstor ako ne postoji
        if (this.frmPP == null) {
            this.frmPP = new FrmPoslovniProstor(this);
        }
        toggleScreen(false);
        this.frmPP.setVisible(true);

    }// GEN-LAST:event_cmdPoslovniProstorActionPerformed

    private void cmdIzlazActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdIzlazActionPerformed

        // spremi postavke, najvise zbog broja racuna da se spremi
        frmPostavke.postavkeSave("postavke.txt");

        // kraj programa
        this.setVisible(false);
        System.exit(1);

    }// GEN-LAST:event_cmdIzlazActionPerformed

    /**
     * Procedura za dodavanj stavke na racun (Enter) ili uklanjanja (Del)
     *
     * @param evt predaje informaciju koja je tipka pristisnuta
     */
    private void dodajNaRacun(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_dodajNaRacun

        // ako je odabran proizvod iz tablice ...
        if (tblProizvod.getSelectedRowCount() > 0) {

            int viewRow = tblProizvod.getSelectedRow();
            int modelRow = tblProizvod.convertRowIndexToModel(viewRow);

            // sa DELETE se uklanja odabrani proizvod
            if (evt.getKeyChar() == KeyEvent.VK_DELETE) {
                Proizvod p = (Proizvod) tableModel.getDataObject(modelRow);
                // ukloni ga sa racuna
                r1.removeProizvod(p);
                updateRacun();
            }

            // sa BACKSPACE se brise zadnji dodani znak u filteru
            if (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                String txt = txtSearch.getText();
                int stringLen = txt.length();
                if (stringLen > 0) {
                    txtSearch.setText(txt.substring(0, stringLen - 1));
                    newFilter();
                }
            }

            // ako se unese znak, postavi u filter i filtriraj tablicu
            if (evt.getKeyChar() >= '0' && evt.getKeyChar() <= 'z') {
                String txt = txtSearch.getText();
                txtSearch.setText(txt + evt.getKeyChar());
                newFilter();
            }

        } else {
            // inace, ako nije odabran proizvod ...

            // oznaci prvi redak ako tablica nije prazna
            if (tblProizvod.getRowCount() > 0) {
                tblProizvod.setRowSelectionInterval(0, 0);
            } else {
                // ako je tablica prazna, samo provjeri za BACKSPACE
                if (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                    String txt = txtSearch.getText();
                    int stringLen = txt.length();
                    if (stringLen > 0) {
                        txtSearch.setText(txt.substring(0, stringLen - 1));
                        newFilter();
                    }
                }

                // ... ali i za ostale tipke omoguci unos u filter
                if (evt.getKeyChar() >= '0' && evt.getKeyChar() <= 'z') {
                    String txt = txtSearch.getText();
                    txtSearch.setText(txt + evt.getKeyChar());
                    newFilter();
                }
            }
        }

        // ako korisnik pritisne TAB, skoci na Ispis dugme
        if (evt.getKeyChar() == KeyEvent.VK_TAB) {
            if (cmdIspis.isEnabled()) {
                cmdIspis.requestFocus();
            } else {
                cmdNovi.requestFocus();
            }
        }


    }// GEN-LAST:event_dodajNaRacun

    private void createKeybindings(JTable table) {
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // ako je odabran proizvod iz tablice ...
                if (tblProizvod.getSelectedRowCount() > 0) {
                    int viewRow = tblProizvod.getSelectedRow();
                    int modelRow = tblProizvod.convertRowIndexToModel(viewRow);
                    Proizvod p = (Proizvod) tableModel.getDataObject(modelRow);
                    // Ako proizvod/usluga nema cijenu, tada ponudi
                    //  korisniku unos cijene
                    if (p.getCijena() == 0) {
                        // povratna vr. dialoga
                        int retVal;

                        // paneli
                        JPanel p1 = new JPanel();
                        JPanel p2 = new JPanel();

                        // text polja
                        final JTextField txtCijena = new JTextField(14);
                        final JTextField txtKolicina = new JTextField(14);

                        // pocetne vr.
                        txtCijena.setText("0.00");
                        txtKolicina.setText("");
                        txtCijena.setToolTipText("Cijena proizvoda. Npr. 120 (bez znaka KN)");
                        txtKolicina.setToolTipText("Dodatak u nazivu. Npr. 1kg");
                        txtKolicina.addFocusListener(new java.awt.event.FocusAdapter() {
                            public void focusGained(java.awt.event.FocusEvent evt) {
                                txtKolicina.setSelectionStart(0);
                                txtKolicina.setSelectionEnd(txtKolicina.getText().length());
                            }
                        });
                        txtCijena.addFocusListener(new java.awt.event.FocusAdapter() {
                            public void focusGained(java.awt.event.FocusEvent evt) {
                                txtCijena.setSelectionStart(0);
                                txtCijena.setSelectionEnd(txtCijena.getText().length());
                            }
                        });

                        // povezivanje
                        p1.add(new JLabel("Iznos: "));
                        p1.add(txtCijena);
                        p2.add(new JLabel("Kolicina: "));
                        p2.add(txtKolicina);

                        // obj. koji sadrzi sve panele
                        Object[] message = {p1, p2};

                        // slozi izgled
                        for (int i = 0; i < message.length; i++) {
                            JPanel panel = (JPanel) message[i];
                            for (Component comp : panel.getComponents()) {
                                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
                            }
                        }

                        retVal = JOptionPane.showOptionDialog(
                                null, message, "Proizvod", JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.INFORMATION_MESSAGE, null, null, null);
                        if (retVal == JOptionPane.OK_OPTION) {

                            double cijena;
                            try {
                                cijena = Double.parseDouble(txtCijena.getText());
                            } catch (Exception e) {
                                cijena = 0.00;
                            }

                            // Napravi novi Proizvod objekt koji ce se dodati na racun
                            Proizvod noviP = new Proizvod(
                                    p.getNaziv() + " " + txtKolicina.getText(), cijena, p.getPdv(), p.getPnp());

                            // dodaj ga na racun
                            r1.addProizvod(noviP);
                        }

                    } else {
                        // dodaj ga na racun odmah
                        r1.addProizvod(p);
                        // provjeri da li korisnik isti proizvod unosi vise puta
                        if (r1.getProizvodKolicina(p) > 1) {
                            lblStatus.setText("Sa kombinacijom Shift+Enter mozete unijeti kolicinu za proizvod.");
                            lblStatus.setForeground(Color.BLACK);
                        }
                    }
                    // azuriraj racun
                    updateRacun();
                }
            }
        });
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_MASK), "Shift+Enter");
        table.getActionMap().put("Shift+Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // ako je odabran proizvod iz tablice ...
                if (tblProizvod.getSelectedRowCount() > 0) {
                    int viewRow = tblProizvod.getSelectedRow();
                    int modelRow = tblProizvod.convertRowIndexToModel(viewRow);
                    Proizvod p = (Proizvod) tableModel.getDataObject(modelRow);
                    final double osnovnaCijena = p.getCijena();

                    DecimalFormat dF = new DecimalFormat("0.00");

                    // povratna vr. dialoga
                    int retVal;

                    // paneli
                    JPanel p1 = new JPanel();
                    JPanel p2 = new JPanel();

                    // text polja
                    final JTextField txtCijena = new JTextField(14);
                    final JTextField txtKolicina = new JTextField(14);

                    // pocetne vr.
                    txtCijena.setText(dF.format(p.getCijena()));
                    txtKolicina.setText("1.00");
                    txtCijena.setToolTipText("Ukupna cijena proizvoda. Umnozak kolicine i pocetne cijene.");
                    txtKolicina.setToolTipText("Mnozitelj za osnovnu cijenu.");
                    txtKolicina.addFocusListener(new java.awt.event.FocusAdapter() {
                        public void focusGained(java.awt.event.FocusEvent evt) {
                            txtKolicina.setSelectionStart(0);
                            txtKolicina.setSelectionEnd(txtKolicina.getText().length());
                        }
                    });
                    txtKolicina.addFocusListener(new java.awt.event.FocusAdapter() {
                        public void focusLost(java.awt.event.FocusEvent evt) {
                            DecimalFormat dF = new DecimalFormat("0.00");
                            double cijena = osnovnaCijena;

                            double kolicina;
                            try {
                                kolicina = Double.parseDouble(txtKolicina.getText());
                            } catch (Exception e) {
                                kolicina = 1.00;
                            }
                            txtCijena.setText(dF.format(cijena * kolicina));
                        }
                    });
                    txtCijena.addFocusListener(new java.awt.event.FocusAdapter() {
                        public void focusGained(java.awt.event.FocusEvent evt) {
                            txtCijena.setSelectionStart(0);
                            txtCijena.setSelectionEnd(txtCijena.getText().length());
                        }
                    });

                    // povezivanje
                    p1.add(new JLabel("Cijena: "));
                    p1.add(txtCijena);
                    p2.add(new JLabel("Kolicina: "));
                    p2.add(txtKolicina);

                    // obj. koji sadrzi sve panele
                    Object[] message = {p1, p2};

                    // slozi izgled
                    for (int i = 0; i < message.length; i++) {
                        JPanel panel = (JPanel) message[i];
                        for (Component comp : panel.getComponents()) {
                            comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
                        }
                    }

                    retVal = JOptionPane.showOptionDialog(
                            null, message, "Kolicina", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.INFORMATION_MESSAGE, null, null, null);
                    if (retVal == JOptionPane.OK_OPTION) {

                        double cijena = osnovnaCijena;

                        double kolicina;
                        try {
                            kolicina = Double.parseDouble(txtKolicina.getText());
                        } catch (Exception e) {
                            kolicina = 1.00;
                        }
                        // Napravi novi Proizvod objekt koji ce se dodati na racun
                        Proizvod noviP = new Proizvod(
                                p.getNaziv() + " x" + kolicina, cijena * kolicina, p.getPdv(), p.getPnp());

                        // dodaj ga na racun
                        r1.addProizvod(noviP);

                        // azuriraj racun                    
                        updateRacun();
                    }
                }
            }
        });

    }

    private void cmdIspisActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdIspisActionPerformed

        // ugasi dugme, kasnije korisnik ukljuci sa cmdNovi
        cmdIspis.setEnabled(false);
        cmdBlagajnik.setEnabled(false);
        cmdNacinPlacanja.setEnabled(false);
        cmdPopust.setEnabled(false);


        // uzmi timeout postavku
        final int timeout = frmPostavke.getTimeout();

        count = 0;
        timerSlanjeRacuna = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                count++;

                if (count <= timeout) {
                    progresBar.setValue(count);
                } else {
                    // ovo ne treba, inace ce se na interrupt prekinuti daljnje izvrsavanje
                    //  i npr. racun se nece ispisati, itd.
                    //sendThread.interrupt();
                    logger.info(("Greska u slanju racuna: " + brRac + ". Provjerite internet vezu."));
                    timerSlanjeRacuna.stop();
                }
            }
        });

        sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(0);

                    XMLRacun x1 = new XMLRacun();

                    x1.loadKeyStore(frmPostavke.getKeyFile(), frmPostavke.getKeyPassword());
                    x1.setOib(frmPostavke.getOib());
                    x1.setUSustPdv(frmPostavke.isUSustPdv());
                    x1.setOibOper(oibBlagajnika);
                    x1.setURL(frmPostavke.getsURL());
                    x1.setBrRac(brRac, frmPostavke.getPoslovnica(), frmPostavke.getBlagajna());

                    double pdv;
                    double pnp;
                    double osnovica;
                    double iznosPdv, iznosPnp;

                    // faktor korekcije, ako je popust veci od 0
                    double k = (1 - popust);

                    for (Proizvod p : r1.getProizvodi()) {
                        pdv = p.getPdv() * 100;
                        pnp = p.getPnp() * 100;
                        osnovica = p.getOsnovica();
                        iznosPdv = p.getIznosPdv();
                        iznosPnp = p.getIznosPnp();

                        if (pdv > 0) {
                            x1.addPDV(pdv, osnovica * k, iznosPdv * k);
                        }
                        if (pnp > 0) {
                            x1.addPNP(pnp, osnovica * k, iznosPnp * k);
                        }
                    }
                    x1.setIznosUkupno(r1.getUkupanIznos() * k);

                    if (nacinPlacanja == 0) {
                        x1.setNacinPlacanja("G");
                    } else if (nacinPlacanja == 1) {
                        x1.setNacinPlacanja("K");
                    } else if (nacinPlacanja == 2) {
                        x1.setNacinPlacanja("T");
                    } else if (nacinPlacanja == 3) {
                        x1.setNacinPlacanja("O");
                    }

                    x1.buildRacun();
                    x1.saveRacun("Racuni/racun" + brRac + ".xml");
                    x1.signRacun();
                    // nema potrebe spremnati potpisani xml
                    //x1.saveRacun("Racuni/racun" + brRac + ".signed.xml");

                    zkod = x1.getZastitniKod();
                    // azuriraj izgled
                    updateRacun();

                    // salji racun i postavi timeout pri slanju                        
                    x1.sendRacun(timeout);


                    if (x1.getGreska().length() > 0) {
                        if (count < timeout) {
                            logger.info(("Greska sa posluzitelja: " + x1.getGreska()));
                            x1.saveRacun("Racuni/racun" + brRac + ".error.xml");
                            timerSlanjeRacuna.stop();
                            progresBar.setValue(progresBar.getMaximum());
                            JOptionPane.showMessageDialog(
                                    null, "Prijavite gresku autorima:\n" + x1.getGreska(),
                                    "Greska sa posluzitelja", JOptionPane.ERROR_MESSAGE);
                        } else {
                            logger.info("Greska u internet vezi.");
                            progresBar.setValue(progresBar.getMaximum());
                        }
                    } else {
                        // racun je uspjesno poslan
                        x1.saveRacun("Racuni/racun" + brRac + ".response.xml");
                        timerSlanjeRacuna.stop();
                        progresBar.setValue(progresBar.getMaximum());
                    }

                    jir = x1.getJir();

                    // provjeri da li je prozor Promet ucitan
                    if (frmPromet != null) {
                        String prometDatum =
                                new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(frmPromet.getOdabraniDatum());
                        String sada = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(new Date());

                        // Da li je mjesecni prikaz aktiviran ili ne ?
                        if (frmPromet.getMjesecniPrikaz()) {
                            // ako prozor Promet prikazuje mjesecni promet za tekuci mjesec
                            //  onda dodaj stavku, inace nema smisla dodavati nesto sa danasnjim datumom
                            if (sada.substring(3).equalsIgnoreCase(prometDatum.substring(3))) {
                                frmPromet.addPromet(brRac, new Date(), x1.getIznosUkupno(),
                                        x1.getUkupnoPdv(), x1.getUkupnoPnp(), false,
                                        x1.getOibOper(), zkod, jir);
                            }
                        } else {
                            // ako prozor Promet prikazuje danasnji promet
                            //  onda dodaj stavku, inace nema smisla dodavati nesto sa danasnjim datumom
                            if (sada.equalsIgnoreCase(prometDatum)) {
                                frmPromet.addPromet(brRac, new Date(), x1.getIznosUkupno(),
                                        x1.getUkupnoPdv(), x1.getUkupnoPnp(), false,
                                        x1.getOibOper(), zkod, jir);
                            }
                        }
                    }

                    // azuriraj izgled
                    updateRacun();

                    // spremi u tekst datoteku
                    saveRacun("Racuni/racun" + brRac + ".txt");

                    Ispis ispis = new Ispis("racun" + brRac, frmPostavke.getPisac(),
                            frmPostavke.getPapirX(), frmPostavke.getPapirY(),
                            frmPostavke.getMarginaX(),
                            frmPostavke.getMarginaY(),
                            frmPostavke.getIspisnaPovrsinaX(),
                            frmPostavke.getIspisnaPovrsinaY());
                    
                    try {
                        /**
                         * Ispis.
                         */
                        JTextArea txtIspis = new JTextArea(txtRacun.getText());
                        txtIspis.setFont(new Font(frmPostavke.getFontNaziv(), Font.PLAIN, frmPostavke.getFontVelicina()));
                        ispis.print(txtIspis.getPrintable(null, null));
                    } catch (PrinterException ex) {
                        logger.log(Level.SEVERE, "cmdIspis()", ex);
                    }

                    // provjeri da li treba izvrsiti naredbu poslije slanja racuna
                    if (frmPostavke.getPoslijeSlanja().trim().length() > 0) {
                        try {
                            String cmd = frmPostavke.getPoslijeSlanja();
                            cmd = cmd.replace("{brRac}", String.valueOf(brRac));
                            cmd = cmd.replace("{racun.txt}", String.valueOf("racun" + brRac + ".txt"));
                            Runtime.getRuntime().exec(cmd);
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, "Runtime.getRuntime().exec(" + frmPostavke.getPoslijeSlanja() + ")", ex);
                        }
                    }

                    File txtRacun;
                    do {
                        // uvecaj broj racuna za 1
                        brRac++;
                        // provjeri da li datoteka racunXX.txt vec postoji
                        txtRacun = new File("Racuni/racun" + brRac + ".txt");
                    } while (txtRacun.exists());
  
                    // azuriraj u postavkama
                    frmPostavke.setBrRac(brRac);

                    // postavi fokus na izradu novog racuna
                    cmdNovi.requestFocus();


                } catch (InterruptedException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        });

        // provjeri da li ima proizvoda na racunu
        if (r1.getProizvodi().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nema stavki na racunu.",
                    "Greska", JOptionPane.ERROR_MESSAGE);
            cmdIspis.setEnabled(true);
        } else if ((brRac > 100)
                && (frmPostavke.getKeyFile().endsWith("test.pfx")
                && frmPostavke.getKeyPassword().startsWith("POS3x"))) {
            // provjeri da li je broj racuna presao 100 
            //  a da se i dalje koristi testni kljuc
            JOptionPane.showMessageDialog(this, "Zatrazite od Fine privatni kljuc kako bi dalje nastavili izdavati racune.",
                    "Greska", JOptionPane.ERROR_MESSAGE);
        } else if (blokiran) {
            JOptionPane.showMessageDialog(this, "Registrirajte program kako bi dalje nastavili izdavati racune.",
                    "Greska", JOptionPane.ERROR_MESSAGE);
        } else {
            // provjeri da li treba izvrsiti naredbu prije slanja racuna
            if (frmPostavke.getPrijeSlanja().trim().length() > 0) {
                try {
                    String cmd = frmPostavke.getPrijeSlanja();
                    cmd = cmd.replace("{brRac}", String.valueOf(brRac));
                    cmd = cmd.replace("{racun.txt}", String.valueOf("racun" + brRac + ".txt"));
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Runtime.getRuntime().exec(" + frmPostavke.getPrijeSlanja() + ")", ex);
                }
            }
            // ako je sve ok, salji racun i ispis ga
            timerSlanjeRacuna.start();
            sendThread.start();

        }

    }// GEN-LAST:event_cmdIspisActionPerformed

    private void cmdPostavkeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdPostavkeActionPerformed

        /*
         * Prije prikaza postavki,
         * postavi svjeze vr. za omjer i sirine stupaca.
         */
        frmPostavke.setOmjer(getSplitterPane());
        frmPostavke.getStupac().clear();
                        
        for (Integer stupac : new Integer[]{0, 1, 2, 3}) {
            int width = getTableColumnWidth(stupac);
            frmPostavke.getStupac().add(width);

        }

        
        toggleScreen(false);
        frmPostavke.setVisible(true);

    }// GEN-LAST:event_cmdPostavkeActionPerformed

    private void tblProizvodKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_tblProizvodKeyPressed

        // sa Enter se treba fokus prebaciti na cmdIspis
        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            if (cmdIspis.isEnabled()) {
                cmdIspis.requestFocus();
            } else {
                cmdNovi.requestFocus();
            }
            // this.getRootPane().setDefaultButton(cmdIspis);
        }

    }// GEN-LAST:event_tblProizvodKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdBlagajnik;
    private javax.swing.JButton cmdCjenik;
    private javax.swing.JButton cmdIspis;
    private javax.swing.JButton cmdIzlaz;
    private javax.swing.JButton cmdNacinPlacanja;
    private javax.swing.JButton cmdNapomena;
    private javax.swing.JButton cmdNovi;
    private javax.swing.JButton cmdOProgramu;
    private javax.swing.JButton cmdPacijent;
    private javax.swing.JButton cmdPopust;
    private javax.swing.JButton cmdPoslovniProstor;
    private javax.swing.JButton cmdPostavke;
    private javax.swing.JButton cmdPromet;
    private javax.swing.JButton cmdR1R2;
    private javax.swing.JButton cmdUredjaj;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel pnlIzbornik;
    private javax.swing.JPanel pnlProizvod;
    private javax.swing.JPanel pnlRacun;
    private javax.swing.JScrollPane pnlScrollIzbornik;
    private javax.swing.JSplitPane pnlSplitter;
    private javax.swing.JProgressBar progresBar;
    private javax.swing.JTable tblProizvod;
    private javax.swing.JTextArea txtRacun;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

    /**
     * Ucitava cjenik iz datoteke.<BR>
     *
     * @param fileName naziv datoteke.Trebala bi biti <I>cjenik.txt</I>.<BR>
     */
    public void initCjenik(String fileName) {

        try {

            // napravi Cjenik objekt
            Cjenik c1 = new Cjenik();

            // ucitaj cjenik iz datoteke
            c1.loadCjenik(fileName);

            // prvo isprazni tablicu proizvoda
            // u slucaju da je vec napunjena
            tableModel.deleteProizvodList();

            // napuni tablicu sa proizvodima
            tableModel.addProizvodList(c1.getProizvodi());

        } catch (IOException e) {
            logger.log(Level.SEVERE, "initCjenik()", e);
        }

    }

    /**
     * Funkcija za kontrolu cmdIspis dugmica
     *
     * @param state true ako zelimo blokirati ispis u cmdIspis
     */
    public void setBlokiran(boolean state) {
        blokiran = state;
    }

    /**
     * Format niza znakova za ispisa na fiksni broj znakova.<BR> Fiksni broj
     * znakova je odredjen sa <I>duzinaLinije</I> postavkom.<BR>
     *
     * @param str1 prvi niz
     * @param str2 drugi niz
     * @return spojeni niz sa razmakom izmedju <I>str1</I> i <I>str2</I>.
     */
    private String formatLine(String str1, String str2) {

        String result;

        if (frmPostavke.getDuzinaLinije() > (str1.length() + str2.length())) {
            // uzmi prvi arg.
            result = str1;

            // napuni razmacima
            for (int i = 0; i < frmPostavke.getDuzinaLinije() - (str1.length() + str2.length()); i++) {
                result = result + " ";
            }
            // dodaj drugi dio
            result = result + str2;
        } else {
            /**
             * Algoritam za prelamanje na fiksnu duljinu znakova.
             */
            if (str1.length() > frmPostavke.getDuzinaLinije()
                    && str2.length() < frmPostavke.getDuzinaLinije()) {

                String tmp = "";
                // razlom u tmp varijablu
                for (int i = 0; i < str1.length(); i = i + frmPostavke.getDuzinaLinije()) {
                    if (i + frmPostavke.getDuzinaLinije() < str1.length()) {
                        tmp = tmp + str1.substring(i, i + frmPostavke.getDuzinaLinije()) + "\n";
                    } else {
                        tmp = tmp + str1.substring(i) + "\n";
                    }
                }
                result = tmp + formatLine(" ", str2);
            } else if (str2.length() > frmPostavke.getDuzinaLinije()
                    && str1.length() < frmPostavke.getDuzinaLinije()) {

                String tmp = "";
                // razlom u tmp varijablu
                for (int i = 0; i < str2.length(); i = i + frmPostavke.getDuzinaLinije()) {
                    if (i + frmPostavke.getDuzinaLinije() < str2.length()) {
                        tmp = tmp + str2.substring(i, i + frmPostavke.getDuzinaLinije()) + "\n";
                    } else {
                        tmp = tmp + str2.substring(i) + "\n";
                    }
                }
                result = str1 + "\n" + tmp;
            } else if (str1.length() < frmPostavke.getDuzinaLinije()
                    && str2.length() < frmPostavke.getDuzinaLinije()) {
                result = str1 + "\n" + formatLine(" ", str2);
            } else {

                String tmp = "";
                // razlom u tmp varijablu
                for (int i = 0; i < str1.length(); i = i + frmPostavke.getDuzinaLinije()) {
                    if (i + frmPostavke.getDuzinaLinije() < str1.length()) {
                        tmp = tmp + str1.substring(i, i + frmPostavke.getDuzinaLinije()) + "\n";
                    } else {
                        tmp = tmp + str1.substring(i) + "\n";
                    }
                }

                // privremeno spremanje prvog dijela
                result = tmp;

                tmp = "";
                // razlom u tmp varijablu
                for (int i = 0; i < str2.length(); i = i + frmPostavke.getDuzinaLinije()) {
                    if (i + frmPostavke.getDuzinaLinije() < str2.length()) {
                        tmp = tmp + str2.substring(i, i + frmPostavke.getDuzinaLinije()) + "\n";
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
     * Dohvat objekta <I>txtRacun</I>.
     *
     * @return <I>txtRacun</I>.
     */
    public JTextArea getTxtRacun() {
        return txtRacun;
    }

    /**
     * Postavlja ili osvjezava sadrzaj u txtRacun.<BR> Poziva se za svaku
     * promjenu u Racun objektu, nacinu placanja, popustu, ...<BR>
     */
    public void updateRacun() {

        String text = "";
        String datum = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",
                Locale.ENGLISH).format(new Date());
        DecimalFormat dFormat = new DecimalFormat("0.00");

        String naplata = "?";
        String separator = "";
        String popust = "";
        String pacijent = "";

        String[] napomenaLinije;
        String napomenaTekst = "";

        double osnovicaIznos = 0.00;
        double pdvIznos = 0.00;
        double pnpIznos = 0.00;

        if (nacinPlacanja == 0) {
            naplata = "Novcanice";
        } else if (nacinPlacanja == 1) {
            naplata = "Kartica";
        } else if (nacinPlacanja == 2) {
            naplata = "Transakcija";
        } else if (nacinPlacanja == 3) {
            naplata = "Ostalo";
        }

        for (int i = 0; i < frmPostavke.getDuzinaLinije(); i++) {
            separator = separator + "-";
        }

        /**
         * Kolicinski prikaz artikala na racunu.
         */
        if (frmPostavke.isKolicina()) {
            Hashtable<Proizvod, Integer> kolicina = new Hashtable<Proizvod, Integer>();
            for (Proizvod p : r1.getProizvodi()) {
                kolicina.put(p, r1.getProizvodKolicina(p));
            }
            // Prodji po hash tablici kolicine proizvoda i
            //  napuni text varijablu
            for (Proizvod p : kolicina.keySet()) {
                int kolicinaProizvoda = kolicina.get(p);
                text = text + formatLine(p.getNaziv().trim() + " x" + kolicinaProizvoda,
                        dFormat.format(p.getCijena() * kolicinaProizvoda) + " KN") + "\n";

                // pokupi iznose pdv-a, pnp-a i osnovice za prikaz na racunu
                osnovicaIznos = osnovicaIznos + p.getOsnovica() * kolicinaProizvoda;
                pdvIznos = pdvIznos + p.getIznosPdv() * kolicinaProizvoda;
                pnpIznos = pnpIznos + p.getIznosPnp() * kolicinaProizvoda;

            }           
        } else {
            for (Proizvod p : r1.getProizvodi()) {
                text = text
                        + formatLine(p.getNaziv().trim(),
                        dFormat.format(p.getCijena()) + " KN") + "\n";

                // pokupi iznose pdv-a, pnp-a i osnovice za prikaz na racunu
                osnovicaIznos = osnovicaIznos + p.getOsnovica();
                pdvIznos = pdvIznos + p.getIznosPdv();
                pnpIznos = pnpIznos + p.getIznosPnp();
            }
        }

        // postavi popust liniju za ispis, ako postoji neki popust
        if (this.popust > 0) {
            // faktor korekcije, ako je popust veci od 0                
            double k = (1 - this.popust);

            popust = formatLine("Popust " + dFormat.format(this.popust * 100) + " %:",
                    dFormat.format(r1.getUkupanIznos() * k) + " KN")
                    + "\n\n";

            // primjeni i na osnovicu, pdv i pnp
            osnovicaIznos = osnovicaIznos * k;
            pdvIznos = pdvIznos * k;
            pnpIznos = pnpIznos * k;

        }

        // Pacijenti
        if (this.odabraniPacijent > 0) {
            pacijent = "\n" + formatLine("Pacijent: ", pacijenti.get(odabraniPacijent)) + "\n";
        } else {
            pacijent = "";
        }

        // Uredjaj
        String uredjaj = "";
        if (uredjajDodatak.length() > 0) {
            uredjaj = "\n" + formatLine("Uredjaj:", uredjajDodatak) + "\n";
        }
        // ako nema pnp-a, nema potrebe ispisivati
        String pnpFormatLine = "";
        if (pnpIznos > 0) {
            pnpFormatLine = formatLine("PNP: ", dFormat.format(pnpIznos) + " KN");
        }

        // Obrada napomene
        if (napomena.length() > 0) {
            napomenaLinije = napomena.split("\n");
            napomenaTekst = "\n";
            for (String linija : napomenaLinije) {
                napomenaTekst = napomenaTekst + formatLine(linija, "") + "\n";
            }
        }

        txtRacun.setText(header
                + "\nDatum:" + datum
                + "\n" + r1r2Dodatak
                + "\n" + formatLine("Br.racuna: ",
                brRac + "/" + frmPostavke.getPoslovnica() + "/" + frmPostavke.getBlagajna())
                + "\n" + formatLine("Blagajnik:", blagajnik.get(oibBlagajnika))
                + "\n" + formatLine("Placanje:", naplata)
                + transakcijskiNiz + "\n\n"
                + formatLine(frmPostavke.getOznakaProizvoda(), "IZNOS") + "\n"
                + separator
                + "\n" + text
                + separator + "\n"
                + formatLine("Ukupno: ", r1.getUkupanIznosIspis() + " KN")
                + "\n\n"
                + formatLine("Osnovica: ", dFormat.format(osnovicaIznos) + " KN")
                + "\n"
                + formatLine("PDV: ", dFormat.format(pdvIznos) + " KN")
                + "\n"
                + pnpFormatLine
                + "\n"
                + popust
                + pacijent
                + uredjaj
                + napomenaTekst
                + "Z.kod:\n"
                + formatLine(zkod, "") + "\n"
                + "JIR:\n"
                + formatLine(jir, "") + "\n"
                + footer);

        /**
         * Dodatak u naslovu za oznaku testne ili prod. instance programa
         */
        if (frmPostavke.getKeyFile().endsWith("test.pfx")) {
            setTitle("Blagajna v." + frmOProgramu.ver + " [Test]");
        } else {
            setTitle("Blagajna v." + frmOProgramu.ver + " [Produkcija]");
        }
    }

    /**
     * Napravi inicijalizaciju header i footer varijabli za ispis racuna<BR>
     * Postavlja ih bilo iz datoteka podnozje.txt i zaglavlje.txt ili iz<BR>
     * postavki, koristeci vlasnika, oib, ... itd.<BR>
     */
    public void initHeaderAndFooter() {

        String line;
        int lineNum = 0;

        try {

            File headerFile = new File("zaglavlje.txt");
            if (headerFile.exists()) {
                // prvo ucitaj zaglavlje, max. 10 linija
                BufferedReader br = new BufferedReader(new FileReader(
                        "zaglavlje.txt"));
                this.header = "";
                while ((line = br.readLine()) != null) {
                    lineNum++;
                    this.header = this.header + line + "\n";
                }
                br.close();
            } else {
                // postavi obicno zaglavlje ako nije pronadjena datoteka
                // zaglavlja
                this.header = frmPostavke.getNaziv() + "\nvl. " + frmPostavke.getVlasnik() + "\nOIB: " + frmPostavke.getOib();
            }

            lineNum = 0;

            File footerFile = new File("podnozje.txt");
            if (footerFile.exists()) {
                // prvo ucitaj zaglavlje, max. 10 linija
                BufferedReader br = new BufferedReader(new FileReader(
                        "podnozje.txt"));
                this.footer = "";
                while ((line = br.readLine()) != null) {
                    lineNum++;
                    this.footer = this.footer + line + "\n";
                }
                br.close();
            } else {
                // postavi praznu liniju ako nije pronadjena datoteka podnozja
                this.footer = "\n";
            }

        } catch (Exception e) {
            System.err.println("Parse Error: " + e.getMessage());
            this.header = frmPostavke.getNaziv() + "\nvl. " + frmPostavke.getVlasnik() + "\nOIB: " + frmPostavke.getOib();
            this.footer = "\n";

        }

    }
    
    /**
     * <B>Podjela ekrana</B><BR>
     * Dohvaca razdjelnik ekrana.
     * @return omjer podjele ekrana
     */
    public int getSplitterPane() {
        return pnlSplitter.getDividerLocation();
    }

    /**
     * <B>Podjela ekrana</B><BR>
     * Postavlja razdjelnik ekrana.
     * @param omjer podjela ekrana
     */
    public void setSplitterPane(final int omjer) {

        Thread t_setSplitterPane = new Thread(new Runnable()
        {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    logger.log(Level.SEVERE, "setSplitterPane()", ex);
                }
                pnlSplitter.setDividerLocation(omjer);
            }
        }, "setSplitterPane");     
        t_setSplitterPane.start();
             
    }
    
    /**
     * <B>Sirina stupca</B><BR>
     * Dohvaca sirinu stupca, prema imenu.<BR>
     * Ako stupac ne postoji, vraca 0.
     * @param columnIndex redni br. stupca
     * @return sirina stupca
     */
    public int getTableColumnWidth(int columnIndex) {
        int retVal = 0;
        Enumeration<TableColumn> enumColumn = tblProizvod.getColumnModel().getColumns();
        while (enumColumn.hasMoreElements()) {
            TableColumn stupac = enumColumn.nextElement();
            if (stupac.getModelIndex() == columnIndex) {                    
                retVal = stupac.getPreferredWidth();
            }            
        }
        return retVal;
    }

    /**
     * <B>Sirina stupca</B><BR>
     * Postavlja sirinu stupca, prema imenu.<BR>
     * @param columnIndex redni br.stupca
     * @param width sirina stupca
     */
    public void setTableColumnWidth(int columnIndex, int width) {
        int retVal = 0;
        Enumeration<TableColumn> enumColumn = tblProizvod.getColumnModel().getColumns();
        while (enumColumn.hasMoreElements()) {
            TableColumn stupac = enumColumn.nextElement();
            if (stupac.getModelIndex() == columnIndex) {                    
                stupac.setPreferredWidth(width);
            }            
        }
    }
    
    /**
     * Zaglavlje racuna.
     * @return tekst zaglavlja racuna
     */
    public String getHeader() {
        return header;
    }

    /**
     * Podnozje racuna.
     * @return tekst podnozja racuna
     */
    public String getFooter() {
        return footer;
    }
    
    /**
     * Postavlja blagajnika prema oib-u. Oib mora biti jedan od onih u listi
     * blagajnika.<BR>
     *
     * @param oib iz liste blagajnika
     * @return <I>true</I> ako je ispravan oib, inace <I>false</I>
     */
    public boolean setBlagajnik(String oib) {

        // nadji oznaku blagajnika prema oib-u
        if (blagajnik.containsKey(oib)) {
            oibBlagajnika = oib;
            return true;
        }

        return false;

    }

    /**
     * Ucitava blagajnike i oib-e
     *
     * @param fileName naziv datoteke u kojoj su pohranjeni
     */
    private void loadBlagajnik(String fileName) {

        /**
         * Max. 2 parametra u blagajnik.txt (oznaka;oib).<BR>
         * <B>Npr.</B><I>Maja;12345678900</I>
         */
        String[] arr = new String[2];

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            StringTokenizer token;
            int lineNum = 0, tokenNum = 0;

            while ((line = br.readLine()) != null) {

                // barem 5 znakova duljine treba biti da se obradi
                if (line.length() > 5) {
                    // preskoci komentare koji pocinju sa znakom #
                    //  i ako znak ; nije pronadjen negdje u sredini linije
                    if (line.charAt(0) != '#' && line.indexOf(';') > 1) {

                        // Oznaka
                        arr[0] = line.substring(0, line.indexOf(';'));
                        // OIB
                        arr[1] = line.substring(line.indexOf(';') + 1);

                        blagajnik.put(arr[1], arr[0]);

                    }
                }

                // uvecaj oznaku linije
                lineNum++;
            }
            br.close();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "loadBlagajnik()", e);
        }


    }

    /**
     * Sprema racun u text datoteku
     *
     * @param fileName naziv datoteke
     */
    private void saveRacun(String fileName) {
        PrintWriter out = null;
        String[] linije = txtRacun.getText().split("\n");
        try {
            out = new PrintWriter(fileName);
            for (String linija : linije) {            
                out.println(linija);
            }
            out.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "saveRacun()", ex);
        } finally {
            out.close();
        }

    }

    /**
     * Update the row filter regular expression from the expression in the text
     * box.
     */
    private void newFilter() {
        RowFilter<RacunJTableDataModel, Object> rf;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter("^" + txtSearch.getText(), 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }

    private void initSearch() {
        // inicijaliziraj sorter
//        sorter = new TableRowSorter<CustomJTableDataModel>(tableModel);
//        tblProizvod.setRowSorter(sorter);
//    Whenever filterText changes, invoke newFilter.
        txtSearch.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        newFilter();
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        newFilter();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        newFilter();
                    }
                });
    }

    /**
     * Korisnicko sucelje postavlja.<BR>
     */
    public void ui() {

        // Postavi referencu na Izgled objekt u frmPostavke objektu (Postavke prozor)
        izgled = frmPostavke.izgled;

        Runnable doUI = new Runnable() {
            @Override
            public void run() {

                // Panel sa dugmicima
                for (Component comp : pnlIzbornik.getComponents()) {
                    if (comp instanceof JButton) {
                        //comp.setMinimumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                        //comp.setMaximumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                        comp.setPreferredSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));

                        comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
                    }
                }
                // Panel sa proizvodima
                for (Component comp : pnlProizvod.getComponents()) {
                    if (comp instanceof JLabel) {
                        //comp.setMinimumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                        //comp.setMaximumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                        comp.setPreferredSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));

                        comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
                    }
                }

                // Za jTable objekte, pridjeli custom renderer za tablicu
                tblProizvod.setDefaultRenderer(
                        Object.class,
                        /**
                         * Vlastiti renderer za prikaz celija u tablici.<BR>
                         * Treba prvo napraviti instancu objekta
                         * <I>izgled</I>.<BR>
                         */
                        new DefaultTableCellRenderer() {
                            // override renderer preparation
                            @Override
                            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                    boolean hasFocus,
                                    int row, int column) {
                                // allow default preparation
                                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                                // Postavi font cijeloj resetki
                                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));

                                // Oboji redak koji je oznacen (ima fokus)
                                //if (isRowSelected(table, row)) {
                                if (isSelected) {
                                    comp.setBackground(boja2);
                                } else {
                                    comp.setBackground(boja1);
                                }
                                return comp;

                            }
                        });
                tblProizvod.repaint();
                
                /*
                 * Postavi omjer podjele ekrana.
                 */
                int omjer = frmPostavke.getOmjer();
                if (omjer > 0) {
                    setSplitterPane(omjer);
                }

                /*
                 * Postavi sirine stupaca.
                 */
                for (Integer stupac : new Integer[]{0, 1, 2, 3}) {
                    int width = frmPostavke.getStupac().get(stupac);
                    if (width > 0) {
                        setTableColumnWidth(stupac, width);
                    }
                }
                

                // Racun, od v.1.09 se pokusava prikaz prilagoditi ekranu a ispis prema postavkama za ispis
                txtRacun.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));

                // Pokusaj pozvati ui() za ostale prozore
                if (frmCjenik != null) {
                    frmCjenik.ui();
                }
                if (frmOProgramu != null) {
                    frmOProgramu.ui();
                }
                if (frmPP != null) {
                    frmPP.ui();
                }
                if (frmPromet != null) {
                    frmPromet.ui();
                }
        

            }
        };
        
        SwingUtilities.invokeLater(doUI);

    }

    /**
     * Zamrzava ili odmrzava glavni prozor.<BR>
     *
     * @param state ako je <I>true</I> onda odmrzava, inace <I>false</I>
     * zamrzava
     */
    public void toggleScreen(boolean state) {
//
//        for (Component comp : this.getComponents()) {
//            comp.setEnabled(state);
//        }
//        pnlProizvod.setEnabled(state);
//        for (Component comp : pnlProizvod.getComponents()) {
//            comp.setEnabled(state);
//        }
//        tblProizvod.setEnabled(state);
//        for (Component comp : tblProizvod.getComponents()) {
//            comp.setEnabled(state);
//        }
//        if (!state) {
//            tableModel.deleteProizvodList();
//        } else {
//            initCjenik();
//        }
//
//        pnlRacun.setEnabled(state);
//        for (Component comp : pnlRacun.getComponents()) {
//            comp.setEnabled(state);
//        }
//        txtRacun.setEnabled(state);
//        pnlIzbornik.setEnabled(state);
//        for (Component comp : pnlIzbornik.getComponents()) {
//            comp.setEnabled(state);
//        }
//
//        tblProizvod.requestFocus();

        this.setVisible(state);

    }

    /**
     * Stvara dialog prozor za izbor blagajnika.<BR>Treba koristiti ako je
     * ucitano vise blagajnika iz datoteke sa blagajnicima.<BR>
     *
     * @param izKonstruktora Zastavica koja pokazuje da li je metoda pozvana iz
     * konstruktora tako da na <I>Cancel</I> program zavrsi ako bblagajnik ne
     * zna OIB.
     */
    private void createOibConfirmDialog(boolean izKonstruktora) {

        String poruka = "Molim, unesite OIB blagajnika";
        String inputOib;

        JPasswordField pwd = new JPasswordField(11);
        char[] password;

        while (true) {
            int action = JOptionPane.showConfirmDialog(null, pwd, poruka, JOptionPane.OK_CANCEL_OPTION);
            if (action == JOptionPane.CANCEL_OPTION) {
                // Provjera ako je u pocetku programa korisnik odabrai Cancel dugme
                if (izKonstruktora) {
                    // .. tada zavrsi sa programom (blagajnik vjerojatno ne zna oib)
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(null, "Molim, unesite OIB i pritisnite OK!");
                }
            } else if (action == JOptionPane.OK_OPTION) {
                password = pwd.getPassword();
                inputOib = String.valueOf(password);
                boolean result = setBlagajnik(String.valueOf(inputOib));
                if (result) {
                    // Ovdje treba biti oprezan, ako je iz konstruktora poziv
                    //  tada se ovo ne treba izvrsiti
                    if (!izKonstruktora) {
                        toggleScreen(true);
                        updateRacun();
                    }
                    break;
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Unjeli ste krivi OIB", "Greska", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Provjerava internet vezu periodicno.<BR>
     */
    private void provjeriInternetVezu() {
        
        internetVezaThread = new Thread(new Runnable() {
            @Override
            public void run() {

                boolean arhivirano = false;
                
                while (true) {
                    // ako je manje od 2 min. tada povecaj za 1
                    if (vremenskiInterval < 120) {
                        vremenskiInterval++;
                    }

                    // XML echo objekt
                    XMLEcho echo = new XMLEcho();

                    // uzorak za slanje
                    String uzorak = "123456789abcdefg";

                    // postavi uzorak
                    echo.setTestniUzorak(uzorak);

                    // poalji uzorak
                    echo.sendEcho();

                    // provjeri odgovor
                    if (echo.getTestniUzorak().equalsIgnoreCase(uzorak)) {

                        lblStatus.setText("Internet veza: OK");
                        lblStatus.setForeground(Color.BLUE);

                        // da li je vec arhivirano i treba li uopce arhivirati ?
                        if (!arhivirano && frmPostavke.isArhiviraj()) {
                            // Pokreni arhiviranje za 1 min.
                            frmOProgramu.pokreniArhiviranje(60);
                            arhivirano = true;
                        }

                    } else {

                        lblStatus.setText("Internet veza: GRESKA");
                        lblStatus.setForeground(Color.RED);

                        vremenskiInterval = 120;
                    }

                    try {
                        Thread.sleep(vremenskiInterval * 1000);
                    } catch (InterruptedException ex) {
                        logger.log(Level.SEVERE, "provjeriInternetVezu()", ex);
                    }
                }
            }
        });
        internetVezaThread.start();
    }

    /**
     * Vraca trenutni br.racuna (sljedeci slobodni).<BR>
     *
     * @return broj racuna
     */
    public int getBrRac() {
        return brRac;
    }

    /**
     * Referenca na FrmPostavke klasu objekata.<BR>
     *
     * @return referenca na <I>frmPostavke</I> objekt
     */
    public FrmPostavke getFrmPostavke() {
        return frmPostavke;
    }

    /**
     * <B>Racun</B><BR>
     * Za dohvati <I>Racun</I> objekta.<BR>
     * Stavke se mogu dobiti sa:<BR>
     * <UL>
     * for (Proizvod p : r1.getProizvodi()) { <BR>
     * ...<BR>
     * }<BR>
     * </UL>
     *
     * @return trenutno prikazan racun na ekranu.
     */
    public Racun getRacun() {
        return r1;
    }

    /**
     * Referenca na <I>logger</I> objekt.<BR> <I>logger</I> zapisuje logove u
     * datoteku, <I>blagajna.log</I>.<BR>
     *
     * @return referenca na <I>logger</I> objekt za logiranje poruka u datoteku.
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Postavlja referencu na frmPostavke objekat.<BR>
     *
     * @param frmPostavke instanca FrmPostavke objekta
     */
    public void setFrmPostavke(FrmPostavke frmPostavke) {
        this.frmPostavke = frmPostavke;
    }

    /**
     * Tablicni model za prikaz i koristenje u tlbProizvod resetki.<BR>
     *
     * @return <I>RacunJTableDataModel</I> tip podatka
     */
    public RacunJTableDataModel getTableModel() {
        return tableModel;
    }

    /**
     * Tablicni model za prikaz i koristenje u tlbProizvod resetki.<BR>
     *
     * @param tableModel <I>RacunJTableDataModel</I> tip podatka. Povezuje
     * <I>tableModel</I> globalni objekt.
     */
    public void setTableModel(RacunJTableDataModel tableModel) {
        this.tableModel = tableModel;
    }

    private void setUpHotKeys() {

        setHotKeysOnComponent(pnlProizvod);
        setHotKeysOnComponent(pnlIzbornik);
        setHotKeysOnComponent(pnlRacun);


    }

    private void setHotKeysOnComponent(JComponent component) {
        final String F2_MAP_VALUE = "Ispis";
        final String F3_MAP_VALUE = "Novi racun";
        final String F4_MAP_VALUE = "Poslovni prostor";
        final String F5_MAP_VALUE = "Postavke";
        final String F6_MAP_VALUE = "Cjenik/proizvodi";
        final String F7_MAP_VALUE = "Nacin placanja";
        final String F8_MAP_VALUE = "Promet";
        final String F9_MAP_VALUE = "Popust";
        final String F1_MAP_VALUE = "O programu";
        final String F10_MAP_VALUE = "Izlaz";

        // F2 - Ispis
        component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), F2_MAP_VALUE);
        component.getActionMap().put(F2_MAP_VALUE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmdIspisActionPerformed(e);
            }
        });

        // F3 - Novi racun
        component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), F3_MAP_VALUE);
        component.getActionMap().put(F3_MAP_VALUE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmdNoviActionPerformed(e);
            }
        });

        // F4 - Poslovni prostor
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), F4_MAP_VALUE);
        component.getActionMap().put(F4_MAP_VALUE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmdPoslovniProstorActionPerformed(e);
            }
        });

        // F5 - Postavke
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), F5_MAP_VALUE);
        component.getActionMap().put(F5_MAP_VALUE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmdPostavkeActionPerformed(e);
            }
        });

        // F6 - Cjenik/Proizvodi
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), F6_MAP_VALUE);
        component.getActionMap().put(F6_MAP_VALUE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmdCjenikActionPerformed(e);
            }
        });

        // F7 - Nacin placanja
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), F7_MAP_VALUE);
        component.getActionMap().put(F7_MAP_VALUE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmdNacinPlacanjaActionPerformed(e);
            }
        });

        // F8 - Promet
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), F8_MAP_VALUE);
        component.getActionMap().put(F8_MAP_VALUE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmdPrometActionPerformed(e);
            }
        });

        // F9 - Popust
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), F9_MAP_VALUE);
        component.getActionMap().put(F9_MAP_VALUE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmdPopustActionPerformed(e);
            }
        });


        // F1 - O programu
        component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), F1_MAP_VALUE);
        component.getActionMap().put(F1_MAP_VALUE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmdOProgramuActionPerformed(e);
            }
        });

        // F10 - Izlaz

        component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), F10_MAP_VALUE);
        component.getActionMap().put(F10_MAP_VALUE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmdIzlazActionPerformed(e);
            }
        });
    }
}
