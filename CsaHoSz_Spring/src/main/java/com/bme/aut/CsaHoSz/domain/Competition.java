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
public class Competition implements IMock {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty
    @Size(min=5, max=100)
    @Column(unique = true)
    private String name;

    @NotNull
    @Min(1)
    private int cost;

    @ManyToMany
    @NotNull
    private List<User> participants;

    public Competition(){
        participants = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public User getParticipantAt(int i){
        return participants.get(i);
    }

    public void addParticipant(User u){
        participants.add(u);
    }

    public boolean removeParticipant(User u){
        return participants.remove(u);
    }

    @Override
    public void hidePasswords(){
        participants.stream().forEach(participant ->{
            participant.hidePasswords();
        });
    }

}
