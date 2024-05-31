import Interfaces.ControlInterface;
import Interfaces.NotificationInterface;
import Interfaces.SubmissionInterface;

public class UserInterface implements Runnable, NotificationInterface {

    private final SubmissionInterface submissionInterface;
    private final ControlInterface controlInterface;

    public UserInterface(SubmissionInterface submissionInterface, ControlInterface controlInterface) {
        this.submissionInterface = submissionInterface;
        this.controlInterface = controlInterface;
    }


    @Override
    public void run() {

    }

    @Override
    public void display(String info) {

    }
}
