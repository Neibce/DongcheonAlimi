package dev.jun0.dcalimi.item;

public class PostItem {
    private int mId;
    private String mTitle;
    private String mUploader;
    private String mDate;

    public void set(int id, String title, String uploader, String date){
        mId = id;
        mTitle = title;
        mUploader = uploader;
        mDate = date;
    }

    public int getId(){
        return mId;
    }
    public String getTitle(){
        return mTitle;
    }
    public String getUploader(){
        return mUploader;
    }
    public String getDate(){
        return mDate;
    }

}