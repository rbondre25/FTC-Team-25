
package test;

/*
 * FTC Team 5218: izzielau, November 02, 2015
 */

import com.qualcomm.hardware.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;

import opmodes.NeverlandServoConstants;
import team25core.DeadReckon;
import team25core.DeadReckonTask;
import team25core.MonitorGyroTask;
import team25core.MonitorMotorTask;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.Team25DcMotor;
import team25core.TwoWheelDirectDriveDeadReckon;
import team25core.TwoWheelGearedDriveDeadReckon;

public class DeadReckonTest extends Robot {

    private final static int TICKS_PER_INCH = 318;
    private final static int LED_CHANNEL = 0;

    private DcMotorController mc;
    private Team25DcMotor leftTread;
    private Team25DcMotor rightTread;
    private DeviceInterfaceModule core;
    private ModernRoboticsI2cGyro gyro;
    private ColorSensor color;
    private Servo leftPusher;
    private Servo rightPusher;
    private Servo leftBumper;
    private Servo rightBumper;

    private TwoWheelGearedDriveDeadReckon deadReckon;
    private TwoWheelGearedDriveDeadReckon deadReckonPush;
    private DeadReckonTask deadReckonPushTask;
    private DeadReckonTask deadReckonTask;
    private MonitorGyroTask monitorGyroTask;

    private MonitorMotorTask monitorMotorTask;

    protected void handleDeadReckonEvent(DeadReckonTask.DeadReckonEvent e)
    {
        switch (e.kind) {
        case SEGMENT_DONE:
            // telemetry.addDataPersist("Segments Completed: ", ++e.segment_num);
            break;
        case PATH_DONE:
        }
    }

    @Override
    public void handleEvent(RobotEvent e)
    {
        if (e instanceof DeadReckonTask.DeadReckonEvent) {
            handleDeadReckonEvent((DeadReckonTask.DeadReckonEvent) e);
        }
    }

    @Override
    public void init()
    {
        // Gyro.
        gyro = (ModernRoboticsI2cGyro)hardwareMap.gyroSensor.get("gyro");
        gyro.setHeadingMode(ModernRoboticsI2cGyro.HeadingMode.HEADING_CARDINAL);
        gyro.calibrate();

        // Color.
        color = hardwareMap.colorSensor.get("color");
        core  = hardwareMap.deviceInterfaceModule.get("interface");

        core.setDigitalChannelMode(LED_CHANNEL, DigitalChannelController.Mode.OUTPUT);
        core.setDigitalChannelState(LED_CHANNEL, false);

        // Servos.
        rightPusher = hardwareMap.servo.get("rightPusher");
        leftPusher  = hardwareMap.servo.get("leftPusher");
        rightBumper = hardwareMap.servo.get("rightBumper");
        leftBumper  = hardwareMap.servo.get("leftBumper");

        rightPusher.setPosition(NeverlandServoConstants.RIGHT_PUSHER_STOWED);
        leftPusher.setPosition(NeverlandServoConstants.LEFT_PUSHER_STOWED);
        rightBumper.setPosition(NeverlandServoConstants.RIGHT_BUMPER_BLOCK);
        leftBumper.setPosition(NeverlandServoConstants.LEFT_BUMPER_BLOCK);

        // Treads.
        mc = hardwareMap.dcMotorController.get("motors");
        leftTread  = new Team25DcMotor(this, mc, 1);
        rightTread = new Team25DcMotor(this, mc, 2);

        rightTread.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        leftTread.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        rightTread.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        leftTread.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        // Class: path.
        deadReckon = new TwoWheelGearedDriveDeadReckon(this, TICKS_PER_INCH, gyro, leftTread, rightTread);
        deadReckon.addSegment(DeadReckon.SegmentType.STRAIGHT, 10, 0.7);
        deadReckon.addSegment(DeadReckon.SegmentType.TURN, 45, 0.7);
        deadReckon.addSegment(DeadReckon.SegmentType.STRAIGHT, 60, 0.7);
    }

    @Override
    public void start()
    {
        gyro.resetZAxisIntegrator();

        monitorMotorTask = new MonitorMotorTask(this, leftTread);
        addTask(monitorMotorTask);

        deadReckonTask = new DeadReckonTask(this, deadReckon);
        addTask(deadReckonTask);

        monitorGyroTask = new MonitorGyroTask(this, gyro);
        addTask(monitorGyroTask);
    }

    @Override
    public void stop()
    {
        if (deadReckonTask != null) {
            deadReckonTask.stop();
        }
    }
}
