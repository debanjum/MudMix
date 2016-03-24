package edu.dartmouth.cs.armudwear.gesture;

import edu.dartmouth.cs.armudwear.Globals;

class InvClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = InvClassifier.N7408fbe37(i);
        return p;
    }
    static double N7408fbe37(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[64]).doubleValue() <= 18.162853) {
            p = InvClassifier.N79d12f988(i);
        } else if (((Double) i[64]).doubleValue() > 18.162853) {
            p = InvClassifier.N223e453213(i);
        }
        return p;
    }
    static double N79d12f988(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[1]).doubleValue() <= 90.466776) {
            p = InvClassifier.N148be2a39(i);
        } else if (((Double) i[1]).doubleValue() > 90.466776) {
            p = InvClassifier.N2fff291c11(i);
        }
        return p;
    }
    static double N148be2a39(Object []i) {
        double p = Double.NaN;
        if (i[23] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[23]).doubleValue() <= 4.246544) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[23]).doubleValue() > 4.246544) {
            p = InvClassifier.N6bcc45de10(i);
        }
        return p;
    }
    static double N6bcc45de10(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[1]).doubleValue() <= 71.303996) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[1]).doubleValue() > 71.303996) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
    static double N2fff291c11(Object []i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[19]).doubleValue() <= 1.429467) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[19]).doubleValue() > 1.429467) {
            p = InvClassifier.N18e405e212(i);
        }
        return p;
    }
    static double N18e405e212(Object []i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[18]).doubleValue() <= 6.82652) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[18]).doubleValue() > 6.82652) {
            p = Globals.COMMAND_ID_CLAP;
        }
        return p;
    }
    static double N223e453213(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[0]).doubleValue() <= 709.981773) {
            p = InvClassifier.N2630e72e14(i);
        } else if (((Double) i[0]).doubleValue() > 709.981773) {
            p = InvClassifier.N76b92f6f22(i);
        }
        return p;
    }
    static double N2630e72e14(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[64]).doubleValue() <= 29.160236) {
            p = InvClassifier.N5499909815(i);
        } else if (((Double) i[64]).doubleValue() > 29.160236) {
            p = InvClassifier.N7330862d21(i);
        }
        return p;
    }
    static double N5499909815(Object []i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[14]).doubleValue() <= 18.318575) {
            p = InvClassifier.N2de4168c16(i);
        } else if (((Double) i[14]).doubleValue() > 18.318575) {
            p = Globals.COMMAND_ID_CLAP;
        }
        return p;
    }
    static double N2de4168c16(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[1]).doubleValue() <= 51.183655) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[1]).doubleValue() > 51.183655) {
            p = InvClassifier.N598458dd17(i);
        }
        return p;
    }
    static double N598458dd17(Object []i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[19]).doubleValue() <= 11.939969) {
            p = InvClassifier.N7fe6431418(i);
        } else if (((Double) i[19]).doubleValue() > 11.939969) {
            p = Globals.COMMAND_ID_CLAP;
        }
        return p;
    }
    static double N7fe6431418(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[0]).doubleValue() <= 612.355337) {
            p = InvClassifier.N663e17a519(i);
        } else if (((Double) i[0]).doubleValue() > 612.355337) {
            p = Globals.COMMAND_ID_CLAP;
        }
        return p;
    }
    static double N663e17a519(Object []i) {
        double p = Double.NaN;
        if (i[31] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[31]).doubleValue() <= 1.604398) {
            p = InvClassifier.N7682861320(i);
        } else if (((Double) i[31]).doubleValue() > 1.604398) {
            p = Globals.COMMAND_ID_DROP;
        }
        return p;
    }
    static double N7682861320(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[7]).doubleValue() <= 31.045932) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[7]).doubleValue() > 31.045932) {
            p = Globals.COMMAND_ID_DROP;
        }
        return p;
    }
    static double N7330862d21(Object []i) {
        double p = Double.NaN;
        if (i[27] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[27]).doubleValue() <= 18.301246) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[27]).doubleValue() > 18.301246) {
            p = Globals.COMMAND_ID_CLAP;
        }
        return p;
    }
    static double N76b92f6f22(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[2]).doubleValue() <= 245.75656) {
            p = InvClassifier.N77d95d4d23(i);
        } else if (((Double) i[2]).doubleValue() > 245.75656) {
            p = Globals.COMMAND_ID_DROP;
        }
        return p;
    }
    static double N77d95d4d23(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[5]).doubleValue() <= 60.22989) {
            p = Globals.COMMAND_ID_CLAP;
        } else if (((Double) i[5]).doubleValue() > 60.22989) {
            p = InvClassifier.N5f7ca7b324(i);
        }
        return p;
    }
    static double N5f7ca7b324(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[6]).doubleValue() <= 26.80505) {
            p = Globals.COMMAND_ID_DROP;
        } else if (((Double) i[6]).doubleValue() > 26.80505) {
            p = Globals.COMMAND_ID_CLAP;
        }
        return p;
    }
}