package model;

public class Route {
    private final int routeID;
    private final int sourceAirportID;
    private final int destinationAirportID;
    private final double baseDistanceKM;
    private final double cost;


    public Route(int routeID, int sourceAirportID, int destinationAirportID, double baseDistanceKM, double cost) {
        this.routeID = routeID;
        this.sourceAirportID = sourceAirportID;
        this.destinationAirportID = destinationAirportID;
        this.baseDistanceKM = baseDistanceKM;
        this.cost = cost;
    }

    public int getRouteID() {
        return routeID;
    }

    public int getSourceAirportID() {
        return sourceAirportID;
    }

    public int getDestinationAirportID() {
        return destinationAirportID;
    }

    public double getBaseDistanceKM() {
        return baseDistanceKM;
    }


    public double getTicketPrice() {
        return cost;
    }
}