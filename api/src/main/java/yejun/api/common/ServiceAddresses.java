package yejun.api.common;

public class ServiceAddresses {
    private final String enr;
    private final String crs;
    private final String stu;

    public ServiceAddresses() {
        enr = null;
        crs = null;
        stu = null;
    }

    public ServiceAddresses(String enrolmentAddress, String courseAddress, String studentAddress) {
        this.enr = enrolmentAddress;
        this.crs = courseAddress;
        this.stu = studentAddress;
    }

    public String getEnr() {
        return enr;
    }

    public String getCrs() {
        return crs;
    }

    public String getStu() {
        return stu;
    }
}
