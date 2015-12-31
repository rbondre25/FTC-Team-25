package test;

/*
 * FTC Team 25: izzielau, November 17, 2015
 */

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.RobotLog;

public class GyroDeltaTest extends OpMode {
    DcMotor leftMotor;
    DcMotor rightMotor;

    GyroSensor gyro;

    private int currentRead;
    private int lastRead;

    @Override
    public void init() {
        rightMotor = hardwareMap.dcMotor.get("motor_2");
        leftMotor  = hardwareMap.dcMotor.get("motor_1");
        leftMotor.setDirection(DcMotor.Direction.REVERSE);

        gyro = hardwareMap.gyroSensor.get("gyro");
        gyro.calibrate();
    }

    @Override
    public void start() {
        gyro.resetZAxisIntegrator();

        leftMotor.setPower(-0.251);
        rightMotor.setPower(0.251);
    }

    @Override
    public void loop() {
        lastRead = -1;
        currentRead = gyro.getHeading();

        int delta;

        if (currentRead < lastRead) {
            delta = 360 - lastRead  + currentRead;
        } else {
            delta = currentRead - lastRead;
        }

        // Current read, last read, delta.
        RobotLog.i(Integer.toString(currentRead) + "," + Integer.toString(lastRead) + "," + Integer.toString(delta));

        lastRead = currentRead;
    }

    @Override
    public void stop() {
        leftMotor.setPower(0.0);
        rightMotor.setPower(0.0);
    }
}
