#!/bin/bash

# Script per executar els tests amb Java 21

export JAVA_HOME=/usr/local/Cellar/openjdk@21/21.0.7/libexec/openjdk.jdk/Contents/Home
echo "Utilitzant Java 21: $JAVA_HOME"
echo "Versió de Java:"
$JAVA_HOME/bin/java -version
echo ""

echo "Executant tests amb Java 21..."
mvn test
