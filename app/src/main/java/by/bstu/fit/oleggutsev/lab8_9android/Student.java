package by.bstu.fit.oleggutsev.lab8_9android;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Oleg Gutsev on 07.11.2017.
 */

public class Student {

    private String key;
    private String name;
    private String surname;
    private String moreInfo;
    private String photoUri;
    private String mark;

    public Student(){

    }

    public Student(String name, String surname, String moreInfo, String mark, String photoUri, String key) {
        this.key = key;
        this.name = name;
        this.surname = surname;
        this.moreInfo = moreInfo;
        this.mark = mark;
        this.photoUri = photoUri;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    @Exclude
    public Map<String, String> toMap(){
        HashMap<String, String> result = new HashMap<>();
        if(!name.isEmpty() && !surname.isEmpty() &&
                !moreInfo.isEmpty() && !mark.isEmpty() && !photoUri.isEmpty()) {
            result.put("name", name);
            result.put("surname", surname);
            result.put("moreInfo", moreInfo);
            result.put("mark", mark);
            result.put("photoId", photoUri);
        }
        return result;
    }
}
