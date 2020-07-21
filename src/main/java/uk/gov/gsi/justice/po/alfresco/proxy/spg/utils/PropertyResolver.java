package uk.gov.gsi.justice.po.alfresco.proxy.spg.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Taleb Benouaer on 28/05/2015.
 * This class acts as a properties object
 * The class are loaded in either of two ways:
 * 1- from a config file by passing the filePath in the constructor
 * 2- setting the properties directly by passing a map of the properties in the constructor
 */
public class PropertyResolver extends Properties {
    private static final String MISSING_PROPERTIES_FILE_ERROR = "missing properties file";
    private static final String ERROR_READING_PROPERTIES_FILE_ERROR_MESSAGE = "error locating the endpoint address in the config file";

    private Log log = LogFactory.getLog(PropertyResolver.class);
    private String configFilePath;

    public PropertyResolver(String configFilePath) {
        this.configFilePath = configFilePath;
        loadProperties();
    }

    public PropertyResolver(Map<String,String> propertiesMap) {
        for (Map.Entry<String,String> configEntry : propertiesMap.entrySet()) {
            setProperty(configEntry.getKey(), configEntry.getValue());
        }
    }
    
    public  Set<String> getSetFromCSVProperty(String key) {
      
      String csvList=getProperty(key);
      return StringUtils.commaDelimitedListToSet(csvList);
      
    }
    

    public Map<String, String> getPropertiesWithPrefix(String propertyKeyPrefix) {
        Map<String, String> matchingProperties = new HashMap<>();
        for (String propertyName : stringPropertyNames()) {
            if (propertyName.startsWith(propertyKeyPrefix)) {
                matchingProperties.put(propertyName, getProperty(propertyName));
            }
        }
        return matchingProperties;
    }

    /**
     * adds properties from config into a Map
     * K is the property name without its prefix
     * V is the property value
     * @param propertyKeyPrefix property prefix to search for in config
     */
    public Map<String,String> getPropertiesWithoutPrefix(String propertyKeyPrefix){
        Map<String, String> matchingProperties = new HashMap<>();
        for(String propertyName : stringPropertyNames()){
            if(propertyName.startsWith(propertyKeyPrefix)){
                matchingProperties.put(propertyName.replaceAll(propertyKeyPrefix, ""), getProperty(propertyName));
            }
        }
        return matchingProperties;
    }

    /**
     * This method searches the configuration files for any properties with the specified prefix
     * If any exist, it loops through the keyset, stripping the prefix from each key and appending it to the return list
     *
     * @param prefix
     * @return the keyset of the matched prefix properties, with the prefixes stripped off the key string
     */
    public List<String> extractPrefixPropertyIds(String prefix) {
        List<String> propertyIds = new ArrayList<>();
        if (!getPropertiesWithPrefix(prefix).isEmpty()) {
            for (String propertyKey : getPropertiesWithPrefix(prefix).keySet()) {
                propertyIds.add(propertyKey.replaceAll(prefix, ""));
            }
        }
        return propertyIds;
    }

    private void loadProperties() {
        try {
            InputStream inputStream = new FileInputStream(configFilePath);
                load(inputStream);
                inputStream.close();
        } catch (FileNotFoundException e) {
            log.error(MISSING_PROPERTIES_FILE_ERROR);
            log.error(e);
        } catch (IOException e) {
            log.error(ERROR_READING_PROPERTIES_FILE_ERROR_MESSAGE);
            log.error(e);
        }
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    /*
        Parses "CSV Maps", ala schema <> CRC configuration:

        prefix                      key    mappedValues
        crc_schema_version_mapping_0_9_9: C01,C02,C03,C04,C05
        crc_schema_version_mapping_0_9_10: C06,C07,C08,C09,C10,C11
        crc_schema_version_mapping_0_9_11: C12,C13,C14,C15,C16,C17,C18,C19,C20,C21


         */
        @Nullable
        String getKeyFromValueInCSVMapList(String valueToFind, String propertyPrefix) {
            Map<String, String> csvMapList = getPropertiesWithPrefix(propertyPrefix);
            if (!csvMapList.isEmpty()) {
                for (Map.Entry<String, String> mapKey : csvMapList.entrySet()) {
                    String[] mappedValues = mapKey.getValue().split(",");
                    if (Arrays.asList(mappedValues).contains(valueToFind)) {
                        return mapKey.getKey().replaceAll(propertyPrefix, "");
                    }
                }
            }
            return null;
        }
}
