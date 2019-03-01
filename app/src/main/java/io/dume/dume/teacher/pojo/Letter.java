package io.dume.dume.teacher.pojo;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Letter {

    String uid;
    String body;
    Date timestamp;
    DocumentSnapshot doc;
    String token;
    String name;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocumentSnapshot getDoc() {
        return doc;
    }

    public void setDoc(DocumentSnapshot doc) {
        this.doc = doc;
    }

    Letter() {

    }

    public Letter(String uid, String body, Date timestamp) {
        this.uid = uid;
        this.body = body;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
