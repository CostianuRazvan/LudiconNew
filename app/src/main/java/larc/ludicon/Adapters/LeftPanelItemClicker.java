package larc.ludicon.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import larc.ludicon.Activities.MainActivity;
import larc.ludicon.Activities.SettingsActivity;

/**
 * Created by LaurUser on 12/28/2015.
 */
public class LeftPanelItemClicker {
    public static void OnItemClick(ListView i_Drawerlist, final Context i_context,final Activity i_currActivity){
        i_Drawerlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
                        switch(position) {
                            case 0:
                                Toast.makeText(i_context, "Hei, wait for it..", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(i_context, "Hei, wait for it..", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(i_context, "Hei, wait for it..", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(i_context, "Hei, wait for it..", Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                Intent helpIntent = new Intent(i_currActivity, SettingsActivity.class);
                                //helpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i_currActivity.startActivity(helpIntent);
                                break;
                        }
                    }});
    }
}