package com.jszczygiel.foundation.repos.interfaces;

import java.util.Map;

public interface BaseModel {

    String getId();

    Map<String, Object> toMap(Object next);
    Map<String, Object> toMap();
}
