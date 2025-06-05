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
echo " Añadiendo alumnos ..."

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

profesores=(
    "222345666,Marc,Gómez Valls"
    "333666333,Carlos,Contreras"
    "444123444,Mario,Rodriguez"
    "555666555,Alejandro,López"
    "666555666,Adrian,Gonzalez"
)

for profesor in "${profesores[@]}"; do
    IFS=',' read -r dni nombre apellidos <<< "$profesor"

    echo " Añadiendo profesor $nombre $apellidos ($dni)..."

    curl -s -b admin_cookie.txt \
        -X POST "http://localhost:9090/CentroEducativo/profesores?key=$KEY" \
        -H "accept: text/plain" \
        -H "Content-Type: application/json" \
        -d "{\"apellidos\": \"$apellidos\", \"dni\": \"$dni\", \"nombre\": \"$nombre\", \"password\": \"123456\"}"
done

echo " Todos los profesores fueron añadidos."

asignaturas=(
    "ISW,A, Ingenieria de Software"
    "IPC,B,Interfaces Persona Computador"
    "MAD,A,Matematica Discreta"
    "AMA,B,Analisis Matematico"
)

for asignatura in "${asignaturas[@]}"; do
    IFS=',' read -r acronimo cuatrimestre nombre <<< "$asignatura"

    echo " Añadiendo asignatura $nombre $cuatrimestre ($acronimo)..."

    curl -s -b admin_cookie.txt \
        -X POST "http://localhost:9090/CentroEducativo/asignaturas?key=$KEY" \
        -H "Content-Type: application/json" \
        -d "{  \"acronimo\": \"$acronimo\",  \"creditos\": 4.5,  \"cuatrimestre\": \"$cuatrimestre\",  \"curso\": 3,  \"nombre\": \"$nombre\"}"
done

echo " Todas las asignaturas fueron añadidos."

acronimos=(
    "ISW"
    "IPC"
    "MAD"
    "AMA"
    "DEW"
)

dni_alumnos=(222222222 333333333 444444444 555555555 666666666)

for acronimo in "${acronimos[@]}"; do
    echo "Añadiendo alumnos a asignatura $acronimo..."

    for dni in "${dni_alumnos[@]}"; do
        echo "  - Añadiendo alumno con DNI $dni a $acronimo..."

        curl -s -b admin_cookie.txt \
            -X POST "http://localhost:9090/CentroEducativo/alumnos/$dni/asignaturas?key=$KEY" \
            -H "accept: text/plain" \
            -H "Content-Type: application/json" \
            -d "$acronimo"
    done
done



dni_profesores=(222345666 333666333 444123444 555666555 666555666)

for acronimo in "${acronimos[@]}"; do
    echo "Añadiendo profesores a asignatura $acronimo..."

    for dni in "${dni_profesores[@]}"; do
        echo "  - Añadiendo profesor con DNI $dni a $acronimo..."

        curl -s -b admin_cookie.txt \
            -X POST "http://localhost:9090/CentroEducativo/profesores/$dni/asignaturas?key=$KEY" \
            -H "accept: text/plain" \
            -H "Content-Type: application/json" \
            -d "$acronimo"
    done
done

echo " Haciendo login como profesor..."

KEYP=$(curl -s -c profesor_cookie.txt \
    -X POST "http://localhost:9090/CentroEducativo/login" \
    -H "accept: text/plain" \
    -H "Content-Type: application/json" \
    -d '{ "dni": "222345666", "password": "123456" }' | tr -d '"')
    
echo " Key obtenida: $KEYP"

for acronimo in "${acronimos[@]}"; do
    echo "Añadiendo notas alumnos a asignatura $acronimo..."

    for dni in "${dni_alumnos[@]}"; do
        echo "  - Añadiendo nota a alumno con DNI $dni a $acronimo..."

        curl -s -b profesor_cookie.txt \
            -X PUT "http://localhost:9090/CentroEducativo/alumnos/$dni/asignaturas/$acronimo?key=$KEYP" \
            -H "accept: text/plain" \
            -H "Content-Type: application/json" \
            -d "5"
    done
done