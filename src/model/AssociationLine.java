import java.awt.*;
import java.awt.geom.AffineTransform;

class AssociationLine extends Connection {
    //        繼承設定           //
    //  以拖一起始的port尋找validport來建立line
    AssociationLine(BaseObject start, BaseObject end, Point startPort, Point endPort) {
        super(start, end);
        this.startPort = startPort;
        this.endPort = endPort;
        // 新增startPortOffset  endPortOffset 以便重新繪製可以找到對應的port
        this.startPortOffset = new Point(startPort.x - start.x, startPort.y - start.y);
        this.endPortOffset = new Point(endPort.x - end.x, endPort.y - end.y);
    }

    void draw(Graphics g) {
        // isValid <- from connection line 看是不是合法的port
        if (!isValid) return;
        Graphics2D g2d = (Graphics2D) g;

        // 繪製直線（從 startPort 連到 endPort）
        g2d.setColor(Color.BLACK);
        g2d.drawLine(startPort.x, startPort.y, endPort.x, endPort.y);

        // 計算箭頭方向（startport 朝向 endport）// atan2 計算對應夾角
        double angle = Math.atan2(endPort.y - startPort.y, endPort.x - startPort.x);

        // 避免難以閱讀把他額外做成一個function
        // input : graph , [Point(x,y)]int x, int y, 箭頭地朝向角度 等等可以用在General....line 跟 composition line繪製箭頭應該是一樣的只是形狀不同
        drawArrowHead(g2d, endPort.x, endPort.y, angle);
    }

    private void drawArrowHead(Graphics2D g2d, int x, int y, double angle) {
        // 不知道為什麼沒放這個會出事
        // 直接rotate會發生偏移
        // https://stackoverflow.com/questions/6591832/translate-method-in-graphics
        AffineTransform orgin = g2d.getTransform();
        g2d.translate(x, y);
        // 好像跟重新設置原點有關
        g2d.rotate(angle); // 根據方向旋轉

        //設定跟繪製的中心點x,y左上右上兩點連線
        // .
        //   .
        //    . <- origin
        //   .
        //  .
        int arrowSize = 10; // 箭頭長度
        int arrowWidth = 5; // 箭頭寬度

        g2d.drawLine(0, 0, -arrowSize, -arrowWidth); // 左側
        g2d.drawLine(0, 0, -arrowSize, arrowWidth);  // 右側

        // 需要把坐標系便回去 有點神奇
        g2d.setTransform(orgin);
    }
}
