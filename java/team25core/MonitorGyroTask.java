package team25core;

/*
 * FTC Team 25: cmacfarl, September 01, 2015
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.RobotLog;

public class MonitorGyroTask extends RobotTask {

    /*
     * No Events.
     */

    protected Robot robot;
    protected GyroSensor gyro;

    public MonitorGyroTask(Robot robot, GyroSensor gyro)
    {
        super(robot);

        this.gyro = gyro;
        this.robot = robot;
    }

    @Override
    public void start()
    {

    }

    @Override
    public void stop()
    {
        robot.removeTask(this);
    }

    public void reset()
    {
        gyro.resetZAxisIntegrator();
        SingleShotTimerTask delay = new SingleShotTimerTask(robot, 2000) {
            @Override
            public void handleEvent(RobotEvent e)
            {
                // Wait for gyro to reset.
            }
        };
        robot.addTask(delay);
    }

    @Override
    public boolean timeslice()
    {
        robot.telemetry.addData("Gyro Heading: ", gyro.getHeading());
        RobotLog.i("Heading:" + gyro.getHeading());

        return false;
    }
}
