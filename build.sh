#!/bin/bash

# Script para compilar e executar o Sistema de Leilões Distribuído

echo "╔═══════════════════════════════════════════════╗"
echo "║   SISTEMA DE LEILÕES DISTRIBUÍDO             ║"
echo "╚═══════════════════════════════════════════════╝"
echo

if [ -z "$1" ]; then
    echo "Uso: ./build.sh [opcao]"
    echo
    echo "Opções:"
    echo "  compile     - Compila todos os arquivos Java"
    echo "  run         - Executa o launcher principal"
    echo "  server      - Inicia apenas o servidor"
    echo "  client      - Inicia apenas o cliente"
    echo "  clean       - Remove arquivos .class compilados"
    echo
    exit 0
fi

case "$1" in
    compile)
        echo "[1/4] Compilando arquivos Java..."
        javac *.java
        if [ $? -ne 0 ]; then
            echo "Erro na compilação!"
            exit 1
        fi
        echo "[2/4] Arquivos compilados com sucesso!"
        echo "[3/4] Listando arquivos gerados:"
        ls -1 *.class
        echo "[4/4] Compilação concluída!"
        ;;
    run)
        echo "Iniciando launcher do sistema..."
        java main
        ;;
    server)
        echo "Iniciando Servidor..."
        java Server
        ;;
    client)
        echo "Iniciando Cliente..."
        java Client
        ;;
    clean)
        echo "Limpando arquivos compilados..."
        rm -f *.class
        echo "Limpeza concluída!"
        ;;
    *)
        echo "Opção desconhecida: $1"
        echo "Use: ./build.sh compile / run / server / client / clean"
        exit 1
        ;;
esac
