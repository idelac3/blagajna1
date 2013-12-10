package borg;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextArea;

/**
 * <H1>Telnet Client</H1><BR>
 * Telnet connector class to establish a telnet-like connection to remote host.<BR>
 * <BR>
 * This type of client works on <I>hook</I> principle:<UL>
 * <LI>1.</B> A connection is established.</LI>
 * <LI>2.</B> A hook is placed to direct printouts to a <I>handler</I>.</LI>
 * <LI>3.</B> Commands are sent with <I>send(...)</I> function.</LI>
 * <LI>4.</B> At the end, session is closed with <I>disconnect()</I> call.</LI>
 * </UL>
 * <B>Hooks</B><BR>
 * To place a hook, you need a <I>handler</I> that will accept printouts (eg.
 * String data type) and do something with it. An example is hook handler which
 * simply appends printouts to JTextArea which should be visible on screen, so
 * that user can have telnet-like experience. <BR>
 * 
 * @author eigorde
 * 
 */
public class TelnetClient {

    private boolean isConnected;

    private Socket client;
    private BufferedOutputStream out;
    private BufferedInputStream in;

    private String printout;

    private boolean keepaliveFlag;
    
    /**
     * <H1>Telnet Client</H1><BR>
     * New <I>Telnet client</I> instance. Next would be to connect to remote side with
     * <I>connect()<I/> function.
     */
    public TelnetClient() {
        isConnected = false;
        printout = "";
    }

    /**
     * <H1>Telnet Client</H1><BR>
     * New <I>Telnet client</I> instance with supplied socket.
     * This allows reverse telnet, when you need to listen and accept connection:<BR>
     * <PRE>
     *  int telnetPort = 4444;
     *  ServerSocket listener = new ServerSocket(telnetPort);
     *  while (true) {
     *  
     *   TelnetClient tc;
     *   clientSocket = listener.accept();
     *   telnetClient = new TelnetClient(clientSocket);
     *  
     *   Thread t = new Thread(telnetClient);
     *   t.start();    
     *  }
     *  listener.close();
     * </PRE>
     * It is not necessary to connect to remote side with
     * <I>connect()<I/> function.
     */
    public TelnetClient(Socket clientSocket) throws IOException {
    	
    	client = clientSocket;
    	
        isConnected = true;

        out = new BufferedOutputStream(client.getOutputStream());
        in = new BufferedInputStream(client.getInputStream());
        
    }
    
    /**
     * <H1>Connect</H1><BR>
     * Establish a telnet connection to remote host.
     * 
     * @param host
     *            hostname or ip address
     * @param port
     *            tcp port number, default is 23 for telnet service(s)
     * @throws UnknownHostException
     * @throws IOException
     */
    public void connect(String host, int port) throws UnknownHostException, IOException {

        /*
         * Try to disconnect first.
         */
        if (isConnected) {
            disconnect();
        }

        client = new Socket(host, port);
        isConnected = true;

        out = new BufferedOutputStream(client.getOutputStream());
        in = new BufferedInputStream(client.getInputStream());

    }

    /**
     * <H1>Disconnect</H1><BR>
     * Close telnet session.<BR>
     * In case of exception (eg. IOException), a connection status will turn to
     * <I>false</I>.
     */
    public void disconnect() {
        try {
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {

        } finally {
            isConnected = false;
            printout = "";
        }
    }

    /**
     * <H1>IsConnected</H1><BR>
     * Determine connection status.
     * 
     * @return <I>true</I> if connected, otherwise <I>false</I>
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * <H1>Send</H1><BR>
     * Send <I>command</I> to remote host.
     * 
     * @param command
     *            remote command (eg. <I>PLLDP;</I>)
     */
    public void send(String command) {
    	
        /*
         * Make sure line ends with CR LF bytes.
         */
    	if (!command.endsWith("\r\n")) {        
    		command = command + "\r\n";
    	}
    	
        /*
         * Clear printout.
         */
        printout = "";

        /*
         * Send bytes to output stream and flush.
         */
        try {
            out.write(command.getBytes());
            out.flush();
        } catch (IOException e) {
            isConnected = false;
        }
    }
    
    /**
     * <H1>Send</H1><BR>
     * Send <I>KEEPALIVE</I> message to remote host.
     * 
     */
    public void sendKeepAlive() {
    	
        /*
         * Make sure line ends with CR LF bytes.
         */
    	String command = "KEEPALIVE\r\n";

    	/*
    	 * Turn off keepalive flag. It will be turned on
    	 * upon reception of KEEPALIVE message from 
    	 * telnet server.
    	 */
    	keepaliveFlag = false;
    	
        /*
         * Send bytes to output stream and flush.
         */
        try {
            out.write(command.getBytes());
            out.flush();
        } catch (IOException e) {
            isConnected = false;
        }
    }
    
    /**
     * <H1>Hook</H1><BR>
     * It will place a hook to a text area that can accept strings.
     * <H2>Note</H2><BR>
     * This is a non-blocking call that reads from socket in it's own thread.<BR>
     * A new hook can be easily written in case that printouts should be placed
     * somewhere else.<BR>
     * 
     * @param outputArea
     *            <I>JTextArea</I> for appending printouts
     */
    public void hook(final JTextArea outputArea) {
        /*
         * Check for Telnet control codes. Ref.
         * http://support.microsoft.com/kb/231866
         */                
        final byte IAC = (byte) 0xff;
        
        /*
         * This thread reads bytes from buffer and appends string to JTextArea
         * object.
         */
        Thread telnetHookThread = new Thread(new Runnable() {

            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int len;
                String line;
                try {
                    while ((len = in.read(buffer)) > 0) {
                    	
                    	int idx = 0;
                    	/*
                    	 * Ignore telnet codes.
                    	 */
                    	while ( (buffer[idx] == IAC) && (idx < len) ) {
                    		idx = idx + 3;
                    	}
                    	
                        line = new String(buffer, idx, len);
                        
                        /*
                         * Check if message is KEEPALIVE.
                         */
                        if (line.indexOf("KEEPALIVE") >= 0) {
                        	keepaliveFlag = true;
                        }
                        else {
                        	/*
                        	 * Append line to screen, JTextArea.
                        	 */
							outputArea.append(line);
							outputArea.setCaretPosition(outputArea.getText()
									.length());
	                        /*
	                         * Also append to printout holder with CR LF ending.
	                         */
	                        printout = printout + line + "\r\n";
                        }
                        
                    }
                } catch (IOException e) {
                    isConnected = false;
                }

            }

        }, "telnetHook");

        /*
         * Start thread.
         */
        telnetHookThread.start();

    }

    /**
     * <H1>Keepalive</H1><BR>
     * Get last status of keepalive. Each <I>sendKeepalive()</I> call will clear status.<BR>
     * 
     * @return <I>true</I> if KEEPALIVE message was received, <I>false</I>
     * after <I>sendKeepalive()</I> function call  
     */
    public boolean getKeepalive() {
        return keepaliveFlag;
    }
    
    /**
     * <H1>Printout</H1><BR>
     * Get last printout. Each <I>send()</I> call will clear printout.<BR>
     * This way, only last printout is available to process.<BR>
     * 
     * @return <I>printout</I> from remote host
     */
    public String getPrintout() {
        return printout;
    }
}
