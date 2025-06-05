import java.awt.*;

/// 拔OVAL來用 但是要補充斜角的port
///
class Rect extends BaseObject {
    Rect(int x, int y) { super(x, y); this.label="RECT";}


    void draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(x, y, 50, 30);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, 50, 30);
        drawLabel(g);
    }


    public boolean contains(int x, int y) {
        return new Rectangle(this.x, this.y, 50, 30).contains(x, y);
    }


    public boolean isPortValid(Point port) {
        for (int[] p : PORTS) {
            if (port.equals(new Point(x + p[0], y + p[1]))) {
                return true;
            }
        }
        return false;
    }

    Point getNearestPort(int px, int py) {
        Point nearest = null;
        double minDist = Double.MAX_VALUE;

        for (int[] port : PORTS) {
            int portX = x + port[0];
            int portY = y + port[1];
            double dist = Math.hypot(px - portX, py - portY);
            if (dist < minDist) {
                minDist = dist;
                nearest = new Point(portX, portY);
            }
        }
        return nearest;
    }

    private static final int[][] PORTS = {
        {0,0}, {25,0}, {50,0}, {50,15},
        {50,30}, {25,30}, {0,30}, {0,15}
    };

    public static int[][] getPorts() {
        return PORTS;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 50, 30);
    }
}
