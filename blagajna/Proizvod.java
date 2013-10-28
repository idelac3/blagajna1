package blagajna;


import java.text.DecimalFormat;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author root
 */
public class Proizvod {

    private String naziv;
    private double cijena;
    private double pdv;
    private double pnp;
    private double osnovica;
    private double iznosPdv;
    private double iznosPnp;
    private String cijenaIspis;
    private String pdvIspis;
    private String pnpIspis;
    private String valuta = "KN";
    
    private String iznosPdvIspisBezPostotnogZnaka;
    private String iznosPnpIspisBezPostotnogZnaka;     
    private String iznosCijenaIspisBezKN;
    
    /**
     * Konsturktor za Proizvod
     *
     * @param naziv naziv proizvoda
     * @param cijena cijena proizvoda
     * @param pdv porez na dodanu vrijednost
     * @param pnp porez na potrosnju
     */
    public Proizvod(String naziv, double cijena, double pdv, double pnp) {
        initProizvod(naziv, cijena, pdv, pnp);
    }

    /**
     * Izracuna ostale vrijednosti iz cijene, pdv-a i pnp-a Npr. cijenu za
     * ispis, pdv i pnp za ispis, osnovicu, itd.
     *
     * @param naziv naziv proizvoda
     * @param cijena cijena proizvoda
     * @param pdv porez na dodanu vrijednost
     * @param pnp porez na potrosnju
     */
    public void initProizvod(String naziv, double cijena, double pdv, double pnp) {

        DecimalFormat dFormat = new DecimalFormat("0.00");

        this.naziv = naziv;
        this.cijena = cijena;
        this.pdv = pdv;
        this.pnp = pnp;

        this.cijenaIspis = dFormat.format(cijena) + " " + valuta;
        this.pdvIspis = dFormat.format(pdv * 100) + " %";
        this.pnpIspis = dFormat.format(pnp * 100) + " %";
        
        this.iznosPdvIspisBezPostotnogZnaka = dFormat.format(pdv * 100);
        this.iznosPnpIspisBezPostotnogZnaka = dFormat.format(pnp * 100);
        this.iznosCijenaIspisBezKN = dFormat.format(cijena);
                
        izracunajOsnovicu();
        izracunajIznosPdv();
        izracunajIznosPnp();
    }

    /**
     * Naziv proizvoda ili usluge.
     * @return naziv
     */
    public String getNaziv() {
        return naziv;
    }

    /**
     * Cijena proizvoda ili usluge.
     * @return cijena
     */
    public double getCijena() {
        return cijena;
    }

    /**
     * Pdv na proizvod ili uslugu.<BR>
     * Npr. <I>0.25</I> za 25%.
     * @return pdv
     */
    public double getPdv() {
        return pdv;
    }

    /**
     * Pnp na proizvod ili uslugu.<BR>
     * Npr. <I>0.03</I> za 3%.
     * @return pnp
     */
    public double getPnp() {
        return pnp;
    }

    /**
     * Osnovica proizvoda ili usluge.<BR>
     * Racuna se kao: <BR>
     * <I>Osnovica = Cijena / ( 100% + PDV (%) + PNP (%) )</I>
     * @return osnovica
     */
    public double getOsnovica() {
        return osnovica;
    }

    /**
     * Iznos pdv-a na proizvod ili uslugu.<BR>
     * <I>iznos pdv-a = osnovica * pdv</I><BR>
     * Npr. cijena = 100kn, pdv = 0.25, osnovica = 80kn, iznos pdv-a = 20 kn.<BR>
     * @return iznos pdv-a
     */
    public double getIznosPdv() {
        return iznosPdv;
    }
    /**
     * Iznos pnp-a na proizvod ili uslugu.<BR>
     * <I>iznos pdv-a = osnovica * pdv</I><BR>
     * Npr. cijena =  99kn, pnp = 0.10, osnovica = 90kn, iznos pnp-a =  9 kn.<BR>
     * @return iznos pnp-a
     */
    public double getIznosPnp() {
        return iznosPnp;
    }

    /**
     * Naziv proizvoda ili usluge.
     * @param naziv 
     */
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    /**
     * Maloprodajna cijena proizvoda ili usluge.<BR>
     * Osnovica, iznosi pdv-a i pnp-a se izracunavaju iz cijene.<BR>
     * @param cijena maloprodajna cijena
     */
    public void setCijena(double cijena) {

        this.cijena = cijena;

        DecimalFormat dFormat = new DecimalFormat("0.00");

        this.cijenaIspis = dFormat.format(cijena) + " " + valuta;
        this.iznosCijenaIspisBezKN = dFormat.format(cijena);
        
        izracunajOsnovicu();
        izracunajIznosPdv();
        izracunajIznosPnp();

    }

    /**
     * Pdv u decimalnom formatu, ne postotku.
     * @param pdv decimalni format, npr. <I>0.25</I> za <I>25%</I>
     */
    public void setPdv(double pdv) {

        this.pdv = pdv;


        DecimalFormat dFormat = new DecimalFormat("0.00");

        this.pdvIspis = dFormat.format(pdv * 100) + " %";
        this.iznosPdvIspisBezPostotnogZnaka = dFormat.format(pdv * 100);
        
        izracunajOsnovicu();
        izracunajIznosPdv();
        izracunajIznosPnp();

    }
    /**
     * Pnp u decimalnom formatu, ne postotku.
     * @param pnp decimalni format, npr. <I>0.03</I> za <I>3%</I>
     */
    public void setPnp(double pnp) {

        this.pnp = pnp;

        DecimalFormat dFormat = new DecimalFormat("0.00");

        this.pnpIspis = dFormat.format(pnp * 100) + " %";
        this.iznosPnpIspisBezPostotnogZnaka = dFormat.format(pnp * 100);

        izracunajOsnovicu();
        izracunajIznosPdv();
        izracunajIznosPnp();

    }

    /**
     * Da li su dva <I>Proizvod</I> objekta jednaka ?
     * @param aThat Proizvod objekt
     * @return <I>true</I> ako su jednaka, inace <I>false</I>
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
        if (!(aThat instanceof Proizvod)) {
            return false;
        }
        //Alternative to the above line :
        //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;

        //cast to native object is now safe
        Proizvod that = (Proizvod) aThat;

        //now a proper field-by-field evaluation can be made
        return this.pdv == that.pdv
                && this.naziv.equals(that.naziv)
                && this.cijena == that.cijena
                && this.pnp == that.pnp;
    }

    /**
     * Izracun hash vr. za objekt <B>Proizvod</B>, radi postavljanja ID-a.<BR>
     * @return hashCode
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.naziv != null ? this.naziv.hashCode() : 0);
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.cijena) ^ (Double.doubleToLongBits(this.cijena) >>> 32));
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.pdv) ^ (Double.doubleToLongBits(this.pdv) >>> 32));
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.pnp) ^ (Double.doubleToLongBits(this.pnp) >>> 32));
        return hash;
    }

    /**
     * Da li su dva <I>Proizvod</I> objekta jednaka samo po nazivu ?
     * @param aThat Proizvod objekt
     * @return <I>true</I> ako su jednakog naziva, inace <I>false</I>
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
        if (!(aThat instanceof Proizvod)) {
            return false;
        }
        //Alternative to the above line :
        //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;

        //cast to native object is now safe
        Proizvod that = (Proizvod) aThat;

        //now a proper field-by-field evaluation can be made
        return this.naziv.equals(that.naziv);
    }

    /**
     * Izracun osnovice iz cijene, pdv-a i pnp-a.
     * <I>Osnovica = Cijena / ( 100% + PDV (%) + PNP (%) )</I>
     */
    private void izracunajOsnovicu() {
        osnovica = cijena / (1 + (pdv + pnp));
    }

    /**
     * Izracuna iznosa pdv-a iz cijene i pdv-a.
     */
    private void izracunajIznosPdv() {
        iznosPdv = osnovica * pdv;
    }
    /**
     * Izracuna iznosa pnp-a iz cijene i pnp-a.
     */
    private void izracunajIznosPnp() {
        iznosPnp = osnovica * pnp;
    }

    /**
     * Cijena za ispis/prikaz.<BR>
     * Npr. <I>100 kn</I>
     * 
     * @return cijena zs ispis
     */
    public String getCijenaIspis() {
        return cijenaIspis;
    }

    /**
     * Pdv za ispis.<BR>
     * Npr. <I>25 %</I>
     * @return pdv ispis
     */
    public String getPdvIspis() {
        return pdvIspis;
    }

    /**
     * Pnp za ispis.<BR>
     * Npr. <I>3 %</I>
     * @return pnp ispis
     */
    public String getPnpIspis() {
        return pnpIspis;
    }

    /**
     * Valuta za ispis.<BR>
     * Npr. <I>kn</I>
     * @return valuta
     */
    public String getValuta() {
        return valuta;
    }
    /**
     * Postavlja valutu.<BR>
     * Npr. <I>kn</I>
     * @param valuta
     */
    public void setValuta(String valuta) {
        this.valuta = valuta;
    }

    /**
     * Objekt tipa <B>Proizvod</B> konvertira u <B>String</B> tip.<BR>
     * @return naziv + cijena. Npr <I>001 Proizvod1 15 KN</I>
     */
    @Override
    public String toString() {
        return naziv + " " + cijenaIspis;
    }

    /**
     * Pdv za ispis bez % znaka ili valute.<BR>
     * Npr. <I>3</I>
     * @return iznos pdv-a
     */
    public String getIznosPdvIspisBezPostotnogZnaka() {
        return iznosPdvIspisBezPostotnogZnaka;
    }
    /**
     * Pnp-a za ispis bez % znaka ili valute.<BR>
     * Npr. <I>35</I>
     * @return iznos pnp-a
     */
    public String getIznosPnpIspisBezPostotnogZnaka() {
        return iznosPnpIspisBezPostotnogZnaka;
    }

    /**
     * Cijena za ispis bez valute.<BR>
     * Npr. <I>100</I>
     * @return iznos cijene
     */
    public String getIznosCijenaIspisBezKN() {
        return iznosCijenaIspisBezKN;
    }
    
    
}
