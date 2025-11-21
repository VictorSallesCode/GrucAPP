# 🔷 GRUC - Gerenciador de Validade de Certificados 🟢

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![Status](https://img.shields.io/badge/Status-Concluído-success?style=for-the-badge)

> **Gestão inteligente e visual para conformidade de equipes operacionais.**

---

## 🟦 Sobre o Projeto

O **GRUC** é uma aplicação Android nativa desenvolvida para gerenciar o vencimento de certificados obrigatórios (AVSEC, CNV e Credenciais) de funcionários.

O diferencial do projeto é a sua **lógica visual de alertas**: o sistema calcula automaticamente os prazos de validade e notifica tanto o funcionário quanto o líder através de um sistema de cores (semáforo), permitindo uma ação proativa antes que um certificado vença.

---

## ❇️ Funcionalidades Principais

### 👤 Para o Funcionário
* **Dashboard Visual:** Visualização imediata do status dos 3 certificados.
* **Cálculo Automático:** O app projeta a data de vencimento baseada na data de emissão (1 ou 2 anos).
* **Alertas Inteligentes:** Pop-ups de aviso quando o vencimento se aproxima.
* **Gestão de Perfil:** Edição de dados de contato e atualização de datas de emissão.

### 👔 Para o Líder
* **Lista Inteligente ("Smart List"):** Visualização de toda a equipe.
* **Detecção de Criticidade:** A borda da foto do funcionário na lista muda de cor baseada no *pior* certificado dele. O líder sabe quem precisa de atenção sem abrir o perfil.
* **Filtros de Pesquisa:** Busca rápida por Nome ou CPF.
* **Modo Leitura:** Visualização detalhada dos dados do funcionário sem permissão de alteração.

### 🔐 Segurança & Acesso
* **Login Seguro:** Autenticação via CPF e Senha.
* **Recuperação de Conta:** Sistema de "Esqueci a Senha" validado por confirmação de dados cadastrais.
* **Banco de Dados Local:** Persistência de dados offline utilizando SQLite.

---

## 🔷 Regra de Negócio (Cores e Prazos)

O sistema processa as datas em tempo real e atribui os seguintes status:

| Status | Cor | Regra de Tempo | Ação do App |
| :--- | :---: | :--- | :--- |
| **Seguro** | 🟢 Verde | > 3 meses para o vencimento | Nenhuma |
| **Atenção** | 🟡 Amarelo | Entre 3 meses e 15 dias | Alerta "Atenção" ao logar |
| **Crítico** | 🟠 Laranja | < 15 dias (2 semanas) | Alerta "Urgente" + Borda Laranja |
| **Vencido** | 🔴 Vermelho | Data ultrapassada | Alerta "Vencido" + Borda Vermelha |

---

## 🌳 Estrutura do Projeto

Abaixo, a árvore de arquivos das classes Java e Layouts XML que compõem a arquitetura MVC do aplicativo:

```text
📂 com.example.gruc
│
├── 💾 Banco de Dados & Modelos
│   ├── DBHelper.java           # Gerenciador do SQLite (CRUD)
│   └── User.java               # Modelo de Objeto (POJO) do Usuário
│
├── 📱 Telas de Acesso (Activities)
│   ├── LoginActivity.java          # Tela Principal de Entrada
│   ├── RegisterActivity.java       # Cadastro (Lógica Lider/Func)
│   └── ForgotPasswordActivity.java # Recuperação de Senha
│
├── 👤 Módulo do Funcionário
│   ├── EmployeeHomeActivity.java   # Dashboard com lógica de cores
│   └── EditProfileActivity.java    # Alteração de dados
│
├── 👔 Módulo do Líder
│   ├── LeaderHomeActivity.java     # Lista geral da equipe
│   ├── EmployeeDetailActivity.java # Visualização "Read-Only"
│   └── EmployeeAdapter.java        # Adaptador da Lista (Lógica da borda colorida)
│
└── 🎨 Resources (Layouts XML)
    ├── activity_login.xml
    ├── activity_register.xml
    ├── activity_employee_home.xml
    ├── activity_leader_home.xml
    ├── item_employee_list.xml      # Design de cada item da lista
    └── drawable/circle_status.xml  # O "semáforo" dinâmico


🛠️ Tecnologias Utilizadas
Linguagem: Java (JDK 8+)

Frontend: XML (Android Layouts)

Layouts: ConstraintLayout, ScrollView, RecyclerView.

Armazenamento: SQLite (Nativo Android).

IDE: Android Studio Koala/Jellyfish.

🚀 Como Executar o Projeto
Clone este repositório ou baixe o arquivo .zip.

Abra o Android Studio.

Selecione Open an Existing Project e aponte para a pasta do projeto.

Aguarde o Gradle sincronizar as dependências.

Conecte um emulador ou dispositivo físico.

Clique no botão Run (▶️).

Nota: O banco de dados é criado automaticamente na primeira execução do aplicativo no dispositivo.

🔮 Próximos Passos (Roadmap)
[ ] Migração do banco SQLite para Nuvem (Firebase ou MySQL).

[ ] Implementação de upload de foto real (Câmera/Galeria) com compressão.

[ ] Notificações Push para avisar vencimentos mesmo com o app fechado.

[ ] Geração de relatórios em PDF para o líder.

<div align="center"> <sub>Desenvolvido com foco em Clean Code e Usabilidade.</sub> </div>