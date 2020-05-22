package br.bandeira.jogodasbandeirasguiz;

public class Score {

    private long id;
    private String date  = "00/00/00";
    private String time  = "00:00:00";
    private String flags =  "flag" ;
    private String hit   = "0";
    private String score = "0";

    public Score(){}

    public Score(String flags, String hit,String time, String score) {
        this.flags = flags;
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

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
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

        return  time +"  "+ flags +"  "+ hit +"  "+ score ;
    }


}
