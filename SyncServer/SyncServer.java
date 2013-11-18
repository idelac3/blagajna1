package SyncServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <B>SyncServer</B><BR>
 * <BR>
 * This class implements server side of synchronization protocol, which
 * supports file uploads, downloads, list of files and folders, and info
 * for each file or folder.<BR>
 * <BR>
 * Protocol uses crc16 checksums to determine if file chunk (by default 4096 bytes)
 * should be uploaded or downloaded. This reduces network transmission when chunks of file
 * are equal on server and on client.<BR>
 * <BR>
 * Another application is to list files on server by desired criteria (<I>pattern</I>) and
 * download them on client side.<BR>
 * @author eigorde
 *
 */
public class SyncServer implements Runnable {

    final int BUFFER_LEN = 4096;

    private String logEntry = "";

    private Socket serverSocket;

    private String rootDir;
    
    /**
     * <B>SyncServer</B> for synchronizing file(s) between server and client.<BR>
     * <BR>
     * <B>Protocol specification</B><BR>
     * <BR>
     * There are several commands available and recognized by server:<BR>
     * <UL>
     * FILE:<I>filename</I><BR>
     * GET:<I>filename</I><BR>
     * LIST:<I>pattern</I><BR>
     * INFO:<I>filename</I><BR>
     * </UL>
     * Each command is a string encapsulated in markers for start and end.<BR>
     * By default, start marker is <I>##BEGIN##</I> and end marker is <I>##END##</I>.
     * Markers assure that commands are recognized during transmission of data.<BR>
     * For example, <I>FILE</I> command will be sent as:<BR>
     * <I>##BEGIN##FILE:myData.zip##END##</I><BR>
     * to server. In general, every command or replay sent in string is encapsulated in markers.<BR>
     * <BR>
     * In the following tables, for illustration all commands and replies are without markers for easier reading.<BR>
     * <BR>
     * <B>FILE</B><BR>
     * <BR>
     * This command should accept file upload from client, by sending calculated crc16 values
     * for each chunk of file (by default 4096 byte chunk size), and then wait for client decision
     * which chunk will be updated by client request. Client then sends new data for selected chunk.<BR>
     * <BR>
     * Table below shows exact protocol message exchange.<BR>
     * <BR>
     * <TABLE border=2>
     * <TR><TH>id</TH><TH>client</TH><TH>description</TH><TH>server</TH></TR>
     * <TR><TD>(1)</TD><TD> FILE:<I>myfile.zip</I></TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(2)</TD><TD> </TD><TD>&lt;-------</TD><TD>crc16 values</TD></TR>
     * <TR><TD>(3)</TD><TD> CHUNK:<I>1</I>,<I>4096</I> </TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(4)</TD><TD> </TD><TD>&lt;-------</TD><TD>OK</TD></TR>
     * <TR><TD>(5)</TD><TD> chunk, data bytes</TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(6)</TD><TD> </TD><TD>&lt;-------</TD><TD>crc16 value of first chunk</TD></TR>
     * <TR><TD>...</TD><TD>...</TD><TD>...</TD><TD>...</TD></TR>
     * <TR><TD>(x)</TD><TD> CHUNK:<I>n</I>,<I>size</I> </TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(x+1)</TD><TD> </TD><TD>&lt;-------</TD><TD>OK</TD></TR>
     * <TR><TD>(x+2)</TD><TD> chunk, data bytes</TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(x+3)</TD><TD> </TD><TD>&lt;-------</TD><TD>crc16 value of received chunk</TD></TR>
     * <TR><TD>last</TD><TD> QUIT</TD><TD>-------&gt;</TD><TD></TD></TR>
     * </TABLE><BR>
     * <BR>
     * <B>GET</B><BR>
     * <BR>
     * This command should accept file download from client, by sending calculated crc16 values
     * for each chunk of file (if file exist), and then wait for client decision
     * which chunk will be downloaded by client request. Client then sends request for selected chunk.<BR>
     * <BR>
     * Table below shows exact protocol message exchange.<BR>
     * <BR>
     * <TABLE border=2>
     * <TR><TH>id</TH><TH>client</TH><TH>description</TH><TH>server</TH></TR>
     * <TR><TD>(1)</TD><TD> GET:<I>myfile.zip</I></TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(2)</TD><TD> </TD><TD>&lt;-------</TD><TD>crc16 values</TD></TR>
     * <TR><TD>(3)</TD><TD> CHUNK:<I>1</I> </TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(4)</TD><TD>  </TD><TD>&lt;-------</TD><TD>SIZE:<I>4096</I></TD></TR>
     * <TR><TD>(5)</TD><TD> OK </TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(6)</TD><TD> </TD><TD>&lt;-------</TD><TD>data</TD></TR>
     * <TR><TD>...</TD><TD>...</TD><TD>...</TD><TD>...</TD></TR>
     * <TR><TD>(x)</TD><TD> CHUNK:<I>n</I> </TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(x+1)</TD><TD>  </TD><TD>&lt;-------</TD><TD>SIZE:<I>xx</I></TD></TR>
     * <TR><TD>(x+2)</TD><TD> OK </TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(x+3)</TD><TD> </TD><TD>&lt;-------</TD><TD>data</TD></TR>
     * <TR><TD>last</TD><TD> QUIT</TD><TD>-------&gt;</TD><TD></TD></TR>
     * </TABLE><BR>
     * <B>LIST</B><BR>
     * <BR>
     * This command should return list of files and folders by matching pattern. A
     * <I>pattern</I> is either a single wildcard char (*), exact file name, exact folder
     * name, or wildcard file name.<BR>
     * A list of items is separated by single colon char (:).<BR>
     * <BR>
     * <B>Matching rules</B><BR>
     * 1. If <I>pattern</I> is <I>folder</I>, return list of its files and folders.<BR>
     * 2. If <I>pattern</I> is single wildcard, return list of files and folders in root directory.<BR>
     * 3. If <I>pattern</I> is contains wildcard, return list of files matching pattern.<BR>
     * If nothing is matched by pattern, a single colon char is returned.<BR>
     * <BR>
     * Table below shows exact protocol message exchange.<BR>
     * <BR>
     * <TABLE border=2>
     * <TR><TH>id</TH><TH>client</TH><TH>description</TH><TH>server</TH></TR>
     * <TR><TD>(1)</TD><TD> LIST:<I>*.zip</I></TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(2)</TD><TD> </TD><TD>&lt;-------</TD><TD><I>file1.zip:file2.zip:file3.zip:</I></TD></TR>
     * <TR><TD>last</TD><TD> QUIT</TD><TD>-------&gt;</TD><TD></TD></TR>
     * </TABLE><BR>
     * <BR>
     * <B>INFO</B><BR>
     * <BR>
     * This command should return file or folder info, by sending type, size, and date.
     * If the file does not exist, then a dash char (-) is returned.<BR>
     * <BR>
     * Table below shows exact protocol message exchange.<BR>
     * <BR>
     * <TABLE border=2>
     * <TR><TH>id</TH><TH>client</TH><TH>description</TH><TH>server</TH></TR>
     * <TR><TD>(1)</TD><TD> INFO:<I>myfile.zip</I></TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(2)</TD><TD> </TD><TD>&lt;-------</TD><TD>TYPE:<I>type</I>,SIZE:<I>size</I>,DATE:<I>date</I></TD></TR>
     * <TR><TD>last</TD><TD> QUIT</TD><TD>-------&gt;</TD><TD></TD></TR>
     * </TABLE><BR>
     * Value for <I>type</I> is: <I>file</I> or <I>directory</I>.<BR>
     * Value for <I>size</I> is in bytes. If TYPE value is <I>directory</I>, then size is 0.<BR>
     * Value for <I>date</I> is in <I>dd-MM-yyyy</I> format.<BR>
     * <BR>
     * <B>Example code</B><BR>
     * <BR>
     * This is example of code how to start SyncServer on tcp port 5002 and use current directory as root directory:<BR><BR>
     * <UL>
     * <I>ServerSocket listener = new ServerSocket(5002);</I><BR>
     * <I>Socket clientSocket;</I><BR><BR>
     * <I>// Accept clients</I><BR>
     * <I>while(true) {</I><BR>
     * <UL><I>SyncServer syncServer;</I><BR>
     * <I>clientSocket = listener.accept();</I><BR>
     * <I>syncServer = new SyncServer(clientSocket, System.getProperty("user.dir"));</I><BR>
     * <I>Thread t = new Thread(syncServer);</I><BR>
     * <I>t.start();</I><BR>
     * </UL>
     * <I>}</I><BR><BR>
     * <I>listener.close();</I>
     * </UL>
     * <BR><BR>
     * <B>NOTE:</B> This implementation handles more clients at same time.<BR>
     * @param serverSocket a socket formed when client connects to server
     * @param rootDir a root directory where synchronization and downloads are allowed
     */
    public SyncServer(Socket serverSocket, String rootDir) {
        this.serverSocket = serverSocket;
        this.rootDir = rootDir;
    }

    /**
     * Start <B>SyncServer</B>.<BR>
     * <B>NOTE:</B> This call will block execution of your code.<BR>
     * Make sure you put it in Thread if you want to continue.<BR>
     */
    public void run() {

        Crc16 checksum = new Crc16();

        List<Integer> chunkCrc16 = new ArrayList<Integer>();

        /*
         * Holders for FILE and GET requests by clients.
         */
        String FILEfilename = "";        
        String GETfilename = "";

        /*
         * Buffer and length.
         */
        byte[] buffer = new byte[BUFFER_LEN];
        int len = 0;

        /*
         * Markers for start and end.
         */
        final String markerStart = "##BEGIN##";
        final String markerEnd = "##END##";
        
        /*
         * Quit flag to terminate while loop.
         */
        boolean quitFlag = false;
        
        /*
         * Compatibility flag.
         */
        boolean compatibilityFlag = false;
        
        try {

            BufferedOutputStream testOut = new BufferedOutputStream(serverSocket.getOutputStream());
            BufferedInputStream testIn = new BufferedInputStream(serverSocket.getInputStream());

            //while ((len = testIn.read(buffer)) > 0) {
            while (!quitFlag) {
                //String serverCmd = new String(buffer, 0, len, "UTF-8");
            	String serverCmd = "";
            	for (byte data; (data = (byte) testIn.read()) > -1;) {
            		serverCmd = serverCmd + String.valueOf( (char)data );
            		if (serverCmd.startsWith(markerStart) && serverCmd.endsWith(markerEnd)) {
            			int beginIndex = markerStart.length();
            			int endIndex = serverCmd.indexOf(markerEnd);
            			serverCmd = serverCmd.substring(beginIndex, endIndex);
            			compatibilityFlag = false;
            			break;
            		}
            		else if (serverCmd.startsWith("FILE:") && serverCmd.endsWith(".zip")) {
            			// Just for compatiblity for old clients who upload only zip files
            			// via FILE method.
            			compatibilityFlag = true;
            			break;
            		}
            		else {
            			if (compatibilityFlag) {
            				if (serverCmd.startsWith("CHUNK:")) {
            					len = testIn.read(buffer);
            					serverCmd = serverCmd + new String(buffer, 0, len);
            					break;
            				}
            				else if (serverCmd.startsWith("QUIT")) {
            					break;
            				}
            			}
            		}
            	}
            	
                if (serverCmd.startsWith("FILE:")) {
                    serverCmd = serverCmd.substring("FILE:".length());
                    while (serverCmd.indexOf('/') >= 0) {
                        serverCmd = serverCmd.substring(serverCmd.indexOf('/') + 1);
                    }
                    while (serverCmd.indexOf('\\') >= 0) {
                        serverCmd = serverCmd.substring(serverCmd.indexOf('\\') + 1);
                    }
                    FILEfilename = this.rootDir + "/" + serverCmd;
                    GETfilename = "";
                    wr("File = " + FILEfilename);
                    File file = new File(FILEfilename);
                    chunkCrc16.clear();
                    if (file.exists()) {

                        byte[] fileData = new byte[BUFFER_LEN];
                        DataInputStream dis = new DataInputStream(new FileInputStream(file));

                        while (len >= 0) {
                            len = dis.read(fileData, 0, BUFFER_LEN);
                            if (len > 0) {
                                int crc16value = checksum.calculate(fileData, 0, len);
                                chunkCrc16.add(crc16value);
                                wr("Checksum: " + String.format("%02X", crc16value));
                            }
                        }

                        dis.close();
                    }
                    chunkCrc16.add(0);

                    for (Integer value : chunkCrc16) {
                        byte[] crc16value = { 0x00, 0x00 };
                        int chunkChecksum = value;
                        crc16value[0] = (byte) ((chunkChecksum >> 8) & 0xff);
                        crc16value[1] = (byte) (chunkChecksum & 0xff);
                        testOut.write(crc16value);
                    }
                    testOut.flush();
                } else if (serverCmd.startsWith("CHUNK:") && FILEfilename.length() > 0) {
                    int chunkNum = getNumeric(serverCmd.substring(serverCmd.indexOf(':') + 1, serverCmd.indexOf(',')));
                    int chunkSize = getNumeric(serverCmd.substring(serverCmd.indexOf(',') + 1));
                    wr("Chunk: " + chunkNum + ", Size: " + chunkSize);
                    
                    if (compatibilityFlag) {                    
                    	testOut.write("OK".getBytes());
                    }
                    else {
                    	testOut.write((markerStart + "OK" + markerEnd).getBytes());
                    }                    
                    testOut.flush();
                    
                    int dataRead = 0;
                    while (dataRead < chunkSize) {
                        dataRead = dataRead + testIn.read(buffer, dataRead, chunkSize - dataRead);
                    }

                    File file = new File(FILEfilename);
                    RandomAccessFile raf = new RandomAccessFile(file, "rw");
                    raf.seek(chunkNum * BUFFER_LEN);
                    raf.write(buffer, 0, chunkSize);
                    if (chunkSize < BUFFER_LEN) {
                        raf.setLength(chunkNum * BUFFER_LEN + chunkSize);
                    }
                    raf.close();

                    int chunkChecksum = checksum.calculate(buffer, 0, chunkSize);
                    byte[] crc16value = { 0x00, 0x00 };
                    crc16value[0] = (byte) ((chunkChecksum >> 8) & 0xff);
                    crc16value[1] = (byte) (chunkChecksum & 0xff);
                    wr("Chunk: " + chunkNum + " updated, Crc16: " + String.format("%02X%02X", crc16value[0], crc16value[1]));
                    testOut.write(crc16value);
                    testOut.flush();
                } else if (serverCmd.startsWith("GET:")) {
                    serverCmd = serverCmd.substring("GET:".length());
                    while (serverCmd.startsWith("/")) {
                        serverCmd = serverCmd.substring(1);
                    }
                    GETfilename = this.rootDir + "/" + serverCmd;
                    FILEfilename = "";
                    wr("Get = " + GETfilename);
                    File file = new File(GETfilename);
                    chunkCrc16.clear();
                    if (file.exists()) {

                        byte[] fileData = new byte[BUFFER_LEN];
                        DataInputStream dis = new DataInputStream(new FileInputStream(file));

                        len = 0;
                        while (len >= 0) {
                            len = dis.read(fileData, 0, BUFFER_LEN);
                            if (len > 0) {
                                int crc16value = checksum.calculate(fileData, 0, len);
                                chunkCrc16.add(crc16value);
                                wr("Checksum: " + String.format("%02X", crc16value));
                            }
                        }

                        dis.close();
                    }
                    chunkCrc16.add(0);

                    for (Integer value : chunkCrc16) {
                        byte[] crc16value = { 0x00, 0x00 };
                        int chunkChecksum = value;
                        crc16value[0] = (byte) ((chunkChecksum >> 8) & 0xff);
                        crc16value[1] = (byte) (chunkChecksum & 0xff);
                        testOut.write(crc16value);
                    }
                    testOut.flush();          
                } else if (serverCmd.startsWith("CHUNK:") && GETfilename.length() > 0) {                    
                    int chunkNum = getNumeric(serverCmd.substring(serverCmd.indexOf(':') + 1));
                    
                    File file = new File(GETfilename);
                    
                    if (file.exists()) {
                        
                        RandomAccessFile raf = new RandomAccessFile(file, "r");
                        raf.seek(chunkNum * BUFFER_LEN);
                        len = raf.read(buffer, 0, BUFFER_LEN);
                        raf.close();
                        
                        testOut.write((markerStart + "SIZE:" + String.valueOf(len) + markerEnd).getBytes());
                        testOut.flush();
                        
                        byte[] okBuffer = new byte[markerStart.length() + "OK".length() + markerEnd.length()];
                        int okLen = testIn.read(okBuffer);
                        String okReply = new String(okBuffer, 0, okLen);
                        
                        if (okReply.indexOf("OK") > 0) {                        
                        	testOut.write(buffer, 0, len);  
                        }
                        else {
                        	wr("OK not found in reply, after SIZE is sent.");
                        }
                        
                    }
                    
                    testOut.flush();
                } else if (serverCmd.startsWith("LIST:")) {
                    String pattern = serverCmd.substring("LIST:".length());
                    wr("List = " + pattern);
                    
                    String retVal = "";
                    File rootDir = new File(this.rootDir);
                    
                    if (pattern.isEmpty() || pattern.equalsIgnoreCase("*")) {                        
                        for (File f : rootDir.listFiles()) {
                            if (f.isDirectory()) {
                                retVal = retVal + f.getName() + "/:";
                            }
                            else if (f.isFile()) {
                                retVal = retVal + f.getName() + ":";
                            }
                        }                        
                    }
                    else if (pattern.indexOf('*') >= 0) {
                        int idx = pattern.indexOf('*');
                        String p1 = pattern.substring(0, idx);
                        String p2 = pattern.substring(idx + 1);
                        
                        for (File f : rootDir.listFiles()) {
                            if (f.isFile()) {
                                String fn = f.getName();
                                boolean flag = false;
                                if (p1.length() > 0 && p2.length() > 0) {
                                    flag = (fn.startsWith(p1) && fn.endsWith(p2));
                                }
                                else if (p1.length() > 0) {
                                    flag = fn.startsWith(p1);
                                }
                                else if (p2.length() > 0) {
                                    flag = fn.endsWith(p2);
                                }
                                if (flag) {
                                    retVal = retVal + fn + ":";
                                }
                            }
                        }
                    }
                    else {
                        File f = new File(this.rootDir + "/" + pattern);
                        if (f.isDirectory()) {
                            for (File m : f.listFiles()) {
                                if (m.isDirectory()) {
                                    retVal = retVal + m.getName() + "/:";
                                } else if (m.isFile()) {
                                    retVal = retVal + m.getName() + ":";
                                }
                            }
                        }
                    }
                    
                    if (retVal.length() == 0) {
                        retVal = ":";
                    }
                    
                    retVal = markerStart + retVal + markerEnd;
                    
                    testOut.write(retVal.getBytes());
                    testOut.flush();
                } else if (serverCmd.startsWith("INFO:")) {
                    String item = serverCmd.substring("INFO:".length());
                    wr("Info = " + item);
                    
                    String retVal = "-";
                    File f = new File(this.rootDir + "/" + item);                    
                    
                    if (f.exists()) {
                        String fType = "-";
                        String fSize = "0";
                        if (f.isFile()) {
                            fType = "file";
                            fSize = String.valueOf(f.length());
                        } else if (f.isDirectory()) {
                            fType = "directory";
                            fSize = "0";
                        }
                        String fDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date(f.lastModified()));
                        retVal = "TYPE:" + fType + ",SIZE:" + fSize + ",DATE:" + fDate;
                    }
                    
                    retVal = markerStart + retVal + markerEnd;
                    
                    testOut.write(retVal.getBytes());
                    testOut.flush();
                } else if (serverCmd.startsWith("CHECKSUM:")) {
                	String item = serverCmd.substring("CHECKSUM:".length());
                    wr("Checksum = " + item);
                    
                    File file = new File(this.rootDir + item);
                    chunkCrc16.clear();
                    
                    if (file.exists()) {

                        byte[] fileData = new byte[BUFFER_LEN];
                        DataInputStream dis = new DataInputStream(new FileInputStream(file));

                        len = 0;
                        while (len >= 0) {
                            len = dis.read(fileData, 0, BUFFER_LEN);
                            if (len > 0) {
                                int crc16value = checksum.calculate(fileData, 0, len);
                                chunkCrc16.add(crc16value);
                                wr("Checksum: " + String.format("%02X", crc16value));
                            }
                        }

                        dis.close();
                    }
                    chunkCrc16.add(0);

                    for (Integer value : chunkCrc16) {
                        byte[] crc16value = { 0x00, 0x00 };
                        int chunkChecksum = value;
                        crc16value[0] = (byte) ((chunkChecksum >> 8) & 0xff);
                        crc16value[1] = (byte) (chunkChecksum & 0xff);
                        testOut.write(crc16value);
                    }
                    testOut.flush();
                    
                } else if (serverCmd.startsWith("QUIT")) {
                    quitFlag = true;
                }
            }

            testIn.close();
            testOut.close();

            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void wr(String arg) {
        logEntry = logEntry + arg + "\n";
    }

    /**
     * Parse numbers in begining, and truncate the rest.<BR>
     * Only integers.
     * @param str string which holds number
     * @return a valid integer 
     */
    private int getNumeric(String str) {
    	String retVal = "0";
    	int len = str.length();
    	for (int i = 0; i < len; i++) {
    		if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
    			retVal = retVal + str.charAt(i);
    		}
    		else {
    			// break on first non numeric char
    			break;
    		}
    	}
    	return Integer.parseInt(retVal);
    }
    
    /**
     * Log entries collected during SyncServer operation.
     * @return log entry.
     */
    public String getLogEntry() {
        return logEntry;
    }
}

