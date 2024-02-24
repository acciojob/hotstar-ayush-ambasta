package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User saveUser = userRepository.save(user);
        return saveUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        Optional<User>optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            return 0;
        }
        int age = optionalUser.get().getAge();
        SubscriptionType subscriptionType = optionalUser.get().getSubscription().getSubscriptionType();
        int rank;

        if(subscriptionType==SubscriptionType.ELITE){
            rank=1;
        }else if(subscriptionType==SubscriptionType.PRO){
            rank=2;
        }else{
            rank=3;
        }
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

        int count = 0;
        for(WebSeries webSeries:webSeriesList){
            int ageLimit = webSeries.getAgeLimit();
            SubscriptionType type = webSeries.getSubscriptionType();
            int reqRank;

            if(type==SubscriptionType.ELITE){
                reqRank=1;
            }else if(type==SubscriptionType.PRO){
                reqRank=2;
            }else{
                reqRank=3;
            }

            if(ageLimit<=age && rank<=reqRank){
                count++;
            }
        }
        return count;
    }


}
