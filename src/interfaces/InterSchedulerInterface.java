package interfaces;

import program.Program;

public interface InterSchedulerInterface {
    void addProcess(Program program);
    int getProcessLoad();
}
