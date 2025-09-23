import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SimuladorSO {
    private static final int QUANTUM = 1000;
    private static final String PROCESS_TABLE_FILE = "process_table.txt"; // A ordem dos dados do arquivo é, respectivamente: PID,TP,CP,ESTADO,NES,N_CPU,TempoAlvo
    private static final Random rand = new Random();

    enum State { PRONTO, EXECUTANDO, BLOQUEADO, TERMINADO }

    static class PCB {
        int pid;
        int tempoTotalAlvo;
        int TP;
        int CP;
        State estado;
        int NES;
        int N_CPU;

        PCB(int pid, int tempoTotalAlvo) {
            this.pid = pid;
            this.tempoTotalAlvo = tempoTotalAlvo;
            this.TP = 0;
            this.CP = TP + 1;
            this.estado = State.PRONTO;
            this.NES = 0;
            this.N_CPU = 0;
        }

        void updateCP() {
            this.CP = this.TP + 1;
        }

        boolean isFinished() {
            return this.TP >= this.tempoTotalAlvo;
        }

        String toCSV() {
            return String.format("%d,%d,%d,%s,%d,%d,%d",
                    pid, TP, CP, estado.name(), NES, N_CPU, tempoTotalAlvo);
        }

        static PCB fromCSV(String line) {
            try {
                String[] parts = line.split(",");
                if (parts.length < 7) return null;
                int pid = Integer.parseInt(parts[0]);
                int TP = Integer.parseInt(parts[1]);
                int CP = Integer.parseInt(parts[2]);
                State estado = State.valueOf(parts[3]);
                int NES = Integer.parseInt(parts[4]);
                int N_CPU = Integer.parseInt(parts[5]);
                int tempoAlvo = Integer.parseInt(parts[6]);

                PCB p = new PCB(pid, tempoAlvo);
                p.TP = TP;
                p.CP = CP;
                p.estado = estado;
                p.NES = NES;
                p.N_CPU = N_CPU;
                return p;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return String.format("PID=%d TP=%d CP=%d ESTADO=%s NES=%d N_CPU=%d TempoAlvo=%d",
                    pid, TP, CP, estado, NES, N_CPU, tempoTotalAlvo);
        }
    }

    private static void saveProcessTable(List<PCB> allProcesses) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(PROCESS_TABLE_FILE))) {
            for (PCB p : allProcesses) {
                writer.write(p.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar tabela de processos: " + e.getMessage());
        }
    }

    private static boolean restoreProcessFromFile(PCB proc) {
        Path path = Paths.get(PROCESS_TABLE_FILE);
        if (!Files.exists(path)) return false;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                PCB p = PCB.fromCSV(line);
                if (p != null && p.pid == proc.pid) {
                    proc.TP = p.TP;
                    proc.CP = p.CP;
                    proc.estado = p.estado;
                    proc.NES = p.NES;
                    proc.N_CPU = p.N_CPU;
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao restaurar processo do arquivo: " + e.getMessage());
        }
        return false;
    }

    private static void printContextSwitch(PCB p, State from, State to) {
        System.out.printf("(%d) %s >>> %s%n", p.pid, from.name(), to.name());
    }

    public static void main(String[] args) {
        int[] tempos = {10000, 5000, 7000, 3000, 3000, 8000, 2000, 5000, 4000, 10000};

        List<PCB> allProcesses = new ArrayList<>();
        for (int i = 0; i < tempos.length; i++) {
            allProcesses.add(new PCB(i, tempos[i]));
        }

        Queue<PCB> pronto = new ArrayDeque<>();
        List<PCB> bloqueado = new ArrayList<>();

        for (PCB p : allProcesses) pronto.add(p);

        saveProcessTable(allProcesses);

        System.out.println("=== Iniciando simulacao do SO ===");

        while (true) {
            if (pronto.isEmpty()) {
                Iterator<PCB> it = bloqueado.iterator();
                while (it.hasNext()) {
                    PCB b = it.next();
                    double chance = rand.nextDouble();
                    if (chance < 0.30) {
                        b.estado = State.PRONTO;
                        b.updateCP();
                        pronto.add(b);
                        it.remove();
                        System.out.printf("(%d) BLOQUEADO >>> PRONTO (chance=%.2f)%n", b.pid, chance);
                    }
                }
            }

            if (pronto.isEmpty()) {
                boolean allTerminated = true;
                for (PCB p : allProcesses) if (p.estado != State.TERMINADO) { allTerminated = false; break;}
                if (allTerminated) break;
                Iterator<PCB> it2 = bloqueado.iterator();
                while (it2.hasNext()) {
                    PCB b = it2.next();
                    double chance = rand.nextDouble();
                    if (chance < 0.30) {
                        b.estado = State.PRONTO;
                        b.updateCP();
                        pronto.add(b);
                        it2.remove();
                        System.out.printf("(%d) BLOQUEADO >>> PRONTO (chance=%.2f)%n", b.pid, chance);
                    }
                }
                if (pronto.isEmpty()) {
                    System.out.println("Nenhum processo PRONTO e nenhum terminou. Forçando tentativa de desbloqueio extra...");
                }
            }

            PCB current = pronto.poll();
            if (current == null) continue;

            boolean restored = restoreProcessFromFile(current);
            current.estado = State.EXECUTANDO;
            current.N_CPU++; // usa a CPU agora
            System.out.printf(">>> (PID %d) PRONTO >>> EXECUTANDO (restored=%b)%n", current.pid, restored);

            int ciclosExecutadosNoBurst = 0;
            boolean wentToBlocked = false;
            while (ciclosExecutadosNoBurst < QUANTUM) {
                current.TP++;
                ciclosExecutadosNoBurst++;
                current.updateCP();

                if (current.isFinished()) {
                    current.estado = State.TERMINADO;
                    System.out.println("---- Processo TERMINADO ----");
                    System.out.println(current);
                    saveProcessTable(allProcesses);
                    break;
                }

                double chanceIO = rand.nextDouble();
                if (chanceIO < 0.01) {
                    current.NES++;
                    State from = current.estado;
                    current.estado = State.BLOQUEADO;
                    bloqueado.add(current);
                    wentToBlocked = true;
                    printContextSwitch(current, from, State.BLOQUEADO);
                    saveProcessTable(allProcesses);
                    break;
                }
            }

            if (current.estado == State.TERMINADO) {
                continue;
            }

            if (wentToBlocked) {
                continue;
            }

            State from = current.estado;
            current.estado = State.PRONTO;
            current.updateCP();
            pronto.add(current);
            printContextSwitch(current, from, State.PRONTO);
            saveProcessTable(allProcesses);

            Iterator<PCB> it = bloqueado.iterator();
            while (it.hasNext()) {
                PCB b = it.next();
                double chance = rand.nextDouble();
                if (chance < 0.30) {
                    b.estado = State.PRONTO;
                    b.updateCP();
                    pronto.add(b);
                    it.remove();
                    System.out.printf("(%d) BLOQUEADO >>> PRONTO (chance=%.2f)%n", b.pid, chance);
                }
            }
        }

        System.out.println("=== Simulação finalizada ===");
        System.out.println("Tabela final de processos gravada em: " + PROCESS_TABLE_FILE);
    }
}