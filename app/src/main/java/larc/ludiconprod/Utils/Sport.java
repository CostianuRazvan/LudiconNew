package larc.ludiconprod.Utils;

import android.graphics.Bitmap;

/**
 * Created by LaurUser on 5/23/2016.
 */
public class Sport {

    public String name;
    public String id;
    public boolean isChecked;
    public Bitmap icon;

    public Sport(String name, String id, boolean isChecked, Bitmap icon) {
        this.name = name;
        this.id = id;
        this.isChecked = isChecked;
        this.icon = icon;
    }

    public void setSelected(boolean value) {
        this.isChecked = value;
    }
}