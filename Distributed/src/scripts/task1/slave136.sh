#!/bin/bash

scp SlaveTask1.java pi@10.10.10.136:SlaveTask1.java

scp MapObjects.java pi@10.10.10.136:MapObjects.java

scp Heap.java pi@10.10.10.136:Heap.java

ssh pi@10.10.10.136 "pi4j -c *.java; pi4j -r SlaveTask1"
