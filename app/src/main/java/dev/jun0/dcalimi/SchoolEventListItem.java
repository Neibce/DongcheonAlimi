package dev.jun0.dcalimi;

public class SchoolEventListItem {
    private String dateStr;
    private String dayStr;
    private String eventStr;

    public void setDate(String date) {
        dateStr = date ;
    }
    public void setDay(String day) {
        dayStr = day ;
    }
    public void setEvent(String event) {
        eventStr = event ;
    }

    public String getDate() {
        return this.dateStr ;
    }
    public String getDay() {
        return this.dayStr;
    }
    public String getEvent() {
        return this.eventStr;
    }

    @Override
    public boolean equals(Object o){
        SchoolEventListItem schoolEventListItem = (SchoolEventListItem) o;
        return (dateStr.equals(schoolEventListItem.dateStr));
    }
}
