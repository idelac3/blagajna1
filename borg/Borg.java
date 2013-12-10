package borg;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import com.easynth.lookandfeel.EaSynthLookAndFeel;
import com.seaglasslookandfeel.SeaGlassLookAndFeel;

/**
 * <H1>Borg</H1><BR>
 * Main window with panels organized in tabs.<BR>
 * Each telnet connection is in it's own tab.
 * 
 * @author eigorde
 *
 */
@SuppressWarnings("serial")
public class Borg extends JFrame {

	/**
	 * Reference to myself. 
	 */
	Borg myBorg = this;
	
	/**
	 * Main window title.
	 */
	private final String title = "Borg v0.1";

	/**
	 * JTabbedPane which covers whole area of main window.
	 */
	private JTabbedPane tabbedPane;
    
	/**
     * Telnet port.
     */
    public int telnetPort;

	/**
     * Syslog port.
     */
    public int syslogPort;
    
	/**
     * Sync port.
     */
    public int syncPort;
	
    /**
     * Sync dir.
     */
    public String syncDir;
        
	/**
	 * Hashmap which holds telnet client objects.
	 * Clients are recognized by <I>ipAddress:port</I> string.
	 */
	private HashMap<String, TelnetClient> telnetClients;
	
	/**
	 * Command history list.
	 */
	private List<String> commandHistory;
	
	/**
	 * Command history pointer which points to next available
	 * element in history.
	 */
	private int commandHistoryPtr;
	
	/**
	 * Keepalive timer to remove closed/stale telnet connections.
	 */
	private Timer tKeepalive;
	
	/**
	 * Initiate main window and keepalive timer.
	 */
	public Borg() {
		init();
		
		int delay = 10 * 1000; // 10 sec. delay
		
		tKeepalive = new Timer(delay, new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				for (String title : telnetClients.keySet()) {
					TelnetClient tc = telnetClients.get(title);
					if (tc.isConnected()) {					
						tc.sendKeepAlive();
					}
					else {
						removeTelnetClientTab(title);
					}
				}				
			}
		});
		
		tKeepalive.start();
		
	}
	
	/**
	 * Build main window.
	 */
	private void init() {
        // Make sure the program exits when the frame closes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);
        setSize(1280, 600);
        setLayout(new BorderLayout());

        // This will center the JFrame in the middle of the screen
        setLocationRelativeTo(null);
        
        // Set some defaults.
        syncDir = System.getProperty("user.dir");
        syncPort = 5002;
        syslogPort = 5001;
        telnetPort = 5003;
        telnetClients = new HashMap<String, TelnetClient>();
        commandHistory = new ArrayList<String>();
        commandHistoryPtr = 0;
        // ****
        
        // Check that ini file exist, load settings.
        // If not, then write default settings.
        File iniFile = new File("borg.ini");
        if (iniFile.exists()) {
			try {
				readSettings();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        else {
        	try {
				saveSettings();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        // Create menu items for this application
        buildMenu();
        
        // Make instance of JTabbedPane
        tabbedPane = new JTabbedPane();        
     
        // Create syslog panel
        addSyslogTab("syslog");
        
        JTextArea syslogArea = getSyslogText("syslog");
        if (syslogArea != null) {
			syslogArea.append("Settings\n");
			syslogArea.append("Syslog port: " + syslogPort + "\n");
			syslogArea.append("Sync.  port: " + syncPort + "\n");
			syslogArea.append("Sync.  dir.: " + syncDir + "\n");
			syslogArea.append("Telnet port: " + telnetPort + "\n");
        }
        
        add(tabbedPane, BorderLayout.CENTER);
        
	}
	
	/**
	 * Save settings to <I>borg.ini</I> file.
	 * @throws IOException
	 */
	private void saveSettings() throws IOException {
		IniSettings settings = new IniSettings();
		
		String section = "Listener";

		settings.set(section, "syslogPort", String.valueOf(syslogPort));
		settings.set(section, "syncPort", String.valueOf(syncPort));
		settings.set(section, "syncDir", syncDir);
		settings.set(section, "telnetPort", String.valueOf(telnetPort));
		
    	// Write to ini file.
		settings.write("borg.ini");
	}
	
	/**
	 * Read settings from <I>borg.ini</I> file.
	 * @throws IOException
	 */
	private void readSettings() throws IOException {
		IniSettings settings = new IniSettings();

		// Read from ini file.
		settings.read("borg.ini");
		String section = "Listener";

		// syslog uses udp 5001 by default.
		settings.setDefault("5001");
		syslogPort = Integer.parseInt(settings.get(section, "syslogPort"));

		// sync. port is on tcp port 5002 and application home folder.
		settings.setDefault("5002");
		syncPort = Integer.parseInt(settings.get(section, "syncPort"));
		settings.setDefault(System.getProperty("user.dir"));
		syncDir = settings.get(section, "syncDir");

		// Telnet uses custom tcp port 5003.
		settings.setDefault("5003");
		telnetPort = Integer.parseInt(settings.get(section, "telnetPort"));
		
	}
	
	/**
	 * Build main menu with sub-menus.
	 */
	private void buildMenu() {
		
		final JMenuBar menuBar = new JMenuBar();
		
		// File menu with items
		final JMenu mnuFile = new JMenu("File");
		final JMenuItem mnuFileExit = new JMenuItem("Exit");
		mnuFileExit.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);				
			}
		});
		
		final JMenuItem mnuFileSettings = new JMenuItem("Settings");
		mnuFileSettings.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				final JDialog dlgSettings = new JDialog(myBorg);
				
				dlgSettings.setTitle("Settings");
				dlgSettings.setLayout(new BoxLayout(dlgSettings.getContentPane(), BoxLayout.Y_AXIS));
				
				final JPanel pnl2 = new JPanel();
				final JTextField txtSyslog = new JTextField(String.valueOf(myBorg.syslogPort));
				pnl2.add(new JLabel("Syslog port:"));
				pnl2.add(txtSyslog);
				
				final JPanel pnl3 = new JPanel();
				final JTextField txtSyncPort = new JTextField(String.valueOf(myBorg.syncPort));
				pnl3.add(new JLabel("Sync.  port:"));
				pnl3.add(txtSyncPort);

				final JPanel pnl4 = new JPanel();
				final JTextField txtSyncDir = new JTextField(String.valueOf(myBorg.syncDir));
				pnl4.add(new JLabel("Sync. dir:"));
				pnl4.add(txtSyncDir);

				final JPanel pnl5 = new JPanel();
				final JTextField txtTelnetPort = new JTextField(String.valueOf(myBorg.telnetPort));
				pnl5.add(new JLabel("Telnet port:"));
				pnl5.add(txtTelnetPort);
				
				final JPanel pnl1 = new JPanel();
				final JButton btnSave = new JButton("Save");
				btnSave.addActionListener(new ActionListener() {					
					@Override
					public void actionPerformed(ActionEvent arg0) {
				        
						
				        try {

				        	syncPort = Integer.parseInt(txtSyncPort.getText());
				        	syncDir = txtSyncDir.getText();
				        	
				        	telnetPort = Integer.parseInt(txtTelnetPort.getText());
				        	syslogPort = Integer.parseInt(txtSyslog.getText());
				        	
				        	saveSettings();
							
							JOptionPane.showMessageDialog(dlgSettings, "Please restart application to apply changes.", "Settings", JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(dlgSettings, e.getMessage(), "Settings", JOptionPane.ERROR_MESSAGE);
						}
				        						
						dlgSettings.dispose();
					}
				});
				
				final JButton btnClose = new JButton("Close");
				btnClose.addActionListener(new ActionListener() {					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						dlgSettings.dispose();
					}
				});
				pnl1.add(btnSave);
				pnl1.add(btnClose);

				/*
				 * Order how item appear in dialog.
				 */
				dlgSettings.add(pnl2);
				dlgSettings.add(pnl5);
				dlgSettings.add(pnl3);
				dlgSettings.add(pnl4);
				dlgSettings.add(pnl1);
				
				/*
				 * Pack and make them visible.
				 */
				dlgSettings.setVisible(true);
				dlgSettings.pack();
				
			}
		});
		
		final JMenu mnuLookAndFeel = new JMenu("LookAndFeel");
		
		final ButtonGroup group = new ButtonGroup();
		
		for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            final JRadioButtonMenuItem mnuLookAndFeelItem = 
            		new JRadioButtonMenuItem(info.getName());
            
            mnuLookAndFeelItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
				
					try {
						for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
							String selectedLookAndFeel = mnuLookAndFeelItem.getText();
		                    if (selectedLookAndFeel.equalsIgnoreCase(info.getName())) {
		                        UIManager.setLookAndFeel(info.getClassName());
		                        break;
		                    }
		                }
						
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedLookAndFeelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SwingUtilities.updateComponentTreeUI(myBorg);
				}
			});
            
            if (UIManager.getLookAndFeel().getName().equalsIgnoreCase(mnuLookAndFeelItem.getText())) {
            	mnuLookAndFeelItem.setSelected(true);
            }
            
            group.add(mnuLookAndFeelItem);
            mnuLookAndFeel.add(mnuLookAndFeelItem);
		}
		
		mnuFile.add(mnuFileSettings);
		mnuFile.add(mnuLookAndFeel);
		mnuFile.addSeparator();
		mnuFile.add(mnuFileExit);
		
		// Finally, add File menu to menu bar for main window
		menuBar.add(mnuFile);
		
		setJMenuBar(menuBar);
		
	}
	
	/**
	 * <H1>Add telnet client tab</H1><BR>
	 * This function adds single telnet client panel to existing collection.
	 * Title of tab is: <I>ipAddress:port</I>
	 * @param title panel title
	 * @param telnetClient telnet client object which handles incoming connection 
	 */
	public void addTelnetClientTab(String title, TelnetClient telnetClient) {
		tabbedPane.addTab(title, getTelnetPanel());
		telnetClients.put(title, telnetClient);
		
	}

	/**
	 * <H1>Remove telnet client tab</H1><BR>
	 * This function removes single telnet client panel from existing collection.
	 * It also removes telnet client object from hashmap <I>telnetClients</I> which holds 
	 * references to all active telnet sessions.
	 * @param title tab title should be: <I>ipAddress:port</I> 
	 */
	public void removeTelnetClientTab(String title) {
		for (int index = 0; index < tabbedPane.getComponentCount(); index++) {
			if (tabbedPane.getTitleAt(index).equalsIgnoreCase(title)) {
				tabbedPane.removeTabAt(index);
				telnetClients.remove(title);
			}
		}
	}	
	
	/**
	 * <H1>Get telnet client text area</H1><BR>
	 * This is JTextArea which is used to append printouts from telnet servers.
	 * @param title tab title should be: <I>ipAddress:port</I> 
	 * @return JTextArea with (for) printouts
	 */
	public JTextArea getTelnetClientText(String title) {
		JTextArea retVal = null;
		for (int index = 0; index < tabbedPane.getTabCount(); index++) {
			if (tabbedPane.getTitleAt(index).equalsIgnoreCase(title)) {
				Component cpm0 = tabbedPane.getComponent(index);
				if (cpm0 instanceof JPanel) {
					JPanel panel = (JPanel) cpm0;
					for (Component cmp1 : panel.getComponents())
						if (cmp1 instanceof JScrollPane) {
							JScrollPane pane = (JScrollPane) cmp1;
							JViewport viewPort = pane.getViewport();
							retVal = (JTextArea) viewPort.getView();
							break;
						}
				}
			}
		}		
		return retVal;
	}
	
	/**
	 * Create a JPanel for telnet connection, 
	 * with printout text area and input command text field.
	 * @return JPanel object ref.
	 */
	private JPanel getTelnetPanel() {
		
		final JPanel panel = new JPanel(new BorderLayout());
		final JTextArea area = new JTextArea();
		area.setFont(new Font("Monospaced", Font.PLAIN, 16));
		
		final JScrollPane scrollPanel = new JScrollPane(area);
				
		final JTextField commandInput = new JTextField();
		commandInput.setFont(new Font("Monospaced", Font.PLAIN, 16));
		commandInput.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				int code = arg0.getKeyCode();
				if (code == KeyEvent.VK_ENTER) {
					String cmd = commandInput.getText();
					
					commandHistory.add(cmd);
					commandHistoryPtr = commandHistory.size() - 1;
					
					commandInput.setText("");
					
					int index = tabbedPane.getSelectedIndex();
					String title = tabbedPane.getTitleAt(index);
					
					TelnetClient telnetClient = telnetClients.get(title);
					if (telnetClient == null) {
						JOptionPane.showMessageDialog(null, "Missing entry in telnet client table: " + title,
								"Telnet client table", JOptionPane.ERROR_MESSAGE);
								
					}
					else {
					
						if (telnetClient.isConnected()) {						
							telnetClient.send(cmd);
						}
						else {
							removeTelnetClientTab(title);
						}
					}
				}
				else if (code == KeyEvent.VK_UP) {
					if (commandHistory.size() > 0) {
						commandInput.setText(commandHistory
								.get(commandHistoryPtr));
						commandHistoryPtr--;
						if (commandHistoryPtr == 0) {
							commandHistoryPtr = commandHistory.size() - 1;
						}
					}
				}
				else if (code == KeyEvent.VK_DOWN) {
					if (commandHistory.size() > 0) {
						commandInput.setText(commandHistory
								.get(commandHistoryPtr));
						commandHistoryPtr++;
						if (commandHistoryPtr > commandHistory.size() - 1) {
							commandHistoryPtr = 0;
						}
					}
				}
			}
		});
		
		panel.add(scrollPanel, BorderLayout.CENTER);
		panel.add(commandInput, BorderLayout.PAGE_END);
		
		return panel;
	}
	
	/**
	 * <H1>Add syslog tab</H1><BR>
	 * This function adds syslog panel to existing collection.
	 * Title of tab is any string.
	 * @param title panel title 
	 */
	public void addSyslogTab(String title) {
		tabbedPane.addTab(title, getSyslogPanel());		
	}
	
	/**
	 * Create a JPanel for syslog connection, 
	 * with printout text area.
	 * @return JPanel object ref.
	 */
	private JPanel getSyslogPanel() {
		
		final JPanel panel = new JPanel(new BorderLayout());
		final JTextArea area = new JTextArea();
		area.setFont(new Font("Monospaced", Font.PLAIN, 16));
		
		final JScrollPane scrollPanel = new JScrollPane(area);				
		
		panel.add(scrollPanel, BorderLayout.CENTER);
		
		return panel;
	}
	
	/**
	 * <H1>Get syslog text area</H1><BR>
	 * This is JTextArea which is used to append printouts from sysloggers.
	 * @param title tab title should be same value used in <I>addSyslogTab(String <B>title</B>)</I>
	 * function call
	 * @return JTextArea with (for) printouts
	 */
	public JTextArea getSyslogText(String title) {
		JTextArea retVal = null;
		for (int index = 0; index < tabbedPane.getTabCount(); index++) {
			if (tabbedPane.getTitleAt(index).equalsIgnoreCase(title)) {
				Component cpm0 = tabbedPane.getComponent(index);
				if (cpm0 instanceof JPanel) {
					JPanel panel = (JPanel) cpm0;
					for (Component cmp1 : panel.getComponents())
						if (cmp1 instanceof JScrollPane) {
							JScrollPane pane = (JScrollPane) cmp1;
							JViewport viewPort = pane.getViewport();
							retVal = (JTextArea) viewPort.getView();
							break;
						}
				}
			}
		}		
		return retVal;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {


		/*
		 * Install some custom LookAndFeel skins.
		 * 
		 * To make them default, you can use VM argument:
		 * -Dswing.defaultlaf=com.seaglasslookandfeel.SeaGlassLookAndFeel
		 * or this one:
		 * -Dswing.defaultlaf=com.easynth.lookandfeel.EaSynthLookAndFeel
		 */
		final EaSynthLookAndFeel easynthLAF = new EaSynthLookAndFeel();		
		final SeaGlassLookAndFeel seaglassLAF = new SeaGlassLookAndFeel();
		
		UIManager.installLookAndFeel(easynthLAF.getName(), easynthLAF.getClass().getName());
		UIManager.installLookAndFeel(seaglassLAF.getName(), seaglassLAF.getClass().getName());
		
		/*
		 *  Make main window visible.
		 */
		final Borg borg = new Borg();
		borg.setVisible(true);
		
		Thread t_telnetListener = new Thread(new Runnable() {

			@Override
			public void run() {
				
				ServerSocket tcpListener = null;
				try {
					tcpListener = new ServerSocket(borg.telnetPort);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Socket clientSocket;

				while (true)
					try {

						TelnetClient telnetClient;
						clientSocket = tcpListener.accept();
						telnetClient = new TelnetClient(clientSocket);

						String ipAddress = clientSocket.getInetAddress()
								.getHostAddress();
						int clientPort = clientSocket.getPort();
						String panelTitle = ipAddress + ":" + clientPort;

						borg.getSyslogText("syslog").append(
								"Telnet connection from: " + panelTitle + "\n");

						borg.addTelnetClientTab(panelTitle, telnetClient);
						telnetClient.hook(borg.getTelnetClientText(panelTitle));

					} catch (IOException ex) {
						ex.printStackTrace();
						break;
					}

				try {
					tcpListener.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, "telnetListener");
		t_telnetListener.start();

		Thread t_syslogListener = new Thread(new Runnable() {

			@Override
			public void run() {
				final int BUFFER_LEN = 4096;
				
		        byte[] receiveData = new byte[BUFFER_LEN];

		        DatagramSocket udpListener = null;

		        /*
		         * Open syslog port for listening.
		         * If it fails, then terminate this thread.
		         */
		        try {
		            udpListener = new DatagramSocket(borg.syslogPort);
		        } catch (SocketException e) {
		            // Print error on console.
		            e.printStackTrace();
		            // Quit function.
		            return; 
		        }

		        while (true)
		            try {
		            	
		            	// Allocate space for new udp datagram.
		                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		                // Wait for new udp datagram.
		                udpListener.receive(receivePacket);
		                
		                // Extract datagram content.
		                String sentence = new String(receivePacket.getData());

		                /*
		                 * Process message.
		                 */
		                InetAddress ipAddress = receivePacket.getAddress();
		                String ipAddressString = ipAddress.getHostAddress();

		                // Append message to text area.
		                borg.getSyslogText("syslog").append("From " + ipAddressString + " received:\n");
		                borg.getSyslogText("syslog").append(sentence + "\n");
		                
		                
		            } catch (IOException e) {
		                // Print error on console.
		                e.printStackTrace();
		                // Quit while loop.
		                break; 
		            }
		        
		        udpListener.close();
	
			}
			
		}, "syslogListener");
		t_syslogListener.start();

		Thread t_syncListener = new Thread(new Runnable() {

			@Override
			public void run() {
				
				ServerSocket tcpListener = null;
				try {
					tcpListener = new ServerSocket(borg.syncPort);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Socket clientSocket;

				while (true)
					try {

						SyncServer syncServer;
						clientSocket = tcpListener.accept();
						syncServer = new SyncServer(clientSocket, borg.syncDir);

						String ipAddress = clientSocket.getInetAddress()
								.getHostAddress();
						int clientPort = clientSocket.getPort();
						String panelTitle = ipAddress + ":" + clientPort;

						borg.getSyslogText("syslog").append(
								"Sync. connection from: " + panelTitle + "\n");
						
						Thread t_syncClient = new Thread(syncServer, "syncClient-" + panelTitle);
						t_syncClient.start();
						
					} catch (IOException ex) {
						ex.printStackTrace();
						break;
					}

				try {
					tcpListener.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, "syncListener");
		t_syncListener.start();

         

	}

}
