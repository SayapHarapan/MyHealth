package com.ichiban.kelompok1.myhealth;

public class DataWanita {
    private String tinggibadan;
    private String beratbadan;
    private String lingkarperut;
    private String kadarlemak;

    public DataWanita() {
    }

    public DataWanita(String tinggibadan, String beratbadan, String lingkarperut, String kadarlemak) {
        this.tinggibadan = tinggibadan;
        this.beratbadan = beratbadan;
        this.lingkarperut = lingkarperut;
        this.kadarlemak = kadarlemak;
    }

    public String getTinggibadan() {
        return tinggibadan;
    }

    public void setTinggibadan(String tinggibadan) {
        this.tinggibadan = tinggibadan;
    }

    public String getBeratbadan() {
        return beratbadan;
    }

    public void setBeratbadan(String beratbadan) {
        this.beratbadan = beratbadan;
    }

    public String getLingkarperut() {
        return lingkarperut;
    }

    public void setLingkarperut(String lingkarperut) {
        this.lingkarperut = lingkarperut;
    }

    public String getKadarlemak() {
        return kadarlemak;
    }

    public void setKadarlemak(String kadarlemak) {
        this.kadarlemak = kadarlemak;
    }
}
