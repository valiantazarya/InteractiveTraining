package com.thepoweroftether.interactivetraining;

/**
 * Created by ValiantAzarya on 03/12/2016.
 */
public class ExcerciseClass {
    public class Question {
        private int ID;
        private String QUESTION;
        private String OPTA;
        private String OPTB;
        private String OPTC;
        private String OPTD;
        private String ANSWER;
        public Question()
        {
            ID=0;
            QUESTION="";
            OPTA="";
            OPTB="";
            OPTC="";
            OPTD="";
            ANSWER="";
        }
        public Question(String qUESTION, String oPTA, String oPTB, String oPTC, String oPTD,
                        String aNSWER) {

            QUESTION = qUESTION;
            OPTA = oPTA;
            OPTB = oPTB;
            OPTC = oPTC;
            OPTD = oPTD;
            ANSWER = aNSWER;
        }

        //Getter
        public int getID()
        {
            return ID;
        }
        public String getQUESTION() {
            return QUESTION;
        }
        public String getOPTA() {
            return OPTA;
        }
        public String getOPTB() {
            return OPTB;
        }
        public String getOPTC() {
            return OPTC;
        }
        public String getOPTD() {
            return OPTD;
        }
        public String getANSWER() {
            return ANSWER;
        }

        //Setter
        public void setID(int id)
        {
            ID=id;
        }
        public void setQUESTION(String qUESTION) {
            QUESTION = qUESTION;
        }
        public void setOPTA(String oPTA) {
            OPTA = oPTA;
        }
        public void setOPTB(String oPTB) {
            OPTB = oPTB;
        }
        public void setOPTC(String oPTC) {
            OPTC = oPTC;
        }
        public void setOPTD(String oPTD) {
            OPTC = oPTD;
        }
        public void setANSWER(String aNSWER) {
            ANSWER = aNSWER;
        }

    }
}
