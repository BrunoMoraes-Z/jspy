#!/bin/sh
set -e
mkdir -p target/classes
javac --release 8 -d target/classes $(find src -name "*.java")
jar cfm target/JSpy.jar src/META-INF/MANIFEST.MF -C target/classes .
