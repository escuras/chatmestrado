package pt.IPG.messenger.recyclerview;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Created by Dytstudio.
 */

public class Contact implements Serializable {
    String name;
    int image;
    String email;

    public Contact() { }

    public Contact(String name, int image, String email) {
        this.name = name;
        this.image = image;
        this.email = email;
    }

    public int getImage(){
        return image;
    }

    public void setImage(@DrawableRes int img){
        image = img;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}
