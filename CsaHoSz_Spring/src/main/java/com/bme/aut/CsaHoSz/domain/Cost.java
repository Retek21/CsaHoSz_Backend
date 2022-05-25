package com.bme.aut.CsaHoSz.domain;

import com.bme.aut.CsaHoSz.domain.User.User;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cost implements IMock {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(min=5, max=100)
    @Column(unique = true)
    private String description;

    @NotNull
    @Min(1)
    private Long price;

    @NotNull
    @ManyToMany
    private List<User> appliedUsers;

    public Cost(){
        appliedUsers = new ArrayList<User>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public int getAppliedUsersSize(){
        return appliedUsers.size();
    }

    public User getAppliedUserAt(int i){
        return appliedUsers.get(i);
    }

    public void addAppliedUser(User u){
        appliedUsers.add(u);
    }

    public boolean removeAppliedUser(User u){
        return appliedUsers.remove(u);
    }

    public void clearAppliedUsers(){
        appliedUsers.clear();
    }

    public List<User> getAppliedUsers() {
        return appliedUsers;
    }

    public void setAppliedUsers(List<User> appliedUsers) {
        this.appliedUsers = appliedUsers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void hidePasswords(){
        appliedUsers.stream().forEach(appliedUser ->{
            appliedUser.hidePasswords();
        });
    }
}
