package com.project.project.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false )
    private Long id;

@OneToOne(optional =false, orphanRemoval = true)
@JoinColumn(name = "place_id", referencedColumnName = "id", nullable=false, unique=true)
private place place;
@Column(name="Item",nullable = false)
private String item;
@Column(name="Ingredients",nullable = false)
private String ingredients;
@Column(name="Price",nullable = false)
private String price;

public place getplace(){return place;}
public void setplace(place place){this.place=place;}
public String getItem(){return item;}
public void setItem(String item){this.item=item;}
public String getIngredients(){return ingredients;}
public void setIngredients(String ingredients){this.ingredients=ingredients;}
public String getPrice(){return price;}
public void setPrice(String price){this.price=price;}
public Long getId(){return id;}
public void setId(Long id){this.id=id;}
}
