package com.example.demo.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class Location {
    @Autowired(required = true)
    RestTemplate restTemplate;
    public String countryUrl="http://paytm-staging.axisrooms.com/api/be/getAllLocation?countryId=1";
    public String stateUrl="http://paytm-staging.axisrooms.com/api/be/getAllLocation?stateId=90";
    public  String cityUrl="http://paytm-staging.axisrooms.com/api/be/getAllLocation?cityId=1870";

    public String getState(String state) {
        String RESPONSE_DATA = getRestTemplate(countryUrl);
        JSONObject obj = new JSONObject(RESPONSE_DATA);
        JSONArray arr = obj.getJSONArray("locations");
        Map<Long,String> stateMap=new HashMap<>();
        Long stateId=0l;
        for (int i = 0; i < arr.length(); i++)
        {
            stateMap.put(arr.getJSONObject(i).getLong("stateId"),arr.getJSONObject(i).getString("state"));
        }
        for (Map.Entry<Long, String> entry : stateMap.entrySet()) {
            if (entry.getValue().equals(state)) {
                stateId=entry.getKey();
            }
        }
         return stateId.toString();
    }
    public String getCity(String city) {
        String RESPONSE_DATA = getRestTemplate(stateUrl);
        JSONObject obj = new JSONObject(RESPONSE_DATA);
        JSONArray arr = obj.getJSONArray("locations");
        Map<Long,String> cityMap=new HashMap<>();
        Long cityId=0l;
        for (int i = 0; i < arr.length(); i++)
        {
            cityMap.put(arr.getJSONObject(i).getLong("cityId"),arr.getJSONObject(i).getString("city"));
           // System.out.println(cityMap);
        }
        for (Map.Entry<Long, String> entry : cityMap.entrySet()) {
            if (entry.getValue().equals(city)) {
                cityId=entry.getKey();
            }
        }
        return cityId.toString();
    }
    public String getLocations(String location) {
        String RESPONSE_DATA = getRestTemplate(cityUrl);
        JSONObject obj = new JSONObject(RESPONSE_DATA);
        JSONArray arr = obj.getJSONArray("locations");
        Map<Long,String> locationMap=new HashMap<>();
        Long locationId=0l;
        for (int i = 0; i < arr.length(); i++)
        {
            locationMap.put(arr.getJSONObject(i).getLong("locationId"),arr.getJSONObject(i).getString("location"));
        }
        for (Map.Entry<Long, String> entry : locationMap.entrySet()) {
            if (entry.getValue().equals(location)) {
                locationId=entry.getKey();
            }
        }
        return locationId.toString();
    }

    private String getRestTemplate(String URL){
        HttpHeaders headers=new HttpHeaders();
        headers.add("apikey", "6ac26a4cb838a51fb5406b724ba2c43c4110f133");
        headers.add("channelid", "9");
        headers.add("access_key", "c6533c7cf985b3e4f6f84eaa5ac54a263ba9f1fdb4bd0f915d891d33e5b4b64e");
        HttpEntity request = new HttpEntity<>(headers);
        ResponseEntity<String> responseData = restTemplate.exchange(URL, HttpMethod.GET,request, String.class);
        return responseData.getBody();
    }
}

