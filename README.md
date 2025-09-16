# Gerenciador de Pedidos - Guia de Execução

Este projeto consiste em um sistema de gerenciamento de pedidos com duas interfaces: um servidor HTTP REST API e uma aplicação CLI (Command Line Interface) que consome a API.

## Pré-requisitos

- **Java 11 ou superior**
- **Maven 3.6 ou superior**
- **Sistema Operacional**: Windows, Linux ou macOS

## Estrutura do Projeto

O projeto possui dois pontos de entrada principais:
- `Main.java` - Servidor HTTP REST API
- `CLIMain.java` - Cliente CLI que consome a API

## Como Executar

### 1. Preparação do Ambiente

#### 1.1. Navegue até o diretório do projeto
```bash
cd \\wsl.localhost\Ubuntu-24.04\home\antoniolemos\infnet\gerenciador-pedidos\gerenciador-pedidos
```

#### 1.2. Compile o projeto com Maven
```bash
mvn clean compile
```

### 2. Iniciando o Servidor HTTP

O servidor HTTP deve ser iniciado **PRIMEIRO** antes de usar a aplicação CLI.

#### 2.1. Execute o servidor
```bash
mvn exec:java -Dexec.mainClass="Main"
```

**Ou usando Java diretamente:**
```bash
java -cp target/classes Main
```

#### 2.2. Verificação do Servidor
Quando o servidor iniciar corretamente, você verá:
- O servidor estará rodando na porta **7000**
- URL base: `http://localhost:7000`
- O banco de dados SQLite será criado automaticamente se não existir

#### 2.3. Endpoints Disponíveis

**Autenticação:**
- `POST /auth/register` - Registrar usuário
- `POST /auth/login` - Login
- `POST /auth/logout` - Logout
- `GET /profile` - Perfil do usuário autenticado

**Produtos:**
- `GET /products` - Listar produtos
- `GET /products/{id}` - Buscar produto por ID

**Endereços:**
- `GET /addresses` - Listar endereços do usuário
- `POST /addresses` - Criar endereço
- `PUT /addresses/{id}` - Atualizar endereço
- `DELETE /addresses/{id}` - Deletar endereço

**Pedidos:**
- `GET /orders` - Listar pedidos do usuário
- `GET /orders/{id}` - Buscar pedido por ID
- `POST /orders` - Criar pedido
- `DELETE /orders/{id}` - Cancelar pedido

### 3. Iniciando a Aplicação CLI

A aplicação CLI deve ser executada **APÓS** o servidor estar rodando.

#### 3.1. Abra um novo terminal
Mantenha o terminal do servidor rodando e abra uma nova janela/aba de terminal.

#### 3.2. Navegue até o diretório do projeto (se necessário)
```bash
cd \\wsl.localhost\Ubuntu-24.04\home\antoniolemos\infnet\gerenciador-pedidos\gerenciador-pedidos
```

#### 3.3. Execute a aplicação CLI
```bash
mvn exec:java -Dexec.mainClass="CLIMain"
```

**Ou usando Java diretamente:**
```bash
java -cp target/classes CLIMain
```

## Usando a Aplicação CLI

### Menu Principal

Quando a aplicação CLI iniciar, você verá o seguinte fluxo:

#### 1. Tela de Autenticação
```
=== AUTENTICAÇÃO ===
1. Login
2. Registrar
3. Sair
```

**Para novos usuários:**
- Escolha a opção "2. Registrar"
- Preencha: nome, email, senha, confirmação de senha e CPF
- Após o registro, faça login com as credenciais criadas

**Para usuários existentes:**
- Escolha a opção "1. Login"
- Digite email e senha

#### 2. Menu Principal (após autenticação)
```
=== MENU PRINCIPAL ===
1. Gerenciar Produtos
2. Gerenciar Endereços
3. Gerenciar Pedidos
4. Logout
5. Sair
```

### Funcionalidades Disponíveis

#### Gerenciar Produtos
- **Listar produtos**: Visualizar todos os produtos disponíveis
- **Buscar por ID**: Ver detalhes específicos de um produto

#### Gerenciar Endereços
- **Listar endereços**: Ver seus endereços cadastrados
- **Adicionar endereço**: Criar novo endereço de entrega
- **Atualizar endereço**: Modificar endereço existente
- **Deletar endereço**: Remover endereço

#### Gerenciar Pedidos
- **Listar pedidos**: Ver histórico de pedidos
- **Ver detalhes**: Informações completas de um pedido específico
- **Criar pedido**: Fazer um novo pedido
- **Cancelar pedido**: Cancelar pedido existente

## Exemplos de Uso

### Exemplo: Criar um Pedido Completo

1. **Inicie o servidor**
2. **Inicie a CLI**
3. **Registre-se ou faça login**
4. **Cadastre um endereço:**
   - Menu Principal → 2. Gerenciar Endereços → 2. Adicionar novo endereço
5. **Visualize produtos disponíveis:**
   - Menu Principal → 1. Gerenciar Produtos → 1. Listar todos os produtos
6. **Crie um pedido:**
   - Menu Principal → 3. Gerenciar Pedidos → 3. Criar novo pedido
   - Selecione o endereço cadastrado
   - Adicione produtos ao carrinho

## Solução de Problemas

### Erro: "Connection refused"
- **Causa**: Servidor não está rodando
- **Solução**: Inicie o servidor primeiro usando `Main.java`

### Erro: "Address already in use"
- **Causa**: Porta 7000 já está em uso
- **Solução**: Encerre outros processos na porta 7000 ou altere a porta no código

### Erro de Compilação
- **Causa**: Dependências não instaladas
- **Solução**: Execute `mvn clean install` para baixar dependências

### Erro: "Class not found"
- **Causa**: Projeto não foi compilado
- **Solução**: Execute `mvn clean compile` antes de executar

## Tecnologias Utilizadas

- **Backend**: Java 11+, Javalin (Web Framework)
- **Banco de Dados**: SQLite
- **Build**: Maven
- **Serialização**: Jackson (JSON)
- **Autenticação**: Token Bearer simples
- **Cliente HTTP**: Java HTTP Client nativo

## Estrutura de Dados

O sistema utiliza SQLite com as seguintes tabelas:
- `users` - Usuários do sistema
- `addresses` - Endereços dos usuários
- `products` - Catálogo de produtos
- `orders` - Pedidos realizados
- `order_items` - Itens dos pedidos

## Observações Importantes

1. **O servidor deve estar sempre rodando** para a CLI funcionar
2. **Dados são persistidos** no banco SQLite local
3. **Autenticação é obrigatória** para todas as operações exceto login/registro
4. **Produtos são somente leitura** na CLI (apenas visualização)
5. **Pedidos podem ser cancelados** apenas se estiverem em status apropriado

## Parar as Aplicações

- **Servidor**: Pressione `Ctrl+C` no terminal do servidor
- **CLI**: Use a opção "5. Sair" no menu ou pressione `Ctrl+C`
