/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 *
 * Convert given times to and from local and UTC times
 * 
 * @author Ben
 */
public class ConvertTimeZone {
    
    //Get the local time zone based on users location
    private static ZoneId localZone = ZoneId.of(TimeZone.getDefault().getID());
    
    /**
     * 
     * Convert the passed UTC date and time and convert it to the users local time
     * 
     * @param dateTime
     * @return 
     */
    public static String getLocalTime(LocalDateTime dateTime){
        LocalDateTime ldt = dateTime;
        ZonedDateTime zdt = ZonedDateTime.ofInstant(ldt.toInstant(ZoneOffset.UTC), localZone);
        String localTime = String.valueOf(zdt.toLocalTime());
        
        
        return localTime;
    }
    
    /**
     * 
     * Convert passed UTC date and time and convert it to the users local date
     * 
     * @param dateTime
     * @return 
     */
    public static String getLocalDate(LocalDateTime dateTime){
        LocalDateTime ldt = dateTime;
        ZonedDateTime zdt = ZonedDateTime.ofInstant(ldt.toInstant(ZoneOffset.UTC), localZone);
        String localDate = String.valueOf(zdt.toLocalDate());
        
        return localDate;
    }
    
    /**
     * 
     * Convert passed local date and time and convert it to UTC date and time
     * 
     * @param dateTime
     * @return 
     */
    public static String getUTCDateTime(LocalDateTime dateTime){
        LocalDateTime ldt = dateTime;
        ZonedDateTime zdt = ZonedDateTime.of(ldt, localZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
        Instant instant = zdt.toInstant();
        String utcDateTime = formatter.format(instant); 
        
        return utcDateTime;
    }
    
    /**
     * 
     * Convert passed local date and time and convert it to UTC time
     * 
     * @param dateTime
     * @return 
     */
    public static String getUTCTime(LocalDateTime dateTime){
        LocalDateTime ldt = dateTime;
        ZonedDateTime zdt = ZonedDateTime.of(ldt, localZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("UTC"));
        Instant instant = zdt.toInstant();
        String utcTime = formatter.format(instant); 
        
        return utcTime;
    }
}
