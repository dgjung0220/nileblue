package com.bearpot.dgjung.nileblue.VO;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by dg.jung on 2017-11-15.
 */

public class WeatherVo implements Serializable {
    private int[] conditions;
    private float temperature;
    private float feelsLikeTemperature;
    private float dewPoint;
    private int huminity;

    public WeatherVo(){}
    public WeatherVo(int[] conditions, float temperature, float feelsLikeTemperature, float dewPoint, int huminity) {
        this.conditions = conditions;
        this.temperature = temperature;
        this.feelsLikeTemperature = feelsLikeTemperature;
        this.dewPoint = dewPoint;
        this.huminity = huminity;
    }

    public int[] getConditions() {
        return conditions;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getFeelsLikeTemperature() {
        return feelsLikeTemperature;
    }

    public float getDewPoint() {
        return dewPoint;
    }

    public int getHuminity() {
        return huminity;
    }

    @Override
    public String toString() {
        return "WeatherVo{" +
                "conditions=" + Arrays.toString(conditions) +
                ", temperature=" + temperature +
                ", feelsLikeTemperature=" + feelsLikeTemperature +
                ", dewPoint=" + dewPoint +
                ", huminity=" + huminity +
                '}';
    }
}
