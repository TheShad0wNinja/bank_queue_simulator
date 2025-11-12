package com.bank.ui.components;

import com.bank.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThemeButton extends JButton {
    public enum Variant {
        PRIMARY,
        DEFAULT
    }

    private boolean hovered = false;
    private final Variant variant = Variant.PRIMARY;
    private Color backgroundColor = Theme.PRIMARY;
    private Color hoverColor = Theme.PRIMARY_HOVER;
    private Color borderColor = Theme.BORDER;
    private boolean hasBorder = true;

    public ThemeButton(String text) {
        this(text, Variant.DEFAULT);
    }

    public ThemeButton(String text, Variant variant) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setBackground(backgroundColor);
        setFont(Theme.DEFAULT_FONT);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(8, 12, 8, 12));
        setOpaque(false);

        switch (variant) {
            case PRIMARY:
                backgroundColor = Theme.PRIMARY;
                hoverColor = Theme.PRIMARY_HOVER;
                hasBorder = false;
                break;
            default:
                backgroundColor = Theme.PANEL_BG;
                hoverColor = Theme.BORDER;
                hasBorder = true;
                setForeground(Color.BLACK);
        }

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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 12;
        int borderThickness = 2;

        g2.setColor(hovered ? hoverColor : backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        if (hasBorder) {
            g2.setStroke(new BasicStroke(borderThickness));
            g2.setColor(borderColor);
            g2.drawRoundRect(
                    borderThickness / 2,
                    borderThickness / 2,
                    getWidth() - borderThickness,
                    getHeight() - borderThickness,
                    arc,
                    arc
            );
        }

        super.paintComponent(g2);
        g2.dispose();
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }
}