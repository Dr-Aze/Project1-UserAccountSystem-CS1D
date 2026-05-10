/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project1.useraccountsystem.cs1d;

/**
 *
 * @author hezen
 */
public class Session {
    private static int userId;
    private static int logId; // 🔥 add this

    public static void setUserId(int id) {
        userId = id;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setLogId(int id) {
        logId = id;
    }

    public static int getLogId() {
        return logId;
    }
}
