package GUI;

import nikorunnerlib.src.Geometry.*;

public class Spline {

    /*
     * Start Pose
     * First Control
     * Second Control
     * End Pose 
     */
    
    public Pose2d startPose;
    public Pose2d endPose;
    public Vector2d firstControl;
    public Vector2d secondControl;

    public Spline(Pose2d startPose, Vector2d firstControl, Vector2d secondControl, Pose2d endPose) {
        this.startPose = startPose;
        this.endPose = endPose;
        this.firstControl = firstControl;
        this.secondControl = secondControl;
    }

    public void setStartPose(Pose2d startPose) {
        this.startPose = startPose;
    }

    public void setFirstControl(Vector2d firstControl) {
        this.firstControl = firstControl;
    }

    public void setSecondControl(Vector2d secondControl) {
        this.secondControl = secondControl;
    }

    public void setEndPose(Pose2d endPose) {
        this.endPose = endPose;
    }


    public Pose2d getStartPose() {
        return startPose;
    }

    public Vector2d getFirstControl() {
        return firstControl;
    }

    public Vector2d getSecondControl() {
        return secondControl;
    }

    public Pose2d getEndPose() {
        return endPose;
    }

    public Point2d getIndexVector(int index) {
        if(index == 0) {
            return startPose.getPoint2d();
        } else if (index == 1) {
            return firstControl.toPoint2d();
        } else if (index == 2) {
            return secondControl.toPoint2d();
        } else if(index == 3) {
            return endPose.getPoint2d();
        }
        return null;
    }

    public Point2d getIndexOutside(int index) {
        if(index == 0) {
            return startPose.getPoint2d();
        } else if (index == 1) {
            return endPose.getPoint2d();
        }

        return null;
    }
}
