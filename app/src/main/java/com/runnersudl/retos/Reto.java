package com.runnersudl.retos;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reto {
    public String id;
    public final String name;
    public final String descripcion;
    public final List<LatLng> points;
    public String userUID;

    Reto(String id, String name, String descripcion, List<LatLng> points) {
        this.id = id;
        this.name = name;
        this.descripcion = descripcion;
        this.points = new ArrayList<>(points);
    }

    Reto(String name, String descripcion, List<LatLng> points) {
        this.name = name;
        this.descripcion = descripcion;
        this.points = new ArrayList<>(points);
    }

    public LatLng getMiddlePoint() {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        return new LatLng(latitude/n, longitude/n);
    }

    Map<String, Object> toHashMap(){
        Map<String, Object> reto = new HashMap<>();
        reto.put("CreadoPor", FirebaseAuth.getInstance().getCurrentUser().getUid());
        reto.put("Name", name);
        reto.put("Descripcion", descripcion);
        reto.put("Puntos", new Gson().toJson(points));
        return reto;
    }

    static Reto fromHashMapToReto(Map<String, Object> retoMap, String id){
        String userUID = (String) retoMap.get("CreadoPor");
        String name = (String) retoMap.get("Name");
        String descripcion = (String) retoMap.get("Descripcion");

        Type type = new TypeToken<List<LatLng>>() {}.getType();
        List<LatLng> points = new Gson().fromJson((String) retoMap.get("Puntos"), type);

        Reto reto = new Reto(id, name, descripcion, points);
        reto.setUserUID(userUID);
        return reto;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}

