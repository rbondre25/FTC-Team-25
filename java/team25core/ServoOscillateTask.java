package team25core;

/*
 * FTC Team 5218: izzielau, October 6, 2015
 */

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ServoOscillateTask extends RobotTask {

    protected enum EventKind {
        INITIAL,
        ENDPOINT,
    }

    protected EventKind state;
    protected ElapsedTime timer;

    private int TIME = 1525;

    private int initial;
    private int range;
    private int endpoint;

    private Servo servo;

    public ServoOscillateTask(Robot robot, Servo servo, int startPoint, int range)
    {
        super(robot);
        this.initial = startPoint;
        this.range = range;
        if ((startPoint + range) > 255) {
            this.endpoint = (startPoint - range);
        } else {
            this.endpoint = (startPoint + range);
        }
        this.servo = servo;
    }

    @Override
    public void start() {
        timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    }

    @Override
    public void stop() {

    }

    public void stop(float position) {
        servo.setPosition(position);
        robot.removeTask(this);
    }

    @Override
    public void handleEvent(RobotEvent e) {
        robot.removeTask(this);
    }

    @Override
    public boolean timeslice() {
        if (timer.time() > TIME) {
            if (state == EventKind.INITIAL) {
                state = EventKind.ENDPOINT;
                servo.setPosition(initial);
            } else if (state == EventKind.ENDPOINT) {
                state = EventKind.INITIAL;
                servo.setPosition(endpoint);
            }
            timer.reset();
        }

        robot.telemetry.addData("Position: ", servo.getPosition());
        robot.telemetry.addData("Range: ", range);
        return false;
    }
}