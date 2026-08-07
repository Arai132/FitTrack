#!/bin/sh

cd "$(dirname "$0")" || exit 1

# Compile all source files into the out directory (matching everything)
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out @sources.txt || { rm -f sources.txt; exit 1; }
rm -f sources.txt

java -cp out FitTrack