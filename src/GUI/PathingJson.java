package GUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import nikorunnerlib.src.Geometry.Pose2d;
import nikorunnerlib.src.Geometry.Vector2d;
import nikorunnerlib.src.Other.Util;

// Something wrong in conversion of path
public class PathingJson {

    @SuppressWarnings("unchecked")
    public static void convertPathToJson(ArrayList<Spline> splines, File targetFile) throws IOException {

        JSONObject jsonFile = new JSONObject();

        JSONArray waypointsArray = new JSONArray();

        for (int i = 0; i < splines.size(); i++) {

            if (i == 0) {
                // Write StartPose w/ Control Vector
                JSONObject starting = new JSONObject();

                JSONObject startingLocation = new JSONObject();
                startingLocation.put("x", splines.get(i).getStartPose().getX());
                startingLocation.put("y", splines.get(i).getStartPose().getY());

                JSONObject startingLocationAndHeading = new JSONObject();

                startingLocationAndHeading.put("location", startingLocation);
                startingLocationAndHeading.put("heading", splines.get(i).getStartPose().getHeading());

                JSONObject startingControl = new JSONObject();

                // controls
                JSONObject controlVector = new JSONObject();
                // direction
                controlVector.put("direction", splines.get(i).getFirstControl().getDirection());
                // magnitude 1 (wont be used)
                controlVector.put("magnitude", splines.get(i).getFirstControl().getMagnitude());

                startingControl.put("control", controlVector);

                starting.put("startPose", startingLocationAndHeading);
                starting.put("control", controlVector);

                jsonFile.put("start", starting);
            }

            if(i != 0) {
                JSONObject waypoint = new JSONObject();

                JSONObject waypointLocation = new JSONObject();
                waypointLocation.put("x", splines.get(i).getStartPose().getX());
                waypointLocation.put("y", splines.get(i).getStartPose().getY());

                JSONObject controlVector = new JSONObject();

                controlVector.put("direction", splines.get(i).getFirstControl().getDirection());
                controlVector.put("firstControl", splines.get(i - 1).getSecondControl().getMagnitude());
                controlVector.put("secondControl", splines.get(i).getFirstControl().getMagnitude());

                waypoint.put("location", waypointLocation);
                waypoint.put("controls", controlVector);

                waypointsArray.add(waypoint);
            }

            if (i == splines.size() - 1) {
                // Write EndPose w/ Control Vector
                JSONObject ending = new JSONObject();

                JSONObject endingLocation = new JSONObject();
                endingLocation.put("x", splines.get(i).getEndPose().getX());
                endingLocation.put("y", splines.get(i).getEndPose().getY());

                JSONObject endingLocationAndHeading = new JSONObject();

                endingLocationAndHeading.put("location", endingLocation);
                endingLocationAndHeading.put("heading", splines.get(i).getEndPose().getHeading());

                JSONObject endingControl = new JSONObject();

                // controls
                JSONObject controlVector = new JSONObject();
                // direction
                controlVector.put("direction", splines.get(i).getSecondControl().getDirection());
                // magnitude 1
                controlVector.put("magnitude", splines.get(i).getSecondControl().getMagnitude());

                endingControl.put("control", controlVector);

                ending.put("endPose", endingLocationAndHeading);
                ending.put("control", controlVector);

                jsonFile.put("end", ending);
            }
        }

        jsonFile.put("waypoints", waypointsArray);

        FileWriter writeFile = new FileWriter(targetFile.getAbsolutePath());
        writeFile.write(jsonFile.toJSONString());
        writeFile.close();
    }

    public static ArrayList<Spline> convertToPath(File targetFile) throws FileNotFoundException, IOException, ParseException {
        ArrayList<Spline> splines = new ArrayList<>();


        JSONParser parser = new JSONParser();

        FileReader readFile = new FileReader(targetFile.getAbsolutePath());

        JSONObject path = (JSONObject) parser.parse(readFile);

        JSONObject start = (JSONObject) path.get("start");
        JSONArray waypoints = (JSONArray) path.get("waypoints");
        JSONObject end = (JSONObject) path.get("end");

        // start
        JSONObject startPose = (JSONObject) start.get("startPose");
        JSONObject startLocation = (JSONObject) startPose.get("location");
        Pose2d startPose2d = new Pose2d((double) startLocation.get("x"), (double) startLocation.get("y"), (double) startPose.get("heading"));
        Vector2d startControlVector2d = new Vector2d((double) ((JSONObject) start.get("control")).get("magnitude"), (double) ((JSONObject) start.get("control")).get("direction"));

        // end
        JSONObject endPose = (JSONObject) end.get("endPose");
        JSONObject endLocation = (JSONObject) endPose.get("location");
        Pose2d endPose2d = new Pose2d((double) endLocation.get("x"), (double) endLocation.get("y"), (double) endPose.get("heading"));
        Vector2d endControlVector2d = new Vector2d((double) ((JSONObject) end.get("control")).get("magnitude"), (double) ((JSONObject) end.get("control")).get("direction"));


        if(!(waypoints.size() == 0)) {
            for (int i = 0; i < waypoints.size(); i++) {
                // can simplify?
                if(i == 0) {
                    JSONObject startSplineEndControl = ((JSONObject) ((JSONObject) waypoints.get(i)).get("controls"));
                    Vector2d startSplineEndControlVector2d = new Vector2d((double) startSplineEndControl.get("firstControl"), Util.getOppositeAngle((double) startSplineEndControl.get("direction")));

                    JSONObject startSplineEndLocation = ((JSONObject) ((JSONObject) waypoints.get(i)).get("location"));
                    Pose2d startSplineEndPose2d = new Pose2d((double) startSplineEndLocation.get("x"), (double) startSplineEndLocation.get("y"), 0);

                    Spline startSpline = new Spline(startPose2d, startControlVector2d, startSplineEndControlVector2d, startSplineEndPose2d);
                    splines.add(startSpline);
                } 
                if (i == waypoints.size() - 1) {
                    JSONObject endSplineStartLocation = ((JSONObject) ((JSONObject)waypoints.get(i)).get("location"));
                    Pose2d endSplineStartPose2d = new Pose2d((double) endSplineStartLocation.get("x"), (double) endSplineStartLocation.get("y"), 0);

                    JSONObject endSplineStartControl = ((JSONObject) ((JSONObject) waypoints.get(i)).get("controls"));
                    Vector2d endSplineStartControlVector2d = new Vector2d((double) endSplineStartControl.get("secondControl"), (double) endSplineStartControl.get("direction"));

                    Spline endSpline = new Spline(endSplineStartPose2d, endSplineStartControlVector2d, endControlVector2d, endPose2d);
                    splines.add(endSpline);
                } else {
                    JSONObject splineStartLocation = ((JSONObject) ((JSONObject) waypoints.get(i)).get("location"));
                    Pose2d splineStartPose2d = new Pose2d((double) splineStartLocation.get("x"), (double) splineStartLocation.get("y"), 0);

                    JSONObject splineStartControl = ((JSONObject) ((JSONObject) waypoints.get(i)).get("controls"));
                    Vector2d splineStartControlVector2d = new Vector2d((double) splineStartControl.get("secondControl"), (double) splineStartControl.get("direction"));


                    JSONObject splineEndControl = ((JSONObject) ((JSONObject) waypoints.get(i + 1)).get("controls"));
                    Vector2d splineEndControlVector2d = new Vector2d((double) splineEndControl.get("firstControl"), Util.getOppositeAngle((double) splineEndControl.get("direction")));

                    JSONObject splineEndLocation = ((JSONObject) ((JSONObject) waypoints.get(i + 1)).get("location"));
                    Pose2d splineEndPose2d = new Pose2d((double) splineEndLocation.get("x"), (double) splineEndLocation.get("y"), 0);

                    Spline spline = new Spline(splineStartPose2d, splineStartControlVector2d, splineEndControlVector2d, splineEndPose2d);
                    splines.add(spline);
                }
            }

        } else {
            Spline spline = new Spline(startPose2d, startControlVector2d, endControlVector2d, endPose2d);
            splines.add(spline);
        }

        return splines;
    }
}