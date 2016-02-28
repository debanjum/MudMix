package edu.dartmouth.cs.armudwear.gesture;

import edu.dartmouth.cs.armudwear.Globals;

/**
 * Created by michael1 on 2/28/16.
 */
class ObjClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = ObjClassifier.N7106108865(i);
        return p;
    }
    static double N7106108865(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[1]).doubleValue() <= 75.994031) {
            p = ObjClassifier.N45278ab066(i);
        } else if (((Double) i[1]).doubleValue() > 75.994031) {
            p = ObjClassifier.N27024c4867(i);
        }
        return p;
    }
    static double N45278ab066(Object []i) {
        double p = Double.NaN;
        if (i[30] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[30]).doubleValue() <= 7.15249) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[30]).doubleValue() > 7.15249) {
            p = Globals.COMMAND_ID_GET;
        }
        return p;
    }
    static double N27024c4867(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[2]).doubleValue() <= 18.379586) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[2]).doubleValue() > 18.379586) {
            p = ObjClassifier.N7de1efa68(i);
        }
        return p;
    }
    static double N7de1efa68(Object []i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = Globals.COMMAND_ID_GET;
        } else if (((Double) i[20]).doubleValue() <= 16.870826) {
            p = ObjClassifier.N7c3b31f69(i);
        } else if (((Double) i[20]).doubleValue() > 16.870826) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
    static double N7c3b31f69(Object []i) {
        double p = Double.NaN;
        if (i[28] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[28]).doubleValue() <= 0.462641) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[28]).doubleValue() > 0.462641) {
            p = ObjClassifier.N4124bc3470(i);
        }
        return p;
    }
    static double N4124bc3470(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = Globals.COMMAND_ID_GET;
        } else if (((Double) i[1]).doubleValue() <= 187.790656) {
            p = ObjClassifier.N52c01b5571(i);
        } else if (((Double) i[1]).doubleValue() > 187.790656) {
            p = Globals.COMMAND_ID_GET;
        }
        return p;
    }
    static double N52c01b5571(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = Globals.COMMAND_ID_GET;
        } else if (((Double) i[3]).doubleValue() <= 69.907999) {
            p = ObjClassifier.N424293e472(i);
        } else if (((Double) i[3]).doubleValue() > 69.907999) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
    static double N424293e472(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = Globals.NO_COMMAND_DETECTED;
        } else if (((Double) i[64]).doubleValue() <= 11.907073) {
            p = ObjClassifier.N20fb7fb873(i);
        } else if (((Double) i[64]).doubleValue() > 11.907073) {
            p = Globals.COMMAND_ID_GET;
        }
        return p;
    }
    static double N20fb7fb873(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = Globals.COMMAND_ID_GET;
        } else if (((Double) i[0]).doubleValue() <= 311.619174) {
            p = Globals.COMMAND_ID_GET;
        } else if (((Double) i[0]).doubleValue() > 311.619174) {
            p = Globals.NO_COMMAND_DETECTED;
        }
        return p;
    }
}