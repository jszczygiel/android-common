package com.jszczygiel.foundation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jszczygiel.foundation.helpers.LoggerHelper;
import java.io.IOException;
import java.util.List;
import org.json.JSONException;

public enum JsonMapper {
  INSTANCE;

  private final ObjectMapper mapper;

  JsonMapper() {
    mapper = new ObjectMapper();
    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

  }

  public <T> T fromJson(String json, Class<T> clazz) throws JSONException {
    try {
      return mapper.readValue(json, clazz);
    } catch (IOException e) {
      LoggerHelper.log(e);
      throw new JSONException(e.getMessage());
    }
  }

  public <T> T fromJson(JsonParser json, Class<T> clazz) throws JSONException {
    try {
      return mapper.readValue(json, clazz);
    } catch (IOException e) {
      LoggerHelper.log(e);
      throw new JSONException(e.getMessage());
    }
  }

  public String toJson(Object model) {
    try {
      return mapper.writeValueAsString(model);
    } catch (JsonProcessingException e) {
      LoggerHelper.log(e);
      return null;
    }
  }

  public ObjectMapper getObjectMapper() {
    return mapper;
  }

  public <T> List<T> listFromJson(String jsonString, Class<T> klazz) throws JSONException {
    ObjectMapper mapper = new ObjectMapper();
    JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, klazz);
    try {
      return mapper.readValue(jsonString, type);
    } catch (IOException e) {
      LoggerHelper.log(e);
      throw new JSONException(e.getMessage());
    }
  }
}
