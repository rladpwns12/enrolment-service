package yejun.api.course;

import yejun.api.common.Semester;
import yejun.api.common.Type;
import java.util.Calendar;

public class CourseRequestDTO {
    private Type type;
    private String keyword;
    private int year;
    private Semester semester;
    private int page;
    private int size;

    public CourseRequestDTO() {
        type = null;
        keyword = null;
        year = Calendar.getInstance().get(Calendar.YEAR);
        semester = Semester.getSemesterNow();
        page = 0;
        size = 20;
    }

    public CourseRequestDTO(Type type, String keyword, int year, Semester semester, int page, int size) {
        this.type = type;
        this.keyword = keyword;
        this.year = year;
        this.semester = semester;
        this.page = page;
        this.size = size;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Semester getSemester() {
        return semester;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return  "type=" + type +
                ", keyword='" + keyword + '\'' +
                ", year=" + year +
                ", semester=" + semester +
                ", page=" + page +
                ", size=" + size;
    }
}
