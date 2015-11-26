#!/bin/bash

scp SlaveTask2.java pi@10.10.10.136:SlaveTask2.java

scp MapObjects.java pi@10.10.10.136:MapObjects.java

scp Heap.java pi@10.10.10.136:Heap.java

ssh pi@10.10.10.136 "pi4j -c *.java; pi4j -r SlaveTask2"
