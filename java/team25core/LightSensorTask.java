package team25core;

/*
 * FTC Team 25: izzielau, November 01, 2015
 */

import com.qualcomm.robotcore.hardware.LightSensor;

public class LightSensorTask extends RobotTask {
    LightSensor lightSensor;

    // Enumeration: events.
    public enum EventKind {
        RIGHT_SENSOR_WHITE,
        RIGHT_SENSOR_BLACK,
        LEFT_SENSOR_WHITE,
        LEFT_SENSOR_BLACK,
    }

    // Instance of SwitchState.
    protected LightState lightState;

    // Class: boolean limit states.
    public class LightState {
        public boolean right_unknown;
        public boolean right_white;
        public boolean right_black;
        public boolean left_unknown;
        public boolean left_white;
        public boolean left_black;
    }

    public LightSensorTask(Robot robot, LightSensor robotSensor)
    {
        super(robot);

        this.lightSensor = robotSensor;

        this.lightState = new LightState();
        this.lightState.right_white = false;
        this.lightState.right_black = false;
        this.lightState.left_white  = false;
        this.lightState.left_black  = false;
        this.lightState.left_unknown = true;
        this.lightState.right_unknown = true;
    }

    @Override
    public void start()
    {

    }

    @Override
    public void stop()
    {
        // Remove task.
        robot.removeTask(this);
    }

    @Override
    public boolean timeslice()
    {

        return false;
    }
}
