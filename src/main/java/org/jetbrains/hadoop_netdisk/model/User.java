package org.jetbrains.hadoop_netdisk.model;

/**
 * @auther hasaker
 * @create_date 2019-06-19 16:31
 * @description
 */
public class User {
    private String username;
    private String hashedPassword;
    private int usedCapacity;
    private int totalCapacity;
    private int age;
    private int gender;
    private String bio;
    private String avatar;

    public User() {}

    public User(String username, String hashedPassword, int usedCapacity,
                int totalCapacity, int age, int gender, String bio, String avatar) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.usedCapacity = usedCapacity;
        this.totalCapacity = totalCapacity;
        this.age = age;
        this.gender = gender;
        this.bio = bio;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", usedCapacity=" + usedCapacity +
                ", totalCapacity=" + totalCapacity +
                ", age=" + age +
                ", gender=" + gender +
                ", bio='" + bio + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
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

    public int getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(int usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
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
