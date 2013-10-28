package blagajna;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author vedran_kolonic
 */
public class BlagajnaWindowAdapter extends WindowAdapter {

    /**
     * Poruka pri zatvaranju prozora
     */
    private String informationText = "Jeste li sigurni da zelite izaci iz ";

    /**
     * Prazan konstruktor
     */
    public BlagajnaWindowAdapter() {
    }

    /**
     * Prozor se razvlaci preko cijelog ekrana
     * @param e dogadjaj vezan za otvaranje prozora
     */
    @Override
    public void windowOpened(WindowEvent e) {
        JFrame frame = (JFrame) e.getSource();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    /**
     * Dialog poruka, da li zelite stvarno zatvoriti prozor i izaci iz programa?
     * @param e dogadjaj vezan za zatvaranje prozora
     */
    @Override
    public void windowClosing(WindowEvent e) {
        JFrame frame = (JFrame) e.getSource();
        int result = JOptionPane.showConfirmDialog(
                frame,
                informationText + frame.getTitle() + "?",
                frame.getTitle(),
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        if (result == JOptionPane.NO_OPTION) {
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }
}
