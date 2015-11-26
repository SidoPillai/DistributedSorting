#!/bin/bash

scp SlaveTask2.java pi@10.10.10.135:SlaveTask2.java

scp MapObjects.java pi@10.10.10.135:MapObjects.java

scp Heap.java pi@10.10.10.135:Heap.java

ssh pi@10.10.10.135 "pi4j -c *.java; pi4j -r SlaveTask2"
