package com.cattleDB.LocationSimulationService.controller;

public class KalmanFilter {
    private double q; // Process noise covariance
    private double r; // Measurement noise covariance
    private double x; // Current estimate
    private double p; // Estimation error covariance
    private double k; // Kalman gain

    // Constructor with three parameters
    public KalmanFilter(double processNoise, double measurementNoise, double initialEstimate) {
        this.q = processNoise;
        this.r = measurementNoise;
        this.x = initialEstimate;
        this.p = 1; // Initial estimation error
    }

    public double update(double measurement) {
        // Prediction step
        p += q;

        // Update step
        k = p / (p + r);
        x = x + k * (measurement - x);
        p = (1 - k) * p;

        return x;
    }
}
