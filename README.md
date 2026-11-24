# 📚 Folha — Sistema de Gerenciamento de Biblioteca

O **Folha** é um sistema web desenvolvido para auxiliar no gerenciamento de uma biblioteca. A aplicação permite cadastrar e organizar livros, autores, categorias, editoras e leitores, além de controlar empréstimos, devoluções e renovações.

O projeto foi desenvolvido para a disciplina de **Desenvolvimento de Software II**.

## 👩‍💻 Integrantes

- Luciara Pereira — [https://github.com/LuciaraPereira/](https://github.com/LuciaraPereira/)
- Mariana Vieira — [https://github.com/marianavivieira](https://github.com/marianavivieira)
- Sabrina Clemente — [https://github.com/sabrinacls](https://github.com/sabrinacls)
- Vivian Gomes — [https://github.com/vivigomesleal](https://github.com/vivigomesleal)

## 🛠️ Tecnologias utilizadas

### Back-end

- Java 17;
- Spring Boot;
- Spring Web;
- Spring Data JPA;
- Spring Security;
- Bean Validation;
- Maven.

### Front-end

- HTML;
- CSS;
- JavaScript.

### Banco de dados e outras ferramentas

- PostgreSQL;
- Neon PostgreSQL;
- Swagger/OpenAPI;
- Docker;
- API da Open Library.

## ⚙️ Como executar o projeto

### Pré-requisitos

Antes de começar, é necessário ter instalado:

- Java 17;
- Maven;
- Git;
- PostgreSQL ou acesso a um banco PostgreSQL no Neon.

### 1. Clone o repositório

```bash
git clone https://github.com/LuciaraPereira/Folha.git
```

### 2. Acesse a pasta do projeto

```bash
cd Folha
```

### 3. Configure o banco de dados

Crie um banco PostgreSQL ou utilize um banco existente. Em seguida, configure as variáveis de ambiente com os dados da conexão e uma senha para o administrador inicial.

Exemplo no **PowerShell**:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/biblioteca"
$env:DB_USERNAME="seu_usuario"
$env:DB_PASSWORD="sua_senha"
$env:ADMIN_PASSWORD="sua_senha_de_administrador"
```

Se estiver utilizando o Neon, substitua os dados pelos informados na sua conexão.

### 4. Execute a aplicação

```bash
mvn spring-boot:run
```

### 5. Acesse o sistema

Abra o navegador e acesse:

```text
http://localhost:8081
```

Caso ainda não exista um usuário com o login `admin`, o sistema criará o administrador inicial usando a senha definida na variável `ADMIN_PASSWORD`.

```text
Usuário: admin
Senha: definida na variável ADMIN_PASSWORD
```

## 🔗 Links

- **Aplicação publicada:** [Folha - Acesse aqui](https://folha-8ygy.onrender.com/?classId=3c3c5fc4-a93d-4e3e-b194-b0e99b81a7a2&assignmentId=7ff1c3de-65f4-4ab6-9a16-102c1f784b79&submissionId=6d477f5e-f726-e987-da7c-6bc84e6c3a7e)
- **Documentação Swagger:** [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

> **Observação:** 
Como a aplicação está hospedada no plano gratuito do Render, ela pode entrar em modo de inatividade após um período sem acesso. Ao abri-la novamente, pode ser necessário aguardar alguns instantes para que o servidor seja iniciado e o sistema carregue.
Para acessar o Swagger localmente, a aplicação deve estar em execução e o usuário deve estar autenticado.
