package interfaces;

import program.Program;
import simulation.SimulatedProcess;

public interface InterSchedulerInterface {
    void addProcess(SimulatedProcess program);
    int getProcessLoad();
}
