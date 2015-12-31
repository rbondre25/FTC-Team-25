package opmodes;

/*
 * FTC Team 5218: izzielau, October 6, 2015
 */

import com.qualcomm.hardware.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.DcMotor;

import team25core.DeadmanMotorTask;
import team25core.GamepadTask;
import team25core.MonitorGyroTask;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.Team25DcMotor;
import team25core.TwoMotorDriveTask;

public class TankDriveTeleop extends Robot {

    Configuration config;

    @Override
    public void init()
    {
        config = new Configuration();

        // Gyro.
        config.gyro = (ModernRoboticsI2cGyro)hardwareMap.gyroSensor.get("gyro");
        config.gyro.setHeadingMode(ModernRoboticsI2cGyro.HeadingMode.HEADING_CARDINAL);
        config.gyro.calibrate();

        // Servos.
        config.rightPusher = hardwareMap.servo.get("rightPusher");
        config.leftPusher = hardwareMap.servo.get("leftPusher");
        config.rightBumper = hardwareMap.servo.get("rightBumper");
        config.leftBumper = hardwareMap.servo.get("leftBumper");

        config.rightPusher.setPosition(NeverlandServoConstants.RIGHT_PUSHER_STOWED);
        config.leftPusher.setPosition(NeverlandServoConstants.LEFT_PUSHER_STOWED);
        config.rightBumper.setPosition(NeverlandServoConstants.RIGHT_BUMPER_BLOCK);
        config.leftBumper.setPosition(NeverlandServoConstants.LEFT_BUMPER_BLOCK);

        // Treads.
        config.mc = hardwareMap.dcMotorController.get("motors");

        config.leftTread = new Team25DcMotor(this, config.mc, 1);
        config.rightTread = new Team25DcMotor(this, config.mc, 2);
        config.rightTread.setDirection(DcMotor.Direction.REVERSE);

        // Hook.
        config.hook = hardwareMap.dcMotor.get("hook");
    }

    @Override
    public void handleEvent(RobotEvent e)
    {
        // No-op.
    }

    @Override
    public void start()
    {
        super.start();

        // Display gyro.
        final MonitorGyroTask display = new MonitorGyroTask(this, config.gyro);
        this.addTask(display);

        // Joystick control: controls two motor drive.
        TwoMotorDriveTask drive = new TwoMotorDriveTask(this, config.rightTread, config.leftTread);
        this.addTask(drive);

        // Left bumper: automatic forward.
        DeadmanMotorTask forwardLeft = new DeadmanMotorTask(this, config.leftTread, 0.75, GamepadTask.GamepadNumber.GAMEPAD_1, DeadmanMotorTask.DeadmanButton.LEFT_BUMPER);
        this.addTask(forwardLeft);
        DeadmanMotorTask forwardRight = new DeadmanMotorTask(this, config.rightTread, 0.75, GamepadTask.GamepadNumber.GAMEPAD_1, DeadmanMotorTask.DeadmanButton.LEFT_BUMPER);
        this.addTask(forwardRight);

        // Left trigger: automatic backward.
        DeadmanMotorTask backwardLeft = new DeadmanMotorTask(this, config.leftTread, -0.75, GamepadTask.GamepadNumber.GAMEPAD_1, DeadmanMotorTask.DeadmanButton.LEFT_TRIGGER);
        this.addTask(backwardLeft);
        DeadmanMotorTask backwardRight = new DeadmanMotorTask(this, config.rightTread, -0.75, GamepadTask.GamepadNumber.GAMEPAD_1, DeadmanMotorTask.DeadmanButton.LEFT_TRIGGER);
        this.addTask(backwardRight);

        // Right bumper: extends hook.
        DeadmanMotorTask hookExtend = new DeadmanMotorTask(this, config.hook, 0.75, GamepadTask.GamepadNumber.GAMEPAD_2, DeadmanMotorTask.DeadmanButton.RIGHT_BUMPER);
        this.addTask(hookExtend);

        // Right trigger: retracts hook.
        DeadmanMotorTask hookRetract = new DeadmanMotorTask(this, config.hook, -0.75, GamepadTask.GamepadNumber.GAMEPAD_2, DeadmanMotorTask.DeadmanButton.RIGHT_TRIGGER);
        this.addTask(hookRetract);

        // Buttons: moves bumpers to position.
        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1) {
            public void handleEvent(RobotEvent e)
            {
                GamepadEvent event = (GamepadEvent) e;

                if (event.kind == EventKind.BUTTON_X_DOWN) {
                    display.reset();
                }
            }
        });

        // Buttons: moves bumpers to position.
        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_2) {
            public void handleEvent(RobotEvent e)
            {
                GamepadEvent event = (GamepadEvent) e;

                if (event.kind == EventKind.BUTTON_X_DOWN) {
                    config.leftBumper.setPosition(NeverlandServoConstants.LEFT_BUMPER_HOOK);
                } else if (event.kind == EventKind.BUTTON_A_DOWN) {
                    config.leftBumper.setPosition(NeverlandServoConstants.LEFT_BUMPER_BLOCK);
                } else if (event.kind == EventKind.LEFT_BUMPER_DOWN) {
                    config.leftBumper.setPosition(NeverlandServoConstants.LEFT_BUMPER_STOWED);
                } else if (event.kind == EventKind.BUTTON_Y_DOWN) {
                    config.rightBumper.setPosition(NeverlandServoConstants.RIGHT_BUMPER_HOOK);
                } else if (event.kind == EventKind.BUTTON_B_DOWN) {
                    config.rightBumper.setPosition(NeverlandServoConstants.RIGHT_BUMPER_BLOCK);
                } else if (event.kind == EventKind.LEFT_TRIGGER_DOWN) {
                    config.rightBumper.setPosition(NeverlandServoConstants.RIGHT_BUMPER_STOWED);
                }
            }
        });
    }
}