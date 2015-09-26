package hr.from.bkoruznjak.myfirstandroidapp.db;

import java.io.Serializable;

/**
 * Created by borna on 20.09.15..
 */
public class Riddle implements Serializable {

    private String id;
    private String riddleText;
    private String riddleAnwser;
    private int viewCount;
    private int favorite;

    public Riddle(){

    }

    public Riddle(String id, String riddleText, String riddleAnwser, int viewCount, int favorite){
        this.id = id;
        this.riddleText = riddleText;
        this.riddleAnwser = riddleAnwser;
        this.viewCount = viewCount;
        this.favorite = favorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRiddleText() {
        return riddleText;
    }

    public void setRiddleText(String riddleText) {
        this.riddleText = riddleText;
    }

    public String getRiddleAnwser() {
        return riddleAnwser;
    }

    public void setRiddleAnwser(String riddleAnwser) {
        this.riddleAnwser = riddleAnwser;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
