package blagajna;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import javax.swing.table.AbstractTableModel;

// CustomTableDataModel for tablicaProizvoda
public class RacunJTableDataModel extends AbstractTableModel {

    private Iterator iterator;
    /**
     * Data in JTable -> one object == one row
     */
    private List<Proizvod> data = new LinkedList();
    /**
     * Stupci u JTable tablici
     */
    private String[] columns = {"Naziv", "Cijena [Kn]", "PDV [%]", "PNP [%]"};

    /**
     * Da li se mogu celije mijenjati
     */
    private boolean editable;
    /**
     * Konstruktor
     * @param editable <I>true</I> - dopusti promjenu celija<BR>
     * <I>false</I> - zabrani promjenu celija<BR>
     */
    public RacunJTableDataModel(boolean editable) {
        this.editable = editable;
    }

    /**
     * Konstruktor, lista proizvoda
     * @param listaProizvoda Izgradi tablicni model koristeci vec postojecu listu racuna
     * @param editable <I>true</I> - dopusti promjenu celija<BR>
     * <I>false</I> - zabrani promjenu celija<BR>
     */
    public RacunJTableDataModel(List listaProizvoda, boolean editable) {
        data.addAll(listaProizvoda);
        this.editable = editable;
    }

    /**
     * Vraca proizvod
     *
     * @param row redak u tablici
     * @return Proizvod tip podataka
     */
    public Proizvod getDataObject(int row) {
        return (Proizvod) data.get(row);
    }

    /**
     * Uklanja redak u tablici
     *
     * @param row broj retka
     * @return Proizvod tip podatka
     */
    public Proizvod removeDataObject(int row) {
        Proizvod proizvod = (Proizvod) data.remove(row);
        fireTableDataChanged();
        return proizvod;
    }

    /**
     * Postoji li Proizvod u tablici.<BR>     
     *
     * @param proizvod tip podatka je Proizvod
     */
    public boolean isProizvod(Proizvod proizvod) {
        boolean exist = false;
        for (Iterator<Proizvod> it = data.iterator(); it.hasNext();) {
            Proizvod proizvod1 = it.next();
            if (proizvod1.equalsByName(proizvod)) {                
                exist = true;
                break;
            }
        }
        return exist;
    }
    
    /**
     * Dodaje proizvod na kraj tablice<BR>
     * ili mijenja postojeci proizvod ako su nazivi isti.<BR>
     *
     * @param proizvod tip podatka je Proizvod
     */
    public void addProizvod(Proizvod proizvod) {
        boolean exist = false;
        for (Iterator<Proizvod> it = data.iterator(); it.hasNext();) {
            Proizvod proizvod1 = it.next();
            if (proizvod1.equalsByName(proizvod)) {
                proizvod1.initProizvod(proizvod.getNaziv(), proizvod.getCijena(), proizvod.getPdv(), proizvod.getPnp());
                exist = true;
                break;
            }
        }
        if (!exist) {
            data.add(proizvod);
        }
        fireTableDataChanged();
    }

    /**
     * Dodaje listu proizvoda u tablicu
     *
     * @param l lista proizvoda
     */
    public void addProizvodList(List l) {
        data.addAll(l);
        fireTableDataChanged();
    }

    /**
     * Isprazni tablicu proizvoda
     */
    public void deleteProizvodList() {
        data.clear();
        fireTableDataChanged();
    }

    /**
     * Broj redaka, proizvoda u tablici
     *
     * @return int tip podatka
     */
    @Override
    public int getRowCount() {
        return data.size();
    }

    /**
     * Broj stupaca, proizvoda u tablici
     *
     * @return int tip podatka
     */
    @Override
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Naziv stupca, proizvoda u tablici
     *
     * @return String tip podatka
     */
    @Override
    public String getColumnName(int i) {
        return columns[i];
    }

    /**
     * Dohvat vrijednosti redak x stupac
     *
     * @return Object tip podatka
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Proizvod proizvod = (Proizvod) data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return String.valueOf(proizvod.getNaziv());
            case 1:
                return String.valueOf(proizvod.getIznosCijenaIspisBezKN());
            case 2:
                return String.valueOf(proizvod.getIznosPdvIspisBezPostotnogZnaka());
            case 3:
                return String.valueOf(proizvod.getIznosPnpIspisBezPostotnogZnaka());
            default:
                return null;
        }
    }

    /**
     * Definicija da li je celija u tablici promjenjiva
     *
     * @param rowIndex broj retka
     * @param columnIndex broj stupca
     * @return true ako je promjenjiva, inace false
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return editable;
            case 1:
                return editable;
            case 2:
                return editable;
            case 3:
                return editable;
            default:
                return false;
        }
    }

    /**
     * Tells the view which type of object will be displayed. This allows the
     * JTable to display the data in a way that is most appropriate for the type
     * of object that exists in that row.
     *
     * @param columnIndex
     * @return
     */
    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: // Naziv
                return String.class;
            case 1: // Cijena
                return String.class;
            case 2: // PDV
                return String.class;
            case 3: // PNP
                return String.class;
            default:
                return null;
        }
    }

    /**
     * Postavi vrijednost celiji
     *
     * @param value Objekt sa nekom vijednosti
     * @param rowIndex broj retka
     * @param columnIndex broj stupca
     */
    @Override
    public void setValueAt(Object objectValue, int rowIndex, int columnIndex) {
        Proizvod proizvod = (Proizvod) data.get(rowIndex);

        Double value = null;
        if (columnIndex > 0) {
            if (objectValue instanceof String) {
                value = Double.parseDouble((String) objectValue);
            }
            if (objectValue instanceof Double) {
                value = (Double) value;
            }
        }



        switch (columnIndex) {
            case 0:
                proizvod.setNaziv((String) objectValue);
                break;
            case 1:
                proizvod.setCijena(value);
                break;
            case 2:
                proizvod.setPdv(value/100);
                break;
            case 3:
                proizvod.setPnp(value/100);
                break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Vraca listu proizvoda
     *
     * @return List<Proizvod>
     */
    public List<Proizvod> getData() {
        return data;
    }

    /**
     * Postavlja podatke
     *
     * @param data List<Proizvod>
     */
    public void setData(List<Proizvod> data) {
        this.data = data;
    }

    public Iterator getIterator() {
        this.iterator = data.iterator();
        return iterator;
    }

    /**
     * Uklanja proizvod prema nazivu
     *
     * @param naziv
     */
    public void removeObjectWithID(String naziv) {
        iterator = data.iterator();
        while (iterator.hasNext()) {
            Proizvod proizvod = (Proizvod) iterator.next();
            if (naziv.equals(proizvod.getNaziv())) {
                iterator.remove();
            }
        }
    }

    public void sortByName(final boolean reverse) {
        Comparator c = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (!(o1 instanceof Proizvod)) {
                    return 0;
                }
                if (!(o2 instanceof Proizvod)) {
                    return 0;
                }

                Proizvod p1 = (Proizvod) o1;
                Proizvod p2 = (Proizvod) o2;

                if (reverse) {
                    return p2.getNaziv().compareTo(p1.getNaziv());
                }
                return p1.getNaziv().compareTo(p2.getNaziv());
            }
        };
        Collections.sort(data, c);
        fireTableDataChanged();
    }

    public void sortByPrice(final boolean reverse) {
        Comparator c = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (!(o1 instanceof Proizvod)) {
                    return 0;
                }
                if (!(o2 instanceof Proizvod)) {
                    return 0;
                }

                Proizvod p1 = (Proizvod) o1;
                Proizvod p2 = (Proizvod) o2;


                if (p1.getCijena() > p2.getCijena()) {
                    if (reverse) {
                        return -1;
                    }
                    return 1;
                }
                if (p1.getCijena() < p2.getCijena()) {
                    if (reverse) {
                        return 1;
                    }
                    return -1;
                } else {
                    return 0;
                }

            }
        };
        Collections.sort(data, c);
        fireTableDataChanged();
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    
}
