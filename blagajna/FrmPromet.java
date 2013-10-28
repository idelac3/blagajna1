package blagajna;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTable.PrintMode;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author eigorde
 */
public class FrmPromet extends javax.swing.JFrame {

    /**
     * Logiranje gresaka
     */
    private Logger logger;
    /**
     * tablica proizvoda/usluga -> model podataka ispod same tablice
     */
    private PrometJTableDataModel tableModel = new PrometJTableDataModel();
    /**
     * odabrani datum (polje txtDatum)
     */
    private Date odabraniDatum;
    /**
     * Mjesecni prikaz promtea?<BR> Ako je <I>flase</I> tada je dnevni prikaz
     */
    private boolean mjesecniPrikaz;
    /**
     * Dretva za ucitavanje podataka o prometu
     */
    private Thread loadThread;
    /**
     * varijabla za cuvanje fokusa kod promjene datuma iz polja, ili pomocu
     * dugmica se fokus izgubi i ne vrati, pa ova varijabla pomaze da se fokus
     * opet vrati tamo gdje je i bio -1 - fokus ne treba postaviti 0 - fokus
     * treba postaviti na txtDatum 1 - fokus treba postaviti na
     * cmdDatumPrethodni 2 - fokus treba postaviti na cmdDatumSljedeci
     */
    private int fokus = -1;
    /**
     * referenca na prozor FrmBlagajna
     */
    private FrmBlagajna blagajna;
    /**
     * referenca na prozor FrmPostavke
     */
    private FrmPostavke postavke;
    /**
     * Popis racuna be JIRa
     */
    private List<String> bezJIRa = new ArrayList<String>();
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
     * Prozor FrmPromet
     *
     * @param postavke referenca na FrmPostavke prozor
     * @param blagajna referenca na FrmBlagajna prozor
     *
     */
    public FrmPromet(FrmPostavke postavke, FrmBlagajna blagajna) {

        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                setExtendedState(MAXIMIZED_BOTH);
            }
        });

        try {

            // logiranje problema na syslog/datoteku
            logger = blagajna.getLogger();


            initComponents();

            // postavi pocetne boje
            boja1 = cmdIspis.getBackground();
            boja2 = Color.ORANGE;

            // sakrij prozor pri zatvaranju prozora
            this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            // postavi model podataka
            tblPromet.setModel(tableModel);
            // postavi sirine stupaca
            tblPromet.getColumnModel().getColumn(0).setPreferredWidth(20);
            tblPromet.getColumnModel().getColumn(1).setPreferredWidth(40);
            tblPromet.getColumnModel().getColumn(2).setPreferredWidth(20);
            tblPromet.getColumnModel().getColumn(3).setPreferredWidth(50);
            tblPromet.getColumnModel().getColumn(4).setPreferredWidth(90);
            tblPromet.getColumnModel().getColumn(5).setPreferredWidth(90);

            // pridjeli postavke iz konstruktora, 
            //  globalnim postavkama za ovaj prozor
            this.postavke = postavke;
            this.blagajna = blagajna;

            // korisnicko sucelje
            ui();

            // postavi datum na trenutnu vr., danasnji dan
            odabraniDatum = new Date();

            // azuriraj txtDatum
            txtDatum.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(odabraniDatum));

            // ucitaj promet za danasnji dan
            loadPromet();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "FrmPromet()", e);
        }

    }

    /**
     * Vraca trenutno odabrani datum pregleda prometa
     *
     * @return Date tip podatka
     */
    public Date getOdabraniDatum() {
        return odabraniDatum;
    }

    /**
     * Da li je aktiviran mjesecni prikaz ?<BR>
     *
     * @return <I>true</I> ako je, inace <I>false</I>.
     */
    public boolean getMjesecniPrikaz() {
        return mjesecniPrikaz;
    }

    /**
     * Dodaje novu stavku Promet tipa na kraj resetke za odabrani datum.<BR>
     *
     * @param brRac broj racuna
     * @param datVrijeme datum i vrijeme izdavanja racuna
     * @param iznosUkupno ukupni iznos
     * @param pdv pdv
     * @param pnp pnp
     * @param storno storniran (true ako je racun storniran)
     * @param oibOper oib blagajnika
     * @param zkod zastitni kod
     * @param jir jedinstveni identifikator racuna
     */
    public void addPromet(int brRac, Date datVrijeme, double iznosUkupno, double pdv, double pnp, boolean storno, String oibOper, String zkod, String jir) {

        // da glavni prozor FrmBlagajna moze dodati izdani racun
        //  bez da se sa loadPromet sve ponovno ucitava
        tableModel.addPromet(
                new Promet(brRac, datVrijeme, iznosUkupno,
                pdv, pnp, storno, oibOper, zkod, jir));

    }

    /**
     * Ucitava promet u resetku za odabrani datum.<BR>
     */
    private void loadPromet() {

        loadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // brojac
                    int i = 0;

                    // Uzmi vrijeme pocetka loadPromet() funkcije
                    long lStartTime = new Date().getTime();

                    // za ucitavanje racuna iz xml-a
                    XMLRacun x1;

                    // popis xml racuna
                    List<String> xmlFile = new ArrayList<String>();

                    // po def. svi racuni bi trebali biti u Racuni folderu
                    File dirRacun = new File("Racuni");

                    // formiraj tekstualni oblik odabranog datuma
                    String datum = new SimpleDateFormat(
                            "dd.MM.yyyy", Locale.ENGLISH).format(odabraniDatum);

                    // postavi progres bar na pocetak
                    progresBar.setMinimum(0);
                    progresBar.setMaximum(postavke.getBrRac());

                    // onemoguci dugmice i tekst polje za promjenu datuma
                    txtDatum.setEnabled(false);
                    cmdDatumPrethodni.setEnabled(false);
                    cmdDatumSljedeci.setEnabled(false);

                    // sada provjeri da li postoji folder Racun gdje se trebaju spremati racuni
                    if (dirRacun.exists()) {

                        // prodji po listi racuna
                        for (File txtFile : dirRacun.listFiles()) {

                            // uvecaj brojac i progres bar
                            i++;
                            progresBar.setValue(i);

                            // ime datoteke racuna (xml, txt, signed.xml, ...)
                            String filename = txtFile.getName();
                            String fileDate = new SimpleDateFormat(
                                    "dd.MM.yyyy", Locale.ENGLISH).format(txtFile.lastModified());

                            // provjeri da li datoteka/racun sa trazenim datumom
                            // ovo je najjednostavniji nacin da se samo po datumu (bez vremena)
                            //  usporedjuje da li datoteka ima trazeni datum
                            if (fileDate.equalsIgnoreCase(datum)) {
                                // provjera koji je nastavak datoteke
                                if (filename.endsWith((".txt"))) {
                                    // ne treba sadrzaj tih datoteka za popunjavanje tablice
                                    // koristit se za ispis, npr. cmdIspis()
                                } else if (filename.endsWith(".signed.xml")) {
                                    // ne treba sadrzaj tih datoteka za popunjavanje tablice
                                } else if (filename.endsWith(".response.xml")) {
                                    // ne treba za sada sadrzaj tih datoteka 
                                    // koriste se za ucitavanje JIRa
                                } else if (filename.endsWith("storno.xml")) {
                                    // stornirani racuni, ne treba ih dodati na popis                                   
                                } else if (filename.endsWith("storno.response.xml")) {
                                    // odgovor(i) na storniranje racuna, za JIR                                    
                                } else if (filename.endsWith("error.xml")) {
                                    // Greske kod odziva iz PU
                                } else if (filename.endsWith(".xml")) {
                                    // racuni u xml formatu
                                    xmlFile.add("Racuni/" + filename);
                                }
                            }

                        }

                        logger.info("Ukupno datoteka u Racuni folderu: " + i
                                + "\nBroj racuna (.xml) za " + datum + ": " + xmlFile.size());

                    }

                    // postavi progres bar na pocetak opet
                    progresBar.setMinimum(0);
                    progresBar.setMaximum(postavke.getBrRac());

                    // ocisti tablicu proizvoda
                    tableModel.deletePrometList();

                    // napuni tablicu sa proizvodima
                    for (i = 0; i < xmlFile.size(); i++) {

                        // formiraj naziv datoteke odgovora
                        //  nesto poput: Racuni/racun123.response.xml
                        File xmlFileResponse = new File(xmlFile.get(i).substring(
                                0, xmlFile.get(i).length() - 3) + "response.xml");

                        File xmlFileStornoResponse = new File(xmlFile.get(i).substring(
                                0, xmlFile.get(i).length() - 3) + "storno.response.xml");

                        x1 = new XMLRacun();
                        x1.loadRacun(xmlFile.get(i));

                        String jir = "-";
                        boolean storniran = false;

                        // postoji li odgovor na racun i barem ima 100 znakova ?
                        if (xmlFileResponse.exists() && xmlFileResponse.length() > 100) {

                            // ucitaj xml odgovor
                            x1.loadRacunOdgovor(xmlFileResponse.getAbsolutePath());
                            jir = x1.getJir();
                        }

                        // postoji li storno odgovor?
                        if (xmlFileStornoResponse.exists()) {
                            storniran = true;
                        }

                        tableModel.addPromet(
                                new Promet(x1.getBrRac(), x1.getDatVrijeme(), x1.getIznosUkupno(),
                                x1.getUkupnoPdv(), x1.getUkupnoPnp(), storniran,
                                x1.getOibOper(), x1.getZastitniKod(), jir));

                        // postavi progres
                        progresBar.setValue(i);
                    }

                    // do kraja postavi
                    progresBar.setValue(progresBar.getMaximum());

                    // omoguci dugmice i tekst polje za promjenu datuma
                    txtDatum.setEnabled(true);
                    cmdDatumPrethodni.setEnabled(true);
                    cmdDatumSljedeci.setEnabled(true);

                    // postavi fokus natrag
                    if (fokus == 0) {
                        txtDatum.requestFocus();
                    } else if (fokus == 1) {
                        cmdDatumPrethodni.requestFocus();
                    } else if (fokus == 2) {
                        cmdDatumSljedeci.requestFocus();
                    }

                    // kraj loadPromet() funkcije		
                    long lEndTime = new Date().getTime();

                    // izracunaj razliku		
                    long difference = lEndTime - lStartTime;

                    // prikazi u sekundama		
                    DecimalFormat dFormat = new DecimalFormat("0.00");

                    // Zabiljezi odziv
                    logger.info("loadPromet() odziv: " + dFormat.format(difference) + " msec.");

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "loadThread()", e);

                    // omoguci dugmice i tekst polje za promjenu datuma
                    txtDatum.setEnabled(true);
                    cmdDatumPrethodni.setEnabled(true);
                    cmdDatumSljedeci.setEnabled(true);

                    // postavi fokus natrag
                    if (fokus == 0) {
                        txtDatum.requestFocus();
                    } else if (fokus == 1) {
                        cmdDatumPrethodni.requestFocus();
                    } else if (fokus == 2) {
                        cmdDatumSljedeci.requestFocus();
                    }

                    // do kraja postavi
                    progresBar.setValue(progresBar.getMinimum());

                }
            }
        });
        loadThread.start();

        // Dnevni prikaz, zastavica
        mjesecniPrikaz = false;

    }

    /**
     * Korisnicko sucelje postavlja.<BR>
     */
    public void ui() {
        // UI
        
        // referenca na izgled objekt            
        izgled = blagajna.izgled;
            
        // Panel sa dugmicima
        for (Component comp : pnlIzbornik.getComponents()) {
            if (comp instanceof JButton || comp instanceof JTextField) {
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
                //comp.setMinimumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                //comp.setMaximumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                comp.setPreferredSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));                
            }
        }

        // Za jTable objekte, pridjeli custom renderer za tablicu
        tblPromet.setDefaultRenderer(
                Object.class,
                /**
                 * Vlastiti renderer za prikaz celija u tablici.<BR> Treba prvo
                 * napraviti instancu objekta <I>izgled</I>.<BR>
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
        tblPromet.repaint();

    }

    /**
     * funkcija za selektiranje txt polja
     */
    private void selText(JTextField txtField) {

        txtField.setSelectionStart(0);
        txtField.setSelectionEnd(txtField.getText().length());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPromet = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPromet = new javax.swing.JTable();
        progresBar = new javax.swing.JProgressBar();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlIzbornik = new javax.swing.JPanel();
        cmdStorniraj = new javax.swing.JButton();
        cmdIspis = new javax.swing.JButton();
        cmdDatumPrethodni = new javax.swing.JButton();
        txtDatum = new javax.swing.JTextField();
        cmdDatumSljedeci = new javax.swing.JButton();
        cmdSpremiCsv = new javax.swing.JButton();
        cmdUkupno = new javax.swing.JButton();
        cmdPosaljiBezJIRa = new javax.swing.JButton();
        cmdIzlaz = new javax.swing.JButton();
        cmdMjesecno = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Promet");

        tblPromet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Racun", "Datum", "Iznos", "OIB Blagajnika", "Zastitni kod", "JIR"
            }
        ));
        tblPromet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblPrometKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblPromet);

        javax.swing.GroupLayout pnlPrometLayout = new javax.swing.GroupLayout(pnlPromet);
        pnlPromet.setLayout(pnlPrometLayout);
        pnlPrometLayout.setHorizontalGroup(
            pnlPrometLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1187, Short.MAX_VALUE)
            .addComponent(progresBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlPrometLayout.setVerticalGroup(
            pnlPrometLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrometLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progresBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        cmdStorniraj.setText("Storniraj");
        cmdStorniraj.setToolTipText("Storniraj racun prema broju");
        cmdStorniraj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdStornirajActionPerformed(evt);
            }
        });
        cmdStorniraj.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdStornirajFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdStornirajFocusLost(evt);
            }
        });

        cmdIspis.setText("Ispis");
        cmdIspis.setToolTipText("Ispis racuna prema broju");
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

        cmdDatumPrethodni.setText("<<");
        cmdDatumPrethodni.setToolTipText("Prethodni dan");
        cmdDatumPrethodni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDatumPrethodniActionPerformed(evt);
            }
        });
        cmdDatumPrethodni.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdDatumPrethodniFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdDatumPrethodniFocusLost(evt);
            }
        });

        txtDatum.setText("jTextField1");
        txtDatum.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDatumFocusGained(evt);
            }
        });
        txtDatum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDatumKeyPressed(evt);
            }
        });

        cmdDatumSljedeci.setText(">>");
        cmdDatumSljedeci.setToolTipText("Sljedeci dan");
        cmdDatumSljedeci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDatumSljedeciActionPerformed(evt);
            }
        });
        cmdDatumSljedeci.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdDatumSljedeciFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdDatumSljedeciFocusLost(evt);
            }
        });

        cmdSpremiCsv.setText("Spremi");
        cmdSpremiCsv.setToolTipText("Spremi promet u CSV format");
        cmdSpremiCsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSpremiCsvActionPerformed(evt);
            }
        });
        cmdSpremiCsv.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdSpremiCsvFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdSpremiCsvFocusLost(evt);
            }
        });

        cmdUkupno.setText("Ukupno");
        cmdUkupno.setToolTipText("Statistika za promet");
        cmdUkupno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdUkupnoActionPerformed(evt);
            }
        });
        cmdUkupno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdUkupnoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdUkupnoFocusLost(evt);
            }
        });

        cmdPosaljiBezJIRa.setText("Posalji bez JIRa");
        cmdPosaljiBezJIRa.setToolTipText("Salje sve racune bez JIRa koji nisu uspjesno poslani");
        cmdPosaljiBezJIRa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPosaljiBezJIRaActionPerformed(evt);
            }
        });
        cmdPosaljiBezJIRa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdPosaljiBezJIRaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdPosaljiBezJIRaFocusLost(evt);
            }
        });

        cmdIzlaz.setText("Izlaz");
        cmdIzlaz.setToolTipText("Zatvori prozor");
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

        cmdMjesecno.setText("Mjesecno");
        cmdMjesecno.setToolTipText("Mjesecni promet");
        cmdMjesecno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdMjesecnoActionPerformed(evt);
            }
        });
        cmdMjesecno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdMjesecnoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdMjesecnoFocusLost(evt);
            }
        });

        javax.swing.GroupLayout pnlIzbornikLayout = new javax.swing.GroupLayout(pnlIzbornik);
        pnlIzbornik.setLayout(pnlIzbornikLayout);
        pnlIzbornikLayout.setHorizontalGroup(
            pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIzbornikLayout.createSequentialGroup()
                .addComponent(cmdIspis)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdStorniraj)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdDatumPrethodni)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdDatumSljedeci)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdMjesecno, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdSpremiCsv)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdUkupno)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdPosaljiBezJIRa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(cmdIzlaz))
        );

        pnlIzbornikLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdDatumPrethodni, cmdDatumSljedeci, cmdIspis, cmdIzlaz, cmdMjesecno, cmdPosaljiBezJIRa, cmdSpremiCsv, cmdStorniraj, cmdUkupno, txtDatum});

        pnlIzbornikLayout.setVerticalGroup(
            pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cmdIspis, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cmdStorniraj, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cmdDatumPrethodni, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cmdPosaljiBezJIRa, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cmdIzlaz, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(txtDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cmdDatumSljedeci, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cmdMjesecno, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cmdSpremiCsv, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cmdUkupno, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pnlIzbornikLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdDatumPrethodni, cmdDatumSljedeci, cmdIspis, cmdIzlaz, cmdMjesecno, cmdPosaljiBezJIRa, cmdSpremiCsv, cmdStorniraj, cmdUkupno, txtDatum});

        jScrollPane2.setViewportView(pnlIzbornik);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPromet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPromet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(85, 85, 85))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(389, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdIzlazActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdIzlazActionPerformed

        this.setVisible(false);

    }//GEN-LAST:event_cmdIzlazActionPerformed

    private void cmdDatumPrethodniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDatumPrethodniActionPerformed
        //  prvo postavi kalendar
        Calendar cal = Calendar.getInstance();
        // i datum u kalendaru
        cal.setTime(odabraniDatum);
        // minus smanjuje br.dana
        cal.add(Calendar.DATE, -1);
        // vrati nazad iz.vr.
        odabraniDatum = cal.getTime();
        // azuriraj prikaz datuma
        txtDatum.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(odabraniDatum));
        // sacuvaj fokus
        fokus = 1;
        // ucitaj promet
        loadPromet();
    }//GEN-LAST:event_cmdDatumPrethodniActionPerformed

    private void cmdDatumSljedeciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDatumSljedeciActionPerformed
        //  prvo postavi kalendar
        Calendar cal = Calendar.getInstance();
        // i datum u kalendaru
        cal.setTime(odabraniDatum);
        // plus dodaje br.dana
        cal.add(Calendar.DATE, 1);
        // vrati nazad iz.vr.
        odabraniDatum = cal.getTime();
        // azuriraj prikaz datuma
        txtDatum.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(odabraniDatum));
        // sacuvaj fokus
        fokus = 2;
        // ucitaj promet
        loadPromet();
    }//GEN-LAST:event_cmdDatumSljedeciActionPerformed

    private void txtDatumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDatumKeyPressed
        // postavi format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);

        // korisnik je Enter-om potvrdio zeljeni datum
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                // pokusaj parsati u Date tip varijable
                odabraniDatum = dateFormat.parse(txtDatum.getText());
                // vrati natrag ono sto je zavrsilo u varijabli
                txtDatum.setText(dateFormat.format(odabraniDatum));
                // ako uspije, obojaj u crno
                txtDatum.setForeground(Color.BLACK);
                // sacuvaj fokus
                fokus = 0;
                // ucitaj promet iz odabranog datuma
                loadPromet();
            } catch (ParseException e) {
                // ako ne uspije, obojaj u crveno
                txtDatum.setForeground(Color.RED);
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            // vrati u txtDatum polje sadrzaj iz var. odabraniDatum
            // znaci korisnik se predomislio i zeli natrag promjenu
            txtDatum.setText(dateFormat.format(odabraniDatum));
            // ako uspije, obojaj u crno
            txtDatum.setForeground(Color.BLACK);
        }

    }//GEN-LAST:event_txtDatumKeyPressed

    private void txtDatumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDatumFocusGained
        selText(txtDatum);
    }//GEN-LAST:event_txtDatumFocusGained

    private void cmdIspisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdIspisActionPerformed
        String brojRacuna = JOptionPane.showInputDialog(this, "Broj racuna:",
                "Ispis racuna", JOptionPane.INFORMATION_MESSAGE);
        if (brojRacuna == null) {
            // korisnik se predomislio
            // :D
        } else {
            // File obj. za txt racun
            File fileRacun = new File("Racuni/racun" + brojRacuna + ".txt");
            if (fileRacun.exists()) {
                // ako postoji, ucitaj sadrzaj
                try {
                    BufferedReader br = new BufferedReader(new FileReader(fileRacun));
                    String line;
                    String racunSadrzaj = "";
                    while ((line = br.readLine()) != null) {
                        racunSadrzaj = racunSadrzaj + line + "\n";
                    }
                    br.close();
                    // ispis
                    JTextArea txtRacun = new JTextArea(racunSadrzaj);
                    txtRacun.setFont(new Font(
                            postavke.getFontNaziv(), Font.PLAIN, postavke.getFontVelicina()));

                    Ispis ispis = new Ispis("racun" + brojRacuna, postavke.getPisac(),
                            postavke.getPapirX(), postavke.getPapirY(),
                            postavke.getMarginaX(),
                            postavke.getMarginaY(),
                            postavke.getIspisnaPovrsinaX(),
                            postavke.getIspisnaPovrsinaY());
                    try {
                        /**
                         * Ispis.
                         */
                        ispis.print(txtRacun.getPrintable(null, null));
                    } catch (PrinterException ex) {
                        logger.log(Level.SEVERE, "cmdIspisiUkupno()", ex);
                    }

                    logger.info("cmdIspis(), sadrzaj:\n" + racunSadrzaj);

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "cmdIspis()", e);
                }

            } else {
                // info korisniku ako datoteka ne postoji
                JOptionPane.showMessageDialog(this,
                        "Datoteka:\n" + fileRacun.getAbsoluteFile().toString() + "\nne postoji!",
                        "Ispis racuna", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_cmdIspisActionPerformed

    private void tblPrometKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPrometKeyPressed
        // ako korisnik pritisne TAB, skoci na Ispis dugme
        if (evt.getKeyChar() == KeyEvent.VK_TAB) {
            cmdIspis.requestFocus();
        }
        // ako je Enter tipka, prikazi racun
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {

            int viewRow = tblPromet.getSelectedRow();
            int modelRow = tblPromet.convertRowIndexToModel(viewRow);
            Promet p = (Promet) tableModel.getDataObject(modelRow);

            int brojRacuna = p.getBrRac();

            // File obj. za txt racun
            File fileRacun = new File("Racuni/racun" + brojRacuna + ".txt");
            if (fileRacun.exists()) {
                // ako postoji, ucitaj sadrzaj
                try {
                    BufferedReader br = new BufferedReader(new FileReader(fileRacun));
                    String line;
                    String racunSadrzaj = "";
                    while ((line = br.readLine()) != null) {
                        racunSadrzaj = racunSadrzaj + line + "\n";
                    }
                    br.close();
                    // ispis
                    JTextArea txtRacun = new JTextArea(racunSadrzaj);
                    txtRacun.setFont(new Font(
                            postavke.getFontNaziv(), Font.PLAIN, postavke.getFontVelicina()));

                    // paneli
                    JPanel p1 = new JPanel();
                    JPanel p2 = new JPanel();


                    // povezivanje
                    p1.add(txtRacun);
                    p2.add(new JLabel("Br. racuna: " + brojRacuna));

                    // obj. koji sadrzi sve panele
                    Object[] message = {p2, p1};

                    JOptionPane.showMessageDialog(this, message,
                            "Promet", JOptionPane.PLAIN_MESSAGE);

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "tblPrometKeyPressed()", e);
                }

            } else {
                // info korisniku ako datoteka ne postoji
                JOptionPane.showMessageDialog(this,
                        "Datoteka:\n" + fileRacun.getAbsoluteFile().toString() + "\nne postoji!",
                        "Racun " + brojRacuna, JOptionPane.ERROR_MESSAGE);
            }

        }

    }//GEN-LAST:event_tblPrometKeyPressed

    private void cmdStornirajActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdStornirajActionPerformed
        String brojRacuna = JOptionPane.showInputDialog(this, "Broj racuna:",
                "Storniraj racun", JOptionPane.QUESTION_MESSAGE);
        String sLog;

        if (brojRacuna == null) {
            // korisnik se predomislio
            // :D
        } else {
            // File obj. za xml racun
            File fileRacun = new File("Racuni/racun" + brojRacuna + ".xml");
            // File obj. za provjeru da li vec postoji storn. odgovor za racun
            File fileResponse = new File("Racuni/racun" + brojRacuna + ".storno.response.xml");
            if (fileResponse.exists()) {
                // info korisniku ako datoteka ne postoji
                JOptionPane.showMessageDialog(this,
                        "Datoteka:\n" + fileResponse.getAbsoluteFile().toString() + "\n postoji. Racun je vec storniran!",
                        "Storniraj racun", JOptionPane.ERROR_MESSAGE);
            } else {
                if (fileRacun.exists()) {
                    // ako postoji, ucitaj sadrzaj
                    try {
                        // napravi xml obj. za racun
                        XMLRacun x1 = new XMLRacun();
                        // ucitaj racun iz xml datoteke
                        x1.loadRacun(fileRacun.getAbsolutePath());
                        // ucitaj priv. kljuc
                        x1.loadKeyStore(postavke.getKeyFile(), postavke.getKeyPassword());
                        // URL - ovo je malo nespretno, jer se moze dogoditi da ne ode na pravi server
                        //  gdje je i originalno racun napravljen
                        x1.setURL(postavke.getsURL());
                        // postavi storniranje
                        x1.stornirajRacun();
                        // izgradi ponovno xml racun
                        x1.buildRacun();
                        // spremi u xml datoteku
                        x1.saveRacun("Racuni/racun" + brojRacuna + ".storno.xml");
                        // potpisi
                        x1.signRacun();
                        // logiranje
                        sLog = ("Storniram racun br. " + brojRacuna + " ...");
                        // posalji racun
                        x1.sendRacun(postavke.getTimeout());
                        if (x1.getJir().length() > 0) {

                            // spremi racun
                            x1.saveRacun("Racuni/racun" + brojRacuna + ".storno.response.xml");

                            // osvjezi prikaz u tablici
                            loadPromet();

                            // obavijesti korisnika
                            JOptionPane.showMessageDialog(this,
                                    "Racun " + brojRacuna + "storniran.",
                                    "Storniraj racun", JOptionPane.INFORMATION_MESSAGE);

                        } else {
                            sLog = sLog + "\nGRESKA: " + x1.getGreska();

                            JOptionPane.showMessageDialog(this,
                                    "\nGRESKA: " + x1.getGreska(),
                                    "Storniraj racun", JOptionPane.ERROR_MESSAGE);

                        }

                        logger.info(sLog);

                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "cmdStorniraj()", e);
                    }

                } else {
                    // info korisniku ako datoteka ne postoji
                    JOptionPane.showMessageDialog(this,
                            "Datoteka:\n" + fileRacun.getAbsoluteFile().toString() + "\nne postoji!",
                            "Storniraj racun", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_cmdStornirajActionPerformed

    private void cmdSpremiCsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSpremiCsvActionPerformed

        // Datoteka za spremanje prometa
        String filename;

        // Format cijene, pdv-a i pnp-a
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(blagajna.lokalizacija);
        // Forsiraj , zbog Excel formatiranja
        otherSymbols.setDecimalSeparator(',');
        DecimalFormat dFormat = new DecimalFormat("0.00", otherSymbols);


        // napravi Save As dialog
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
        FileFilter filter1 = new ExtensionFileFilter("Excel/Spreadsheet datoteka", new String[]{"csv", "txt"});
        jfc.setFileFilter(filter1);
        //jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

            // spremi naziv datoteke
            filename = (jfc.getSelectedFile().toString());
            // dodaj csv nastavak ako korisnik nije odabrao
            if (!(filename.endsWith(".txt") || filename.endsWith(".csv"))) {
                filename = filename + ".csv";
            }

            // otvori printer za ispis u datoteku
            PrintWriter pw = null;
            try {
                // otvori datoteku
                File file = new File(filename);
                pw = new PrintWriter(file);

                // ispis naslovnih polja
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    pw.print(tableModel.getColumnName(i) + ";");
                }
                pw.println();

                // ispis tablice u datoteku
                for (Promet p : tableModel.getData()) {
                    // ispis svake stavke
                    pw.println(p.getBrRac() + ";"
                            + "\"" + p.getDatumIspis() + "\";"
                            + "\"" + dFormat.format(p.getIznosUkupno()) + "\";"
                            + "\"" + dFormat.format(p.getUkupnoPdv()) + "\";"
                            + "\"" + dFormat.format(p.getUkupnoPnp()) + "\";"
                            + p.getStorniranIspis() + ";"
                            + p.getOibOper() + ";"
                            + p.getZkod() + ";"
                            + p.getJir() + ";");
                }
                pw.close();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "cmdSpremiCsv()", ex);
            } finally {
                pw.close();
            }
        }
    }//GEN-LAST:event_cmdSpremiCsvActionPerformed

    private void cmdUkupnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUkupnoActionPerformed

        final DecimalFormat dFormat = new DecimalFormat("0.00");

        double ukupno = 0.00;
        double pdv = 0.00;
        double pnp = 0.00;
        int storno = 0;

        // zbroji ukupni iznos racuna, pdv-a, pnp-a i storniranih
        for (Promet p : tableModel.getData()) {
            if (p.isStorniran()) {
                storno++;
            } else {
                ukupno = ukupno + p.getIznosUkupno();
                pdv = pdv + p.getUkupnoPdv();
                pnp = pnp + p.getUkupnoPnp();
            }
        }

        String poruka = "";
        if (mjesecniPrikaz) {
            poruka = "Mjesecni promet, " + txtDatum.getText().substring(
                    txtDatum.getText().indexOf('.') + 1, txtDatum.getText().length()) + "\n";
        } else {
            poruka = "Dnevni promet, " + txtDatum.getText() + "\n";
        }
        poruka = poruka
                + "--------------------------\n"
                + "Ukupno: " + dFormat.format(ukupno) + " KN\n"
                + "PDV: " + dFormat.format(pdv) + " KN\n"
                + "PNP: " + dFormat.format(pnp) + " KN\n"
                + "Stornirano: " + storno + " racuna.\n";

        final JDialog dialog = new JDialog(this, "Ukupno");
        
        final JTextArea txtPoruka = new JTextArea(poruka);
        txtPoruka.setFont(new Font(postavke.getFontNaziv(), Font.PLAIN, postavke.getFontVelicina()));

        final JButton cmdIzlaz = new JButton("Izlaz");
        cmdIzlaz.setToolTipText("Zatvara prozor");
        cmdIzlaz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dialog.dispose();
            }
        });
        cmdIzlaz.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdIzlaz.setBackground(boja2);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdIzlaz.setBackground(boja1);
            }
        });

        final JButton cmdIspisiUkupno = new JButton("Ispis ukupnog");
        cmdIspisiUkupno.setToolTipText("Ispisuje na pisac ukupni promet");
        cmdIspisiUkupno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {

                String nazivZadatka;
                if (mjesecniPrikaz) {
                    nazivZadatka = "Ukupno " + txtDatum.getText().substring(
                            txtDatum.getText().indexOf('.') + 1, txtDatum.getText().length());
                } else {
                    nazivZadatka = "Ukupno " + txtDatum.getText();
                }

                Ispis ispis = new Ispis(nazivZadatka.replace('.', '-'), postavke.getPisac(),
                        postavke.getPapirX(), postavke.getPapirY(),
                        postavke.getMarginaX(),
                        postavke.getMarginaY(),
                        postavke.getIspisnaPovrsinaX(),
                        postavke.getIspisnaPovrsinaY());

                try {
                    /**
                     * Ispis.
                     */
                    ispis.print(txtPoruka.getPrintable(null, null));
                } catch (PrinterException ex) {
                    logger.log(Level.SEVERE, "cmdIspisiUkupno()", ex);
                }
            }
        });
        cmdIspisiUkupno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdIspisiUkupno.setBackground(boja2);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdIspisiUkupno.setBackground(boja1);
            }
        });
        
        final JButton cmdIspisiPromet = new JButton("Ispis prometa");
        cmdIspisiPromet.setToolTipText("Ispisuje na pisac promet prikazan u tablici");
        cmdIspisiPromet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {

                String nazivZadatka;
                if (mjesecniPrikaz) {
                    nazivZadatka = "Promet " + txtDatum.getText().substring(
                            txtDatum.getText().indexOf('.') + 1, txtDatum.getText().length());
                } else {
                    nazivZadatka = "Promet " + txtDatum.getText();
                }

                Ispis ispis = new Ispis(nazivZadatka.replace('.', '-'), postavke.getPisac(),
                        postavke.getPapirX(), postavke.getPapirY(),
                        postavke.getMarginaX(),
                        postavke.getMarginaY(),
                        postavke.getIspisnaPovrsinaX(),
                        postavke.getIspisnaPovrsinaY());

                try {
                    /**
                     * Ispis.
                     */
                    ispis.setPrintDialog(true);
                    ispis.print(tblPromet.getPrintable(PrintMode.FIT_WIDTH, null, null));

                } catch (PrinterException ex) {
                    logger.log(Level.SEVERE, "cmdIspisiPromet()", ex);
                }
            }
        });
        cmdIspisiPromet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdIspisiPromet.setBackground(boja2);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdIspisiPromet.setBackground(boja1);
            }
        });
        
        final JButton cmdIspisiRacune = new JButton("Ispis svih racuna");
        cmdIspisiRacune.setToolTipText("Ispisuje na pisac sve racune prikazane u tablici");
        cmdIspisiRacune.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {

                // Ucitavanje i ispis svakog racuna
                for (Promet p : tableModel.getData()) {

                    /**
                     * Postavlja naziv ispisnog zadatka za Print Spooler.
                     */
                    String nazivZadatka;
                    nazivZadatka = "Racun " + p.getBrRac() + ", " + p.getDatumIspis().substring(0, 10);

                    Ispis ispis = new Ispis(nazivZadatka.replace('.', '-'), postavke.getPisac(),
                            postavke.getPapirX(), postavke.getPapirY(),
                            postavke.getMarginaX(),
                            postavke.getMarginaY(),
                            postavke.getIspisnaPovrsinaX(),
                            postavke.getIspisnaPovrsinaY());

                    try {
                        // Ucitava svaki racun, liniju po liniju
                        BufferedReader br = new BufferedReader(new FileReader("Racuni/racun" + p.getBrRac() + ".txt"));
                        String line;
                        String racun = "";
                        while ((line = br.readLine()) != null) {
                            racun = racun + line + "\n";
                        }
                        br.close();

                        // Formira text polje za povezivanje na pisac i ispis
                        JTextArea txtRacun = new JTextArea(racun);
                        txtRacun.setFont(new Font(postavke.getFontNaziv(), Font.PLAIN, postavke.getFontVelicina()));

                        /**
                         * Ispis.
                         */
                        ispis.print(txtRacun.getPrintable(null, null));

                    } catch (PrinterException ex1) {
                        logger.log(Level.SEVERE, "cmdIspisiRacune(), PrinterException", ex1);
                    } catch (IOException ex2) {
                        logger.log(Level.SEVERE, "cmdIspisiRacune(), PrinterException", ex2);
                    }
                }

            }
        });
        cmdIspisiRacune.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdIspisiRacune.setBackground(boja2);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdIspisiRacune.setBackground(boja1);
            }
        });
        
        final JButton cmdBlagajnickiIzvjestaj = new JButton("Blag. izvjestaj");
        cmdBlagajnickiIzvjestaj.setToolTipText("Ispisuje na pisac brojeve racuna i iznose prikazane u tablici");
        cmdBlagajnickiIzvjestaj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {

                /**
                 * Postavlja naziv ispisnog zadatka za Print Spooler.
                 */
                String nazivZadatka;
                nazivZadatka = "Blagajnicki izvjestaj " + txtDatum.getText();

                Ispis ispis = new Ispis(nazivZadatka.replace('.', '-'), postavke.getPisac(),
                        postavke.getPapirX(), postavke.getPapirY(),
                        postavke.getMarginaX(),
                        postavke.getMarginaY(),
                        postavke.getIspisnaPovrsinaX(),
                        postavke.getIspisnaPovrsinaY());


                double ukupno = 0.00;

                int duzinaLinije = postavke.getDuzinaLinije();
                String izvjestaj = postavke.formatLine("Blagajnicki izvjestaj", txtDatum.getText(), duzinaLinije) + "\n";

                // Blagajnicki izvjestaj
                for (Promet p : tableModel.getData()) {
                    izvjestaj = izvjestaj + postavke.formatLine("Racun " + p.getBrRac(), p.getIznosUkupnoIspis(), duzinaLinije) + "\n";

                    ukupno = ukupno + p.getIznosUkupno();
                }
                izvjestaj = izvjestaj + postavke.formatLine("Ukupno: ", dFormat.format(ukupno), duzinaLinije) + "\n";

                // Formira text polje za povezivanje na pisac i ispis
                JTextArea txtRacun = new JTextArea(izvjestaj);
                txtRacun.setFont(new Font(postavke.getFontNaziv(), Font.PLAIN, postavke.getFontVelicina()));

                try {
                    /**
                     * Ispis.
                     */
                    ispis.print(txtRacun.getPrintable(null, null));
                } catch (PrinterException ex) {
                    logger.log(Level.SEVERE, "Racuni()", ex);
                }

            }
        });
        cmdBlagajnickiIzvjestaj.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdBlagajnickiIzvjestaj.setBackground(boja2);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdBlagajnickiIzvjestaj.setBackground(boja1);
            }
        });

        /**
         * Slozi izgled dugmica.
         */        
        for (JButton dugmic : new JButton[] {cmdIzlaz, cmdIspisiUkupno, cmdIspisiPromet, cmdIspisiRacune, cmdBlagajnickiIzvjestaj}) {        
            dugmic.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina())); 
            //dugmic.setPreferredSize(new Dimension(dugmic.getWidth(), izgled.getDebljinaDugmica()));
        }
        
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);        
        
        JPanel pnlUkupno = new JPanel(new BorderLayout());
        pnlUkupno.add(new Label("Ukupno:"), BorderLayout.PAGE_START);
        pnlUkupno.add(txtPoruka, BorderLayout.CENTER);
        
        JPanel pnlDugmici = new JPanel(new BorderLayout());
        JPanel pnlRed1 = new JPanel();
        JPanel pnlRed2 = new JPanel();
        pnlRed1.add(cmdIspisiUkupno);
        pnlRed1.add(cmdIspisiPromet);
        pnlRed1.add(cmdIspisiRacune);
        pnlRed2.add(cmdBlagajnickiIzvjestaj);
        pnlRed2.add(cmdIzlaz);
                    
        pnlDugmici.add(pnlRed1, BorderLayout.PAGE_START);
        pnlDugmici.add(pnlRed2, BorderLayout.PAGE_END);
        
        dialog.add(pnlUkupno, BorderLayout.CENTER);
        dialog.add(pnlDugmici, BorderLayout.PAGE_END);

        dialog.pack();
        dialog.setVisible(true);
    }//GEN-LAST:event_cmdUkupnoActionPerformed

    private void cmdPosaljiBezJIRaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPosaljiBezJIRaActionPerformed

        loadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // za ucitavanje racuna iz xml-a
                    XMLRacun x1;

                    // po def. svi racuni bi trebali biti u Racuni folderu
                    File dirRacun = new File("Racuni");
                    File xmlRacun;
                    File xmlRacunResponse;

                    // postavi progres bar na pocetak
                    progresBar.setMinimum(0);
                    progresBar.setMaximum(postavke.getBrRac());

                    // onemoguci dugmic
                    cmdPosaljiBezJIRa.setEnabled(false);

                    // broj uspjesno prijavljenih racuna 
                    //  (oni koji nisu do sada poslani)
                    int uspjesno = 0;
                    // ukupno neprijavljenih racuna
                    int ukupno = 0;

                    int brRac = postavke.getBrRac();

                    // sada provjeri da li postoji folder Racun gdje se trebaju spremati racuni
                    if (dirRacun.exists()) {

                        // prodji po listi racuna
                        for (int i = 1; i < brRac; i++) {

                            // uvecaj progres bar
                            progresBar.setValue(i);

                            // ime datoteke racuna (xml, txt, signed.xml, ...)
                            xmlRacun = new File("Racuni/racun" + i + ".xml");
                            xmlRacunResponse = new File("Racuni/racun" + i + ".response.xml");

                            // ako postoji xml racun, a ne i odgovor
                            if (xmlRacun.exists() && !xmlRacunResponse.exists()) {
                                ukupno++;
                                x1 = new XMLRacun();
                                x1.loadRacun(xmlRacun.getAbsolutePath());
                                x1.loadKeyStore(postavke.getKeyFile(), postavke.getKeyPassword());
                                // URL - ovo je malo nespretno, jer se moze dogoditi da ne ode na pravi server
                                //  gdje je i originalno racun napravljen
                                x1.setURL(postavke.getsURL());
                                x1.buildRacun();
                                x1.signRacun();
                                //logger.info("Salje racun " + i);
                                x1.sendRacun(postavke.getTimeout());
                                // ako je uspjesno poslan, spremi ga u xml
                                if (x1.getJir().length() > 0) {
                                    uspjesno++;
                                    x1.saveRacun("Racuni/racun" + i + ".response.xml");
                                }
                            }


                        }

                        logger.info("Uspjesno prijavljeno: " + uspjesno + "/" + ukupno);

                    }

                    // postavi progres bar na pocetak opet
                    progresBar.setMinimum(0);
                    progresBar.setMaximum(postavke.getBrRac());

                    // omoguci dugmice 
                    cmdPosaljiBezJIRa.setEnabled(true);

                    // prikazi korisniku statistiku uspjesno poslanih racuna
                    JOptionPane.showMessageDialog(
                            null, "Uspjesno prijavljeno: " + uspjesno + "/" + ukupno,
                            "Posalji sve bez JIRa", JOptionPane.INFORMATION_MESSAGE);

                    // ucitaj promet za tekuci/odabrani datum
                    loadPromet();


                } catch (Exception e) {
                    logger.log(Level.SEVERE, "cmdPosaljiBezJIRa(), sendThread()", e);

                    // omoguci dugmice 
                    cmdPosaljiBezJIRa.setEnabled(true);

                    // do kraja postavi
                    progresBar.setValue(progresBar.getMinimum());

                }
            }
        });
        loadThread.start();

    }//GEN-LAST:event_cmdPosaljiBezJIRaActionPerformed

    private void cmdIspisFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIspisFocusGained
        cmdIspis.setBackground(boja2);
    }//GEN-LAST:event_cmdIspisFocusGained

    private void cmdIspisFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIspisFocusLost
        cmdIspis.setBackground(boja1);
    }//GEN-LAST:event_cmdIspisFocusLost

    private void cmdStornirajFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdStornirajFocusGained
        cmdStorniraj.setBackground(boja2);
    }//GEN-LAST:event_cmdStornirajFocusGained

    private void cmdStornirajFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdStornirajFocusLost
        cmdStorniraj.setBackground(boja1);
    }//GEN-LAST:event_cmdStornirajFocusLost

    private void cmdDatumPrethodniFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdDatumPrethodniFocusGained
        cmdDatumPrethodni.setBackground(boja2);
    }//GEN-LAST:event_cmdDatumPrethodniFocusGained

    private void cmdDatumPrethodniFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdDatumPrethodniFocusLost
        cmdDatumPrethodni.setBackground(boja1);
    }//GEN-LAST:event_cmdDatumPrethodniFocusLost

    private void cmdDatumSljedeciFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdDatumSljedeciFocusGained
        cmdDatumSljedeci.setBackground(boja2);
    }//GEN-LAST:event_cmdDatumSljedeciFocusGained

    private void cmdDatumSljedeciFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdDatumSljedeciFocusLost
        cmdDatumSljedeci.setBackground(boja1);
    }//GEN-LAST:event_cmdDatumSljedeciFocusLost

    private void cmdSpremiCsvFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdSpremiCsvFocusGained
        cmdSpremiCsv.setBackground(boja2);
    }//GEN-LAST:event_cmdSpremiCsvFocusGained

    private void cmdSpremiCsvFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdSpremiCsvFocusLost
        cmdSpremiCsv.setBackground(boja1);
    }//GEN-LAST:event_cmdSpremiCsvFocusLost

    private void cmdUkupnoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdUkupnoFocusGained
        cmdUkupno.setBackground(boja2);
    }//GEN-LAST:event_cmdUkupnoFocusGained

    private void cmdUkupnoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdUkupnoFocusLost
        cmdUkupno.setBackground(boja1);
    }//GEN-LAST:event_cmdUkupnoFocusLost

    private void cmdPosaljiBezJIRaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPosaljiBezJIRaFocusGained
        cmdPosaljiBezJIRa.setBackground(boja2);
    }//GEN-LAST:event_cmdPosaljiBezJIRaFocusGained

    private void cmdPosaljiBezJIRaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPosaljiBezJIRaFocusLost
        cmdPosaljiBezJIRa.setBackground(boja1);
    }//GEN-LAST:event_cmdPosaljiBezJIRaFocusLost

    private void cmdIzlazFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIzlazFocusGained
        cmdIzlaz.setBackground(boja2);
    }//GEN-LAST:event_cmdIzlazFocusGained

    private void cmdIzlazFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIzlazFocusLost
        cmdIzlaz.setBackground(boja1);
    }//GEN-LAST:event_cmdIzlazFocusLost

    private void cmdMjesecnoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdMjesecnoFocusGained
        cmdMjesecno.setBackground(boja2);
    }//GEN-LAST:event_cmdMjesecnoFocusGained

    private void cmdMjesecnoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdMjesecnoFocusLost
        cmdMjesecno.setBackground(boja1);
    }//GEN-LAST:event_cmdMjesecnoFocusLost

    private void cmdMjesecnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdMjesecnoActionPerformed

        Thread mjesecniThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Calendar cal = Calendar.getInstance();

                // Godina i mjesec
                int yyyy = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(odabraniDatum));
                int mm = Integer.parseInt(new SimpleDateFormat("MM", Locale.ENGLISH).format(odabraniDatum)) - 1;

                // postavi progres bar na pocetak
                progresBar.setMinimum(0);
                progresBar.setMaximum(31);

                // ocisti tablicu proizvoda
                tableModel.deletePrometList();

                // Spremi var. odabraniDatum
                Date tmpOdabraniDatum = new Date();
                tmpOdabraniDatum = odabraniDatum;
                                
                for (int i = 1; i <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                    try {
                        cal.set(yyyy, mm, i);
                        odabraniDatum = cal.getTime();

                        /**
                         * Ucitaj promet.
                         */
                        // za ucitavanje racuna iz xml-a
                        XMLRacun x1;

                        // popis xml racuna
                        List<String> xmlFile = new ArrayList<String>();

                        // po def. svi racuni bi trebali biti u Racuni folderu
                        File dirRacun = new File("Racuni");

                        // formiraj tekstualni oblik odabranog datuma
                        String datum = new SimpleDateFormat(
                                "dd.MM.yyyy", Locale.ENGLISH).format(cal.getTime());

                        // sada provjeri da li postoji folder Racun gdje se trebaju spremati racuni
                        if (dirRacun.exists()) {

                            // prodji po listi racuna i napunni sa svim racunima iz 
                            //  odabranog mjeseca
                            for (File txtFile : dirRacun.listFiles()) {

                                // ime datoteke racuna (xml, txt, signed.xml, ...)
                                String filename = txtFile.getName();
                                String fileDate = new SimpleDateFormat(
                                        "dd.MM.yyyy", Locale.ENGLISH).format(txtFile.lastModified());

                                // provjeri da li datoteka/racun sa trazenim datumom
                                // ovo je najjednostavniji nacin da se samo po datumu (bez vremena)
                                //  usporedjuje da li datoteka ima trazeni datum
                                if (fileDate.equalsIgnoreCase(datum)) {
                                    // provjera koji je nastavak datoteke
                                    if (filename.endsWith((".txt"))) {
                                        // ne treba sadrzaj tih datoteka za popunjavanje tablice
                                        // koristit se za ispis, npr. cmdIspis()
                                    } else if (filename.endsWith(".signed.xml")) {
                                        // ne treba sadrzaj tih datoteka za popunjavanje tablice
                                    } else if (filename.endsWith(".response.xml")) {
                                        // ne treba za sada sadrzaj tih datoteka 
                                        // koriste se za ucitavanje JIRa
                                    } else if (filename.endsWith("storno.xml")) {
                                        // stornirani racuni, ne treba ih dodati na popis                                   
                                    } else if (filename.endsWith("storno.response.xml")) {
                                        // odgovor(i) na storniranje racuna, za JIR                                    
                                    } else if (filename.endsWith("error.xml")) {
                                        // Greske kod odziva iz PU                                        
                                    } else if (filename.endsWith(".xml")) {
                                        // racuni u xml formatu
                                        xmlFile.add("Racuni/" + filename);
                                    }
                                }
                            }
                        }

                        // napuni tablicu sa proizvodima
                        for (int j = 0; j < xmlFile.size(); j++) {

                            // formiraj naziv datoteke odgovora
                            //  nesto poput: Racuni/racun123.response.xml
                            File xmlFileResponse = new File(xmlFile.get(j).substring(
                                    0, xmlFile.get(j).length() - 3) + "response.xml");

                            File xmlFileStornoResponse = new File(xmlFile.get(j).substring(
                                    0, xmlFile.get(j).length() - 3) + "storno.response.xml");

                            x1 = new XMLRacun();
                            x1.loadRacun(xmlFile.get(j));

                            String jir = "-";
                            boolean storniran = false;

                            // Postoji li odgovor na racun?
                            // Provjeri da je datoteka veca od 100 bajtova.
                            if (xmlFileResponse.exists() && xmlFileResponse.length() > 100) {
                                // Ucitavanje JIRa iz odgovora
                                x1.loadRacunOdgovor(xmlFileResponse.getAbsolutePath());
                                jir = x1.getJir();
                            }

                            // postoji li storno odgovor?
                            if (xmlFileStornoResponse.exists()) {
                                storniran = true;
                            }

                            tableModel.addPromet(
                                    new Promet(x1.getBrRac(), x1.getDatVrijeme(), x1.getIznosUkupno(),
                                    x1.getUkupnoPdv(), x1.getUkupnoPnp(), storniran,
                                    x1.getOibOper(), x1.getZastitniKod(), jir));

                            // postavi progres
                            progresBar.setValue(i);
                        }


                    } catch (Exception e) {
                        // do kraja postavi
                        progresBar.setValue(progresBar.getMaximum());
                        break;
                    }
                }

                // vrati vr. varijabli
                odabraniDatum = tmpOdabraniDatum;

                // do kraja postavi
                progresBar.setValue(progresBar.getMaximum());

            }
        });
        mjesecniThread.start();

        // Mjesecni prikaz, zastavica
        mjesecniPrikaz = true;

    }//GEN-LAST:event_cmdMjesecnoActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdDatumPrethodni;
    private javax.swing.JButton cmdDatumSljedeci;
    private javax.swing.JButton cmdIspis;
    private javax.swing.JButton cmdIzlaz;
    private javax.swing.JButton cmdMjesecno;
    private javax.swing.JButton cmdPosaljiBezJIRa;
    private javax.swing.JButton cmdSpremiCsv;
    private javax.swing.JButton cmdStorniraj;
    private javax.swing.JButton cmdUkupno;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnlIzbornik;
    private javax.swing.JPanel pnlPromet;
    private javax.swing.JProgressBar progresBar;
    private javax.swing.JTable tblPromet;
    private javax.swing.JTextField txtDatum;
    // End of variables declaration//GEN-END:variables
}
