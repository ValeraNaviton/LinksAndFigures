package Assignment1.map;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import Assignment1.map.shapes.Movable;
import Assignment1.map.shapes.Path;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import Assignment1.map.shapes.PolyShapeSkeleton2;
import Assignment1.map.shapes.ControlPointSkeleton;



/**
 * <p>
 * create this class once. this class will hold all control behavior related to
 * shapes.</br>
 * </p>
 *
 */
public class MapAreaSkeleton extends Pane {

    /**
     * <p>
     * instead of calling getChildren every time you can call directly the reference
     * of it which is initialized in constructor.</br>
     * </p>
     */
    private ObservableList<Node> children;
    private ObservableList<Movable> shapes;

    /**
     * <p>
     * active shape that is currently being manipulated.</br>
     * </p>
     */
    private PolyShapeSkeleton2 activeShape;
    /**
     * <p>
     * last location of the mouse.</br>
     * </p>
     */
    private double startX, startY;
    /**
     * <p>
     * Reference to ToolSate so you don't have to call ToolSate.getState() every
     * time.</br>
     * </p>
     */
    private ToolStateSkeleton tool;

    /**
     * <p>
     * create a new object and register mouse events.</br>
     * </p>
     */
    private SelectionArea sa;
    private Path path;

    public MapAreaSkeleton() {
        super();
        registerMouseEvents();
        sa = new SelectionArea();
        children = this.getChildren();
        tool = ToolStateSkeleton.state();
        shapes = FXCollections.observableArrayList();
    }

    /**
     * <p>
     * helper function to register all helper functions for mouse events.</br>
     * </p>
     */
    private void registerMouseEvents() {
        addEventHandler(MouseEvent.MOUSE_PRESSED, this::pressClick);
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::releaseClick);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::dragClick);
    }

    /**
     * <p>
     * this method is called by the JavaFX event system. should not be called
     * manually.</br>
     * this function will be called when {@link MouseEvent#MOUSE_PRESSED} is
     * triggered.</br>
     * </p>
     *
     * @param e
     *            - {@link MouseEvent} object
     */
    private void pressClick(MouseEvent e) {
        e.consume();
        startX = e.getX();
        startY = e.getY();
        switch (activeTool()) {
            case DOOR:
                break;
            case MOVE:
                if (!children.contains(e.getTarget())) System.out.println("Not Contains");
                else {

                    System.out.println("Contains");
                    activeShape =(PolyShapeSkeleton2) e.getTarget();

                }
                break;
            case PATH:
                Movable movable = (Movable) e.getTarget();
                path = new Path();
                path.addLockPSS(movable);
                children.add(path);
                break;
            case SELECT:
//                shapes.clear();
//                for(int i = 0; i < shapes.size(); i++) {
//                    if(shapes.get(i) instanceof ControlPointSkeleton) {
//                        ((ControlPointSkeleton) shapes.get(i)).setFill(Color.GRAY);
//                    }
//                }
//                shapes.clear();
//                children.remove(sa);
//                sa = new SelectionArea();
                sa.start(startX, startY);
                children.add(sa);
                break;
            case ERASE:
                break;
            case ROOM:
                activeShape = new PolyShapeSkeleton2(tool.getOption());
                children.add(activeShape);
                break;
            default:
                throw new UnsupportedOperationException(
                        "Cursor for Tool \"" + activeTool().name() + "\" is not implemented");
        }
    }

    /**
     * <p>
     * this method is called by the JavaFX event system. should not be called
     * manually.</br>
     * this function will be called when {@link MouseEvent#MOUSE_DRAGGED} is
     * triggered.</br>
     * </p>
     *
     * @param e
     *            - {@link MouseEvent} object
     */
    private void dragClick(MouseEvent e) {
        e.consume();

        switch (activeTool()) {
            case DOOR:
                break;
            case PATH:
                path.reDraw(startX, startY, e.getX(), e.getY(), true);
                path.setStroke(Color.BLACK);
                path.setStrokeWidth(10.0);
                break;
            case ERASE:
                break;
            case SELECT:
                sa.end(e.getX(), e.getY());

                break;
            case MOVE:

                if(!shapes.isEmpty()){
                    shapes.forEach(mv -> mv.translate(e.getX() - startX, e.getY() - startY));

                }else {
                    if ((e.getTarget() instanceof Movable)) {
                        ((Movable) e.getTarget()).translate(e.getX() - startX, e.getY() - startY);
                    }
                }
                //start only needs to be updated for move
                startX = e.getX();
                startY = e.getY();
                break;
            case ROOM:
                activeShape.reDraw(startX, startY, e.getX(), e.getY(), true);
                activeShape.setFill(Color.DARKRED);
                activeShape.setStrokeWidth(3);
                activeShape.setStroke(Color.BLACK);
                break;
            default:
                throw new UnsupportedOperationException("Drag for Tool \"" + activeTool().name() + "\" is not implemneted");
        }
    }

    /**
     * <p>
     * this method is called by the JavaFX event system. should not be called
     * manually.</br>
     * this function will be called when {@link MouseEvent#MOUSE_RELEASED} is
     * triggered.</br>
     * </p>
     *
     * @param e
     *            - {@link MouseEvent} object
     */
    private void releaseClick(MouseEvent e) {
        e.consume();
        switch (activeTool()) {
            case DOOR:
                break;
            case MOVE:
                break;
            case PATH:
                path.registerControlPoints();

                children.addAll(path.getControlPoints());


                children.stream().filter(n -> n instanceof Movable && !(n instanceof Path)
                        && n.contains(e.getX(), e.getY())).forEachOrdered(n -> path.addLockPSS((Movable) n));

//                children.addAll(path.getControlPoints());
//
//                if (e.getTarget() instanceof PolyShapeSkeleton2) {
//                    PolyShapeSkeleton2 pss = ((PolyShapeSkeleton2) e.getTarget());
//                    path.addLock(pss);
//                    pss.addLock((Movable) path.getControlPoints().get(1));
//                }
//                PolyShapeSkeleton2 pss2 = getFirstContain(e.getX(), e.getY());
//                if (pss2 != null) {
//                    path.addLock(pss2);
//                    pss2.addLock((Movable) path.getControlPoints().get(0));
//                }

                path.links();

                break;
            case SELECT:
                sa.containsAny(children, (Node node) ->{
                    if (!(node instanceof Movable)) return;
                    shapes.add((Movable) node);
                    ((ControlPointSkeleton)node).setFill(Color.BLACK);
                    ((ControlPointSkeleton)node).setStroke(Color.RED);
                });
                //children.remove(sa);
                sa.clear();
                break;
            case ERASE:
                EventTarget target = e.getTarget();
                if (!(target instanceof PolyShapeSkeleton2)) {
                } else {
                    children.removeAll(((PolyShapeSkeleton2) target).getControlPoints());
                    children.removeAll((PolyShapeSkeleton2) target);
                }

                break;
            case ROOM:
                activeShape.registerControlPoints();

                children.addAll(activeShape.getControlPoints());
                break;
            default:
                throw new UnsupportedOperationException(
                        "Release for Tool \"" + activeTool().name() + "\" is not implemneted");
        }
        activeShape = null;
    }

//    public PolyShapeSkeleton2 getFirstContain(double x, double y) {
//        for (Node node : children) {
//            if (node instanceof PolyShapeSkeleton2 && node != path) {
//                if (node.getBoundsInLocal().contains(x, y))
//                    return (PolyShapeSkeleton2) node;
//            }
//        }
//        return null;
//    }

    /**
     * <p>
     * helper function that returns the current {@link Tools}.</br>
     * </p>
     *
     * @return current active {@link Tools}
     */
    private Tools activeTool() {
        return tool.getTool();
    }

    /**
     * <p>
     * create a new string that adds all shapes to one string separated by
     * {@link System#lineSeparator()}.</br>
     * </p>
     *
     * @return string containing all shapes.
     */
    public String convertToString() {
        // for each node in children
        return children.stream()
                // filter out any node that is not PolyShape
                .filter(PolyShapeSkeleton2.class::isInstance)
                // cast filtered nodes to PolyShapes
                .map(PolyShapeSkeleton2.class::cast)
                // convert each shape to a string format
                .map(PolyShapeSkeleton2::convertToString)
                // join all string formats together using new line
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * <p>
     * create all shapes that are stored in given map. each key contains one list
     * representing on PolyShape.</br>
     * </p>
     *
     * @param map
     *            - a data set which contains all shapes in this object.
     */
    public void convertFromString(Map<Object, List<String>> map) {
        // for each key inside of map
        map.keySet().stream()
                // create a new PolyShape with given list in map
                .map(k -> new PolyShapeSkeleton2(map.get(k)))
                // for each created PolyShape
                .forEach((PolyShapeSkeleton2 s) -> {
                    children.add(s);
                    children.addAll(s.getControlPoints());
                });

    }

    /**
     * <p>
     * call this function to clear all shapes in {@link MapAreaSkeleton}.</br>
     * </p>
     */
    public void clearMap() {
        children.clear();
    }

}
