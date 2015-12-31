
package test;

/*
 * FTC Team 5218: izzielau, November 02, 2015
 */

import com.qualcomm.hardware.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.Servo;

import opmodes.BeaconArms;
import opmodes.NeverlandServoConstants;
import team25core.ColorSensorTask;
import team25core.DeadReckon;
import team25core.DeadReckonTask;
import team25core.MonitorGyroTask;
import team25core.MonitorMotorTask;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.SingleShotTimerTask;
import team25core.Team25DcMotor;
import team25core.TwoWheelGearedDriveDeadReckon;

public class BeaconArmsTest extends Robot {

    private final static int TICKS_PER_INCH = 200;
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

    public void handleDeadReckonEvent(DeadReckonTask.DeadReckonEvent e)
    {
        switch (e.kind) {
        case SEGMENT_DONE:
            break;
        case PATH_DONE:
            leftPusher.setPosition(NeverlandServoConstants.LEFT_PUSHER_DEPLOYED);
            SingleShotTimerTask sstt = new SingleShotTimerTask(this, 750) {
                @Override
                public void handleEvent(RobotEvent e)
                {
                    handleBeacon();
                }
            };
            this.addTask(sstt);

            SingleShotTimerTask ssttafter = new SingleShotTimerTask(this, 750) {
                @Override
                public void handleEvent(RobotEvent e)
                {
                    // Class: Dead reckon (push).
                    deadReckonPush = new TwoWheelGearedDriveDeadReckon(this.robot, TICKS_PER_INCH, gyro, leftTread, rightTread);
                    deadReckonPush.addSegment(DeadReckon.SegmentType.STRAIGHT, -3, 0.4);
                    deadReckonPush.addSegment(DeadReckon.SegmentType.STRAIGHT, 0, 0);

                    deadReckonPushTask = new DeadReckonTask(this.robot, deadReckonPush);
                    addTask(deadReckonPushTask);
                }
            };
            this.addTask(ssttafter);

            break;
        }
    }

    public void handleBeacon() {
        addTask(new ColorSensorTask(this, color, core, true, true, LED_CHANNEL) {
            public void handleEvent(RobotEvent e)
            {
                final BeaconArms pushers = new BeaconArms(rightPusher, leftPusher, true);
                ColorSensorEvent event = (ColorSensorEvent) e;

                if (event.kind == EventKind.BLUE) {
                    pushers.allStow();
                    SingleShotTimerTask delay = new SingleShotTimerTask(robot, 2000) {
                        @Override
                        public void handleEvent(RobotEvent e)
                        {
                            pushers.colorDeploy();
                        }
                    };
                    addTask(delay);
                } else if (event.kind == EventKind.RED) {
                    pushers.allStow();
                    SingleShotTimerTask delay = new SingleShotTimerTask(robot, 2000) {
                        @Override
                        public void handleEvent(RobotEvent e)
                        {
                            pushers.rightDeploy();
                        }
                    };
                    addTask(delay);
                } else {
                    pushers.allStow();
                }

                deadReckonPushTask = new DeadReckonTask(robot, deadReckonPush);
                addTask(deadReckonPushTask);
            }
        });
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
        core = hardwareMap.deviceInterfaceModule.get("interface");

        core.setDigitalChannelMode(LED_CHANNEL, DigitalChannelController.Mode.OUTPUT);
        core.setDigitalChannelState(LED_CHANNEL, false);

        // Servos.
        rightPusher = hardwareMap.servo.get("rightPusher");
        leftPusher = hardwareMap.servo.get("leftPusher");
        rightBumper = hardwareMap.servo.get("rightBumper");
        leftBumper = hardwareMap.servo.get("leftBumper");

        rightPusher.setPosition(NeverlandServoConstants.RIGHT_PUSHER_STOWED);
        leftPusher.setPosition(NeverlandServoConstants.LEFT_PUSHER_STOWED);
        rightBumper.setPosition(NeverlandServoConstants.RIGHT_BUMPER_BLOCK);
        leftBumper.setPosition(NeverlandServoConstants.LEFT_BUMPER_BLOCK);

        // Treads.
        mc = hardwareMap.dcMotorController.get("motors");

        leftTread = new Team25DcMotor(this, mc, 1);
        rightTread = new Team25DcMotor(this, mc, 2);

        rightTread.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        leftTread.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        rightTread.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        leftTread.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        // Class: Dead reckon.
        deadReckon = new TwoWheelGearedDriveDeadReckon(this, TICKS_PER_INCH, gyro, leftTread, rightTread);
        deadReckon.addSegment(DeadReckon.SegmentType.STRAIGHT, 62, 0.9);
        deadReckon.addSegment(DeadReckon.SegmentType.TURN, -35, 0.7);
        deadReckon.addSegment(DeadReckon.SegmentType.STRAIGHT, 84, 0.9);
        deadReckon.addSegment(DeadReckon.SegmentType.TURN, -45, 0.7);
        deadReckon.addSegment(DeadReckon.SegmentType.STRAIGHT, 17, 0.9);
        deadReckon.addSegment(DeadReckon.SegmentType.STRAIGHT, 0, 0);
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