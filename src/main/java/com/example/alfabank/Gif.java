package com.example.alfabank;

public class Gif {
    Data data;

    public void setData(Data data) {
        this.data = data;
    }

}
class Data {
    private String id;

    String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}