package model;

import java.util.Objects;
import model.interfaces.Identifiable;
import model.interfaces.Exportable;

/**
 * Represents a weekly schedule entry for a {@link Subject} in a {@link MonsterHighGroup}.
 * @author Fatima Roman
 * @version 1.0
 */
public class Schedule implements Identifiable, Exportable {

    private int id;
    private Subject subject;
    private MonsterHighGroup group;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String classroom;

    public Schedule(int id, Subject subject, MonsterHighGroup group,
                    String dayOfWeek, String startTime, String endTime, String classroom) {
        this.id = id; this.subject = subject; this.group = group;
        this.dayOfWeek = dayOfWeek; this.startTime = startTime;
        this.endTime = endTime; this.classroom = classroom;
    }

    @Override public int getId()              { return id; }
    @Override public void setId(int id)       { this.id = id; }
    public Subject getSubject()               { return subject; }
    public void setSubject(Subject s)         { this.subject = s; }
    public MonsterHighGroup getGroup()        { return group; }
    public void setGroup(MonsterHighGroup g)  { this.group = g; }
    public String getDayOfWeek()              { return dayOfWeek; }
    public void setDayOfWeek(String d)        { this.dayOfWeek = d; }
    public String getStartTime()              { return startTime; }
    public void setStartTime(String t)        { this.startTime = t; }
    public String getEndTime()                { return endTime; }
    public void setEndTime(String t)          { this.endTime = t; }
    public String getClassroom()              { return classroom; }
    public void setClassroom(String c)        { this.classroom = c; }

    @Override
    public String toCsv() {
        int sid = (subject != null) ? subject.getId() : -1;
        int gid = (group   != null) ? group.getId()   : -1;
        return id+","+sid+","+gid+","+dayOfWeek+","+startTime+","+endTime+","+classroom;
    }

    @Override
    public String toString() {
        String sName = (subject != null) ? subject.getName() : "N/A";
        String gName = (group != null) ? group.getName() : "N/A";
        return  "\n" +
                " ┌─ Schedule #" + id + "\n" +
                " │ " + dayOfWeek + "  " + startTime + " → " + endTime + "  │  Room: " + classroom + "\n" +
                " │ Subject  » " + sName + "\n" +
                " └ Group    » " + gName + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;
        return id == ((Schedule) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}