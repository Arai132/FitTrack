#!/bin/sh

cd "$(dirname "$0")" || exit 1
PROJECT_ROOT="$(pwd)"

# Compile main + test sources into the out directory
mkdir -p out
find src test -name "*.java" > sources.txt
javac -d out @sources.txt || { rm -f sources.txt; exit 1; }
rm -f sources.txt

# Run from a scratch directory so FitTrackFacadeTest's file persistence
# never touches a real fittrack_data.ser in the project folder.
RUN_DIR="$(mktemp -d)"
trap 'rm -rf "$RUN_DIR"' EXIT
cd "$RUN_DIR" || exit 1

java -cp "$PROJECT_ROOT/out" AllTests
