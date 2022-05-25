package com.bme.aut.CsaHoSz.controller;

import com.bme.aut.CsaHoSz.domain.*;
import com.bme.aut.CsaHoSz.domain.User.User;
import com.bme.aut.CsaHoSz.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IMessageRepository messageRepository;

    @Autowired
    private ITrainingRepository trainingRepository;

    @Autowired
    private ICostRepository costRepository;

    @Autowired
    private ICompetitionRepository competitionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/user_list")
    @ResponseBody
    public ResponseEntity<List<User>> getUserList(){
        List<User> users = userRepository.findAll();
        if(users != null) {
            users.stream().forEach(user -> {user.hidePasswords();});
            return new ResponseEntity<List<User>>(users, HttpStatus.OK);
        }
        else
            return new ResponseEntity<List<User>>(HttpStatus.NOT_FOUND);

    }

    @GetMapping("{username}")
    @ResponseBody
    public ResponseEntity<User> getUser(@PathVariable String username){
        User user = userRepository.findDistinctByUserName(username);
        if(user == null)
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        else {
            user.hidePasswords();
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }
    }

    @GetMapping("current_user")
    @ResponseBody
    public ResponseEntity<User> getUser(Principal principal){
        User user = userRepository.findDistinctByUserName(principal.getName());
        if(user == null)
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        else {
            user.hidePasswords();
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }
    }

    @PostMapping("/remove/{username}")
    public HttpStatus removeUser(@PathVariable String username, Principal principal){
        if(username != null){
            if(username.equals(principal.getName())) {
                User user = userRepository.findDistinctByUserName(username);
                List<Message> sentMessages = messageRepository.findAllBySender(user);
                List<Message> receivedMessages = messageRepository.findAllByReceiver(user);
                List<Training> trainings = trainingRepository.findAll();
                List<Cost> costs = costRepository.findAll();
                List<Competition> competitions = competitionRepository.findAll();
                //Deletes All the existing records connected to the user as well
                //Architecture with Spring Data Cascading in mind would have been better
                if(user != null){
                    if(sentMessages != null)
                        sentMessages.stream().forEach(message -> messageRepository.delete(message));
                    if(receivedMessages != null)
                        receivedMessages.stream().forEach(message -> messageRepository.delete(message));
                    if(trainings != null)
                        trainings.stream().forEach(training -> {
                            if(training.getCoach().equals(user))
                                trainingRepository.delete(training);
                            else {
                                for(int i=0; i<training.getParticipantsSize(); i++){
                                    if(training.getParticipantAt(i).equals(user)){
                                        training.removeParticipant(user);
                                        break;
                                    }
                                }
                                trainingRepository.saveAndFlush(training);
                            }
                        });
                    if(costs != null)
                        costs.stream().forEach(cost -> {
                            List<User> appliedUsers = cost.getAppliedUsers();
                            for(int i=0; i<appliedUsers.size(); i++){
                                String appliedUser = appliedUsers.get(i).getUserName();
                                if(appliedUser.equals(user.getUserName())){
                                    cost.removeAppliedUser(user);
                                    break;
                                }
                            }
                            costRepository.saveAndFlush(cost);
                        });
                    if(competitions != null)
                        competitions.stream().forEach(competition -> {
                            List<User> participants = competition.getParticipants();
                            for(int i=0; i<participants.size(); i++){
                                String participant = participants.get(i).getUserName();
                                if(participant.equals(user.getUserName())){
                                    competition.removeParticipant(user);
                                    break;
                                }
                            }
                            competitionRepository.saveAndFlush(competition);
                        });
                    userRepository.delete(user);
                    return HttpStatus.OK;
                }
                else
                    return HttpStatus.NOT_FOUND;
            }
            else
                return HttpStatus.FORBIDDEN;
        }
        else
            return HttpStatus.NOT_ACCEPTABLE;
    }

    @PatchMapping("/update/{userName}")
    public HttpStatus updateUser(@PathVariable String userName,
                                 @RequestParam(name="username", required = false) String newUserName,
                                 @RequestParam(name="name", required = false) String name,
                                 @RequestParam(name="email", required = false) String email,
                                 @RequestParam(name="password", required = false) String password,
                                 @RequestParam(name="newsletter", required = false) String newsletter,
                                 Principal principal){
        User user = userRepository.findDistinctByUserName(userName);
        if(principal.getName().equals(userName)){
            if(user != null){
                if(newUserName!=null) {
                    if (userRepository.findDistinctByUserName(newUserName) == null)
                        user.setUserName(newUserName);
                    else
                        return HttpStatus.CONFLICT;
                }
                if(email!=null) {
                    if (userRepository.findDistinctByEmail(email) == null)
                        user.setEmail(email);
                    else
                        return HttpStatus.CONFLICT;
                }
                if(name!=null)
                    user.setName(name);
                if(password!=null)
                    user.setPassword(passwordEncoder.encode(password));
                if(newsletter!=null) {
                    if (newsletter.toLowerCase().equals("true"))
                        user.setNewsletterSubscription(true);
                    else
                        user.setNewsletterSubscription(false);
                }

                userRepository.saveAndFlush(user);
                return HttpStatus.OK;
            }
            else
                return HttpStatus.NOT_FOUND;
        }
        else
            return HttpStatus.FORBIDDEN;

    }

    @PatchMapping("/update_role/{username}")
    public HttpStatus updateUserRole(@PathVariable(name="username", required = true) String userName,
                                        @RequestParam(name="role", required = true) String newRole){
        if(Roles.isValid(newRole)){
            User user = userRepository.findDistinctByUserName(userName);
            if(user != null) {
                user.setRole(newRole);
                userRepository.saveAndFlush(user);
                return HttpStatus.OK;
            }
            else
                return HttpStatus.NOT_FOUND;
        }
        else
            return HttpStatus.NOT_ACCEPTABLE;
    }

    @PostMapping("/add_member")
    public HttpStatus addMember(@RequestParam(name="username", required = true) String username,
                                        @RequestParam(name="name", required = true) String name,
                                        @RequestParam(name="email", required = true) String email,
                                        @RequestParam(name="password", required = true) String password,
                                        @RequestParam(name="newsletter", required = true) String newsletter){

        return buildUser(username, name, email, password, newsletter, "ROLE_MEMBER");
    }

    @PostMapping("/add_coach")
    public HttpStatus addCoach(@RequestParam(name="username", required = true) String username,
                                   @RequestParam(name="name", required = true) String name,
                                   @RequestParam(name="email", required = true) String email,
                                   @RequestParam(name="password", required = true) String password,
                                   @RequestParam(name="newsletter", required = true) String newsletter){

        return buildUser(username, name, email, password, newsletter, "ROLE_COACH");
    }

    @PostMapping("/add_admin")
    public HttpStatus addAdmin(@RequestParam(name="username", required = true) String username,
                                   @RequestParam(name="name", required = true) String name,
                                   @RequestParam(name="email", required = true) String email,
                                   @RequestParam(name="password", required = true) String password,
                                   @RequestParam(name="newsletter", required = true) String newsletter){

        return buildUser(username, name, email, password, newsletter, "ROLE_ADMIN");
    }


    private HttpStatus buildUser(String userName, String name, String email,
                                           String password, String newsletter, String role){
        User user = userRepository.findDistinctByUserName(userName);

        if (user != null)
            return HttpStatus.CONFLICT;

        if(2 > userName.length() || userName.length() > 64 ||
                2 > name.length() || name.length() > 64 ||
                2 > email.length() || email.length() > 64 ||
                8 > password.length() || password.length() > 64)
            return HttpStatus.NOT_ACCEPTABLE;

        if(!Roles.isValid(role))
            return HttpStatus.NOT_ACCEPTABLE;

        user = new User();
        user.setUserName(userName);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        if(newsletter.toLowerCase().equals("true"))
            user.setNewsletterSubscription(true);
        else
            user.setNewsletterSubscription(false);
        user.setRole(role);
        userRepository.saveAndFlush(user);
        return HttpStatus.OK;
    }

}
