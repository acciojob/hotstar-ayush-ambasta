package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        int userId = subscriptionEntryDto.getUserId();
        Optional<User> optionalUser = userRepository.findById(userId);

        if(optionalUser.isEmpty()){
            return 0;
        }
        int noOfScreensRequired = subscriptionEntryDto.getNoOfScreensRequired();
        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();

        int totalAmountPaid;

        if(subscriptionType==SubscriptionType.BASIC){
            totalAmountPaid = 500+200*noOfScreensRequired;
        }else if (subscriptionType==SubscriptionType.PRO) {
            totalAmountPaid = 800+250*noOfScreensRequired;
        }else{
            totalAmountPaid = 1000+350*noOfScreensRequired;
        }

        Date startSubscriptionDate = new Date();

        Subscription savedSubscription = new Subscription(subscriptionType,noOfScreensRequired,startSubscriptionDate,totalAmountPaid);



        subscriptionRepository.save(savedSubscription);
        userRepository.save(optionalUser.get());
        return totalAmountPaid;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository


        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            return null;
        }
        Subscription subscription = optionalUser.get().getSubscription();

        int noOfScreensSubscribed = subscription.getNoOfScreensSubscribed();
        int totalAmountPaid = subscription.getTotalAmountPaid();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();
        int difference;
        int AmountToPay;

        if(subscriptionType==SubscriptionType.ELITE){
            throw  new Exception("Already the best Subscription");
        }else if(subscriptionType==SubscriptionType.PRO) {
            AmountToPay = 1000+350*noOfScreensSubscribed;
            subscription.setSubscriptionType(SubscriptionType.ELITE);

        }else{
            AmountToPay = 800+250*noOfScreensSubscribed;
            subscription.setSubscriptionType(SubscriptionType.PRO);
        }

        difference = AmountToPay-totalAmountPaid;
        subscription.setTotalAmountPaid(AmountToPay);

        subscriptionRepository.save(subscription);
        userRepository.save(optionalUser.get());

        return difference;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription>subscriptionList=subscriptionRepository.findAll();

        int totalRevenue = 0;
        for(Subscription subscription:subscriptionList){
            totalRevenue  = totalRevenue + subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
