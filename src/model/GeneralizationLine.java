import java.awt.*;
import java.awt.geom.AffineTransform;

class GeneralizationLine extends Connection {
    public GeneralizationLine(BaseObject start, BaseObject end, Point startMouse, Point endMouse) {
        super(start, end);

        // 設定 startPort 和 endPort，選擇最近的 port
        this.startPort = start.getNearestPort(startMouse.x, startMouse.y);
        this.endPort = end.getNearestPort(endMouse.x, endMouse.y);

        this.startPortOffset = new Point(startPort.x - start.x, startPort.y - start.y);
        this.endPortOffset = new Point(endPort.x - end.x, endPort.y - end.y);
    }

    void draw(Graphics g) {
        if (!isValid) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);

        int x1 = startPort.x, y1 = startPort.y;
        int x2 = endPort.x, y2 = endPort.y;

        int halfx = (x1 + x2) / 2;

        // 第一線段
        g2d.drawLine(x1, y1, halfx, y1);

        // 第二線段
        g2d.drawLine(halfx, y1, halfx, y2);

        // 第三線段
        g2d.drawLine(halfx, y2, x2, y2);

        // 繪製末端箭頭
        drawArrow(g2d, x2, y2, Math.atan2(y2 - y2, x2 - halfx));
    }

    private void drawArrow(Graphics2D g2d, int x, int y, double angle) {
        AffineTransform oldTransform = g2d.getTransform();
        g2d.setColor(Color.WHITE);
        g2d.translate(x, y);
        g2d.rotate(angle - Math.PI/2);

        Polygon arrow = new Polygon();
        int size = 5;
        arrow.addPoint(0, 0);
        arrow.addPoint(-size, -size);
        arrow.addPoint(size, -size);

        g2d.fillPolygon(arrow);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(arrow);
        g2d.setTransform(oldTransform);
    }
}
