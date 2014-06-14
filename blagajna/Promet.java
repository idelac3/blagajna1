package blagajna;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author eigorde
 */
public class Promet {

    private int brRac;
    private Date datum;
    private double iznosUkupno;
    private double ukupnoPdv;
    private double ukupnoPnp;
    private boolean storniran;
    private String nacinPlacanja;
    private String oibOper;
    private String zkod;
    private String jir;

    /**
     * Napravi zapis tipa Promet za prikaz na prozoru FrmPromet
     *
     * @param brRac broj rauna
     * @param datum datum izdavanja
     * @param iznosUkupno ukupni iznos
     * @param ukupnoPdv ukupno pdv-a
     * @param ukupnoPnp ukupno pnp-a
     * @param storniran da li je storniran
     * @param nacinPlacanja kako je raun placen
     * @param oibOper oib blagajnika
     * @param zkod zastiti kod
     * @param jir jedinstveni identifikator racuna
     */
    public Promet(int brRac, Date datum, double iznosUkupno,
            double ukupnoPdv, double ukupnoPnp, boolean storniran,
            String nacinPlacanja, String oibOper, String zkod, String jir) {
        this.brRac = brRac;
        this.datum = datum;
        this.iznosUkupno = iznosUkupno;
        this.ukupnoPdv = ukupnoPdv;
        this.ukupnoPnp = ukupnoPnp;
        this.storniran = storniran;
        this.nacinPlacanja = nacinPlacanja;
        this.zkod = zkod;
        this.jir = jir;
        this.oibOper = oibOper;
    }

    /**
     * Broj racuna
     *
     * @return int
     */
    public int getBrRac() {
        return brRac;
    }

    /**
     * Broj racuna
     *
     * @param brRac int
     */
    public void setBrRac(int brRac) {
        this.brRac = brRac;
    }

    /**
     * Datum racuna za prikaz
     *
     * @return u formatu dd.MM.yyyy HH:mm
     */
    public String getDatumIspis() {
        String dateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH).format(datum);
        return dateTime;
    }

    /**
     * Datum racuna
     *
     * @return Date
     */
    public Date getDatum() {
        return datum;
    }

    /**
     * Datum racuna
     *
     * @param datum Date
     */
    public void setDatum(Date datum) {
        this.datum = datum;
    }

    /**
     * Ukupni iznos na racunu za prikaz
     *
     * @return u formatu 0.00 KN
     */
    public String getIznosUkupnoIspis() {
        DecimalFormat dFormat = new DecimalFormat("0.00");
        return dFormat.format(iznosUkupno) + " KN";
    }

    /**
     * Ukupni iznos
     *
     * @return double
     */
    public double getIznosUkupno() {
        return iznosUkupno;
    }

    /**
     * Ukupni iznos
     *
     * @param iznosUkupno double
     */
    public void setIznosUkupno(double iznosUkupno) {
        this.iznosUkupno = iznosUkupno;
    }

    /**
     * Storniran racun za prikaz
     *
     * @return u formatu "STORNIRAN" ako je, inace ""
     */
    public String getStorniranIspis() {
        if (storniran) {
            return "STORNIRAN";
        } else {
            return "";
        }
    }

    /**
     * Ukupno pnp-a za prikaz
     *
     * @return u formatu 0.00 KN
     */
    public String getUkupnoPnpIspis() {
        DecimalFormat dFormat = new DecimalFormat("0.00");
        return dFormat.format(ukupnoPnp) + " KN";
    }

    /**
     * Ukupno pdv-a za prikaz
     *
     * @return u formatu 0.00 KN
     */
    public String getUkupnoPdvIspis() {
        DecimalFormat dFormat = new DecimalFormat("0.00");
        return dFormat.format(ukupnoPdv) + " KN";
    }

    /**
     * Ukupno pdv-a
     *
     * @return double
     */
    public double getUkupnoPdv() {
        return ukupnoPdv;
    }

    /**
     * Ukupno pdv-a
     *
     * @param ukupnoPdv double
     */
    public void setUkupnoPdv(double ukupnoPdv) {
        this.ukupnoPdv = ukupnoPdv;
    }

    /**
     * Ukupno pnp-a
     *
     * @return double
     */
    public double getUkupnoPnp() {
        return ukupnoPnp;
    }

    /**
     * Ukupno pnp-a
     *
     * @param ukupnoPnp double
     */
    public void setUkupnoPnp(double ukupnoPnp) {
        this.ukupnoPnp = ukupnoPnp;
    }

    /**
     * Storniran
     *
     * @return true ako je racun storniran
     */
    public boolean isStorniran() {
        return storniran;
    }

    /**
     * Storniran
     *
     * @param storniran true za storniran racun
     */
    public void setStorniran(boolean storniran) {
        this.storniran = storniran;
    }

    /**
     * Nacin placanja
     *
     * @return kako je racun placen
     */
    public String getNacinPlacanja() {
        return nacinPlacanja;
    }

    /**
     * Nacin placanja
     *
     * @param postavi nacin placanja racuna
     */
    public void setNacinPlacanja(String nacinPlacanja) {
        this.nacinPlacanja = nacinPlacanja;
    }

    /**
     * Zastitni kod
     *
     * @return String
     */
    public String getZkod() {
        return zkod;
    }

    /**
     * Zastitni kod
     *
     * @param zkod String
     */
    public void setZkod(String zkod) {
        this.zkod = zkod;
    }

    /**
     * Jedinstveni identifikator racuna
     *
     * @return String
     */
    public String getJir() {
        return jir;
    }

    /**
     * Jedinstveni identifikator racuna
     *
     * @param jir String
     */
    public void setJir(String jir) {
        this.jir = jir;
    }

    /**
     * OIB blagajnika
     *
     * @return String
     */
    public String getOibOper() {
        return oibOper;
    }

    /**
     * OIB blagajnika
     *
     * @param oibOper String
     */
    public void setOibOper(String oibOper) {
        this.oibOper = oibOper;
    }

    /**
     * Za provjeru jednakosti se uzima broj racuna, datum i ukupni iznos to bi
     * trebalo biti dovoljno da se utvrdi da li su dvije stvke prometa jednake
     * @param aThat Promet objekt
     */
    @Override
    public boolean equals(Object aThat) {
        //check for self-comparison
        if (this == aThat) {
            return true;
        }

        //use instanceof instead of getClass here for two reasons
        //1. if need be, it can match any supertype, and not just one class;
        //2. it renders an explict check for "that == null" redundant, since
        //it does the check for null already - "null instanceof [type]" always
        //returns false. (See Effective Java by Joshua Bloch.)
        if (!(aThat instanceof Promet)) {
            return false;
        }
        //Alternative to the above line :
        //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;

        //cast to native object is now safe
        Promet that = (Promet) aThat;

        //now a proper field-by-field evaluation can be made
        return this.brRac == that.brRac
                && this.datum.equals(that.datum)
                && this.iznosUkupno == that.iznosUkupno;

    }

    /**
     * Za izracuna hash-a se uzima broj racuna, datum i ukupni iznos
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.datum != null ? this.datum.hashCode() : 0);
        hash = 43 * hash + (int) (this.brRac ^ ((this.brRac) >>> 16));
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.iznosUkupno) ^ (Double.doubleToLongBits(this.iznosUkupno) >>> 32));
        return hash;
    }

    /**
     * Ovdje equalsByName znaci zapravo po broju racuna
     * usporedba da li su dvije stavke prometa jednake
     * @param aThat Promet objekt
     * @return true ako dvije stavke Promet tipa imaju isti broj racuna
     */
    public boolean equalsByName(Object aThat) {
        //check for self-comparison
        if (this == aThat) {
            return true;
        }

        //use instanceof instead of getClass here for two reasons
        //1. if need be, it can match any supertype, and not just one class;
        //2. it renders an explict check for "that == null" redundant, since
        //it does the check for null already - "null instanceof [type]" always
        //returns false. (See Effective Java by Joshua Bloch.)
        if (!(aThat instanceof Promet)) {
            return false;
        }
        //Alternative to the above line :
        //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;

        //cast to native object is now safe
        Promet that = (Promet) aThat;

        //now a proper field-by-field evaluation can be made
        return this.brRac == (that.brRac);
    }
}
