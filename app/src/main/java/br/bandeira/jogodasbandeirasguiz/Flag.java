package br.bandeira.jogodasbandeirasguiz;

public class Flag {

    private String flagName;
    private int idFlag;
    private boolean isSymbol = false;

    public Flag(){}

    public boolean isSymbol() {
        return isSymbol;
    }

    public void setIsSymbol(boolean mixed) {
        this.isSymbol = mixed;
    }

    public int getIdFlag() {
        return idFlag;
    }

    public void setIdImage(int idFlag) {
        this.idFlag = idFlag;
    }

    public String getFragName() {
        return flagName;
    }

    public void setFragName(String flagName) {
        this.flagName = flagName;
    }
}
