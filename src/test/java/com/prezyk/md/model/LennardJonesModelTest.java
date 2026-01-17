package com.prezyk.md.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.prezyk.util.Assertions.assertBigDecimalMatrixEquals;
import static com.prezyk.util.CalculationPrecision.PRECISION;

class LennardJonesModelTest {

    private static final int BIG_DECIMAL_SCALE = 100;

    @Test
    public void textNextAccelerationsBasic() {
        BigDecimal[][] nextPositionsMatrix = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(0.5).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(-0.5).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(-0.5).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(0.5).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        LennardJonesModel model = new LennardJonesModel(BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                                                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                                                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP));
        BigDecimal[][] actualNextAccelerations = model.calculateNextAcceleration(nextPositionsMatrix);

        BigDecimal[][] expectedNextAccelerations = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(24).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(-24).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(-24).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(24).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        assertBigDecimalMatrixEquals(expectedNextAccelerations, actualNextAccelerations, BIG_DECIMAL_SCALE);
    }

    @Test
    public void textNextAccelerationsEpsDiffThanOne() {
        BigDecimal[][] nextPositionsMatrix = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(0).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(0).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(-1).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        LennardJonesModel model = new LennardJonesModel(BigDecimal.valueOf(0.5).setScale(PRECISION, RoundingMode.HALF_UP),
                                                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                                                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP)
        );
        BigDecimal[][] actualNextAccelerations = model.calculateNextAcceleration(nextPositionsMatrix);

        BigDecimal[][] expectedNextAccelerations = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(12).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(-12).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(-12).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(12).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        assertBigDecimalMatrixEquals(expectedNextAccelerations, actualNextAccelerations, BIG_DECIMAL_SCALE);
    }

    @Test
    public void textNextAccelerationsDistanceOtherThanOne() {
        BigDecimal[][] nextPositionsMatrix = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(-0.15).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(0.62).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(-0.65).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(0.12).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        LennardJonesModel model = new LennardJonesModel(BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                                                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                                                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP));
        BigDecimal[][] actualNextAccelerations = model.calculateNextAcceleration(nextPositionsMatrix);

        BigDecimal[][] expectedNextAccelerations = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(390144).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(390144).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(-390144).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(-390144).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        assertBigDecimalMatrixEquals(expectedNextAccelerations, actualNextAccelerations, BIG_DECIMAL_SCALE);
    }

    @Test
    public void textNextAccelerationsSigmaOtherThanOne() {
        BigDecimal[][] nextPositionsMatrix = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(0).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(0).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        LennardJonesModel model = new LennardJonesModel(BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                                                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                                                        BigDecimal.valueOf(0.5).setScale(PRECISION, RoundingMode.HALF_UP));
        BigDecimal[][] actualNextAccelerations = model.calculateNextAcceleration(nextPositionsMatrix);

        BigDecimal[][] expectedNextAccelerations = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(0.36328125).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(0.36328125).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(-0.36328125).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(-0.36328125).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        assertBigDecimalMatrixEquals(expectedNextAccelerations, actualNextAccelerations, BIG_DECIMAL_SCALE);
    }

    @Test
    public void textNextAccelerationsMassOtherThanOne() {
        BigDecimal[][] nextPositionsMatrix = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(0).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(0).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        LennardJonesModel model = new LennardJonesModel(BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                                                        BigDecimal.valueOf(0.5).setScale(PRECISION, RoundingMode.HALF_UP),
                                                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP));
        BigDecimal[][] actualNextAccelerations = model.calculateNextAcceleration(nextPositionsMatrix);

        BigDecimal[][] expectedNextAccelerations = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(-48).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(-48).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(48).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(48).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        assertBigDecimalMatrixEquals(expectedNextAccelerations, actualNextAccelerations, BIG_DECIMAL_SCALE);
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