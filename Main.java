/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import blagajna.FrmBlagajna;
import blagajna.GetOpts;
import blagajna.SyncClient;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
        
/**
 *
 * @author eigorde
 */
public class Main {
    
    private static String rootDir = System.getProperty("user.dir");

    static {
        if (!rootDir.endsWith("/")) {
            rootDir = rootDir + "/";
        }
    }
    
    /**
     * Ispis pomoci kod parametara za komandnu liniju.
     */
    private static void help() {
        System.out.println("Blagajna");
        System.out.println("");
        System.out.println("-s adresa posluzitelja za auto-update");
        System.out.println("-p port posluzitelja, obicno tcp port 5002");
        System.out.println("");
        System.out.println("Folder za blagajnu: " + rootDir);
        System.out.println("");
        System.out.println("Autor: igor.delac@gmail.com");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        GetOpts o = new GetOpts(args);
        
        String hostname = o.getSwitch("-s");
        String port = o.getSwitch("-p");
        
        if (o.isSwitch("-h")) {
            help();
            return;
        }
        
        if (hostname.length() > 0) {

            String[] otherFiles = {
                "blagajnik.txt",
                "cacerts",
                "cjenik.txt",
                "postavke.txt",
                "test.pfx"
            };
                    
            if (port.length() == 0) {
                port = "5002";
            }                                       
            
            SyncClient cli = new SyncClient(hostname, Integer.parseInt(port));
            
            String allFiles;
            String classFiles;
            
            try {
                allFiles = cli.list("*");
                for (String item : allFiles.split(":")) {
                    if (item.endsWith("/")) {
                        classFiles = cli.list(item);
                        for (String classFile : classFiles.split(":")) {
                            if (classFile.endsWith(".class")) {
                                System.out.println("Update: " + item + classFile);
                                cli.get(item + classFile, rootDir + item + classFile);
                            }
                        }
                    }
                    else if (item.endsWith(".class")) {
                        System.out.println("Update: " + item);
                        cli.get(item, rootDir + item);
                    }
                    else {
                        System.out.println("Skip: " + item);
                    }
                }

                                                
                for (String otherFile : otherFiles) {
                    File fOther = new File(rootDir + otherFile);
                    if (!fOther.exists()) {                                            
                        System.out.println("Get: " + rootDir + otherFile);                    
                        cli.get(otherFile, rootDir + otherFile);
                    }
                }
                                
            } catch (UnknownHostException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        /*
         * Set the Nimbus look and feel
         */
        // <editor-fold defaultstate="collapsed"
        // desc=" Look and feel setting code (optional) ">
		/*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase
         * /tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            //System.out.println("LookAndFeel:");
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                    .getInstalledLookAndFeels()) {
                //System.out.println(info.getName());
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmBlagajna.class
                    .getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmBlagajna.class
                    .getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmBlagajna.class
                    .getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmBlagajna.class
                    .getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FrmBlagajna().setVisible(true);
            }
        });
    }
}
