/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.kauailabs.NavxMicroNavigationSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IntegratingGyroscope;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

@TeleOp(name ="TeleOpMecanumExample")

// @Disabled

public class TeleOpMecanumExample extends LinearOpMode {

    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;

    private Servo servoArm = null;

    IntegratingGyroscope gyro;
    NavxMicroNavigationSensor navxMicro;

    private static final double ticks = 1440; // AndyMark = 1120, TETRIX = 1440
    private static final double gearReduction = 1.0; // Greater than 1.0; Less than 1.0 if geared up
    private static final double wheelDiameterInches = 4.0;
    private static final double pi = 3.1415;
    private static final double conversionTicksToInches = (ticks * gearReduction) / (pi * wheelDiameterInches);

    @Override
    public void runOpMode()throws InterruptedException {

        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        servoArm = hardwareMap.servo.get ("servoArm");

        navxMicro = hardwareMap.get(NavxMicroNavigationSensor.class, "navx");
        gyro = (IntegratingGyroscope)navxMicro;

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        ResetEncoders();
        DisableEncoders();

        double servoArmPosition = 0;
        servoArm.setPosition(servoArmPosition);

        double speed = 1;
        double direction = 1;
        boolean aButtonPad1 = false;
        boolean bButtonPad1 = false;
        double turnType = 2.0;

        double frontLeftClip;
        double frontRightClip;
        double backLeftClip;
        double backRightClip;

        telemetry.log().add("Gyro Calibrating. Do Not Move!");

        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        while (navxMicro.isCalibrating())  {
            telemetry.addData("Calibrating", "%s", Math.round(timer.seconds())%2==0 ? "|.." : "..|");
            telemetry.update();
            Thread.sleep(50);
        }

        telemetry.log().clear();
        telemetry.log().add("Gyro Calibrated. Press Play to Begin!");

        waitForStart();
        telemetry.clear();

        while (opModeIsActive()) {

            frontLeft.setPower(frontLeftClip = speed * direction *
                    (-gamepad1.left_stick_y + gamepad1.left_stick_x + (gamepad1.right_stick_x * turnType * direction)));
            frontRight.setPower(frontRightClip = speed * direction *
                    (-gamepad1.left_stick_y - gamepad1.left_stick_x - (gamepad1.right_stick_x * turnType * direction)));
            backLeft.setPower(backLeftClip = speed * direction *
                    (-gamepad1.left_stick_y - gamepad1.left_stick_x + (gamepad1.right_stick_x * turnType * direction)));
            backRight.setPower(backRightClip = speed * direction *
                    (-gamepad1.left_stick_y + gamepad1.left_stick_x - (gamepad1.right_stick_x * turnType * direction)));

            frontLeft.setPower(Range.clip(frontLeftClip, -1.0, 1.0));
            frontRight.setPower(Range.clip(frontRightClip, -1.0, 1.0));
            backLeft.setPower(Range.clip(backLeftClip, -1.0, 1.0));
            backRight.setPower(Range.clip(backRightClip, -1.0, 1.0));

            if (gamepad1.a) {
                aButtonPad1 = true;
            } else if (aButtonPad1) {
                aButtonPad1 = false;
                if (speed == 0.5) {
                    speed = 1;
                } else {
                    speed = 0.5;
                }
            }

            if (gamepad1.b) {
                bButtonPad1 = true;
            } else if (bButtonPad1) {
                bButtonPad1 = false;
                if (direction == -1) {
                    direction = 1;
                } else {
                    direction = -1;
                }
            }

            if (gamepad1.y) {
                ResetEncoders();
                DisableEncoders();
            }

            if (gamepad1.dpad_up) {
                servoArm.setPosition(0.0);
            }

            if (gamepad1.left_bumper && servoArmPosition > 0.0) {
                servoArm.setPosition(servoArmPosition -= .02);
            }

            if (gamepad1.dpad_down) {
                servoArm.setPosition(1.0);
            }

            if (gamepad1.right_bumper && servoArmPosition < 1.0) {
                servoArm.setPosition(servoArmPosition += .02);
            }

            double gyroHeading = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
            double invertGyroHeading = gyroHeading * -1;
            double frontLeftInches = frontLeft.getCurrentPosition() / conversionTicksToInches;
            double frontRightInches = frontRight.getCurrentPosition() / conversionTicksToInches;
            double backLeftInches = backLeft.getCurrentPosition() / conversionTicksToInches;
            double backRightInches = backRight.getCurrentPosition() / conversionTicksToInches;

            telemetry.addLine()
                    .addData("Heading: ", "%.1f", invertGyroHeading);
            telemetry.addLine()
                    .addData("Front Left Inches ",(int)frontLeftInches + "   Power: " + "%.1f", frontLeft.getPower());
            telemetry.addLine()
                    .addData("Front Right Inches: ",(int)frontRightInches + "   Power: " + "%.1f", frontRight.getPower());
            telemetry.addLine()
                    .addData("Back Left Inches: ",(int)backLeftInches + "   Power: " + "%.1f", backLeft.getPower());
            telemetry.addLine()
                    .addData("Back Right Inches: ",(int)backRightInches + "   Power: " + "%.1f", backRight.getPower());
            telemetry.update();
        }
    }

    private void ResetEncoders() {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void DisableEncoders() {
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}