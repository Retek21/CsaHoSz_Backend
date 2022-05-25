package com.bme.aut.CsaHoSz.controller;

import com.bme.aut.CsaHoSz.domain.IMock;
import com.bme.aut.CsaHoSz.domain.Training;
import com.bme.aut.CsaHoSz.domain.User.User;
import com.bme.aut.CsaHoSz.repositories.ITrainingRepository;
import com.bme.aut.CsaHoSz.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/training")
public class TrainingController {

    @Autowired
    private ITrainingRepository trainingRepository;

    @Autowired
    private IUserRepository userRepository;

    @GetMapping("/training_list")
    public ResponseEntity<List<Training>> getTrainingList(){
        List<Training> trainings = trainingRepository.findAll();
        if(trainings != null) {
            trainings.stream().forEach(training -> {training.hidePasswords();});
            return new ResponseEntity<List<Training>>(trainings, HttpStatus.OK);
        }
        else
            return new ResponseEntity<List<Training>>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/add_training")
    public HttpStatus addTraining(@RequestParam(name="date", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date date,
                                  @RequestParam(name="coach_username", required = true) String coachUserName){
        if (date != null && coachUserName != null){
            User u = userRepository.findDistinctByUserName(coachUserName);
            if(u != null){
                if(u.getRole().equals("ROLE_COACH")){
                    Training training = new Training();
                    training.setCoach(u);
                    training.setDate(date);
                        trainingRepository.saveAndFlush(training);
                    return HttpStatus.OK;
                }
                else
                    return HttpStatus.NOT_ACCEPTABLE;
            }
            else
                return HttpStatus.NOT_FOUND;
        }
        else
            return HttpStatus.NOT_ACCEPTABLE;
    }

    @PostMapping("/remove_training")
    public HttpStatus removeTraining(@RequestParam(name="date", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date date,
                                     @RequestParam(name="coach_username", required = true) String coachUserName){
        if (date != null && coachUserName != null){
            User coach = userRepository.findDistinctByUserName(coachUserName);
            Training training = trainingRepository.findDistinctByDateAndCoach(date, coach);
            if(coach != null && training != null){
                trainingRepository.delete(training);
                return HttpStatus.OK;
            }
            else
                return HttpStatus.NOT_FOUND;
        }
        else
            return HttpStatus.NOT_ACCEPTABLE;
    }

    @PostMapping("/add_participant")
    public HttpStatus addParticipant(@RequestParam(name="date", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date date,
                                     @RequestParam(name="coach_username", required = true) String coachUserName,
                                     @RequestParam(name="participant_username", required = true) String participantUserName,
                                     Principal principal){
        if(principal.getName().equals(participantUserName)){
            if(date != null && participantUserName != null && coachUserName != null){
                User participant = userRepository.findDistinctByUserName(participantUserName);
                User coach = userRepository.findDistinctByUserName(coachUserName);
                Training training = trainingRepository.findDistinctByDateAndCoach(date, coach);
                if(participant != null && training != null){
                    training.addParticipant(participant);
                    trainingRepository.saveAndFlush(training);
                    return HttpStatus.OK;
                }
                else
                    return HttpStatus.NOT_FOUND;
            }
            else
                return HttpStatus.NOT_ACCEPTABLE;
        }
        else return HttpStatus.FORBIDDEN;
    }


    @PostMapping("/remove_participant")
    public HttpStatus removeParticipant(@RequestParam(name="date", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date date,
                                        @RequestParam(name="coach_username", required = true) String coachUserName,
                                        @RequestParam(name="participant_username", required = true) String participantUserName,
                                        Principal principal){
        if(principal.getName().equals(participantUserName)){
            if(date != null && participantUserName != null && coachUserName != null){
                User participant = userRepository.findDistinctByUserName(participantUserName);
                User coach = userRepository.findDistinctByUserName(coachUserName);
                Training training = trainingRepository.findDistinctByDateAndCoach(date, coach);
                if(participant != null && training != null){
                    training.removeParticipant(participant);
                    trainingRepository.saveAndFlush(training);
                    return HttpStatus.OK;
                }
                else
                    return HttpStatus.NOT_FOUND;
            }
            else
                return HttpStatus.NOT_ACCEPTABLE;
        }
        else
            return HttpStatus.FORBIDDEN;

    }


}
