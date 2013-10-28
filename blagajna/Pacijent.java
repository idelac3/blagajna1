/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blagajna;

/**
 *
 * @author eigorde
 */
public class Pacijent {

    private int sifra;
    private String ime;
    private int karton;

    public Pacijent() {
    }

    public Pacijent(int sifra, String ime, int karton) {
        this.sifra = sifra;
        this.ime = ime;
        this.karton = karton;
    }

    public int getSifra() {
        return sifra;
    }

    public void setSifra(int sifra) {
        this.sifra = sifra;
    }

    public void setSifra(String sifra) {
        try {
            this.sifra = Integer.parseInt(sifra);
        } catch (NumberFormatException nfe) {
            this.sifra = 0;
        }
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public int getKarton() {
        return karton;
    }

    public void setKarton(int karton) {
        this.karton = karton;
    }

    public void setKarton(String karton) {
        try {
            this.karton = Integer.parseInt(karton);
        } catch (NumberFormatException nfe) {
            this.karton = 0;
        }
    }
}
