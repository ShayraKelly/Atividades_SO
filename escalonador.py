import io
import random
from collections import deque

# ------------------------------------
# PARTE 1: ESTRUTURA BASE
# ------------------------------------

class Processo:
    def __init__(self, id, chegada, execucao, prioridade):
        self.id = id
        self.chegada = chegada
        self.execucao = execucao
        self.prioridade = prioridade
        self.tempo_restante = execucao
        self.tempo_espera = 0
        self.tempo_retorno = 0
        self.tempo_conclusao = 0

def imprimir_resultados(nome_algoritmo, processos_finalizados, ordem_execucao):
    processos_finalizados.sort(key=lambda p: p.id)
    
    total_tempo_espera = sum(p.tempo_espera for p in processos_finalizados)
    total_tempo_retorno = sum(p.tempo_retorno for p in processos_finalizados)
    num_processos = len(processos_finalizados)
    
    tempo_medio_espera = total_tempo_espera / num_processos if num_processos > 0 else 0
    tempo_medio_retorno = total_tempo_retorno / num_processos if num_processos > 0 else 0

    output = io.StringIO()
    print(f"Resultados para o algoritmo: {nome_algoritmo}", file=output)
    print("-" * 30, file=output)
    print(f"Ordem de Execução: {' -> '.join(ordem_execucao)}", file=output)
    print("\nMétricas individuais dos processos:", file=output)
    print(f"{'Processo':<10}| {'Tempo de Espera':<18}| {'Tempo de Retorno'}", file=output)
    print("-" * 50, file=output)
    for p in processos_finalizados:
        print(f"{p.id:<10}| {p.tempo_espera:<18}| {p.tempo_retorno}", file=output)
    print("\nMétricas Médias:", file=output)
    print(f"Tempo Médio de Espera: {tempo_medio_espera:.2f}", file=output)
    print(f"Tempo Médio de Retorno: {tempo_medio_retorno:.2f}\n", file=output)
    
    return output.getvalue()

# ----------------------------------------------------
# PARTE 2: ALGORITMO FCFS (First-Come, First-Served)
# ----------------------------------------------------

def simular_fcfs(processos_originais):
    processos = [Processo(p.id, p.chegada, p.execucao, p.prioridade) for p in processos_originais]
    processos.sort(key=lambda p: p.chegada)
    
    tempo_atual = 0
    ordem_execucao = []
    
    for processo in processos:
        if tempo_atual < processo.chegada:
            tempo_atual = processo.chegada
        
        processo.tempo_espera = tempo_atual - processo.chegada
        processo.tempo_conclusao = tempo_atual + processo.execucao
        processo.tempo_retorno = processo.tempo_conclusao - processo.chegada
        tempo_atual = processo.tempo_conclusao
        ordem_execucao.append(processo.id)
        
    return imprimir_resultados("FCFS (First-Come, First-Served)", processos, ordem_execucao)

# ----------------------------------------------------
# PARTE 3: ALGORITMO  SJF (Shortest Job First)
# ----------------------------------------------------

def simular_sjf(processos_originais):
    processos = [Processo(p.id, p.chegada, p.execucao, p.prioridade) for p in processos_originais]
    tempo_atual = 0
    ordem_execucao = []
    processos_finalizados = []
    
    while processos:
        processos_prontos = [p for p in processos if p.chegada <= tempo_atual]
        if not processos_prontos:
            tempo_atual += 1
            continue
            
        processos_prontos.sort(key=lambda p: p.execucao)
        processo_atual = processos_prontos[0]
        
        processo_atual.tempo_espera = tempo_atual - processo_atual.chegada
        processo_atual.tempo_conclusao = tempo_atual + processo_atual.execucao
        processo_atual.tempo_retorno = processo_atual.tempo_conclusao - processo_atual.chegada
        tempo_atual = processo_atual.tempo_conclusao
        ordem_execucao.append(processo_atual.id)
        
        processos.remove(processo_atual)
        processos_finalizados.append(processo_atual)

    return imprimir_resultados("SJF (Shortest Job First)", processos_finalizados, ordem_execucao)

# ----------------------------------------------------
# PARTE 4: ALGORITMO ROUND ROBIN (RR)
# ----------------------------------------------------

def simular_rr(processos_originais, quantum):
    processos = [Processo(p.id, p.chegada, p.execucao, p.prioridade) for p in processos_originais]
    processos.sort(key=lambda p: p.chegada)
    
    tempo_atual = 0
    ordem_execucao = []
    processos_finalizados = []
    fila_prontos = deque()
    indice_proximo_processo = 0
    
    while indice_proximo_processo < len(processos) or fila_prontos:
        while indice_proximo_processo < len(processos) and processos[indice_proximo_processo].chegada <= tempo_atual:
            fila_prontos.append(processos[indice_proximo_processo])
            indice_proximo_processo += 1

        if not fila_prontos:
            tempo_atual = processos[indice_proximo_processo].chegada
            continue

        processo_atual = fila_prontos.popleft()
        
        tempo_execucao_fatia = min(quantum, processo_atual.tempo_restante)
        tempo_atual += tempo_execucao_fatia
        processo_atual.tempo_restante -= tempo_execucao_fatia
        ordem_execucao.append(processo_atual.id)

        while indice_proximo_processo < len(processos) and processos[indice_proximo_processo].chegada <= tempo_atual:
            fila_prontos.append(processos[indice_proximo_processo])
            indice_proximo_processo += 1

        if processo_atual.tempo_restante <= 0:
            processo_atual.tempo_conclusao = tempo_atual
            processo_atual.tempo_retorno = processo_atual.tempo_conclusao - processo_atual.chegada
            processo_atual.tempo_espera = processo_atual.tempo_retorno - processo_atual.execucao
            processos_finalizados.append(processo_atual)
        else:
            fila_prontos.append(processo_atual)
            
    return imprimir_resultados(f"Round Robin (Quantum={quantum})", processos_finalizados, ordem_execucao)

# ---------------------------------------------------------
# PARTE 5: ALGORITMO PRIORITY SCHEDULING
# ---------------------------------------------------------

def simular_prioridade(processos_originais):
    """Simula o escalonamento por prioridade preemptivo."""
    processos = [Processo(p.id, p.chegada, p.execucao, p.prioridade) for p in processos_originais]
    processos_restantes = list(processos)
    processos_finalizados = []
    ordem_execucao = []
    tempo_atual = 0
    
    while processos_restantes:
        processos_prontos = [p for p in processos_restantes if p.chegada <= tempo_atual]
        
        if not processos_prontos:
            tempo_atual = min(p.chegada for p in processos_restantes)
            continue
        
        processo_atual = min(processos_prontos, key=lambda p: p.prioridade)
        
        if not ordem_execucao or ordem_execucao[-1] != processo_atual.id:
            ordem_execucao.append(processo_atual.id)
            
        processo_atual.tempo_restante -= 1
        tempo_atual += 1
        
        if processo_atual.tempo_restante == 0:
            processo_atual.tempo_conclusao = tempo_atual
            processo_atual.tempo_retorno = processo_atual.tempo_conclusao - processo_atual.chegada
            processo_atual.tempo_espera = processo_atual.tempo_retorno - processo_atual.execucao
            processos_finalizados.append(processo_atual)
            processos_restantes.remove(processo_atual)
            
    return imprimir_resultados("Priority Scheduling (Preemptive)", processos_finalizados, ordem_execucao)

# ---------------------------------------------------------
# PARTE 6: ALGORITMO PRIORITY SCHEDULING - MULTIPLE QUEUES
# ---------------------------------------------------------

def simular_multiplas_filas(processos_originais, quantum=4):
    """
    Simula múltiplas filas com prioridade.
    Processos de maior prioridade rodam primeiro.
    Entre processos de mesma prioridade, usa-se Round Robin.
    """
    processos = [Processo(p.id, p.chegada, p.execucao, p.prioridade) for p in processos_originais]
    processos.sort(key=lambda p: p.chegada)
    
    tempo_atual = 0
    ordem_execucao = []
    processos_finalizados = []
    fila_prontos = deque()
    indice_proximo_processo = 0
    
    while indice_proximo_processo < len(processos) or fila_prontos:
        while indice_proximo_processo < len(processos) and processos[indice_proximo_processo].chegada <= tempo_atual:
            fila_prontos.append(processos[indice_proximo_processo])
            indice_proximo_processo += 1

        if not fila_prontos:
            tempo_atual = processos[indice_proximo_processo].chegada
            continue

        fila_prontos = deque(sorted(list(fila_prontos), key=lambda p: p.prioridade))
        
        processo_atual = fila_prontos.popleft()
        
        if not ordem_execucao or ordem_execucao[-1] != processo_atual.id:
            ordem_execucao.append(processo_atual.id)

        tempo_execucao_fatia = min(quantum, processo_atual.tempo_restante)
        
        for _ in range(tempo_execucao_fatia):
            tempo_atual += 1
            processo_atual.tempo_restante -= 1
            while indice_proximo_processo < len(processos) and processos[indice_proximo_processo].chegada <= tempo_atual:
                fila_prontos.append(processos[indice_proximo_processo])
                indice_proximo_processo += 1
            if processo_atual.tempo_restante == 0:
                break

        if processo_atual.tempo_restante <= 0:
            processo_atual.tempo_conclusao = tempo_atual
            processo_atual.tempo_retorno = processo_atual.tempo_conclusao - processo_atual.chegada
            processo_atual.tempo_espera = processo_atual.tempo_retorno - processo_atual.execucao
            processos_finalizados.append(processo_atual)
        else:
            fila_prontos.append(processo_atual)
            
    return imprimir_resultados(f"Priority Scheduling - Multiple Queues (RR com Quantum={quantum})", processos_finalizados, ordem_execucao)

# ---------------------------------------------------------
# PARTE 7: LOTTERY SCHEDULING
# ---------------------------------------------------------

def simular_loteria(processos_originais):
    """Simula o escalonamento por Loteria."""
    processos = [Processo(p.id, p.chegada, p.execucao, p.prioridade) for p in processos_originais]
    processos_finalizados = []
    ordem_execucao = []
    tempo_atual = 0
    num_total_processos = len(processos)

    while len(processos_finalizados) < num_total_processos:
        processos_prontos = [p for p in processos if p.chegada <= tempo_atual and p.tempo_restante > 0]

        if not processos_prontos:
            tempo_atual = min(p.chegada for p in processos if p.tempo_restante > 0)
            continue

        bilhetes = []
        for p in processos_prontos:
            num_bilhetes = max(1, 4 - p.prioridade) 
            bilhetes.extend([p] * num_bilhetes)
        
        if not bilhetes:
            tempo_atual +=1
            continue
            
        processo_vencedor = random.choice(bilhetes)

        if not ordem_execucao or ordem_execucao[-1] != processo_vencedor.id:
            ordem_execucao.append(processo_vencedor.id)

        tempo_atual += 1
        processo_vencedor.tempo_restante -= 1

        if processo_vencedor.tempo_restante == 0:
            processo_vencedor.tempo_conclusao = tempo_atual
            processo_vencedor.tempo_retorno = processo_vencedor.tempo_conclusao - processo_vencedor.chegada
            processo_vencedor.tempo_espera = processo_vencedor.tempo_retorno - processo_vencedor.execucao
            processos_finalizados.append(processo_vencedor)

    return imprimir_resultados("Lottery Scheduling", processos_finalizados, ordem_execucao)

# ------------------------------------
# PARTE 8: FUNÇÃO PRINCIPAL
# ------------------------------------

def main():
    # Dados de entrada da atividade
    lista_processos = [
        Processo(id="P1", chegada=0, execucao=5, prioridade=2),
        Processo(id="P2", chegada=2, execucao=3, prioridade=1),
        Processo(id="P3", chegada=4, execucao=8, prioridade=3),
        Processo(id="P4", chegada=5, execucao=6, prioridade=2),
        Processo(id="P5", chegada=11, execucao=8, prioridade=1)
    ]

    resultados_finais = ""
    separador = "------------------------------------------------------------\n"

    # Execução de todos os 6 algoritmos
    resultados_finais += simular_fcfs(lista_processos)
    resultados_finais += separador
    resultados_finais += simular_sjf(lista_processos)
    resultados_finais += separador
    resultados_finais += simular_rr(lista_processos, quantum=4)
    resultados_finais += separador
    resultados_finais += simular_prioridade(lista_processos)
    resultados_finais += separador
    resultados_finais += simular_multiplas_filas(lista_processos)
    resultados_finais += separador
    resultados_finais += simular_loteria(lista_processos)

    # Salva o arquivo de texto final
    try:
        with open("resultados_escalonamento.txt", "w", encoding="utf-8") as f:
            f.write(resultados_finais)
        print("Arquivo 'resultados_escalonamento.txt' gerado com sucesso!")
    except IOError as e:
        print(f"Erro ao salvar o arquivo: {e}")

if __name__ == "__main__":
    main()