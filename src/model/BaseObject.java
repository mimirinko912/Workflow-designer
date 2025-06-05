import java.awt.*;

// BaseObject-Rectangle
//         -Oval
//         Group
//         ****需要另外處理Group有些要回傳null或者直接return****
//         需要做的function:
//                     draw外框
//                     連接port
//                     select



abstract class BaseObject {
    int x, y;
    String label = "";
    Color labelColor = Color.BLACK;
    int fontSize = 10;
    boolean selected = false;
    String labelShape = "Rectangle";
    Color labelBackgroundColor = new Color(0, 0, 0, 0); // 透明

    private Group parentGroup = null;

    public void setParentGroup(Group group) {
        this.parentGroup = group;
    }

    public Group getParentGroup() {
        return parentGroup;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public BaseObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    abstract void draw(Graphics g);
    abstract boolean contains(int x, int y);
    public void move(int newX, int newY) {
        if (this instanceof Group) {
            ((Group) this).move(newX, newY);
        } else {
            this.x = newX;
            this.y = newY;
        }
    }

    void setLabel(String label, Color bgColor, int size, String shape) {
        this.label = label;
        this.labelBackgroundColor = bgColor;
        this.fontSize = size;
        this.labelShape = shape;
    }

    void drawLabel(Graphics g) {
        // 設置字體
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(label);
        int textHeight = fm.getHeight();

        // 背景形狀尺寸（根據字體大小動態調整）
        int boxWidth = textWidth;
        int boxHeight = textHeight;
        int centerX = x + 25;
        int centerY = y + 15;

        // 繪製背景形狀
        g.setColor(labelBackgroundColor);
        if (labelShape.equals("Oval")) {
            g.fillOval(centerX - boxWidth/2, centerY - boxHeight/2, boxWidth, boxHeight);
        } else {
            g.fillRect(centerX - boxWidth/2, centerY - boxHeight/2, boxWidth, boxHeight);
        }

        // 繪製文字
        g.setColor(labelColor);
        g.drawString(label, centerX - textWidth/2, centerY + textHeight/4 + 2);
    }

    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 50, 30);
    }

    public abstract boolean isPortValid(Point port);
    abstract Point getNearestPort(int px, int py);
}
