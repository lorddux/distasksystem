package ru.hse.lorddux.utils;

import java.io.File;

public class PathManager {
    private static final String DEFAULT_SUBFOLDER = "var";
    private static final char PATH_SEPARATOR = File.separatorChar;

    private static String subfolder = DEFAULT_SUBFOLDER;

    /**
     *
     * @param clazz
     * @return
     */
    public static String getDefaultClassPath(Class clazz) {
        StringBuilder path = new StringBuilder(subfolder);
        appendDir(path, clazz.getCanonicalName());
        File checkDir = new File(path.toString());
        if (! checkDir.exists()) {
//            checkDir.mkdirs();
        }
        return path.toString();
    }

    /**
     *
     * @param clazz
     * @param filename
     * @return
     */
    public static String getDefaultClassFilePath(Class clazz, String filename) {
        StringBuilder path = new StringBuilder(getDefaultClassPath(clazz));
        appendDir(path, filename);
        return path.toString();
    }

    /**
     *
     * @param dir
     * @return
     */
    public static boolean createDir(String dir) {
        File checkDir = new File(dir);
        if (! checkDir.exists()) {
//            checkDir.mkdirs();
            return true;
        }
        return false;
    }

    public static String getSubfolder() {
        return subfolder;
    }

    public static void setSubfolder(String subfolder) {
        PathManager.subfolder = subfolder;
    }

    private static void appendDir(StringBuilder pathBuilder, String dir) {
        if (pathBuilder.length() > 0) {
            pathBuilder.append(PATH_SEPARATOR);
        }
        pathBuilder.append(dir);
    }
}
