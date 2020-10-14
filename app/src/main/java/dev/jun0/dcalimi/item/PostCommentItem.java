package dev.jun0.dcalimi.item;

public class PostCommentItem {
    private String mBody;
    private String mUploader;
    private String mDate;

    public PostCommentItem(String body, String uploader, String date){
        mBody = body;
        mUploader = uploader;
        mDate = date;
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
