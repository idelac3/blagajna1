package blagajna;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 
public class Zipper
{
    private List<String> fileList;

    /**
     * Create <B>Zipper</B> object to make zip files in archive.<BR>
     */
    public Zipper(){        
        fileList = new ArrayList<String>();
    }
 
    /**
     * Zip it.
     * @param outputZipFile output zip file.
     * @throws IOException 
     */
    public void zipIt(String outputZipFile) throws IOException {

        byte[] buffer = new byte[1024];

        FileOutputStream fos = new FileOutputStream(outputZipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);

        for (String file : this.fileList) {

            ZipEntry ze = new ZipEntry(file);
            zos.putNextEntry(ze);

            FileInputStream in = new FileInputStream(file);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
        }

        zos.closeEntry();
        // remember close it
        zos.close();
       
    }

    /**
     * Add file to list.<BR>  
     * @param file to include in zip archive.
     */
    public void addFile(String fileName){
        File file = new File(fileName);
        if (file.exists()) {
            fileList.add(fileName);
        }
    }
 
    /**
     * This method will clean file list and allow you to add files again.
     */
    public void clearFiles() {
        fileList.clear();
    }
}