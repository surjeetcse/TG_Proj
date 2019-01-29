package com.example.demo.service;

import com.example.demo.generatedclasses.common.WrapperClasses.*;
import com.example.demo.generatedclasses.common.bean.scrunch.Information;
import com.example.demo.generatedclasses.common.bean.scrunch.RoomAmenity;
import com.example.demo.generatedclasses.common.bean.scrunch.RoomInformation;
import com.example.demo.staticdump.model.document.StaticDumpHotel;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.json.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JsonService {
    @Autowired(required = true)
    RestTemplate restTemplate;

    @Autowired(required = true)
    Location location;


    public String FILE_PATH = "/home/axisrooms/Downloads/demo/src/main/resources/TG_dump.json";

    public String CREATE_HOTEL_URL= "https://jckstaging-extranet.axisrooms.com/api/be/createBasicHotel";
    public String ADD_ROOM_URL="https://jckstaging-extranet.axisrooms.com/api/be/addRoom";
    public String HOTEL_AMENITIES_URL="https://jckstaging-extranet.axisrooms.com/api/be/updateHotelAmenities";
    public String HOTEL_POLICY_URL="https://jckstaging-extranet.axisrooms.com/api/be/updateHotelPolicy";
    public  String MAP_LOCATION_URL="https://jckstaging-extranet.axisrooms.com/api/be/updateMapLocation";

    StaticDumpHotel staticDumpHotel[];
    ObjectMapper MAPPER = new ObjectMapper();

    public String jsonToJava() {

        String RESPONSE_DATA=new String();
        try {
            JsonFactory jsonfactory = new JsonFactory();
            JsonParser parser = jsonfactory.createJsonParser(new File(FILE_PATH)); // starting parsing of JSON String
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                FileReader fileReader = new FileReader(new File(FILE_PATH));
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                StringBuffer stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                staticDumpHotel = objectMapper.readValue(stringBuffer.toString(), StaticDumpHotel[].class);
                bufferedReader.close();
                fileReader.close();
            }
            for (StaticDumpHotel sd : staticDumpHotel) {
                RESPONSE_DATA=WriteObjectToFile(sd);
            }
        } catch (Exception e) {
            System.out.println("inside catch 2>" + e);
        }
        return RESPONSE_DATA;
    }

    public String WriteObjectToFile(StaticDumpHotel serObj) {
        String RESPONSE_DATA=new String();
        try {
            // Save Basic Hotel Details
            RESPONSE_DATA=createBasicHotel(serObj);
            String hotelId = RESPONSE_DATA.substring(0, RESPONSE_DATA.indexOf("|"));
            String productId = RESPONSE_DATA.substring(RESPONSE_DATA.indexOf("|")+1, RESPONSE_DATA.length());

            // Save lat and long
            updateMapLocation(serObj, hotelId);

            // Add Room Details
            addRoom(serObj ,productId);

            //Update hotel Amenities
            updateHotelAmenities(serObj,hotelId);

            //update Hotel Policy
            updateHotelPolicy(serObj,hotelId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return RESPONSE_DATA;
    }

    private String sendRestTemplate(String jsonInString,String URL){
        String RESPONSE_DATA=new String();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("apikey", "6ac26a4cb838a51fb5406b724ba2c43c4110f133");
        headers.add("channelId", "1");
        headers.add("access_key", "c6533c7cf985b3e4f6f84eaa5ac54a263ba9f1fdb4bd0f915d891d33e5b4b64e");
        headers.add("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(jsonInString, headers);
        RESPONSE_DATA = restTemplate.postForObject(URL, request, String.class);
        return RESPONSE_DATA;
    }

    private String createBasicHotel(StaticDumpHotel serObj){
        String RESPONSE_DATA=new String();
        String value=new String();
        StaticDumpHotelWrapper sd = new StaticDumpHotelWrapper();
        StaticDumpHotel staticDumpHotel = serObj;
        Information information = staticDumpHotel.getInformation();
        sd.hotelId = "0";
        sd.supplierId = "1863";
        if(information.getName()!=null)sd.hotelName = information.getName();else sd.hotelName="***Not avalable***";
        if(information.getName()!=null)sd.displayName = information.getName();else sd.displayName="***Not avalable***";
        sd.hotelType=new ArrayList<>();
        if(information.getStarRating()!=null)sd.starRating = information.getStarRating();else sd.starRating="***Not avalable***";
        sd.chainName = "118";
        sd.currency = "1";
        if(information.getCheckInTime()!=null)sd.checkInTime = information.getCheckInTime();else sd.checkInTime="***Not avalable***";
        if(information.getCheckOutTime()!=null)sd.checkOutTime = information.getCheckOutTime();else sd.checkOutTime="***Not avalable***";

        sd.country = "1";
        String stateId=location.getState(information.getState());if(stateId!="")sd.state=stateId;else sd.state = "-1";
        String cityId=location.getCity(information.getCity());if(cityId!="")sd.city=cityId;else sd.city = "-1";
        String locationId =location.getLocations(information.getLocation());if(locationId!="")sd.locality=locationId; else sd.locality = "-1";
        if(information.getLocation()!=null)sd.locationName = information.getLocation();else sd.locationName="***Not avalable***";
        if(information.getAddress1()!=null)sd.newLocation = information.getAddress1();else sd.newLocation="***Not avalable***";
        sd.zipcode = "560039";
        if(information.getAddress2()!=null)sd.streetAddress = information.getAddress2();else sd.streetAddress="***Not avalable***";
        if(information.getOverview()!=null)sd.description = information.getOverview();else sd.description="***Not avalable***";
        sd.commission = "10";
        if(information.getVendorCode()!=null)sd.vendorCode = information.getVendorCode();else sd.vendorCode="***Not avalable***";
        if(information.getTripAdvisorRatingId()!=null)sd.tripAdvisorRatingId = information.getTripAdvisorRatingId();else sd.tripAdvisorRatingId="***Not avalable***";
        sd.gstNumber = "-1";
        sd.videoURL = "***Not avalable***";
        sd.propertyTheme=new ArrayList<>();
        if(information.getLatitude()!=null)sd.latitude = information.getLatitude();else sd.latitude="***Not avalable***";
        if(information.getLongitude()!=null)sd.longitude = information.getLongitude();else sd.longitude="***Not avalable***";
        sd.dmid = "1";
        sd.pid = "1";
        sd.sellerName = "";
        if(information.getNoOfRooms()!=null)sd.noOfRooms = information.getNoOfRooms();else sd.noOfRooms="***Not avalable***";
        sd.bookingWindows= Arrays.asList("today");
        sd.propHighlights=new ArrayList<>();
        if(information.getNoOfFloors()!=null)sd.noOfFloors = information.getNoOfFloors();else sd.noOfFloors="***Not avalable***";
        try {
            String jsonInString = MAPPER.writeValueAsString(sd);
            RESPONSE_DATA = sendRestTemplate(jsonInString,CREATE_HOTEL_URL);
            JSONObject obj = new JSONObject(RESPONSE_DATA);
            Long NEW_HOTEL_ID=obj.getJSONObject("result").getLong("hotelId");
            Long NEW_PRODUCT_ID = obj.getJSONObject("result").getLong("productId");

             value=NEW_HOTEL_ID+"|"+NEW_PRODUCT_ID;
        }catch (Exception e){e.printStackTrace(); }
    return  value;
    }

    private void addRoom(StaticDumpHotel serObj,String productId){
        AddRoomWrapper addRoomWrapper = new AddRoomWrapper();
        StaticDumpHotel staticDumpHotel = serObj;
        Information information = staticDumpHotel.getInformation();
        List<RoomInformation> list=serObj.getRoomInformations();
        for (RoomInformation roomInformation:list
             ) {
            addRoomWrapper.id=0;
              addRoomWrapper.productId=Long.parseLong(productId);
            if(roomInformation.getRoomType()!=null) addRoomWrapper.displayName=roomInformation.getRoomType();else addRoomWrapper.displayName="***Not avalable***";
            addRoomWrapper.userid="1848";
            if(roomInformation.getRoomType()!=null) addRoomWrapper.type=roomInformation.getRoomType();else addRoomWrapper.type="***Not avalable***";
            addRoomWrapper.isDorm=false;
            if(roomInformation.getDescription()!=null) addRoomWrapper.description=roomInformation.getDescription();else addRoomWrapper.description="***Not avalable***";
            if(information.getNoOfRooms()!=null) addRoomWrapper.totalRoom=Integer.parseInt(information.getNoOfRooms());else addRoomWrapper.totalRoom=-1;
            addRoomWrapper.bedType="double";
            if(roomInformation.getView()!=null)addRoomWrapper.roomView=roomInformation.getView();else addRoomWrapper.roomView="***Not avalable***";
            addRoomWrapper.roomViews="***Not avalable***";
            addRoomWrapper.extraBedType="***Not avalable***";
            if(roomInformation.getMaxAdultOccupancy()!=null) addRoomWrapper.maxAdults=Integer.parseInt(roomInformation.getMaxAdultOccupancy());else addRoomWrapper.maxAdults=-1;
            if(roomInformation.getMaxChildOccupancy()!=null) addRoomWrapper.maxChild=Integer.parseInt(roomInformation.getMaxChildOccupancy());else addRoomWrapper.maxChild=-1;
            if(roomInformation.getMaxInfantOccupancy()!=null)
            addRoomWrapper.maxInfant=Integer.parseInt(roomInformation.getMaxInfantOccupancy());else addRoomWrapper.maxInfant=-1;
            addRoomWrapper.isSmoking="fase";

            List<RoomAmenity> amenitiesList=roomInformation.getAmenities();
            String roomAmenitiesList = amenitiesList.stream().map(RoomAmenity::getDescription).collect(Collectors.joining(","));
            addRoomWrapper.amenities=Arrays.asList(roomAmenitiesList);
            addRoomWrapper.roomSize="-1";
            addRoomWrapper.extraAdultAfterX="0";
            if(roomInformation.getMaxGuestOccupancy()!=null)
            addRoomWrapper.baseOccupancy=Integer.parseInt(roomInformation.getMaxGuestOccupancy());
            else addRoomWrapper.baseOccupancy=-1;

            try {
                   String jsonInString = MAPPER.writeValueAsString(addRoomWrapper);
                    sendRestTemplate(jsonInString,ADD_ROOM_URL);
                   }catch (Exception e){e.printStackTrace(); }

        }
    }
    public void updateHotelAmenities(StaticDumpHotel serObj,String hotelId){
        UpdateHotelAmenitiesWrapper hotelAmenitiesWrapper=new UpdateHotelAmenitiesWrapper();
        StaticDumpHotel staticDumpHotel = serObj;
        hotelAmenitiesWrapper.hotelId=hotelId;
        List<String> list=new ArrayList<>();
        hotelAmenitiesWrapper.aminetiesList=Arrays.asList("1232");
        try {
            String jsonInString = MAPPER.writeValueAsString(hotelAmenitiesWrapper);
            sendRestTemplate(jsonInString,HOTEL_AMENITIES_URL);
        }catch (Exception e){

        }
    }
    public void updateHotelPolicy(StaticDumpHotel serObj,String hotelId){
        UpdateHotelPolicyWrapper hotelPolicyWrapper=new UpdateHotelPolicyWrapper();
        StaticDumpHotel staticDumpHotel = serObj;
        hotelPolicyWrapper.hotelId=hotelId;
        if(staticDumpHotel.getHotelPolicies().toString()!=null) hotelPolicyWrapper.hotelPolicy=staticDumpHotel.getHotelPolicies().toString();else hotelPolicyWrapper.hotelPolicy="*** Not Avalable***";

        hotelPolicyWrapper.hotelPolicy=staticDumpHotel.getHotelPolicies().toString();
        try {
            String jsonInString = MAPPER.writeValueAsString(hotelPolicyWrapper);
            sendRestTemplate(jsonInString,HOTEL_POLICY_URL);
        }catch (Exception e){e.printStackTrace(); }
    }

    public void updateMapLocation(StaticDumpHotel serObj, String hotelId){
        UpdateMapLocationWrapper mapLocationWrapper=new UpdateMapLocationWrapper();
        StaticDumpHotel staticDumpHotel = serObj;
        Information information = staticDumpHotel.getInformation();
        mapLocationWrapper.hotelId=hotelId;
        if(information.getLatitude()!=null) mapLocationWrapper.latitude=information.getLatitude();else mapLocationWrapper.latitude="-1";
        if(information.getLongitude()!=null) mapLocationWrapper.longitude=information.getLongitude();else mapLocationWrapper.longitude="-1";
        try {
            String jsonInString = MAPPER.writeValueAsString(mapLocationWrapper);sendRestTemplate(jsonInString,MAP_LOCATION_URL);
        }catch (Exception e){e.printStackTrace(); }
    }
}
