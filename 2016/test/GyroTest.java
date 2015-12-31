
package test;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;

import team25core.GyroTask;
import team25core.Robot;
import team25core.RobotEvent;

/**
 * Created by katie on 11/14/15.
 */
public class GyroTest extends Robot {
    GyroSensor sensor;
    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void handleEvent(RobotEvent e){
        //See below...
    }

    @Override
    public void init(){
        sensor = hardwareMap.gyroSensor.get("gyroSensor");
        sensor.calibrate();

        leftMotor = hardwareMap.dcMotor.get("leftMotor");
        rightMotor = hardwareMap.dcMotor.get("rightMotor");

        leftMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        rightMotor.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

    }

    @Override
    public void start() {
        // Display: gyro.
        GyroTask displayGyro = new GyroTask(this, sensor, 360, true);
        this.addTask(displayGyro);
    }
}
