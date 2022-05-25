package com.bme.aut.CsaHoSz.domain.User;

import com.bme.aut.CsaHoSz.domain.IMock;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
public class User implements IMock{

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty
    @Column(unique=true)
    @Size(min=2, max=64)
    private String userName;

    @NotEmpty
    @Size(min=2, max=64)
    private String name;

    @Email
    @NotEmpty
    @Column(unique=true)
    @Size(min=2, max=64)
    private String email;


    private boolean newsletterSubscription;

    @NotEmpty
    @Size(min=8, max=64)
    private String password;

    @NotEmpty
    private String role;

    public Long getId(){return id;}

    public void setId(Long id){this.id=id;}

    public String getName(){return name;}

    public void setName(String name){this.name=name;}

    public String getUserName(){return userName;}

    public void setUserName(String userName){this.userName=userName;}

    public String getEmail(){return email;}

    public void setEmail(String email){this.email=email;}

    public String getPassword(){return password;}

    public void setPassword(String password){this.password=password;}

    public boolean getNewsletterSubscription() {return newsletterSubscription;}

    public void setNewsletterSubscription(boolean newsletterSubscription) {this.newsletterSubscription = newsletterSubscription;}

    public String getRole(){return role;}

    public void setRole(String role){this.role=role;}

    @Override
    public void hidePasswords(){
        this.password = "HIDDEN_PASSWORD";
    }

}
