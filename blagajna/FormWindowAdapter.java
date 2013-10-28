package blagajna;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 *
 * @author vedran_kolonic
 */
public class FormWindowAdapter extends WindowAdapter {

    private FrmBlagajna blagajna;
    private boolean fullscreen;

    public FormWindowAdapter(FrmBlagajna blagajna, boolean fullscreen) {
        this.blagajna = blagajna;
        this.fullscreen = fullscreen;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        JFrame frame = (JFrame) e.getSource();
        if (fullscreen) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } // in the middle
        else {
            // Get the size of the screen
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

            // Determine the new location of the window
            int w = frame.getSize().width;
            int h = frame.getSize().height;
            int x = (dim.width - w) / 2;
            int y = (dim.height - h) / 2;
            // Move the window
            frame.setLocation(x, y);
        }
        // onemogući glavni ekran, tj. blaganju
        //blagajna.toggleScreen(false);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        JFrame frame = (JFrame) e.getSource();
        // omoguci blagajnu
        blagajna.toggleScreen(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
