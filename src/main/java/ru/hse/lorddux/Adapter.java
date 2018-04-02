package ru.hse.lorddux;

import ru.hse.lorddux.config.Configuration;
import ru.hse.lorddux.executor.PythonExecutor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;

/**
 * the main class if the worker
 * obtains configuration.
 * starts up executors, queue processors
 */
public class Adapter {

    public static void main(String[] args) throws Exception {
//        ProcessBuilder probuilder = new ProcessBuilder( "python", "C:\\test.py");
//        Process p = probuilder.start();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        String s;
//        System.out.println("kek");
//        while ((s = reader.readLine()) != null) {
//            System.out.println(s);
//        }
        PythonExecutor e = new PythonExecutor("python", "C:\\test.py", 10);
        System.out.println(e.processTask("kk"));
    }
}
