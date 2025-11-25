# 📝 Atividade 3 - Algoritmos de Gerenciamento de Memória com Mapa de Bits

Esta branch contém o código-fonte desenvolvido para a atividade de **Gerenciamento de Memória** da disciplina de Sistemas Operacionais. O objetivo é implementar e simular o comportamento da memória RAM utilizando um **Mapa de Bits** para controlar a alocação e desalocação de processos.

Todo o código relacionado a esta atividade está disponível de forma isolada nesta branch, facilitando a organização e a consulta dos algoritmos implementados.

## 📝 Sobre o Projeto

A aplicação simula o gerenciamento de espaço livre na memória principal, determinando como as "lacunas" (buracos) são preenchidas pelas solicitações dos processos. [cite_start]A memória é representada visualmente como um vetor de bits, onde `0` indica espaço livre e `1` indica espaço ocupado [cite: 59-61].

O projeto contempla a implementação de cinco algoritmos clássicos, permitindo a comparação entre suas estratégias de busca e eficiência no uso do espaço.

## ⚙️ Regras de Negócio e Diretivas

O simulador opera sob as seguintes diretrizes definidas na atividade:

* [cite_start]**Estrutura da Memória:** A memória RAM é simulada como um vetor de **32 blocos**[cite: 141].
* [cite_start]**Mapa de Bits:** A estrutura de dados utilizada para rastrear espaços é um vetor unidimensional de 0s e 1s[cite: 60].
* **Processos Definidos:** O sistema gerencia um conjunto fixo de 10 processos com tamanhos variados:
    * [cite_start]P1 (5 blocos), P2 (4 blocos), P3 (2 blocos), P4 (5 blocos), P5 (8 blocos) [cite: 143-148].
    * [cite_start]P6 (3 blocos), P7 (5 blocos), P8 (8 blocos), P9 (2 blocos), P10 (6 blocos) [cite: 149-154].

### Fluxo de Execução
Para cada algoritmo, o programa segue a seguinte lógica:
1.  [cite_start]Realiza o sorteio de **30 processos** sequencialmente[cite: 157].
2.  [cite_start]**Alocação:** Se o processo sorteado *não* estiver na memória, o algoritmo tenta alocá-lo[cite: 158].
3.  [cite_start]**Desalocação:** Se o processo sorteado *já* estiver na memória, ele deve ser desalocado (liberando os blocos para `0`)[cite: 159].
4.  [cite_start]**Visualização:** Exibe o mapa de memória a cada operação[cite: 160].

## 🗂️ Algoritmos Implementados

[cite_start]O projeto contém funções separadas para cada uma das seguintes estratégias[cite: 64]:

1.  [cite_start]**First Fit (Primeiro Encaixe):** Percorre a lista e aloca o primeiro buraco que seja grande o suficiente[cite: 6].
2.  [cite_start]**Next Fit (Próximo Encaixe):** Similar ao First Fit, mas continua a busca a partir do último ponto de alocação/desalocação[cite: 11].
3.  [cite_start]**Best Fit (Melhor Encaixe):** Analisa todo o mapa e aloca o menor buraco possível que comporte o processo, minimizando o desperdício imediato[cite: 15].
4.  [cite_start]**Quick Fit (Encaixe Rápido):** Estratégia que visa agilidade na alocação utilizando listas separadas para tamanhos comuns[cite: 23].
5.  [cite_start]**Worst Fit (Pior Encaixe):** Aloca sempre o maior buraco disponível, deixando sobras maiores na memória[cite: 29].

## 📊 Exemplo de Saída Esperada

Para cada operação, o programa exibe o estado atualizado do mapa de bits e o status da ação.

```text
--- Executando First Fit ---
Sorteado: P1 (5 blocos)
Status: Alocado com sucesso.
Mapa: [1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

Sorteado: P2 (4 blocos)
Status: Alocado com sucesso.
Mapa: [1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

Sorteado: P1
Status: Processo já na memória. Desalocando...
Mapa: [0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

Estatísticas Finais:
Fragmentação Externa: X blocos
