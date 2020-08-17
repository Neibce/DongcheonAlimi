package dev.jun0.dcalimi.item;

public class PostCommentItem {
    private int mId;
    private String mBody;
    private String mUploader;
    private String mDate;

    public PostCommentItem(int id, String body, String uploader, String date){
        mId = id;
        mBody = body;
        mUploader = uploader;
        mDate = date;
    }

    public int getId(){
        return mId;
    }
    public String getBody(){
        return mBody;
    }
    public String getUploader(){
        return mUploader;
    }
    public String getDate(){
        return mDate;
    }

}
