package com.poohcom1.spritesheetparser.app.reusables;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToggleButtonRadio extends JPanel {
    private static final Color BUTTON_UNSELECTED_COLOR = Color.black;
    private static final Color BUTTON_SELECTED_COLOR = Color.BLUE;
    private static final Color BUTTON_BACKGROUND = new Color(122, 131, 167);

    private final List<JToggleButton> buttons;

    private int selectedIndex = 0;

    public ToggleButtonRadio() {
        setLayout(new FlowLayout());

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        buttons = new ArrayList<>();
    }

    private void buttonInit(JToggleButton button, String toolTip) {
        button.setFocusable(false);
        button.setContentAreaFilled(false);
        button.setToolTipText(toolTip);
        button.setBackground(BUTTON_BACKGROUND);
        button.setPreferredSize(new Dimension(button.getIcon().getIconWidth(), button.getIcon().getIconHeight()));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setOpaque(true);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.isSelected()) button.setOpaque(false);
            }
        });
    }

    private void onSelected(JToggleButton button) {
        button.setSelected(true);
        button.setOpaque(true);
        //((FontIcon) button.getIcon()).setIconColor(BUTTON_SELECTED_COLOR);
    }

    private void onUnSelected(JToggleButton button) {
        button.setSelected(false);
        button.setOpaque(false);
        //((FontIcon) button.getIcon()).setIconColor(BUTTON_UNSELECTED_COLOR);
    }

    public void addButton(JToggleButton newButton, ButtonToggledListener listener, String toolTip) {
        buttonInit(newButton, toolTip);

        if (buttons.size() == 0) onSelected(newButton);

        newButton.addActionListener(e -> {
            buttons.forEach(this::onUnSelected);

            selectedIndex = buttons.indexOf(newButton);
            onSelected(newButton);
            listener.buttonToggled();
        });

        add(newButton);
        buttons.add(newButton);
    }

    public void addButton(String name, ButtonToggledListener listener, String toolTip) {
        addButton(new JToggleButton(name), listener, toolTip);
    }

    public void addButton(Icon icon, ButtonToggledListener listener, String toolTip) {
        addButton(new JToggleButton(icon), listener, toolTip);
    }


    public void setButtonsEnabled(boolean enabled) {
        if (enabled) {
            buttons.forEach(button -> {
                button.setSelected(buttons.indexOf(button) == 0);
                button.setEnabled(true);
            });
        } else {
            buttons.forEach(button -> {
                button.setSelected(false);
                button.setEnabled(false);
            });
        }
    }

    public List<JToggleButton> getButtons() {
        return buttons;
    }

    public interface ButtonToggledListener {
        void buttonToggled();
    }

    @Override
    public void removeAll() {
        super.removeAll();
        buttons.clear();
    }
}
