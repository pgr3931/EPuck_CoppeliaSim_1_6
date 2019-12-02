package at.fhv.dgr1992.helper;

/**
 * Determines the Operating System the application is running on
 *
 *
 * Source: https://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
 *
 */
public class OSValidator {

    private static String OS = System.getProperty("os.name").toLowerCase();

    private static String windowsArch = System.getenv("PROCESSOR_ARCHITECTURE");

    private static String windowsWow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    public static boolean isWindows64Bit(){
        if(windowsArch != null){
            System.out.println(windowsArch);
        }
        if(windowsWow64Arch != null){
            System.out.printf(windowsWow64Arch);
        }
        return (windowsArch != null && windowsArch.endsWith("64")) || (windowsWow64Arch != null && windowsWow64Arch.endsWith("64")) ? true : false;
    }

    public static boolean isLinux64Bit(){
        return System.getProperty("os.arch").endsWith("64")? true : false;
    }

    public static boolean isSolaris() {

        return (OS.indexOf("sunos") >= 0);

    }
}