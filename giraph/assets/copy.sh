#!/bin/bash

hdfs dfs -rm /input/$1
hdfs dfs -copyFromLocal $1 /input
hdfs dfs -cat /input/$1
