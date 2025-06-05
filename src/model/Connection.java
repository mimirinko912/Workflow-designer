import java.awt.*;
//Connection -ass
//             -- compo
//          --GEnG
//  找連接port 不同形狀
/// 繪製直線
/// //      check port vaild
/// /   移動後要重新繪製
/// /
abstract class Connection {
    BaseObject start, end;
    Point startPort, endPort; // 記錄連接port
    protected BaseObject startObj, endObj;
    protected Point startPoint, endPoint;
    protected boolean isValid = true;

    // 處理的是 計算mouse相對移動座標來計算 port
    public Point startPortOffset; // 連接起始點的相對偏移量
    public Point endPortOffset;   // 連接終點的相對偏移量

    public BaseObject getStartObject() {
        return startObj;
    }

    public BaseObject getEndObject() {
        return endObj;
    }

    Connection(BaseObject start, BaseObject end) {
        this.start = start;
        this.end = end;
        // 從中心點找
        this.startPort = start.getNearestPort(end.x + 25, end.y + 15);
        this.endPort = end.getNearestPort(start.x + 25, start.y + 15);

        // 記錄 port 相對於物件的偏移量
        if (startPort != null) {
            startPortOffset = new Point(startPort.x - start.x, startPort.y - start.y);
        }
        if (endPort != null) {
            endPortOffset = new Point(endPort.x - end.x, endPort.y - end.y);
        }
        //check port是不是basic obf
        validatePorts();
    }

    private void validatePorts() {

        //是不是可以ㄉ
        if (!start.isPortValid(startPort) || !end.isPortValid(endPort)) {
            isValid = false;
        }

        //要在墨點是在obj上面才能建立
        if (end.contains(startPort) || start.contains(endPort)) {
            isValid = false;
        }
    }


    //移動繪製新的線重新找startport 跟 end point
    public void updatePorts() {
        if (start != null && startPortOffset != null) {
            startPort = new Point(start.x + startPortOffset.x, start.y + startPortOffset.y);
        }
        if (end != null && endPortOffset != null) {
            endPort = new Point(end.x + endPortOffset.x, end.y + endPortOffset.y);
        }
        validatePorts(); // 重新驗證
    }

    abstract void draw(Graphics g);

    //不要在往下了
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.
    //.

    //不要在往下了
    public Point getStartPortOffset() {
        return startPortOffset;
    }

    public Point getEndPortOffset() {
        return endPortOffset;
    }


}
