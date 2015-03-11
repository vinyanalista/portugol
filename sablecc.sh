#!/bin/sh
rm -rf sablecc/*
java -cp lib/sablecc.jar org.sablecc.sablecc.SableCC src/portugol.sablecc -d sablecc