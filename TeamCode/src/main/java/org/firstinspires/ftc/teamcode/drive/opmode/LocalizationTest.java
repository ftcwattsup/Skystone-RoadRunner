package org.firstinspires.ftc.teamcode.drive.opmode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveBase;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveREV;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveREVOptimized;

/**
 * This is a simple teleop routine for testing localization. Drive the robot around like a normal
 * teleop routine and make sure the robot's estimated pose matches the robot's actual pose (slight
 * errors are not out of the ordinary, especially with sudden drive motions). The goal of this
 * exercise is to ascertain whether the localizer has been configured properly (note: the pure
 * encoder localizer heading may be significantly off if the track width has not been tuned).
 */
@Config
@TeleOp(group = "drive")
public class LocalizationTest extends LinearOpMode {
    public static double VX_WEIGHT = 1;
    public static double VY_WEIGHT = 1;
    public static double OMEGA_WEIGHT = 1;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        hardwareMap.get(DcMotor.class, "lf").setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardwareMap.get(DcMotor.class, "rf").setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardwareMap.get(DcMotor.class, "lb").setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardwareMap.get(DcMotor.class, "rb").setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        SampleMecanumDriveBase drive = new SampleMecanumDriveREVOptimized(hardwareMap);

        waitForStart();

        while (!isStopRequested()) {
            Pose2d baseVel = new Pose2d(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    -gamepad1.right_stick_x
            );

           /* baseVel = new Pose2d(
                    -gamepad1.left_stick_y,
                    0,
                    0
            );*/

            Pose2d vel;

           // if(gamepad1.dpad_left) baseVel = new Pose2d(0, -0.5, 0);
            //if(gamepad1.dpad_right) baseVel = new Pose2d(0, 0.5, 0);
            //if(gamepad1.dpad_up)  baseVel = new Pose2d(0.5, 0, 0);
            //if(gamepad1.dpad_down)  baseVel = new Pose2d(-0.5, 0, 0);

            if (Math.abs(baseVel.getX()) + Math.abs(baseVel.getY()) + Math.abs(baseVel.getHeading()) > 1) {
                // re-normalize the powers according to the weights
                double denom = VX_WEIGHT * Math.abs(baseVel.getX())
                    + VY_WEIGHT * Math.abs(baseVel.getY())
                    + OMEGA_WEIGHT * Math.abs(baseVel.getHeading());
                vel = new Pose2d(
                    VX_WEIGHT * baseVel.getX(),
                    VY_WEIGHT * baseVel.getY(),
                    OMEGA_WEIGHT * baseVel.getHeading()
                ).div(denom);
            } else {
                vel = baseVel;
            }

            drive.setDrivePower(vel);

            drive.update();

            Pose2d poseEstimate = drive.getPoseEstimate();
            telemetry.addData("x", poseEstimate.getX());
            telemetry.addData("y", poseEstimate.getY());
            telemetry.addData("heading", poseEstimate.getHeading());
            telemetry.addData("lf", hardwareMap.get(DcMotor.class, "lf").getCurrentPosition());
            telemetry.addData("lb", hardwareMap.get(DcMotor.class, "lb").getCurrentPosition());
            telemetry.addData("rf", hardwareMap.get(DcMotor.class, "rf").getCurrentPosition());
            telemetry.addData("rb", hardwareMap.get(DcMotor.class, "rb").getCurrentPosition());
            telemetry.addData("ticks per revolution", hardwareMap.get(DcMotor.class, "lf").getMotorType().getTicksPerRev());
            telemetry.addData("max velocity", drive.getWheelVelocities().get(0));


            telemetry.update();
        }
    }
}
