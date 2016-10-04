package is.pinterjann.jaws.activities;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import is.pinterjann.jaws.adapter.NetworkAdapter;
import is.pinterjann.jaws.model.WirelessNetwork;

public class SaveSSIDsTask extends AsyncTask<Object,Void,String> {

    Context context;
    NetworkAdapter networkAdapter;

    // Pass along file a object we can write to

    protected String doInBackground(Object... objects) {
        networkAdapter = (NetworkAdapter)objects[0];
        context = (Context)objects[1];
        File outfile = (File)objects[2];

        PrintWriter pwriter;
        try {pwriter = new PrintWriter(new FileWriter(outfile, true));}
        catch (IOException e) {Log.e("JAWS",e.getMessage()); return e.getMessage();}

        List<WirelessNetwork> networkList = networkAdapter.getNetworkList();
        int num_networks = networkList.size();

        if (num_networks <= 0)
            return "No networks to save!";

        Collections.sort(networkList);

        Date now = new Date();
        DateFormat dfTime = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        DateFormat dfDate = DateFormat.getDateInstance(DateFormat.SHORT);
        String timeString = dfTime.format(now);
        String dateString = dfDate.format(now);

        String nowString = ("Collected at " + timeString + " on " + dateString);

        /*       Output format looks like:
        # Collected at 2016/10/3 22:32:36
        'samplessid' , 97:12:89:37:14:54 , -34
        ...
        Sorted in order of signal quality
        */

        pwriter.format("# %s %n", nowString);

        for (int i=0;i<num_networks;i++) {
            WirelessNetwork nw = networkList.get(i);
            String ssid = "'" + nw.getSsid() + "'";
            String bssid = nw.getBssid();
            int strength = nw.getSignal();
            pwriter.format("%-23s , %-17s , %-5d%n", ssid, bssid, strength);
        }
        pwriter.format("%n");

        pwriter.close();

        return ("Saved " + num_networks + " SSIDs to " + outfile.getAbsolutePath());

    }

    protected void onProgressUpdate() {}

    protected void onPostExecute(String msg) {

        Toast toast = Toast.makeText(context, (msg), Toast.LENGTH_SHORT);
        toast.show();
    }
}
