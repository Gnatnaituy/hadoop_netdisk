package org.jetbrains.hadoop_netdisk.entity;

/**
 * @auther hasaker
 * @create_date 2019-06-19 16:31
 * @description
 */
public class HadoopUser {
    private String username;
    private String password;
    private double usedCapacity;
    private double totalCapacity;
    private String bio;
    private String avatar;

    public HadoopUser() {}

    public HadoopUser(String username, String password, String bio, String avatar) {
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(double usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public double getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(double totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
