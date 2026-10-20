# Big Data Labs: Hadoop HDFS & MapReduce

**Hands-on Hadoop labs in Java and Python: programmatic HDFS I/O, a MapReduce WordCount job and Hadoop Streaming.**

![Java](https://img.shields.io/badge/Java-8-ED8B00?logo=openjdk&logoColor=white)
![Apache Hadoop](https://img.shields.io/badge/Apache_Hadoop-3.2.0-66CCFF?logo=apachehadoop&logoColor=black)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)
![Python](https://img.shields.io/badge/Python-3-3776AB?logo=python&logoColor=white)

## Overview

This repository collects the practical work (TPs) from a Big Data course at SUPMTI Rabat, done with a lab partner. The labs use the Hadoop 3.2 Java API to read, write and inspect files on HDFS, implement the classic WordCount job with the MapReduce API, and start on the same problem with Hadoop Streaming in Python. Sample datasets (a retail purchases log and the text of *Alice in Wonderland*) are included for running the jobs.

## Labs

### Lab 1: HDFS file operations (Java API), `lab1/`

Three standalone programs built against `hadoop-common` and `hadoop-hdfs` 3.2.0. Each one gets a `FileSystem` from the cluster's default `Configuration`.

| Program | What it does |
|---|---|
| `ReadHDFS` | Opens `/user/root/purchases.txt` on HDFS and prints it line by line with a `BufferedReader`. |
| `HDFSWrite` | Creates a new HDFS file and writes the given text to it, unless the file already exists. |
| `HadoopFileStatus` | Prints a file's metadata (name, size, owner, permissions, replication factor, block size) and every block's offset, length and host DataNodes, then renames the file. |

```bash
# copy the sample data to HDFS first
hdfs dfs -mkdir -p /user/root
hdfs dfs -put purchases.txt /user/root/

# read
hadoop jar ReadHDFS.jar

# write: <hdfs_file_path> <text_to_write>
hadoop jar HDFSWrite.jar /user/root/hello.txt "Hello HDFS"

# file status + block locations, then rename: <directory> <filename> <new_name>
hadoop jar HadoopFileStatus.jar /user/root purchases.txt achats.txt
```

### Lab 3: MapReduce WordCount (Java), `Bigdatalabs/lab3_mapreduce/`

A WordCount job written with the `org.apache.hadoop.mapreduce` API (adds `hadoop-mapreduce-client-core` 3.2.0):

- `TokenizerMapper` splits each line into tokens and emits `(word, 1)`.
- `IntSumReducer` sums the counts per word. It is also registered as the **combiner** to cut shuffle traffic.
- `WordCount` is the driver: it configures the job, the key/value types (`Text`, `IntWritable`) and the input/output paths.

```bash
cd Bigdatalabs/lab3_mapreduce
mvn clean package                      # builds target/WordCount.jar

hdfs dfs -mkdir -p /user/root/input
hdfs dfs -put ../../alice.txt /user/root/input/

hadoop jar target/WordCount.jar /user/root/input /user/root/output
hdfs dfs -cat /user/root/output/part-r-00000 | head
```

### Python mapper/reducer with Hadoop Streaming, `Bigdatalabs/Reducer.py`

The streaming version of WordCount: a reducer reads tab-separated `word\tcount` pairs from stdin (already sorted by key by Hadoop), adds up consecutive counts for the same word and prints the totals.

> **Status:** work in progress. Only the reducer is in the repo so far; the matching `mapper.py` (emit `word\t1` per token) is not committed yet, and `Reducer.py` needs its indentation fixed before it will run.

Once both scripts are in place, the job is launched with the Hadoop Streaming jar:

```bash
hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
  -files mapper.py,Reducer.py \
  -mapper "python3 mapper.py" \
  -reducer "python3 Reducer.py" \
  -input /user/root/input \
  -output /user/root/output-streaming
```

## Tech Stack

- **Java 8** (Maven compiler source/target 1.8)
- **Apache Hadoop 3.2.0**: `hadoop-common`, `hadoop-hdfs`, `hadoop-mapreduce-client-core`
- **Apache Maven** with `maven-compiler-plugin` 3.8.1 and `maven-jar-plugin` 3.2.2 (executable JARs with a `Main-Class` manifest)
- **Python 3** for Hadoop Streaming

## Project Structure

```
BIGDATALABS/
├── lab1/                              # Lab 1: HDFS Java API
│   ├── pom.xml
│   └── src/main/java/edu/supmti/hadoop/
│       ├── ReadHDFS.java
│       ├── HDFSWrite.java
│       └── HadoopFileStatus.java
├── Bigdatalabs/
│   ├── lab3_mapreduce/                # Lab 3: MapReduce WordCount
│   │   ├── pom.xml
│   │   └── src/main/java/edu/ismagi/hadoop/mapreduce/
│   │       ├── WordCount.java         # driver
│   │       ├── TokenizerMapper.java
│   │       └── IntSumReducer.java
│   └── Reducer.py                     # Hadoop Streaming reducer (WIP)
├── ReadHDFS.jar, HDFSWrite.jar,       # prebuilt Lab 1 JARs
│   HadoopFileStatus.jar
├── purchases.txt / achat.txt          # sample retail purchases dataset (TSV)
└── alice.txt                          # sample text for WordCount
```

Lab 1 is a single Maven module. Its `pom.xml` sets the JAR's `finalName` and `mainClass` (currently `HDFSWrite`), and the three JARs at the repo root were built by switching that setting. Lab 3 is a separate Maven module whose JAR runs `WordCount` directly.

## Getting Started

**Prerequisites:** a running Hadoop 3.x cluster (single-node or Docker), JDK 8+, Maven 3, and Python 3 for the streaming lab.

```bash
git clone https://github.com/AbdouniAbdessamad/BIGDATALABS.git
cd BIGDATALABS

# Lab 1: rebuild (edit <finalName>/<mainClass> in lab1/pom.xml to choose the program)
cd lab1 && mvn clean package && cd ..

# Lab 3
cd Bigdatalabs/lab3_mapreduce && mvn clean package && cd ../..
```

Then run the `hadoop jar ...` commands from each lab above.

## Possible Improvements

- Add the missing `mapper.py`, fix `Reducer.py`'s indentation, and test the pipeline locally with `cat alice.txt | python3 mapper.py | sort | python3 Reducer.py`.
- Remove build output (`target/`, root `*.jar`) from version control, add a `.gitignore`, and move the Lab 1 classes into their declared `edu.supmti.hadoop` package.
- Make `ReadHDFS` take the file path as an argument instead of hard-coding `/user/root/purchases.txt`, and split the read/rename steps of `HadoopFileStatus`.
- Use a single parent Maven project with one module per lab, so every program builds in one step.

## Author

**Abdessamad Abdouni**, Information Systems Engineering student at SUPMTI Rabat, Morocco
GitHub: [github.com/AbdouniAbdessamad](https://github.com/AbdouniAbdessamad)
