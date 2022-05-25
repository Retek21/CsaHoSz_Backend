package com.bme.aut.CsaHoSz.controller;

import com.bme.aut.CsaHoSz.domain.Cost;
import com.bme.aut.CsaHoSz.domain.IMock;
import com.bme.aut.CsaHoSz.domain.User.User;
import com.bme.aut.CsaHoSz.repositories.ICostRepository;
import com.bme.aut.CsaHoSz.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cost")
public class CostController {

    @Autowired
    ICostRepository costRepository;

    @Autowired
    IUserRepository userRepository;


    @GetMapping("{username}")
    public ResponseEntity<List<Cost>> getUserCosts(@PathVariable String userName){
        if(userName != null){
            User user = userRepository.findDistinctByUserName(userName);
            List<Cost> costs = costRepository.findAll();
            if(user != null && costs != null){
                List<Cost> userCosts = new ArrayList<>();
                costs.stream().forEach(cost -> {
                    List<User> appliedUsers = cost.getAppliedUsers();
                    for(int i = 0; i < appliedUsers.size(); i++){
                        String currentUserName = appliedUsers.get(i).getUserName();
                        if(currentUserName.equals(userName)){
                            userCosts.add(cost);
                            break;
                        }
                    }
                });
                return new ResponseEntity<List<Cost>>(userCosts, HttpStatus.OK);
            }
            else
                return new ResponseEntity<List<Cost>>(HttpStatus.NOT_FOUND);
        }
        else
            return new ResponseEntity<List<Cost>>(HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("{description}")
    public ResponseEntity<Cost> getCost(@PathVariable String description){
        if(description != null){
            Cost cost = costRepository.findDistinctByDescription(description);
            if(cost != null){
                cost.hidePasswords();
                return new ResponseEntity<Cost>(cost, HttpStatus.OK);
            }
            else
                return new ResponseEntity<Cost>(HttpStatus.NOT_FOUND);
        }
        else
            return new ResponseEntity<Cost>(HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("cost_list")
    public ResponseEntity<List<Cost>> getAllCosts(){
        List<Cost> costs = costRepository.findAll();
            if(costs != null){
                costs.stream().forEach(cost->{cost.hidePasswords();});
                return new ResponseEntity<List<Cost>>(costs, HttpStatus.OK);
            }
            else
                return new ResponseEntity<List<Cost>>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("new_cost")
    public HttpStatus addCost(@RequestParam(name="price") long price,
                              @RequestParam(name="description") String description){
        if(description!=null && 5 <= description.length() && description.length() <= 150  && price >= 1){
            Cost cost = new Cost();
            cost.setPrice(price);
            cost.setDescription(description);
            costRepository.saveAndFlush(cost);
            return HttpStatus.OK;
        }
        else
            return HttpStatus.NOT_ACCEPTABLE;
    }

    @PatchMapping("/update/{description}")
    public HttpStatus changeCost(@PathVariable String description,
                                           @RequestParam(name="price", required = false) Long price,
                                           @RequestParam(name="description",required = false) String newDescription){
        if(description != null && 5 <= description.length() && description.length() <= 150 && (price == null || price >= 1)){
            Cost cost = costRepository.findDistinctByDescription(description);
            if(cost != null){
                if(price != null){
                    cost.setPrice(price);
                }
                if(newDescription != null){
                    if(costRepository.findDistinctByDescription(newDescription) == null){
                        cost.setDescription(description);
                    }
                    else
                        return HttpStatus.CONFLICT;
                }
                costRepository.saveAndFlush(cost);
                return HttpStatus.OK;
            }
            else
                return HttpStatus.NOT_FOUND;
        }
        else
            return HttpStatus.NOT_ACCEPTABLE;
    }
}
