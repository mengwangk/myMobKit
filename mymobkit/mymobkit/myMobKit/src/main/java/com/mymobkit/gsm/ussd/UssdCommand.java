package com.mymobkit.gsm.ussd;

/**
 * USSD (Unstructured Supplementary Service Data) commands.
 *
 * Refer to
 *
 * http://forum.xda-developers.com/showthread.php?t=1953506
 * http://umeshisran4android.blogspot.in/2015/11/how-to-readparse-ussd-messages.html
 * https://github.com/alaasalman/ussdinterceptor/
 */
public final class UssdCommand {

    // Information
    public static final String SOFTWARE_VERSION_INFO = "*#44336#";
    public static final String VIEW_SOFTWARE_VERSION = "*#1234#";

   /*

    *#12580*369# SW & HW Info
    *#197328640# Service Mode
    *#06# = IMEI Number.
            *#1234# = Firmware Version.
            *#2222# = H/W Version.
            *#8999*8376263# = All Versions Together.
    *#272*imei#* Product code
    *#*#3264#*#*- RAM version
    *#92782# = Phone Model
    *#*#9999#*#*= Phone/pda/csc info



    Testing

    *#07# Test History
    *#232339# WLAN Test Mode
    *#232331# Bluetooth Test Mode
    *#*#232331#*#*- Bluetooth test
    *#0842# Vibration Motor Test Mode
    *#0782# Real Time Clock Test
    *#0228# ADC Reading
    *#32489# (Ciphering Info)
            *#232337# Bluetooth Address
    *#0673# Audio Test Mode
    *#0*# General Test Mode
    *#3214789650# LBS Test Mode
    *#0289# Melody Test Mode
    *#0589# Light Sensor Test Mode
    *#0588# Proximity Sensor Test Mode
    *#7353# Quick Test Menu
    *#8999*8378# = Test Menu.
            *#*#0588#*#*- Proximity sensor test
    *#*#2664#*#*- Touch screen test
    *#*#0842#*#*- Vibration test*


    Network

    *7465625*638*# Configure Network Lock MCC/MNC
    #7465625*638*# Insert Network Lock Keycode
    *7465625*782*# Configure Network Lock NSP
    #7465625*782*# Insert Partitial Network Lock Keycode
    *7465625*77*# Insert Network Lock Keycode SP
    #7465625*77*# Insert Operator Lock Keycode
    *7465625*27*# Insert Network Lock Keycode NSP/CP
    #7465625*27*# Insert Content Provider Keycode
    *#7465625# View Phone Lock Status
    *#232338# WLAN MAC Address
    *#526# WLAN Engineering Mode -runs wlan tests (same as below)
    *#528# WLAN Engineering Mode
    *#2263# RF Band Selection-not sure about this one appears to be locked
    *#301279# HSDPA/HSUPA Control Menu---change HSDPA classes (opt. 1-5)



    Tools/Misc.

    *#*#1111#*#*- Service Mode

    #273283*255*663282*# Data Create SD Card

    *#4777*8665# = GPSR Tool.
            *#4238378# GCF Configuration
    *#1575# GPS Control Menu


    *#9090# Diagnostic Configuration
    *#7284# USB I2C Mode Control—mount to usb for storage/modem
    *#872564# USB Logging Control
    *#9900# System dump mode- can dump logs for debugging

    *#34971539# Camera Firmware Update
    *#7412365# Camera Firmware Menu

    *#273283*255*3282*# Data Create Menu- change sms, mms, voice, contact limits
    *2767*4387264636# Sellout SMS / PCODE view
    *#3282*727336*# Data Usage Status
    *#*#8255#*#*- Show GTalk service monitor-great source of info

    *#3214789# GCF Mode Status

    *#0283# Audio Loopback Control
    #7594# Remap Shutdown to End Call TSK
    *#272886# Auto Answer Selection

    ****SYSTEM***

    USE CAUTION

    *#7780# Factory Reset
    *2767*3855# Full Factory Reset
    *#*#7780#*#* Factory data reset



    *#745# RIL Dump Menu
    *#746# Debug Dump Menu
    *#9900# System Dump Mode

    *#8736364# OTA Update Menu
    *#2663# TSP / TSK firmware update
    *#03# NAND Flash S/N
    */


}
