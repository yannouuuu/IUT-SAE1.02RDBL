#!/bin/bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$ROOT_DIR/src"

# Préférence : JAVA_HOME si défini, sinon fallback openjdk-17, sinon PATH.
JAVA_HOME="${JAVA_HOME:-${JAVA_HOME_FALLBACK:-/usr/lib/jvm/java-17-openjdk-amd64}}"
JAVA_BIN="${JAVA_HOME:+$JAVA_HOME/bin}"
JAVA_CMD="${JAVA_BIN:+$JAVA_BIN/java}"
JAVAC_CMD="${JAVA_BIN:+$JAVA_BIN/javac}"

command -v java >/dev/null 2>&1 || true
if [ -z "${JAVA_CMD:-}" ] || [ ! -x "$JAVA_CMD" ]; then JAVA_CMD="$(command -v java || true)"; fi
if [ -z "${JAVAC_CMD:-}" ] || [ ! -x "$JAVAC_CMD" ]; then JAVAC_CMD="$(command -v javac || true)"; fi

if [ -z "$JAVA_CMD" ] || [ -z "$JAVAC_CMD" ]; then
	echo "[IJAVA] Java introuvable. Installez un JDK 17 (openjdk-17-jdk) puis relancez." >&2
	exit 1
fi

get_major_version() {
	"$1" -version 2>&1 | head -n 1 | sed -E 's/.*"([0-9]+).*"/\1/'
}

JAVA_VER="$(get_major_version "$JAVA_CMD")"
JAVAC_VER="$(get_major_version "$JAVAC_CMD")"

if [ "${JAVA_VER:-0}" -lt 17 ] || [ "${JAVAC_VER:-0}" -lt 17 ]; then
	echo "[IJAVA] Java 17 requis. Version détectée : java ${JAVA_VER:-?}, javac ${JAVAC_VER:-?}." >&2
	echo "Installez un JDK 17 (ex: sudo apt install -y openjdk-17-jdk) puis relancez." >&2
	exit 1
fi

echo "[IJAVA] Compilation en cours..."

mkdir -p "$ROOT_DIR/classes"

"$JAVAC_CMD" -source 17 -target 17 -d "$ROOT_DIR/classes" -cp "$ROOT_DIR/lib/ijava.jar" -sourcepath "$SRC_DIR" "$SRC_DIR"/*.java

echo "[IJAVA] Lancement de Main..."

cd "$ROOT_DIR/classes"
"$JAVA_CMD" -cp "$ROOT_DIR/lib/ijava.jar:." ijava2.clitools.MainCLI execute Main
