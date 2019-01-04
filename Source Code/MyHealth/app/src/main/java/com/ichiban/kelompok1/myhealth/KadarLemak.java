package com.ichiban.kelompok1.myhealth;

public class KadarLemak {
    private String nilai;
    private String tanggal;

    public KadarLemak() {
    }

    public KadarLemak(String nilai, String tanggal) {
        this.nilai = nilai;
        this.tanggal = tanggal;
    }

    public String getNilai() {
        return nilai;
    }

    public void setNilai(String nilai) {
        this.nilai = nilai;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
