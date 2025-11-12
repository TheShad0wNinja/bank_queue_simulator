package com.bank.ui.components;

import com.bank.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThemeToggledButton extends JButton {
    private final Color onBackgroundColor = Theme.PRIMARY_LIGHT;
    private final Color onHoverColor = Theme.PRIMARY_HOVER;
    private final Color onTextColor = Theme.PRIMARY;
    private final Color offBackgroundColor = new Color(0,0,0,0);
    private final Color offHoverColor = Theme.PRIMARY_HOVER;
    private final Color offTextColor = Theme.TEXT_SECONDARY;
    private boolean hovered = false;
    private boolean toggled = false;

    public ThemeToggledButton(String text) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        Color offTextColor = Theme.TEXT_PRIMARY;
        setForeground(offTextColor);
        setBackground(offBackgroundColor);
        setFont(Theme.DEFAULT_FONT);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    public void toggle() {
        toggled = !toggled;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (toggled) {
            g2.setColor(onBackgroundColor);
            setForeground(onTextColor);
        }
        else {
            g2.setColor(hovered ? offHoverColor : offBackgroundColor);
            setForeground(hovered ? Color.white : offTextColor);
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        super.paintComponent(g2);
        g2.dispose();
    }
}
