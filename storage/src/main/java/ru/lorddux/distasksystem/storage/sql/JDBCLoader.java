package ru.lorddux.distasksystem.storage.sql;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;

public class JDBCLoader {
    public static Driver loadDriver(String jarPath, String className)
            throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        URL u = new URL(String.format("jar:file:%s!/", jarPath));
        URLClassLoader ucl = new URLClassLoader(new URL[]{u});
        return (Driver) Class.forName(className, true, ucl).newInstance();
    }
}
