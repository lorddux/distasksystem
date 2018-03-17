package executer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Scanner;

public class PythonExecutor implements Executor {
    public void loadArtifact(String url) throws Exception {

    }

    public void run() {
        ProcessBuilder p = new ProcessBuilder("python", "C:\\Users\\Алексей\\Desktop\\labs\\LAB2\\tcpclient.py", "45150", "1", "1", "0");
        try {
            Process pr = p.start();
            InputStream stream = pr.getInputStream();
            InputStream er = pr.getErrorStream();
            Scanner es = new Scanner(er);
            Scanner bf = new Scanner(stream);
            String line;
            while(bf.hasNextLine()) {
                line = bf.nextLine();
                System.out.println(line);
            }
            byte[] b = new byte[1000];
            int size = er.read(b);
            byte[] bt  = new byte[size];
            System.arraycopy(b, 0, bt, 0, size);
            System.out.println(Arrays.toString(bt));
//            while(es.hasNextLine()) {
//                System.out.println(new String(es.nextLine().getBytes("UTF8")));
//            }
        } catch (Exception e ) {
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) {
        PythonExecutor kk = new PythonExecutor();
        kk.run();
    }
}
