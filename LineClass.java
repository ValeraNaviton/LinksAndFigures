package Assignment1.map;

import javafx.scene.shape.Line;

public class LineClass {



    public class LineGenerator extends javafx.scene.shape.Line {

        public void setCoordinators(double startX, double startY, double endX, double endY) {
            this.setStartX(startX);
            this.setStartY(startY);
            this.setEndX(endX);
            this.setEndY(endY);

        }

        public void setEnd(double endX, double endY) {
            this.setEndX(endX);
            this.setEndY(endY);
        }
    }

}
