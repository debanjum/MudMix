package edu.dartmouth.cs.armudwear.gesture;

import edu.dartmouth.cs.armudwear.Globals;

class InvClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = InvClassifier.N3e4b73740(i);
        return p;
    }
    static double N3e4b73740(Object []i) {
        double p = Double.NaN;
        if (i[17] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[17]).doubleValue() <= 37.325975) {
            p = InvClassifier.N4b88e49f1(i);
        } else if (((Double) i[17]).doubleValue() > 37.325975) {
            p = Globals.COMMAND_ID_CLAP;
        }
        return p;
    }
    static double N4b88e49f1(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[1]).doubleValue() <= 146.298707) {
            p = InvClassifier.N859a9772(i);
        } else if (((Double) i[1]).doubleValue() > 146.298707) {
            p = InvClassifier.N346b52a117(i);
        }
        return p;
    }
    static double N859a9772(Object []i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[20]).doubleValue() <= 16.930061) {
            p = InvClassifier.N3684c7e93(i);
        } else if (((Double) i[20]).doubleValue() > 16.930061) {
            p = Globals.COMMAND_ID_CLAP;
        }
        return p;
    }
    static double N3684c7e93(Object []i) {
        double p = Double.NaN;
        if (i[27] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[27]).doubleValue() <= 1.216989) {
            p = InvClassifier.N4760adf54(i);
        } else if (((Double) i[27]).doubleValue() > 1.216989) {
            p = InvClassifier.N65e5ffc07(i);
        }
        return p;
    }
    static double N4760adf54(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[10]).doubleValue() <= 6.153683) {
            p = InvClassifier.N1c3cd46f5(i);
        } else if (((Double) i[10]).doubleValue() > 6.153683) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
    static double N1c3cd46f5(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[13]).doubleValue() <= 1.522519) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[13]).doubleValue() > 1.522519) {
            p = InvClassifier.N5d9ed1e46(i);
        }
        return p;
    }
    static double N5d9ed1e46(Object []i) {
        double p = Double.NaN;
        if (i[17] == null) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[17]).doubleValue() <= 3.117518) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[17]).doubleValue() > 3.117518) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
    static double N65e5ffc07(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[0]).doubleValue() <= 239.109723) {
            p = InvClassifier.N5ef9ca838(i);
        } else if (((Double) i[0]).doubleValue() > 239.109723) {
            p = InvClassifier.N1354e4e11(i);
        }
        return p;
    }
    static double N5ef9ca838(Object []i) {
        double p = Double.NaN;
        if (i[21] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[21]).doubleValue() <= 3.792602) {
            p = InvClassifier.N615e8f2b9(i);
        } else if (((Double) i[21]).doubleValue() > 3.792602) {
            p = InvClassifier.N70981fe10(i);
        }
        return p;
    }
    static double N615e8f2b9(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[3]).doubleValue() <= 10.970725) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[3]).doubleValue() > 10.970725) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
    static double N70981fe10(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[0]).doubleValue() <= 174.330781) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[0]).doubleValue() > 174.330781) {
            p = Globals.COMMAND_ID_DROP;
        }
        return p;
    }
    static double N1354e4e11(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[1]).doubleValue() <= 66.487381) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[1]).doubleValue() > 66.487381) {
            p = InvClassifier.N52a7e38012(i);
        }
        return p;
    }
    static double N52a7e38012(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[11]).doubleValue() <= 18.872331) {
            p = InvClassifier.Nc4912e513(i);
        } else if (((Double) i[11]).doubleValue() > 18.872331) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
    static double Nc4912e513(Object []i) {
        double p = Double.NaN;
        if (i[30] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[30]).doubleValue() <= 2.036769) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[30]).doubleValue() > 2.036769) {
            p = InvClassifier.N11777eb014(i);
        }
        return p;
    }
    static double N11777eb014(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[3]).doubleValue() <= 63.868398) {
            p = InvClassifier.N3c1e5dc515(i);
        } else if (((Double) i[3]).doubleValue() > 63.868398) {
            p = InvClassifier.N6d9e4f5816(i);
        }
        return p;
    }
    static double N3c1e5dc515(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[7]).doubleValue() <= 19.181891) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[7]).doubleValue() > 19.181891) {
            p = Globals.COMMAND_ID_CLAP;
        }
        return p;
    }
    static double N6d9e4f5816(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[6]).doubleValue() <= 34.565053) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[6]).doubleValue() > 34.565053) {
            p = Globals.COMMAND_ID_DROP;
        }
        return p;
    }
    static double N346b52a117(Object []i) {
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
