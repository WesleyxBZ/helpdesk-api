package com.wesleyxbz.helpdesk.api.entity;

import com.wesleyxbz.helpdesk.api.enums.PriorityEnum;
import com.wesleyxbz.helpdesk.api.enums.StatusEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TICKET")
public class Ticket implements Serializable {

    private static final Long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user_person_fk")
    private User user;

    private Date date;
    private String title;
    private Integer number;
    private StatusEnum status;
    private PriorityEnum priority;

    @ManyToOne
    @JoinColumn(name = "id_assigned_user_fk")
    private User assignedUser;

    private String description;
    private String image;

    @Transient
    private List<ChangeStatus> changes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public PriorityEnum getPriority() {
        return priority;
    }

    public void setPriority(PriorityEnum priority) {
        this.priority = priority;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ChangeStatus> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeStatus> changes) {
        this.changes = changes;
    }

}
