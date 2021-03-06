package pt.IPG.messenger.recyclerview;

/**
 * Created by Dytstudio.
 */

public class Chat {

    private String mName;
    private String mLastChat;
    private String mTime;

    private String mRoom;

    private int mImage;
    private boolean online;

    public String getRoom() {
        return mRoom;
    }

    public void setRoom(String mRoom) {
        this.mRoom = mRoom;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLastChat() {
        return mLastChat;
    }

    public void setLastChat(String lastChat) {
        mLastChat = lastChat;
    }

    public String getTime() {
        return mTime;
    }

    public void setmTime(String time) {
        mTime = time;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int image) {
        mImage = image;
    }

    public boolean getOnline(){
        return online;
    }

    public void setOnline(boolean on){
        online = on;
    }
}