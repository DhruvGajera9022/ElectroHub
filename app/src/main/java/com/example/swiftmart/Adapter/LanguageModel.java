package com.example.swiftmart.Adapter;

public class LanguageModel {
    int image;
    String name, nameInEnglish;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getNameInEnglish() {
        return nameInEnglish;
    }

    public void setNameInEnglish(String nameInEnglish) {
        this.nameInEnglish = nameInEnglish;
    }

    public LanguageModel(String name, String nameInEnglish) {
        this.name = name;
        this.nameInEnglish = nameInEnglish;
    }
}
