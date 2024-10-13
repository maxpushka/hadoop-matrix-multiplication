# Directories definition
SRC_DIR = src
BUILD_DIR = build
PACKAGE_DIR = matrixmultiply
UTIL_DIR = util
OUTPUT_DIR = matrix_output
INPUT_DIR = input
DOCKER_EXEC = docker exec -w /tmp -t namenode

# Java source files
SRC_FILES = $(wildcard $(SRC_DIR)/$(PACKAGE_DIR)/*.java)
UTIL_FILES = $(wildcard $(SRC_DIR)/$(UTIL_DIR)/*.java)

# Default Java version for compilation for Hadoop
JAVA_VERSION = 1.8

# Run the matrix generator CLI
# Format: rows A, cols A/rows B, cols B
MATRIX_SIZES = 200 300 200

# Build the matrix generator CLI
build-gen: $(UTIL_FILES)
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) $(UTIL_FILES)

run-gen: build-gen
	mkdir -p $(INPUT_DIR)
	java -cp $(BUILD_DIR) MatrixGeneratorCLI ${INPUT_DIR} ${MATRIX_SIZES}

# Build main application
build: $(SRC_FILES)
	mkdir -p $(BUILD_DIR)
	javac -source $(JAVA_VERSION) -target $(JAVA_VERSION) -classpath `hadoop classpath` -d $(BUILD_DIR) $(SRC_FILES)
	jar -cvf $(BUILD_DIR)/matrixmultiply.jar -C $(BUILD_DIR)/ .

# Deploy jar to namenode
deploy: build
	docker cp $(BUILD_DIR)/matrixmultiply.jar namenode:/tmp

# Run the Hadoop job
run: deploy input
	# Remove existing output directory if it exists
	${DOCKER_EXEC} hdfs dfs -rm -r -skipTrash hdfs://namenode:9000/user/root/${OUTPUT_DIR} || true
	# Run the job
	${DOCKER_EXEC} hadoop jar matrixmultiply.jar MatrixMultiplyDriver input ${OUTPUT_DIR} ${MATRIX_SIZES}
	# Show the output
	${DOCKER_EXEC} hadoop fs -cat ${OUTPUT_DIR}/part-r-00000

# Prepare input files and upload them to HDFS
input: input/a.txt input/b.txt
	docker cp ${INPUT_DIR} namenode:/tmp
	${DOCKER_EXEC} hdfs dfs -rm -r -skipTrash hdfs://namenode:9000/user/root/${INPUT_DIR} || true
	${DOCKER_EXEC} hdfs dfs -mkdir -p ${INPUT_DIR} || true
	${DOCKER_EXEC} hdfs dfs -put ${INPUT_DIR}/a.txt ${INPUT_DIR}/a.txt || true
	${DOCKER_EXEC} hdfs dfs -put ${INPUT_DIR}/b.txt ${INPUT_DIR}/b.txt || true
