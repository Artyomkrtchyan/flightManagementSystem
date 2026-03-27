package model;

public class Airport {
    private int id;
    private String code;
    private String name;
    private double lat;
    private double lng;
    private String cityName; // Поле для города 🏙️

    // Конструктор теперь принимает 6 параметров
    public Airport(int id, String code, String name, double lat, double lng, String cityName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.cityName = cityName;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }

    // Исправленный метод без лишних скобок
    public String getCityName() {
        return this.cityName;
    }
}