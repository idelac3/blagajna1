/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blagajna;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 * <B>Ispis</B><BR>
 * <BR>
 * Ispis teksta na pisac.<BR>
 * <BR>
 * Prije samog ispisa sa <I>print()</I> funkcijom, treba postaviti sve potrebne
 * parametre:<BR>
 * <UL>naziv zadatka, pisac, papir, margine, ispisnu povrsinu</UL>
 * Moguce je jos postaviti i print dialog koji ce se prikazati korisniku prije ispisa.<BR>
 * <BR>
 * <B>Primjer ispisa</B><BR>
 * <UL>Ispis ispis = new Ispis(nazivZadatka.replace('.', '-'), postavke.getPisac(),<BR>
 * postavke.getPapirX(), postavke.getPapirY(),<BR>
 * postavke.getMarginaX(), postavke.getMarginaY(),<BR>
 * postavke.getIspisnaPovrsinaX(), postavke.getIspisnaPovrsinaY());<BR>
 * <BR>
 * try {<BR>
 * ispis.print(txtPoruka.getPrintable(null, null));<BR>
 * } catch (PrinterException ex) {<BR>
 * logger.log(Level.SEVERE, "cmdIspisiUkupno()", ex);<BR>
 * }</UL>
 * 
 *
 * @author eigorde
 */
public class Ispis {

    private String nazivZadatka;
    private String pisac;
    private double papirX, papirY;
    private double marginaX, marginaY;
    private double ispisnaPovrsinaX, ispisnaPovrsinaY;
    private boolean printDialog;

    /**
     * Postavi pocetne vr. varijablama.
     */
    private void init() {
        nazivZadatka = null;
        pisac = null;
        papirX = 0.00;
        papirY = 0.00;
        marginaX = 0.00;
        marginaY = 0.00;
        ispisnaPovrsinaX = 0.00;
        ispisnaPovrsinaY = 0.00;
        printDialog = false;
    }

    /**
     * <B>Ispis</B><BR> Za ispravan ispis treba postaviti naziv zadatka, ime
     * pisaca, velicinu papira, margine i ispisnu povrsinu.<BR> Parametre
     * postavite rucno.<BR>
     *
     */
    public Ispis() {
        init();
    }

    /**
     * <B>Ispis</B><BR> Za ispravan ispis treba postaviti naziv zadatka, ime
     * pisaca, velicinu papira, margine i ispisnu povrsinu.<BR> Parametre
     * postavite rucno.<BR>
     *
     * @param nazivZadatka naziv pod kojim ce se ispisni zadatak pojaviti u
     * <I>Print Spooleru</I>
     */
    public Ispis(String nazivZadatka) {
        init();
        this.nazivZadatka = nazivZadatka;
    }

    /**
     * <B>Ispis</B><BR> Za ispravan ispis treba postaviti naziv zadatka, ime
     * pisaca, velicinu papira, margine i ispisnu povrsinu.<BR> Parametre
     * postavite rucno.<BR>
     *
     * @param nazivZadatka naziv pod kojim ce se ispisni zadatak pojaviti u
     * <I>Print Spooleru</I>
     * @param pisac naziv zeljenog pisaca koji ce ispisati tekst
     */
    public Ispis(String nazivZadatka, String pisac) {
        init();
        this.nazivZadatka = nazivZadatka;
        this.pisac = pisac;
    }

    /**
     * <B>Ispis</B><BR> Za ispravan ispis treba postaviti naziv zadatka, ime
     * pisaca, velicinu papira, margine i ispisnu povrsinu.<BR> Parametre
     * postavite rucno.<BR> <BR> Naziv zadatka i pisac postavite rucno.<BR>
     * Mjerna jedinica: mm<BR>
     *
     * @param papirX sirina papira (npr. za A4 je 210 mm)
     * @param papirY duljina/visina papira (npr. za A4 je 297 mm)
     * @param maringaX lijeva margina
     * @param marginaY gornja margina
     * @param ispisnaPovrsinaX sirina ispisne povrsine
     * @param ispisnaPovrsinaY duljina ispisne povrsine
     */
    public Ispis(double papirX, double papirY, double marginaX, double marginaY, double ispisnaPovrsinaX, double ispisnaPovrsinaY) {
        init();
        this.papirX = papirX;
        this.papirY = papirY;
        this.marginaX = marginaX;
        this.marginaY = marginaY;
        this.ispisnaPovrsinaX = ispisnaPovrsinaX;
        this.ispisnaPovrsinaY = ispisnaPovrsinaY;
    }

    /**
     * <B>Ispis</B><BR> Za ispravan ispis treba postaviti naziv zadatka, ime
     * pisaca, velicinu papira, margine i ispisnu povrsinu.<BR> Parametre
     * postavite rucno.<BR> <BR> Mjerna jedinica: mm<BR>
     *
     * @param nazivZadatka naziv pod kojim ce se ispisni zadatak pojaviti u
     * <I>Print Spooleru</I>
     * @param pisac naziv zeljenog pisaca koji ce ispisati tekst
     * @param papirX sirina papira (npr. za A4 je 210 mm)
     * @param papirY duljina/visina papira (npr. za A4 je 297 mm)
     * @param maringaX lijeva margina
     * @param marginaY gornja margina
     * @param ispisnaPovrsinaX sirina ispisne povrsine
     * @param ispisnaPovrsinaY duljina ispisne povrsine
     */
    public Ispis(String nazivZadatka, String pisac, double papirX, double papirY, double marginaX, double marginaY, double ispisnaPovrsinaX, double ispisnaPovrsinaY) {
        init();
        this.nazivZadatka = nazivZadatka;
        this.pisac = pisac;
        this.papirX = papirX;
        this.papirY = papirY;
        this.marginaX = marginaX;
        this.marginaY = marginaY;
        this.ispisnaPovrsinaX = ispisnaPovrsinaX;
        this.ispisnaPovrsinaY = ispisnaPovrsinaY;
    }

    /**
     * Naziv zadatka je obvezan podatak i ako nije postavljen ispis nije
     * moguc.<BR>
     *
     * @param nazivZadatka naziv pod kojim ce se u <I>Print Spooler</I> poslati
     * tekst na ispis
     */
    public void setNazivZadatka(String nazivZadatka) {
        this.nazivZadatka = nazivZadatka;
    }

    /**
     * Pisac koji ce ispisati tekst.
     *
     * @param pisac ime pisaca instaliranog u sistemu
     */
    public void setPisac(String pisac) {
        this.pisac = pisac;
    }

    /**
     * Sirina papira.
     *
     * @param papirX mm
     */
    public void setPapirX(double papirX) {
        this.papirX = papirX;
    }

    /**
     * Visina papira.
     *
     * @param papirY mm
     */
    public void setPapirY(double papirY) {
        this.papirY = papirY;
    }

    /**
     * Lijeva margina.
     *
     * @param marginaX mm
     */
    public void setMarginaX(double marginaX) {
        this.marginaX = marginaX;
    }

    /**
     * Gornja margina.
     *
     * @param marginaY mm
     */
    public void setMarginaY(double marginaY) {
        this.marginaY = marginaY;
    }

    /**
     * Ispisna povrsina, sirina.
     *
     * @param ispisnaPovrsinaX mm
     */
    public void setIspisnaPovrsinaX(double ispisnaPovrsinaX) {
        this.ispisnaPovrsinaX = ispisnaPovrsinaX;
    }

    /**
     * Ispisna povrsina, visina.
     *
     * @param ispisnaPovrsinaY mm
     */
    public void setIspisnaPovrsinaY(double ispisnaPovrsinaY) {
        this.ispisnaPovrsinaY = ispisnaPovrsinaY;
    }

    /**
     * Oznaka da li ce se prikazati prozor za ispis prije samog ispisa.<BR> Ako
     * nije postavljena, tada nece.
     *
     * @param printDialog <I>true</I> za prikaz print dialoga
     */
    public void setPrintDialog(boolean printDialog) {
        this.printDialog = printDialog;
    }

    /**
     * Ispis na pisac.<BR> Prije poziva postavite potrebne parametre (naziv,
     * pisac, papir, margine ...). <BR>
     *
     * @param pr Printable objekt, dohvatiti preko getPrintable(..) poziva
     * @throws PrinterException
     */
    public void print(Printable pr) throws PrinterException {
        
        /**
         * Provjera imena zadatka i pisaca.
         */
        if (nazivZadatka == null || pisac == null) {
            throw new PrinterException();
        }
        
        /**
         * Print lista zadataka, ime.
         */
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (nazivZadatka != null) {
            printerJob.setJobName(nazivZadatka);
        }

        /**
         * Postavi printer.
         */
        PrintService odabraniPisac = PrintServiceLookup.lookupDefaultPrintService();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService p : printServices) {
            if (pisac.equalsIgnoreCase(p.getName())) {
                odabraniPisac = p;
            }
        }

        printerJob.setPrintService(odabraniPisac);

        /**
         * Postavljanje velicine papira.
         */
        //Paper paper = new Paper();
        Paper paper = printerJob.defaultPage().getPaper();

        paper.setSize(papirX, papirY);
        paper.setImageableArea(marginaX,
                marginaY,
                ispisnaPovrsinaX,
                ispisnaPovrsinaY);

        /**
         * Povezivanje na txtRacun objekt.
         */
        PageFormat pageFormat = printerJob.defaultPage();
        pageFormat.setOrientation(PageFormat.PORTRAIT);
        pageFormat.setPaper(paper);
        printerJob.defaultPage(pageFormat);

        printerJob.setPrintable(pr, printerJob.validatePage(pageFormat));

        /**
         * Ispis.
         */
        if (printDialog) {
            if (printerJob.printDialog()) {
                printerJob.print();
            }
        } else {
            printerJob.print();
        }
    }
}
