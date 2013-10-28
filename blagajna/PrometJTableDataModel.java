package blagajna;


import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author eigorde
 */
public class PrometJTableDataModel extends AbstractTableModel {

    private Iterator iterator;
    // Data in JTable -> one object == one row
    private List<Promet> data = new LinkedList();
    // Columns in JTable
    private String[] columns = {"Broj racuna", "Datum", "Iznos", 
        "PDV", "PNP", "Storniran", "OIB blagajnika", 
        "Zastitni kod", "Jedinstveni identifikator racuna"};

    
    // konstruktori
    public PrometJTableDataModel() {
    }

    public PrometJTableDataModel(List l) {
        data.addAll(l);
    }

    
    public Promet getDataObject(int row) {
        return (Promet) data.get(row);
    }

    public Promet removeDataObject(int row) {
        Promet promet = (Promet) data.remove(row);
        fireTableDataChanged();
        return promet;
    }
    // add Node at end 
    public void addPromet(Promet promet) {
        boolean exist = false;
        for (Iterator<Promet> it = data.iterator(); it.hasNext();) {
            Promet promet1 = it.next();
            // postoji li vec podatak o prometu? (uzima se u obzir samo brRac, pogledati Promet.java, equalsByName funkciju)
            if (promet1.equalsByName(promet)) {
                // azuriraj postojeci zapisu o prometu sa novim vrijednostima
                promet1.setBrRac(promet.getBrRac());
                promet1.setDatum(promet.getDatum());
                promet1.setIznosUkupno(promet.getIznosUkupno());
                promet1.setUkupnoPdv(promet.getUkupnoPdv());
                promet1.setUkupnoPnp(promet.getUkupnoPnp());
                promet1.setStorniran(promet.isStorniran());
                promet1.setOibOper(promet.getOibOper());
                promet1.setZkod(promet.getZkod());
                promet1.setJir(promet.getJir());
                exist = true;
                break;
            }
        }
        if (!exist) {
            data.add(promet);
        }
        fireTableDataChanged();
    }

    public void addPrometList(List l) {
        data.addAll(l);
        fireTableDataChanged();
    }

    public void deletePrometList() {
        data.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int i) {
        return columns[i];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Promet promet = (Promet) data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return String.valueOf(promet.getBrRac());
            case 1:
                return String.valueOf(promet.getDatumIspis());
            case 2:
                return String.valueOf(promet.getIznosUkupnoIspis());
            case 3:
                return String.valueOf(promet.getUkupnoPdvIspis());
            case 4:
                return String.valueOf(promet.getUkupnoPnpIspis());
            case 5:
                return String.valueOf(promet.getStorniranIspis());                
            case 6:
                return String.valueOf(promet.getOibOper());
            case 7:
                return String.valueOf(promet.getZkod());
            case 8:
                return String.valueOf(promet.getJir());

            default:
                return null;
        }
    }
    
        // Defines which cell is editable
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
                return false;
            case 2:
                return false;
            case 3:
                return false;
            case 4:
                return false;
            case 5:
                return false;
                // itd. by def. nista nije editabilno
            default:
                return false;
        }
    }

    // Tells the view which type of object will be displayed.
    // This allows the JTable to display the data in a way that
    // is most appropriate for the type of object that exists 
    // in that row. 
    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: // br.racuna
                return Integer.class;
            case 1: // datum
                return String.class;
            case 2: // iznos ukupno
                return String.class;
            case 3: // pdv
                return String.class;
            case 4: // pnp 
                return String.class;
            case 5: // storniran
                return String.class;
            case 6: // oib blagajnika
                return String.class;
            case 7: // zkod
                return String.class;
            case 8: // jir
                return String.class;

            default:
                return null;
        }
    }
    
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Promet promet = (Promet) data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                promet.setBrRac( (Integer) value);
                break;
            case 1:
                promet.setDatum((Date) value);
                break;
            case 2:
                promet.setIznosUkupno((Double) value);
                break;
            case 3:
                promet.setUkupnoPdv((Double) value);
                break;
            case 4:
                promet.setUkupnoPnp((Double) value);
                break;
            case 5:
                promet.setStorniran((Boolean) value);
                break;                
            case 6:
                promet.setOibOper((String) value);
                break;
            case 7:
                promet.setZkod((String) value);
                break;
            case 8:
                promet.setJir((String) value);                
                break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public List<Promet> getData() {
        return data;
    }

    public void setData(List<Promet> data) {
        this.data = data;
    }

    public Iterator getIterator() {
        this.iterator = data.iterator();
        return iterator;
    }

    public void removeObjectWithID(int brRac) {
        iterator = data.iterator();
        while (iterator.hasNext()) {
            Promet promet = (Promet) iterator.next();
            if (brRac == (promet.getBrRac())) {
                iterator.remove();
            }
        }
    }
        
}
