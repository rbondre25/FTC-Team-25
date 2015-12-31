package opmodes;

/*
 * FTC Team 5218: izzielau, December 27, 2015
 */

import com.qualcomm.hardware.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.Servo;

import team25core.DeadReckonTask;
import team25core.MonitorGyroTask;
import team25core.MonitorMotorTask;
import team25core.Team25DcMotor;
import team25core.TwoWheelGearedDriveDeadReckon;

public class Configuration {
    public final static int TICKS_PER_INCH = 200;
    public final static int LED_CHANNEL = 0;

    public DcMotorController mc;
    public Team25DcMotor leftTread;
    public Team25DcMotor rightTread;
    public DcMotor hook;
    public DeviceInterfaceModule core;
    public ModernRoboticsI2cGyro gyro;
    public ColorSensor color;
    public Servo leftPusher;
    public Servo rightPusher;
    public Servo leftBumper;
    public Servo rightBumper;

    public TwoWheelGearedDriveDeadReckon deadReckon;
    public TwoWheelGearedDriveDeadReckon deadReckonPush;
    public DeadReckonTask deadReckonPushTask;
    public DeadReckonTask deadReckonTask;

    public MonitorGyroTask monitorGyroTask;
    public MonitorMotorTask monitorMotorTask;

    public boolean zeroGyro = false;

}
