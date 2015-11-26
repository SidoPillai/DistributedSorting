#!/bin/bash

scp SlaveTask1.java pi@10.10.10.135:SlaveTask1.java

scp MapObjects.java pi@10.10.10.135:MapObjects.java

scp Heap.java pi@10.10.10.135:Heap.java

ssh pi@10.10.10.135 "pi4j -c *.java; pi4j -r SlaveTask1"
