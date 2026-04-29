# ⚡ Quick Start - Sistema de Leilões

Guia rápido para começar em 5 minutos!

---

## 1️⃣ Compilar

```bash
cd c:\Users\guilm\Desktop\leilaozao

# Windows
javac *.java

# Ou use o script
build.bat compile
```

---

## 2️⃣ Iniciar Servidor

```bash
# Terminal 1
java Server
```

**Esperado:**
```
╔════════════════════════════════════════╗
║  SERVIDOR DE LEILÕES INICIADO          ║
║  Porta: 5555                           ║
║  ThreadPool: 10 clientes simultâneos   ║
╚════════════════════════════════════════╝
Leilões inicializados: 4 itens disponíveis
```

---

## 3️⃣ Conectar Cliente

```bash
# Terminal 2
java Client

# Digite seu nome quando solicitado
Nome: Alice
```

---

## 4️⃣ Testar Comandos

```
# No terminal do cliente, digite:

listar                    # Ver todos os leilões
status 1                  # Ver detalhes do leilão #1
lancar 1 2500            # Fazer lance de R$ 2500 no leilão #1
historico 1              # Ver histórico de lances
ajuda                    # Ver menu de ajuda
sair                     # Desconectar
```

---

## 🧪 Teste Rápido: CONCORRÊNCIA

```bash
# Terminal 1 - Servidor
java Server

# Terminal 2 - Cliente Alice
java Client
# Nome: Alice
# Comando: lancar 1 2500

# Terminal 3 - Cliente Bob (novo terminal)
java Client
# Nome: Bob
# Comando: lancar 1 3000

# Terminal 2 - Alice
# Comando: historico 1
```

**Resultado**: Ambos os lances aparecem no histórico ✓

---

## 📁 Arquivos do Projeto

| Arquivo | Descrição |
|---------|-----------|
| `main.java` | Launcher do sistema |
| `Server.java` | Servidor de leilões (socket + threads) |
| `Client.java` | Cliente para se conectar |
| `ClientHandler.java` | Gerencia cada cliente (thread) |
| `AuctionManager.java` | Gerencia leilões (thread-safe) |
| `Auction.java` | Modelo de leilão |
| `Bid.java` | Modelo de lance |
| `README.md` | Documentação completa |
| `ARQUITETURA.md` | Detalhes de arquitetura e threads |
| `TESTES.md` | Guia de testes detalhado |

---

## 🎯 Características Distribuídas Implementadas

✅ **Transparência de Localização**
- Clientes acessam servidor sem saber localização exata

✅ **Concorrência**
- ThreadPool permite múltiplos clientes simultâneos
- Lances são processados em paralelo sem conflitos

✅ **Disponibilidade**
- Servidor continua disponível mesmo com falhas de clientes
- Sistema resiliente

---

## 🔧 Troubleshooting Rápido

| Problema | Solução |
|----------|---------|
| `javac: comando não encontrado` | Instale JDK (https://www.oracle.com/java/technologies/downloads/) |
| `Connection refused` | Servidor não está rodando, inicie com `java Server` |
| `Porta 5555 em uso` | Feche outra app usando essa porta ou mude em `Server.java` |
| Múltiplos clientes não funcionam | Verifique se `ExecutorService` está inicializado em `Server.java` |

---

## 📖 Próximas Leituras

1. `README.md` - Visão geral completa
2. `ARQUITETURA.md` - Como funciona internamente
3. `TESTES.md` - Testes detalhados para validar sistema

---

## 💡 Dicas

- Use `Ctrl+C` para parar servidor ou cliente
- Teste com múltiplos clientes para ver concorrência funcionando
- Observe o histórico de lances para entender sincronização
- Verifique logs no terminal do servidor para ver conexões

---

Divirta-se com o sistema distribuído! 🚀
