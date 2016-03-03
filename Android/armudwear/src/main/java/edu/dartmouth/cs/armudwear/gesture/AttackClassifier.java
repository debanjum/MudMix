package edu.dartmouth.cs.armudwear.gesture;

import edu.dartmouth.cs.armudwear.Globals;

class AttackClassifier {

        public static double classify(Object[] i)
                throws Exception {

                double p = Double.NaN;
                p = AttackClassifier.N1744ee4f44(i);
                return p;
        }
        static double N1744ee4f44(Object []i) {
                double p = Double.NaN;
                if (i[64] == null) {
                        p = Globals.NO_COMMAND_DETECTED;
                } else if (((Double) i[64]).doubleValue() <= 35.856138) {
                        p = AttackClassifier.N2e79907845(i);
                } else if (((Double) i[64]).doubleValue() > 35.856138) {
                        p = AttackClassifier.N15992bab47(i);
                }
                return p;
        }
        static double N2e79907845(Object []i) {
                double p = Double.NaN;
                if (i[30] == null) {
                        p = Globals.NO_COMMAND_DETECTED;
                } else if (((Double) i[30]).doubleValue() <= 9.988797) {
                        p = Globals.NO_COMMAND_DETECTED;
                } else if (((Double) i[30]).doubleValue() > 9.988797) {
                        p = AttackClassifier.N2efbce1e46(i);
                }
                return p;
        }
        static double N2efbce1e46(Object []i) {
                double p = Double.NaN;
                if (i[0] == null) {
                        p = Globals.COMMAND_ID_ATTACK;
                } else if (((Double) i[0]).doubleValue() <= 585.688632) {
                        p = Globals.COMMAND_ID_ATTACK;
                } else if (((Double) i[0]).doubleValue() > 585.688632) {
                        p = Globals.NO_COMMAND_DETECTED;
                }
                return p;
        }
        static double N15992bab47(Object []i) {
                double p = Double.NaN;
                if (i[10] == null) {
                        p = Globals.COMMAND_ID_ATTACK;
                } else if (((Double) i[10]).doubleValue() <= 70.982076) {
                        p = AttackClassifier.N7b01f2a048(i);
                } else if (((Double) i[10]).doubleValue() > 70.982076) {
                        p = Globals.COMMAND_ID_ATTACK;
                }
                return p;
        }
        static double N7b01f2a048(Object []i) {
                double p = Double.NaN;
                if (i[4] == null) {
                        p = Globals.COMMAND_ID_ATTACK;
                } else if (((Double) i[4]).doubleValue() <= 167.816858) {
                        p = Globals.COMMAND_ID_ATTACK;
                } else if (((Double) i[4]).doubleValue() > 167.816858) {
                        p = Globals.NO_COMMAND_DETECTED;
                }
                return p;
        }
}