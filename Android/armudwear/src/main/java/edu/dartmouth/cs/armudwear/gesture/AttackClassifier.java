package edu.dartmouth.cs.armudwear.gesture;

import edu.dartmouth.cs.armudwear.Globals;

class AttackClassifier {

        public static double classify(Object[] i)
                throws Exception {

                double p = Double.NaN;
                p = AttackClassifier.N6e37fa090(i);
                return p;
        }
        static double N6e37fa090(Object []i) {
                double p = Double.NaN;
                if (i[2] == null) {
                        p = Globals.NO_COMMAND_DETECTED;
                } else if (((Double) i[2]).doubleValue() <= 66.447381) {
                        p = AttackClassifier.N1d009d671(i);
                } else if (((Double) i[2]).doubleValue() > 66.447381) {
                        p = AttackClassifier.N769af3d85(i);
                }
                return p;
        }
        static double N1d009d671(Object []i) {
                double p = Double.NaN;
                if (i[30] == null) {
                        p = Globals.NO_COMMAND_DETECTED;
                } else if (((Double) i[30]).doubleValue() <= 2.861875) {
                        p = Globals.NO_COMMAND_DETECTED;
                } else if (((Double) i[30]).doubleValue() > 2.861875) {
                        p = AttackClassifier.N135de4112(i);
                }
                return p;
        }
        static double N135de4112(Object []i) {
                double p = Double.NaN;
                if (i[12] == null) {
                        p = Globals.NO_COMMAND_DETECTED;
                } else if (((Double) i[12]).doubleValue() <= 6.869175) {
                        p = Globals.NO_COMMAND_DETECTED;
                } else if (((Double) i[12]).doubleValue() > 6.869175) {
                        p = AttackClassifier.N792822053(i);
                }
                return p;
        }
        static double N792822053(Object []i) {
                double p = Double.NaN;
                if (i[10] == null) {
                        p = Globals.COMMAND_ID_ATTACK;
                } else if (((Double) i[10]).doubleValue() <= 13.522317) {
                        p = Globals.COMMAND_ID_ATTACK;
                } else if (((Double) i[10]).doubleValue() > 13.522317) {
                        p = AttackClassifier.N39d1b4674(i);
                }
                return p;
        }
        static double N39d1b4674(Object []i) {
                double p = Double.NaN;
                if (i[0] == null) {
                        p = Globals.NO_COMMAND_DETECTED;
                } else if (((Double) i[0]).doubleValue() <= 286.599955) {
                        p = Globals.NO_COMMAND_DETECTED;
                } else if (((Double) i[0]).doubleValue() > 286.599955) {
                        p = Globals.COMMAND_ID_ATTACK;
                }
                return p;
        }
        static double N769af3d85(Object []i) {
                double p = Double.NaN;
                if (i[10] == null) {
                        p = Globals.COMMAND_ID_ATTACK;
                } else if (((Double) i[10]).doubleValue() <= 12.051446) {
                        p = AttackClassifier.N36ff26bd6(i);
                } else if (((Double) i[10]).doubleValue() > 12.051446) {
                        p = Globals.COMMAND_ID_ATTACK;
                }
                return p;
        }
        static double N36ff26bd6(Object []i) {
                double p = Double.NaN;
                if (i[10] == null) {
                        p = Globals.COMMAND_ID_ATTACK;
                } else if (((Double) i[10]).doubleValue() <= 10.907309) {
                        p = Globals.COMMAND_ID_ATTACK;
                } else if (((Double) i[10]).doubleValue() > 10.907309) {
                        p = Globals.NO_COMMAND_DETECTED;
                }
                return p;
        }
}