#!/bin/bash

scp MainSlave.java pi@10.10.10.136:MainSlave.java

ssh pi@10.10.10.136 "pi4j -c MainSlave.java; pi4j -r MainSlave 10.10.10.136"
