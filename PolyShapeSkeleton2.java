package Assignment1.map.shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import Assignment1.map.MapAreaSkeleton;
/**
 * <p>
 * this class represents the generic symmetrical shapes. for math behind this
 * class read link below.</br>
 * this class is meant to be used in 3 phases.
 * <ol>
 * <li>create a new PolyShape in {@link MouseEvent#MOUSE_PRESSED} stage then add
 * it to the {@link MapAreaSkeleton#getChildren()}</li>
 * <li>during the {@link MouseEvent#MOUSE_DRAGGED} stage call
 * {@link PolyShapeSkeleton2#reDraw(double, double, double, double, boolean)}</li>
 * <li>finally in {@link MouseEvent#MOUSE_RELEASED} stage call
 * {@link PolyShapeSkeleton2#registerControlPoints()} then add
 * {@link PolyShapeSkeleton2#getControlPoints()} to
 * {@link MapAreaSkeleton#getChildren()}</li>
 * </ol>
 * </p>
 *
 * @see <a href=
 *      "http://dimitroff.bg/generating-vertices-of-regular-n-sided-polygonspolyhedra-and-circlesspheres/">
 *      Generating vertices of regular n-sided polygons/polyhedra and
 *      circles/spheres</a>
 *

 */
public class PolyShapeSkeleton2 extends Polygon implements Movable {

    /**
     * <p>
     * use these static final variables to convert form and to string.</br>
     * allowing changes for key words to be in one place.</br>
     * </p>
     */
    private static final String POINTS_COUNT = "sides";
    private static final String FILL = "fill";
    private static final String ID = "ID";
    private static final String STROKE = "stroke";
    private static final String WIDTH = "strokeWidth";
    private static final String POINTS = "points";
    private static final String LOCKS = "locks";
    private int id;

    private static int idCounter;

    /**
     * <p>
     * store points of {@link Polygon#getPoints()} in this variable.</br>
     * this variable does not to be changed once initialized in constructor hence
     * final.</br>
     * </p>
     */
    private final ObservableList<Double> POLY_POINTS;

    private int sides;
    /**
     * <p>
     * store number of sides this shape has. in reality it is count for number of
     * corners( points).</br>
     * </p>
     */

    /**
     * <p>
     * this angle variable is used to rotate the shape in
     * {@link PolyShapeSkeleton2#reDraw(double, double, double, double, boolean)}.</br>
     * there is no need to save this as class variable if you do not plan on adding
     * rotate functionality. if removed must be passed to
     * {@link PolyShapeSkeleton2#cacluatePoints()} as function variable.</br>
     * </p>
     */
    private double angle;

    /**
     * <p>
     * these variables are used in
     * {@link PolyShapeSkeleton2#reDraw(double, double, double, double, boolean)} to
     * represent radius.</br>
     * there is no need to save these as class variables if you do not plan on
     * adding any functionality depending on them.</br>
     * if removed must be passed to {@link PolyShapeSkeleton2#cacluatePoints()} as
     * function variable.</br>
     * </p>
     */
    private double dx, dy;

    /**
     * <p>
     * these variables are used in
     * {@link PolyShapeSkeleton2#reDraw(double, double, double, double, boolean)} to
     * represent center of shape.</br>
     * there is no need to save these as class variables if you do not plan on
     * adding any functionality depending on them.</br>
     * if removed must be passed to {@link PolyShapeSkeleton2#cacluatePoints()} as
     * function variable.</br>
     * </p>
     */
    private double x1, y1;

    /**
     * <p>
     * this variable is unidolized and filled
     * {@link PolyShapeSkeleton2#registerControlPoints()}.</br>
     * should be returned to parent container to be rendered.</br>
     * </p>
     */
    protected ControlPointSkeleton[] cPoints;
    protected ObservableList<Movable> locks;


    /**
     * <p>
     * create a PolyShape with specific number of sides.</br>
     * Initializes POLY_POINTS and sides.</br>
     * to be called in {@link MouseEvent#MOUSE_PRESSED} stage.</br>
     * </p>
     *
     * @param sides
     *            - number of sides this shape will have
     */
    public PolyShapeSkeleton2(int sides) {
        this.sides = sides;
        POLY_POINTS = getPoints();
        locks = FXCollections.observableArrayList();
    }


    /**
     * <p>
     * create a PolyShape from given list of strings.</br>
     * each row will contain one property and it is separated by spaces.</br>
     * </p>
     *
     * @param list
     *            - list of string representing a PolyShape
     */
    public PolyShapeSkeleton2(List<String> list) {
        POLY_POINTS = getPoints();
        convertFromString( list);
        //shape is complete so registerControlPoints is called in constructor
        registerControlPoints();
    }

    /**
     * <p>
     * default constructor to initialize common variables.</br>
     * </p>
     */

    public ObservableList<Movable> getLocks() {
        return locks;
    }

    /**
     * <p>
     * must be called from
     * {@link PolyShapeSkeleton2#reDraw(double, double, double, double, boolean)} to
     * create points.</br>
     * it assumed that that <code>POLY_POINTS.clear()</code> is called before this
     * method. as it adds not replace.</br>
     * if needed instead of {@link ObservableList#addAll(Object...)},
     * {@link ObservableList#setAll(Object...)} can be used.</br>
     * </p>
     */
    private void calculatePoints() {
        for (int side = 0; side < sides; side++) {
            POLY_POINTS.addAll(point(Math::cos, dx / 2, angle, side, sides) + x1,
                    point(Math::sin, dy / 2, angle, side, sides) + y1);
        }
    }



    public void addLock(Movable m) {
        if (m != null)
            locks.add(m);
    }

    /**
     * <p>
     * calculate the radian angle between two points assuming x1 and y1 is in the
     * center of Cartesian plane.</br>
     * </p>
     *
     * @param x1
     *            - starting x position
     * @param y1
     *            - starting y position
     * @param x2
     *            - ending x position
     * @param y2
     *            - ending y position
     * @return radian angle between two points
     */
    private double radianShift(double x1, double y1, double x2, double y2) {
        return Math.atan2(y2 - y1, x2 - x1);
    }

    /**
     * <p>
     * calculate the x or y of a point given the radius using Parametric Equations.
     * for more info read link below.</br>
     * pass a function reference of {@link Math#cos(double)} or
     * {@link Math#sin(double)} like <code>Math::sin</code>.</br>
     * </p>
     *
     * @see <a href=
     *      "http://doubleroot.in/lessons/circle/parametric-equation/">Parametric
     *      Equation</a>
     * @param operation
     *            - {@link Math#cos(double)} to calculate x,
     *            {@link Math#sin(double)} for y, ex <code>Math::sin</code>.
     * @param radius
     *            - radius of the circle encapsulating the point.
     * @param shift
     *            - amount of angle to shift the shape for rotate feature.
     * @param side
     *            - side counter for which point is being calculated.
     * @param SIDES
     *            - total number of sides in this shape.
     * @return x or y depending on operation variable
     */
    private double point(DoubleUnaryOperator operation, double radius, double shift, double side, final int SIDES) {
        return radius * operation.applyAsDouble(shift + side * 2.0 * Math.PI / SIDES);
    }

    /**
     * <p>
     * after shape is drawn call this method to initialize and set all
     * ControlPoints.</br>
     * to be called in {@link MouseEvent#MOUSE_RELEASED} stage.</br>
     * </p>
     */
    public void registerControlPoints() {
        cPoints = new ControlPointSkeleton[POLY_POINTS.size() / 2];
        for( int i = 0; i < POLY_POINTS.size(); i += 2){
            final int j = i;
            cPoints[i / 2] = new ControlPointSkeleton( POLY_POINTS.get( i), POLY_POINTS.get( i + 1));
            cPoints[i / 2].addChangeListener(
                    ( value, vOld, vNew) -> POLY_POINTS.set( j, vNew.doubleValue()),
                    ( value, vOld, vNew) -> POLY_POINTS.set( j + 1, vNew.doubleValue()));
        }
    }


    /**
     * <p>
     * measure the distance between 2 points.</br>
     * </p>
     *
     * @return the distance between 2 points
     */
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * <p>
     * this method will recalculate the points inside of the shape based in the
     * staring and current position of mouse.</br>
     * {@link PolyShapeSkeleton2#registerControlPoints()} should not be called till
     * this method is not called anymore.</br>
     * to be called in {@link MouseEvent#MOUSE_DRAGGED} stage.</br>
     * </p>
     *
     * @param x1
     *            - starting x position
     * @param y1
     *            - starting y position
     * @param x2
     *            - ending x position
     * @param y2
     *            - ending y position
     * @param symmetrical
     *            - should be set to true by default unless you are creating
     *            features needing false behavior
     */
    public void reDraw(double x1, double y1, double x2, double y2, boolean symmetrical) {
        angle = radianShift(x1, y1, x2, y2);
        // if symmetrical dx and dy are the same
        dx = symmetrical ? distance(x1, y1, x2, y2) : x2 - x1;
        dy = symmetrical ? dx : y2 - y1;
        // shift the point to center of PolyShape
        this.x1 = x1 + (x2 - x1) / 2;
        this.y1 = y1 + (y2 - y1) / 2;
        POLY_POINTS.clear();

        calculatePoints();

//		angle = radianShift(x1,y1,x2,y2);
//		double radius= distance(x1, y1, x2, y2);
//		x1=x1+(x2-x1)/2;
//		y1=y1+(y2-y1)/2;
//		POLY_POINTS.clear();
//		calculatePoints();

    }

    /**
     * <p>
     * convert current object to a string.</br>
     * each property is located in one line separated by {@link System#lineSeparator()}.</br>
     * each line starts with a name of property and its value/s in front of it all separated by space.</br>
     * </p>
     *
     * @return a single string with explained format.
     */
    public String convertToString() {
        String newLine = System.lineSeparator();
        //join every point in POLY_POINTS and add to builder

        return POINTS_COUNT + " " + sides + newLine +
                FILL + " " + colorToString(getFill()) + newLine +
                STROKE + " " + colorToString(getStroke()) + newLine +
                WIDTH + " " + getStrokeWidth() + newLine +
                POINTS + " " + POLY_POINTS.stream().map(e -> Double.toString(e)).collect(Collectors.joining(" "));
    }

    /**
     * <p>
     * convert array of strings to a PolyShape. called from constructor.</br>
     * each property is located in one index of the list.</br>
     * each index starts with a name of property and its value/s in front of it all separated by space.</br>
     * </p>
     *
     * @param list - a list of properties for this shape
     */
    private void convertFromString(List<String> list) {
        list.forEach(line -> {
            String[] tokens = line.split(" ");
            switch (tokens[0]) {
                case ID:
                    id = Integer.valueOf(tokens[1]);
                    break;
                case POINTS_COUNT:
                    sides = Integer.valueOf(tokens[1]);
                    break;
                case FILL:
                    setFill(stringToColor(tokens[1], tokens[2]));
                    break;
                case STROKE:
                    setStroke(stringToColor(tokens[1], tokens[2]));
                    break;
                case WIDTH:
                    setStrokeWidth(Double.valueOf(tokens[1]));
                    break;
                case POINTS:
                    // create a stream of line.split( " ") and skip the first element as it is the
                    // name, add the rest to POLY_POINTS
                    Stream.of(tokens).skip(1).mapToDouble(Double::valueOf).forEach(POLY_POINTS::add);
                case LOCKS:

                    break;
                default:
                    throw new UnsupportedOperationException("\"" + tokens[0] + "\" is not supported");
            }
        });
    }

    /**
     * <p>
     * this method will return current control points used in this shape as and array.</br>
     * to be called in {@link MouseEvent#MOUSE_RELEASED} stage.</br>
     * </p>
     *
     * @return array of ControlPoints returned as Node to easy addition to layout container
     */
    public Node[] getControlPoints(){
        return cPoints;
    }

    /**
     * <p>
     * convert a {@link Paint} to a string in hex format followed by a space and alpha channel.</br>
     * this method just calls {@link PolyShapeSkeleton2#colorToString(Color)}.</br>
     * </p>
     *
     * @param p - paint object to be converted
     * @return string format of {@link Paint} in hex format plus alpha
     */
    private String colorToString(Paint p) {
        return colorToString(Color.class.cast(p));
    }

    @Override
    public int getID() {
        // TODO Auto-generated method stub
        return id;
    }

    /**
     * <p>
     * convert a {@link Color} to a string in hex format followed by a space and alpha channel.</br>
     * </p>
     *
     * @param c - color object to be converted
     * @return string format of {@link Color} in hex format plus alpha
     */
    private String colorToString(Color c) {
        return String.format("#%02X%02X%02X %f",
                (int) (c.getRed() * 100),
                (int) (c.getGreen() * 100),
                (int) (c.getBlue() * 100),
                c.getOpacity());
    }

    @Override
    public void translate(double dx, double dy) {
        Arrays.stream(cPoints).forEachOrdered(c -> c.translate(dx, dy));
        locks.forEach(m -> m.translate(dx, dy));
    }

    /**
     * <p>
     * convert a string and given alpha to a {@link Color} object using
     * {@link Color#web(String, double)}.</br>
     * </p>
     *
     * @param color
     *            - hex value of a color in #ffffff
     * @param alpha
     *            - alpha value of color between 0 and 1
     * @return color object created from input
     */
    private Color stringToColor(String color, String alpha) {
        return Color.web(color, Double.valueOf(alpha));
    }




}


