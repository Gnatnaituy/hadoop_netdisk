package org.jetbrains.hadoop_netdisk.model;

/**
 * @auther hasaker
 * @create_date 2019-06-19 16:31
 * @description
 */
public class User {
    private String username;
    private String hashedPassword;
    private double usedCapacity;
    private double totalCapacity;
    private int age;
    private int gender;
    private String bio;
    private String avatar;

    public User() {}

    public User(String username, String hashedPassword, int age, int gender, String bio, String avatar) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.age = age;
        this.gender = gender;
        this.bio = bio;
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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
