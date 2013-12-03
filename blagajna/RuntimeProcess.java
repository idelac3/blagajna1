package blagajna;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class RuntimeProcess implements Runnable {
    
    //String[] cmd = new String[] { "cmd.exe", "/K", "dir" };
    //String[] cmd = new String[] { "cmd.exe", "/C", "" };
    
    private String[] cmd;
    
    private StringBuilder printout;
    
    private Process proc;
    
    private InputStreamReader stdIn;
    private InputStreamReader stdErr;
    private OutputStreamWriter stdOut; 

    BufferedOutputStream out;
    
    public RuntimeProcess(String[] cmd, BufferedOutputStream out) {
    
    	this.out = out;
    	
        this.cmd = cmd;
        printout = new StringBuilder();

    }

    public void run() {

        try {

            proc = Runtime.getRuntime().exec(cmd);
           
            stdIn = new InputStreamReader(proc.getInputStream());
            stdErr = new InputStreamReader(proc.getErrorStream());
            stdOut = new OutputStreamWriter(proc.getOutputStream());            
            
            int chr;
            
            while ( (chr = stdIn.read()) > 0) {
            	
            	while (stdErr.ready()) {
            		int chr1 = stdErr.read();
                	char c = (char)chr1;
                    printout.append(c);          
                    
                    out.write(chr1);
                    out.flush();            		
            	}

            	char c = (char)chr;
                printout.append(c);          
                
                out.write(chr);
                out.flush();
            }
            stdIn.close();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }

        System.out.println("End.");

    }

    public void terminate() throws IOException {        
        proc.destroy();
    }
    
    public String getPrintout() {
    	String retVal = printout.toString();
    	printout = new StringBuilder();
        return retVal;
    }

    public void send(int chr) throws IOException {
        stdOut.write(chr);
        stdOut.flush();
    }
    
    public void send(String command) throws IOException {   	
        stdOut.write(command + "\n");
        stdOut.flush();
    }
    
}
