package team25core;

/*
 * FTC Team 25: cmacfarl, December 09, 2015
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.RobotLog;

public class ResetMotorGyroComboTask extends RobotTask {

    public enum EventKind {
        DONE,
    }

    public class ResetMotorGyroComboEvent extends RobotEvent {

        EventKind kind;

        public ResetMotorGyroComboEvent(RobotTask task, EventKind kind)
        {
            super(task);
            this.kind = kind;
        }

        @Override
        public String toString()
        {
            return (super.toString() + "ResetGyroHeading Event " + kind);
        }
    }

    protected GyroSensor gyro;
    protected DcMotor motor;

    protected boolean motorDone;
    protected boolean gyroDone;

    PeriodicTimerTask ptt;

    public ResetMotorGyroComboTask(Robot robot, DcMotor motor, GyroSensor gyro)
    {
        super(robot);

        this.motor = motor;
        this.gyro = gyro;
        this.motorDone = false;
        this.gyroDone = false;
    }

    @Override
    public void start()
    {
        ResetMotorEncoderTask rmt = new ResetMotorEncoderTask(this.robot, motor) {
            @Override
            public void handleEvent(RobotEvent e)
            {
                RobotLog.i("251 Combo encoder reset done");
                motorDone = true;
            }
        };
        this.robot.addTask(rmt);

        ResetGyroHeadingTask rgt = new ResetGyroHeadingTask(this.robot, gyro) {
            @Override
            public void handleEvent(RobotEvent e)
            {
                RobotLog.i("251 Gyro reset done");
                gyroDone = true;
            }
        };
        this.robot.addTask(rgt);
    }

    @Override
    public void stop()
    {
        robot.removeTask(this);
    }

    @Override
    public boolean timeslice()
    {
        if (motorDone && gyroDone) {
            ResetMotorGyroComboEvent done = new ResetMotorGyroComboEvent(this, EventKind.DONE);
            robot.queueEvent(done);
            return true;
        } else {
            return false;
        }
    }
}
