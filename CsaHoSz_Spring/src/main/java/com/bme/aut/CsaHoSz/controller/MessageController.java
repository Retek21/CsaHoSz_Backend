package com.bme.aut.CsaHoSz.controller;

import com.bme.aut.CsaHoSz.domain.IMock;
import com.bme.aut.CsaHoSz.domain.Message;
import com.bme.aut.CsaHoSz.domain.User.User;
import com.bme.aut.CsaHoSz.repositories.IMessageRepository;
import com.bme.aut.CsaHoSz.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IMessageRepository messageRepository;

    @GetMapping("{userName}/incoming")
    public ResponseEntity<List<Message>> getIncomingMessages(@PathVariable String userName,
                                                           Principal principal){
        if(userName != null){
            if(userName.equals(principal.getName())){
                User receiver = userRepository.findDistinctByUserName(userName);
                List<Message> messages = messageRepository.findAllByReceiver(receiver);
                if(messages != null){
                    messages.stream().forEach(message -> {message.hidePasswords();});
                    return new ResponseEntity<List<Message>>(messages, HttpStatus.OK);
                }
                else
                    return new ResponseEntity<List<Message>>(HttpStatus.NOT_FOUND);
            }
            else return new ResponseEntity<List<Message>>(HttpStatus.FORBIDDEN);
        }
        else
            return new ResponseEntity<List<Message>>(HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("{userName}/outgoing")
    public ResponseEntity<List<Message>> getOutgoingMessages(@PathVariable String userName,
                                                           Principal principal){
        if(userName != null){
            if(userName.equals(principal.getName())){
                User sender = userRepository.findDistinctByUserName(userName);
                List<Message> messages = messageRepository.findAllBySender(sender);
                if(messages != null){
                    messages.stream().forEach(message -> {message.hidePasswords();});
                    return new ResponseEntity<List<Message>>(messages, HttpStatus.OK);
                }
                else
                    return new ResponseEntity<List<Message>>(HttpStatus.NOT_FOUND);
            }
            else
                return new ResponseEntity<List<Message>>(HttpStatus.FORBIDDEN);
        }
        else
            return new ResponseEntity<List<Message>>(HttpStatus.NOT_ACCEPTABLE);
    }

    @PostMapping("{senderUserName}/new_message")
    public HttpStatus sendMessage(@PathVariable String senderUserName,
                                  @RequestParam(name="msg_body", required = true) String msgBody,
                                  @RequestParam(name="receiver_username", required = true) String receiverUserName,
                                  Principal principal){
        if(!senderUserName.equals(receiverUserName)
                && senderUserName != null
                && msgBody != null
                && 5 <= msgBody.length() && msgBody.length() <= 150
                && receiverUserName != null){
            if(senderUserName.equals(principal.getName())){
                User sender = userRepository.findDistinctByUserName(senderUserName);
                User receiver = userRepository.findDistinctByUserName(receiverUserName);
                if(sender != null && receiver != null){
                    Message message = new Message();
                    message.setMessageBody(msgBody);
                    message.setSender(sender);
                    message.setReceiver(receiver);
                    messageRepository.saveAndFlush(message);
                    return HttpStatus.OK;
                }
                else
                    return HttpStatus.NOT_FOUND;
            }
            else return HttpStatus.FORBIDDEN;
        }
        else
            return HttpStatus.NOT_ACCEPTABLE;
    }


}
