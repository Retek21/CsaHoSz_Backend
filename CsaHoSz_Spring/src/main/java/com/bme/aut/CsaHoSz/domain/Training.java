package com.bme.aut.CsaHoSz.domain;

import com.bme.aut.CsaHoSz.domain.User.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Training implements IMock{

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    Date date;

    @NotNull
    @ManyToMany
    List<User> participants;

    @NotNull
    @ManyToOne
    private User coach;

    public Training(){
        participants = new ArrayList<User>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getParticipantsSize(){
        return participants.size();
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

    public void clearParticipants(){
        participants.clear();
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void setCoach(User u){
        coach = u;
    }

    public User getCoach(){
        return coach;
    }

    @Override
    public void hidePasswords(){
        coach.hidePasswords();
        participants.stream().forEach(participant ->{
            participant.hidePasswords();
        });
    }

}
