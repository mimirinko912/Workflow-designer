import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 在有些如錯誤訊息的迴傳 在兼顧 code 的可讀性，以及function位置的合理性，應該使用instanceof，而非多建立新的class來繼承並塞入額外的function
// 要使用 instanceof 的原因是因為，直觀的說在單一一個Project之中不應該額外寫一個父 class 只為了解決一個nested的問題

class CanvasPanel extends JPanel {
    private List<BaseObject> objects = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();
    private ToolMode toolMode = ToolMode.SELECT;
    private List<BaseObject> selectedObjects = new ArrayList<>();
    private BaseObject dragStartObj = null;       // 連接線用的起始點
    private Point dragStartPoint = null;          // 拖拽用的起始點屁眼
    private Rectangle selectionBox = null;
    private BaseObject draggedObject = null;
    private Point dragOffset = null;
    private Point initialDragPoint = null; // 記錄拖曳起始點（用於計算偏移）
    private Map<BaseObject, Point> initialObjectPositions = new HashMap<>(); // 記錄物件初始位置
    private Point currentDragPoint = null; // 新增：記錄當前拖曳點

    public CanvasPanel() {
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
    }

    public void openLabelStyleDialog() {

        if (selectedObjects.size() != 1) {
            JOptionPane.showMessageDialog(this, "Please select exactly one object!");
            return;
        }

        BaseObject obj = selectedObjects.get(0);
        if (obj instanceof Group) {
            JOptionPane.showMessageDialog(this, "Groups cannot be labeled!");
            return;
        }

        LabelStyleDialog dialog = new LabelStyleDialog(
            (Frame)SwingUtilities.getWindowAncestor(this),
            obj
        );
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                repaint();
            }
        });
        dialog.setVisible(true);
        repaint();
    }

    // 處理滑鼠按下事件
    private void handleMousePressed(MouseEvent e) {
        switch (toolMode) {
            case RECT, OVAL -> createObject(e);
            case SELECT -> {
                startSelection(e);
                // 初始化拖曳狀態：記錄所有被選中物件的初始位置
                initialDragPoint = e.getPoint();
                initialObjectPositions.clear();
                for (BaseObject obj : selectedObjects) {
                    initialObjectPositions.put(obj, new Point(obj.x, obj.y));
                }
            }
            case ASSOCIATION, GENERALIZATION, COMPOSITION -> startConnection(e);
        }
        repaint();
    }

    //--- UseCase D: 移動物件 ---//
    private void handleMouseDragged(MouseEvent e) {
        if (toolMode == ToolMode.SELECT && !selectedObjects.isEmpty()) {
            // 計算整體偏移量
            int dx = e.getX() - initialDragPoint.x;
            int dy = e.getY() - initialDragPoint.y;

            // 移動所有被選中的物件（含嵌套群組）
            for (BaseObject obj : selectedObjects) {
                Point initialPos = initialObjectPositions.get(obj);
                if (initialPos != null) {
                    int newX = initialPos.x + dx;
                    int newY = initialPos.y + dy;
                    obj.move(newX, newY);
                }
            }

            // 立即更新所有相關連線
            updateAllConnections();
            repaint();
        } else if (toolMode == ToolMode.SELECT && dragStartPoint != null) {
            updateSelectionBox(e);
            repaint();
        } else if (toolMode != ToolMode.SELECT && dragStartObj != null) {
            // 新增：在連接線模式下記錄當前拖曳點
            currentDragPoint = e.getPoint();
            repaint();
        }
    }

    private void handleMouseReleased(MouseEvent e) {

        switch (toolMode) {
            case SELECT -> {
                finishSelection(e);
                draggedObject = null;
                dragOffset = null;
            }
            case ASSOCIATION, GENERALIZATION, COMPOSITION -> finishConnection(e);
        }
        repaint();
    }

    //--- UseCase D: 移動物件 ---//
    private void updateAllConnections() {
        for (Connection conn : connections) {
            conn.updatePorts(); // 強制更新連線端點
        }
    }

    //--- UseCase A: 創建物件 ---//
    private void createObject(MouseEvent e) {
        BaseObject newObj = switch (toolMode) {
            case RECT -> new Rect(e.getX(), e.getY());
            case OVAL -> new Oval(e.getX(), e.getY());
            default -> null;
        };
        if (newObj != null) {
            objects.add(newObj);
            selectedObjects.add(newObj);
        }
    }

    //--- UseCase B: 建立連接線 ---//
    private void startConnection(MouseEvent e) {
        dragStartObj = findObjectAt(e.getX(), e.getY());
        if (dragStartObj != null && !(dragStartObj instanceof Group)) {
            dragStartPoint = e.getPoint();
        }

    }

    private void finishConnection(MouseEvent e) {
        if (dragStartObj == null) return;
        BaseObject dragEndObj = findObjectAt(e.getX(), e.getY());
        if (dragEndObj != null && !(dragEndObj instanceof Group)) {
            // 修改：使用正確的startPort和endPort
            Point startPort = dragStartObj.getNearestPort(dragStartPoint.x, dragStartPoint.y);
            Point endPort = dragEndObj.getNearestPort(e.getX(), e.getY());

            Connection conn = switch (toolMode) {
                case ASSOCIATION -> new AssociationLine(dragStartObj, dragEndObj, startPort, endPort);
                case GENERALIZATION -> new GeneralizationLine(dragStartObj, dragEndObj, startPort, endPort);
                case COMPOSITION -> new CompositionLine(dragStartObj, dragEndObj, startPort, endPort);
                default -> null;
            };
            if (conn != null) connections.add(conn);
        }
        dragStartObj = null;
        currentDragPoint = null; // 重置當前拖曳點
    }

    //--- UseCase C: 選擇物件 ---//
    private void startSelection(MouseEvent e) {
        // 取消所有物件的選中狀態
        selectedObjects.forEach(obj -> obj.setSelected(false));
        selectedObjects.clear();

        // 找到被點擊的物件（可能是GROUP或object）
        BaseObject clickedObj = findObjectAt(e.getX(), e.getY());
        if (clickedObj != null) {
            // 將物件移到最上層
            // 可以先將 select 的 object or group remove
            // 再重新加入 canvas 就可以讓他們跑到最上層
            objects.remove(clickedObj);
            objects.add(clickedObj);
            clickedObj.setSelected(true);
            // 把他加進選取的 list 以便 traverse 檢查 nested group
            selectedObjects.add(clickedObj);
            if (clickedObj instanceof Group) {
                ((Group) clickedObj).getChildren().forEach(child -> {
                    child.setSelected(true);
                });
            }
        } else {
            dragStartPoint = e.getPoint();
        }
        repaint();
    }

    private void updateSelectionBox(MouseEvent e) {
        int x1 = Math.min(dragStartPoint.x, e.getX());
        int y1 = Math.min(dragStartPoint.y, e.getY());
        int x2 = Math.max(dragStartPoint.x, e.getX());
        int y2 = Math.max(dragStartPoint.y, e.getY());
        selectionBox = new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    private void finishSelection(MouseEvent e) {
        if (selectionBox != null) {
            // 遍歷所有物件，檢查是否完全位於框選範圍內
            for (BaseObject obj : objects) {
                Rectangle bounds = obj.getBounds();
                if (selectionBox.contains(bounds)) {
                    if (obj instanceof Group) {
                        // 群組物件需所有子物件都在範圍內才選中
                        boolean allChildrenIn = ((Group) obj).getChildren().stream()
                            .allMatch(child -> selectionBox.contains(child.getBounds()));
                        if (allChildrenIn) {
                            obj.setSelected(true);
                            selectedObjects.add(obj);
                        }
                    } else {
                        obj.setSelected(true);
                        selectedObjects.add(obj);
                    }
                }
            }
            selectionBox = null;
        }
    }

    private BaseObject findObjectAt(int x, int y) {
        for (int i = objects.size()-1; i >= 0; i--) {
            BaseObject obj = objects.get(i);
            if (obj.contains(x, y)) {
                if (obj.getParentGroup() != null) {
                    return obj.getParentGroup();
                }
                return obj;
            }
        }
        return null;
    }

    private void drawGroupObjects(Graphics g, Group group) {
        for (BaseObject child : group.getChildren()) {
            child.draw(g);
            if (child instanceof Group) {
                drawGroupObjects(g, (Group) child); // 遞迴處理
            }
            if (child.selected) {
                drawPorts(g, child);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 繪製所有物件
        for (BaseObject obj : objects) {
            obj.draw(g);
            if (obj instanceof Group) {
                drawGroupObjects(g, (Group) obj);
            }
            if (obj.selected) {
                drawPorts(g, obj);
            }
        }

        // 繪製連接線
        for (Connection conn : connections) {
            conn.draw(g);
        }

        // 繪製選擇框
        if (toolMode == ToolMode.SELECT && selectionBox != null) {
            g.setColor(new Color(0, 0, 255, 50));
            g.fillRect(selectionBox.x, selectionBox.y, selectionBox.width, selectionBox.height);
            g.drawRect(selectionBox.x, selectionBox.y, selectionBox.width, selectionBox.height);
        }

        // 繪製預覽線
        else if (dragStartObj != null && currentDragPoint != null) {
            // 修改：使用正確的startPort和currentDragPoint繪製預覽線
            if(dragStartObj.getNearestPort(dragStartPoint.x, dragStartPoint.y)==null){
                return;
            }
            Point startPort = dragStartObj.getNearestPort(dragStartPoint.x, dragStartPoint.y);
            g.setColor(Color.GRAY);
            g.drawLine(startPort.x, startPort.y, currentDragPoint.x, currentDragPoint.y);
        }
    }

    private void drawPorts(Graphics g, BaseObject obj) {
        if (obj instanceof Rect) {
            // Rect 的 8 個連接埠
            int[] xPoints = {obj.x, obj.x + 25, obj.x + 50, obj.x + 50, obj.x + 50, obj.x + 25, obj.x, obj.x};
            int[] yPoints = {obj.y, obj.y, obj.y, obj.y + 15, obj.y + 30, obj.y + 30, obj.y + 30, obj.y + 15};
            for (int i = 0; i < 8; i++) {
                g.fillRect(xPoints[i] - 2, yPoints[i] - 2, 4, 4);
            }
        } else if (obj instanceof Oval) {
            // Oval 的 4 個連接埠
            int centerX = obj.x + 25, centerY = obj.y + 15;
            int[] xPoints = {centerX, centerX + 25, centerX, centerX - 25};
            int[] yPoints = {centerY - 15, centerY, centerY + 15, centerY};
            for (int i = 0; i < 4; i++) {
                g.fillRect(xPoints[i] - 2, yPoints[i] - 2, 4, 4);
            }
        }
    }
    public void groupSelected() {
        if (selectedObjects.isEmpty()) return;

        List<BaseObject> toGroup = selectedObjects.stream()
            .filter(obj -> obj.getParentGroup() == null)
            .toList();

        if (toGroup.isEmpty()) return;

        int minX = toGroup.stream().mapToInt(obj -> obj.x).min().orElse(0);
        int minY = toGroup.stream().mapToInt(obj -> obj.y).min().orElse(0);

        Group group = new Group(minX, minY);
        for (BaseObject obj : toGroup) {
            group.addChild(obj);
            objects.remove(obj);
        }
        objects.add(group);

        selectedObjects.clear();
        selectedObjects.add(group);
        repaint();
    }

    public void ungroupSelected() {



        List<Group> groupsToUngroup = selectedObjects.stream()
            .filter(obj -> obj instanceof Group)
            .map(obj -> (Group) obj)
            .toList();

        for (Group group : groupsToUngroup) {
            List<BaseObject> children = group.getChildren();
            objects.remove(group); // 移除外層 Group
            children.forEach(child -> {
                child.setParentGroup(null); // 解除子物件的父群組關聯
                child.setSelected(false);
            });
            objects.addAll(children);
        }
        selectedObjects.clear();
        selectedObjects.forEach(obj -> obj.setSelected(false));
        selectedObjects.clear();
        repaint();
    }

    public void setToolMode(ToolMode mode) {
        this.toolMode = mode;
        dragStartObj = null;
        dragStartPoint = null;
    }
}
