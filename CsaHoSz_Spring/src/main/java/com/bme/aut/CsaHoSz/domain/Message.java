package com.bme.aut.CsaHoSz.domain;

import com.bme.aut.CsaHoSz.domain.User.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Message implements IMock{

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @ManyToOne
    private User sender;

    @NotNull
    @ManyToOne
    private User receiver;

    @NotEmpty
    @Size(min=5, max=100)
    private String messageBody;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    @Override
    public void hidePasswords(){
        sender.hidePasswords();
        receiver.hidePasswords();
    }
}
