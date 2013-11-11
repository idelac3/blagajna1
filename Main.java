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
import java.text.SimpleDateFormat;
import java.util.Date;
        
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
     * Info o datoteci u prikladnom formatu za SyncClient potrebe.
     * @param filename naziv i putanja do datoteke
     * @return niz formata: TYPE:<I>type</I>,SIZE:<I>size</I>,DATE:<I>dd-MM-yyyy</I>
     */
    private static String fileInfo(String filename) {
        String localInfo = "-";
        File localFile = new File(filename);

        if (localFile.exists()) {
            if (localFile.isFile()) {
                localInfo = "TYPE:file";
                localInfo = localInfo + ",SIZE:" + String.valueOf(localFile.length());
            } else if (localFile.isDirectory()) {
                localInfo = "TYPE:directory";
                localInfo = localInfo + ",SIZE=0";
            }

            localInfo = localInfo + ",DATE:"
                    + new SimpleDateFormat("dd-MM-yyyy").format(new Date(localFile.lastModified()));
        }

        return localInfo;
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
                    
            String allFiles;
            String classFiles;
            
            int updatedCount = 0;
            
            try {
    
                SyncClient cli = new SyncClient();                    
                cli.connect(hostname, Integer.parseInt(port));
    
                allFiles = cli.list("*");
                for (String item : allFiles.split(":")) {
                    if (item.endsWith("/")) {
                        classFiles = cli.list(item);
                        for (String classFile : classFiles.split(":")) {
                            if (classFile.endsWith(".class")) {
                                boolean updateClassFile = false;
                                
                                String remoteFileInfo = cli.info(item + classFile);
                                String localFileInfo = fileInfo(rootDir + item + classFile);
                                
                                updateClassFile = !localFileInfo.equalsIgnoreCase(remoteFileInfo);
                                
                                if (updateClassFile) {
                                    System.out.println("Update: " + item + classFile);
                                    cli.get(item + classFile, rootDir + item + classFile);
                                    updatedCount++;
                                }
                            }
                        }
                    }
                    else if (item.endsWith(".class")) {
                        boolean updateClassFile = false;

                        String remoteFileInfo = cli.info(item);
                        String localFileInfo = fileInfo(rootDir + item);

                        updateClassFile = !localFileInfo.equalsIgnoreCase(remoteFileInfo);

                        if (updateClassFile) {
                            System.out.println("Update: " + item);
                            cli.get(item, rootDir + item);
                            updatedCount++;
                        }

                    }
                    else {
                        //System.out.println("Skip: " + item);
                    }
                }

                System.out.println("Updated: " + updatedCount + " class files.");
                                                
                for (String otherFile : otherFiles) {
                    File fOther = new File(rootDir + otherFile);
                    if (!fOther.exists()) {                                            
                        System.out.println("Get: " + rootDir + otherFile);                    
                        cli.get(otherFile, rootDir + otherFile);
                    }
                }
                                
                cli.disconnect();
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        else {
			/*
			 * Set the Nimbus look and feel
			 */
			// <editor-fold defaultstate="collapsed"
			// desc=" Look and feel setting code (optional) ">
			/*
			 * If Nimbus (introduced in Java SE 6) is not available, stay with
			 * the default look and feel. For details see
			 * http://download.oracle.com/javase
			 * /tutorial/uiswing/lookandfeel/plaf.html
			 */
			try {
				// System.out.println("LookAndFeel:");
				for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
						.getInstalledLookAndFeels()) {
					// System.out.println(info.getName());
					if ("Nimbus".equals(info.getName())) {
						javax.swing.UIManager.setLookAndFeel(info
								.getClassName());
						break;
					}
				}
			} catch (ClassNotFoundException ex) {
				java.util.logging.Logger.getLogger(FrmBlagajna.class.getName())
						.log(java.util.logging.Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				java.util.logging.Logger.getLogger(FrmBlagajna.class.getName())
						.log(java.util.logging.Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				java.util.logging.Logger.getLogger(FrmBlagajna.class.getName())
						.log(java.util.logging.Level.SEVERE, null, ex);
			} catch (javax.swing.UnsupportedLookAndFeelException ex) {
				java.util.logging.Logger.getLogger(FrmBlagajna.class.getName())
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
}
