package org.eclipse.e4.tapiji.git.model;


public class CommitReference {

    private String name;
    private int time;
    private int type;
    private String hash;

    public CommitReference(String name, int time, int type, String hash) {
        super();
        this.name = name;
        this.time = time;
        this.type = type;
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "CommitReference [name=" + name + ", time=" + time + ", type=" + type + ", hash=" + hash + "]";
    }

}
