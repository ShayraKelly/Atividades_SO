# 📝 Atividade 1 - Simulador de Escalonamento de Processos

Esta branch contém o código-fonte de uma atividade desenvolvida para a disciplina de Sistemas Operacionais. O objetivo é aplicar e visualizar na prática os conceitos de gerenciamento de processos, escalonamento de CPU e mudanças de estado através de uma simulação desenvolvida em Java.

Todo o código relacionado a essa atividade está disponível aqui, de forma isolada das demais, para facilitar a organização e consulta.

📝 Sobre o Projeto
A aplicação simula um pequeno sistema operacional gerenciando 10 processos. O escalonador implementado segue o algoritmo Round-Robin, onde cada processo recebe uma fatia de tempo (quantum) para utilizar a CPU. A simulação também modela eventos como operações de Entrada/Saída (E/S), que levam um processo ao estado de bloqueio, e a consequente troca de contexto, que é um dos conceitos centrais da atividade.

⚙️ Regras de Negócio da Simulação
O ambiente simulado opera sob as seguintes regras:

Processos: Estão em execução 10 processos com PIDs de 0 a 9 e tempos de execução totais distintos.

Escalonador: A política de escalonamento é Round-Robin, com os processos sendo executados em ordem crescente de PID.

Quantum: Foi definido um quantum de 1000 ciclos de CPU para cada vez que um processo entra em execução.

Estados do Processo: Os processos podem transitar entre os estados PRONTO, EXECUTANDO, BLOQUEADO e FINALIZADO.

Operação de E/S: A cada ciclo, um processo em execução tem 1% de chance de solicitar uma operação de E/S, o que o move para o estado BLOQUEADO.

Saída do Bloqueio: Um processo no estado BLOQUEADO tem 30% de chance de voltar para PRONTO quando o escalonador lhe der atenção.

Troca de Contexto: Ocorre em duas situações:

      O quantum do processo termina sem E/S (EXECUTANDO -> PRONTO).
      O processo solicita uma operação de E/S (EXECUTANDO -> BLOQUEADO).

Tabela de Processos (PCB): A cada troca de contexto, os dados de todos os processos são salvos no arquivo TabelaDeProcessos.txt, simulando a atualização da Tabela de Processos do Sistema Operacional.

🗂️ Estrutura de Dados de um Processo

Cada processo na simulação é representado por uma estrutura que contém os seguintes dados:

PID: Identificador de Processo

TP: Tempo de Processamento (total de ciclos já executados)

CP: Contador de Programa (definido como TP + 1)

EP: Estado do Processo (ex: PRONTO)
NES: Número de Vezes que Realizou E/S
N_CPU: Número de Vezes que Usou a CPU
