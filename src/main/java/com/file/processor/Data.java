package com.file.processor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Data {

  @JsonProperty("id")
  String id;

  @JsonProperty("fileName")
  String fileName;

}
