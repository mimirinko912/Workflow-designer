import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Arrays;

public class WorkflowEditor extends JFrame {

    /// 美觀建議是cursor給的
    private Map<JButton, ImageIcon> normalIcons = new HashMap<>();
    private Map<JButton, ImageIcon> hoverIcons = new HashMap<>();
    private final Color SELECTED_BG = new Color(50, 50, 50); // 比純黑更柔和的選中色
    private final int ICON_OFFSET = 0; // 懸浮偏移量
    /// 美觀建議是cursor給的


    private CanvasPanel canvas;
    private JButton selectButton, rectButton, ovalButton, groupButton, ungroupButton, associationButton, generalizationButton, compositionButton;
    private ToolMode currentMode = ToolMode.SELECT;

    public WorkflowEditor() {
        setTitle("ANALog-----As Needed As Lavish log");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout(8, 1));

        // //needed bottom ass
        // 按鈕初始化修改 (統一使用32x32尺寸)
        int iconSize = 32; // 可根據需求調整

        selectButton = new JButton(createScaledIcon("/icon/select_icon-removebg-preview.png", iconSize, iconSize));
        styleButton(selectButton, iconSize);

        groupButton = new JButton(createScaledIcon("/icon/group_icon-removebg-preview.png", iconSize, iconSize));
        styleButton(groupButton, iconSize);

        ungroupButton = new JButton(createScaledIcon("/icon/ungroup_icon-removebg-preview.png", iconSize, iconSize));
        styleButton(ungroupButton, iconSize);

        rectButton = createButtonWithState("/icon/rect_icon-removebg-preview.png", iconSize);
        ovalButton = createButtonWithState("/icon/oval_icon-removebg-preview.png", iconSize);
        associationButton = createButtonWithState("/icon/line_icon-removebg-preview.png", iconSize);
        generalizationButton = createButtonWithState("/icon/arrow_icon-removebg-preview.png", iconSize);
        compositionButton = createButtonWithState("/icon/diamond_icon-removebg-preview.png", iconSize);



        toolbar.add(selectButton);
        toolbar.add(rectButton);
        toolbar.add(ovalButton);
        toolbar.add(groupButton);
        toolbar.add(ungroupButton);
        toolbar.add(associationButton);
        toolbar.add(generalizationButton);
        toolbar.add(compositionButton);

        //放在左邊
        add(toolbar, BorderLayout.WEST);
        toolbar.setLayout(new GridLayout(0, 1, 0, 5)); // 垂直間距5px

        //把 畫布?我想不到更好ㄉcanvas翻譯 放在右邊EAST不知道為什麼不行只能放在CENTER
        canvas = new CanvasPanel();
        add(canvas, BorderLayout.CENTER);

        rectButton.addActionListener(e -> setMode(ToolMode.RECT));
        ovalButton.addActionListener(e -> setMode(ToolMode.OVAL));
        selectButton.addActionListener(e -> setMode(ToolMode.SELECT));
        groupButton.addActionListener(e -> canvas.groupSelected());
        ungroupButton.addActionListener(e -> canvas.ungroupSelected());
        associationButton.addActionListener(e -> setMode(ToolMode.ASSOCIATION));
        generalizationButton.addActionListener(e -> setMode(ToolMode.GENERALIZATION));
        compositionButton.addActionListener(e -> setMode(ToolMode.COMPOSITION));

        // LABEL 另外做一個edit menu
        // https://blog.csdn.net/qq1437722579/article/details/98657546
        JMenuBar menuBar = new JMenuBar();
        JMenu editMenu = new JMenu("Edit");
        JMenuItem labelItem = new JMenuItem("Label");
        labelItem.addActionListener(e -> canvas.openLabelStyleDialog());
        editMenu.add(labelItem);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }

    private JButton createButtonWithState(String iconPath, int size) {
        ImageIcon normalIcon = createScaledIcon(iconPath, size, size);
        ImageIcon hoverIcon = createOffsetIcon(iconPath, size, size, ICON_OFFSET);

        JButton button = new JButton(normalIcon);

        // 按鈕狀態因為未來需要處理按下去變黑的情況
        normalIcons.put(button, normalIcon);
        hoverIcons.put(button, hoverIcon);

        // 設定按鈕樣式
        button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBackground(new Color(255, 255, 255));

        return button;
    }

    // 生成偏移圖示的工具方法
    private ImageIcon createOffsetIcon(String path, int width, int height, int offset) {
        ImageIcon original = createScaledIcon(path, width, height);
        BufferedImage img = new BufferedImage(
            width + offset,
            height + offset,
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(original.getImage(), offset, offset, null);
        g2d.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createScaledIcon(String path, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    // 統一設定按鈕樣式
    private void styleButton(JButton button, int size) {
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(size, size));
    }

    private void setMode(ToolMode mode) {
        currentMode = mode;
        canvas.setToolMode(mode);
        resetButtonStates(); // 改為重置所有狀態
        applySelectedEffect(); // 套用選中效果
    }

    //結束選取的時候應該要重設顏色
    private void resetButtonStates() {
    // 重置所有按鈕狀態
        List<JButton> modeButtons = Arrays.asList(
            rectButton, ovalButton, associationButton,
            generalizationButton, compositionButton
        );

        for (JButton btn : modeButtons) {
            btn.setIcon(normalIcons.get(btn));
            btn.setBackground(new Color(255, 255, 255));
        }
    }

    private void applySelectedEffect() {
        // 套用選中效果
        JButton selectedButton = switch (currentMode) {
            case RECT -> rectButton;
            case OVAL -> ovalButton;
            case ASSOCIATION -> associationButton;
            case GENERALIZATION -> generalizationButton;
            case COMPOSITION -> compositionButton
            default -> null;
        };

        if (selectedButton != null) {
            selectedButton.setIcon(hoverIcons.get(selectedButton));
            selectedButton.setBackground(SELECTED_BG);
        }
    }
}
