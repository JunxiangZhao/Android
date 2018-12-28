package com.example.httpapi;

public class Issue {
    String title;
    String created_at;
    String state;
    String body;

    Issue(String _title, String _create_at, String _state, String _body){
        title = _title;
        created_at = _create_at;
        state = _state;
        body = _body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getCreate_at() {
        return created_at;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreate_at(String create_at) {
        this.created_at = create_at;
    }
}
