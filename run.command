#!/bin/sh

cd "$(dirname "$0")" || exit 1

# Compile all source files into the src directory
find src -name "*.java" -exec javac -d src {} + || exit 1

# Run the main class
java -cp src FitTrack