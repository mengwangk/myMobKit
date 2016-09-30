package com.mymobkit.test;

import org.junit.Test;

import java.util.regex.Pattern;

public class TestCommandLine {

    @Test
    public void testParser() {

        Pattern pattern = Pattern.compile("digi", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        if (pattern.matcher("[Digi Menu]").find()) {
            System.out.println("matched");
        } else {
            System.out.println("not matched");
        }

       /* final String commandLine = "MYMOBKITREMOTEdevice 1&device2&session id&ring:1:1212121212";
        final String from = "mengwangk@gmail.com";

        if (TextUtils.isEmpty(commandLine)) return;

        if (!commandLine.startsWith(GTalkUtils.COMMAND_START_PATTERN)) return;

        String commandInfo = commandLine.replaceFirst(GTalkUtils.COMMAND_START_PATTERN, "");

        String command = "";
        String args = "";
        String deviceFrom = "";
        String deviceTo = "";
        String sessionId = "";
        // Check if the command is meant for this device
        if (!TextUtils.isEmpty(commandInfo) && commandInfo.contains(GTalkUtils.COMMAND_SEPARATOR)) {
            deviceFrom = commandInfo.substring(0, commandInfo.indexOf(GTalkUtils.COMMAND_SEPARATOR)).trim();
            int index1 = commandInfo.indexOf(GTalkUtils.COMMAND_SEPARATOR, commandInfo.indexOf(GTalkUtils.COMMAND_SEPARATOR) + 1);
            deviceTo = commandInfo.substring(commandInfo.indexOf(GTalkUtils.COMMAND_SEPARATOR) + 1, index1);
            int index2 = commandInfo.indexOf(GTalkUtils.COMMAND_SEPARATOR, index1 + 1);
            sessionId = commandInfo.substring(index1 + 1, index2);
            commandInfo = commandInfo.substring(index2+1);
        }

        System.out.println("from -" + deviceFrom + "-");
        System.out.println("to -" + deviceTo + "-");
        System.out.println("commandInfo -" + commandInfo + "-");
        System.out.println("sessionId -" + sessionId + "-");

        // Check if the command is target for this device
        // Split the command and args from the command info String and trim these
        if (commandInfo.contains(":")) {
            command = commandInfo.substring(0, commandInfo.indexOf(":")).trim();
            args = commandInfo.substring(commandInfo.indexOf(":") + 1);
        } else {
            command = commandInfo.trim();
            args = "";
        }

        System.out.println("command -" + command + "-");
        System.out.println("args -" + args + "-");*/

    }

}