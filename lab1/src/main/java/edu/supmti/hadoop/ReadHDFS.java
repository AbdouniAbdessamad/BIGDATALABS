import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class ReadHDFS {
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        Path filePath = new Path("/user/root/purchases.txt");

        try (FileSystem fs = FileSystem.get(conf);
             FSDataInputStream inStream = fs.open(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(inStream))) {

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
