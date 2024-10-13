import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MatrixMultiplyDriver {
    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.err.println("Usage: MatrixMultiplyDriver <input path> <output path> <rowsA> <colsA> <colsB>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        conf.setInt("nrowsA", Integer.parseInt(args[2]));
        conf.setInt("ncolsA", Integer.parseInt(args[3]));
        conf.setInt("ncolsB", Integer.parseInt(args[4]));

        Job job = Job.getInstance(conf, "Matrix Multiply");

        job.setJarByClass(MatrixMultiplyDriver.class);
        job.setMapperClass(MatrixMultiplyMapper.class);
        job.setReducerClass(MatrixMultiplyReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

