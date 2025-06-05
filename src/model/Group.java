import java.awt.*;
import java.util.ArrayList;
import java.util.List;

///Group 內容可以是 object or group
///     rect oval
///     group
/// group position
///  需要list 來儲存內建的
///     addchild
///     set child select
///     相對移動
///     繪製child port & 有期徒刑
///
///
/// 新增 Get child parent 來用來解除nested un group 旺上尋找
class Group extends BaseObject{
    private List<BaseObject> children = new ArrayList<>();
    public Group(int x, int y) {
        super(x, y);
    }

    // set true if one of it selected
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        for (BaseObject child : children) {
            child.setSelected(selected);
        }
    }

    // 提供select return each cijgipdhigdh 回傳
    boolean contains(int px, int py) {
        return children.stream().anyMatch(child -> child.contains(px, py));
    }


    /// ************************************************************************* */
    /// ************************************************************************* */
    /// ************************************************************************* */
    /// ************************************************************************* */
    /// ************************************************************************* */
    /// ************************************************************************* */
    /// ********************* NEST GROUP 老 媽 得 了 M V P *********************** */
    /// ************************************************************************* */
    /// ************************************************************************* */
    /// ************************************************************************* */
    /// ************************************************************************* */
    /// ************************************************************************* */
    /// ************************************************************************* */
    //地回的去找Group裡面的每個OBJ或者是GROUP就要地回去找
    public void move(int newX, int newY) {
        int dx = newX - x;
        int dy = newY - y;
        for (BaseObject child : children) {
            if (child instanceof Group) {
                ((Group) child).move(child.x + dx, child.y + dy);
            } else {
                child.x += dx;
                child.y += dy;
            }
        }
        this.x = newX;
        this.y = newY;
    }


    void addChild(BaseObject obj) {
        children.add(obj);
        obj.setParentGroup(this);
    }

    void draw(Graphics g) {
        for (BaseObject child : children) {
            child.draw(g);
        }
        drawLabel(g);
    }

    List<BaseObject> getChildren() {
        return children;
    }

    //Group 範圍 框選
    public Rectangle getBounds() {
        if (children.isEmpty()) return new Rectangle(x, y, 0, 0);
        int minX = children.stream().mapToInt(c -> c.x).min().orElse(x);
        int minY = children.stream().mapToInt(c -> c.y).min().orElse(y);
        int maxX = children.stream().mapToInt(c -> c.x + 50).max().orElse(x + 50);
        int maxY = children.stream().mapToInt(c -> c.y + 30).max().orElse(y + 30);
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    Point getNearestPort(int px, int py){return null;}
    public boolean isPortValid(Point port) {return false;}
}
