#!/bin/bash

# --- Configuración ---
RECIPIENT_EMAIL="petsafedam@gmail.com" 
EMAIL_SUBJECT_PREFIX="[Monitor Docker]"
HOSTNAME=$(hostname)
# --- Fin de la Configuración ---

# Función para enviar correo (si el comando 'mail' está disponible)
send_notification() {
    local subject="$1"
    local body="$2"
    if command -v mail &> /dev/null; then
        printf "%b" "$body" | mail -s "$subject" "$RECIPIENT_EMAIL"
        if [ $? -eq 0 ]; then
            echo "Notificación por correo enviada a $RECIPIENT_EMAIL."
        else
            echo "Error al enviar la notificación por correo. Revisa la configuración del comando 'mail'."
        fi
    else
        echo "Comando 'mail' no encontrado. No se pueden enviar notificaciones por correo."
        echo "Asunto: $subject"
        echo "Cuerpo:"
        printf "%b" "$body"
    fi
}

echo "Iniciando la revisión de contenedores Docker en $HOSTNAME a las $(date)"

# Verificar si Docker está instalado
if ! command -v docker &> /dev/null; then
    error_message="Error: Comando 'docker' no encontrado. Por favor, instala Docker."
    echo "$error_message"
    send_notification "$EMAIL_SUBJECT_PREFIX Error - Docker no encontrado en $HOSTNAME" "$error_message en $HOSTNAME a las $(date)"
    exit 1
fi

# Intentar obtener la información de los contenedores y verificar si el demonio Docker está respondiendo
# Usamos 2>&1 para capturar también errores si 'docker ps' falla (ej. demonio no activo)
containers_info_output=$(docker ps -a --format "{{.ID}}\t{{.Names}}\t{{.Status}}" 2>&1)
docker_ps_exit_code=$?

if [ $docker_ps_exit_code -ne 0 ]; then
    error_message="Error: No se pudieron listar los contenedores Docker en $HOSTNAME.\nEl demonio Docker podría no estar en ejecución o accesible.\n\nSalida del comando Docker:\n$containers_info_output"
    echo -e "$error_message" # Usar -e para interpretar \n
    send_notification "$EMAIL_SUBJECT_PREFIX Error - Acceso a Docker fallido en $HOSTNAME" "$error_message"
    exit 1
fi

# Si la salida de 'docker ps -a' está vacía (después de una ejecución exitosa), no hay contenedores.
if [ -z "$containers_info_output" ]; then
    echo "No se encontraron contenedores Docker en $HOSTNAME."
    # Opcionalmente, enviar un correo si no se encuentran contenedores.
    # send_notification "$EMAIL_SUBJECT_PREFIX No hay contenedores en $HOSTNAME" "No se encontraron contenedores Docker en $HOSTNAME a las $(date)."
    exit 0
fi

declare -a notification_messages # Array para almacenar los mensajes de notificación
actions_taken=false

# Procesar cada contenedor
# IFS=$'\t' asegura que los campos se separen por tabulador.
# read -r previene que las barras invertidas sean interpretadas.
while IFS=$'\t' read -r container_id container_name container_status; do
    # Omitir líneas vacías o malformadas que podrían surgir si la salida de docker ps es inesperada
    if [ -z "$container_id" ] || [ -z "$container_name" ]; then
        continue
    fi

    echo "Revisando contenedor: $container_name (ID: $container_id) - Estado: $container_status"

    # Verificar si el contenedor está en ejecución.
    # "Up" usualmente indica en ejecución. Otros estados como "Exited", "Created", "Paused" significan que no está activo.
    if [[ "$container_status" != Up* ]]; then
        echo "Contenedor $container_name (ID: $container_id) no está en ejecución (Estado: $container_status)."
        echo "Intentando iniciar $container_name..."

        # Intentar iniciar el contenedor y redirigir la salida estándar y de error
        if docker start "$container_id" > /dev/null 2>&1; then
            # Verificar el nuevo estado después del intento de inicio
            new_status=$(docker ps -a --filter "id=$container_id" --format "{{.Status}}")
            if [[ "$new_status" == Up* ]]; then
                message="Contenedor '$container_name' (ID: $container_id) estaba detenido y ha sido iniciado exitosamente. Nuevo estado: $new_status."
                notification_messages+=("$message")
                echo "$message"
                actions_taken=true
            else
                message="Contenedor '$container_name' (ID: $container_id) estaba detenido. Se intentó iniciar, pero aún no está 'Up'. Estado actual: $new_status."
                notification_messages+=("$message")
                echo "$message"
                actions_taken=true # Se considera una acción aunque no esté completamente 'Up'
            fi
        else
            message="Fallo al intentar iniciar el contenedor '$container_name' (ID: $container_id). Revisa los logs: docker logs $container_id"
            notification_messages+=("$message")
            echo "$message"
            actions_taken=true
        fi
    else
        echo "Contenedor $container_name (ID: $container_id) ya está en ejecución."
    fi
done <<< "$containers_info_output"


# Enviar notificación por correo si se tomaron acciones
if [ "$actions_taken" = true ]; then
    echo "Se tomaron acciones en uno o más contenedores. Preparando notificación..."
    email_body="Informe del estado de los contenedores Docker desde $HOSTNAME a las $(date):\n\n"
    if [ ${#notification_messages[@]} -gt 0 ]; then
        for notif in "${notification_messages[@]}"; do
            email_body+="- $notif\n"
        done
    else
        # Este caso no debería ocurrir si actions_taken es true, pero como fallback.
        email_body+="Se realizó una revisión y se podrían haber reiniciado algunos contenedores.\n"
    fi

    email_subject="$EMAIL_SUBJECT_PREFIX Acciones realizadas en contenedores en $HOSTNAME"
    send_notification "$email_subject" "$email_body"
else
    echo "Todos los contenedores revisados ya estaban en ejecución. No se tomaron acciones, no se envía correo."
fi

echo "Revisión de contenedores Docker finalizada a las $(date)."
