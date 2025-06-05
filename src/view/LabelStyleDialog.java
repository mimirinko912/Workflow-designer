import javax.swing.*;
import java.awt.*;

class LabelStyleDialog extends JDialog {
    // Label的字
    private JTextField nameField;
    // 輸入要改的字的textbox
    private JComboBox<String> shapeCombo;
    // 修改顏色
    private JButton colorButton;
    private JPanel colorPreview;
    private Color selectedColor;
    // 字體大小
    private JSpinner fontSizeSpinner;
    // 確認修改
    private boolean confirmed = false;
    // 要修改的只能是basic object: RECT & OVAL
    private BaseObject targetObject;

    // 開一個浮動視窗 來調整

    public LabelStyleDialog(Frame owner, BaseObject obj) {
        super(owner, "Label Style Editor", false);
        this.targetObject = obj;
        this.selectedColor = obj.labelBackgroundColor; // 初始顏色
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setupUI();
        setSize(300, 300);
        setLocationRelativeTo(null);
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label Text
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Label Text:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(targetObject.label, 15);
        mainPanel.add(nameField, gbc);

        // Label Shape
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Label Shape:"), gbc);
        gbc.gridx = 1;
        shapeCombo = new JComboBox<>(new String[]{"Rectangle", "Oval"});
        shapeCombo.setSelectedItem(targetObject.labelShape);
        mainPanel.add(shapeCombo, gbc);

        // Color Picker
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Background Color:"), gbc);
        gbc.gridx = 1;

        // 開一個浮動視窗提供顏色選擇
        colorButton = new JButton("Select Color");
        colorPreview = new JPanel();
        colorPreview.setPreferredSize(new Dimension(50, 20));
        colorPreview.setBackground(selectedColor);
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        colorPanel.add(colorButton);
        colorPanel.add(colorPreview);
        mainPanel.add(colorPanel, gbc);

        colorButton.addActionListener(e -> chooseColor());

        // Font Size
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Font Size:"), gbc);
        gbc.gridx = 1;
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(targetObject.fontSize, 1, 24, 1));
        mainPanel.add(fontSizeSpinner, gbc);

        // 按鈕區域
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcButton = new GridBagConstraints();

        // Cancel 按鈕 靠右
        gbcButton.gridx = 0;
        gbcButton.gridy = 0;
        gbcButton.anchor = GridBagConstraints.EAST;
        gbcButton.ipadx = 10;

        gbcButton.insets = new Insets(10, 10, 10, 20);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton, gbcButton);

        // OK 按鈕 靠右
        gbcButton.gridx = 1; //只要調整X就好了
        gbcButton.anchor = GridBagConstraints.WEST;
        gbcButton.ipadx = 10;

        gbcButton.insets = new Insets(10, 20, 10, 10);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> confirmChanges());
        buttonPanel.add(okButton, gbcButton);

        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }


    // 幸運大轉盤
    private void chooseColor() {
        JDialog colorDialog = new JDialog(this, "選擇顏色", true);
        colorDialog.setLayout(new GridLayout(2, 2, 5, 5));
        Color[] colors = {Color.RED, Color.YELLOW, Color.BLUE, Color.WHITE};

        for (Color color : colors) {
            JButton btn = new JButton();
            btn.setBackground(color);
            btn.addActionListener(e -> {
                selectedColor = color;
                colorPreview.setBackground(color);
                colorDialog.dispose();
            });
            colorDialog.add(btn);
        }

        colorDialog.setSize(200, 150);
        colorDialog.setLocationRelativeTo(this);
        colorDialog.setVisible(true);
    }

    ///結束 (按下"OK"應該要及時repaint)
    private void confirmChanges() {
        confirmed = true;
        applyChanges();
        dispose();
    }

    ///更改label
    public void applyChanges() {
        //當confirm才做沒有的時候不要 (改)
        if (!confirmed) return;

        targetObject.setLabel(
            nameField.getText(),
            selectedColor,
            (int) fontSizeSpinner.getValue(),
            (String) shapeCombo.getSelectedItem()
        );



    }
}
