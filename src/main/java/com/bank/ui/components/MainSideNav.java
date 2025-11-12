package com.bank.ui.components;

import com.bank.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class MainSideNav extends ThemePanel {
    private Consumer<String> onSelect;
    private String[] pages;
    private ThemeToggledButton currentPage;

    public void setOnSelect(Consumer<String> onSelect) {
        this.onSelect = onSelect;
    }

    public MainSideNav(String[] pages, String defaultPage) {
        setLayout(new GridBagLayout());
        setBackground(Theme.PANEL_BG);
        setPreferredSize(new Dimension(250, Integer.MAX_VALUE));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        JLabel logo = new JLabel("Bank Queue Sim");
        logo.setFont(Theme.TITLE_FONT);
        c.insets = new Insets(10, 20, 10, 20);
        add(logo, c);

        add(Box.createVerticalStrut(100));

        c.insets = new Insets(1, 20, 1, 20);
        int i = 1;
        for (String page : pages) {
            ThemeToggledButton btn = new ThemeToggledButton(page.substring(0, 1).toUpperCase() + page.substring(1));
            if (page.equals(defaultPage)) {
                currentPage = btn;
                currentPage.toggle();
            }

            btn.addActionListener(e -> {
                if (btn.equals(currentPage)) {return;}

                currentPage.toggle();
                currentPage = btn;
                currentPage.toggle();

                onSelect.accept(page);
            });

            c.gridy = i++;
            add(btn, c);
        }

        c.gridy = i + 1;
        c.weighty = 1;
        add(Box.createVerticalGlue(), c);
    }
}
