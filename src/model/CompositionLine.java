import java.awt.*;
import java.awt.geom.AffineTransform;
class CompositionLine extends Connection {

    //default 從association 拔過來用
    public CompositionLine(BaseObject start, BaseObject end, Point startMouse, Point endMouse) {
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

        g2d.drawLine(startPort.x, startPort.y, endPort.x, endPort.y);

        double angle = Math.atan2(endPort.y - startPort.y, endPort.x - startPort.x);

        drawRotatedDiamond(g2d, endPort.x, endPort.y, angle);
    }

    private void drawRotatedDiamond(Graphics2D g2d, int x, int y, double angle) {
        AffineTransform oldTransform = g2d.getTransform();
        g2d.translate(x, y);
        // https://stackoverflow.com/questions/12852076/how-can-i-rotate-an-onscreen-component-in-java

        g2d.rotate(angle + Math.PI/2); // Math.PI/2 剛剛好

        //短軸 x 長軸 x/2
        //origin 左 右 對面
        int size = 6;
        Polygon diamond = new Polygon();
        diamond.addPoint(0, 0);
        diamond.addPoint(-size/2, size);
        diamond.addPoint(0, size*2);
        diamond.addPoint(size/2, size);

        // 填充白色就可以把連接線蓋掉了 我一定是天才
        g2d.setColor(Color.WHITE);
        g2d.fillPolygon(diamond);

        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(diamond);

        g2d.setTransform(oldTransform);
    }
}
