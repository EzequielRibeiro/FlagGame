package br.bandeira.jogodasbandeirasguiz;

public class Score {

    private long id;
    private String date;
    private String time;
    private String error;
    private String hit;
    private String score;

    public Score(){}

    public Score(String error, String hit,String time, String score) {
        this.error = error;
        this.hit  = hit;
        this.time = time;
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getHit() {
        return hit;
    }

    public void setHit(String hit) {
        this.hit = hit;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {

        return  time +"  "+ error +"  "+ hit +"  "+ score ;
    }


}
