# Eu Conto 📊
### Contabilidade PGC-NIRF: Inventários e Balanços Moçambicanos

**Eu Conto** é um aplicativo Android móvel e adaptivo desenhado para automatizar processos de contabilidade e elaboração de relatórios financeiros em conformidade com o **Plano Geral de Contas para as Normas Integradas de Relato Financeiro (PGC-NIRF)** da República de Moçambique.

O aplicativo serve como um assistente completo para contadores, técnicos de contas e gerentes de empresas, facilitando o levantamento de inventários patrimoniais, classificação de contas e verificação da saúde financeira corporativa em mobilidade.

---

## 🚀 Principais Funcionalidades

### 1. Inventário Classificado Patrimonial (PGC-NIRF)
*   **Seccionamento Oficial:** Organização minuciosa entre **Activo** (Bens e Direitos), **Passivo** (Obrigações) e **Capital Próprio** (Situação Líquida).
*   **Estruturação de Classes:** Agrupamento e totalização automáticos por Classes do PGC-NIRF (de 1 a 8), como *Meios Financeiros*, *Inventários*, *Investimentos de Capital*, *Contas a Receber e a Pagar*, entre outras.
*   **Níveis de Contas:** Detalhamento hierárquico, desde a conta principal (2 dígitos) até subcontas específicas.

### 2. Classificação Inteligente com IA (Gemini API) ✨
*   **Enquadramento Automático:** Insira a descrição do bem ou obrigação (ex: *"Arroz comercial para revenda"* ou *"Empréstimo bancário de curto prazo"*) e a Inteligência Artificial mapeará o item instantaneamente para a classe e a conta correta do PGC-NIRF.
*   **Gestão Segura de Chave de API:** O usuário pode configurar sua própria chave Gemini API diretamente no aplicativo, armazenada de forma segura nas preferências locais do celular.
*   **Robustez na Chamada:** Payload moderno utilizando requisição JSON purificada diretamente na API Gemini.

### 3. Emissor de Relatórios Oficiais em PDF 📄
*   **Layout Técnico Impresso:** Exporta documentos PDF formatados no padrão A4, prontos para submissão oficial e contabilidade interna.
*   **Páginas Dinâmicas:** Gerenciamento inteligente de quebras de página contábeis com cabeçalhos de continuação e numeração.
*   **Comentário Executivo de Situação Líquida:** A partir dos resultados consolidados de Activo e Passivo, o aplicativo gera automaticamente um comentário textual em conformidade contábil descrevendo o estado de Situação Patrimonial Líquida (Favorável/Ativa, Nula, ou Desfavorável/Passiva).
*   **Campos de Assinatura:** Bloco final com posições formais para o Técnico de Contabilidade e a Gerência corporativa.

### 4. Persistência Offline (Banco de Dados Room) 🗄️
*   Armazenamento resiliente e local de múltiplas Empresas (Nome, Atividade, Cidade, NUIT), Inventários agendados e elementos patrimoniais detalhados.
*   Funciona totalmente offline sem requisições a servidores externos terceiros, garantindo máxima privacidade dos dados fiscais e corporativos.

---

## 🛠️ Arquitetura e Tecnologias

*   **Linguagem de Programação:** Kotlin (Modern Android Development)
*   **Interface Gráfica (UI):** Jetpack Compose na sua totalidade com design moderno seguindo as diretrizes do **Material 3 (M3)**.
*   **Persistência Local:** Room Database para gerenciar o modelo relacional de informações contábeis.
*   **Rede e API:** OkHttp para chamadas HTTP ultra-síncronas e robustas junto ao ecossistema Gemini.
*   **Engine de PDF:** Android native `PdfDocument` com cálculos de insets, posicionamento linear dinâmico de texto `wrapText` e desenho de tabelas corporativas direto no Canvas de pintura.

---

## ⚙️ Configuração da Inteligência Artificial

Para iniciar a classificação e catalogação automática baseada em inteligência artificial:

1.  Acesse o menu de **Configurações** (ou clique no botão de engrenagem azul na tela de adição de elementos adicionais).
2.  Insira uma **Chave de API do Gemini** (a qual pode ser obtida gratuitamente em [Google AI Studio](https://aistudio.google.com/)).
3.  Pronto! Ligue o interruptor de IA e utilize o botão *"Classificar com IA"* para realizar sugestões automáticas de contas contábeis num clique.
