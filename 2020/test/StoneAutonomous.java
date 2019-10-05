package test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import team25core.DeadReckonPath;
import team25core.DeadReckonTask;
import team25core.FourWheelDirectDrivetrain;
import team25core.MechanumGearedDrivetrain;
import team25core.Robot;
import team25core.RobotEvent;

@Autonomous(name = "Stone Autonomous", group = "Team 25")
public class StoneAutonomous extends Robot {

    private final static String TAG = "STONE";

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor rearLeft;
    private DcMotor rearRight;

    private MechanumGearedDrivetrain drivetrain;

    private Telemetry.Item stonePositionTelem;
    private Telemetry.Item stoneTelem;
    private Telemetry.Item stoneConfidenceTelem;
    private Telemetry.Item stoneType;
    private Telemetry.Item stoneMidpointTlm;
    private Telemetry.Item imageMidpointTlm;
    private Telemetry.Item leftMidpointTlm;


    private double confidence;
    private double left;
    private double type;
    private double stoneMidpoint;
    private double imageMidpoint;
    private double leftMidpoint;
    private double margin;
    private boolean inCenter;


    StoneDetectionTaskMargarita sdTask;

    @Override
    public void handleEvent(RobotEvent e) {
        if (e instanceof DeadReckonTask.DeadReckonEvent) {
            RobotLog.i("Completed path segment %d", ((DeadReckonTask.DeadReckonEvent) e).segment_num);
        }
    }

    public void goPickupSkystone()
    {

    }
    public void setStoneDetection()
    {   //caption - what appears in the screen (left origin)
        // string -- list of characters (unknown)
        stonePositionTelem = telemetry.addData("left origin", "unknown");
        stoneConfidenceTelem = telemetry.addData("confidence"," N/A" );
        stoneType = telemetry.addData("stonetype","nostone");
        imageMidpointTlm = telemetry.addData("Image Mdpt", "unknown");
        stoneMidpointTlm = telemetry.addData("stone mdpt", "unknown");

        sdTask = new StoneDetectionTaskMargarita(this, "Webcam1") {

            //starts when you find a skystone
            @Override
            public void handleEvent(RobotEvent e) {
                StoneDetectionEvent event = (StoneDetectionEvent)e;
                confidence = event.stones.get(0).getConfidence();
                left = event.stones.get(0).getLeft();
                RobotLog.ii(TAG, "Saw: " + event.kind + " Confidence: " + confidence);

                imageMidpoint = event.stones.get(0).getImageWidth() / 2.0;
                stoneMidpoint = (event.stones.get(0).getWidth() / 2.0) + left;

                stonePositionTelem.setValue(left);
                stoneConfidenceTelem.setValue(confidence);
                imageMidpointTlm.setValue(imageMidpoint);
                stoneMidpointTlm.setValue(stoneMidpoint);


                if (event.kind == EventKind.OBJECTS_DETECTED) {
                    if (Math.abs(imageMidpoint - stoneMidpoint) < margin) {
                        inCenter = true;
                        RobotLog.i("506 Found gold");
                        sdTask.stop();
                        drivetrain.stop();
                        goPickupSkystone();

                    }

                }

            }
        };

        sdTask.init(telemetry, hardwareMap);
        //later will find the skystone
        sdTask.setDetectionKind(StoneDetectionTaskMargarita.DetectionKind.EVERYTHING);

    }
    @Override
        public void init()
        {
            frontLeft = hardwareMap.get(DcMotor.class, "rearLeft");
            frontRight = hardwareMap.get(DcMotor.class, "rearRight");
            rearLeft = hardwareMap.get(DcMotor.class, "frontLeft");
            rearRight = hardwareMap.get(DcMotor.class, "frontRight");

            drivetrain = new MechanumGearedDrivetrain(frontRight, rearRight, frontLeft, rearRight);
            drivetrain.encodersOn();
            drivetrain.resetEncoders();

            setStoneDetection();

        }
    @Override

    public void startStrafing()
    {
        //start looking for the Skystone
        addTask(sdTask);
        drivetrain.strafe(SkystoneConstants25.STRAFE_SPEED);
    }
    public void start()
    {
        DeadReckonPath path = new DeadReckonPath();

       /* path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, 1.0);
        path.addSegment(DeadReckonPath.SegmentType.TURN, 90, 1.0);
        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, 1.0);
        path.addSegment(DeadReckonPath.SegmentType.TURN, 90, 1.0);
        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, 1.0);
        path.addSegment(DeadReckonPath.SegmentType.TURN, 90, 1.0);
        path.addSegment(DeadReckonPath.SegmentType.STRAIGHT, 10, 1.0);
        path.addSegment(DeadReckonPath.SegmentType.TURN, 90, 1.0);

        /**
         * Alternatively, this could be an anonymous class declaration that implements
         * handleEvent() for task specific event handlers.
         */
      //  this.addTask(new DeadReckonTask(this, path, drivetrain));

        startStrafing();


    }
}
