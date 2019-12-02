package at.fhv.dgr1992.vRepApiLibraries;

import at.fhv.dgr1992.helper.OSValidator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class LibraryLoader {
//    private final static String LIBREMOTEAPIJAVA = "libremoteApiJava";
    private final static String LIBREMOTEAPIJAVA = "remoteApiJava";

    public static void loadLibrary(){
        if(OSValidator.isWindows()){
            if(OSValidator.isWindows64Bit()) {
                loadLib("Windows"+File.separator+"64Bit"+File.separator+LIBREMOTEAPIJAVA+".dll","dll");
            } else {
                loadLib("Windows"+File.separator+"32Bit"+File.separator+LIBREMOTEAPIJAVA+".dll","dll");
            }
        } else if(OSValidator.isMac()){
            loadLib("Mac"+File.separator+LIBREMOTEAPIJAVA+".dylib","dylib");
        } else if(OSValidator.isUnix() || OSValidator.isSolaris()){
            if(OSValidator.isLinux64Bit()) {
                loadLib("Linux"+File.separator+"64Bit"+File.separator+LIBREMOTEAPIJAVA+".so","so");
            } else {
                loadLib("Linux"+File.separator+"32Bit"+File.separator+LIBREMOTEAPIJAVA+".so","so");
            }
        } else {
            System.out.println("Error: Unknown Operating System!!");
        }
    }

    /**
     * Puts library to temp dir and loads to memory
     */
    private static void loadLib(String path, String fileExtension) {
        try {
            //Create a stream containing the library which needs to be written to the drive
            InputStream in = LibraryLoader.class.getResourceAsStream(path);

            //Path to the folder with lib
            String libFolder = System.getProperty("java.io.tmpdir")+File.separator+"vrep";

            //Create the folder for lib file
            new File(libFolder).mkdirs();

            //Write to a tmp file so that multiple instances can be run
            File fileOut = File.createTempFile(LIBREMOTEAPIJAVA,"."+ fileExtension,new File(libFolder));

            //Define that this temporary file needs to be deleted when the applications exits
            fileOut.deleteOnExit();
            System.out.println("Writing library to: " + fileOut.getAbsolutePath());

            //Create a output stream to temporary file and start copying
            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);

            //Close all streams
            in.close();
            out.close();

            try {
                //Load the library
                System.load(fileOut.toString());
            } catch (Exception ex){
                System.out.println(ex);
                System.exit(0);
            }

        } catch (Exception e) {
            System.out.println("Failed to load required library \n" + e.getMessage());
        }
    }
}
