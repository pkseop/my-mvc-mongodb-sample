package my.spring.sample.mvc.utils;

import my.spring.sample.mvc.model.Coords;
import my.spring.sample.mvc.model.CoordsRange;

public class GeoUtil {

    public static Coords movePoint(double latitude, double longitude, double distanceInMeters, double bearing) {
        double brngRad = Math.toRadians(bearing);
        double latRad = Math.toRadians(latitude);
        double lonRad = Math.toRadians(longitude);
        int earthRadiusInMetres = 6371000;
        double distFrac = distanceInMeters / earthRadiusInMetres;

        double latitudeResult = Math.asin(Math.sin(latRad) * Math.cos(distFrac) + Math.cos(latRad) * Math.sin(distFrac) * Math.cos(brngRad));
        double a = Math.atan2(Math.sin(brngRad) * Math.sin(distFrac) * Math.cos(latRad), Math.cos(distFrac) - Math.sin(latRad) * Math.sin(latitudeResult));
        double longitudeResult = (lonRad + a + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

        double latDeg = Math.toDegrees(latitudeResult);
        double lngDeg = Math.toDegrees(longitudeResult);

//	    System.out.println(latDeg + " " + lngDeg);

        return new Coords(latDeg, lngDeg);
    }

    public static CoordsRange getRange(double latitude, double longitude, double distanceInMeteres) {
        Coords ltCoord = movePoint(latitude, longitude, distanceInMeteres, 315);
        Coords rbCoord = movePoint(latitude, longitude, distanceInMeteres, 125);

        return new CoordsRange(ltCoord.getLat(), ltCoord.getLng(), rbCoord.getLat(), rbCoord.getLng());
    }

    /**
     * 두 지점간의 거리 계산
     *
     * @param lat1
     *            지점 1 위도
     * @param lng1
     *            지점 1 경도
     * @param lat2
     *            지점 2 위도
     * @param lng2
     *            지점 2 경도
     * @return
     */
    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        double theta = lng1 - lng2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = dist * 1000.0;

        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
