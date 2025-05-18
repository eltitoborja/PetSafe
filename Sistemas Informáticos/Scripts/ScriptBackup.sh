#!/bin/bash

# Configuración
DB_USER="root"
DB_PASSWORD="root"
DB_NAME="PetSafe"
BACKUP_DIR="/git/Backups"
BACKUP_FILE="petsafe_backup_$(date +%F).sql.gz"

# Crear directorio si no existe
mkdir -p "$BACKUP_DIR"

# Exportar y comprimir la base de datos
echo "Exportando base de datos..."
MYSQL_PWD="$DB_PASSWORD" mariadb-dump -u "$DB_USER" "$DB_NAME" | gzip > "$BACKUP_DIR/$BACKUP_FILE"

# Verificar si se creó el archivo correctamente
if [ -f "$BACKUP_DIR/$BACKUP_FILE" ]; then
    echo "Backup creado: $BACKUP_FILE"
else
    echo "Error: El backup no se creó."
    exit 1
fi

# Añadir al repositorio Git y subir
cd /git
git add "Backups/$BACKUP_FILE"
git commit -m "Backup del $DB_NAME - $(date +%F)"
git push

echo "Backup subido a GitHub correctamente."