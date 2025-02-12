package com.cattleDB.LocationSimulationService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cattleDB.LocationSimulationService.models.PositionSanitized;
import smile.clustering.DBSCAN;
import smile.math.distance.Distance;

public class DBSCANLocalizationExample {

    /**
     * Reusable function that clusters positions using DBSCAN.
     *
     * @param positions List of PositionSanitized objects
     */
    public static void clusterPositions(List<PositionSanitized> positions) {
        // Convert positions into a 2D array where each row is [latitude, longitude]
        double[][] data = new double[positions.size()][2];
        for (int i = 0; i < positions.size(); i++) {
            data[i][0] = positions.get(i).getLatitude();
            data[i][1] = positions.get(i).getLongitude();
        }

        // Define DBSCAN parameters:
        // eps: the maximum distance (in kilometers) to consider two points as neighbors.
        // Since the HaversineDistance returns kilometers, 5 meters is 0.005 km.
//        double eps = 0.5; // 5 meters in km
        double eps = 0.005; // 1 meter in km
        int minPts = 3;

        // Run DBSCAN using our custom Haversine distance metric
        DBSCAN<double[]> dbscan = DBSCAN.fit(data, new HaversineDistance(), minPts, eps);
        int[] labels = dbscan.y;  // Access cluster labels via the public field 'y'


        System.out.println("-----------------------------------------------Output clustering results for each position-----------------------------------------------------");
        // Output clustering results for each position
        for (int i = 0; i < labels.length; i++) {
            String clusterLabel = labels[i] == -1 ? "Noise" : "Cluster " + labels[i];
            System.out.println("Position: " + positions.get(i).getName() +
                    " (" + positions.get(i).getLatitude() + ", " + positions.get(i).getLongitude() + ") -> " +
                    clusterLabel);
        }
        System.out.println("-------------------------------------------------Output clustering results for each position---------------------------------------------------");



        // Group positions by cluster label
        Map<Integer, List<PositionSanitized>> clusters = new HashMap<>();
        for (int i = 0; i < labels.length; i++) {
            int label = labels[i];
            clusters.computeIfAbsent(label, k -> new ArrayList<>()).add(positions.get(i));
        }

        // Compute and print cluster centers for each cluster (including noise if desired)
        System.out.println("\nCluster Centers:");
        for (Map.Entry<Integer, List<PositionSanitized>> entry : clusters.entrySet()) {
            int clusterLabel = entry.getKey();
            List<PositionSanitized> clusterPoints = entry.getValue();
            double sumLat = 0.0;
            double sumLon = 0.0;
            for (PositionSanitized pos : clusterPoints) {
                sumLat += pos.getLatitude();
                sumLon += pos.getLongitude();
            }
            double centerLat = sumLat / clusterPoints.size();
            double centerLon = sumLon / clusterPoints.size();
            String labelStr = clusterLabel == -1 ? "Noise" : "Cluster " + clusterLabel;
            System.out.println(labelStr + " center: (" + centerLat + ", " + centerLon + ") with " +
                    clusterPoints.size() + " points.");
        }

    }

    public static void main(String[] args) {
        // Retrieve or build a list of PositionSanitized records (from a database, for example)
        List<PositionSanitized> positions = getPositions();

        // Pass the positions to our clustering function
        clusterPositions(positions);
    }

    /**
     * Dummy method to simulate retrieval of PositionSanitized data.
     * In a real application, this data might be fetched from a database.
     */
    private static List<PositionSanitized> getPositions() {
        List<PositionSanitized> positions = new ArrayList<>();

        // Example positions (adjust these as needed)
        positions.add(new PositionSanitized(
                1L, "Pos1", null, "protocol",
                OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(),
                false, true, 48.8566, 2.3522, 1L, 1L, 10, 20,
                1L, "Beacon1", 1, 1, "uuid1", 1L, "Device1", 0.0
        ));
        positions.add(new PositionSanitized(
                2L, "Pos2", null, "protocol",
                OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(),
                false, true, 48.856605, 2.352205, 1L, 1L, 10, 20,
                2L, "Beacon2", 1, 2, "uuid2", 1L, "Device1", 0.0
        ));
        positions.add(new PositionSanitized(
                3L, "Pos3", null, "protocol",
                OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(),
                false, true, 48.856610, 2.352210, 1L, 1L, 10, 20,
                3L, "Beacon3", 1, 3, "uuid3", 1L, "Device1", 0.0
        ));
        // A farther position that might be classified as noise or a separate cluster
        positions.add(new PositionSanitized(
                4L, "Pos4", null, "protocol",
                OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(),
                false, true, 48.8570, 2.3530, 1L, 1L, 10, 20,
                4L, "Beacon4", 1, 4, "uuid4", 1L, "Device1", 0.0
        ));

        return positions;
    }
}

/**
 * Custom distance measure using the Haversine formula.
 * Computes the great-circle distance (in km) between two latitude/longitude points.
 */
class HaversineDistance implements Distance<double[]> {
    @Override
    public double d(double[] a, double[] b) {
        final int R = 6371; // Earth's radius in kilometers
        double latDistance = Math.toRadians(b[0] - a[0]);
        double lonDistance = Math.toRadians(b[1] - a[1]);
        double aVal = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(a[0])) * Math.cos(Math.toRadians(b[0]))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(aVal), Math.sqrt(1 - aVal));
        return R * c;
    }

    @Override
    public String toString() {
        return "HaversineDistance";
    }
}
