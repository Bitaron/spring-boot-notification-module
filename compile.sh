#!/bin/bash
echo "Compiling main code only..."
mvn clean compile -DskipTests

if [ $? -eq 0 ]; then
  echo "Compilation successful!"
else
  echo "Compilation failed. Check the logs for details."
fi 