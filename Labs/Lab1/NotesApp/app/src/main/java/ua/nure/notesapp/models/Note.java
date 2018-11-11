package ua.nure.notesapp.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import ua.nure.notesapp.R;


public class Note implements Serializable {
    private UUID id;
    private String title;
    private String description;
    private Importance importance;
    private Date date;
    private String imagePath;

    public Note() {
        id = UUID.randomUUID();
        title = "";
        description = "";
        importance = Importance.LOW;
        date = Calendar.getInstance().getTime();
        imagePath = "";
    }

    public Note(String title, String description, Importance importance, Date date, String imagePath) {
        id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.importance = importance;
        this.date = date;
        this.imagePath = imagePath;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Importance getImportance() {
        return importance;
    }

    public Integer getImportanceIcon() {

        if (importance == Importance.HIGH) {
            return R.drawable.high;
        }
        if (importance == Importance.LOW) {
            return R.drawable.low;
        }

        return R.drawable.normal;
    }

    public Date getDate() {
        return date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }


    public void setDate(Date date) {
        this.date = date;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

