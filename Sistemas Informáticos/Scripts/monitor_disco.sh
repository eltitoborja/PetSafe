#!/bin/bash
# disk_monitor.sh

# Configuración
THRESHOLD=60  # Porcentaje de uso de disco para alertas
EMAIL="petsafedam@gmail.com"  # Email para recibir alertas
LOG_FILE="/home/dam1"

# Función para logging
log_message() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOG_FILE"
}

# Función para enviar email
send_email() {
    local subject="$1"
    local body="$2"
    echo "$body" | mail -s "$subject" "$EMAIL"
}

# Función principal
check_disk_space() {
    log_message "Iniciando chequeo de espacio en disco"

    # Obtener uso de disco para todas las particiones montadas
    DISK_USAGE=$(df -h | grep '^/dev/')

    # Preparar el reporte
    REPORT="Reporte de espacio en disco - $(date '+%Y-%m-%d %H:%M:%S')\n\n"

    while read -r line; do
        # Extraer información
        FILESYSTEM=$(echo "$line" | awk '{print $1}')
        SIZE=$(echo "$line" | awk '{print $2}')
        USED=$(echo "$line" | awk '{print $3}')
        AVAIL=$(echo "$line" | awk '{print $4}')
        PERCENT=$(echo "$line" | awk '{print $5}' | sed 's/%//')
        MOUNTPOINT=$(echo "$line" | awk '{print $6}')

        # Añadir al reporte
        REPORT+="Filesystem: $FILESYSTEM\n"
        REPORT+="Montado en: $MOUNTPOINT\n"
        REPORT+="Tamaño: $SIZE\n"
        REPORT+="Usado: $USED\n"
        REPORT+="Disponible: $AVAIL\n"
        REPORT+="Porcentaje usado: $PERCENT%\n"
        REPORT+="------------------------\n"

        # Comprobar si supera el umbral
        if [ "$PERCENT" -gt "$THRESHOLD" ]; then
            ALERT="ALERTA: Alto uso de disco en $MOUNTPOINT ($PERCENT%)"
            send_email "Alerta de espacio en disco" "$ALERT"
            log_message "$ALERT"
