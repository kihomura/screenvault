package com.kihomura.screenvault.pojo;

import java.time.LocalDate;

public class ReviewDetail {
    private String text;
    private LocalDate date;

    public ReviewDetail() {}

    public ReviewDetail(String text, LocalDate date) {
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ReviewDetail{" +
                "text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
