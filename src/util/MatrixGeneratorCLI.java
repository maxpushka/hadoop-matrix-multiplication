import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MatrixGeneratorCLI {

    public static int[][] generateMatrix(int rows, int cols) {
        Random random = new Random();
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(1000)+1; // 1 .. 1000
            }
        }
        return matrix;
    }

    // Method to write a matrix to a file in the specified "A,row,column,value" format
    public static void writeMatrixToFile(int[][] matrix, String filename, char matrixName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                writer.write(matrixName + "," + i + "," + j + "," + matrix[i][j]);
                writer.newLine();
            }
        }
        writer.close();
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java MatrixGeneratorCLI <output_dir> <rowsA> <colsA_rowsB> <colsB>");
            System.exit(1);
        }

        // Parse command-line arguments
        String outputDir = args[0];
        // Check if the output directory exists and is a directory
        Path outputPath = Paths.get(outputDir);
        if (!Files.exists(outputPath) || !Files.isDirectory(outputPath)) {
            System.err.println("Error: Output directory does not exist or is not a directory: " + outputDir);
            System.exit(1);
        }

        int rowsA = Integer.parseInt(args[1]);
        int colsA_rowsB = Integer.parseInt(args[2]); // This is colsA (for matrix A) and rowsB (for matrix B)
        int colsB = Integer.parseInt(args[3]);

        // Validate dimensions for matrix multiplication
        if (rowsA <= 0 || colsA_rowsB <= 0 || colsB <= 0) {
            System.out.println("Matrix dimensions must be positive integers.");
            System.exit(1);
        }

        // Matrices A (rowsA x colsA_rowsB) and B (colsA_rowsB x colsB) can be multiplied if colsA == rowsB
        System.out.println("Generating matrices A and B...");
        int[][] matrixA = generateMatrix(rowsA, colsA_rowsB);
        int[][] matrixB = generateMatrix(colsA_rowsB, colsB);

        // Write the matrices to the respective files
        try {
            // Dynamically construct file paths for a.txt and b.txt
            Path aFilePath = outputPath.resolve("a.txt");
            Path bFilePath = outputPath.resolve("b.txt");
            writeMatrixToFile(matrixA, aFilePath.toString(), 'A');
            writeMatrixToFile(matrixB, bFilePath.toString(), 'B');
            System.out.println("Matrices have been written to input/a.txt and input/b.txt in the desired format.");
        } catch (IOException e) {
            System.err.println("Error writing the matrices to files: " + e.getMessage());
            System.exit(1);
        }
    }
}
