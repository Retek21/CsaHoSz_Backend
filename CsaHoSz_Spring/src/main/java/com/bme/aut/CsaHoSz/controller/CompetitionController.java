package com.bme.aut.CsaHoSz.controller;

import com.bme.aut.CsaHoSz.domain.Competition;
import com.bme.aut.CsaHoSz.domain.User.User;
import com.bme.aut.CsaHoSz.repositories.ICompetitionRepository;
import com.bme.aut.CsaHoSz.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/competition")
public class CompetitionController{

    @Autowired
    ICompetitionRepository competitionRepository;

    @Autowired
    IUserRepository userRepository;

    @GetMapping("competition_list")
    public ResponseEntity<List<Competition>> getCompetitionList(){
        List<Competition> competitions = competitionRepository.findAll();
        if(competitions != null){
            competitions.stream().forEach(competition -> {competition.hidePasswords();});
            return new ResponseEntity<List<Competition>>(competitions, HttpStatus.OK);
        }
        else
            return new ResponseEntity<List<Competition>>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("{competitionName}")
    public ResponseEntity<Competition> getCompetition(@PathVariable String name){
        Competition competition = competitionRepository.findDistinctByName(name);
        if(competition != null){
            competition.hidePasswords();
            return new ResponseEntity<Competition>(competition, HttpStatus.OK);
        }
        else
            return new ResponseEntity<Competition>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("new_competition")
    public HttpStatus addCompetition(@RequestParam(name="competition_name")String name,
                                     @RequestParam(name="cost")Integer cost){
            if (name != null && 5 <= name.length() && name.length() <= 150 && cost >= 1) {
                if (competitionRepository.findDistinctByName(name) == null) {
                    Competition competition = new Competition();
                    competition.setName(name);
                    competition.setCost(cost);
                    competitionRepository.saveAndFlush(competition);
                    return HttpStatus.OK;
                } else
                    return HttpStatus.CONFLICT;
            } else
                return HttpStatus.NOT_ACCEPTABLE;
        }


    @PostMapping("remove_competition")
    public HttpStatus removeCompetition(@RequestParam(name="competition_name")String name){
        if(name != null){
            Competition competition = competitionRepository.findDistinctByName(name);
            if(competition != null){
                competitionRepository.delete(competition);
                return HttpStatus.OK;
            }
            else
                return HttpStatus.NOT_FOUND;
        }
        else
            return HttpStatus.NOT_ACCEPTABLE;
    }

    @PostMapping("/add_participant")
    public HttpStatus addParticipant(@RequestParam(name="competition_name") String competitionName,
                                     @RequestParam(name="user_name")String userName,
                                     Principal principal){
        if(principal.getName().equals(userName)){
            if(competitionName != null && userName != null){
                User participant = userRepository.findDistinctByUserName(userName);
                Competition competition = competitionRepository.findDistinctByName(competitionName);
                if(participant != null && competition != null){
                    competition.addParticipant(participant);
                    competitionRepository.saveAndFlush(competition);
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

    @PostMapping("/remove_participant")
    public HttpStatus removeParticipant(@RequestParam(name="competition_name") String competitionName,
                                        @RequestParam(name="user_name")String userName,
                                        Principal principal){
        if(principal.getName().equals(userName)){
            if(competitionName != null && userName != null){
                User participant = userRepository.findDistinctByUserName(userName);
                Competition competition = competitionRepository.findDistinctByName(competitionName);
                if(participant != null && competition != null){
                    boolean success = competition.removeParticipant(participant);
                    if(success) {
                        competitionRepository.saveAndFlush(competition);
                        return HttpStatus.OK;
                    }
                    else
                        return HttpStatus.NOT_FOUND;
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

    @PatchMapping("update/{competitionName}")
    public HttpStatus updateCompetition(@PathVariable String competitionName,
                                        @RequestParam(name="cost", required = false)Integer cost,
                                        @RequestParam(name="new_name", required = false)String newName){
            if (competitionName != null && (cost == null || cost >= 1)
                    && (newName == null || (5 <= newName.length() && newName.length() <= 150))) {
                Competition competition = competitionRepository.findDistinctByName(competitionName);
                if (competition != null) {
                    if (newName != null) {
                        Competition conflictingCompetition = competitionRepository.findDistinctByName(newName);
                        if (conflictingCompetition == null) {
                            competition.setName(newName);
                        } else
                            return HttpStatus.CONFLICT;
                    }
                    if (cost != null) {
                        competition.setCost(cost);
                    }
                    competitionRepository.saveAndFlush(competition);
                    return HttpStatus.OK;
                } else
                    return HttpStatus.NOT_FOUND;
            } else
                return HttpStatus.NOT_ACCEPTABLE;

    }

}