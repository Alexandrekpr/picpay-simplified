# PicPay Simplificado

Uma API REST robusta que simula uma plataforma de pagamentos simplificada, permitindo a transferência de dinheiro entre usuários comuns e lojistas. O projeto foca em boas práticas de desenvolvimento, arquitetura em camadas com Spring Boot e integrações com serviços externos.

---

## 🛠️ Tecnologias e Ferramentas

* **Linguagem:** Java 17 (Compatível com versões superiores, desenvolvido e testado também em Java 25)
* **Framework:** Spring Boot 3.x
    * Spring Web (REST APIs)
    * Spring Data JPA (Persistência de dados)
    * Spring Validation (Validação de payloads)
* **Banco de Dados:** PostgreSQL / H2 (Banco em memória para testes/desenvolvimento)
* **Ferramentas Auxiliares:** Lombok (Produtividade), RestTemplate (Comunicação HTTP)

---

## ⚙️ Regras de Negócio Implementadas

* **Tipos de Usuários:** O sistema diferencia usuários comuns de lojistas.
* **Restrição de Lojistas:** Lojistas podem apenas *receber* transferências; eles não têm permissão para enviar dinheiro.
* **Validação de Saldo:** O usuário pagador deve ter saldo suficiente antes de concluir a transação.
* **Autorização Externa:** Antes de finalizar, a transferência consulta um serviço autorizador externo.
* **Consistência (Transacional):** A transferência é protegida por `@Transactional`, garantindo que se qualquer etapa falhar (como o débito ou crédito), a operação sofra rollback completo.
* **Notificações:** Após o sucesso da transação, o recebedor recebe uma notificação assíncrona enviada por um serviço de e-mail externo.

---

## 🛣️ Rotas da API

### 👥 Usuários (`/users`)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/users` | Cadastra um novo usuário (Comum ou Lojista) |
| `GET` | `/users` | Lista todos os usuários cadastrados de forma paginada |
| `GET` | `/users/{id}` | Busca os detalhes de um usuário específico pelo ID |

### 💸 Transações (`/transactions`)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/transactions` | Realiza a transferência de saldo entre dois usuários |

#### Exemplo de Payload para Transação (`POST /transactions`):
```json
{
  "value": 100.00,
  "senderId": 4,
  "receiverId": 15
}
