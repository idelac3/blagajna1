package blagajna;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author vedran_kolonic
 */
public class FrmCjenik extends javax.swing.JFrame implements ListSelectionListener {

    /**
     * Objekt za logiranje gresaka
     */
    private Logger logger;

    /**
     * tablica proizvoda/usluga -> model podataka ispod same tablice
     */
    private RacunJTableDataModel tableModel;
    /**
     * sorter tablice
     */
    private TableRowSorter<RacunJTableDataModel> sorter;
    /**
     * Popis proizvoda
     */
    private List<String> proizvodi;
    /**
     * Mapa proizvoda
     */
    private Map<String, Proizvod> proizvodiMap;
    /**
     * Oznaceni redak (onaj sa fokusom u tablici)
     */
    int selectedRow = -1;
    /**
     * Cjenik
     */
    private Cjenik c1;
    /**
     * Adapter za prozor
     */
    private FormWindowAdapter windowAdapter;
    /**
     * Ref. na glavni prozor, frmBlagajna
     */
    private final FrmBlagajna blagajna;
    private boolean priceSortReverse;
    private boolean nameSortReverse = true;

    /**
     * Boja dugmica kada nema fokus
     */
    private Color boja1;
    /**
     * Boja dugmica kada ima fokus
     */
    private Color boja2;
    /**
     * Javni objekt za dohvat izgleda korisnickog sucelja.<BR>
     * Font, velicina, debljina, ... <BR>
     */
    public Izgled izgled;

    /**
     * Konstruktor za FrmCjenik prozor
     *
     * @param blagajna referenca na glavni prozor, FrmBlagajna prozor
     */
    public FrmCjenik(FrmBlagajna blagajna) {

        // napravi referencu na glavni prozor
        this.blagajna = blagajna;

        try {
            // logiranje problema na syslog/datoteku
            logger = blagajna.getLogger();

            // model podataka
//            this.tableModel = new RacunJTableDataModel(true);
                    
            this.tableModel = blagajna.getTableModel();
            this.tableModel.setEditable(true);

            this.windowAdapter = new FormWindowAdapter(blagajna, true);
            this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            this.addWindowListener(windowAdapter);


            // Napravi GUI
            initComponents();
            // postavi pocetne boje
            boja1 = cmdSpremiPromjene.getBackground();
            boja2 = Color.ORANGE;
            ui();
            
            tblProizvod.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // postavi tablici proizvoda/usluga obj.model podataka
            tblProizvod.setModel(tableModel);

            // ucitaj cjenik
            initCjenik();

            // inicijaliziraj sorter
            sorter = new TableRowSorter<RacunJTableDataModel>(tableModel);
            tblProizvod.setRowSorter(sorter);


            tblProizvod.getSelectionModel().addListSelectionListener(this);

            tblProizvod.getColumnModel().getColumn(0).setPreferredWidth(150);
            tblProizvod.getColumnModel().getColumn(1).setPreferredWidth(50);
            tblProizvod.getColumnModel().getColumn(2).setPreferredWidth(50);
            tblProizvod.getColumnModel().getColumn(3).setPreferredWidth(50);


            initSearch();

            createKeybindings(tblProizvod);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "FrmCjenik()", e);
        }
    }

    private void createKeybindings(JTable table) {
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // ako je odabran proizvod iz tablice ...
                // napravi nešto
                // za sada ne radi ništa, inače bez ovoga defaultno ponašanje je prelazak u novi red u tablici
            }
        });
    }

    /**
     * Procedura za selektiranje jTextField polja za unos teksta
     *
     * @param txtField jTextField objekt
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

        pnlUnos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtNazivProizvoda = new javax.swing.JTextField();
        txtPDV = new javax.swing.JTextField();
        txtPNP = new javax.swing.JTextField();
        txtCijenaProizvoda = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmdDodajProizvod = new javax.swing.JButton();
        cmdBrisiProizvod = new javax.swing.JButton();
        cmdPromijeni = new javax.swing.JButton();
        cmdSortirajTablicu = new javax.swing.JButton();
        cmdSortirajTablicuPoCijeni = new javax.swing.JButton();
        pnlIzbornik = new javax.swing.JPanel();
        cmdSpremiPromjene = new javax.swing.JButton();
        cmdIzlaz = new javax.swing.JButton();
        pnlProizvod = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProizvod = new javax.swing.JTable();
        txtError = new javax.swing.JTextField();

        setTitle("Cjenik / Proizvodi");
        setAlwaysOnTop(true);

        pnlUnos.setBorder(javax.swing.BorderFactory.createTitledBorder("Unos"));
        pnlUnos.setPreferredSize(new java.awt.Dimension(201, 364));

        jLabel1.setText("Naziv:");

        jLabel2.setText("Cijena:");

        jLabel3.setText("PDV:");

        jLabel4.setText("PNP:");

        txtNazivProizvoda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNazivProizvodaFocusGained(evt);
            }
        });
        txtNazivProizvoda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNazivProizvodaKeyTyped(evt);
            }
        });

        txtPDV.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPDVFocusGained(evt);
            }
        });
        txtPDV.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPDVKeyTyped(evt);
            }
        });

        txtPNP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPNPFocusGained(evt);
            }
        });
        txtPNP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPNPKeyTyped(evt);
            }
        });

        txtCijenaProizvoda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCijenaProizvodaActionPerformed(evt);
            }
        });
        txtCijenaProizvoda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCijenaProizvodaFocusGained(evt);
            }
        });
        txtCijenaProizvoda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCijenaProizvodaKeyTyped(evt);
            }
        });

        jLabel5.setText("KN");

        jLabel6.setText("%");

        jLabel7.setText("%");

        cmdDodajProizvod.setText("Dodaj");
        cmdDodajProizvod.setToolTipText("Dodaj novu stavku u cjenik");
        cmdDodajProizvod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDodajProizvodActionPerformed(evt);
            }
        });
        cmdDodajProizvod.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdDodajProizvodFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdDodajProizvodFocusLost(evt);
            }
        });

        cmdBrisiProizvod.setText("Obrisi");
        cmdBrisiProizvod.setToolTipText("Obrisi oznacenu stavku");
        cmdBrisiProizvod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBrisiProizvodActionPerformed(evt);
            }
        });
        cmdBrisiProizvod.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdBrisiProizvodFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdBrisiProizvodFocusLost(evt);
            }
        });

        cmdPromijeni.setText("Promijeni");
        cmdPromijeni.setToolTipText("Spremi promjene u odabranu stavku");
        cmdPromijeni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPromijeniActionPerformed(evt);
            }
        });
        cmdPromijeni.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdPromijeniFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdPromijeniFocusLost(evt);
            }
        });

        cmdSortirajTablicu.setText("Sortiraj po nazivu proizvoda");
        cmdSortirajTablicu.setToolTipText("Sortiraj po nazivu proizvoda");
        cmdSortirajTablicu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSortirajTablicuActionPerformed(evt);
            }
        });
        cmdSortirajTablicu.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdSortirajTablicuFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdSortirajTablicuFocusLost(evt);
            }
        });

        cmdSortirajTablicuPoCijeni.setText("Sortiraj po cijeni proizvoda");
        cmdSortirajTablicuPoCijeni.setToolTipText("Sortiraj po cijeni proizvoda");
        cmdSortirajTablicuPoCijeni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSortirajTablicuPoCijeniActionPerformed(evt);
            }
        });
        cmdSortirajTablicuPoCijeni.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdSortirajTablicuPoCijeniFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdSortirajTablicuPoCijeniFocusLost(evt);
            }
        });

        javax.swing.GroupLayout pnlUnosLayout = new javax.swing.GroupLayout(pnlUnos);
        pnlUnos.setLayout(pnlUnosLayout);
        pnlUnosLayout.setHorizontalGroup(
            pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnosLayout.createSequentialGroup()
                .addGroup(pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlUnosLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNazivProizvoda)
                            .addGroup(pnlUnosLayout.createSequentialGroup()
                                .addGroup(pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtPDV, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPNP)
                                    .addComponent(txtCijenaProizvoda, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)))))
                    .addComponent(cmdDodajProizvod, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdBrisiProizvod, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdPromijeni, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSortirajTablicu, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSortirajTablicuPoCijeni, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlUnosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdBrisiProizvod, cmdDodajProizvod, cmdPromijeni, cmdSortirajTablicu, cmdSortirajTablicuPoCijeni});

        pnlUnosLayout.setVerticalGroup(
            pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlUnosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNazivProizvoda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCijenaProizvoda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtPDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlUnosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPNP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmdDodajProizvod, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdBrisiProizvod, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdPromijeni)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdSortirajTablicu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdSortirajTablicuPoCijeni)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        pnlUnosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdBrisiProizvod, cmdDodajProizvod, cmdPromijeni, cmdSortirajTablicu, cmdSortirajTablicuPoCijeni});

        pnlIzbornik.setBorder(javax.swing.BorderFactory.createTitledBorder("Izbornik"));

        cmdSpremiPromjene.setText("Spremi promjene");
        cmdSpremiPromjene.setToolTipText("Spremi promjene trajno");
        cmdSpremiPromjene.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSpremiPromjeneActionPerformed(evt);
            }
        });
        cmdSpremiPromjene.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cmdSpremiPromjeneFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmdSpremiPromjeneFocusLost(evt);
            }
        });

        cmdIzlaz.setText("Izlaz");
        cmdIzlaz.setToolTipText("Zatvori prozor bez spremanja promjena");
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

        javax.swing.GroupLayout pnlIzbornikLayout = new javax.swing.GroupLayout(pnlIzbornik);
        pnlIzbornik.setLayout(pnlIzbornikLayout);
        pnlIzbornikLayout.setHorizontalGroup(
            pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlIzbornikLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(cmdSpremiPromjene, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(cmdIzlaz, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pnlIzbornikLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdIzlaz, cmdSpremiPromjene});

        pnlIzbornikLayout.setVerticalGroup(
            pnlIzbornikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlIzbornikLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmdSpremiPromjene, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmdIzlaz, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pnlIzbornikLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdIzlaz, cmdSpremiPromjene});

        pnlProizvod.setBorder(javax.swing.BorderFactory.createTitledBorder("Proizvodi"));
        pnlProizvod.setPreferredSize(new java.awt.Dimension(375, 365));

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

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
        tblProizvod.setDragEnabled(true);
        tblProizvod.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblProizvodFocusGained(evt);
            }
        });
        tblProizvod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblProizvodFiltriraj(evt);
            }
        });
        jScrollPane2.setViewportView(tblProizvod);

        javax.swing.GroupLayout pnlProizvodLayout = new javax.swing.GroupLayout(pnlProizvod);
        pnlProizvod.setLayout(pnlProizvodLayout);
        pnlProizvodLayout.setHorizontalGroup(
            pnlProizvodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProizvodLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlProizvodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlProizvodLayout.setVerticalGroup(
            pnlProizvodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProizvodLayout.createSequentialGroup()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        txtError.setEditable(false);
        txtError.setFocusable(false);
        txtError.setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtError)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlProizvod, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlUnos, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlIzbornik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlProizvod, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .addComponent(pnlUnos, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .addComponent(pnlIzbornik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtError, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdIzlazActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdIzlazActionPerformed
//        tableModel.deleteProizvodList();
        tableModel.setEditable(false);
        blagajna.toggleScreen(true);
        this.setVisible(false);
    }//GEN-LAST:event_cmdIzlazActionPerformed

    private void tblProizvodFiltriraj(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblProizvodFiltriraj

        // ako je odabran proizvod iz tablice ...
        if (tblProizvod.getSelectedRowCount() > 0) {

            int viewRow = tblProizvod.getSelectedRow();
            int modelRow = tblProizvod.convertRowIndexToModel(viewRow);

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
            if ((evt.getKeyChar() >= 'A' && evt.getKeyChar() <= 'z') || (evt.getKeyChar() == '-')) {
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
                if (evt.getKeyChar() >= 'A' && evt.getKeyChar() <= 'z') {
                    String txt = txtSearch.getText();
                    txtSearch.setText(txt + evt.getKeyChar());
                    newFilter();
                }
            }
        }
        // ako korisnik pritisne TAB, skoci na Ispis dugme
        if (evt.getKeyChar() == KeyEvent.VK_TAB) {
            txtNazivProizvoda.requestFocus();
        }
    }//GEN-LAST:event_tblProizvodFiltriraj

    private void cmdDodajProizvodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDodajProizvodActionPerformed
        
        String naziv = txtNazivProizvoda.getText();
        String cijenaTemp = txtCijenaProizvoda.getText();
        String pdvTemp = txtPDV.getText();
        String pnpTemp = txtPNP.getText();

        if (naziv != null && cijenaTemp != null) {
            try {
                double cijena = Double.parseDouble(cijenaTemp);
                double pdv = Double.parseDouble(pdvTemp);
                double pnp = Double.parseDouble(pnpTemp);

                Proizvod p = new Proizvod(naziv, cijena, pdv / 100, pnp / 100);
                proizvodi.add(p.getNaziv());
                proizvodiMap.put(naziv, p);
                tableModel.addProizvod(p);
                txtError.setText("Proizvod: " + naziv + " " + pdv + " " + pnp + " unesen!");
                txtNazivProizvoda.requestFocus();
                
            } catch (NumberFormatException e) {
                txtError.setText("Pogresan unos! Za cijenu, pdv i pnp unesite broj odvojen tockom. Npr. 10.99");
            }
        }

    }//GEN-LAST:event_cmdDodajProizvodActionPerformed

    private void cmdPromijeniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPromijeniActionPerformed

        String naziv = txtNazivProizvoda.getText();
        String cijenaTemp = txtCijenaProizvoda.getText();
        String pdvTemp = txtPDV.getText();
        String pnpTemp = txtPNP.getText();

        if (naziv != null && cijenaTemp != null) {
            try {
                double cijena = Double.parseDouble(cijenaTemp);
                double pdv = Double.parseDouble(pdvTemp);
                double pnp = Double.parseDouble(pnpTemp);

                // napravi proizvod
                Proizvod p = new Proizvod(naziv, cijena, pdv / 100, pnp / 100);
                // provjeri da li postoji prije promjene
                if (tableModel.isProizvod(p)) {
                    // azuriraj mapu proizvoda
                    proizvodiMap.put(naziv, p);
                    // azurira tablicu                    
                    tableModel.addProizvod(p);
                    txtError.setText("Proizvod: " + naziv + " " + pdv + " " + pnp + " promijenjen.");
                    txtNazivProizvoda.requestFocus();
                }
                else {
                    txtError.setText("Proizvod: " + naziv + " ne postoji. Prvo ga dodajte.");
                }
            } catch (NumberFormatException e) {
                txtError.setText("Pogresan unos! Za cijenu, pdv i pnp unesite broj odvojen tockom. Npr. 10.99");
            }
        }

    }//GEN-LAST:event_cmdPromijeniActionPerformed

    private void txtNazivProizvodaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNazivProizvodaFocusGained
        txtError.setText("");
        selText(txtNazivProizvoda);
    }//GEN-LAST:event_txtNazivProizvodaFocusGained

    private void txtCijenaProizvodaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCijenaProizvodaFocusGained
        txtError.setText("");
        selText(txtCijenaProizvoda);
    }//GEN-LAST:event_txtCijenaProizvodaFocusGained

    private void txtPDVFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPDVFocusGained
        txtError.setText("");
        selText(txtPDV);
    }//GEN-LAST:event_txtPDVFocusGained

    private void txtPNPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPNPFocusGained
        txtError.setText("");
        selText(txtPNP);
    }//GEN-LAST:event_txtPNPFocusGained

    private void txtNazivProizvodaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNazivProizvodaKeyTyped

//        if ((evt.getKeyChar() < 'A' || evt.getKeyChar() > 'z') 
//               && (evt.getKeyChar() < '0' || evt.getKeyChar() > '9')
//               && evt.getKeyChar() != '.'
//               && evt.getKeyChar() != '\'' 
//               && evt.getKeyChar() != '-' 
//               && evt.getKeyChar() != ' ' 
//               && evt.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
//            txtError.setText("Pokušaj unosa nedozvoljenog znaka " + evt.getKeyChar() + "\n Dozvoljeni znakovi: A-Z a-z -");
//        } else {
        txtError.setText("");
//        }

    }//GEN-LAST:event_txtNazivProizvodaKeyTyped

    private void txtCijenaProizvodaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCijenaProizvodaKeyTyped
        if ((evt.getKeyChar() < '0' || evt.getKeyChar() > '9')
                && evt.getKeyChar() != '.' && evt.getKeyChar() != ' ' && evt.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
            txtError.setText("Pokusaj unosa nedozvoljenog znaka " + evt.getKeyChar() + "\n Dozvoljeni znakovi: 0-9 .");
        } else {
            txtError.setText("");
        }
    }//GEN-LAST:event_txtCijenaProizvodaKeyTyped

    private void txtPDVKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPDVKeyTyped
        if ((evt.getKeyChar() < '0' || evt.getKeyChar() > '9')
                && evt.getKeyChar() != '.' && evt.getKeyChar() != ' ' && evt.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
            txtError.setText("Pokusaj unosa nedozvoljenog znaka " + evt.getKeyChar() + "\n Dozvoljeni znakovi: 0-9 .");
        } else {
            txtError.setText("");
        }
    }//GEN-LAST:event_txtPDVKeyTyped

    private void txtPNPKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPNPKeyTyped
        // TODO add your handling code here:
        if ((evt.getKeyChar() < '0' || evt.getKeyChar() > '9')
                && evt.getKeyChar() != '.' && evt.getKeyChar() != ' ' && evt.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
            txtError.setText("Pokusaj unosa nedozvoljenog znaka " + evt.getKeyChar() + "\n Dozvoljeni znakovi: 0-9 .");
        } else {
            txtError.setText("");
        }
    }//GEN-LAST:event_txtPNPKeyTyped

    private void cmdBrisiProizvodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBrisiProizvodActionPerformed
        // TODO add your handling code here:
        int i = tblProizvod.getSelectedRowCount();
        if (i == 0) {
            txtError.setText("Prvo oznacite jedan od proizvada iz tablice!");
        } else {
            int viewRow = tblProizvod.getSelectedRow();
            int modelRow = tblProizvod.convertRowIndexToModel(viewRow);
            tableModel.removeDataObject(modelRow);
            txtSearch.setText("");
            newFilter();
            
            i = viewRow;
            // pokusaj selektirate sljedeci redak
            if(tblProizvod.getRowCount() > i) {
                tblProizvod.setRowSelectionInterval(i, i);
            }
            else {
                // ili zadnji
                if(tblProizvod.getRowCount() > 0) {
                    tblProizvod.setRowSelectionInterval(tblProizvod.getRowCount() - 1, tblProizvod.getRowCount() - 1);
                }
                else {
                    // ili ako nema redaka, ocisti polja
                    txtNazivProizvoda.setText("");
                    txtCijenaProizvoda.setText("");
                    txtPDV.setText("");
                    txtPNP.setText("");
                    txtNazivProizvoda.requestFocus();
                }
            }
        }

    }//GEN-LAST:event_cmdBrisiProizvodActionPerformed

    private void tblProizvodFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblProizvodFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_tblProizvodFocusGained

    private void cmdSpremiPromjeneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSpremiPromjeneActionPerformed

        spremiPromjeneCjenik();
        tableModel.deleteProizvodList();
        blagajna.initCjenik("cjenik.txt");
        blagajna.toggleScreen(true);
        this.setVisible(false);

    }//GEN-LAST:event_cmdSpremiPromjeneActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void txtCijenaProizvodaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCijenaProizvodaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCijenaProizvodaActionPerformed

    private void cmdSortirajTablicuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSortirajTablicuActionPerformed
        // TODO add your handling code here:
        if(nameSortReverse){
            nameSortReverse = false;
        }
        else{
            nameSortReverse = true;
        }
        tableModel.sortByName(nameSortReverse);
      
    }//GEN-LAST:event_cmdSortirajTablicuActionPerformed

    private void cmdSortirajTablicuPoCijeniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSortirajTablicuPoCijeniActionPerformed
        // TODO add your handling code here:
        if(priceSortReverse){
            priceSortReverse = false;
        }
        else{
            priceSortReverse = true;
        }
        tableModel.sortByPrice(priceSortReverse);
    }//GEN-LAST:event_cmdSortirajTablicuPoCijeniActionPerformed

    private void cmdDodajProizvodFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdDodajProizvodFocusGained
        cmdDodajProizvod.setBackground(boja2);
    }//GEN-LAST:event_cmdDodajProizvodFocusGained

    private void cmdDodajProizvodFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdDodajProizvodFocusLost
        cmdDodajProizvod.setBackground(boja1);
    }//GEN-LAST:event_cmdDodajProizvodFocusLost

    private void cmdBrisiProizvodFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdBrisiProizvodFocusGained
        cmdBrisiProizvod.setBackground(boja2);
    }//GEN-LAST:event_cmdBrisiProizvodFocusGained

    private void cmdBrisiProizvodFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdBrisiProizvodFocusLost
        cmdBrisiProizvod.setBackground(boja1);
    }//GEN-LAST:event_cmdBrisiProizvodFocusLost

    private void cmdPromijeniFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPromijeniFocusGained
        cmdPromijeni.setBackground(boja2);
    }//GEN-LAST:event_cmdPromijeniFocusGained

    private void cmdPromijeniFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdPromijeniFocusLost
        cmdPromijeni.setBackground(boja1);
    }//GEN-LAST:event_cmdPromijeniFocusLost

    private void cmdSortirajTablicuFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdSortirajTablicuFocusGained
        cmdSortirajTablicu.setBackground(boja2);
    }//GEN-LAST:event_cmdSortirajTablicuFocusGained

    private void cmdSortirajTablicuFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdSortirajTablicuFocusLost
        cmdSortirajTablicu.setBackground(boja1);
    }//GEN-LAST:event_cmdSortirajTablicuFocusLost

    private void cmdSortirajTablicuPoCijeniFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdSortirajTablicuPoCijeniFocusGained
        cmdSortirajTablicuPoCijeni.setBackground(boja2);
    }//GEN-LAST:event_cmdSortirajTablicuPoCijeniFocusGained

    private void cmdSortirajTablicuPoCijeniFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdSortirajTablicuPoCijeniFocusLost
        cmdSortirajTablicuPoCijeni.setBackground(boja1);
    }//GEN-LAST:event_cmdSortirajTablicuPoCijeniFocusLost

    private void cmdSpremiPromjeneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdSpremiPromjeneFocusGained
        cmdSpremiPromjene.setBackground(boja2);
    }//GEN-LAST:event_cmdSpremiPromjeneFocusGained

    private void cmdSpremiPromjeneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdSpremiPromjeneFocusLost
        cmdSpremiPromjene.setBackground(boja1);
    }//GEN-LAST:event_cmdSpremiPromjeneFocusLost

    private void cmdIzlazFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIzlazFocusGained
        cmdIzlaz.setBackground(boja2);
    }//GEN-LAST:event_cmdIzlazFocusGained

    private void cmdIzlazFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmdIzlazFocusLost
        cmdIzlaz.setBackground(boja1);
    }//GEN-LAST:event_cmdIzlazFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdBrisiProizvod;
    private javax.swing.JButton cmdDodajProizvod;
    private javax.swing.JButton cmdIzlaz;
    private javax.swing.JButton cmdPromijeni;
    private javax.swing.JButton cmdSortirajTablicu;
    private javax.swing.JButton cmdSortirajTablicuPoCijeni;
    private javax.swing.JButton cmdSpremiPromjene;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnlIzbornik;
    private javax.swing.JPanel pnlProizvod;
    private javax.swing.JPanel pnlUnos;
    private javax.swing.JTable tblProizvod;
    private javax.swing.JTextField txtCijenaProizvoda;
    private javax.swing.JTextField txtError;
    private javax.swing.JTextField txtNazivProizvoda;
    private javax.swing.JTextField txtPDV;
    private javax.swing.JTextField txtPNP;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

    /**
     * Ucitavanje cjenika iz datoteke
     */
    public void initCjenik() {

        try {
            c1 = new Cjenik();
            // ucitaj cjenik iz datoteke
            c1.loadCjenik("cjenik.txt");
            // napuni tablicu sa proizvodima
            for (int i = 0; i < c1.getSize(); i++) {
                tableModel.addProizvod(c1.getProizvodi().get(i));
            }

            proizvodi = new ArrayList<String>();
            proizvodiMap = new HashMap<String, Proizvod>();
            for (Object o : tableModel.getData()) {
                Proizvod p = (Proizvod) o;
                proizvodi.add(p.getNaziv());
                proizvodiMap.put(p.getNaziv(), p);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "initCjenik()", e);
        }
    }

    /**
     * Spremanje promjena u datoteku
     */
    private void spremiPromjeneCjenik() {

        try {
            if (c1 != null) {

                // pridjeli podatke iz tablice u cjenik c1 objekt
                c1.setProizvodi(tableModel.getData());
                // pozovu funkciju spremanja
                c1.saveCjenik("cjenik.txt");

                txtError.setText("Promjene spremljene u cjenik.txt datoteku.");

            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "spremiPromjeneCjenik()", e);
        }

    }

    /**
     * Azurira filter za pretrazivanje resetke
     *
     */
    private void newFilter() {
        RowFilter<RacunJTableDataModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter("^" + txtSearch.getText(), 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }

    /**
     * Inicijalizacija filtera za brzo pretrazivanje proizvoda u resetki
     */
    private void initSearch() {
        // inicijaliziraj sorter
//        sorter = new TableRowSorter<CustomJTableDataModel>(tableModel);
//        tblProizvod.setRowSorter(sorter);
//    Whenever filterText changes, invoke newFilter.
        txtSearch.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        newFilter();
                    }

                    public void insertUpdate(DocumentEvent e) {
                        newFilter();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        newFilter();
                    }
                });
    }

    /**
     * Metoda za prikaz naziva, cijene, pdv-a, pnp-a odabranog proizvoda iz
     * resetke
     *
     * @param e odabrani proizvod
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == tblProizvod.getSelectionModel() && tblProizvod.getRowSelectionAllowed()) {
            selectedRow = e.getFirstIndex();
            int i = tblProizvod.getSelectedRowCount();
            if (i > 0) {
                int viewRow = tblProizvod.getSelectedRow();
                int modelRow = tblProizvod.convertRowIndexToModel(viewRow);

                DecimalFormat dFormat = new DecimalFormat("0.00");

                Proizvod p = tableModel.getDataObject(modelRow);

                txtNazivProizvoda.setText(p.getNaziv());
                txtCijenaProizvoda.setText(dFormat.format(p.getCijena()));
                txtPDV.setText(dFormat.format(p.getPdv() * 100));
                txtPNP.setText(dFormat.format(p.getPnp() * 100));
            }
        }
        if (e.getValueIsAdjusting()) {
            //System.out.println("The mouse button has not yet been released");
        }
    }

    /**
     * Korisnicko sucelje postavlja.<BR>
     */   
    public void ui(){
        // UI
        
        // referenca na izgled objekt
        izgled = blagajna.izgled;    
        
        // Panel sa dugmicima
        for (Component comp : pnlIzbornik.getComponents()) {
            if (comp instanceof JButton) {
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));

                //comp.setMinimumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                //comp.setMaximumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                comp.setPreferredSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));

            }
        }
        // Panel sa proizvodima
        for (Component comp : pnlProizvod.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
                
                //comp.setMinimumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                //comp.setMaximumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                comp.setPreferredSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));

                

            }
        }
        // Panel sa dugmicima i text poljima
        for (Component comp : pnlUnos.getComponents()) {
            if (comp instanceof JButton || comp instanceof JTextField) {
                                
                comp.setFont(new Font(izgled.getFontNaziv(), Font.PLAIN, izgled.getFontVelicina()));
                
                //comp.setMinimumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                //comp.setMaximumSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                comp.setPreferredSize(new Dimension(comp.getWidth(), izgled.getDebljinaDugmica()));
                
            }
        }        

        // Za jTable objekte, pridjeli custom renderer za tablicu
        tblProizvod.setDefaultRenderer(
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
        tblProizvod.repaint();

    }    
}
