#SETUP
    docker compose up -d
    docker compose exec --user root hadoop bash
    rm -rf /hadoop/dfs/datanode/*
    rm -rf /hadoop/dfs/namenode/*
    /opt/hadoop/bin/hdfs namenode -format -force -nonInteractive
    nohup /opt/hadoop/bin/hdfs namenode   > /var/log/hadoop/nn.log 2>&1 &
    export HADOOP_HOME=/opt/hadoop
    export PATH=$HADOOP_HOME/bin:$PATH
    nohup hdfs datanode > /var/log/hadoop/datanode.log 2>&1 &
    #CHECK IF NAMENODE AND DATANODE ARE ALIVE 
        ps aux | grep -E 'NameNode|DataNode' | grep -v grep
    exit

#DATA TRANSFER
    docker compose exec --user root hadoop /opt/hadoop/bin/hdfs dfs -mkdir -p /data/taxi
    docker compose cp data/taxi/yellow_tripdata_2022-01.parquet hadoop:/tmp/
    docker compose cp data/taxi/taxi_zone_lookup.csv    hadoop:/tmp/
    docker compose exec --user root hadoop /opt/hadoop/bin/hdfs dfs -put /tmp/yellow_tripdata_2025-01.parquet /data/taxi/
    docker compose exec --user root hadoop /opt/hadoop/bin/hdfs dfs -put /tmp/taxi_zone_lookup.csv    /data/taxi/
    #CHECK IF FILES ARE COPPIED
    docker compose exec hadoop /opt/hadoop/bin/hdfs dfs -ls /data/taxi


#TEST SPARK
    docker compose exec spark bash -lc "export HOME=/tmp && mkdir -p /tmp/.ivy2"
    docker compose exec spark bash
    /opt/bitnami/spark/bin/spark-shell \
    --master local[*] --conf spark.jars.ivy=/tmp/.ivy2 \
    -e "spark.read.parquet('hdfs://hadoop:9000/data/taxi/yellow_tripdata_2025-01.parquet').show(3)"

#CREATE JAR OF JAVA PROJECT
    mvn clean package

#COPY PROJECT JAR TO app DIRECTORY

#EXECUTION
    docker exec -it --user root spark bash
    spark-submit --class com.example.application.Main --master local[*] /app/spark-taxi-analysis-1.0-SNAPSHOT.jar hdfs://hadoop:9000

#SEE AND COPY RESULTS TO LOCAL
    docker exec -it hadoop bash 
    hdfs dfs -ls /output/
    hdfs dfs -get /output /tmp/output
    exit
    docker cp hadoop:/tmp/output D:\GOprojects\spark-hdfs-ha-project\output



#Github repo:
    https://github.com/SUT-technology/spark-hdfs-ha-project.git
#Input and output HDFS: src\main\java\com\example\config\HdfsPaths.java