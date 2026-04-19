/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.project1.useraccountsystem.cs1d;

import javax.swing.JFrame;


/**
 *
 * @author DrAze
 * renzz12345
 * Aknh-NPC
 */
public class Project1UserAccountSystemCS1D {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Login System");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(680, 480);
        frame.setLocationRelativeTo(null);

        //  pass frame here
        frame.setContentPane(new loginScreen(frame));

        frame.setVisible(true);
    }
}
