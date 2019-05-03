package Assignment1.map.shapes;
import javafx.scene.Node;

import java.util.Arrays;

public class Path extends PolyShapeSkeleton2{

    public Path() {
        super(2);
    }

    @Override
    public void translate(double dx, double dy) {
        locks.forEach(movable -> movable.translate(dx, dy));
    }

    public void addLockPSS(Movable movable) {
        if (!(movable instanceof PolyShapeSkeleton2)) {
            return;
        }
        locks.add(movable);
    }

    public void links() {
        locks.stream()
                .map(PolyShapeSkeleton2.class::cast)
                .forEach((PolyShapeSkeleton2 shapeSkeleton2) -> {
                    Arrays.stream(getControlPoints()).map(node -> (ControlPointSkeleton) node).filter(c -> shapeSkeleton2.contains(c.getCenterX(), c.getCenterY())).forEachOrdered(shapeSkeleton2::addLock);
                });
    }



}
