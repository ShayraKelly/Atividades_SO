import java.util.*;

public class GerenciamentoMemoria {

    static final int tamanho_memoria = 32;
    static final int num_operacoes = 30;
    static int[] memoria = new int[tamanho_memoria];
    static int ultimoIndiceNextFit = 0;

    static class Processo {
        String id;
        int tamanho;
        int inicio = -1;

        Processo(String id, int tamanho) {
            this.id = id;
            this.tamanho = tamanho;
        }
    }

    static Map<String, Processo> processos = new LinkedHashMap<>();

    public static void main(String[] args) {
        inicializarProcessos();
        executarAlgoritmo("First Fit");
        executarAlgoritmo("Next Fit");
        executarAlgoritmo("Best Fit");
        executarAlgoritmo("Quick Fit");
        executarAlgoritmo("Worst Fit");
    }

    static void inicializarProcessos() {
        processos.put("P1", new Processo("P1", 5));
        processos.put("P2", new Processo("P2", 4));
        processos.put("P3", new Processo("P3", 2));
        processos.put("P4", new Processo("P4", 5));
        processos.put("P5", new Processo("P5", 8));
        processos.put("P6", new Processo("P6", 3));
        processos.put("P7", new Processo("P7", 5));
        processos.put("P8", new Processo("P8", 8));
        processos.put("P9", new Processo("P9", 2));
        processos.put("P10", new Processo("P10", 6));
    }

    static void executarAlgoritmo(String nome) {
        Arrays.fill(memoria, 0);
        ultimoIndiceNextFit = 0;
        System.out.println("=== Executando " + nome + " ===");
        Random random = new Random();
        List<Processo> lista = new ArrayList<>(processos.values());

        for (int i = 0; i < num_operacoes; i++) {
            Processo p = lista.get(random.nextInt(lista.size()));
            if (p.inicio == -1) {
                boolean alocou = switch (nome) {
                    case "First Fit" -> firstFit(p);
                    case "Next Fit" -> nextFit(p);
                    case "Best Fit" -> bestFit(p);
                    case "Quick Fit" -> quickFit(p);
                    case "Worst Fit" -> worstFit(p);
                    default -> false;
                };
                System.out.println("\nAlocando " + p.id + " (" + p.tamanho + " blocos): " +
                        (alocou ? "Sucesso" : "Falhou"));
            } else {
                desalocar(p);
                System.out.println("Desalocando " + p.id);
            }
            mostrarMemoria();
        }
    }

    static boolean firstFit(Processo p) {
        for (int i = 0; i <= tamanho_memoria - p.tamanho; i++) {
            if (blocoLivre(i, p.tamanho)) {
                alocar(p, i);
                return true;
            }
        }
        return false;
    }

    static boolean nextFit(Processo p) {
        for (int i = ultimoIndiceNextFit; i < tamanho_memoria; i++) {
            if (blocoLivre(i, p.tamanho)) {
                alocar(p, i);
                ultimoIndiceNextFit = i + p.tamanho;
                return true;
            }
        }
        for (int i = 0; i < ultimoIndiceNextFit; i++) {
            if (blocoLivre(i, p.tamanho)) {
                alocar(p, i);
                ultimoIndiceNextFit = i + p.tamanho;
                return true;
            }
        }
        return false;
    }

    static boolean bestFit(Processo p) {
        int melhorInicio = -1;
        int menorEspaco = Integer.MAX_VALUE;
        for (int i = 0; i < tamanho_memoria; ) {
            if (memoria[i] == 0) {
                int j = i;
                while (j < tamanho_memoria && memoria[j] == 0) j++;
                int espaco = j - i;
                if (espaco >= p.tamanho && espaco < menorEspaco) {
                    menorEspaco = espaco;
                    melhorInicio = i;
                }
                i = j;
            } else i++;
        }
        if (melhorInicio != -1) {
            alocar(p, melhorInicio);
            return true;
        }
        return false;
    }

    static boolean quickFit(Processo p) {
        int[] tamanhos = {2, 3, 4, 5, 6, 8};
        int tamanho_proximo = tamanhos[0];
        for (int t : tamanhos) {
            if (p.tamanho <= t) {
                tamanho_proximo = t;
                break;
            }
        }
        return bestFit(p);
    }

    static boolean worstFit(Processo p) {
        int piorInicio = -1;
        int maiorEspaco = -1;
        for (int i = 0; i < tamanho_memoria; ) {
            if (memoria[i] == 0) {
                int j = i;
                while (j < tamanho_memoria && memoria[j] == 0) j++;
                int espaco = j - i;
                if (espaco >= p.tamanho && espaco > maiorEspaco) {
                    maiorEspaco = espaco;
                    piorInicio = i;
                }
                i = j;
            } else i++;
        }
        if (piorInicio != -1) {
            alocar(p, piorInicio);
            return true;
        }
        return false;
    }

    static boolean blocoLivre(int inicio, int tamanho) {
        for (int i = inicio; i < inicio + tamanho; i++) {
            if (memoria[i] == 1) return false;
        }
        return true;
    }

    static void alocar(Processo p, int inicio) {
        for (int i = inicio; i < inicio + p.tamanho; i++) {
            memoria[i] = 1;
        }
        p.inicio = inicio;
    }

    static void desalocar(Processo p) {
        for (int i = p.inicio; i < p.inicio + p.tamanho; i++) {
            memoria[i] = 0;
        }
        p.inicio = -1;
    }

    static void mostrarMemoria() {
        System.out.println(Arrays.toString(memoria));
    }
}
