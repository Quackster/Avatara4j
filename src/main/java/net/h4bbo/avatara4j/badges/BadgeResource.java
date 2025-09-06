package net.h4bbo.avatara4j.badges;

public class BadgeResource {
    public int Id;
    public String ExtraData1;
    public String ExtraData2;
    public String Type;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getExtraData1() {
        return ExtraData1;
    }

    public void setExtraData1(String extraData1) {
        ExtraData1 = extraData1;
    }

    public String getExtraData2() {
        return ExtraData2;
    }

    public void setExtraData2(String extraData2) {
        ExtraData2 = extraData2;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
