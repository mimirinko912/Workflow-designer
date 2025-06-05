import java.awt.*;
/// anal 只有上下左右四個port
/// port position
/// validate
class Oval extends BaseObject {
    Oval(int x, int y) { super(x, y); this.label="OVAL";}


    void draw(Graphics g) {
        g.setColor(Color.GRAY); // 設定填充灰色
        g.fillOval(x, y, 50, 30); // 填充橢圓形
        g.setColor(Color.BLACK);
        g.drawOval(x, y, 50, 30);
        drawLabel(g);
    }

    //看mouse是不是在這個obj裡面ㄉ
    public boolean contains(int x, int y) {
        double dx = (x - (this.x+25)) / 25.0;
        double dy = (y - (this.y+15)) / 15.0;
        return dx*dx + dy*dy <= 1;
    }

    //check是不是 p 一個可以用的port
    public boolean isPortValid(Point port) {
        for (int[] p : PORTS) {
            // 看我mouse最後停的地方是不是在port
            if (port.equals(new Point(x + p[0], y + p[1]))) {
                return true;
            }
        }
        return false;
    }

    // euclid nearest distance
    Point getNearestPort(int px, int py) {
        Point nearest = new Point(x + 25, y);
        double minDist = Double.MAX_VALUE;

        for (int[] port : PORTS) {
            int portX = x + port[0];
            int portY = y + port[1];
            double dist = Math.hypot(px - portX, py - portY);
            if (dist < minDist) {
                minDist = dist;
                nearest.setLocation(portX, portY);
            }
        }
        return nearest;
    }

    //固定port 點
    private static final int[][] PORTS = {
        {25,0}, {50,15}, {25,30}, {0,15}
    };
    //return ports for connectionline drawwwwwwww 草すぎます
    public static int[][] getPorts() {
        return PORTS;
    }
    //圖形的bound for selection check if all in the selection box desu
    public Rectangle getBounds() {
        return new Rectangle(x, y, 50, 30);
    }

}
