package CurveAnalysis;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Curve extends Pane {


    private ArrayList<Double> values;
    private double xMin;
    private double xMax;
    private double xInc;
    private Axes axes;
    private Path path;
    private String[] behaviour = {"", "", "", "", ""};

    public Curve(
            ArrayList<Double> values,
            double xMin, double xMax, double xInc,
            Axes axes
    ) {
        this.values = values;
        this.xMin = xMin;
        this.xMax = xMax;
        this.xInc = xInc;
        this.axes = axes;
        this.path = new Path();
        draw();
        analyseBehaviour();
    }

    private void draw() {

        path.setStroke(Color.ORANGE.deriveColor(0, 1, 1, 0.6));
        path.setStrokeWidth(2);
        path.setClip(
                new Rectangle(
                        0, 0,
                        axes.getPrefWidth(),
                        axes.getPrefHeight()
                )
        );

        double x = xMin;
        double y = calcYValue(x);

        path.getElements().add(
                new MoveTo(
                        mapX(x, axes), mapY(y, axes)
                )
        );

        x += xInc;
        while (x < xMax) {
            y = calcYValue(x);
            path.getElements().add(
                    new LineTo(
                            mapX(x, axes), mapY(y, axes)
                    )
            );
            x += xInc;
        }

        setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
        setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

        getChildren().setAll(path);
    }

    private double mapX(double x, Axes axes) {
        double tx = axes.getPrefWidth() / 2;
        double sx = axes.getPrefWidth() /
                (axes.getXAxis().getUpperBound() -
                        axes.getXAxis().getLowerBound());
        return x * sx + tx;
    }

    private double mapY(double y, Axes axes) {
        double ty = axes.getPrefHeight() / 2;
        double sy = axes.getPrefHeight() /
                (axes.getYAxis().getUpperBound() -
                        axes.getYAxis().getLowerBound());

        return -y * sy + ty;
    }

    private double calcYValue(double x) {
        double y = 0;
        y = (values.get(4) * Math.pow(x, 4) + values.get(3) * Math.pow(x, 3) * +values.get(2) * Math.pow(x, 2) + values.get(1) * x + values.get(0));

        // System.out.println(y);
        double multiplyer = 1.;

        //alternativ
        for (double value : values) {
            y += value * multiplyer;

            multiplyer *= x;
        }


        //altanativ3
        /*
        y=values.get(4);

        for (int i=3;i>0;i--){

            y*=x;

            y+=values.get(i);
        }
         */

        return y;

    }

    private void analyseBehaviour() {
        System.out.println("ZERO POINT IS: " + getInitZeroPoint(1, 10));
        if (values.get(0) != 0 && values.get(1) != 0 && values.get(2) != 0) {
            behaviour[4] = "das";
            //pqFormel(values);
        }
        if (values.get(0) != 0 && values.get(1) != 0 && values.get(2) == 0 && values.get(3) == 0 && values.get(4) == 0) {
            if (values.get(1) < 0) {
                double x0 = (values.get(0) / (values.get(1))) * -1;
                behaviour[4] = "Nullstelle ist bei " + Math.round(x0 * 100.) / 100.;
            } else if (values.get(1) > 0) {
                double x1 = values.get(0) / (-values.get(1));
                behaviour[4] = "Nullstelle ist bei " + Math.round(x1 * 100.) / 100.;
            }
        }
    }

    private void pqFormel(ArrayList<Double> values) {
        String nullStele = "";
        double wertP, wertQ;

        wertP = values.get(1) / values.get(2);
        wertQ = values.get(0) / values.get(2);

        wertP = Math.round(wertP * 100f) / 100f;
        wertQ = Math.round(wertQ * 100f) / 100f;


        if ((Math.pow((wertP / 2), 2) - (wertQ)) > 0) {
            double x = -(wertP / 2) + Math.sqrt(Math.pow((wertP / 2), 2) - (wertQ));
            double x1 = -(wertP / 2) - Math.sqrt(Math.pow((wertP / 2), 2) - (wertQ));
            nullStele = "bei x1 :\t" + Math.round(x * 100f) / 100f + "\nbei x2 :\t" + Math.round(x1 * 100f) / 100f;
        } else {
            nullStele = "" + (-wertP / 2);
        }


        if (nullStele.contains("NaN")) {
            behaviour[4] = "keine null stelle : " + nullStele;
            behaviour[4] = "Hat Null Stelle bei :" + nullStele + "und bei :";
        } else {

        }
        behaviour[4] = "Hat Null Stelle bei :" + nullStele;
    }

    public Double getInitZeroPoint(double steps, int laps) {
        if (steps == 0 || laps == 0) return null;
        double result;
        double negativeResult;
        double predictedZeroPoint = steps;
        int lapsFinished = 0;
        do {
            result = 0;
            negativeResult = 0;
            for (int i = 0; i < values.size(); i++) {
                if (i > 0) {
                    result += values.get(i) * Math.pow(predictedZeroPoint, i); //store a positive value
                    negativeResult += values.get(i) * Math.pow(-predictedZeroPoint, i); //store a negative value
                } else {
                    result += values.get(i);
                    negativeResult += values.get(i);
                }
                System.out.println("RESULT AT " + predictedZeroPoint + " IS: NR: " + negativeResult + " AND PR: " + result);
            }
            System.out.println("NEGATIVE RESULT: " + negativeResult + "\nPOSITIVE RESULT: " + result);

            if (negativeResult != 0 && negativeResult != 0)
                predictedZeroPoint += steps; //add original steps value to predictedZeroPoint
            System.out.println("PREDICTED ZERO POINT: " + predictedZeroPoint);
            lapsFinished++;
        } while (result != 0 && negativeResult != 0 && lapsFinished != laps);
        if (result != 0 && negativeResult != 0) return null;
        return result != 0 ? predictedZeroPoint : -predictedZeroPoint;
    }

    public String getBehaviour(int index) {
        if (index >= 0 && index <= 4) {
            return behaviour[index];
        } else {
            return "none";
        }
    }

   /* public void setFunction(ArrayList<Double> values){
        this.values = values;
        draw();
    }*/
}
