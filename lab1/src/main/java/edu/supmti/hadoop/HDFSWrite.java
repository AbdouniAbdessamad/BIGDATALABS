import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSWrite {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: HDFSWrite <hdfs_file_path> <text_to_write>");
            System.exit(1);
        }

        String hdfsFilePath = args[0];
        String textToWrite = args[1];

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path(hdfsFilePath);

        if (!fs.exists(path)) {
            FSDataOutputStream outStream = fs.create(path);
            outStream.writeUTF(textToWrite);
            outStream.close();
            System.out.println("File written to HDFS: " + hdfsFilePath);
        } else {
            System.out.println("File already exists: " + hdfsFilePath);
        }

        fs.close();
    }
}
