package com.doll1av.finalproject.myroomiehelper;

/**
 *
 * Constructor for tasks, straitforward
 *
 */

public class AddTask {

   private String task;
   private String date;
   private String id;


    private  String username;
    public AddTask(){}

    public AddTask(String task, String Date, String id, String Username) {
        this.task = task;
        this.date = Date;
        this.id = id;
        this.username = Username;

    }

    public String getTask() {
        return task;
    }

    public String getId() { return id; }

    public String getDate() {
        return date;
    }

    public String getUsername() { return username; }

}
