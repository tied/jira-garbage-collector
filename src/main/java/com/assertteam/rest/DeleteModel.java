package com.assertteam.rest;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class DeleteModel {

    @XmlElement
    private String message;
    @XmlElement
    private String type;

    public DeleteModel() {
    }

    public DeleteModel(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
