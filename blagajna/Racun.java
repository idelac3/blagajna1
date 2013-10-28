package blagajna;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author root
 */
public class Racun {

    private List<Proizvod> proizvodi;
    private double ukupanIznos;

    /**
     * <B>Racun</B><BR>
     * Inicijalizacija instance.
     */
    public Racun() {
        proizvodi = new ArrayList<Proizvod>();
    }

    /**
     * <B>Proizvod, dodaj</B><BR>
     * Dodaje proizvod na popis.
     *
     * @param p Proizvod tip podatka
     */
    public void addProizvod(Proizvod p) {
        proizvodi.add(p);
        updateUkupanIznos();
    }

    /**
     * <B>Proizvod, ukloni</B><BR>
     * Uklanja proizvod sa popisa.
     *
     * @param p Proizvod tip podatka
     */
    public void removeProizvod(Proizvod p) {
        proizvodi.remove(p);
        updateUkupanIznos();
    }

    /**
     * <B>Lista proizvoda</B><BR>
     * Popis proizvoda na racunu.
     *
     * @return List<Proizvod> referenca na <I>ArrayList</I> objekt koji sadrzi popis
     */
    public List<Proizvod> getProizvodi() {
        return proizvodi;
    }

    /**
     * <B>Ukupan iznos</B><BR>
     * Ukupan iznos na racunu.
     *
     * @return double tip vrijednosti
     */
    public double getUkupanIznos() {
        return ukupanIznos;
    }

    /**
     * <B>Ukupan iznos</B><BR>
     * Ukupan iznos na racunu.
     *
     * @return String tip vrijednosti, formata 0.00, bez KN oznake
     */
    public String getUkupanIznosIspis() {
        DecimalFormat dFormat = new DecimalFormat("0.00");
        return dFormat.format(ukupanIznos);
    }

    /**
     * Azurira ukupan iznos na racunu za svako dodavanje/uklanjanje proizvoda sa
     * racuna
     */
    private void updateUkupanIznos() {
        ukupanIznos = 0.0;
        for (Proizvod proizvod : proizvodi) {
            ukupanIznos = ukupanIznos + proizvod.getCijena();
        }
    }
    
    /**
     * <B>Kolicina proizvoda</B><BR>
     * Koliko je puta neki proizvod dodan na racun.
     * @param proizvod referenca na Proizvod objekt
     * @return broj istih proizvoda na racunu
     */
    public int getProizvodKolicina(Proizvod proizvod) {
        int kolicina = 0;
        for (Proizvod p : proizvodi) {
            if (p.equals(proizvod)) {
                kolicina++;
            }
        }
        return kolicina;
    }
}
