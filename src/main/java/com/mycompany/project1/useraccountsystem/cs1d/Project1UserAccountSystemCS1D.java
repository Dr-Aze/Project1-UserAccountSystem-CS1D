/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.project1.useraccountsystem.cs1d;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 *
 * @author DrAze
 * renzz12345
 * Aknh-NPC
 */
public class Project1UserAccountSystemCS1D {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Strata Login System");
        
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {

                int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to exit?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {

                    LogService.updateTimeOut(); // 🔥 important

                    System.exit(0);
                }
            }
        });

        //  pass frame here
        frame.setContentPane(new LoginScreen(frame));
        frame.setVisible(true);
        
        
    }
}
