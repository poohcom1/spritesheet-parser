package com.poohcom1.app.reusables;

import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;

public class ToggleButton extends JToggleButton {
    private static final Color BACKGROUND = new Color(122, 131, 167);
    private static final Color BACKGROUND_HOVER = new Color(193, 203, 226, 255);

    private static final Color ICON = Color.black;
    private static final Color ICON_DISABLED = Color.gray;

    public ToggleButton(String text) {
        super(text);
        init();
    }

    public ToggleButton(Icon icon) {
        super(icon);
        init();
    }

    private void init() {
        setFocusable(false);
        setContentAreaFilled(false);
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(getIcon().getIconWidth(), getIcon().getIconHeight()));

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!isSelected() && isEnabled()) {
                    setOpaque(true);
                    setBackground(BACKGROUND_HOVER);
                } else {
                    setBackground(BACKGROUND);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!isSelected() && isEnabled()) {
                    setOpaque(false);
                    setBackground(BACKGROUND);
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            setOpaque(false);
            ((FontIcon) getIcon()).setIconColor(ICON_DISABLED);
        } else {
            ((FontIcon) getIcon()).setIconColor(ICON);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setBackground(BACKGROUND);
            setOpaque(true);
        } else {
            setOpaque(false);
        }
    }
}
