package todofy.cm.models;

import todofy.cm.R;

/**
 * Created by minhazv on 5/3/2017.
 */

public class Course {
    public String title;
    public String description;
    public int id;
    public boolean isFavorite;

    public Course(String title, String description, int id, boolean isFav) {
        this.title = title;
        this.description = description;
        this.isFavorite = isFav;
        this.id = id;
    }
}
