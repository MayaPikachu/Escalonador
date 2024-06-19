import interfaces.ControlInterface;
import interfaces.NotificationInterface;
import interfaces.SubmissionInterface;

import javax.swing.*;

public class UserInterface implements Runnable, NotificationInterface {

    private final SubmissionInterface submissionInterface;
    private final ControlInterface controlInterface;

    public UserInterface(SubmissionInterface submissionInterface, ControlInterface controlInterface) {
        this.submissionInterface = submissionInterface;
        this.controlInterface = controlInterface;
    }

    public UserInterface() {
        submissionInterface = null;
        controlInterface = null;
    }


    @Override
    public void run() {
        JFrame jframe = new JFrame();
        JButton startSimulationBtn = new JButton("Start Simulation");
        JButton suspendSimulationBtn = new JButton("Suspend Simulation");
        JButton resumeSimulationBtn = new JButton("Resume Simulation");
        JButton stopSimulationBtn = new JButton("Stop Simulation");
        startSimulationBtn.addActionListener(e -> {
            System.out.println("Starting simulation");
        });
        suspendSimulationBtn.addActionListener(e -> {
            System.out.println("Suspending simulation");
        });
        resumeSimulationBtn.addActionListener(e -> {
            System.out.println("Resuming simulation");
        });
        stopSimulationBtn.addActionListener(e -> {
            System.out.println("Stopping simulation");
        });
        startSimulationBtn.setBounds(10, 10, 100, 30);
        suspendSimulationBtn.setBounds(10, 50, 100, 30);
        resumeSimulationBtn.setBounds(10, 90, 100, 30);
        stopSimulationBtn.setBounds(10, 130, 100, 30);
        jframe.add(startSimulationBtn);
        jframe.add(suspendSimulationBtn);
        jframe.add(resumeSimulationBtn);
        jframe.add(stopSimulationBtn);
        jframe.setSize(640, 480);
        jframe.setLayout(null);
        jframe.setResizable(false);
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void display(String info) {

    }
}
