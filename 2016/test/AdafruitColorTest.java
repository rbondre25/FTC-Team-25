package test;

/*
 * FTC Team 5218: izzielau, November 28, 2015
 */

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;

public class AdafruitColorTest extends OpMode {
    int red;
    int green;
    int blue;

    static final int LED_CHANNEL = 0;

    ColorSensor colorSensor;
    DeviceInterfaceModule coreinterface;

    @Override
    public void init() {
        red = 0;
        green = 0;
        blue = 0;

        colorSensor = hardwareMap.colorSensor.get("color_sensor");
        coreinterface = hardwareMap.deviceInterfaceModule.get("Device Interface Module 1");

        coreinterface.setDigitalChannelMode(LED_CHANNEL, DigitalChannelController.Mode.OUTPUT);
        coreinterface.setDigitalChannelState(LED_CHANNEL, false);
    }

    @Override
    public void loop() {
        red = colorSensor.red();
        green = colorSensor.green();
        blue = colorSensor.blue();

        telemetry.addData("R: ", red);
        telemetry.addData("G: ", green);
        telemetry.addData("B: ", blue);
    }
}
