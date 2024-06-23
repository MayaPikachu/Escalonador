package simulation;

import interfaces.ControlInterface;
import interfaces.NotificationInterface;
import interfaces.SubmissionInterface;

import javax.swing.*;

public class UserInterface implements Runnable, NotificationInterface {

    private final SubmissionInterface submissionInterface;
    private final ControlInterface controlInterface;
    JFrame jframe;

    public UserInterface(SubmissionInterface submissionInterface, ControlInterface controlInterface) {
        this.submissionInterface = submissionInterface;
        this.controlInterface = controlInterface;
        jframe = new JFrame();
    }

    @Override
    public void run() {
        JButton startSimulationBtn = new JButton("Start Simulation");
        JButton suspendSimulationBtn = new JButton("Suspend Simulation");
        JButton resumeSimulationBtn = new JButton("Resume Simulation");
        JButton stopSimulationBtn = new JButton("Stop Simulation");
        JButton displayProcessQueuesBtn = new JButton("Display Process Queues");
        JButton displaySubmissionQueueBtn = new JButton("Display Submission Queue");
        JTextField jobSubmissionField = new JTextField();
        JButton submitJobBtn = new JButton("Submit Job");
        startSimulationBtn.addActionListener(e -> {
            System.out.println("Starting simulation");
            controlInterface.startSimulation();
        });
        suspendSimulationBtn.addActionListener(e -> {
            System.out.println("Suspending simulation");
            controlInterface.suspendSimulation();
        });
        resumeSimulationBtn.addActionListener(e -> {
            System.out.println("Resuming simulation");
            controlInterface.resumeSimulation();
        });
        stopSimulationBtn.addActionListener(e -> {
            System.out.println("Stopping simulation");
            controlInterface.stopSimulation();
        });
        displayProcessQueuesBtn.addActionListener(e -> {
            controlInterface.displayProcessQueues();
        });
        displaySubmissionQueueBtn.addActionListener(e -> {
            submissionInterface.displaySubmissionQueue();
        });
        submitJobBtn.addActionListener(e -> {
            String programName = jobSubmissionField.getText();
            submissionInterface.submitJob(programName);
        });
        startSimulationBtn.setBounds(10, 10, 200, 30);
        suspendSimulationBtn.setBounds(10, 50, 200, 30);
        resumeSimulationBtn.setBounds(10, 90, 200, 30);
        stopSimulationBtn.setBounds(10, 130, 200, 30);
        displayProcessQueuesBtn.setBounds(10, 170, 200, 30);
        displaySubmissionQueueBtn.setBounds(10, 210, 200, 30);
        jobSubmissionField.setBounds(10, 250, 200, 30);
        submitJobBtn.setBounds(10, 290, 200, 30);
        jframe.add(startSimulationBtn);
        jframe.add(suspendSimulationBtn);
        jframe.add(resumeSimulationBtn);
        jframe.add(stopSimulationBtn);
        jframe.add(displayProcessQueuesBtn);
        jframe.add(displaySubmissionQueueBtn);
        jframe.add(jobSubmissionField);
        jframe.add(submitJobBtn);
        jframe.setSize(640, 480);
        jframe.setLayout(null);
        jframe.setResizable(false);
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void display(String info) {
        JOptionPane.showMessageDialog(jframe, info);
    }
}
