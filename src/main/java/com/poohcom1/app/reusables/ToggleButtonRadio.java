package com.poohcom1.app.reusables;

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



    public void addButton(ToggleButton newButton, ButtonToggledListener listener, String toolTip) {
        newButton.setToolTipText(toolTip);

        if (buttons.size() == 0) newButton.setSelected(true);

        newButton.addActionListener(e -> {
            buttons.forEach(b -> b.setSelected(false));

            selectedIndex = buttons.indexOf(newButton);
            newButton.setSelected(true);
            listener.buttonToggled();
        });

        add(newButton);
        buttons.add(newButton);
    }

    public void addButton(String name, ButtonToggledListener listener, String toolTip) {
        addButton(new ToggleButton(name), listener, toolTip);
    }

    public void addButton(Icon icon, ButtonToggledListener listener, String toolTip) {
        addButton(new ToggleButton(icon), listener, toolTip);
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
