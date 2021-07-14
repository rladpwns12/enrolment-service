package yejun.api.common;

import java.util.Calendar;

public enum Semester {
    SPRING, SUMMER, FALL, WINTER;

    public static Semester getSemesterNow() {
        int month = Calendar.getInstance().get(Calendar.MONTH) +1;
        switch (month){
            case 12:
            case 1:
            case 2:
                return Semester.WINTER;
            case 3:
            case 4:
            case 5:
            case 6:
                return Semester.SPRING;
            case 7:
            case 8:
                return Semester.SUMMER;
            case 9:
            case 10:
            case 11:
                return Semester.FALL;
            default:
                return Semester.SPRING;
        }
    }
}
