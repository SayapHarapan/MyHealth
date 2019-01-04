package com.ichiban.kelompok1.myhealth;

public class User {
    private String nama;
    private String beratbadan;
    private String tinggibadan;
    private String lingkarperut;
    private String tanggallahir;
    private String jeniskelamin;
    private String kalori;
    private String kadarlemak;

    public User() {
    }

    public User(String nama, String beratbadan, String tinggibadan, String lingkarperut, String tanggallahir, String jeniskelamin, String kalori, String kadarlemak) {
        this.nama = nama;
        this.beratbadan = beratbadan;
        this.tinggibadan = tinggibadan;
        this.lingkarperut = lingkarperut;
        this.tanggallahir = tanggallahir;
        this.jeniskelamin = jeniskelamin;
        this.kalori = kalori;
        this.kadarlemak = kadarlemak;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getBeratbadan() {
        return beratbadan;
    }

    public void setBeratbadan(String beratbadan) {
        this.beratbadan = beratbadan;
    }

    public String getTinggibadan() {
        return tinggibadan;
    }

    public void setTinggibadan(String tinggibadan) {
        this.tinggibadan = tinggibadan;
    }

    public String getLingkarperut() {
        return lingkarperut;
    }

    public void setLingkarperut(String lingkarperut) {
        this.lingkarperut = lingkarperut;
    }

    public String getTanggallahir() {
        return tanggallahir;
    }

    public void setTanggallahir(String tanggallahir) {
        this.tanggallahir = tanggallahir;
    }

    public String getJeniskelamin() {
        return jeniskelamin;
    }

    public void setJeniskelamin(String jeniskelamin) {
        this.jeniskelamin = jeniskelamin;
    }

    public String getKalori() {
        return kalori;
    }

    public void setKalori(String kalori) {
        this.kalori = kalori;
    }

    public String getKadarlemak() {
        return kadarlemak;
    }

    public void setKadarlemak(String kadarLemak) {
        this.kadarlemak = kadarLemak;
    }
}
