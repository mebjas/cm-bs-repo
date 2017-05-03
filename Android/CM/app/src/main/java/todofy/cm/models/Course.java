package todofy.cm.models;

import todofy.cm.R;

/**
 * Created by minhazv on 5/3/2017.
 */

public class Course {
    public String title;
    public String description;
    public boolean isFavorite;

    public Course(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
