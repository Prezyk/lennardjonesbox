package com.prezyk.md.model;

import org.junit.jupiter.api.Test;

import static com.prezyk.util.Assertions.assertDoubleMatrixEquals;

class LennardJonesModelTest {

    private static final double MASS_NOT_RELEVANT = 0.;

    @Test
    public void textNextAccelerationsBasic() {
        double[][] nextPositionsMatrix = new double[][] {
                new double[] {0.5, -0.5},
                new double[] {-0.5, 0.5}
        };

        LennardJonesModel model = new LennardJonesModel(1, 1, 1);
        double[][] actualNextAccelerations = model.calculateNextAcceleration(nextPositionsMatrix);

        double[][] expectedNextAccelerations = new double[][] {
                new double[] {24, -24},
                new double[] {-24, 24}
        };

        assertDoubleMatrixEquals(expectedNextAccelerations, actualNextAccelerations);
    }

    @Test
    public void textNextAccelerationsEpsDiffThanOne() {
        double[][] nextPositionsMatrix = new double[][] {
                new double[] {0, 0},
                new double[] {-1, 1}
        };

        LennardJonesModel model = new LennardJonesModel(0.5, 1, 1);
        double[][] actualNextAccelerations = model.calculateNextAcceleration(nextPositionsMatrix);

        double[][] expectedNextAccelerations = new double[][] {
                new double[] {12, -12},
                new double[] {-12, 12}
        };

        assertDoubleMatrixEquals(expectedNextAccelerations, actualNextAccelerations);
    }

    @Test
    public void textNextAccelerationsDistanceOtherThanOne() {
        double[][] nextPositionsMatrix = new double[][] {
                new double[] {-0.15, 0.62},
                new double[] {-0.65, 0.12}
        };

        LennardJonesModel model = new LennardJonesModel(1, 1, 1);
        double[][] actualNextAccelerations = model.calculateNextAcceleration(nextPositionsMatrix);

        double[][] expectedNextAccelerations = new double[][] {
                new double[] {390144, 390144},
                new double[] {-390144, -390144}
        };

        assertDoubleMatrixEquals(expectedNextAccelerations, actualNextAccelerations);
    }

    @Test
    public void textNextAccelerationsSigmaOtherThanOne() {
        double[][] nextPositionsMatrix = new double[][] {
                new double[] {0, 0},
                new double[] {1, 1}
        };

        LennardJonesModel model = new LennardJonesModel(1, 1, 0.5);
        double[][] actualNextAccelerations = model.calculateNextAcceleration(nextPositionsMatrix);

        double[][] expectedNextAccelerations = new double[][] {
                new double[] {0.36328125, 0.36328125},
                new double[] {-0.36328125, -0.36328125}
        };

        assertDoubleMatrixEquals(expectedNextAccelerations, actualNextAccelerations);
    }

    @Test
    public void textNextAccelerationsMassOtherThanOne() {
        double[][] nextPositionsMatrix = new double[][] {
                new double[] {0, 0},
                new double[] {1, 1}
        };

        LennardJonesModel model = new LennardJonesModel(1, 0.5, 1);
        double[][] actualNextAccelerations = model.calculateNextAcceleration(nextPositionsMatrix);

        double[][] expectedNextAccelerations = new double[][] {
                new double[] {-48, -48},
                new double[] {48, 48}
        };

        assertDoubleMatrixEquals(expectedNextAccelerations, actualNextAccelerations);
    }

    //TODO non testable due to rounding - need to migrate to BigDecimal first
//    @Test
//    public void testPotentialEnergyBasic() {
//        double[][] currentPositionsMatrix = new double[][] {
//                new double[] {Math.sqrt(0.5)/2, -Math.sqrt(0.5)/2},
//                new double[] {-Math.sqrt(0.5)/2, Math.sqrt(0.5)/2}
//        };
//
//        LennardJonesModel model = new LennardJonesModel(1, MASS_NOT_RELEVANT, 1);
//
//        double actualPotentialEnergy = model.calculatePotentialEnergy(currentPositionsMatrix);
//        double expectedPotentialEnergy = 0.;
//
//        assertEquals(expectedPotentialEnergy, actualPotentialEnergy);
//    }
//
//    @Test
//    public void testPotentialEnergyForEps() {
//        double[][] currentPositionsMatrix = new double[][] {
//                new double[] {0.25, -0.25},
//                new double[] {-0.25, 0.25}
//        };
//
//        LennardJonesModel model = new LennardJonesModel(0.5, MASS_NOT_RELEVANT, 0.5);
//
//        double actualPotentialEnergy = model.calculatePotentialEnergy(currentPositionsMatrix);
//        double expectedPotentialEnergy = -0.03076172;
//
//        assertEquals(expectedPotentialEnergy, actualPotentialEnergy);
//    }
//
//    @Test
//    public void testPotentialEnergyForDistance() {
//        double[][] currentPositionsMatrix = new double[][] {
//                new double[] {0.25, -0.25},
//                new double[] {-0.25, 0.25}
//        };
//
//        LennardJonesModel model = new LennardJonesModel(1, MASS_NOT_RELEVANT, 1);
//
//        double actualPotentialEnergy = model.calculatePotentialEnergy(currentPositionsMatrix);
//        double expectedPotentialEnergy = 16128;
//
//        assertEquals(expectedPotentialEnergy, actualPotentialEnergy);
//    }
//
//    @Test
//    public void testPotentialEnergyForSigma() {
//        double[][] currentPositionsMatrix = new double[][] {
//                new double[] {0.25, -0.25},
//                new double[] {-0.25, 0.25}
//        };
//
//        LennardJonesModel model = new LennardJonesModel(1, MASS_NOT_RELEVANT, 0.5);
//
//        double actualPotentialEnergy = model.calculatePotentialEnergy(currentPositionsMatrix);
//        double expectedPotentialEnergy = -0.06152344;
//
//        assertEquals(expectedPotentialEnergy, actualPotentialEnergy);
//    }


}