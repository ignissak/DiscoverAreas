package net.ignissak.discoverareas.objects;

public class DiscoveryEntry {

    private transient String uuid;
    private int areaId;
    private Long time;

    public DiscoveryEntry(String uuid, int areaId, Long time) {
        this.uuid = uuid;
        this.areaId = areaId;
        this.time = time;
    }

    public String getUuid() {
        return uuid;
    }

    public int getAreaId() {
        return areaId;
    }

    public Long getTime() {
        return time;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
