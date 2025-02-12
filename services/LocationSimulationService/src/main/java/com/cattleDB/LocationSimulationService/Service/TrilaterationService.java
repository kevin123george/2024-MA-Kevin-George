package com.cattleDB.LocationSimulationService.Service;


import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

/**
 * Example service to do 2D trilateration or fallback to weighted average.
 */
public class TrilaterationService {

    // Constants for your RSSI → distance model
    private static final double A = -59; // RSSI at 1 meter
    private static final double N = 2.0; // Path-loss exponent

    private static final double EARTH_RADIUS_M = 6371000.0;

    /**
     * Convert RSSI to distance in meters.
     */
    public static double rssiToDistance(int rssi) {
        // d = 10 ^ ((A - RSSI) / (10 * N))
        return Math.pow(10.0, (A - rssi) / (10.0 * N));
    }

    /**
     * Convert (lat, lon) to local (x, y) in meters using an equirectangular approximation
     * around reference (refLat, refLon).
     */
    public static double[] latlonToXY(double lat, double lon, double refLat, double refLon) {
        double latRad  = Math.toRadians(lat);
        double lonRad  = Math.toRadians(lon);
        double refLatRad = Math.toRadians(refLat);
        double refLonRad = Math.toRadians(refLon);

        double avgLat = 0.5 * (latRad + refLatRad);

        double x = (lonRad - refLonRad) * Math.cos(avgLat) * EARTH_RADIUS_M;
        double y = (latRad - refLatRad) * EARTH_RADIUS_M;

        return new double[]{x, y};
    }

    /**
     * Convert local (x, y) in meters back to (lat, lon) in degrees.
     */
    public static double[] xyToLatlon(double x, double y, double refLat, double refLon) {
        double refLatRad = Math.toRadians(refLat);
        double refLonRad = Math.toRadians(refLon);

        double latRad = (y / EARTH_RADIUS_M) + refLatRad;
        double avgLat = 0.5 * (latRad + refLatRad);

        double lonRad = (x / (EARTH_RADIUS_M * Math.cos(avgLat))) + refLonRad;

        return new double[]{
                Math.toDegrees(latRad),
                Math.toDegrees(lonRad)
        };
    }

    /**
     * If 3+ beacons, run a Levenberg-Marquardt solver (least squares) to find
     * the (x, y) that best matches the distance constraints. Otherwise, fallback
     * to weighted average.
     *
     * @param beaconXY array of [ [bx1, by1], [bx2, by2], ...]
     * @param distances array of distance in meters [d1, d2, d3, ...]
     * @return double[]{finalX, finalY}
     */
    public static double[] solvePosition(List<double[]> beaconXY, List<Double> distances) {
        int n = beaconXY.size();
        if (n < 3) {
            // Weighted Average fallback
            return computeWeightedAverage(beaconXY, distances);
        }

        try {
            // We'll do a least squares approach:
            // residual[i] = sqrt((x - bx_i)^2 + (y - by_i)^2) - d_i
            // We'll also define a Jacobian for better convergence.

            // initial guess: centroid
            double initX = 0.0;
            double initY = 0.0;
            for (double[] xy : beaconXY) {
                initX += xy[0];
                initY += xy[1];
            }
            initX /= n;
            initY /= n;
            double[] initialGuess = new double[]{initX, initY};

            // Build the problem
            MultivariateVectorFunction valueFunc = new MultivariateVectorFunction() {
                @Override
                public double[] value(double[] point) {
                    double px = point[0];
                    double py = point[1];
                    double[] residuals = new double[n];
                    for (int i=0; i<n; i++) {
                        double bx = beaconXY.get(i)[0];
                        double by = beaconXY.get(i)[1];
                        double d  = distances.get(i);
                        double distEst = Math.sqrt((px - bx)*(px - bx) + (py - by)*(py - by));
                        residuals[i] = distEst - d;
                    }
                    return residuals;
                }
            };

            // Optional: approximate Jacobian
            MultivariateMatrixFunction jacobianFunc = new MultivariateMatrixFunction() {
                @Override
                public double[][] value(double[] point) {
                    double px = point[0];
                    double py = point[1];
                    double[][] jacobian = new double[n][2]; // n residuals, 2 params (x,y)
                    for (int i=0; i<n; i++) {
                        double bx = beaconXY.get(i)[0];
                        double by = beaconXY.get(i)[1];
                        double distEst = Math.sqrt((px - bx)*(px - bx) + (py - by)*(py - by));
                        // partial derivative wrt x
                        if (distEst == 0) {
                            // avoid div zero
                            jacobian[i][0] = 0;
                            jacobian[i][1] = 0;
                        } else {
                            jacobian[i][0] = (px - bx) / distEst;
                            jacobian[i][1] = (py - by) / distEst;
                        }
                    }
                    return jacobian;
                }
            };

            LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
//            ConvergenceChecker<LeastSquaresProblem.Evaluation> checker =
//                    new SimpleVectorValueChecker(1e-8, 1e-8);

            LeastSquaresProblem problem = new LeastSquaresBuilder()
                    .start(initialGuess)
                    .model(valueFunc, jacobianFunc)
                    .target(new double[n])    // target is all 0 (we want residuals=0)
                    .lazyEvaluation(false)
                    .maxEvaluations(1000)
                    .maxIterations(1000)
//                    .checker(checker)
                    .build();

            // Solve
            RealVector solution = optimizer.optimize(problem).getPoint();
            double finalX = solution.getEntry(0);
            double finalY = solution.getEntry(1);

            return new double[]{finalX, finalY};

        } catch (Exception e) {
            // If solver fails, fallback
            return computeWeightedAverage(beaconXY, distances);
        }
    }

    /**
     * Weighted average fallback in local XY:
     *   w_i = 1 / (d_i^2 + epsilon)
     *   x = sum(bx_i * w_i) / sum(w_i)
     *   y = sum(by_i * w_i) / sum(w_i)
     */
    private static double[] computeWeightedAverage(List<double[]> beaconXY, List<Double> distances) {
        double eps = 1e-6;
        double sumW = 0.0;
        double sumX = 0.0;
        double sumY = 0.0;

        for (int i = 0; i < beaconXY.size(); i++) {
            double d = distances.get(i);
            double w = 1.0 / (d * d + eps);
            sumW += w;
            sumX += beaconXY.get(i)[0] * w;
            sumY += beaconXY.get(i)[1] * w;
        }

        if (sumW < eps) {
            return new double[]{0.0, 0.0};
        }

        double finalX = sumX / sumW;
        double finalY = sumY / sumW;
        return new double[]{finalX, finalY};
    }

}