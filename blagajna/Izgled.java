/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blagajna;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * UI korisnicko sucelje, font, velicina, raspored, ...<BR>
 * Trenutno sluzi za pohranu varijabli za izgled korisnickog sucelja.
 * @author eigorde
 */
public class Izgled {
    
    private String fontNaziv;
    private int fontVelicina;
    private int debljinaDugmica;

    /**
     * Definira <B>Izgled</B> objekt sa pocetnim vrijednostima.
     * @param fontNaziv ime fonta
     * @param fontVelicina velicina fonta
     * @param debljinaDugmica debljina dugmica
     */
    public Izgled(String fontNaziv, int fontVelicina, int debljinaDugmica) {
        this.fontNaziv = fontNaziv;
        this.fontVelicina = fontVelicina;
        this.debljinaDugmica = debljinaDugmica;
    }

    
    /**
     * Font za prikaz.
     * @return ime fonta
     */
    public String getFontNaziv() {
        return fontNaziv;
    }

    /**
     * Postavi novi font.
     * @param fontNaziv ime fonta
     */
    public void setFontNaziv(String fontNaziv) {
        this.fontNaziv = fontNaziv;
    }

    /**
     * Velicina fonta.
     * @return velicina fonta
     */
    public int getFontVelicina() {
        return fontVelicina;
    }

    /**
     * Postavi velicinu fonta.
     * @param fontVelicina velicina fonta
     */
    public void setFontVelicina(int fontVelicina) {
        this.fontVelicina = fontVelicina;
    }

    /**
     * Debljina dugmica (JButton).
     * @return debljina (vrijednost se krece oko 35 pix.)
     */
    public int getDebljinaDugmica() {
        return debljinaDugmica;
    }

    /**
     * Debljina dugmica (JButton).
     * @param debljinaDugmica debljina (vrijednost se krece oko 35 pix.)
     */
    public void setDebljinaDugmica(int debljinaDugmica) {
        this.debljinaDugmica = debljinaDugmica;
    }
    
    /**
     * Sprema izgled u datoteku
     *
     * @deprecated Ne koristiti vise stari nacin ucitavanja postavki za izgled
     * @param fileName naziv datoteke gdje ce se izgled spremiti
     */
    public void spremiIzgled(String fileName) throws FileNotFoundException, IOException {
        
        Properties izgled = new Properties();

        // preslikaj postavke iz lokalnih varijabli
        izgled.setProperty("fontNaziv", String.valueOf(fontNaziv));
        izgled.setProperty("fontVelicina", String.valueOf(fontVelicina));
        izgled.setProperty("debljinaDugmica", String.valueOf(debljinaDugmica));


        // postavi datoteku za pisanje
        OutputStream os = new FileOutputStream(fileName);
        izgled.store(os, "Postavke");
        os.close();

    }
    
      
    /**
     * Ucita izgled u datoteku
     *
     * @deprecated Ne koristiti vise stari nacin ucitavanja postavki za izgled
     * @param fileName naziv datoteke za ucitati izgled 
     */
    public void ucitajIzgled(String fileName) throws FileNotFoundException, IOException {
        
        Properties izgled = new Properties();

        // postavi datoteku za citanje
        InputStream is = new FileInputStream(fileName);
        izgled.load(is);
        is.close();

        // preslikaj postavke iz lokalnih varijabli
        fontNaziv = izgled.getProperty("fontNaziv");
        fontVelicina = Integer.parseInt(izgled.getProperty("fontVelicina"));
        debljinaDugmica = Integer.parseInt(izgled.getProperty("debljinaDugmica"));

    }
    
}
