package blagajna;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Cjenik {

    /**
     * Lista proizvoda
     */
    private List<Proizvod> proizvodi = new ArrayList<Proizvod>();

    /**
     * Prazan konstruktor, nista se nece dogoditi
     */
    public Cjenik() throws IOException {
    }

    /**
     * Broj proizvoda u cjeniku
     *
     * @return Integer
     */
    public int getSize() {
        return proizvodi.size();
    }

    /**
     * Popis proizvoda
     *
     * @return List<Proizvod>
     */
    public List<Proizvod> getProizvodi() {
        return proizvodi;
    }

    /**
     * Postavi popis proizvoda
     *
     * @param proizvodi List<Proizvod>
     */
    public void setProizvodi(List<Proizvod> proizvodi) {
        this.proizvodi = proizvodi;
    }

    /**
     * Ucitaj cjenik iz datoteke
     *
     * @param fileName naziv datoteke
     */
    public void loadCjenik(String fileName) throws FileNotFoundException, IOException {

        // ocekuju se max. 4 parametra u cjeniku (naziv, cijena, pdv i pnp)
        String[] arr = new String[4];


        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        StringTokenizer token;
        int lineNum = 0, tokenNum = 0;

        while ((line = br.readLine()) != null) {
            // prva linija ima br.vrijednos 1, a ne 0
            lineNum++;
            // barem 5 znakova duljine treba biti da se obradi
            if (line.length() > 5) {
                // preskoci komentare koji pocinju sa znakom #
                if (line.charAt(0) != '#') {

                    // Inicijalizacija varijabli
                    String naziv;
                    double cijena;
                    double pdv;
                    double pnp;

                    for (int i = 0; i < 4; i++) {
                        arr[i] = "0.00";
                    }

                    // razlomi liniju koristeci ; znak
                    token = new StringTokenizer(line, ";");

                    // uzmi dijelove
                    while (token.hasMoreTokens() && tokenNum < 4) {
                        arr[tokenNum] = token.nextToken();
                        tokenNum++;
                    }

                    naziv = arr[0];
                    cijena = Double.parseDouble(arr[1]);
                    // pretvorba iz postotaka u decimalan broj
                    pdv = (Double.parseDouble(arr[2]) / 100);
                    // pretvorba iz postotaka u decimalan broj
                    pnp = (Double.parseDouble(arr[3]) / 100);

                    // dodaj na listu proizvoda ucitani proizvod iz cjenika
                    Proizvod p = new Proizvod(naziv, cijena, pdv, pnp);
                    proizvodi.add(p);

                    tokenNum = 0;
                }
            }
        }
        br.close();

    }

    /**
     * Spremi cjenik u datoteku
     *
     * @param fileName naziv datoteke
     */
    public void saveCjenik(String fileName) throws FileNotFoundException {

        File file = new File(fileName);
        PrintWriter pw = new PrintWriter(file);

        // ispisi zaglavlje
        pw.println("#v0.1");
        pw.println("#naziv cijena pdv pnp");

        // ispis proizvode u cjeniku
        for (Proizvod p : proizvodi) {
            pw.println(p.getNaziv() + ";" + (p.getCijena()) + ";" + (p.getPdv() * 100) + ";" + (p.getPnp() * 100));
        }

        pw.close();

    }
}
