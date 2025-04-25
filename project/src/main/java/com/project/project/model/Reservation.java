package com.project.project.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
    @ManyToOne(optional = false)
    @JoinColumn(name = "User_id", referencedColumnName = "id", nullable = false)
    private MyUser User;
    public MyUser getUser(){return User;}
    public void setUser(MyUser User){this.User=User;}

    @ManyToOne(optional = false)
    @JoinColumn(name = "place_id", referencedColumnName = "id", nullable = false)
    private place place;
    public place getPlace(){return place;}
    public void setPlace(place place){this.place=place;}
   @Column(name="date", nullable = false)
   private String date;
   @Column(name="time", nullable = false)
   private String time;
    @Column(name="partysize", nullable = false)
    private String partysize;

   
   
 
    public String getDate(){return date;}   
    public void setDate(String date){this.date=date;}

    public String getTime(){return time;}
    public void setTime(String time){this.time=time;}

    public String partysize(){return partysize;}
    public void setPartysize(String partysize){this.partysize=partysize;}
}
