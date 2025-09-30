# 📝 Atividade 2 - Implementação de Algoritmos de Escalonamento de Processos  

Esta branch contém o código-fonte referente à Atividade 2 da disciplina de Sistemas Operacionais.  
O objetivo da atividade é implementar e analisar diferentes algoritmos de escalonamento de processos, compreendendo como cada estratégia impacta o desempenho do sistema e a ordem de execução dos processos.  

Todo o código relacionado a esta atividade está disponível de forma isolada nesta branch, para facilitar a organização e consulta.

O trabalho tem como objetivos principais a implementação dos algoritmos FCFS, SJF, Round Robin, Priority Scheduling, Priority Scheduling com múltiplas filas e Lottery Scheduling em python, além da simulação do comportamento de uma CPU escalonando processos. A atividade também visa exibir resultados como a ordem de execução, o tempo de espera e o tempo de retorno de cada processo, bem como os tempos médios correspondentes, permitindo a análise comparativa entre as diferentes estratégias de escalonamento.

## 📝 Sobre o Projeto  

A entrada consiste em uma lista de processos com os seguintes atributos:  

- ID do processo  
- Tempo de chegada  
- Tempo de execução (burst time)  
- Prioridade  

A saída deve apresentar os resultados de cada algoritmo em um único arquivo de texto, separados por blocos e devidamente identificados.  

## 📂 Estrutura esperada de saída (exemplo)  

```xt
------- FCFS -------
Ordem de Execução: P1 → P2 → P3 → P4
...
Tempo Médio de Espera: X
Tempo Médio de Retorno: Y

------- SJF -------
Ordem de Execução: ...
...
