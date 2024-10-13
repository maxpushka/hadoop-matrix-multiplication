import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MatrixMultiplyReducer extends Reducer<Text, Text, Text, IntWritable> {

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Maps to store the intermediate results from both matrices
        Map<Integer, Integer> Amap = new HashMap<>();
        Map<Integer, Integer> Bmap = new HashMap<>();

        // Separate values from A and B into corresponding maps
        for (Text val : values) {
            String[] parts = val.toString().split(",");
            String matrixName = parts[0];
            int index = Integer.parseInt(parts[1]);
            int value = Integer.parseInt(parts[2]);

            if (matrixName.equals("A")) {
                Amap.put(index, value);
            } else if (matrixName.equals("B")) {
                Bmap.put(index, value);
            }
        }

        // Calculate the dot product
        int result = 0;
        for (int k : Amap.keySet()) {
            if (Bmap.containsKey(k)) {
                result += Amap.get(k) * Bmap.get(k);
            }
        }

        context.write(key, new IntWritable(result));
    }
}

