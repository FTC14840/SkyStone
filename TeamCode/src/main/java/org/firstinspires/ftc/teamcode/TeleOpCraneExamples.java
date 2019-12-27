package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name ="TeleOpCraneExamples")

public class TeleOpCraneExamples extends LinearOpMode {

    private DcMotor craneLift = null;
    private DcMotor craneExtension = null;

    private Servo hookLeft = null;
    private Servo hookRight = null;
    private Servo armLeft = null;
    private Servo armRight = null;
    private Servo headSwivel = null;
    private Servo headCompressor = null;
    private Servo headRotate = null;


    @Override
    public void runOpMode() {

        //------------------------------------------------------------------------------------------

        craneLift = hardwareMap.dcMotor.get("craneLift");

        //------------------------------------------------------------------------------------------

        craneExtension = hardwareMap.dcMotor.get("craneExtension");
        craneExtension.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        craneExtension.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //------------------------------------------------------------------------------------------

        hookLeft = hardwareMap.servo.get("hookLeft");
        hookRight = hardwareMap.servo.get("hookRight");
        hookLeft.setPosition(.11); // .11 to .59
        hookRight.setPosition(.54); // .54 to .09

        //------------------------------------------------------------------------------------------

        // The set position needs to be tucked then changed to these
        armLeft = hardwareMap.servo.get("armLeft");
        armRight = hardwareMap.servo.get("armRight");

        double armLeftPosition = 1.0;
        armLeft.setPosition(armLeftPosition);
        double armRightPosition = 0.0;
        armRight.setPosition(armRightPosition);

        //------------------------------------------------------------------------------------------

        headSwivel = hardwareMap.servo.get("headSwivel");
        headSwivel.setPosition(.46);

        //------------------------------------------------------------------------------------------

        headCompressor = hardwareMap.servo.get("headCompressor");
        double headCompressorPosition = 0.5;
        headCompressor.setPosition(headCompressorPosition);

        //------------------------------------------------------------------------------------------

        headRotate = hardwareMap.servo.get("headRotate");
        double headRotatePosition = .50;
        headRotate.setPosition(headRotatePosition);

        //------------------------------------------------------------------------------------------

        telemetry.addLine("Ready To Start... Press Play to Begin!");
        telemetry.update();

        waitForStart();
        telemetry.clear();
        telemetry.update();

        while (opModeIsActive()) {

            //--------------------------------------------------------------------------------------

            craneLift.setPower(gamepad2.left_stick_y);

            //--------------------------------------------------------------------------------------

            int craneExtensionMax = -10500;
            int craneExtensionMin = 0;

            if (gamepad2.right_bumper) {

                craneExtension.setTargetPosition(craneExtensionMax);
                craneExtension.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                craneExtension.setPower(1);
                while (craneExtension.isBusy()) {
                    DisplayTelemetry();
                }
                craneExtension.setPower(0);
                craneExtension.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            }

            if (gamepad2.left_bumper) {

                craneExtension.setTargetPosition(craneExtensionMin);
                craneExtension.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                craneExtension.setPower(1);
                while (craneExtension.isBusy()) {
                    DisplayTelemetry();
                }
                craneExtension.setPower(0);
                craneExtension.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }

            // Do not use this in competition, for setup only
            craneExtension.setPower(gamepad1.right_stick_y);
            DisplayTelemetry();

            //--------------------------------------------------------------------------------------

            if (gamepad1.right_bumper) {
                hookLeft.setPosition(.59); // .11 to .59
                hookRight.setPosition(.09); // .54 to .09
            }

            if (gamepad1.left_bumper) {
                hookLeft.setPosition(.11); // .11 to .59
                hookRight.setPosition(.54); // .54 to .09
            }

            //--------------------------------------------------------------------------------------

            if (gamepad2.right_stick_button) {
                armLeftPosition = .47;
                armRightPosition = .50;
            }

            if (gamepad2.left_stick_button) {
                armLeftPosition = 1.0;
                armRightPosition = 0.0;
            }

            armLeft.setPosition(armLeftPosition + (gamepad2.right_stick_y * .50));
            armRight.setPosition(armRightPosition + (-gamepad2.right_stick_y * .50));

            //--------------------------------------------------------------------------------------

            if (gamepad2.dpad_up) {
                headSwivel.setPosition(0.46);
            }

            if (gamepad2.dpad_left) {
                headSwivel.setPosition(.98);
            }

            if (gamepad2.dpad_right) {
                headSwivel.setPosition(0.0);
            }

            //--------------------------------------------------------------------------------------

            if (gamepad2.x) {
                headCompressor.setPosition(headCompressorPosition -= 1.0);
            }

            if (gamepad2.a) {
                headCompressor.setPosition(headCompressorPosition = 0.5);
            }

            if (gamepad2.b) {
                headCompressor.setPosition(headCompressorPosition += 1.0);
            }

            //--------------------------------------------------------------------------------------

            headRotate.setPosition(headRotatePosition + gamepad2.right_stick_x);

            //--------------------------------------------------------------------------------------
        }
    }

    public void DisplayTelemetry () {
        telemetry.addLine()
                .addData("Crane Extension", "%7d" + "   " + "%7d" + "   " + "%.1f",
                        craneExtension.getTargetPosition(), craneExtension.getCurrentPosition(), craneExtension.getPower());
        telemetry.update();
    }
}

//            if (gamepad1.dpad_up) {
//                servoArm.setPosition(0.0);
//            }
//
//            if (gamepad1.left_bumper && servoArmPosition > 0.0) {
//                servoArm.setPosition(servoArmPosition -= .02);
//            }
//
//            if (gamepad1.dpad_down) {
//                servoArm.setPosition(1.0);
//            }
//
//            if (gamepad1.right_bumper && servoArmPosition < 1.0) {
//                servoArm.setPosition(servoArmPosition += .02);
//            }