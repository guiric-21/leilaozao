# 🧪 Guia de Testes - Sistema de Leilões Distribuído

Este guia fornece instruções passo a passo para testar o sistema de leilões distribuído.

---

## ✅ Teste 1: Compilação Básica

### Objetivo
Verificar se todos os arquivos Java compilam corretamente.

### Passos
1. Abra um terminal/PowerShell na pasta do projeto
2. Execute:
   ```bash
   javac *.java
   ```
3. Verifique se não há erros
4. Confirme que arquivos `.class` foram criados

### Resultado Esperado
✓ Nenhuma mensagem de erro
✓ Arquivos `.class` visíveis com `dir *.class` (Windows) ou `ls *.class` (Linux/Mac)

---

## ✅ Teste 2: Inicialização do Servidor

### Objetivo
Verificar se o servidor inicia corretamente e cria os leilões.

### Passos
1. Abra um terminal e execute:
   ```bash
   java Server
   ```

### Resultado Esperado
```
╔════════════════════════════════════════╗
║  SERVIDOR DE LEILÕES INICIADO          ║
║  Porta: 5555                           ║
║  ThreadPool: 10 clientes simultâneos   ║
╚════════════════════════════════════════╝
Leilões inicializados: 4 itens disponíveis
```

### Ações
- Deixe o servidor rodando
- **NÃO feche este terminal** (mantenha aberto para próximos testes)

---

## ✅ Teste 3: Conexão de um Cliente

### Objetivo
Testar se um cliente consegue se conectar ao servidor.

### Passos
1. Abra um **novo** terminal e execute:
   ```bash
   java Client
   ```
2. Quando solicitado "Digite seu nome:", digite um nome (ex: "Alice")

### Resultado Esperado
```
✓ Conectado ao servidor de leilões!
Bem-vindo ao Sistema de Leilões!
Digite seu nome:
Alice
Olá, Alice! Digite 'ajuda' para ver comandos disponíveis.
```

### Ações
- Digite `ajuda` para ver menu de comandos
- **Deixe este cliente aberto** para próximos testes

---

## ✅ Teste 4: Listar Leilões

### Objetivo
Verificar se o cliente consegue listar todos os leilões disponíveis.

### Passos
1. No terminal do cliente, digite:
   ```
   listar
   ```

### Resultado Esperado
```
=== LEILÕES DISPONÍVEIS ===
#1 - Notebook Dell XPS 13 (Lance Atual: R$ 2000.00)
#2 - iPhone 15 Pro (Lance Atual: R$ 3500.00)
#3 - Relógio Inteligente (Lance Atual: R$ 800.00)
#4 - Câmera Canon EOS R5 (Lance Atual: R$ 5000.00)
```

---

## ✅ Teste 5: Ver Status de um Leilão

### Objetivo
Verificar detalhes de um leilão específico.

### Passos
1. No terminal do cliente, digite:
   ```
   status 1
   ```

### Resultado Esperado
```
=== LEILÃO #1 ===
Item: Notebook Dell XPS 13
Lance Inicial: R$ 2000.00
Lance Atual: R$ 2000.00
Vencedor Atual: Nenhum
Total de Lances: 0
Status: ATIVO
Tempo Restante: 60s
```

---

## ✅ Teste 6: Fazer um Lance

### Objetivo
Testar se o cliente consegue fazer um lance válido.

### Passos
1. No terminal do cliente, digite:
   ```
   lancar 1 2500
   ```

### Resultado Esperado
```
✓ Lance registrado com sucesso!
=== LEILÃO #1 ===
Item: Notebook Dell XPS 13
Lance Inicial: R$ 2000.00
Lance Atual: R$ 2500.00
Vencedor Atual: Alice
Total de Lances: 1
Status: ATIVO
Tempo Restante: 59s
```

---

## ✅ Teste 7: Rejeitar Lance Inválido

### Objetivo
Verificar se o sistema rejeita lances menores ou iguais ao lance atual.

### Passos
1. No terminal do cliente, digite:
   ```
   lancar 1 2000
   ```

### Resultado Esperado
```
✗ Lance deve ser maior que R$ 2500.00
```

---

## ✅ Teste 8: Visualizar Histórico de Lances

### Objetivo
Verificar se o histórico de lances é mantido corretamente.

### Passos
1. No terminal do cliente, digite:
   ```
   historico 1
   ```

### Resultado Esperado
```
=== HISTÓRICO DE LANCES - LEILÃO #1 ===
[HH:MM:SS] Alice: R$ 2500.00
```

---

## ✅ Teste 9: CONCORRÊNCIA - Múltiplos Clientes

### Objetivo
Testar se múltiplos clientes conseguem fazer lances simultaneamente (CONCORRÊNCIA).

### Passos
1. **Terminal 1** (Servidor): Mantém servidor rodando (já aberto)

2. **Terminal 2** (Cliente Alice): 
   - Já está conectado
   - Digite: `lancar 2 4000` (fazer lance no iPhone)

3. **Abra um NOVO Terminal 3** (Cliente Bob):
   ```bash
   java Client
   ```
   - Digite nome: `Bob`
   - Digite: `lancar 2 4500`

4. **Terminal 2** (Cliente Alice):
   - Digite: `historico 2`

### Resultado Esperado
```
=== HISTÓRICO DE LANCES - LEILÃO #2 ===
[HH:MM:SS] Alice: R$ 4000.00
[HH:MM:SS] Bob: R$ 4500.00
```

**Conceito Demonstrado**: ✓ CONCORRÊNCIA - múltiplos clientes acessando simultaneamente

---

## ✅ Teste 10: TRANSPARÊNCIA DE LOCALIZAÇÃO

### Objetivo
Demonstrar que o cliente não precisa saber onde o servidor está fisicamente.

### Passos
1. Servidor continua rodando em localhost:5555
2. Cliente conecta sem saber detalhes internos da arquitetura
3. Todas as operações funcionam via interface remota (socket)

### Conceito Demonstrado
✓ TRANSPARÊNCIA DE LOCALIZAÇÃO - cliente acessa dados remotos como se fossem locais

---

## ✅ Teste 11: DISPONIBILIDADE - Desconexão de Cliente

### Objetivo
Verificar que o servidor continua operacional mesmo quando um cliente desconecta.

### Passos
1. **Terminal 2** (Cliente Alice): Digite `sair`
2. Observe no terminal do **Servidor**: Deve mostrar mensagem de desconexão
3. **Terminal 3** (Cliente Bob): Digite `listar`

### Resultado Esperado
- Terminal do Servidor: `Cliente Alice desconectado.`
- Cliente Bob continua funcionando normalmente
- Leilões continuam disponíveis

**Conceito Demonstrado**: ✓ DISPONIBILIDADE - sistema resiliente a falhas de clientes

---

## ✅ Teste 12: Teste de Carga - Múltiplos Clientes

### Objetivo
Testar o ThreadPool com múltiplos clientes simultâneos.

### Passos
1. Abra **5 novos terminais** (além do servidor e clientes anteriores)
2. Em cada terminal, execute:
   ```bash
   java Client
   ```
3. Conecte com nomes diferentes: Carlos, Diana, Eduardo, Francisca, Gabriel
4. Cada um executa:
   ```
   lancar 3 900
   lancar 3 1000
   lancar 3 1100
   ...
   ```

### Resultado Esperado
- Todos os 5 clientes conseguem se conectar
- Lances são processados corretamente mesmo com concorrência
- Sem deadlocks ou race conditions
- Histórico registra todos os lances em ordem

**Conceito Demonstrado**: ✓ CONCORRÊNCIA - ThreadPool suporta 10 clientes (testamos 5)

---

## ✅ Teste 13: Erro de Conexão - Cliente com Servidor Inativo

### Objetivo
Verificar mensagem de erro quando servidor não está disponível.

### Passos
1. **Feche** o terminal do servidor (se ainda estiver rodando)
2. Abra um novo terminal e tente:
   ```bash
   java Client
   ```

### Resultado Esperado
```
✗ Erro ao conectar: Connection refused
```

**Conceito Demonstrado**: ✓ DISPONIBILIDADE - sistema avisa quando não está disponível

---

## 📊 Resumo dos Testes

| Teste | Conceito Demonstrado | Status |
|-------|----------------------|--------|
| 1-2 | Compilação e Setup | ✓ |
| 3-5 | Conectividade e Listagem | ✓ |
| 6-8 | Operações Básicas | ✓ |
| 9-10 | **CONCORRÊNCIA** | ✓ |
| 11 | **TRANSPARÊNCIA DE LOCALIZAÇÃO** | ✓ |
| 12 | **DISPONIBILIDADE** | ✓ |
| 13 | Tratamento de Erros | ✓ |

---

## 🔧 Troubleshooting

### Problema: `Porta 5555 já em uso`
**Solução**: 
- Feche aplicações que usam porta 5555
- Modifique a porta em `Server.java` (linha: `private static final int PORT = 5555;`)

### Problema: `Connection refused`
**Solução**:
- Verifique se servidor está rodando
- Verifique hostname e porta no cliente
- Use `localhost` ao invés de `127.0.0.1`

### Problema: Mensagens truncadas no cliente
**Solução**:
- Maximize a janela do terminal
- Use `historico <id>` para ver histórico completo

---

## 📝 Notas Importantes

1. **Sincronização**: Todos os dados são thread-safe via `synchronized` e `ConcurrentHashMap`
2. **Duração de Leilões**: 60 segundos (modificável em `Auction.java`)
3. **Limite de Clientes**: 10 simultâneos (ThreadPool size em `Server.java`)
4. **Dados em Memória**: Tudo é perdido ao desligar servidor (sem persistência)

---

## 🎯 Próximos Passos

Após completar os testes:
1. Adicione persistência em banco de dados
2. Implemente replicação de servidores
3. Crie interface web (WebSocket)
4. Adicione autenticação de usuários
5. Implemente descoberta automática de servidores
