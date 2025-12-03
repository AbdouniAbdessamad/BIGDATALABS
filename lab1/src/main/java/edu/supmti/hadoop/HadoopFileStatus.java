import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HadoopFileStatus {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: HadoopFileStatus <directory> <filename> <new_name>");
            System.exit(1);
        }

        String dir = args[0];
        String filename = args[1];
        String newname = args[2];

        Configuration conf = new Configuration();
        FileSystem fs;

        try {
            fs = FileSystem.get(conf);

            Path filepath = new Path(dir, filename);

            if (!fs.exists(filepath)) {
                System.out.println("File does not exist: " + filepath);
                System.exit(1);
            }

            FileStatus status = fs.getFileStatus(filepath);

            System.out.println("File Name: " + status.getPath().getName());
            System.out.println("Size: " + status.getLen() + " bytes");
            System.out.println("Owner: " + status.getOwner());
            System.out.println("Permission: " + status.getPermission());
            System.out.println("Replication: " + status.getReplication());
            System.out.println("Block Size: " + status.getBlockSize());

            BlockLocation[] blocks =
                    fs.getFileBlockLocations(status, 0, status.getLen());

            for (BlockLocation block : blocks) {
                System.out.println("Block offset: " + block.getOffset());
                System.out.println("Block length: " + block.getLength());
                System.out.print("Hosts: ");
                for (String host : block.getHosts()) {
                    System.out.print(host + " ");
                }
                System.out.println("\n");
            }

            // rename
            Path newPath = new Path(dir, newname);
            fs.rename(filepath, newPath);
            System.out.println("Renamed to: " + newPath.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
