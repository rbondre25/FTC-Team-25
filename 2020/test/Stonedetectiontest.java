package test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import team25core.Robot;
import team25core.RobotEvent;

@Autonomous(name = "Stones Detection Test1", group = "Team 25")
public class Stonedetectiontest extends Robot {

    private final static String TAG = "STONE";

    private Telemetry.Item stonePositionTelem;
    private Telemetry.Item stoneTelem;
    private Telemetry.Item stoneConfidenceTelem;
    private double confidence;
    private double left;

    StoneDetectionTaskMargarita mdTask;

    @Override
    public void handleEvent(RobotEvent e)
    {

    }

    @Override
        public void init()
        {
            mdTask = new StoneDetectionTaskMargarita(this, "Webcam1") {
                @Override
                public void handleEvent(RobotEvent e) {
                    StoneDetectionEvent event = (StoneDetectionEvent)e;
                    confidence = event.stones.get(0).getConfidence();
                    left = event.stones.get(0).getLeft();
                    RobotLog.ii(TAG, "Saw: " + event.kind + " Confidence: " + confidence);
                    stonePositionTelem = telemetry.addData("left origin", left);
                    stoneConfidenceTelem = telemetry.addData("confidence", confidence);
                }
            };

            mdTask.init(telemetry, hardwareMap);
            mdTask.setDetectionKind(StoneDetectionTaskMargarita.DetectionKind.EVERYTHING);

        }
    @Override
    public void start()
    {
        addTask(mdTask);
    }
}
