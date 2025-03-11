package com.kihomura.screenvault.pojo;

import com.kihomura.screenvault.converter.ReviewConverter;
import com.kihomura.screenvault.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Table(name="user_content", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "content_id"})})
@Entity
public class UserContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "rate", precision = 3, scale = 1)
    @DecimalMin("0,0")
    @DecimalMax("10.0")
    private BigDecimal rate;

    @Convert(converter = ReviewConverter.class)
    @Column(name = "review", columnDefinition = "json")
    private List<ReviewDetail> reviews;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status;

    @Column(name = "watch_date")
    private LocalDate watchDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public @DecimalMin("0,0") @DecimalMax("10.0") BigDecimal getRate() {
        return rate;
    }

    public void setRate(@DecimalMin("0,0") @DecimalMax("10.0") BigDecimal rate) {
        this.rate = rate;
    }

    public List<ReviewDetail> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDetail> reviews) {
        this.reviews = reviews;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getWatchDate() {
        return watchDate;
    }

    public void setWatchDate(LocalDate watchDate) {
        this.watchDate = watchDate;
    }

    @Override
    public String toString() {
        return "UserContent{" +
                "id=" + id +
                ", user=" + user +
                ", content=" + content +
                ", rate=" + rate +
                ", reviews=" + reviews +
                ", status=" + status +
                ", watchDate=" + watchDate +
                '}';
    }
}
