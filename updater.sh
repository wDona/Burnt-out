#!/bin/bash
LATEST=$(curl -s https://api.github.com/repos/wdona/burnt-out/releases | grep tag_name | head -1 | cut -d '"' -f 4)
VERSION=${LATEST#v}  # quita la "v" del principio -> 1.2.15
CURRENT=$(cat /home/container/version.txt 2>/dev/null || echo "none")

# Función para descargar el jar
descargar_jar() {
  echo "Descargando versión $LATEST..."
  curl -L "https://github.com/wDona/Burnt-out/releases/download/$LATEST/burnt-out-server-$VERSION.jar" \
    -o /home/container/burnt-out-server.jar
  echo "$LATEST" > /home/container/version.txt
  echo "Actualizado."
}

if [ ! -f /home/container/burnt-out-server.jar ] || [ "$LATEST" != "$CURRENT" ]; then
  descargar_jar
else
  echo "Ya usas la version latest"
fi

while ! jar tf /home/container/burnt-out-server.jar > /dev/null 2>&1; do
  echo "Jar corrupto detectado, reintentando descarga en 10 segundos..."
  descargar_jar
  sleep 10
done

echo "Iniciando servidor..."
chmod +x /home/container/burnt-out-server.jar
echo "Permiso de ejecucion dado"
echo "Servidor iniciado..."
LOG_FILE="/home/container/logs/server-$(date +%Y-%m-%d_%H-%M-%S).log"
mkdir -p /home/container/logs
exec java -Dterminal.ansi=true -jar /home/container/burnt-out-server.jar 35090 2>&1 | tee "$LOG_FILE"
echo "Log guardado en $LOG_FILE"
echo "Java terminó con código: $?"