package com.ymuzikant.networkscanner.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yaron Muzikant on 14-Mar-17.
 */
public class ARP {
    private static final String TAG = "ARP";

    private static final String ARP_TABLE_LINE_REGEX =
            "([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})\\s*0x[0-9a-fA-F]*\\s*0x[0-9a-fA-F]\\s*([0-9a-fA-F:]*).*";

    public ArrayList<NetworkDevice> getDevices() {
        ArrayList<NetworkDevice> devices = new ArrayList<>();
        Runtime runtime = Runtime.getRuntime();
        try {
            Process arpProcess = runtime.exec("cat /proc/net/arp");
            int arpResult = arpProcess.waitFor();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(arpProcess.getInputStream()));

            Pattern arpTableLinePattern = Pattern.compile(ARP_TABLE_LINE_REGEX);
            String arpTableLine;
            while ((arpTableLine = stdInput.readLine()) != null) {
                Log.d(TAG, arpTableLine);

                // Add device from arp table
                Matcher arpTableLineMatcher = arpTableLinePattern.matcher(arpTableLine);
                if (arpTableLineMatcher.matches() && arpTableLineMatcher.groupCount() == 2) {
                    String ip = arpTableLineMatcher.group(1);
                    String mac = arpTableLineMatcher.group(2).toLowerCase();

                    if (!mac.equals("00:00:00:00:00:00")) {
                        devices.add(new NetworkDevice(ip, mac));
                    }
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, "Failed getting devices from ARP [" + ex.getMessage() + "]");
        }

        return devices;
    }
}
