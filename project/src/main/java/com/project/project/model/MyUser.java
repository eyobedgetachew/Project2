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
import jakarta.persistence.Table;

@Entity
@Table(name = "my_user")
public class MyUser {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", nullable=false)
    private Long id;
    @Column(name="username", nullable=false, unique=true)
   private String username;
   @Column(name="password", nullable=false, length =1000)
   private String password;
   @Column(name="email", nullable=false, unique=true, length =320)
   private String email;
@Column(name="first_name", nullable=false, length = 50)
private String first_name;

public String getFirstname(){return first_name;}
public void setFirstname(String first_name){this.first_name=first_name;}
   
@Column(name="last_name", nullable=false, length = 50)
private String last_name;

public String getLastname(){return last_name;}
public void setLastname(String last_name){this.last_name=last_name;}

public String getEmail(){return email;}
   public void setEmail(String email){this.email = email;}
   
 public String getPassword(){return password;}

   public void setPassword(String password){this.password= password;}
   
   public String getUsername(){return username;}
   public void setUsername(String username){this.username =username;}
   public Long getId(){return id;
    }
    public void setId(Long id){this.id = id;
    }   
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Reservation> reservation = new ArrayList<>();
public List<Reservation> getReservation(){return reservation;}
public void setReservation(List<Reservation> reservation){this.reservation=reservation;}
}
