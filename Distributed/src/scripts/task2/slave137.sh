#!/bin/bash

scp SlaveTask2.java pi@10.10.10.137:SlaveTask2.java

scp MapObjects.java pi@10.10.10.137:MapObjects.java

scp Heap.java pi@10.10.10.137:Heap.java

ssh pi@10.10.10.137 "pi4j -c *.java; pi4j -r SlaveTask2"
