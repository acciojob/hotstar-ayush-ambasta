package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.SubscriptionType;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        String seriesName = webSeriesEntryDto.getSeriesName();
        int ageLimit = webSeriesEntryDto.getAgeLimit();
        double rating = webSeriesEntryDto.getRating();
        SubscriptionType subscriptionType = webSeriesEntryDto.getSubscriptionType();
        int productionHouseId = webSeriesEntryDto.getProductionHouseId();

        WebSeries webSeries = webSeriesRepository.findBySeriesName(seriesName);

        if(webSeries!=null){
            throw new Exception("Series is already present");
        }else{
            Optional<ProductionHouse> optionalProductionHouse = productionHouseRepository.findById(productionHouseId);
            ProductionHouse productionHouse = optionalProductionHouse.get();
            List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
            int count = webSeriesList.size();
            double oldRating = productionHouse.getRatings();
            double newRating = (double)(count*oldRating + rating)/(count+1);


            WebSeries newWebseries = new WebSeries(seriesName,ageLimit,rating,subscriptionType);
            webSeriesList.add(newWebseries);
            productionHouse.setWebSeriesList(webSeriesList);
            productionHouse.setRatings(newRating);

            productionHouseRepository.save(productionHouse);
            WebSeries saveWebSeries = webSeriesRepository.save(newWebseries);
            return saveWebSeries.getId();
        }

    }

}
