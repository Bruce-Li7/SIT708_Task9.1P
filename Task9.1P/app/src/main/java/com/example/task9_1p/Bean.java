package com.example.task9_1p;

import java.io.Serializable;

public class Bean  implements Serializable {

    public int _id;
    public String value0, value1, value2, value3, value4 ;
    public String type;
    public String latitude,longitude;

    public Bean(int _id, String value0, String value1, String value2, String value3, String value4, String type,String latitude,String longitude) {
        this._id = _id;
        this.value0 = value0;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
