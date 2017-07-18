package com.cabot.androidtemplateproject.modules.registration.model;

import java.io.Serializable;

/**
 * Created by dennymathew on 24/05/17.
 */

public class Initialization implements Serializable {

    private int status;
    private String message;

    public void setStatus(int status) {
        this.status = status;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() { return status; }
    public String getMessage() {
        return message;
    }

    public class ResponseData implements Serializable {

        private int id;
        private String device_id;

        public int getId() {
            return id;
        }

        public String getDevice_id() {
            return device_id;
        }
    }
}
