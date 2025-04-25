package com.project.project.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;



@Entity
@Table(name="place")
public class place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id", nullable = false)
    private Long id;

    @Column(name= "names", nullable = false, unique = true)
    private String name;

    @Column(name= "description", nullable=false)
    private String description;

    public String getName(){
        return name;}
    public void setName(String name){
        this.name =name;
    }
    public String getDescription (){return description;}
    public void setDescription(String description){
        this.description = description;
    }
    public Long getId(){return id;}
    public void setID(Long id ){this.id=id;}

@OneToOne(mappedBy = "place", cascade = CascadeType.REMOVE, orphanRemoval = true)
private Menu menu;
public Menu getMenu(){return menu;}
public void setMenu(Menu menu){this.menu=menu;}
@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Reservation> reservation = new ArrayList<>();
public List<Reservation> getReservation(){return reservation;}
public void setReservation(List<Reservation> reservation){this.reservation=reservation;}
}
