package blagajna;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * <B>SyncClient</B><BR>
 * <BR>
 * This class implements client side of synchronization protocol, which
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
public class SyncClient {

    private final int BUFFER_LEN = 4096;
    
    private String hostname;
    private int port;   
    
    private boolean connected;
    
    private Socket clientSocket;

    private BufferedOutputStream clientOut;
    private BufferedInputStream clientIn;
    
    /**
     * <B>SyncClient</B> for synchronizing file(s) between server and client.<BR>
     * <BR>
     * <B>Protocol specification</B><BR>
     * <BR>
     * There are several commands available to clients recognized by server:<BR>
     * <UL>
     * FILE:<I>filename</I><BR>
     * GET:<I>filename</I><BR>
     * LIST:<I>pattern</I><BR>
     * INFO:<I>filename</I><BR>
     * </UL>
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
     * <TR><TD>(4)</TD><TD> </TD><TD>&lt;-------</TD><TD>data</TD></TR>
     * <TR><TD>...</TD><TD>...</TD><TD>...</TD><TD>...</TD></TR>
     * <TR><TD>(x)</TD><TD> CHUNK:<I>n</I> </TD><TD>-------&gt;</TD><TD></TD></TR>
     * <TR><TD>(x+1)</TD><TD> </TD><TD>&lt;-------</TD><TD>data</TD></TR>
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
     * This is example of code how to start SyncClient and upload file to server on tcp port 5002:
     * <PRE>
     * SyncClient client = new SyncClient();
     * client.connect("localhost", 5002);
     * client.sync("C:/Temp/test.zip");
     * client.disconnect();
     * </PRE>
     * Similar to upload is download procedure:
     * <PRE>
     * SyncClient client = new SyncClient();
     * client.connect("localhost", 5002);
     * client.get("myfile.zip", "C:/Temp/myfile.zip");
     * client.disconnect();
     * </PRE>
     * You should always finish session with <I>disconnect</I> call, to let remote side know
     * that file operations are done and tcp session should be closed.<BR>
     * <BR>
     * <B>NOTE:</B><BR>
     * Chunk size is defined in <I>BUFFER_LEN</I> variable and should remain <I>4096</I> bytes.
     * Otherwise, different value for chunk size might violate protocol.<BR>
     */    
    public SyncClient() {
        this.hostname = "";
        this.port = 0;
        
        connected = false;
        
        clientSocket = null;
        
        clientOut = null;
        clientIn = null;
    }
    
    /**
     * <H1>Connect</H1>
     * Establish a tcp connection to remote.
     * @param hostname server to connect to.
     * @param port tcp port number to connect to (by default 5002).
     */
    public void connect(String hostname, int port) throws IOException {                
        
        this.hostname = hostname;
        this.port = port;
        
        clientSocket = new Socket(hostname, port);
        clientSocket.setTcpNoDelay(true);

        clientOut = new BufferedOutputStream(clientSocket.getOutputStream());
        clientIn = new BufferedInputStream(clientSocket.getInputStream());
        
        connected = true;
    }
    
    /**
     * <H1>Disconnect</H1>
     * Disconnect from remote. This function will send QUIT message before closing
     * connection.<BR>
     */
    public void disconnect() throws IOException {                
                
        clientOut.write("QUIT".getBytes());
        clientOut.flush();
        
        clientIn.close();
        clientOut.close();
                
        clientSocket.close();
                
        connected = false;
    }    
    
    /**
     * Start file synchronization.<BR>
     * <BR>
     * <B>Synchronization overview</B><BR>
     * <BR>
     * (1) A data buffer is allocated in memory.<BR>
     * (2) <I>FILE:</I> message is sent to server to fetch crc16 list for a chosen file.<BR>
     * (3) A list with crc16 values is populated.<BR>
     * (4) A chosen file is examined, chunk by chunk, calculating crc16 for each chunk.<BR>
     * (5) If a calculated crc16 value is different than crc16 value from the list, chunk is sent to server in order to update file.<BR>
     * (6) If a chunk is not in the list (case when a local file is bigger than copy on server), chunk is sent to server in order to append it to file.<BR>
     * (7) At the end, <I>QUIT</I> message is sent to server to close session.<BR>
     * <BR>
     * <B>NOTE:</B><BR>
     * This is a blocking call. Please run it under a Thread if your application has to continue after this call.<BR>
     * @param fileName local name of the file. If name contains paths, it will be truncated on server.<BR>
     * Eg. <I>C:/Temp/myfile.zip</I> will truncate to <I>myfile.zip</I> on server.<BR>
     * @throws IOException
     */
    public void sync(String fileName) throws IOException {
        
        Crc16 checksum = new Crc16();
        
        List<Integer> chunkCrc16 = new ArrayList<Integer>();
        
        if (!connected) {
            throw new IOException();
        }
        
        File file = new File(fileName);
        byte [] fileData = new byte[BUFFER_LEN];
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        
        int len = 0;
        int chunkNum = 0;        
                
        byte[] initData = ("FILE:" + fileName).getBytes();        
        clientOut.write(initData);
        clientOut.flush();
        
        byte[] crc16buffer = new byte[2];
        while( (len = clientIn.read(crc16buffer)) > 0) {       
            int crc16value = ((crc16buffer[0] & 0xff) << 8) | (crc16buffer[1] & 0xff); 
            if(crc16value != 0) {
                chunkCrc16.add(crc16value);
            }
            else {
                break;
            }
        }
        
        len = 0;
        while(len >= 0) {
            len = dis.read(fileData, 0, BUFFER_LEN);
            if (len > 0) {
                int crc16value = checksum.calculate(fileData, 0, len);                
                boolean updateChunk = false;                
                if(chunkNum < chunkCrc16.size()) {
                    if(crc16value != chunkCrc16.get(chunkNum)) {
                        updateChunk = true;          
                    }
                }
                else {
                    updateChunk = true;
                }                
                if(updateChunk) {
                    boolean chunkUpdated = false;
                    int retry = 0;
                    while(!chunkUpdated && retry < 5) {
                        initData = ("CHUNK:" + chunkNum + "," + len).getBytes();
                        clientOut.write(initData);
                        clientOut.flush();
                        byte[] okBuffer = new byte[2];
                        clientIn.read(okBuffer);
                        if (okBuffer[0] == 79 && okBuffer[1] == 75) {
                            clientOut.write(fileData);
                            clientOut.flush();
                        }
                        clientIn.read(crc16buffer);
                        int crc16return = ((crc16buffer[0] & 0xff) << 8) | (crc16buffer[1] & 0xff);
                        if (crc16value == crc16return) {
                            chunkUpdated = true;
                        }
                        else {
                            retry++;
                        }
                    }
                }
                chunkNum++;
            }            
        }

        dis.close();

    }
    
    /**
     * Start file download.<BR>
     * <BR>
     * <B>Download overview</B><BR>
     * <BR>
     * (1) A data buffer is allocated in memory.<BR>
     * (2) <I>GET:</I> message is sent to server to fetch crc16 list for a chosen file.<BR>
     * (3) A list with crc16 values is populated.<BR>
     * (4) A chosen file is examined if it exists, chunk by chunk, calculating crc16 for each chunk.<BR>
     * (5) If a calculated crc16 value is different than crc16 value from the list, chunk request is sent to server in order to fetch it.<BR>
     * (6) A chunk sent by server should be written to output file at chunk position.<BR>
     * (7) At the end, <I>QUIT</I> message is sent to server to close session.<BR>
     * <BR>
     * <B>NOTE:</B><BR>
     * This is a blocking call. Please run it under a Thread if your application has to continue after this call.<BR>
     * @param inputFile remote name of the file. If name contains paths, it will be truncated on server.<BR>
     * @param outputFile local name of the file. If it does not exist, a new file will be created.<BR>
     * @throws IOException
     */    
    public void get(String inputFile, String outputFile) throws IOException {
        
        Crc16 checksum = new Crc16();
        
        List<Integer> chunkCrc16 = new ArrayList<Integer>();
        
        if (!connected) {
            throw new IOException();
        }        
        
        File file = new File(outputFile);
        byte [] fileData = new byte[BUFFER_LEN];

        RandomAccessFile raf = new RandomAccessFile(file, "rw");

        int len = 0;
        int chunkNum = 0;        
                
        clientOut.write(("GET:" + inputFile).getBytes());
        clientOut.flush();
        
        byte[] crc16buffer = new byte[2];
        while( (len = clientIn.read(crc16buffer)) > 0) {       
            int crc16value = ((crc16buffer[0] & 0xff) << 8) | (crc16buffer[1] & 0xff); 
            if(crc16value != 0) {
                chunkCrc16.add(crc16value);
            }
            else {
                break;
            }
        }
        
        for (chunkNum = 0; chunkNum < chunkCrc16.size(); chunkNum++) {
            boolean fetchChunk = false;            
            
            raf.seek(chunkNum * BUFFER_LEN);
            len = raf.read(fileData, 0, BUFFER_LEN);

            if (len > 0) {
                int crc16value = checksum.calculate(fileData, 0, len);
                if(crc16value != chunkCrc16.get(chunkNum)) {
                    fetchChunk = true;          
                }
            }
            else {
                fetchChunk = true;
            }
            
            if (fetchChunk) {

                clientOut.write(("CHUNK:" + chunkNum).getBytes());
                clientOut.flush();

                len = clientIn.read(fileData, 0, BUFFER_LEN);

                if (len > 0) {
                    raf.seek(chunkNum * BUFFER_LEN);
                    raf.write(fileData, 0, len);
                }
                else {
                    System.out.println("This is problem: a crc16 value for chunk " + chunkNum + " received," +
                            " but no data available to read.");
                }
                
            }

        }
        
        raf.setLength(raf.getFilePointer());
        raf.close();

    }
    
    /**
     * List files and folders.<BR>
     * <BR>
     * <B>List overview</B><BR>
     * <BR>
     * (1) A data buffer is allocated in memory.<BR>
     * (2) <I>LIST:</I> message is sent to server to fetch list of files and folders.<BR>
     * (3) A list is received.<BR>
     * (4) Files and folders are separated by colon (:).<BR>
     * (5) Folders in list have slash (/) ending in name, thus making them recognizable for further operations.<BR>
     * (6) At the end, <I>QUIT</I> message is sent to server to close session.<BR>
     * <BR>
     * <B>NOTE:</B><BR>
     * This is a blocking call. Please run it under a Thread if your application has to continue after this call.<BR>
     * @param pattern is wildcard (*), exact file name, folder name, or file pattern containing wildcard.<BR>
     * @return list of items (files and folders) separated by colon (:). If nothing is matched, then single colon (:) char is returned.
     * @throws IOException
     */ 
    public String list(String pattern) throws UnknownHostException, IOException {
        
        String retVal = "";
        
        if (!connected) {
            throw new IOException();
        }
        
        byte [] fileList = new byte[BUFFER_LEN];
        
        if (pattern == null) {
            pattern = "*";
        }

        if (pattern.length() == 0) {
            pattern = "*";
        }
        
        byte[] initData = ("LIST:" + pattern).getBytes();        
        clientOut.write(initData);
        clientOut.flush();


        int len;
        while ((len = clientIn.read(fileList)) > 0) {                   
            String line = new String(fileList, 0, len);
            retVal = retVal + line;
            String endMarker = "END"; 
            if (line.endsWith(endMarker)) {
                retVal = retVal.substring(0,
                        retVal.length() - endMarker.length());
                break;
            }
        }
                
        return retVal;
    }
    
    /**
     * Info for files and folders.<BR>
     * <BR>
     * <B>Info overview</B><BR>
     * <BR>
     * (1) A data buffer is allocated in memory.<BR>
     * (2) <I>INFO:</I> message is sent to server to fetch file or folder details.<BR>
     * (3) A string is received.<BR>
     * (4) String format is: TYPE:<I>type</I>,SIZE:<I>size</I>,DATE:<I>dd-MM-yyyy</I>.<BR>
     * (5) At the end, <I>QUIT</I> message is sent to server to close session.<BR>
     * <BR>
     * <B>NOTE:</B><BR>
     * This is a blocking call. Please run it under a Thread if your application has to continue after this call.<BR>
     * @param file is name of file or folder on remote side (server). <BR>
     * @return A string: TYPE:<I>type</I>,SIZE:<I>size</I>,DATE:<I>dd-MM-yyyy</I> is returned.
     *          If file does not exist, a dash (-) char is returned.<BR>
     * @throws IOException
     */ 
    public String info(String file) throws UnknownHostException, IOException {
        
        String retVal = "";
        
        if (!connected) {
            throw new IOException();
        }
        
        byte [] fileList = new byte[BUFFER_LEN];
        
        if (file == null) {
            return null;
        }

        if (file.length() == 0) {
            return "";
        }
        
        byte[] initData = ("INFO:" + file).getBytes();        
        clientOut.write(initData);
        clientOut.flush();

        int len = clientIn.read(fileList);                   
        String line = new String(fileList, 0, len);
        retVal = retVal + line;
                
        return retVal;
    }
    
}
