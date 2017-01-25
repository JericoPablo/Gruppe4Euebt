package com.hsrt.euebt;

/**
 * Created by Johannes on 10.01.2017.
 */

import java.io.Serializable;

/**
 * A class for representing one extra data unit of a certain type with a certain content for a specific training.
 */
public class TrainingExtra implements Serializable {

    private String trainingName;
    private ExtraType type;
    private String content;

    /**
     * Instantiates a training extra data unit from the training name, the extra data type and the extra data itself.
     * @param trainingName The name of the training this extra data belongs to.
     * @param type The type of this extra data unit.
     * @param content The content of this extra data unit.
     */
    public TrainingExtra(String trainingName, ExtraType type, String content) {
        this.type = type;
        this.content = content;
        this.trainingName = trainingName;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public ExtraType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public enum ExtraType {
        Image, Description
    }
}