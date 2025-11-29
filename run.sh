#!/bin/bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$ROOT_DIR/src"

echo "[IJAVA] Compilation en cours..."

mkdir -p "$ROOT_DIR/classes"

javac -d "$ROOT_DIR/classes" -cp "$ROOT_DIR/lib/ijava.jar" -sourcepath "$SRC_DIR" "$SRC_DIR"/*.java

echo "[IJAVA] Lancement de Main..."

cd "$ROOT_DIR/classes"
java -cp "$ROOT_DIR/lib/ijava.jar:." ijava2.clitools.MainCLI execute Main
