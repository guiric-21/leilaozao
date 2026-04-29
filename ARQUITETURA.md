# 🏛️ Arquitetura - Sistema de Leilões Distribuído

Documentação técnica detalhada da arquitetura do sistema.

---

## 📐 Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────────┐
│                     CLIENTE (Java Application)                       │
│                                                                       │
│  ┌─────────────┐                                                     │
│  │   Scanner   │ ◄─── Entrada do usuário (CLI)                      │
│  └──────┬──────┘                                                     │
│         │                                                             │
│  ┌──────▼──────────────────┐                                         │
│  │   Client.java           │                                         │
│  │ - Socket connection     │                                         │
│  │ - Command sender        │                                         │
│  │ - Message receiver      │                                         │
│  └──────┬──────────────────┘                                         │
│         │                                                             │
│  ┌──────▼──────────────────┐                                         │
│  │  ServerMessageReader    │ ◄─── Thread para receber mensagens     │
│  │  (implementa Runnable)  │                                         │
│  └──────┬──────────────────┘                                         │
│         │                                                             │
│         └─────────────┬──────────────────┬────────────────┐          │
│                       │                  │                │          │
│                   stdout           stdout            stdout          │
└───────────────────────┼──────────────────┼────────────────┼──────────┘
                        │                  │                │
                        │                  │                │
                    TCP/IP SOCKET COMMUNICATION            │
                        │                  │                │
                        │                  │                │
┌───────────────────────▼──────────────────▼────────────────▼──────────┐
│                     SERVIDOR (Java Application)                       │
│                                                                       │
│  ┌────────────────────────────────────────────────────────────┐      │
│  │  Server.java (Main Thread)                                 │      │
│  │  - ServerSocket listener na porta 5555                    │      │
│  │  - Aceita conexões de clientes                            │      │
│  │  - Submete cada cliente ao ThreadPool                     │      │
│  └────┬──────────────────────────────────────────────────────┘      │
│       │                                                              │
│  ┌────▼──────────────────────────────────────────────────────┐      │
│  │  ExecutorService (ThreadPool) - 10 threads               │      │
│  │                                                            │      │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │      │
│  │  │ ClientHandler│  │ ClientHandler│  │ ClientHandler│ ...│      │
│  │  │   Thread 1   │  │   Thread 2   │  │   Thread N   │    │      │
│  │  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘    │      │
│  │         │                 │                 │             │      │
│  │         └─────────────────┴─────────────────┘             │      │
│  │                         │                                 │      │
│  │         ┌───────────────▼───────────────┐               │      │
│  │         │  AuctionManager (COMPARTILHADO) │              │      │
│  │         │  - ConcurrentHashMap<id, Auction>│             │      │
│  │         │  - Thread-safe operations   │                │      │
│  │         │  - Cria/lista/gerencia leilões │              │      │
│  │         └───────┬───────────────┬─────────┘              │      │
│  │                 │               │                        │      │
│  │  ┌──────────────▼────────┐  ┌──▼─────────────────────┐  │      │
│  │  │  Auction 1            │  │  Auction 2             │  │      │
│  │  │ - synchronized addBid │  │ - synchronized addBid  │  │      │
│  │  │ - bidHistory: List    │  │ - bidHistory: List     │  │      │
│  │  │ - currentHighestBid   │  │ - currentHighestBid    │  │      │
│  │  └──────────┬────────────┘  └─────┬──────────────────┘  │      │
│  │             │                     │                     │      │
│  │  ┌──────────▼────────┐ ┌─────────▼───────────┐         │      │
│  │  │  Bid {            │ │  Bid {              │         │      │
│  │  │    bidderName     │ │    bidderName       │         │      │
│  │  │    amount         │ │    amount           │         │      │
│  │  │    timestamp      │ │    timestamp        │         │      │
│  │  │  }                │ │  }                  │         │      │
│  │  └───────────────────┘ └─────────────────────┘         │      │
│  │                                                            │      │
│  └────────────────────────────────────────────────────────┘      │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Fluxo de Requisição - Exemplo: `lancar 1 2500`

```
1. CLIENTE
   ┌──────────────────────────┐
   │ Usuário digita:          │
   │ > lancar 1 2500          │
   └────────────┬─────────────┘
                │
   2. TRANSMISSÃO
                │ Socket TCP/IP
                │ "lancar 1 2500"
                │
                ▼
   3. SERVIDOR - ClientHandler (Thread de cliente)
   ┌──────────────────────────┐
   │ Recebe: "lancar 1 2500"  │
   │ Parser: [lancar, 1, 2500]│
   │ Extrai: id=1, valor=2500 │
   └────────────┬─────────────┘
                │
   4. SERVIDOR - AuctionManager
   ┌─────────────────────────────┐
   │ auctionManager.placeBid(    │
   │   auctionId=1,              │
   │   bidderName="João",        │
   │   amount=2500.0             │
   │ )                           │
   └────────────┬────────────────┘
                │
   5. SERVIDOR - Auction (synchronized)
   ┌──────────────────────────────┐
   │ auction.addBid(              │
   │   bidderName="João",         │
   │   amount=2500.0              │
   │ )                            │
   │                              │
   │ ✓ Verifica se está ativo    │
   │ ✓ Verifica se valor > maior  │
   │ ✓ Cria novo Bid object      │
   │ ✓ Adiciona ao bidHistory    │
   │ ✓ Atualiza currentHighestBid│
   │ ✓ Retorna true              │
   └────────────┬─────────────────┘
                │
   6. RESPOSTA PARA CLIENTE
   ┌────────────────────────────────┐
   │ ✓ Lance registrado com sucesso!│
   │ === LEILÃO #1 ===              │
   │ Item: Notebook Dell XPS 13     │
   │ Lance Atual: R$ 2500.00        │
   │ Vencedor: João                 │
   │ ...                            │
   └────────────┬────────────────────┘
                │ Socket TCP/IP
                │
   7. CLIENTE - Recebe e Exibe
   ┌──────────────────────────────────┐
   │ Mostra resposta ao usuário       │
   └──────────────────────────────────┘
```

---

## 🧵 Modelo de Threads

```
PROCESSO DO SERVIDOR
│
├─ Main Thread
│  ├─ Inicializa ServerSocket (porta 5555)
│  ├─ Inicializa AuctionManager (compartilhado)
│  ├─ Inicializa ExecutorService (ThreadPool com 10 threads)
│  │
│  └─ Loop Infinito: Aceita conexões
│     ├─ Socket socket = serverSocket.accept()
│     ├─ Cria: ClientHandler handler = new ClientHandler(socket, auctionManager)
│     └─ Submete: executorService.execute(handler)
│
├─ Thread Pool (ExecutorService)
│  │
│  ├─ ClientHandler Thread 1 (Cliente Alice)
│  │  ├─ Lê: "listar"
│  │  ├─ Acessa: auctionManager.listAllAuctions() [THREAD-SAFE]
│  │  ├─ Escreve: lista de leilões
│  │  ├─ Lê: "lancar 1 2500"
│  │  ├─ Acessa: auctionManager.placeBid(...) [THREAD-SAFE]
│  │  ├─ Sincroniza com Auction 1 [SYNCHRONIZED]
│  │  └─ Escreve: confirmação
│  │
│  ├─ ClientHandler Thread 2 (Cliente Bob)
│  │  ├─ Lê: "listar"
│  │  ├─ Acessa: auctionManager.listAllAuctions() [THREAD-SAFE]
│  │  ├─ Escreve: lista de leilões
│  │  ├─ Lê: "lancar 1 3000"
│  │  ├─ Acessa: auctionManager.placeBid(...) [THREAD-SAFE]
│  │  ├─ Sincroniza com Auction 1 [SYNCHRONIZED]
│  │  └─ Escreve: confirmação
│  │
│  └─ ClientHandler Thread N
│     └─ ...
│
└─ (Threads podem aceitar/rejeitar lances simultaneamente sem conflito)
```

---

## 🔐 Mecanismos de Sincronização

### 1. ConcurrentHashMap (AuctionManager)

```java
private ConcurrentHashMap<Integer, Auction> auctions;

// ✓ Thread-safe sem locks explícitos
// ✓ Múltiplas threads podem ler simultaneamente
// ✓ Operações atômicas para put/get/remove
```

**Vantagens**:
- Não precisa de `synchronized` para toda a classe
- Permite leitura/escrita concorrente
- Melhor performance que `Collections.synchronizedMap()`

---

### 2. Synchronized Methods (Auction)

```java
public synchronized boolean addBid(String bidderName, double amount) {
    // Apenas uma thread por vez executa este método
    // Thread entra, outras ficam em fila de espera
}

public synchronized String getAuctionStatus() {
    // Apenas uma thread por vez executa este método
}
```

**Vantagens**:
- Garante atomicidade das operações
- Evita race conditions
- Simples de implementar

**Desvantagens**:
- Menos performance que ConcurrentHashMap
- Pode criar gargalos com muitos acessos

---

### 3. ThreadPool (ExecutorService)

```java
ExecutorService executorService = Executors.newFixedThreadPool(10);

// Cada cliente é executado em thread separada
executorService.execute(new ClientHandler(socket, auctionManager));
```

**Vantagens**:
- Limita número de threads (evita estouro de memória)
- Reutiliza threads (melhor performance)
- Gerencia ciclo de vida das threads

---

## 📊 Padrões de Concorrência

### Padrão 1: Producer-Consumer (Lances)

```
Producer (Clientes)           Consumer (AuctionManager)
    │                              │
    ├─ Alice faz lance ────────────┼─► Adiciona à fila
    │                              │
    ├─ Bob faz lance ──────────────┼─► Processa sequencialmente
    │                              │
    └─ Carol faz lance ────────────┼─► Atualiza histórico
```

---

### Padrão 2: Shared Resource (AuctionManager)

```
Thread 1          Thread 2          Thread 3
  │                 │                 │
  └─ getAuction(1)──┼─────────────────┤
  │                 │                 │
  │         ┌───────▼──────────┐      │
  │         │  AuctionManager  │      │
  │         │ (ConcurrentHashMap)    │
  │         │                  │      │
  │         │ Leilão 1         │      │
  │         │ Leilão 2         │      │
  │         │ Leilão 3         │      │
  │         └────┬──────────────┘      │
  │              │                     │
  └──────────────┼─► Leilão 1         │
                 │                     │
             ┌───▼──────────────┐     │
             │ addBid()         │     │
             │ [SYNCHRONIZED]   │     │
             │ Apenas 1 thread  │     │
             │ por vez          │     │
             └──────────────────┘     │
                                      │
                  ┌────────────────────┼─► getAuctionStatus()
                  │                    │
                  └────────────────────┘
```

---

## 🌐 Características de Sistema Distribuído

### 1️⃣ Transparência de Localização

```
ANTES (Sistema Centralizado)
┌──────────┐
│ Cliente  │ ◄─► Dados estão aqui (cliente sabe exatamente)
│ Servidor │
└──────────┘

DEPOIS (Sistema Distribuído)
┌──────────┐         ┌──────────────┐
│ Cliente  │ ◄────► │  Servidor    │ (cliente não sabe onde está)
│(ignorante)         │ (localização  │
└──────────┘         │  desconhecida)│
                     └──────────────┘
```

**Implementação**:
- Cliente usa `new Socket("localhost", 5555)`
- Não acessa dados localmente
- Todos os dados buscados via rede
- Servidor poderia estar em outro país

---

### 2️⃣ Concorrência

```
SEM Concorrência:
Cliente 1: Faz lance → Aguarda ← Libera → Cliente 2: Faz lance
(serial, lento)

COM Concorrência:
Cliente 1: Faz lance ──┐
                       ├─► Servidor processa SIMULTANEAMENTE
Cliente 2: Faz lance ──┤
                       ├─► Sem esperar um pelo outro
Cliente 3: Faz lance ──┘
(paralelo, rápido)
```

**Implementação**:
- `ThreadPool` com 10 threads
- Cada cliente em thread separada
- Sincronização com `synchronized` e `ConcurrentHashMap`
- Teste: múltiplos clientes fazem lances ao mesmo tempo

---

### 3️⃣ Disponibilidade

```
FALHA DE CLIENTE           SERVIDOR CONTINUA DISPONÍVEL
┌──────────┐
│ Alice    │ ✗ Desconecta
└──────────┘
                           ┌─────────────┐
                           │  Servidor   │
┌──────────┐               │ ✓ Continua  │
│ Bob      │ ◄──────────► │   rodando   │
└──────────┘               │             │
                           │ ✓ Leilões   │
┌──────────┐               │   intactos  │
│ Carol    │ ◄──────────► │             │
└──────────┘               └─────────────┘
```

**Implementação**:
- Loop infinito no servidor aceitando conexões
- Desconexão de cliente não afeta outros
- Leilões armazenados em memória compartilhada
- Sistema continua operacional

---

## 📈 Performance Analysis

### Cenários de Teste

#### Teste 1: 1 Cliente, Muitos Lances
```
Alice faz 100 lances na sequência

Timeline:
0s:  Client 1 conecta
0-1s: 100 lances processados
     → Taxa: 100 lances/segundo

Gargalo: Velocidade de rede + processamento Auction.addBid()
```

#### Teste 2: Múltiplos Clientes, 1 Leilão
```
5 clientes fazem lances no Leilão #1 simultaneamente

Timeline:
0s:  Todos conectam
0-1s: 50 lances totais processados SIMULTANEAMENTE
     → Taxa: 50 lances/segundo (paralelo)
     vs 10 lances/segundo (serial)
     → Speedup: 5x mais rápido

Gargalo: synchronized na Auction.addBid()
         (apenas 1 thread modifica por vez)
```

#### Teste 3: Múltiplos Clientes, Múltiplos Leilões
```
5 clientes fazem lances em 4 leilões diferentes

Timeline:
0s:  Todos conectam
0-1s: 100 lances totais processados SIMULTANEAMENTE
     → Taxa: 100 lances/segundo (paralelo)

Gargalo: Minimizado (different Auction objects)
```

---

## 🔧 Configurações Críticas

### Arquivo: `Server.java`

```java
// Porta de escuta
private static final int PORT = 5555;

// Tamanho do thread pool
private static final int THREAD_POOL_SIZE = 10;
// Aumentar se espera mais de 10 clientes simultâneos
// Diminuir se máquina tem poucos recursos
```

### Arquivo: `Auction.java`

```java
// Duração de cada leilão
private static final long AUCTION_DURATION = 60000; // 60 segundos
// Aumentar para leilões mais longos
// Diminuir para testes rápidos
```

---

## 🚨 Problemas Potenciais e Soluções

| Problema | Causa | Solução |
|----------|-------|---------|
| Deadlock entre threads | Synchronized aninhados | Manter apenas 1 nível de sincronização |
| Race condition | Acesso não sincronizado | Usar ConcurrentHashMap ou synchronized |
| ThreadPool saturado | Muitos clientes | Aumentar THREAD_POOL_SIZE |
| Leilão nunca termina | Lógica de expiração | Implementar checkAndCloseAuction() |
| Memória sem limite | Sem limpeza de leilões antigos | Adicionar cleanup de leilões expirados |

---

## 🎯 Próximas Melhorias

1. **Persistência**: Banco de dados para guardar leilões
2. **Replicação**: Múltiplos servidores sincronizados
3. **Load Balancing**: Distribuir carga entre servidores
4. **Logging Distribuído**: Rastrear eventos em múltiplos nós
5. **Service Discovery**: Descoberta automática de servidores
6. **Message Queue**: Redis/RabbitMQ para assíncronia
7. **Caching**: Cache de leilões frequentes

---

## 📚 Referências

- Java Concurrency in Practice - Brian Goetz
- "Designing Data-Intensive Applications" - Martin Kleppmann
- Oracle Java Docs: https://docs.oracle.com/javase/tutorial/essential/concurrency/
