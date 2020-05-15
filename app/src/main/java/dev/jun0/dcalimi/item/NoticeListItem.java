package dev.jun0.dcalimi.item;

public class NoticeListItem {
    private String titleStr;
    private String uploaderStr;
    private String dateStr;

    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setUploader(String uploader) {
        uploaderStr = uploader ;
    }
    public void setDate(String date) {
        dateStr = date ;
    }

    public String getTitle() {
        return this.titleStr ;
    }
    public String getUploader() {
        return this.uploaderStr;
    }
    public String getDateStr() {
        return this.dateStr;
    }
}
