package com.file.processor;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Problem: You are tasked with implementing a multithreaded file processor in Java.
 * The goal is to read data from multiple files concurrently, process each file's content,
 * and store the results in a shared data structure. Each file contains lines of text.

 */

@Component
public class DataReader {

  @Autowired
  ResourceLoader resourceLoader;

  public List<Data> loadFiles() throws IOException {


    List<Data> allData = Collections.synchronizedList(new ArrayList<>());

    // reading files
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
    Resource[] resources = resolver.getResources("classpath:**.json");

    // creating thread pool
    ExecutorService executorService = Executors.newFixedThreadPool(resources.length);

    List<Future<List<Data>>> futures = new ArrayList<>();

    // open each resource in a new thread
    for (Resource resource : resources) {
      Callable<List<Data>> task = prosessEachFileInSeparateThread(resource);
      futures.add(executorService.submit(task));
    }

    // wait until all threads are completed, get the result back and combine them
    futures.forEach(f -> {
      try {
        List<Data> dataList = f.get();
        allData.addAll(dataList);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (ExecutionException e) {
        throw new RuntimeException(e);
      }
    });

    executorService.shutdown();

    // return final data structure
    return allData;
  }


  // separate method to process one file at a time
  private void prosessEachFileInSeparateThread(Resource resource, List<Data> allData)
      throws IOException {
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();
    JsonArray jsonArray = (JsonArray) parser.parse(new FileReader(resource.getFile()));
    List<Data> dataList = new ArrayList<>();
    for (JsonElement jsonElement : jsonArray) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      Data data = gson.fromJson(jsonObject, Data.class);
      dataList.add(data);
    }
    System.out.println(Thread.currentThread().getName() + "--" + resource.getFile().getName());
  }


  // this prepares callable task from the core processing method
  private Callable<List<Data>> prosessEachFileInSeparateThread(Resource resource)
      throws IOException {

    // simplified way
    //Callable<List<Data>> task = () -> getDataFromFile(resource);

    // original way
    Callable<List<Data>> task = new Callable<List<Data>>() {
      @Override
      public List<Data> call() throws Exception {
        return getDataFromFile(resource);
      }
    };

    return task;
  }

  // this is the core processing method
  public List<Data> getDataFromFile(Resource resource) throws IOException {
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();
    JsonArray jsonArray = (JsonArray) parser.parse(new FileReader(resource.getFile()));
    List<Data> dataList = new ArrayList<>();

    for (JsonElement jsonElement : jsonArray) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      Data data = gson.fromJson(jsonObject, Data.class);
      dataList.add(data);
    }
    return dataList;
  }


}
