@echo off
REM Script para compilar e executar o Sistema de Leilões Distribuído

setlocal enabledelayedexpansion

echo ╔═══════════════════════════════════════════════╗
echo ║   SISTEMA DE LEILÕES DISTRIBUÍDO             ║
echo ╚═══════════════════════════════════════════════╝
echo.

if "%1"=="" (
    echo Uso: build.bat [opcao]
    echo.
    echo Opções:
    echo   compile     - Compila todos os arquivos Java
    echo   run         - Executa o launcher principal
    echo   server      - Inicia apenas o servidor
    echo   client      - Inicia apenas o cliente
    echo   clean       - Remove arquivos .class compilados
    echo.
    goto end
)

if "%1"=="compile" (
    echo [1/4] Compilando arquivos Java...
    javac *.java
    if errorlevel 1 (
        echo Erro na compilação!
        goto end
    )
    echo [2/4] Arquivos compilados com sucesso!
    echo [3/4] Listando arquivos gerados:
    dir /b *.class
    echo [4/4] Compilação concluída!
    goto end
)

if "%1"=="run" (
    echo Iniciando launcher do sistema...
    java main
    goto end
)

if "%1"=="server" (
    echo Iniciando Servidor...
    java Server
    goto end
)

if "%1"=="client" (
    echo Iniciando Cliente...
    java Client
    goto end
)

if "%1"=="clean" (
    echo Limpando arquivos compilados...
    del *.class 2>nul
    echo Limpeza concluída!
    goto end
)

echo Opção desconhecida: %1
echo Use: build.bat compile / run / server / client / clean

:end
endlocal
