package com.ampify.dictionarypopup.Database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    @TypeConverter
    public static ArrayList<String> fromString(String string) {
        return new Gson().fromJson(string, new TypeToken<List<String>>() {}.getType());
    }

    @TypeConverter
    public static String fromList(ArrayList<String> list) {
        return new Gson().toJson(list);
    }

}
