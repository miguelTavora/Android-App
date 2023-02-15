package dam_path.dam_45102.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Publication {

    private String history;
    private int likes;
    private String date;
    private Map<String,String> comments;


    //tem de ter para ler os dados dele
    public Publication() {

    }

    public Publication(String history, String date) {
        this.history = history;
        this.date = date;
        likes = 0;

        comments = new HashMap<String,String>();
    }

    public String getHistory(){
        return this.history;
    }

    public String getDate(){
        return this.date;
    }

    public void increaseLike(){
        likes++;
    }

    public int getLikes(){
        return this.likes;
    }

    public void addComment(String user, String text){
        comments.put(user,text);
    }

    public Map<String,String> getComments(){
        return this.comments;
    }

}
