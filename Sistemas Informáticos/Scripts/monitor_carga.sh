#!/bin/bash

# monitor_carga.sh
# Configuración de correo
EMAIL="petsafedam@gmail.com"
ASUNTO="Informe de carga del sistema"

# Obtener fecha actual
FECHA=$(date '+%Y-%m-%d %H:%M:%S')

# Obtener la carga del sistema
LOAD_AVG=$(cat /proc/loadavg)
LOAD_1=$(echo $LOAD_AVG | awk '{print $1}')
LOAD_5=$(echo $LOAD_AVG | awk '{print $2}')
LOAD_15=$(echo $LOAD_AVG | awk '{print $3}')

# Crear el mensaje
MENSAJE="
Informe de carga del sistema
Fecha: $FECHA

Carga media:
- Último minuto: $LOAD_1
- Últimos 5 minutos: $LOAD_5
- Últimos 15 minutos: $LOAD_15
"

# Enviar correo
echo "$MENSAJE" | mail -s "$ASUNTO" "$EMAIL"


# Registrar en log
logger "Monitor de carga: Informe enviado - Carga: $LOAD_1, $LOAD_5, $LOAD_15"

