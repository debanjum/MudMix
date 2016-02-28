package edu.dartmouth.cs.armudwear.gesture;

import edu.dartmouth.cs.armudwear.Globals;

class InvClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = InvClassifier.N59c6878249(i);
        return p;
    }
    static double N59c6878249(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[1]).doubleValue() <= 143.970411) {
            p = InvClassifier.N78ea95d950(i);
        } else if (((Double) i[1]).doubleValue() > 143.970411) {
            p = InvClassifier.Nd0316e664(i);
        }
        return p;
    }
    static double N78ea95d950(Object []i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[19]).doubleValue() <= 11.540085) {
            p = InvClassifier.N7114179351(i);
        } else if (((Double) i[19]).doubleValue() > 11.540085) {
            p = InvClassifier.N4d2264ba63(i);
        }
        return p;
    }
    static double N7114179351(Object []i) {
        double p = Double.NaN;
        if (i[29] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[29]).doubleValue() <= 4.862198) {
            p = InvClassifier.N5e17ede452(i);
        } else if (((Double) i[29]).doubleValue() > 4.862198) {
            p = InvClassifier.N66ecee9062(i);
        }
        return p;
    }
    static double N5e17ede452(Object []i) {
        double p = Double.NaN;
        if (i[27] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[27]).doubleValue() <= 1.09041) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[27]).doubleValue() > 1.09041) {
            p = InvClassifier.N750343fe53(i);
        }
        return p;
    }
    static double N750343fe53(Object []i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[14]).doubleValue() <= 6.113471) {
            p = InvClassifier.N296514b654(i);
        } else if (((Double) i[14]).doubleValue() > 6.113471) {
            p = InvClassifier.N57ef7e3560(i);
        }
        return p;
    }
    static double N296514b654(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[2]).doubleValue() <= 51.802989) {
            p = InvClassifier.N2b32cdf555(i);
        } else if (((Double) i[2]).doubleValue() > 51.802989) {
            p = InvClassifier.N2c06657759(i);
        }
        return p;
    }
    static double N2b32cdf555(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[8]).doubleValue() <= 3.698118) {
            p = InvClassifier.N146fa5fb56(i);
        } else if (((Double) i[8]).doubleValue() > 3.698118) {
            p = InvClassifier.N2dfa0c3857(i);
        }
        return p;
    }
    static double N146fa5fb56(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[1]).doubleValue() <= 55.799362) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[1]).doubleValue() > 55.799362) {
            p = Globals.COMMAND_ID_DROP;
        }
        return p;
    }
    static double N2dfa0c3857(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[15]).doubleValue() <= 4.196641) {
            p = InvClassifier.N7b38481c58(i);
        } else if (((Double) i[15]).doubleValue() > 4.196641) {
            p = Globals.COMMAND_ID_CLAP;
        }
        return p;
    }
    static double N7b38481c58(Object []i) {
        double p = Double.NaN;
        if (i[28] == null) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[28]).doubleValue() <= 0.698907) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[28]).doubleValue() > 0.698907) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
    static double N2c06657759(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[1]).doubleValue() <= 90.39745) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[1]).doubleValue() > 90.39745) {
            p = Globals.COMMAND_ID_DROP;
        }
        return p;
    }
    static double N57ef7e3560(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[15]).doubleValue() <= 4.954443) {
            p = InvClassifier.N2c46f6ad61(i);
        } else if (((Double) i[15]).doubleValue() > 4.954443) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
    static double N2c46f6ad61(Object []i) {
        double p = Double.NaN;
        if (i[27] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[27]).doubleValue() <= 2.663487) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[27]).doubleValue() > 2.663487) {
            p = Globals.COMMAND_ID_DROP;
        }
        return p;
    }
    static double N66ecee9062(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[5]).doubleValue() <= 50.92203) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[5]).doubleValue() > 50.92203) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
    static double N4d2264ba63(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[0]).doubleValue() <= 570.652156) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[0]).doubleValue() > 570.652156) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
    static double Nd0316e664(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[7]).doubleValue() <= 104.82066) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[7]).doubleValue() > 104.82066) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
}