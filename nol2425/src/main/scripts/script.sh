#!/bin/bash

# Paso 1: Login como admin
echo " Haciendo login como administrador..."

KEY=$(curl -s -c admin_cookie.txt \
    -X POST "http://localhost:9090/CentroEducativo/login" \
    -H "accept: text/plain" \
    -H "Content-Type: application/json" \
    -d '{ "dni": "111111111", "password": "654321" }' | tr -d '"')

echo " Key obtenida: $KEY"
echo " Cookie guardada en admin_cookie.txt"

# Paso 2: Crear varios alumnos
echo " Añadiendo alumnos de prueba..."

# Array de alumnos: "dni,nombre,apellidos"
alumnos=(
    "222222222,Lucía,Gómez"
    "333333333,Carlos,Sánchez"
    "444444444,María,Pérez"
    "555555555,David,López"
    "666666666,Ana,Martínez"
)

for alumno in "${alumnos[@]}"; do
    IFS=',' read -r dni nombre apellidos <<< "$alumno"

    echo " Añadiendo alumno $nombre $apellidos ($dni)..."

    curl -s -b admin_cookie.txt \
        -X POST "http://localhost:9090/CentroEducativo/alumnos?key=$KEY" \
        -H "accept: text/plain" \
        -H "Content-Type: application/json" \
        -d "{\"apellidos\": \"$apellidos\", \"dni\": \"$dni\", \"nombre\": \"$nombre\", \"password\": \"123456\"}"
done

echo " Todos los alumnos fueron añadidos."