import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class MatrixMultiplyMapper extends Mapper<Object, Text, Text, Text> {

  @Override
  public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      String[] elements = value.toString().split(",");
      
      // Check if the input line is valid and has at least 4 elements (matrix name, row, column, value)
      if (elements.length != 4) {
          System.err.println("Invalid input line: " + value.toString());
          return;  // Skip this line if it doesn't have the correct number of elements
      }

      String matrixName = elements[0];  // "A" or "B"
      int i = Integer.parseInt(elements[1]);  // Row index
      int j = Integer.parseInt(elements[2]);  // Column index
      int valueOfElement = Integer.parseInt(elements[3]);  // Matrix value

      if (matrixName.equals("A")) {
          // Emit for matrix A: key = (i, k), value = ("A", j, A[i][j])
          for (int k = 0; k < context.getConfiguration().getInt("ncolsB", 0); k++) {
              context.write(new Text(i + "," + k), new Text("A," + j + "," + valueOfElement));
          }
      } else if (matrixName.equals("B")) {
          // Emit for matrix B: key = (i, k), value = ("B", i, B[j][i])
          for (int k = 0; k < context.getConfiguration().getInt("nrowsA", 0); k++) {
              context.write(new Text(k + "," + j), new Text("B," + i + "," + valueOfElement));
          }
      }
  }
}

