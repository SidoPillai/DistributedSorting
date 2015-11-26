#!/bin/bash

scp SlaveTask1.java pi@10.10.10.134:SlaveTask1.java

scp MapObjects.java pi@10.10.10.134:MapObjects.java

scp Heap.java pi@10.10.10.134:Heap.java

ssh pi@10.10.10.134 "pi4j -c *.java; pi4j -r SlaveTask1"
