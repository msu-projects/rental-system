package com.istarvin.view;

import javax.swing.*;
import java.awt.*;

/**
 * Splash screen shown during application startup
 */
public class SplashScreen extends JWindow {
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public SplashScreen() {
        initializeComponents();
        layoutComponents();
        setSize(500, 300);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(400, 30));

        statusLabel = new JLabel("Initializing...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 3));
        mainPanel.setBackground(Color.WHITE);

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(100, 150, 255));

        JLabel titleLabel = new JLabel("Sakila Film Rental Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Center panel with icon/logo placeholder
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel("\uD83C\uDFAC", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 72));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        centerPanel.add(iconLabel, BorderLayout.CENTER);

        // Progress panel
        JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
        progressPanel.setBackground(Color.WHITE);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        progressPanel.add(statusLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);

        // Version label
        JLabel versionLabel = new JLabel("Version 1.0.0", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        versionLabel.setForeground(Color.GRAY);

        progressPanel.add(versionLabel, BorderLayout.SOUTH);

        // Add to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(progressPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public void setProgress(int value, String status) {
        progressBar.setValue(value);
        statusLabel.setText(status);
    }

    public void close() {
        setVisible(false);
        dispose();
    }
}
