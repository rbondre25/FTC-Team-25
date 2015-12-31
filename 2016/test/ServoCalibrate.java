package test;

/*
 * FTC Team 5218: izzielau, December 11, 2015
 */

import com.qualcomm.robotcore.hardware.Servo;

import opmodes.NeverlandServoConstants;
import team25core.RobotEvent;
import team25core.ServoCalibrateTask;

public class ServoCalibrate extends team25core.Robot {
    Servo servo;

    @Override
    public void handleEvent(RobotEvent e)
    {
        // None.
    }

    @Override
    public void init() {
        servo = hardwareMap.servo.get("servo");
        servo.getController().pwmEnable();
    }

    @Override
    public void start() {
        ServoCalibrateTask servoTask = new ServoCalibrateTask(this, servo);
        addTask(servoTask);
    }

    @Override
    public void stop() {

    }
}